package com.Interface;

import com.ActivityNetwork.ActivityNetwork;
import com.ActivityNetwork.NetworkController;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.System.exit;

class CommandLineInterface {
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
   * @return If the login is successful, a three element array of the username, the  authentication token, and the JSON
   * array of project IDs and names associated with the user. Otherwise, an empty array.
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
        System.out.println("Invalid response. Please enter 'Y' for yes, or 'N' for no.");
        accountExistenceResponse = c.readLine("Do you have an account? [Y/N]");
      }

      // If our answer is yes, then we bring them to our login screen.
      if (accountExistenceResponse.equals("Y")) {
        tokenAndProject = loginConsole(c);
        successfulLogin = !tokenAndProject.isEmpty();

      } else {
        // If our answer is no, then we ask about account creation.
        String accountCreationResponse = c.readLine("Would you like to create one? [Y/N]");
        while (!verifyResponse(accountCreationResponse, new ArrayList<>(Arrays.asList("Y", "N")))) {
          System.out.println("Invalid response. Please enter 'Y' for yes, or 'N' for no.");
          accountCreationResponse = c.readLine("Would you like to create one? [Y/N]");
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
   * Display the menu for the project overview screen, and record the desired action from the user.
   *
   * @param c Console object. Used to read responses from the user.
   * @return A two element array of Strings, with the first entry representing the command, and the second
   * representing the project name to change (if applicable).
   */
  private String[] acceptProjectOverviewCommand(Console c) {
    boolean isValidCommand = false;

    System.out.println("----------------------------------------------------");
    System.out.println("Enter 'exit' to exit the program.");
    System.out.println("Enter 'view' to view all of your projects.");
    System.out.println("Enter 'add [project-name]' to add a new project.");
    System.out.println("Enter 'edit [project-name]' to edit an existing project.");
    System.out.println("Enter 'delete [project-name]' to delete an existing project. This is irreversible!");
    System.out.println("----------------------------------------------------\n");

    String[] response = c.readLine("Please enter your option: ").split("\\s+");
    while (!isValidCommand) {

      // Our command must not be empty, and it cannot be greater than 2 words.
      if (response.length > 2 || response.length == 0) {
        System.out.println("Invalid response. Please follow the format specified for the commands.");
        response = c.readLine("Please enter your option: ").split("\\s+");

      } else if (!verifyResponse(response[0], new ArrayList<>(Arrays.asList("exit",
          "view", "add", "edit", "delete")))) {
        System.out.println("Invalid response. Please only use the specified commands.");
        response = c.readLine("Please enter your option: ").split("\\s+");

      } else {
        isValidCommand = true;

      }
    }

    return response;
  }

  /**
   * Display the project overview screen. This displays all projects the user currently possess, and gives the user
   * the freedom to add, edit, or delete new projects. The actions here are irreversible!
   *
   * @param c           Console object. Used to read responses from the user.
   * @param nc          Network controller, created upon successful login.
   * @param projectJSON JSON array of Project IDs and names associated with a given user.
   * @return 0 if the user wants to exit. Otherwise, the network ID of the network the user wants to edit.
   */
  public Long projectOverviewScreen(Console c, NetworkController nc, String projectJSON) {
    boolean successfulSelection = false;
    long networkID = 0;

    while (!successfulSelection) {
      // First, get the desired command.
      String[] command = acceptProjectOverviewCommand(c);

      switch (command[0]) {
        case "exit":
          successfulSelection = true;
          break;

        case "view":
          System.out.println("Your projects are: \n----------------------------------------------------");
          UserAccount.namesFromProjectJSON(projectJSON).forEach(s -> System.out.println("> " + s));
          System.out.println("----------------------------------------------------");
          break;

        case "add":
          successfulSelection = true;
          networkID = nc.createNetwork(command[1]);
          break;

        case "edit":
          // Here, we verify that the project actually exists.
          ArrayList<String> names = UserAccount.namesFromProjectJSON(projectJSON);
          if (!names.contains(command[1])) {
            System.out.println("Network does not exist. ");

          } else {
            successfulSelection = true;
            networkID = UserAccount.idsFromProjectJSON(projectJSON).get(names.indexOf(command[1]));

          }
          break;

        case "delete":
          System.out.println(!nc.deleteNetwork(networkID) ? "Network does not exist. " :
              "Network successfully deleted. ");
          break;
      }
    }

    return networkID;
  }

  /**
   * Display the menu for the project specific screen, and record the desired action from the user.
   *
   * @param c Console object. Used to read responses from the user.
   * @return A two element array of Strings, with the first entry representing the command, and the second
   * representing the node name to change (if applicable).
   */
  private String[] acceptProjectSpecificCommand(Console c) {
    boolean isValidCommand = false;

    System.out.println("----------------------------------------------------");
    System.out.println("Enter 'exit' to return to the project overview screen.");
    System.out.println("Enter 'add [node-name]' to add a node.");
    System.out.println("Enter 'edit [node-name]' to edit an existing node.");
    System.out.println("Enter 'remove [node-name]' to remove an existing node.");
    System.out.println("Enter 'slack-total [node-name]' to view the total slack of a node.");
    System.out.println("Enter 'slack-safety [node-name]' to view the safety slack of a node.");
    System.out.println("Enter 'slack-free [node-name]' to view the free slack of a node.");
    System.out.println("Enter 'view-network' to view the entire network.");
    System.out.println("Enter 'view-critical' to view the critical path.");
    System.out.println("Enter 'undo' to undo the most recent add/edit/remove.");
    System.out.println("Enter 'redo' to redo the most recent add/edit/remove.");
    System.out.println("----------------------------------------------------\n");

    String[] response = c.readLine("Please enter your option: ").split("\\s+");
    while (!isValidCommand) {

      // Our command must not be empty, and it cannot be greater than 2 words.
      if (response.length > 2 || response.length == 0) {
        System.out.println("Invalid response. Please follow the format specified for the commands.");
        response = c.readLine("Please enter your option: ").split("\\s+");

      } else if (!verifyResponse(response[0], new ArrayList<>(Arrays.asList("exit", "add", "edit", "remove",
          "slack-total", "slack-safety", "slack-free", "view-network", "view-critical", "undo", "redo")))) {
        System.out.println("Invalid response. Please only use the specified commands.");
        response = c.readLine("Please enter your option: ").split("\\s+");

      } else {
        isValidCommand = true;

      }
    }

    return response;
  }

  /**
   * Display the console to add a new node to the network.
   *
   * @param c        Console object. Used to read responses from the user.
   * @param w        Working instance of our network.
   * @param nodeName Name of node to add.
   */
  private void addNodeConsole(Console c, ActivityNetwork w, String nodeName) {
    // TODO: finish add node console.
  }

  /**
   * Display the console to edit an existing node on our network.
   *
   * @param c        Console object. Used to read responses from the user.
   * @param w        Working instance of our network.
   * @param nodeName Name of node to edit.
   */
  private void editNodeConsole(Console c, ActivityNetwork w, String nodeName) {
    // TODO: finish edit node console.
  }

  /**
   * Display the project edit screen. This gives the user the freedom to add nodes, edit nodes, remove nodes, view
   * the critical path, compute slack times, undo an action, or redo an action.
   *
   * @param c         Console object. Used to read responses from the user.
   * @param nc        Network controller, created upon successful login.
   * @param networkID Network ID of the desired network to modify.
   */
  public void projectSpecificScreen(Console c, NetworkController nc, Long networkID) {
    boolean successfulSelection = false;

    while (!successfulSelection) {
      // First, get the desired command and copy the current instance of our network.
      String[] command = acceptProjectSpecificCommand(c);
      ActivityNetwork w = nc.retrieveNetwork(networkID);

      switch (command[0]) {
        case "exit":
          successfulSelection = true;
          break;

        case "add":
          addNodeConsole(c, w, command[1]);
          break;

        case "edit":
          editNodeConsole(c, w, command[1]);
          break;

        case "slack-total":
          System.out.println("The total slack of \"" + command[1] + "\" is " +
              w.computeTotalSlack(w.nodeIdFromName(command[1])));
          break;

        case "slack-safety":
          System.out.println("The safety slack of \"" + command[1] + "\" is " +
              w.computeSafetySlack(w.nodeIdFromName(command[1])));
          break;

        case "slack-free":
          System.out.println("The free slack of \"" + command[1] + "\" is " +
              w.computeFreeSlack(w.nodeIdFromName(command[1])));
          break;

        case "view-network":
          System.out.println(w.getNodeList().toString());
          break;

        case "view-critical":
          System.out.println(w.computeCriticalPath().toString());
          break;

        case "undo":
          nc.undoNetworkChange(networkID);
          break;

        case "redo":
          nc.redoNetworkChange(networkID);
          break;
      }

      // Commit our changes to our network chains.
      nc.modifyNetwork(w);
    }
  }
}
