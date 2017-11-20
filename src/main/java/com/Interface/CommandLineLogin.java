package com.Interface;


import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.System.exit;

class CommandLineLogin extends CommandLineInterface {
  /**
   * Constructor. We set our scanner object here.
   *
   * @param sc Scanner object. Used to read responses from the user.
   */
  CommandLineLogin(Scanner sc) {
    super(sc);
  }

  /**
   * The login console. Checks if the login was successful or not (within three tries).
   *
   * @return If the login is successful, a three element array of the username, the  authentication token, and the JSON
   * array of project IDs and names associated with the user. Otherwise, an empty array.
   */
  private ArrayList<String> loginConsole() {
    int loginAttempts = 0;

    while (loginAttempts++ < 3) {
      // Request username and password.
      String username = readLine("Username: ");
      String password = readPassword("Password: ");

      // Obtain the response of our login attempt.
      ArrayList<String> response = UserAccount.verifyLoginInfo(username, password);

      if (response.isEmpty()) {
        System.out.println("\nIncorrect login information. Please try again.");

      } else {
        printHeader();
        return response;
      }
    }

    System.out.println("\nYou have exceeded the number of login attempts.\n");
    printHeader();

    return new ArrayList<>();
  }

  /**
   * The account creation console. Checks if the account creation was successful or not (within three tries).
   */
  private void accountCreationConsole() {
    int passwordCreationAttempts = 0;
    printHeader();

    // Then, request the password. This must be entered within 3 attempts.
    while (passwordCreationAttempts++ < 3) {
      String username = readLine("Desired Username: ");
      String password1 = readPassword("Desired Password: ");
      String password2 = readPassword("Enter Your Password Again: ");

      if (!password1.equals(password2)) {
        System.out.println("Passwords do not match. Please try again.\n");

      } else {
        String result = UserAccount.createAccount(username, password1);

        // Empty result from account creation indicates that an account was successfully created.
        if (result.equals("")) {
          System.out.println("Account successfully created. Please login.\n");
          printHeader();
          return;

        } else {
          System.out.println("Error: " + result + "\n");

        }
      }
    }

    System.out.println("You have exceeded the number of creation attempts.\n");
  }

  /**
   * Display the welcome screen. Determine if the user wants to register a new user, or login.
   *
   * @return If the login is successful, a two element array of the authentication token, and the JSON array of project
   * IDs and names associated with the user. Otherwise, an empty array.
   */
  ArrayList<String> welcomeScreen() {
    boolean successfulLogin = false;
    ArrayList<String> tokenAndProject = new ArrayList<>();

    System.out.print("Team Lychee Critical Path Application\n\n");
    while (!successfulLogin) {

      // First, we ask if they have an account with us.
      if (yesOrNoQuestion("Do you have an account?")) {
        // If our answer is yes, then we bring them to our login screen.
        tokenAndProject = loginConsole();
        successfulLogin = !tokenAndProject.isEmpty();

      } else {
        // If our answer is no, then we ask about account creation.
        if (yesOrNoQuestion("Would you like to create one?")) {
          // If our answer is yes, then bring them to the account creation screen, then back to the login screen.
          accountCreationConsole();

        } else {
          // Exit program here.
          System.out.print("\nWe can't help you here. ): Goodbye.\n");
          exit(1);
        }
      }
    }

    return tokenAndProject;
  }
}
