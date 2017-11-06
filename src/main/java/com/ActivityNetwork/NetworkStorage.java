///
/// This file contains the NetworkStorage class, which will interact with the backend to store and retrieve networks on
/// disk.
///

package com.ActivityNetwork;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

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
      node.put("DependencyNodeID", n.getDependencies().toString().replaceAll("\\[|\\]",""));
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
      Iterator<String> nodeIterator = nodeList.iterator();
      while (nodeIterator.hasNext()) {
        JSONObject jsonNode = (JSONObject) jsonParser.parse(nodeIterator.next());

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
      return new ActivityNetwork(0, "");
    }
  }

  /**
   * Save the current network. The time at which this network was saved is returned.
   *
   * @param a Network to store.
   * @return The time in milliseconds, corresponding to the time which the network was saved. 0 if the network was not
   * successfully saved.
   */
  public static long storeNetwork(ActivityNetwork a) {
    return 0;
  }

  /**
   * Load the network (as it was last saved) from the database given the network ID.
   *
   * @param networkId ID of the network to retrieve.
   * @return An ActivityNetwork instance, corresponding to its last saved instance. An empty network if the network
   * could not be successfully loaded.
   */
  public static ActivityNetwork retrieveNetwork(long networkId) {
    try {
      DefaultHttpClient httpClient = new DefaultHttpClient();

      // TODO: modify these for our own stuff - https://www.mkyong.com/webservices/jax-rs/restful-java-client-with-apache-httpclient/
      HttpGet getRequest = new HttpGet(
          "http://localhost:8080/RESTfulExample/json/product/get");
      getRequest.addHeader("accept", "application/json");

      HttpResponse response = httpClient.execute(getRequest);

      return new ActivityNetwork(0, "");
    }
    catch (IOException e){
      return new ActivityNetwork(0, "");
    }
  }

  /**
   * Creates an account using the given login information.
   *
   * @param u Username of the user to create an account for.
   * @param p Password of the user to create an account for.
   * @return True if account creation was successful. False otherwise.
   */
  public static boolean createAccount (String u, String p) {
    // TODO: finish account creation
    return true;
  }

  /**
   * Checks if the password matches with the given username.
   *
   * @param u Username of the user to login with.
   * @param p Password of the user to login with.
   * @return True if the password and username match (i.e. login was successful). False otherwise.
   */
  public static boolean verifyLoginInfo (String u, String p) {
    // TODO: finish login, is this the only place we can get our project IDs and names??
    return true;
  }
}
