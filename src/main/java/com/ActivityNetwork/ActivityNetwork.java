///
/// This file contains the ActivityNetwork class, which represents a network of nodes in the Critical Path Method.
///

package com.ActivityNetwork;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ActivityNetwork {
  /// Unique identifier for this specific network.
  private long networkId;

  /// List of nodes that belong to this network.
  private ArrayList<ActivityNode> nodeList;

  /// Node ID of the starting node of this network.
  private long startNodeId;

  /**
   * Sort the current list of nodes by order of dependencies (topological sort).
   */
  private void sortNodes() {
    // TODO: finish this
  }

  /**
   * Constructor. Assigns the network ID, and the starting values for node list and start node ID.
   *
   * @param networkId Unique (with respect to list of networks) identifier for this specific network.
   */
  public ActivityNetwork(long networkId) {
    this.networkId = networkId;
    this.nodeList = new ArrayList<>();
    this.startNodeId = 0;
  }

  /**
   * Cloning method, using for creating a new instance of the current network.
   */
  public ActivityNetwork clone() {
    ActivityNetwork a = new ActivityNetwork(this.getNetworkId());
    for (ActivityNode n : this.getNodeList()) {
      a.insertNode(n);
    }

    return a;
  }

  /**
   * Check if a given node is in the network using the node's ID.
   *
   * @param nodeId ID of the node to determine existence of.
   * @return True if the node exists in the network. False otherwise.
   */
  public boolean isNodeInNetwork(long nodeId) {
    for (ActivityNode n : this.nodeList) {
      if (n.getNodeId() == nodeId) {
        return true;
      }
    }
    return false;
  }

  /**
   * Insert the given node into node list. Checks for node ID uniqueness.
   *
   * @param node Node to insert into the node list network.
   * @return True if the insertion was successful. False otherwise.
   */
  public boolean insertNode(ActivityNode node) {
    if (isNodeInNetwork(node.getNodeId())) {
      return false;
    }

    // If node is unique, insert and sort the list.
    nodeList.add(node);
    sortNodes();

    return true;
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
    sortNodes();

    return true;
  }

  /**
   * Return the node given the node ID. Checks for node existence.
   *
   * @param nodeId ID of the node to retrieve.
   * @return Node object corresponding to the given node ID. Otherwise, return an empty node with a node ID of -1.
   */
  public ActivityNode retrieveNode(long nodeId) {
    for (ActivityNode n : this.nodeList) {
      if (nodeId == n.getNodeId()) {
        return n;
      }
    }

    // Node is not in list. Return an empty node with blank fields and zero fields.
    return new ActivityNode(-1, "", "", 0, 0, 0);
  }

  /**
   * Compute the total slack given the node ID of a node in the network. The user here **MUST** check for node existence
   * before using this method.
   *
   * @param nodeId ID of the node to compute the total slack for.
   * @return The total slack of the node with the given ID.
   */
  public double computeTotalSlack(int nodeId) {
    if (!isNodeInNetwork(nodeId)) {
      throw new java.lang.RuntimeException("Node does not exist in network.");
    } else {
      // TODO: finish this calculation: http://www.pmknowledgecenter.com/dynamic_scheduling/baseline/activity-slack-total-safety-and-free-slack-definitions
      return 0;
    }
  }

  /**
   * Compute the safety slack given the node ID of a node in the network. The user here **MUST** check for node
   * existence before using this method.
   *
   * @param nodeId ID of the node to compute the safety slack for.
   * @return The safety slack of the node with the given ID.
   */
  public double computeSafetySlack(int nodeId) {
    if (!isNodeInNetwork(nodeId)) {
      throw new java.lang.RuntimeException("Node does not exist in network.");
    } else {
      // TODO: finish this calculation: http://www.pmknowledgecenter.com/dynamic_scheduling/baseline/activity-slack-total-safety-and-free-slack-definitions
      return 0;
    }
  }

  /**
   * Compute the free slack given the node ID of a node in the network. The user here **MUST** check for node
   * existence before using this method.
   *
   * @param nodeId ID of the node to compute the free slack for.
   * @return The free slack of the node with the given ID.
   */
  public double computeFreeSlack(int nodeId) {
    if (!isNodeInNetwork(nodeId)) {
      throw new java.lang.RuntimeException("Node does not exist in network.");
    } else {
      // TODO: finish this calculation: http://www.pmknowledgecenter.com/dynamic_scheduling/baseline/activity-slack-total-safety-and-free-slack-definitions
      return 0;
    }
  }

  /**
   * Compute the critical path of the current network, and return a sorted array of node IDs that represent this path.
   *
   * @return An array of node IDs that pertain to this network, which represent the current critical path.
   */
  public long[] computeCriticalPath() {
    // TODO: finish this
    return new long[0];
  }

  /**
   * Accessor method for the network's starting node ID.
   *
   * @return The network's starting node ID.
   */
  public long getStartNodeId() {
    return startNodeId;
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
}
