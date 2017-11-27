package com.BaseInterface;

import java.util.*;

class CommandLineInterface {
  /** Scanner object. Used to read responses from the user. */
  private Scanner sc;

  /**
   * Constructor. We set our scanner object here.
   *
   * @param sc Scanner object. Used to read responses from the user.
   */
  CommandLineInterface(Scanner sc) {
    this.sc = sc;
  }

  /**
   * Verify if the response exists in the given input space.
   *
   * @param response   Response to verify.
   * @param inputSpace Commands that are accepted.
   * @return True if responses exists in input space. False otherwise.
   */
  boolean verifyResponse(String response, ArrayList<String> inputSpace) {
    return inputSpace.contains(response);
  }

  /**
   * Print a query, and return the response given by our scanner.
   *
   * @param query Question to prompt user with.
   * @return The response to our query.
   */
  String readLine(String query) {
    System.out.print(query);
    return sc.nextLine();
  }

  /**
   * Print a query, and return the response given by our scanner. This should mask the password entered.
   *
   * @param query Question to prompt user with.
   * @return The password entered by our user.
   */
  String readPassword(String query) {
    System.out.print(query);
    return sc.nextLine();
  }

  /**
   * Print a header. Nothing more, nothing less.
   */
  void printHeader() {
    System.out.println("*******************************************************************");
  }

  /**
   * Attach a title to the header.
   *
   * @param title Title to attach to our header.
   */
  void printHeader(String title) {
    printHeader();
    System.out.println(title);
    printHeader();
  }

  /**
   * Ask the a question, and verify that their input is "Y" for yes, or "N" for no.
   *
   * @param query Question to ask the user.
   * @return True if their answer to the query is yes. False otherwise.
   */
  boolean yesOrNoQuestion(String query) {
    String response = readLine(query + " [Y/N]: ");
    while (!verifyResponse(response, new ArrayList<>(Arrays.asList("Y", "N", "y", "n")))) {
      System.out.println("Invalid response. Please enter 'Y' for yes, or 'N' for no.");
      response = readLine(query + " [Y/N]: ");
    }

    return response.equalsIgnoreCase("Y");
  }

  /**
   * Get a valid, positive time in hours from the user.
   *
   * @param specificTime Type of time to prompt the user to enter.
   * @return A valid, positive time in hours.
   */
  double getPositiveTime(String specificTime) {
    boolean isANumber = false;
    double validTime = 0;

    while (!isANumber) {
      String timeGiven = readLine("Please enter the " + specificTime + " time (in hours): ");
      try {
        validTime = Double.valueOf(timeGiven);

        if (validTime <= 0) {
          System.out.println("Duration must be greater than 0. Please enter another time. ");
        } else {
          isANumber = true;
        }

      } catch (NumberFormatException e) {
        System.out.println("Not a number. Please enter a number. ");
      }
    }

    return validTime;
  }
}
