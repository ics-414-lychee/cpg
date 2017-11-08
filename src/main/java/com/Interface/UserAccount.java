///
/// This file contains the UserAccount class, which will interact with the backend to store and retrieve account info.
///

package com.Interface;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The UserAccount class, which contains a set of methods to interact with the backend about account information.
 */
@SuppressWarnings("unchecked")
public final class UserAccount {
  /**
   * Parse the given username and password, and fit these into a JSON string appropriate for our POST request.
   *
   * @param u Username to use for output string.
   * @param p Password to use for output string.
   * @return JSON string appropriate for the login/creation POST requests.
   */
  private static String usernamePasswordAsJSON(String u, String p) {
    JSONObject jsonLogin = new JSONObject();
    jsonLogin.put("username", u);
    jsonLogin.put("password", p);

    return jsonLogin.toString();
  }

  /**
   * Given the project JSON, return a list of the project IDs.
   *
   * @param p Project JSON that corresponds to all projects associated with the current user.
   * @return List of project IDs associated with the current user.
   */
  public static ArrayList<Long> idsFromProjectJSON(String p) {
    JSONParser jsonParser = new JSONParser();
    ArrayList<Long> idList = new ArrayList<>();

    try {
      JSONObject jsonProject = (JSONObject) jsonParser.parse(p);

      // Obtain our project ID list, and return this as a string of Longs.
      for (String s : ((String) jsonProject.get("projectIDs")).split(",")) {
        idList.add(Long.parseLong(s));
      }
      return idList;

    } catch (ParseException e) {
      // We return an empty list in the event we cannot parse our string.
      return new ArrayList<>();
    }
  }

  /**
   * Given the project JSON, return a list of the project names.
   *
   * @param p Project JSON that corresponds to all projects associated with the current user.
   * @return List of project names associated with the current user.
   */
  public static ArrayList<String> namesFromProjectJSON(String p) {
    JSONParser jsonParser = new JSONParser();
    ArrayList<String> nameList = new ArrayList<>();

    try {
      JSONObject jsonProject = (JSONObject) jsonParser.parse(p);

      // Obtain our project ID list, and return this as a string of Longs.
      nameList.addAll(Arrays.asList(((String) jsonProject.get("projectIDs")).split(",")));
      return nameList;

    } catch (ParseException e) {
      // We return an empty list in the event we cannot parse our string.
      return new ArrayList<>();
    }
  }

  /**
   * Creates an account using the given login information. Following the resource below:
   * https://www.mkyong.com/webservices/jax-rs/restful-java-client-with-apache-httpclient/
   *
   * @param u Username of the user to create an account for.
   * @param p Password of the user to create an account for.
   * @return Error message returned from account creation attempt. If there is no error, this is empty.
   */
  static String createAccount(String u, String p) {
    JSONParser jsonParser = new JSONParser();

    try {
      DefaultHttpClient httpClient = new DefaultHttpClient();

      // TODO: replace with our own links and header for account creation.
      HttpPost postRequest = new HttpPost("http://localhost:8080/RESTfulExample/json/product/get");
      StringEntity loginInfo = new StringEntity(usernamePasswordAsJSON(u, p));
      loginInfo.setContentType("application/json");
      postRequest.setEntity(loginInfo);

      // POST our login information, wait for our response.
      HttpResponse response = httpClient.execute(postRequest);
      if (response.getStatusLine().getStatusCode() != 201) {
        throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
      }

      // Read our response.
      BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
      JSONObject jsonReturned = (JSONObject) jsonParser.parse(br);

      // Disconnect and return our error message that we parsed.
      httpClient.getConnectionManager().shutdown();
      return (String) jsonReturned.get("ErrorMessage");

    } catch (ParseException e) {
      return "Response not correctly parsed.";
    } catch (IOException e) {
      return "IO Exception Somewhere...";
    }
  }

  /**
   * Checks if the password matches with the given username. If this is successful, we return the authentication token
   * in the first element, and the project JSON in the second. Following the resource below:
   * https://www.mkyong.com/webservices/jax-rs/restful-java-client-with-apache-httpclient/
   *
   * @param u Username of the user to login with.
   * @param p Password of the user to login with.
   * @return If the login is successful, a two element array of the authentication token, and the JSON array of project
   * IDs and names associated with the user. Otherwise, an empty array.
   */
  public static ArrayList<String> verifyLoginInfo(String u, String p) {
    JSONParser jsonParser = new JSONParser();

    try {
      DefaultHttpClient httpClient = new DefaultHttpClient();

      // TODO: replace with our own links and header for login.
      HttpPost postRequest = new HttpPost("http://localhost:8080/RESTfulExample/json/product/get");
      StringEntity loginInfo = new StringEntity(usernamePasswordAsJSON(u, p));
      loginInfo.setContentType("application/json");
      postRequest.setEntity(loginInfo);

      // POST our login information, wait for our response.
      HttpResponse response = httpClient.execute(postRequest);
      if (response.getStatusLine().getStatusCode() != 201) {
        throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
      }

      // Read our response.
      BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
      JSONObject jsonReturned = (JSONObject) jsonParser.parse(br);
      JSONObject errorMessage = (JSONObject) jsonParser.parse((String) jsonReturned.get("ErrorJSON"));

      // If we have an error, return an empty list.
      if ((Boolean) errorMessage.get("Error")) {
        httpClient.getConnectionManager().shutdown();
        return new ArrayList<>();

      } else {
        // Otherwise, return the authentication token and the project JSON.
        httpClient.getConnectionManager().shutdown();
        return new ArrayList<>(Arrays.asList((String) errorMessage.get("AuthToken"),
            (String) jsonReturned.get("ProjectJSON")));
      }
    } catch (ParseException | IOException e) {
      return new ArrayList<>();

    }
  }
}
