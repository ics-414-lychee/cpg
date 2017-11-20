package com.ActivityNetwork;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The ActivityNetwork class, which represents a network of nodes in the Critical Path Method.
 */
public class ActivityNetwork {
  /** Unique identifier for this specific network. */
  private long networkId;

  /** Name of the network, assigned by user. */
  private String networkName;

  /** List of nodes that belong to this network. */
  private ArrayList<ActivityNode> nodeList;

  /** The deadline associated with network. Cannot be less than the minimal critical path sum (length). */
  private double hoursDeadline;

  /// For determining critical path duration.
  private double endDuration;

  /// For determining node id's on critical path.
  private ArrayList<Long> critPathIds;

  /**
   * Sort the current list of nodes by order of dependencies (topological sort).
   *
   * @param listOfNodes List of nodes to sort.
   */

  @SuppressWarnings("StatementWithEmptyBody")
  private void sortNodes(ArrayList<ActivityNode> listOfNodes) {
    Stack<Long> stack = new Stack<>();   // Stack to deposit id's.
    ArrayList<ActivityNode> sortedList = new ArrayList<>(nodeList);

    // Using an original list to check if node has already been pushed.
    ArrayList<ActivityNode> original = new ArrayList<>(nodeList);
    for (ActivityNode node : listOfNodes) {

      if (!original.contains(node)) {
        // Triggers if node has already been pushed.
      } else if (node.getDependencies().size() == 0) {
        stack.push(node.getNodeId());
        original.remove(node);

      } else {
        // Make dependency array.
        Set<Long> depend = node.getDependencies();
        Long[] depArray = depend.toArray(new Long[depend.size()]);

        // Call topSort on current node.
        topSort(node, depArray, stack, original);
      }
    }
    int siz = stack.size();
    for (int elemeno = 1; elemeno <= siz; elemeno++) {
      Long nid = stack.pop();
      ActivityNode noNoNode = retrieveNodeReference(nid);
      sortedList.remove(siz - elemeno);
      sortedList.add(siz - elemeno, noNoNode);

    }
    nodeList = sortedList;
  }

  /**
   * Sort the dependencies given recursively.
   *
   * @param node      Current working node.
   * @param dependArr Dependencies associated with this node.
   * @param topStack  Stack to sort nodes into.
   * @param original  Original, unmodified network.
   */
  private void topSort(ActivityNode node, Long[] dependArr, Stack<Long> topStack, ArrayList<ActivityNode> original) {
    // Check all dependencies in array.
    for (Long newId : dependArr) {
      // Create/get new node from dependency list.
      ActivityNode newNode = retrieveNodeReference(newId);

      if (newNode.getDependencies().size() != 0 && original.contains(newNode)) {
        Set<Long> newDepend = newNode.getDependencies();
        Long[] newDepArr = newDepend.toArray(new Long[newDepend.size()]);
        topSort(newNode, newDepArr, topStack, original);
      } else {
        // newNode doesn't have dependencies.
        if (original.contains(newNode)) {
          // New node hasn't been pushed yet.
          topStack.push(newNode.getNodeId());
          original.remove(newNode);
        }
      }
    }
    topStack.push(node.getNodeId());
    original.remove(node);
  }

  /**
   * Constructor. Assigns the network ID, and the starting values for node list, start node ID, and deadline.
   *
   * @param networkId Unique (with respect to list of networks) identifier for this specific network.
   */
  public ActivityNetwork(long networkId, String networkName) {
    this.networkId = networkId;
    this.networkName = networkName;
    this.nodeList = new ArrayList<>();
    this.hoursDeadline = 0;
    this.endDuration = 0;
    this.critPathIds = new ArrayList<>();
  }

  /**
   * Return the network as a string of node names.
   *
   * @return String of the node names.
   */
  @Override
  public String toString() {
    StringBuilder networkString = new StringBuilder("| ");

    for (ActivityNode n : nodeList) {
      networkString.append(n.getName()).append(" | ");
    }
    return networkString.toString();
  }

  /**
   * Cloning method, using for creating a new instance of the current network.
   */
  ActivityNetwork twin() {
    ActivityNetwork a = new ActivityNetwork(this.getNetworkId(), this.getNetworkName());
    for (ActivityNode n : this.getNodeList()) {
      a.insertNode(n);
    }

    a.hoursDeadline = this.hoursDeadline;
    return a;
  }

  /**
   * Check if a given node is in the network using the node's ID.
   *
   * @param nodeId ID of the node to determine existence of.
   * @return True if the node exists in the network. False otherwise.
   */
  boolean isNodeInNetwork(long nodeId) {
    for (ActivityNode n : this.nodeList) {
      if (n.getNodeId() == nodeId) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if a given node is in the network using the node's name.
   *
   * @param nodeName Name of the node to determine existence of.
   * @return True if the node exists in the network. False otherwise.
   */
  public boolean isNodeInNetwork(String nodeName) {
    for (ActivityNode n : this.nodeList) {
      if (n.getName().equals(nodeName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Insert the given node into node list. Checks for node ID andm name uniqueness.
   *
   * @param node Node to insert into the node list network.
   * @return True if the insertion was successful. False otherwise.
   */
  public boolean insertNode(ActivityNode node) {
    if (isNodeInNetwork(node.getNodeId()) || isNodeInNetwork(node.getName())) {
      return false;
    }

    // If node is unique, insert and sort the list.
    nodeList.add(node);
    sortNodes(nodeList);

    return true;
  }

  /**
   * Get the node ID associated with the node of the given name.
   *
   * @param nodeName Name of the node to search for.
   * @return -1 if a node does not exist with that name. Otherwise, the node ID of the node with the given name.
   */
  public long nodeIdFromName(String nodeName) {
    for (ActivityNode n : this.nodeList) {
      if (n.getName().equals(nodeName)) {
        return n.getNodeId();
      }
    }

    return -1;
  }

  /**
   * Get the node name associated with the node of the given ID.
   *
   * @param nodeID ID of the node to search for.
   * @return Empty string if a node does not exist with that name. Otherwise, the node name of the node with the
   * given ID.
   */
  public String nodeNameFromId(long nodeID) {
    for (ActivityNode n :  this.nodeList) {
      if (n.getNodeId() == nodeID) {
        return n.getName();
      }
    }

    return "";
  }

  /**
   * Delete the node in the network with the given node ID. Checks for node existence.
   *
   * @param nodeId ID of the node to delete.
   * @return True if the node existed in the network. False otherwise.
   */
  public boolean deleteNode(final long nodeId) {
    if (!isNodeInNetwork(nodeId)) {
      return false;
    }

    // If node exists in network, delete from the node list.
    nodeList.removeIf(n -> n.getNodeId() == nodeId);

    // Iterate through node list and purge this dependency. Resort the list.
    for (ActivityNode n : nodeList) {
      n.setDependencies(n.getDependencies().stream().filter(d -> d != nodeId).collect(Collectors.toSet()));
    }
    sortNodes(nodeList);

    return true;
  }

  /**
   * Return a **clone** of the node given the node ID. Checks for node existence.
   *
   * @param nodeId ID of the node to retrieve.
   * @return Node object corresponding to the given node ID. Otherwise, return an empty node with a node ID of -1.
   */
  public ActivityNode retrieveNode(long nodeId) {
    for (ActivityNode n : this.nodeList) {
      if (nodeId == n.getNodeId()) {
        return n.twin();
      }
    }

    // Node is not in list. Return an empty node with blank fields and zero fields.
    return new ActivityNode(-1, "", "", 0, 0, 0);
  }

  /**
   * Return a reference of the node given the node ID. Checks for node existence.
   *
   * @param nodeId ID of the node to retrieve.
   * @return Node object corresponding to the given node ID. Otherwise, return an empty node with a node ID of -1.
   */
  private ActivityNode retrieveNodeReference(long nodeId) {
    for (ActivityNode n : this.nodeList) {
      if (nodeId == n.getNodeId()) {
        return n;
      }
    }

    // Node is not in list. Return an empty node with blank fields and zero fields.
    return new ActivityNode(-1, "", "", 0, 0, 0);
  }

  /**
   * Set the dependencies of the node with the given ID. Verify that each dependency exists in the network.
   *
   * @param nodeId       ID of the node to set the dependencies of.
   * @param dependencies The new node's dependencies.
   * @return True if all dependencies in D exist in the network. False otherwise.
   */
  boolean setDependencies(long nodeId, Set<Long> dependencies) {
    // Verify that the dependencies actually exist.
    for (Long d : dependencies) {
      if (!isNodeInNetwork(d)) {
        return false;
      }
    }

    // If they do exist, set the given node's dependencies. Resort our node list.
    retrieveNodeReference(nodeId).setDependencies(dependencies);
    sortNodes(nodeList);
    return true;
  }

  /**
   * Compute the sum of the critical path times. This represents the minimum time a project requires to completion.
   *
   * @return The sum of the critical path times in hours.
   */
  public double computeCriticalPathTime() {
    List<Double> eta = computeCriticalPath().stream().map(n_i ->
        retrieveNodeReference(n_i).getTimes()[3]).collect(Collectors.toList());

    return eta.stream().reduce((double) 0, (n_1, n_2) -> (n_1 + n_2));
  }

  /**
   * Mutator method for the deadline field. This value must not be less than the sum of the critical path times.
   *
   * @param hoursDeadline Desired deadline in hours.
   * @return True if hoursDeadline was changed. False if the value is less than the sum of the critical path times.
   */
  public boolean setHoursDeadline(double hoursDeadline) {
    if (hoursDeadline < computeCriticalPathTime()) {
      return false;
    } else {
      this.hoursDeadline = hoursDeadline;
      return true;
    }
  }

  /**
   * Mutator method for the network name field.
   *
   * @param networkName New name of the network.
   */
  void setNetworkName(String networkName) {
    this.networkName = networkName;
  }

  /**
   * Compute the earliest time the node with the given ID can finish.
   *
   * @param nodeId ID of the node to compute the EF of.
   * @return The earliest finish time of the given node.
   */
  @SuppressWarnings("Duplicates")
  private double computeEarliestFinishTime(long nodeId) {
    double finish = 0;
    double max = 0;

    // If first node, then earliest finish is the task duration.
    if (nodeId == getStartNodeId()) {
      double[] nodeTimes = nodeList.get(0).getTimes();
      return nodeTimes[3];

    } else {
      int i = 0;
      while (retrieveNode(nodeId) != nodeList.get(i)) {

        // Get times of node at index i of nodeList.
        double[] nodeTimes = nodeList.get(i).getTimes();

        // Grabs 4th position (expected Time) and adds to the previous start time.
        finish = finish + nodeTimes[3];
        if (finish > max) {
          max = finish;
        }
        i++;
      }
      double[] time = retrieveNode(nodeId).getTimes();
      return max + time[3];
    }

  }

  /**
   * Compute the latest time the node with the given ID can finish.
   *
   * @param nodeId ID of the node to compute the LF of.
   * @return The latest finish time of the given node.
   */
  @SuppressWarnings("Duplicates")
  private double computeLatestFinishTime(long nodeId) {

    double finish = computeEarliestFinishTime(nodeId);
    double min = finish;

    // Compares param of ActivityNode to last node in nodelist.
    if (retrieveNode(nodeId) == nodeList.get(nodeList.size())) {
      // If node is last node then latest finish time is early finish time.
      return finish;

    } else {
      int i = 1;

      // Starts at end of list and works towards front.
      while (retrieveNode(nodeId) != nodeList.get(nodeList.size() - i)) {
        double[] nodeTimes = nodeList.get(nodeList.size() - i).getTimes();
        finish = finish - nodeTimes[3];
        if (finish < min) {
          min = finish;
        }
        i++;
      }
      return min;
    }
  }

  /**
   * Compute the earliest time a node with the given ID can start.
   *
   * @param nodeId ID of the node to compute the ES of.
   * @return The earliest start time of the given node.
   */
  @SuppressWarnings("Duplicates")
  private double computeEarliestStartTime(long nodeId) {

    double start = 0;
    double max = 0;

    if (nodeId == getStartNodeId()) {
      start = 0;
      return start;
    } else {
      int i = 0;

      // Get times of node at index i of nodeList.
      while (retrieveNode(nodeId) != nodeList.get(i)) {
        double[] nodeTimes = nodeList.get(i).getTimes();

        // Grabs 4th position (expected Time) and adds to the previous start time.
        start = start + nodeTimes[3];
        if (start > max) {
          max = start;
        }
        i++;
      }
      return max;
    }
  }

  /**
   * Compute the latest time a node with the given ID can start.
   *
   * @param nodeId ID of the node to compute the LS of.
   * @return The latest start time of the given node.
   */
  @SuppressWarnings("Duplicates")
  private double computeLatestStartTime(long nodeId) {

    double finish = computeEarliestFinishTime(nodeId);
    double min = finish;

    // Compares param of ActivityNode to last node in nodelist.
    if (retrieveNode(nodeId) == nodeList.get(nodeList.size())) {
      return finish - retrieveNode(nodeId).getTimes()[3];
    } else {
      int i = 1;

      // Starts at end of list and works to the front.
      while (retrieveNode(nodeId) != nodeList.get(nodeList.size() - i)) {
        double[] nodeTimes = nodeList.get(nodeList.size() - i).getTimes();
        finish = finish - nodeTimes[3];
        if (finish < min) {
          min = finish;
        }
        i++;
      }
      return min - retrieveNode(nodeId).getTimes()[3];
    }
  }

  /**
   * Find all predecessors for the node matching the given node ID.
   *
   * @param nodeId ID of the node to find predecessors for.
   * @return All predecessors for the the given node.
   */
  private ArrayList<ActivityNode> findPredecessors(long nodeId) {
    ArrayList<ActivityNode> nodesBefore = new ArrayList<>();
    int index;

    // Implies that if the function node is first then no predecessors exist and therefore nothing happens.
    if (nodeList.get(0) != retrieveNode(nodeId)) {
      for (index = 0; nodeList.get(index) != retrieveNode(nodeId); index++) {
        nodesBefore.add(retrieveNode(nodeId));
      }

    }
    return nodesBefore;
  }

  /**
   * Find all successors for the node matching the given node ID.
   *
   * @param nodeId ID of the node to find successors for.
   * @return All successors for the given node.
   */
  private ArrayList<ActivityNode> findSuccessors(long nodeId) {

    ArrayList<ActivityNode> nodesAfter = new ArrayList<>();

    if (nodeList.get(nodeList.size() - 1) != retrieveNode(nodeId)) {//implies that if the function node is last then no successors exist and therefore nothing happens
      for (int index = 1; nodeList.get(nodeList.size() - index) != retrieveNode(nodeId); index++) {
        nodesAfter.add(retrieveNode(nodeId));
      }
    }
    return nodesAfter;
  }

  /**
   * Compute the total slack given the node ID of a node in the network. The user here **MUST** check for node existence
   * before using this method. Using the method given here http://www.pmknowledgecenter
   * .com/dynamic_scheduling/baseline/activity-slack-total-safety-and-free-slack-definitions
   *
   * @param nodeId ID of the node to compute the total slack for.
   * @return The total slack of the node with the given ID.
   */
  public double computeTotalSlack(long nodeId) {
    if (!isNodeInNetwork(nodeId)) {
      throw new java.lang.RuntimeException("Node does not exist in network.");
    } else {
      return computeLatestFinishTime(nodeId) - computeEarliestFinishTime(nodeId);
    }
  }

  /**
   * Compute the safety slack given the node ID of a node in the network. The user here **MUST** check for node
   * existence before using this method. Using the method given here: http://www.pmknowledgecenter
   * .com/dynamic_scheduling/baseline/activity-slack-total-safety-and-free-slack-definitions
   *
   * @param nodeId ID of the node to compute the safety slack for.
   * @return The safety slack of the node with the given ID.
   */
  public double computeSafetySlack(long nodeId) {
    if (!isNodeInNetwork(nodeId)) {
      throw new java.lang.RuntimeException("Node does not exist in network.");
    } else {
      List<Double> eta = findPredecessors(nodeId).stream().map(n_i ->
          computeLatestFinishTime(n_i.getNodeId())).collect(Collectors.toList());

      return computeLatestStartTime(nodeId) - eta.stream().reduce((double) 0, Double::max);
    }
  }

  /**
   * Compute the free slack given the node ID of a node in the network. The user here **MUST** check for node
   * existence before using this method. Using the method given here: http://www.pmknowledgecenter
   * .com/dynamic_scheduling/baseline/activity-slack-total-safety-and-free-slack-definitions
   *
   * @param nodeId ID of the node to compute the free slack for.
   * @return The free slack of the node with the given ID.
   */
  public double computeFreeSlack(long nodeId) {
    if (!isNodeInNetwork(nodeId)) {
      throw new java.lang.RuntimeException("Node does not exist in network.");
    } else {
      List<Double> eta = findSuccessors(nodeId).stream().map(n_i ->
          computeEarliestStartTime(n_i.getNodeId())).collect(Collectors.toList());

      return eta.stream().reduce((double) 0, Double::min) - computeEarliestFinishTime(nodeId);
    }
  }

/**
 * Compute the critical path of the current network, and return a sorted array of node IDs that represent this path.
 *
 * @return An array of node IDs that pertain to this network, which represent the current critical path.
 */
public ArrayList<Long> computeCriticalPath() {
    // Return an empty list if our network is empty.
    if (nodeList.isEmpty()) {
      return new ArrayList<>();
    }

    endDuration = 0;
    ArrayList<Long> startList = new ArrayList<>();
    ArrayList<ActivityNode> reverseList = new ArrayList<>();
    for(int i = 1;i<=nodeList.size();i++) {
    	reverseList.add(nodeList.get(nodeList.size()-i));
    }

    // Begin the recursion.
    recursionCritPath(0, 0, startList,reverseList);

    return critPathIds;
  }

  /**
   * Recursively determine the critical path.
   *
   * @param index        Current index of dependency node followed.
   * @param nodeDuration Total duration of all previous nodes visited.
   * @param nodeIds      List of all dependencies visited
   */
  private void recursionCritPath(int index, double nodeDuration, ArrayList<Long> nodeIds,ArrayList<ActivityNode> reverseList) {
    int depIndex = 0; // Keep track of where the dependency node is located in nodeList.
    ArrayList<Long> idList = new ArrayList<>(nodeIds); // First idList values = first node located in nodeList.

    // Node has dependencies to follow.
    if (reverseList.get(index).getDependencies().size() != 0) {
      // Grab dependencies of node and convert it to arrayList.
      Set<Long> depSet = reverseList.get(index).getDependencies();
      ArrayList<Long> depList = new ArrayList<>(depSet);

      // Grab dependency ID and locate the index of where dependency is located in nodeList.
      for (Long aDepList : depList) {
        long id = aDepList;
        while (retrieveNode(id) != reverseList.get(depIndex)) {
          depIndex++;
        }

        // Deposit dependency to list to keep track of unique path.
        idList.add(id);
        double duration = reverseList.get(index).getTimes()[3];

        // Track duration of unique path.
        double total = duration + nodeDuration;
        recursionCritPath(depIndex, total, idList,reverseList);

        // Starts index back to the front of list on next iteration.
        depIndex = 0;
      }

    } else {
      // If node has no dependencies then node is last node.

      // Add last node ID to list and grab duration of last node.
      idList.add(reverseList.get(index).getNodeId());
      double duration = reverseList.get(index).getTimes()[3];

      // Total duration time of specific path.
      double total = duration + nodeDuration;
      if (total > endDuration) {
        endDuration = total;
        critPathIds.clear();
        critPathIds.addAll(idList);
      }
    }
  }

  /**
   * Accessor method for the network's starting node ID.
   *
   * @return If the network is empty, return -1. Otherwise, the network's starting node ID.
   */
  long getStartNodeId() {
    return nodeList.isEmpty() ? -1 : nodeList.get(0).getNodeId();
  }

  /**
   * Accessor method for the network ID.
   *
   * @return The network ID.
   */
  public long getNetworkId() {
    return networkId;
  }

  /**
   * Accessor method for the node list.
   *
   * @return The list of nodes, in it's current order.
   */
  public ArrayList<ActivityNode> getNodeList() {
    return nodeList;
  }

  /**
   * Accessor method for the network name.
   *
   * @return The name assigned to this network.
   */
  public String getNetworkName() {
    return networkName;
  }

  /**
   * Accessor method fo the network deadline.
   *
   * @return The network deadline.
   */
  public double getHoursDeadline() {
    return hoursDeadline;
  }
}
