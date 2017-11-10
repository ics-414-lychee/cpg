package project;

///
/// This file contains the ActivityNetwork class, which represents a network of nodes in the Critical Path Method.
///



import java.util.ArrayList;
import java.util.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ActivityNetwork {
/// Unique identifier for this specific network.
private long networkId;


private ActivityNode activityNode;


/// List of nodes that belong to this network.
private ArrayList<ActivityNode> nodeList;

/// Node ID of the starting node of this network.
private long startNodeId;

/// The deadline associated with network. Cannot be less than the minimal critical path sum (length).
private long hoursDeadline;






/**
 * Sort the current list of nodes by order of dependencies (topological sort).
 */

private void sortNodes(ArrayList<ActivityNode> listOfNodes) {
	Stack<Long> stack = new Stack<>();//stack to deposit id's
	ArrayList<ActivityNode> sortedList = new ArrayList<>(nodeList);
	ArrayList<ActivityNode> original = new ArrayList<>(nodeList);//using an original list to check if node has already been pushed
	for (int i=0; i<listOfNodes.size(); i++) {  
		ActivityNode node = listOfNodes.get(i);
		
		  if(!original.contains(node)) {
			  //dibt do nothing
		  }
		  else if(node.getDependencies().size()==0) {
			  stack.push(node.getNodeId());
			  original.remove(node);
		  }
		  else {
			  // make dependency array
			  Set<Long> depend = node.getDependencies();
			  Long[] depArray = depend.toArray(new Long[depend.size()]);
			  // call topSort on current node
			  topSort(node,depArray,stack,original);
		  }
	}
	int siz=stack.size();
	for(int elemeno=1;elemeno<=siz;elemeno++) {
		Long nid = stack.pop();
		ActivityNode noNoNode = retrieveNode(nid);
		sortedList.remove(siz-elemeno);
		sortedList.add(siz-elemeno, noNoNode);
	}
	nodeList = sortedList;
}
//sorts dependencies
private void topSort(ActivityNode node, Long[] dependArr, Stack<Long> topStack, ArrayList<ActivityNode> original) {
	// check all dependencies in array
	for(int j=0;j<dependArr.length;j++) {
		// create/get new node from dependency list
		Long newId = dependArr[j];
		ActivityNode newNode = retrieveNode(newId);
		
		if (newNode.getDependencies().size()!=0 && original.contains(newNode)) {
			Set<Long> newDepend = newNode.getDependencies();
			Long[] newDepArr = newDepend.toArray(new Long[newDepend.size()]);
			topSort(newNode,newDepArr,topStack,original);
		}
		else { // newNode doesn't have dependencies
			if(original.contains(newNode)) { // new node hasn't been pushed yet
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
public ActivityNetwork(long networkId) {
  this.networkId = networkId;
  this.nodeList = new ArrayList<>();
  this.startNodeId = 0;
  this.hoursDeadline = 0;
}

/**
 * Cloning method, using for creating a new instance of the current network.
 */
public ActivityNetwork clone() {
  ActivityNetwork a = new ActivityNetwork(this.getNetworkId());
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
  sortNodes(nodeList);

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
  sortNodes(nodeList);

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
 * Compute the sum of the critical path times. This represents the minimum time a project requires to completion.
 *
 * @return The sum of the critical path times in hours.
 */
private double computeCriticalPathTime() {
  List<Double> eta = computeCriticalPath().stream().map(n_i ->
      retrieveNode(n_i).getTimes()[3]).collect(Collectors.toList());

  return eta.stream().reduce((double) 0, (n_1, n_2) -> (n_1 + n_2));
}

/**
 * Mutator method for the deadline field. This value must not be less than the sum of the critical path times.
 *
 * @param hoursDeadline Desired deadline in hours.
 * @return True if hoursDeadline was changed. False if the value is less than the sum of the critical path times.
 */
public boolean setHoursDeadline(long hoursDeadline) {
  if (hoursDeadline < computeCriticalPathTime()) {
    return false;
  } else {
    this.hoursDeadline = hoursDeadline;
    return true;
  }
}

/**
 * Compute the earliest time the node with the given ID can finish.
 *
 * @param nodeId ID of the node to compute the EF of.
 * @return The earliest finish time of the given node.
 */
private double computeEarliestFinishTime(long nodeId) {
  double offset = hoursDeadline - computeCriticalPathTime();
  // TODO: finish EF computation
  return offset;
}

/**
 * Compute the latest time the node with the given ID can finish.
 *
 * @param nodeId ID of the node to compute the LF of.
 * @return The latest finish time of the given node.
 */
private double computeLatestFinishTime(long nodeId) {
  double offset = hoursDeadline - computeCriticalPathTime();
  // TODO: finish LF computation
  return offset;
}

/**
 * Compute the earliest time a node with the given ID can start.
 *
 * @param nodeId ID of the node to compute the ES of.
 * @return The earliest start time of the given node.
 */
private double computeEarliestStartTime(long nodeId) {
  double offset = hoursDeadline - computeCriticalPathTime();
  // TODO: finish ES computation
  return offset;
}

/**
 * Compute the latest time a node with the given ID can start.
 *
 * @param nodeId ID of the node to compute the LS of.
 * @return The latest start time of the given node.
 */
private double computeLatestStartTime(long nodeId) {
  double offset = hoursDeadline - computeCriticalPathTime();
  // TODO: finish LS computation
  return offset;
}

/**
 * Find all predecessors for the node matching the given node ID.
 *
 * @param nodeId ID of the node to find predecessors for.
 * @return All predecessors for the the given node.
 */
private ArrayList<ActivityNode> findPredecessors(long nodeId) {
  // TODO: finish predecessor search
  return new ArrayList<>(Arrays.asList(new ActivityNode(0, "N", "N", 0, 0, 0)));
}

/**
 * Find all successors for the node matching the given node ID.
 *
 * @param nodeId ID of the node to find successors for.
 * @return All successors for the given node.
 */
private ArrayList<ActivityNode> findSuccessors(long nodeId) {
  // TODO: finish successor search
  return new ArrayList<>(Arrays.asList(new ActivityNode(0, "N", "N", 0, 0, 0)));
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
public double computeFreeSlack(int nodeId) {
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
  // TODO: finish critical path computation
  return new ArrayList<>(Arrays.asList((long) 0, (long) 1));
}

/**
 * Accessor method for the network's starting node ID.
 *
 * @return The network's starting node ID.
 */
public long getStartNodeId() {
  return nodeList.get(0).getNodeId();
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
