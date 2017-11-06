package com.Interface;

import com.ActivityNetwork.NetworkController;
import com.ActivityNetwork.NetworkStorage;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.System.exit;

public class CommandLineRunner {
  /// Network controller, manages several projects.
  NetworkController nc = new NetworkController();

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
   * @return True if the login info was entered within three tries. Otherwise, false to indicate to loop again.
   */
  private boolean loginConsole(Console c) {
    int loginAttempts = 0;

    while (loginAttempts++ < 3) {
      // Request username and password.
      String username = c.readLine("Username: ");
      String password = new String(c.readPassword("Enter your password: "));

      if (!NetworkStorage.verifyLoginInfo(username, password)) {
        System.out.print("\nIncorrect login information. Please try again.\n ");
      } else {
        return true;
      }
    }

    System.out.print("\nYou have exceeded the number of login attempts.\n");
    return false;
  }

  /**
   * The account creation console. Creates an account,
   *
   * @param c
   * @return
   */
  private boolean accountCreationConsole(Console c) {

  }

  /**
   * Display the welcome screen. Determine if the user wants to register a new user, or login. Return true to continue,
   * or false to exit the program.
   *
   * Account Exists? ---> No.  ---> User wants to create an account? ---> No. Go back to one.
   * ---> Yes. ---> Login.                           ---> Yes. Create account.
   *
   * @param c Console object. Used to read responses from the user.
   * @return True if the program should continue. False if the program should break here.
   */
  private boolean welcomeScreen(Console c) {
    boolean validAccountExistenceResponse = false, validAccountCreationResponse = false;
    System.out.print("Team Lychee Critical Path Application\n\n");

    // First, we ask if they have an account with us.
    while (!validAccountExistenceResponse) {
      String accountExistenceResponse = c.readLine("Do you have an account? [Y/N]");

      if (!verifyResponse(accountExistenceResponse, new ArrayList<>(Arrays.asList("Y", "N")))) {
        // We have an invalid response. Iterate through loop.
        System.out.print("Invalid response. Please enter 'Y' for yes, or 'N' for no.");

      } else if (accountExistenceResponse.equals("Y")) {
        // If our answer is yes, then we bring them to our login screen.
        validAccountExistenceResponse = loginConsole(c);

      } else if (accountExistenceResponse.equals("N")) {
        // If our answer is no, then we ask if they want to create an account.
        String accountCreationResponse = c.readLine("Would you like to create one? [Y/N]");

        while (!validAccountCreationResponse) {
          if (!verifyResponse(accountCreationResponse, new ArrayList<>(Arrays.asList("Y", "N")))) {
            // We have an invalid response. Iterate through loop.
            System.out.print("Invalid response. Please enter 'Y' for yes, or 'N' for no.");

          } else if (accountCreationResponse.equals("Y")) {
            // The user wants to create an account. Bring them to the account creation screen.
            validAccountCreationResponse = ;

          } else if (accountCreationResponse.equals("N")) {
            // The user does not want to create an account. Exit here.
            validAccountCreationResponse = ;

          }
        }
      }
    }

    return true;
  }

  /**
   * Executes the runner. Any arguments passed are not used.
   *
   * @param args Arguments passed. This is not used.
   */
  public static void main(String[] args) throws IOException {
    CommandLineRunner r = new CommandLineRunner();
    Console c = System.console();

    // We need the console for this to run!
    if (c == null) {
      System.err.println("No console.");
      exit(1);
    }

    // We first present the login screen. User has access to command space: [create, login]
    while (!r.loginConsole(c)) ;

    // Once passed, the user has access to the projects screen, and command space: [add, delete, edit, exit].

    // We are now in the single project screen. User has access to commands: [add, edit, delete, exit].

  }


}
