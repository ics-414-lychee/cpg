package com.Interface;

import com.ActivityNetwork.ActivityNetwork;
import com.ActivityNetwork.ActivityNode;
import com.ActivityNetwork.NetworkController;

import java.util.*;

class CommandLineProject extends CommandLineInterface {
  /**
   * Constructor. We set our scanner object here.
   *
   * @param sc Scanner object. Used to read responses from the user.
   */
  CommandLineProject(Scanner sc) {
    super(sc);
  }

  /**
   * Display the menu for the project overview screen, and record the desired action from the user.
   *
   * @param printMenu Flag to determine if the menu should be printed again.
   * @return A two element array of Strings, with the first entry representing the command, and the second
   * representing the project name to change (if applicable).
   */
  private String[] acceptProjectOverviewCommand(boolean printMenu) {
    boolean isValidCommand = false;

    // If desired, we print our menu.
    if (printMenu) {
      System.out.println();
      printHeader("Project Overview");

      System.out.println("Commands: exit");
      Arrays.asList("view", "add    [project-name]", "edit   [project-name]", "delete [project-name]",
          "menu").forEach(s -> System.out.println("          " + s));
      printHeader();
    }

    String[] response = readLine("Please enter your option: ").split("\\s+");
    while (!isValidCommand) {

      // Our command must not be empty, and it cannot be greater than 2 words.
      if (response.length > 2 || response.length == 0) {
        System.out.println("Invalid response. Please follow the format specified for the commands.\n");
        response = readLine("Please enter your option: ").split("\\s+");

      } else if (!verifyResponse(response[0], new ArrayList<>(Arrays.asList("exit",
          "view", "add", "edit", "delete", "menu")))) {
        System.out.println("Invalid response. Please only use the specified commands.\n");
        response = readLine("Please enter your option: ").split("\\s+");

      } else if (verifyResponse(response[0], new ArrayList<>(Arrays.asList("add", "edit", "delete"))) &&
          response.length != 2) {
        System.out.println("Invalid response. No project name. \n");
        response = readLine("Please enter your option: ").split("\\s+");

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
   * @param nc Network controller, created upon successful login.
   * @return 0 if the user wants to exit. Otherwise, the network ID of the network the user wants to edit.
   */
  Long projectOverviewScreen(NetworkController nc) {
    boolean successfulSelection = false, printMenu = true;
    ArrayList<String> names = UserAccount.namesFromProjectJSON(nc.getProjectJSON());
    long networkID = 0;

    while (!successfulSelection) {
      // First, get the desired command.
      String[] command = acceptProjectOverviewCommand(printMenu);
      printMenu = false;

      switch (command[0]) {
        case "exit":
          System.out.println("Goodbye.");
          successfulSelection = true;
          break;

        case "view":
          if (UserAccount.namesFromProjectJSON(nc.getProjectJSON()).isEmpty()) {
            System.out.println("\nYou have no projects. Go start one!\n");

          } else {
            System.out.print("\nYour projects are: " + UserAccount.namesFromProjectJSON(nc.getProjectJSON()).get(0));
            System.out.println();
            UserAccount.namesFromProjectJSON(nc.getProjectJSON()).stream().skip(1).forEach(s ->
                System.out.println("                   " + s));
            System.out.println();
          }
          break;

        case "add":
          successfulSelection = true;
          networkID = nc.createNetwork(command[1]);
          break;

        case "edit":
          // Here, we verify that the project actually exists.
          if (!names.contains(command[1])) {
            System.out.println("Project does not exist. \n");

          } else {
            successfulSelection = true;
            networkID = UserAccount.idsFromProjectJSON(nc.getProjectJSON()).get(names.indexOf(command[1]));

          }
          break;

        case "delete":
          // Here, we verify that the project actually exists.
          if (!names.contains(command[1])) {
            System.out.println("Project does not exist. \n");

          } else {
            System.out.println(!nc.deleteNetwork(UserAccount.idsFromProjectJSON(nc.getProjectJSON()).get(
                names.indexOf(command[1]))) ? "Project does not exist. \n" : "Project successfully deleted. \n");
          }
          break;

        case "menu":
          printMenu = true;
          break;
      }
    }

    return networkID;
  }

  /**
   * Display the menu for the project specific screen, and record the desired action from the user.
   *
   * @param printMenu Flag to determine if the menu should be printed or not.
   * @return A two element array of Strings, with the first entry representing the command, and the second
   * representing the argument to that command (if applicable).
   */
  private String[] acceptProjectSpecificCommand(boolean printMenu) {
    boolean isValidCommand = false;

    // If desired, we print our menu.
    if (printMenu) {
      System.out.println();
      printHeader("Project Specific");

      System.out.println("Commands: exit");
      Arrays.asList("add          [activity-name]", "edit         [activity-name]", "remove       [activity-name]",
          "slack-total  [activity-name]", "slack-safety [activity-name]", "slack-free   [activity-name]",
          "set-deadline", "view-network", "view-critical", "undo", "redo",
          "menu").forEach(s -> System.out.println("          " + s));
      printHeader();
    }

    String[] response = readLine("Please enter your option: ").split("\\s+");
    while (!isValidCommand) {

      // Our command must not be empty, and it cannot be greater than 2 words.
      if (response.length > 2 || response.length == 0) {
        System.out.println("Invalid response. Please follow the format specified for the commands.");
        response = readLine("Please enter your option: ").split("\\s+");

      } else if (!verifyResponse(response[0], new ArrayList<>(Arrays.asList("exit", "add", "edit", "remove",
          "slack-total", "slack-safety", "slack-free", "set-deadline", "view-network", "view-critical", "undo",
          "redo")))) {
        System.out.println("Invalid response. Please only use the specified commands.");
        response = readLine("Please enter your option: ").split("\\s+");

      } else if (verifyResponse(response[0], new ArrayList<>(Arrays.asList("add", "edit", "remove", "slack-total",
          "slack-free"))) && response.length != 2) {
        System.out.println("Invalid response. No activity name.");
        response = readLine("Please enter your option: ").split("\\s+");

      } else {
        isValidCommand = true;

      }
    }

    return response;
  }

  /**
   * Get valid dependencies for the given node.
   *
   * @param w        Working instance of our network.
   * @param nodeName Name of node to add dependencies to.
   * @return Set of valid dependencies for a given node.
   */
  private Set<Long> getValidDependencies(ActivityNetwork w, String nodeName) {
    boolean desiredExit = false;
    Set<Long> dependencies = new HashSet<>();

    System.out.println("You are now setting your dependencies.");
    if (yesOrNoQuestion("Would you like to see all current activities in your project? ")) {
      System.out.println(w.toString());
    }

    while (!desiredExit) {
      String dependency = readLine("Enter a dependency, or hit enter to exit. ");
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
   * @param w        Working instance of our network.
   * @param nodeName Name of node to add.
   */
  private void addNodeConsole(ActivityNetwork w, String nodeName) {
    if (w.isNodeInNetwork(nodeName)) {
      System.out.println("Node exists in network. Please choose a unique name.");
      return;
    }

    // The new node ID is incremented from the node with the current largest node ID. Start at 1 if this is empty.
    long nodeID = w.getNodeList().stream().max(
        Comparator.comparingLong(ActivityNode::getNodeId)).orElse(
        new ActivityNode(1, "", "", 0, 0, 0)).getNodeId() + 1;

    String description = readLine("Please describe your activity in one line: ");
    double optimisticTime = getPositiveTime("optimistic");
    double normalTime = getPositiveTime("normal");
    double pessimisticTime = getPositiveTime("pessimistic");
    Set<Long> dependencies = getValidDependencies(w, nodeName);

    while (!w.setHoursDeadline(getPositiveTime("deadline"))) {
      System.out.println("Deadline is not realistic. Please choose a longer deadline.");
    };

    ActivityNode n = new ActivityNode(nodeID, nodeName, description, optimisticTime, normalTime, pessimisticTime);
    n.setDependencies(dependencies);
    w.insertNode(n);
  }

  /**
   * Display the console to edit an existing node on our network.
   *
   * @param w        Working instance of our network.
   * @param nodeName Name of node to edit.
   */
  private void editNodeConsole(ActivityNetwork w, String nodeName) {
    if (!w.isNodeInNetwork(nodeName)) {
      System.out.println("Node does not exist in network. Please choose an existing node.");
      return;
    }
    Long nodeID = w.nodeIdFromName(nodeName);
    ActivityNode n = w.retrieveNode(nodeID);
    boolean timeHasChanged = false;

    // For each entry in our node, we ask if the user wants to update that field.
    for (String entry : new ArrayList<>(Arrays.asList("name", "description", "optimistic time", "normal time",
        "pessimistic time", "dependencies"))) {
      boolean updateDesired = yesOrNoQuestion("Would you like to update this node's " + entry + "?");

      switch (entry) {
        case "name":
          String name = updateDesired ? readLine("Please enter a new node name: ") : n.getName();
          while (w.isNodeInNetwork(nodeName) && updateDesired) {
            System.out.println("There exists a node in the project with that name. Please choose another. ");
            name = readLine("Please enter a new node name: ");
          }
          n.setName(name);
          break;

        case "description":
          n.setDescription(updateDesired ? readLine("Please describe your activity in one line: ") :
              n.getDescription());
          break;

        case "optimistic time":
          n.setOptimisticTime(updateDesired ? getPositiveTime("optimistic") : n.getTimes()[0]);
          timeHasChanged = updateDesired || timeHasChanged;
          break;

        case "normal time":
          n.setNormalTime(updateDesired ? getPositiveTime("normal") : n.getTimes()[1]);
          timeHasChanged = updateDesired || timeHasChanged;
          break;

        case "pessimistic time":
          n.setPessimisticTime(updateDesired ? getPositiveTime("pessimistic") : n.getTimes()[2]);
          timeHasChanged = updateDesired || timeHasChanged;
          break;

        case "dependencies":
          n.setDependencies(getValidDependencies(w, nodeName));
          break;
      }
    }

    // User has to update their deadline if they change the times.
    if (timeHasChanged) {
      w.setHoursDeadline(getPositiveTime("deadline"));
    }

    // Modification is node removal and insertion.
    w.deleteNode(nodeID);
    w.insertNode(n);
  }

  /**
   * Display the project edit screen. This gives the user the freedom to add nodes, edit nodes, remove nodes, view
   * the critical path, compute slack times, undo an action, or redo an action.
   *
   * @param nc        Network controller, created upon successful login.
   * @param networkID Network ID of the desired network to modify.
   */
  void projectSpecificScreen(NetworkController nc, Long networkID) {
    boolean successfulSelection = false, printMenu = true;

    while (!successfulSelection) {
      // First, get the desired command and copy the current instance of our network.
      String[] command = acceptProjectSpecificCommand(printMenu);
      ActivityNetwork w = nc.retrieveNetwork(networkID);
      printMenu = false;

      switch (command[0]) {
        case "exit":
          successfulSelection = true;
          break;

        case "add":
          addNodeConsole(w, command[1]);
          break;

        case "edit":
          editNodeConsole(w, command[1]);
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
          System.out.println((!w.setHoursDeadline(getPositiveTime("deadline"))) ? "Deadline changed successfully. " :
              "Deadline not changed. Time is unrealistic. ");
          break;

        case "slack-free":
          System.out.println("The free slack of \"" + command[1] + "\" is " +
              w.computeFreeSlack(w.nodeIdFromName(command[1])));
          break;

        case "view-network":
          System.out.println(Arrays.toString(w.getNodeList().toArray()));
          break;

        case "view-critical":
          System.out.println(Arrays.toString(w.computeCriticalPath().toArray()));
          break;

        case "undo":
          nc.undoNetworkChange(networkID);
          break;

        case "redo":
          nc.redoNetworkChange(networkID);
          break;

        case "menu":
          printMenu = true;
          break;
      }

      // Commit our changes to our network chains.
      nc.modifyNetwork(w);
    }

    // Save our changes upon exit.
    nc.storeNetwork(networkID);
  }
}
