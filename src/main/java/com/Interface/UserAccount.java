///
/// This file contains the UserAccount class, which will interact with the backend to store and retrieve account info.
///

package com.Interface;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The UserAccount class, which contains a set of methods to interact with the backend about account information.
 */
@SuppressWarnings("unchecked")
public final class UserAccount {
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

      // If the user has no projects, then we return an empty list.
      if (jsonProject.get("ProjectIDs").toString().equals("")) {
        return new ArrayList<>();
      }

      // Obtain our project ID list, and return this as a string of Longs.
      for (String s : jsonProject.get("ProjectIDs").toString().split(",")) {
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

      // If the user has no projects, then we return an empty list.
      if (jsonProject.get("ProjectNames").toString().equals("")) {
        return new ArrayList<>();
      }

      // Obtain our project ID list, and return this as a string of Longs.
      nameList.addAll(Arrays.asList((jsonProject.get("ProjectNames").toString().split(","))));
      return nameList;

    } catch (ParseException e) {
      // We return an empty list in the event we cannot parse our string.
      return new ArrayList<>();
    }
  }

  /**
   * Remove the project with the given project ID from the given project JSON string.
   *
   * @param p         Project JSON that corresponds to all projects associated with the current user.
   * @param projectID ID of the project to remove.
   * @return The same JSON string, without the project with the projectID.
   */
  public static String removeFromProjectJSON(String p, long projectID) {
    ArrayList<String> projectNames = namesFromProjectJSON(p);
    ArrayList<Long> projectIDs = idsFromProjectJSON(p);

    JSONObject jsonProject = new JSONObject();
    int i = projectIDs.indexOf(projectID);

    // If this does not exist, return our input p.
    if (i < 0) {
      return p;

    } else {
      // Otherwise, remove the elements at location 'i' and return our JSON.
      projectNames.remove(i);
      projectIDs.remove(i);

      jsonProject.put("ProjectIDs", String.join(",", projectNames));
      jsonProject.put("ProjectNames", String.join(",",
          projectIDs.stream().map(Object::toString).collect(Collectors.toList())));
      return jsonProject.toJSONString();
    }
  }

  /**
   * Insert the given project ID and project name into our given project JSON string.
   *
   * @param p           Project JSON that corresponds to all projects associated with the current user.
   * @param projectID   ID of the project to insert.
   * @param projectName Name of the project ot insert.
   * @return The same JSON string, with the new project.
   */
  public static String insertIntoProjectJSON(String p, long projectID, String projectName) {
    ArrayList<String> projectNames = namesFromProjectJSON(p);
    ArrayList<Long> projectIDs = idsFromProjectJSON(p);
    JSONObject jsonProject = new JSONObject();

    // If our project already exists in list, return the same JSON string.
    if (projectIDs.contains(projectID)) {
      return p;
    }

    // Otherwise, insert the ID and name into the appropriate lists.
    projectNames.add(projectName);
    projectIDs.add(projectID);

    // Return our new JSON.
    jsonProject.put("ProjectIDs", String.join(",", projectNames));
    jsonProject.put("ProjectNames", String.join(",",
        projectIDs.stream().map(Object::toString).collect(Collectors.toList())));

    return jsonProject.toJSONString();
  }

  /**
   * Creates an account using the given login information. Following the resource below:
   * https://www.mkyong.com/webservices/jax-rs/restful-java-client-with-apache-httpclient/
   *
   * @param u Username of the user to create an account for.
   * @param p Password of the user to create an account for.
   * @return Error message returned from account creation attempt. If there is no error, this is empty.
   */
  public static String createAccount(String u, String p) {
    JSONParser jsonParser = new JSONParser();

    try {
      DefaultHttpClient httpClient = new DefaultHttpClient();

      HttpPost postRequest = new HttpPost("http://localhost/PHPWebServer/register.php");
      List<NameValuePair> params = new ArrayList<>(Arrays.asList(new BasicNameValuePair("username", u),
          new BasicNameValuePair("password", p)));
      postRequest.setEntity(new UrlEncodedFormEntity(params));

      // POST our login information, wait for our response.
      HttpResponse response = httpClient.execute(postRequest);
      if (response.getStatusLine().getStatusCode() != 200) {
        throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
      }

      // Read our response.
      BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
      JSONObject jsonReturned = (JSONObject) jsonParser.parse(br);

      // Disconnect and return our error message that we parsed.
      httpClient.getConnectionManager().shutdown();
      return (String) jsonReturned.get("ErrorMessage");

    } catch (ParseException | IOException e) {
      return "Exception occurred. Do not proceed.";
    }
  }

  /**
   * Checks if the password matches with the given username. If this is successful, we return the authentication token
   * in the first element, and the project JSON in the second. Following the resource below:
   * https://www.mkyong.com/webservices/jax-rs/restful-java-client-with-apache-httpclient/
   *
   * @param u Username of the user to login with.
   * @param p Password of the user to login with.
   * @return If the login is successful, a three element array of the username, the authentication token, and the JSON
   * array of project IDs and names associated with the user. Otherwise, an empty array.
   */
  public static ArrayList<String> verifyLoginInfo(String u, String p) {
    JSONParser jsonParser = new JSONParser();

    try {
      DefaultHttpClient httpClient = new DefaultHttpClient();

      HttpPost postRequest = new HttpPost("http://localhost/PHPWebServer/login.php");
      List<NameValuePair> params = new ArrayList<>(Arrays.asList(new BasicNameValuePair("username", u),
          new BasicNameValuePair("password", p)));
      postRequest.setEntity(new UrlEncodedFormEntity(params));

      // POST our login information, wait for our response.
      HttpResponse response = httpClient.execute(postRequest);
      if (response.getStatusLine().getStatusCode() != 200) {
        throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
      }

      // Read our response.
      BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
      JSONObject jsonReturned = (JSONObject) jsonParser.parse(br);
      JSONObject errorMessage = (JSONObject) jsonParser.parse(jsonReturned.get("ErrorJSON").toString());

      // If we have an error, return an empty list.
      if (errorMessage.get("Error").toString().equals("False")) {
        httpClient.getConnectionManager().shutdown();
        return new ArrayList<>();

      } else {
        // Otherwise, return the username, authentication token and the project JSON.
        httpClient.getConnectionManager().shutdown();
        return new ArrayList<>(Arrays.asList(u, errorMessage.get("Auth").toString(),
            jsonReturned.get("ProjectsJSON").toString()));

      }
    } catch (ParseException | IOException e) {
      return new ArrayList<>();

    }
  }
}
