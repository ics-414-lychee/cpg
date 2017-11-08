package com.Interface;

import com.ActivityNetwork.ActivityNetwork;
import com.ActivityNetwork.NetworkController;

import java.io.Console;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.System.exit;

public class CommandLineInterface {
  /**
   * Verify if the response exists in the given input space.
   *
   * @param response   Response to verify.
   * @param inputSpace Commands that are accepted.
   * @return True if responses exists in input space. False otherwise.
   */
  private boolean verifyResponse(String response, ArrayList<String> inputSpace) {
    for (String i : inputSpace) {
      if (response.equals(i)) {
        return true;
      }
    }

    return false;
  }

  /**
   * The login console. Checks if the login was successful or not (within three tries).
   *
   * @param c Console object. Used to read responses from the user.
   * @return If the login is successful, a two element array of the authentication token, and the JSON array of project
   * IDs and names associated with the user. Otherwise, an empty array.
   */
  private ArrayList<String> loginConsole(Console c) {
    int loginAttempts = 0;

    while (loginAttempts++ < 3) {
      // Request username and password.
      String username = c.readLine("Username: ");
      String password = new String(c.readPassword("Password: "));

      // Obtain the response of our login attempt.
      ArrayList<String> response = UserAccount.verifyLoginInfo(username, password);

      if (response.isEmpty()) {
        System.out.print("\nIncorrect login information. Please try again.\n ");

      } else {
        return response;
      }
    }

    System.out.print("\nYou have exceeded the number of login attempts.\n");
    return new ArrayList<>();
  }

  /**
   * The account creation console. Checks if the account creation was successful or not (within three tries).
   *
   * @param c Console object. Used to read responses from the user.
   * @return True if the account was successfully created. False otherwise.
   */
  private boolean accountCreationConsole(Console c) {
    int passwordCreationAttempts = 0;

    // First, request the username.
    String username = c.readLine("Desired Username: ");

    // Then, request the password. This must be entered within 3 attempts.
    while (passwordCreationAttempts++ < 3) {
      String password1 = new String(c.readPassword("Desired Password: "));
      String password2 = new String(c.readPassword("Enter Your Password Again: "));

      if (!password1.equals(password2)) {
        System.out.print("Incorrect password. Please try again.");

      } else {
        String result = UserAccount.createAccount(username, password1);

        // Empty result from account creation indicates that an account was successfully created.
        if (result.equals("")) {
          System.out.print("Account successfully created. Please login.");
          return true;

        } else {
          System.out.print("Error: " + result);
          return false;

        }
      }
    }

    // User has exceeded the number of attempts. Return false.
    return false;
  }

  /**
   * Display the welcome screen. Determine if the user wants to register a new user, or login.
   *
   * @param c Console object. Used to read responses from the user.
   * @return If the login is successful, a two element array of the authentication token, and the JSON array of project
   * IDs and names associated with the user. Otherwise, an empty array.
   */
  public ArrayList<String> welcomeScreen(Console c) {
    boolean successfulLogin = false;
    ArrayList<String> tokenAndProject = new ArrayList<>();

    System.out.print("Team Lychee Critical Path Application\n\n");
    while (!successfulLogin) {

      // First, we ask if they have an account with us.
      String accountExistenceResponse = c.readLine("Do you have an account? [Y/N]");
      while (!verifyResponse(accountExistenceResponse, new ArrayList<>(Arrays.asList("Y", "N")))) {
        System.out.print("Invalid response. Please enter 'Y' for yes, or 'N' for no.");
      }

      // If our answer is yes, then we bring them to our login screen.
      if (accountExistenceResponse.equals("Y")) {
        tokenAndProject = loginConsole(c);
        successfulLogin = !tokenAndProject.isEmpty();

      } else {
        // If our answer is no, then we ask about account creation.
        String accountCreationResponse = c.readLine("Would you like to create one? [Y/N]");
        while (!verifyResponse(accountCreationResponse, new ArrayList<>(Arrays.asList("Y", "N")))) {
          System.out.print("Invalid response. Please enter 'Y' for yes, or 'N' for no.");
        }

        // If our answer is yes, then bring them to the account creation screen, then back to the login screen.
        if (accountCreationResponse.equals("Y")) {
          accountCreationConsole(c);

        } else {
          // Exit program here.
          System.out.print("Goodbye.\n");
          exit(1);
        }
      }
    }

    return tokenAndProject;
  }

  /**
   * Display the project overview screen. This displays all projects the user currently possess, and gives the user
   * the freedom to add, edit, or delete new projects. The actions here are irreversible!
   *
   * @param c Console object. Used to read responses from the user.
   * @param token Authentication token. Obtained through successful login.
   * @param projectJSON JSON array of Project IDs and names associated with a given user.
   * @return The network ID the user desires to operate on. 0 indicates that the user wants to exit.
   */
  public Long projectOverviewScreen(Console c, String token, String projectJSON) {
    // TODO: work on project overview screen.
    return (long) 0;
  }

  /**
   * Display the project edit screen. This gives the user the freedom to add nodes, edit nodes, remove nodes, view
   * the critical path, compute slack times, undo an action, or redo an action.
   *
   * @param c Console object. Used to read responses from the user.
   * @param token Authentication token. Obtained through successful login.
   * @param projectJSON JSON array of Project IDs and names associated with a given user.
   * @param networkID Network ID of the
   * @return True if the user does not want to exit. False otherwise.
   */
  public boolean projectSpecificScreen(Console c, String token, String projectJSON, Long networkID) {
    // TODO: work on project specific screen.
    return true;
  }
}
