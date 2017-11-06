package com.ActivityNetwork;

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
   * Verify that the nodes are sorted correctly after insertion and deletion.
   */
  @Test
  public void testNodesSortedAfterModification() {
    ActivityNetwork n = new ActivityNetwork(0, "Sample Project");

    n.insertNode(new ActivityNode(9, "Working Wings", "Wings are working", 5, 10, 15));
    ActivityNode a = new ActivityNode(7, "Working Head", "Head is working", 10, 15, 16);
    ActivityNode b = new ActivityNode(3, "Working Legs", "Legs are working", 12, 60, 80);

    // 7 depends on 3, 3 depends on 9, 9 is start node.
    a.setDependencies(new HashSet<>(Arrays.asList((long) 3)));
    b.setDependencies(new HashSet<>(Arrays.asList((long) 9)));
    n.insertNode(a);
    n.insertNode(b);

    assertEquals(9, n.getStartNodeId());
    assertEquals(7, n.getNodeList().get(2).getNodeId());

    // 7 now depends on 9, 9 is start node.
    n.deleteNode(3);

    assertEquals(9, n.getStartNodeId());
    assertEquals(7, n.getNodeList().get(1).getNodeId());
  }
}