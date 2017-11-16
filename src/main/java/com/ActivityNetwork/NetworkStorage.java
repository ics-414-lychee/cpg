package com.ActivityNetwork;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * The NetworkStorage class, which contains a set of methods to interact with the backend.
 */
@SuppressWarnings("unchecked")
public final class NetworkStorage {
  /**
   * Export the given network as a JSON string.
   *
   * @param a Network to store.
   * @return The given network as a JSON string.
   */
  private static String exportNetworkAsJSON(ActivityNetwork a) {
    JSONArray nodeList = new JSONArray();
    JSONObject net = new JSONObject();

    for (ActivityNode n : a.getNodeList()) {
      JSONObject node = new JSONObject();
      node.put("NodeID", n.getNodeId());
      node.put("NodeName", n.getName());
      node.put("Description", n.getDescription());

      // Times are accessed as an array.
      double times[] = n.getTimes();
      node.put("NormalTime", times[0]);
      node.put("OptimisticTime", times[1]);
      node.put("PessimisticTime", times[2]);

      // We store our dependency list as a comma-separated list. Store the nodes in the main list.
      node.put("DependencyNodeID", n.getDependencies().toString().replaceAll("\\[|\\]", ""));
      nodeList.add(node);
    }

    net.put("ProjectID", a.getNetworkId());
    net.put("ProjectName", a.getNetworkName());
    net.put("NodeList", nodeList);

    return net.toString();
  }

  /**
   * Import the given network from a JSON string to an ActivityNetwork instance. If our string cannot be parsed, return
   * an empty network.
   *
   * @param netString JSON string containing our network.
   * @return An ActivityNetwork instance parsed from the given network string.
   */
  private static ActivityNetwork importNetworkAsJSON(String netString) {
    JSONParser jsonParser = new JSONParser();

    try {
      JSONObject jsonNet = (JSONObject) jsonParser.parse(netString);
      ActivityNetwork a = new ActivityNetwork((Long) jsonNet.get("ProjectID"), (String) jsonNet.get("ProjectName"));

      // Obtain and iterate through our node list.
      JSONArray nodeList = (JSONArray) jsonNet.get("NodeList");
      for (String aNodeList : (Iterable<String>) nodeList) {
        JSONObject jsonNode = (JSONObject) jsonParser.parse(aNodeList);

        // Build our node without the dependencies.
        ActivityNode n = new ActivityNode((Long) jsonNode.get("NodeID"), (String) jsonNode.get("NodeName"),
            (String) jsonNode.get("Description"), (Double) jsonNode.get("OptimisticTime"),
            (Double) jsonNode.get("NormalTime"), (Double) jsonNode.get("PessimisticTime"));

        // Our dependencies are stored as a comma separated string. Insert node after parsing.
        HashSet<Long> dependencies = new HashSet<>();
        for (String s : ((String) jsonNode.get("DependencyNodeID")).split(",")) {
          dependencies.add(Long.parseLong(s));
        }
        n.setDependencies(dependencies);
        a.insertNode(n);
      }

      return a;

    } catch (ParseException e) {
      // We return an empty network in the event we cannot parse our string.
      return new ActivityNetwork(0, "Bad");
    }
  }

  /**
   * Perform a POST with the open HTTP client and input, and return the response. Following the resource below:
   * https://www.mkyong.com/webservices/jax-rs/restful-java-client-with-apache-httpclient/
   *
   * @param jsonParser Open JSON Parser instance.
   * @param h          Open HTTP client, used to POST our input.
   * @param i          Input list of name-value pairs to POST.
   * @param f          PHP file to use with our POST request.
   * @return A JSON object containing the response of our POST.
   */
  private static JSONObject postAndGetResponse(JSONParser jsonParser, HttpClient h, List<NameValuePair> i, String f) {
    try {
      HttpPost postRequest = new HttpPost(f);
      postRequest.setEntity(new UrlEncodedFormEntity(i));

      // POST our token, username, and desired name. Wait for our response.
      HttpResponse response = h.execute(postRequest);
      if (response.getStatusLine().getStatusCode() != 200) {
        throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
      }

      // Read our response.
      BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
      return (JSONObject) jsonParser.parse(br);

    } catch (IOException | ParseException e) {
      JSONObject j = new JSONObject();
      j.put("ErrorMessage", e.toString());
      return j;
    }
  }

  /**
   * POST our JSON input j and our regular value-pair input i with the given HTTP client and return the response.
   * Following the resource given here: http://www.baeldung.com/httpclient-post-http-request
   *
   * @param jsonParser Open JSON Parser instance.
   * @param h          Open HTTP client, used to POST our input.
   * @param j          JSON string to POST.
   * @param i          Input list of name-value pairs to POST.
   * @param f          PHP file to use with our POST request.
   * @return A JSON object containing the response of our POST.
   */
  private static JSONObject postJSONAndGetResponse(JSONParser jsonParser, HttpClient h, String j,
                                                   List<NameValuePair> i, String f) {
    try {
      HttpPost postRequest = new HttpPost(f);
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();

      postRequest.setEntity(new UrlEncodedFormEntity(i));

      // POST our token, username, and desired name. Wait for our response.
      HttpResponse response = h.execute(postRequest);
      if (response.getStatusLine().getStatusCode() != 200) {
        throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
      }

      // Read our response.
      BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
      return (JSONObject) jsonParser.parse(br);

    } catch (IOException | ParseException e) {
      JSONObject j = new JSONObject();
      j.put("ErrorMessage", e.toString());
      return j;
    }
  }

  /**
   * Creates a network entry in the backend, associated with the given user and network name. The project ID of this
   * new network is returned.
   *
   * @param token       Authentication token, obtained from a successful login.
   * @param u           Username of the current user with the given token.
   * @param networkName Desired name of the new network.
   * @return An ID of 0 if there exists an error. Otherwise, the network ID that corresponds to this new network.
   */
  static long createNetwork(String token, String u, String networkName) {
    JSONParser jsonParser = new JSONParser();
    DefaultHttpClient httpClient = new DefaultHttpClient();

    // Collect our parameters to POST.
    List<NameValuePair> i = new ArrayList<>(Arrays.asList(new BasicNameValuePair("username", u),
        new BasicNameValuePair("auth", token), new BasicNameValuePair("projectname", networkName)));

    JSONObject jsonReturned = postAndGetResponse(jsonParser, httpClient, i, "http://localhost/PHPWebServer/create.php");
    httpClient.getConnectionManager().shutdown();

    // If we have an error, return a network ID of 0.
    return jsonReturned.get("Error").toString().equals("false") ? 0 :
        Long.parseLong(jsonReturned.get("ProjectID").toString());
  }

  /**
   * Save the current network. If this action is successful, return true.
   *
   * @param token Authentication token, obtained from a successful login.
   * @param u     Username of the current user with the given token.
   * @param a     Network to store.
   * @return True if the action was successful. False otherwise.
   */
  static boolean storeNetwork(String token, String u, ActivityNetwork a) {
    JSONParser jsonParser = new JSONParser();
    DefaultHttpClient httpClient = new DefaultHttpClient();

    // Collect our parameters to POST.
    List<NameValuePair> i = new ArrayList<>(Arrays.asList(new BasicNameValuePair("username", u),
        new BasicNameValuePair("auth", token));

    JSONObject jsonReturned = postAndGetResponse(jsonParser, httpClient, i, "http://localhost/PHPWebServer/save.php");
    httpClient.getConnectionManager().shutdown();

    // If we have an error, indicate that we were not able to save the network.
    return jsonReturned.get("Error").toString().equals("true");
  }

  /**
   * Remove the network from the database, given the network ID.
   *
   * @param token     Authentication token, obtained from a successful login.
   * @param u         Username of the current user with the given token.
   * @param networkId ID of the network to retrieve.
   * @return True if the network was deleted. False otherwise.
   */
  static boolean deleteNetwork(String token, String u, long networkId) {
    JSONParser jsonParser = new JSONParser();
    DefaultHttpClient httpClient = new DefaultHttpClient();

    // Collect our parameters to POST.
    List<NameValuePair> i = new ArrayList<>(Arrays.asList(new BasicNameValuePair("username", u),
        new BasicNameValuePair("auth", token), new BasicNameValuePair("projectid", Long.toString(networkId))));

    JSONObject jsonReturned = postAndGetResponse(jsonParser, httpClient, i, "http://localhost/PHPWebServer/delete.php");
    httpClient.getConnectionManager().shutdown();

    // If we have an error, indicate that we were not able to delete the network.
    return jsonReturned.get("Error").toString().equals("true");
  }

  /**
   * Load the network (as it was last saved) from the database given the network ID.
   *
   * @param token     Authentication token, obtained from a successful login.
   * @param u         Username of the current user with the given token.
   * @param networkId ID of the network to retrieve.
   * @return An ActivityNetwork instance, corresponding to its last saved instance. An empty network if the network
   * could not be successfully loaded.
   */
  static ActivityNetwork retrieveNetwork(String token, String u, long networkId) {
    JSONParser jsonParser = new JSONParser();
    DefaultHttpClient httpClient = new DefaultHttpClient();

    // Collect our parameters to POST.
    List<NameValuePair> i = new ArrayList<>(Arrays.asList(new BasicNameValuePair("username", u),
        new BasicNameValuePair("auth", token), new BasicNameValuePair("projectid", Long.toString(networkId))));

    JSONObject jsonReturned = postAndGetResponse(jsonParser, httpClient, i, "http://localhost/PHPWebServer/load.php");
    httpClient.getConnectionManager().shutdown();

    // If we have an error, indicate that we were not able to retrieve the network.
    return jsonReturned.get("Error").toString().equals("false") ? new ActivityNetwork(0, "Bad") :
        importNetworkAsJSON(jsonReturned.get("NodesJSON").toString());
  }
}
