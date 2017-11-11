package com.Interface;

import com.ActivityNetwork.ActivityNetwork;
import com.ActivityNetwork.ActivityNode;
import com.ActivityNetwork.NetworkController;

import java.io.Console;
import java.util.*;

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
   * Ask the a question, and verify that their input is "Y" for yes, or "N" for no.
   *
   * @param c     Console object. Used to read responses from the user.
   * @param query Question to ask the user.
   * @return True if their answer to the query is yes. False otherwise.
   */
  private boolean yesOrNoQuestion(Console c, String query) {
    String response = c.readLine(query + " [Y/N]");
    while (!verifyResponse(response, new ArrayList<>(Arrays.asList("Y", "N")))) {
      System.out.println("Invalid response. Please enter 'Y' for yes, or 'N' for no.");
      response = c.readLine(query + " [Y/N]");
    }

    return query.equals("Y");
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
  ArrayList<String> welcomeScreen(Console c) {
    boolean successfulLogin = false;
    ArrayList<String> tokenAndProject = new ArrayList<>();

    System.out.print("Team Lychee Critical Path Application\n\n");
    while (!successfulLogin) {

      // First, we ask if they have an account with us.
      if (yesOrNoQuestion(c, "Do you have an account?")) {
        // If our answer is yes, then we bring them to our login screen.
        tokenAndProject = loginConsole(c);
        successfulLogin = !tokenAndProject.isEmpty();

      } else {
        // If our answer is no, then we ask about account creation.
        if (yesOrNoQuestion(c, "Would you like to create one?")) {
          // If our answer is yes, then bring them to the account creation screen, then back to the login screen.
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
  Long projectOverviewScreen(Console c, NetworkController nc, String projectJSON) {
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
            System.out.println("Project does not exist. ");

          } else {
            successfulSelection = true;
            networkID = UserAccount.idsFromProjectJSON(projectJSON).get(names.indexOf(command[1]));

          }
          break;

        case "delete":
          System.out.println(!nc.deleteNetwork(networkID) ? "Project does not exist. " :
              "Project successfully deleted. ");
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
   * representing the argument to that command (if applicable).
   */
  private String[] acceptProjectSpecificCommand(Console c) {
    boolean isValidCommand = false;

    System.out.println("----------------------------------------------------");
    System.out.println("Enter 'exit' to return to the project overview screen.");
    System.out.println("Enter 'add [activity-name]' to add a activity.");
    System.out.println("Enter 'edit [activity-name]' to edit an existing activity.");
    System.out.println("Enter 'remove [activity-name]' to remove an existing activity.");
    System.out.println("Enter 'slack-total [activity-name]' to view the total slack of a activity.");
    System.out.println("Enter 'slack-safety [activity-name]' to view the safety slack of a activity.");
    System.out.println("Enter 'slack-free [activity-name]' to view the free slack of a activity.");
    System.out.println("Enter 'set-deadline' to set the deadline of your entire project.");
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
          "slack-total", "slack-safety", "slack-free", "set-deadline", "view-network", "view-critical", "undo",
          "redo")))) {
        System.out.println("Invalid response. Please only use the specified commands.");
        response = c.readLine("Please enter your option: ").split("\\s+");

      } else {
        isValidCommand = true;

      }
    }

    return response;
  }


  /**
   * Get a valid time (double) in hours from the user.
   *
   * @param c            Console object. Used to read responses from the user.
   * @param specificTime Pessimistic, normal, or optimistic time for the user to enter.
   * @return A time that describes an activity, parsed from the user.
   */
  private double getValidTime(Console c, String specificTime) {
    boolean isANumber = false;
    double validTime = 0;

    while (!isANumber) {
      String timeGiven = c.readLine("Please enter the " + specificTime + " time (in hours): ");
      try {
        validTime = Double.valueOf(timeGiven);

        if (validTime <= 0) {
          System.out.println("Cannot have negative durations. Please enter a non-negative number. ");
        } else {
          isANumber = true;
        }

      } catch (NumberFormatException e) {
        System.out.println("Not a number. Please enter a number. ");
      }
    }

    return validTime;
  }

  /**
   * Get valid dependencies for the given node.
   *
   * @param c        Console object. Used to read responses from the user.
   * @param w        Working instance of our network.
   * @param nodeName Name of node to add dependencies to.
   * @return Set of valid dependencies for a given node.
   */
  private Set<Long> getValidDependencies(Console c, ActivityNetwork w, String nodeName) {
    boolean desiredExit = false;
    Set<Long> dependencies = new HashSet<>();

    System.out.println("You are now setting your dependencies.");
    if (yesOrNoQuestion(c, "Would you like to see all current activities in your project? ")) {
      System.out.println(w.toString());
    }

    while (!desiredExit) {
      String dependency = c.readLine("Enter a dependency, or hit enter to exit. ");
      if (dependency.equals("")) {
        desiredExit = true;

      } else if (dependency.equals(nodeName)) {
        System.out.println("An activity cannot depend on itself. ");

      } else if (!w.isNodeInNetwork(nodeName)) {
        System.out.println("Node does not exist. Please choose a node that exists.");

      } else {
        dependencies.add(w.nodeIdFromName(nodeName));

      }
    }

    return dependencies;
  }


  /**
   * Display the console to add a new node to the network. The node ID of this node is incremented from the current
   * largest.
   *
   * @param c        Console object. Used to read responses from the user.
   * @param w        Working instance of our network.
   * @param nodeName Name of node to add.
   * @return True if the node was added. False otherwise.
   */
  private boolean addNodeConsole(Console c, ActivityNetwork w, String nodeName) {
    if (w.isNodeInNetwork(nodeName)) {
      System.out.println("Node exists in network. Please choose a unique name.");
      return false;
    }

    // The new node ID is incremented from the node with the current largest node ID.
    long nodeID = w.getNodeList().stream().max(
        Comparator.comparingLong(ActivityNode::getNodeId)).orElse(null).getNodeId() + 1;

    String description = c.readLine("Please describe your activity in one line: ");
    double optimisticTime = getValidTime(c, "optimistic");
    double normalTime = getValidTime(c, "normal");
    double pessimisticTime = getValidTime(c, "pessimistic");
    Set<Long> dependencies = getValidDependencies(c, w, nodeName);

    if (yesOrNoQuestion(c, "Would you like to update your project deadline?")) {
      w.setHoursDeadline(getValidTime(c, "deadline"));
    }

    ActivityNode n = new ActivityNode(nodeID, nodeName, description, optimisticTime, normalTime, pessimisticTime);
    n.setDependencies(dependencies);
    return w.insertNode(n);
  }

  /**
   * Display the console to edit an existing node on our network.
   *
   * @param c        Console object. Used to read responses from the user.
   * @param w        Working instance of our network.
   * @param nodeName Name of node to edit.
   */
  private boolean editNodeConsole(Console c, ActivityNetwork w, String nodeName) {
    if (!w.isNodeInNetwork(nodeName)) {
      System.out.println("Node does not exist in network. Please choose an existing node.");
      return false;
    }
    Long nodeID = w.nodeIdFromName(nodeName);
    ActivityNode n = w.retrieveNode(nodeID);

    // For each entry in our node, we ask if the user wants to update that field.
    for (String entry : new ArrayList<>(Arrays.asList("name", "description", "optimistic time", "normal time",
        "pessimistic time", "dependencies"))) {
      boolean updateDesired = yesOrNoQuestion(c, "Would you like to update this node's " + entry + "?");

      switch (entry) {
        case "name":
          String name = updateDesired ? c.readLine("Please enter a new node name: ") : n.getName();
          while (w.isNodeInNetwork(nodeName) && updateDesired) {
            System.out.println("There exists a node in the project with that name. Please choose another. ");
            name = c.readLine("Please enter a new node name: ");
          }
          n.setName(name);
          break;

        case "description":
          n.setDescription(updateDesired ? c.readLine("Please describe your activity in one line: ") :
              n.getDescription());
          break;

        case "optimistic time":
          n.setOptimisticTime(updateDesired ? getValidTime(c, "optimistic") : n.getTimes()[0]);
          break;

        case "normal time":
          n.setNormalTime(updateDesired ? getValidTime(c, "normal") : n.getTimes()[1]);
          break;

        case "pessimistic time":
          n.setPessimisticTime(updateDesired ? getValidTime(c, "pessimistic") : n.getTimes()[2]);
          break;

        case "dependencies":
          n.setDependencies(getValidDependencies(c, w, nodeName));
          break;
      }
    }

    // Modification is node removal and insertion.
    w.deleteNode(nodeID);
    return w.insertNode(n);
  }

  /**
   * Display the project edit screen. This gives the user the freedom to add nodes, edit nodes, remove nodes, view
   * the critical path, compute slack times, undo an action, or redo an action.
   *
   * @param c         Console object. Used to read responses from the user.
   * @param nc        Network controller, created upon successful login.
   * @param networkID Network ID of the desired network to modify.
   */
  void projectSpecificScreen(Console c, NetworkController nc, Long networkID) {
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

        case "set-deadline":
          System.out.println((!w.setHoursDeadline(getValidTime(c, "deadline"))) ? "Deadline changed succesfully. " :
              "Deadline not changed. Time is unrealistic. ");
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
