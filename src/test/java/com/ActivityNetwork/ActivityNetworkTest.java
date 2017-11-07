package com.ActivityNetwork;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.*;

import static org.junit.Assert.*;

public class ActivityNetworkTest {
  /**
   * Verify that networks can be cloned correctly, and that they can be modified separately.
   */
  @Test
  public void testNetworkCloning() {
    ActivityNetwork a = new ActivityNetwork(123, "Sample Project");
    ActivityNetwork b = a.clone();

    assertEquals(a.getNetworkId(), b.getNetworkId());
    a.insertNode(new ActivityNode(0, "Working Wings", "Wings are working", 5, 10, 15));

    assertEquals(1, a.getNodeList().size());
    assertEquals(0, b.getNodeList().size());
  }

  /**
   * Verify that the nodes are inserted if and only if their node IDs are unique.
   */
  @Test
  public void testNodeInsertion() {
    ActivityNetwork n = new ActivityNetwork(0, "Sample Project");
    assertEquals(0, n.getNodeList().size());

    assertEquals(true, n.insertNode(new ActivityNode(0, "Working Wings", "Wings are working", 5, 10, 15)));
    assertEquals(1, n.getNodeList().size());
    assertEquals(0, n.getNodeList().get(0).getNodeId());

    assertEquals(false, n.insertNode(new ActivityNode(0, "Shouldn't Be Joined", "Blah", 5, 10, 15)));
    assertEquals(1, n.getNodeList().size());
    assertEquals(0, n.getNodeList().get(0).getNodeId());
  }

  /**
   * Verify that nodes are deleted correctly, and that repeating the exact same operation (deletion on a node that
   * doesn't exist) does not modify the list further.
   */
  @Test
  public void testNodeTrivialDeletion() {
    ActivityNetwork n = new ActivityNetwork(0, "Sample Project");

    assertEquals(true, n.insertNode(new ActivityNode(0, "Working Wings", "Wings are working", 5, 10, 15)));
    assertEquals(1, n.getNodeList().size());

    assertEquals(true, n.deleteNode(0));
    assertEquals(0, n.getNodeList().size());

    assertEquals(false, n.deleteNode(0));
    assertEquals(0, n.getNodeList().size());
  }

  /**
   * Verify that after adding a node, our node existence method will return true.
   */
  @Test
  public void testNodeExistence() {
    ActivityNetwork n = new ActivityNetwork(0, "Sample Project");

    assertEquals(true, n.insertNode(new ActivityNode(0, "Working Wings", "Wings are working", 5, 10, 15)));
    assertEquals(true, n.isNodeInNetwork(0));
  }

  /**
   * Verify that dependency setting will not occur if the given node does not exist in the network.
   */
  @Test
  public void testBadDependencyList() {
    ActivityNetwork n = new ActivityNetwork(0, "Sample Project");

    n.insertNode(new ActivityNode(9, "Working Wings", "Wings are working", 5, 10, 15));
    n.insertNode(new ActivityNode(7, "Working Head", "Head is working", 10, 15, 16));
    n.insertNode(new ActivityNode(3, "Working Legs", "Legs are working", 12, 60, 80));

    assertEquals(true, n.setDependencies(7, new HashSet<>(Arrays.asList((long) 3))));
    assertEquals(true, n.retrieveNode(7).getDependencies().contains((long) 3));

    assertEquals(false, n.setDependencies(7, new HashSet<>(Arrays.asList((long) 200))));
    assertEquals(false, n.retrieveNode(7).getDependencies().contains((long) 200));
  }

  /**
   * Verify that the correct node is returned when requested, as well as an empty node being returned when the given
   * node ID does not correspond to any node in the current list.
   */
  @Test
  public void testNodeRetrieval() {
    ActivityNetwork n = new ActivityNetwork(0, "Sample Project");

    assertEquals(true, n.insertNode(new ActivityNode(0, "Working Wings", "Wings are working", 5, 10, 15)));
    assertEquals(0, n.retrieveNode(0).getNodeId());
    assertEquals("Working Wings", n.retrieveNode(0).getName());
  }

  /**
   * Verify that the nodes are sorted correctly after insertion and deletion. The history of the expected network is
   * below.
   *
   * (Nodes Without Dependencies)
   * t = 0: 9 ----------- 7 ----------- 3
   *        (index 0) --- (index 1) ---(index 2)
   *
   * .
   * .
   * .
   *
   * (After Setting Dependencies)
   * t = 2: 9 ----------> 3 ----------> 7
   *        (index 0) --> (index 1) --> (index 2)
   *
   * (Deletion of Node 3)
   * t = 3: 9 ----------> 7
   *        (index 0) --> (index 1)
   */
  @Test
  public void testNodesSortedAfterModification() {
    ActivityNetwork n = new ActivityNetwork(0, "Sample Project");

    n.insertNode(new ActivityNode(9, "Working Wings", "Wings are working", 5, 10, 15));
    n.insertNode(new ActivityNode(7, "Working Head", "Head is working", 10, 15, 16));
    n.insertNode(new ActivityNode(3, "Working Legs", "Legs are working", 12, 60, 80));

    // Each insertion triggers a sort, but without dependencies the sort should be stable.
    assertEquals(9, n.getStartNodeId());
    assertEquals(7, n.getNodeList().get(1).getNodeId());
    assertEquals(3, n.getNodeList().get(2).getNodeId());

    // 7 depends on 3, 3 depends on 9, 9 is start node.
    assertEquals(true, n.setDependencies(7, new HashSet<>(Arrays.asList((long) 3))));
    assertEquals(true, n.setDependencies(3, new HashSet<>(Arrays.asList((long) 9))));

    // By setting our dependencies, we change the order (because it sorts inside here).
    assertEquals(9, n.getStartNodeId());
    assertEquals(3, n.getNodeList().get(1).getNodeId());
    assertEquals(7, n.getNodeList().get(2).getNodeId());

    // 7 now depends on 9, 9 is start node.
    assertEquals(true, n.deleteNode(3));

    // Deletion also triggers another sort.
    assertEquals(9, n.getStartNodeId());
    assertEquals(7, n.getNodeList().get(1).getNodeId());
  }
}