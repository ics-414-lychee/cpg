package com.ActivityNetwork;

import java.util.Collections;
import java.util.HashSet;

import org.junit.*;

import static org.junit.Assert.*;

public class ActivityNetworkTest {
  /** Test network for all tests to operate on. */
  private static ActivityNetwork testNetwork;

  /**
   * Create a test network for all tests to operate on.
   */
  @Before
  public void createTestNetwork() {
    testNetwork = new ActivityNetwork(123, "Sample Project");
  }

  /**
   * Verify that networks can be cloned correctly, and that they can be modified separately.
   */
  @Test
  public void testNetworkCloning() {
    ActivityNetwork testNetwork2 = testNetwork.twin();

    assertEquals(testNetwork.getNetworkId(), testNetwork2.getNetworkId());
    testNetwork.insertNode(new ActivityNode(0, "Working Wings", "Wings are working", 5, 10, 15));

    assertEquals(1, testNetwork.getNodeList().size());
    assertEquals(0, testNetwork2.getNodeList().size());
  }

  /**
   * Verify that the nodes are inserted if and only if their node IDs are unique.
   */
  @Test
  public void testNodeInsertion() {
    assertEquals(0, testNetwork.getNodeList().size());

    assertEquals(true, testNetwork.insertNode(new ActivityNode(0, "Working Wings", "Wings are working", 5, 10, 15)));
    assertEquals(1, testNetwork.getNodeList().size());
    assertEquals(0, testNetwork.getNodeList().get(0).getNodeId());

    assertEquals(false, testNetwork.insertNode(new ActivityNode(0, "Shouldn't Be Joined", "Blah", 5, 10, 15)));
    assertEquals(1, testNetwork.getNodeList().size());
    assertEquals(0, testNetwork.getNodeList().get(0).getNodeId());
  }

  /**
   * Verify that nodes are deleted correctly, and that repeating the exact same operation (deletion on a node that
   * doesn't exist) does not modify the list further.
   */
  @Test
  public void testNodeTrivialDeletion() {
    assertEquals(true, testNetwork.insertNode(new ActivityNode(0, "Working Wings", "Wings are working", 5, 10, 15)));
    assertEquals(1, testNetwork.getNodeList().size());

    assertEquals(true, testNetwork.deleteNode(0));
    assertEquals(0, testNetwork.getNodeList().size());

    assertEquals(false, testNetwork.deleteNode(0));
    assertEquals(0, testNetwork.getNodeList().size());
  }

  /**
   * Verify that after adding a node, our node existence method will return true.
   */
  @Test
  public void testNodeExistence() {
    assertEquals(true, testNetwork.insertNode(new ActivityNode(0, "Working Wings", "Wings are working", 5, 10, 15)));
    assertEquals(true, testNetwork.isNodeInNetwork(0));
  }

  /**
   * Verify that dependency setting will not occur if the given node does not exist in the network.
   */
  @Test
  public void testBadDependencyList() {
    testNetwork.insertNode(new ActivityNode(9, "Working Wings", "Wings are working", 5, 10, 15));
    testNetwork.insertNode(new ActivityNode(7, "Working Head", "Head is working", 10, 15, 16));
    testNetwork.insertNode(new ActivityNode(3, "Working Legs", "Legs are working", 12, 60, 80));

    assertEquals(true, testNetwork.setDependencies(7, new HashSet<>(Collections.singletonList((long) 3))));
    assertEquals(true, testNetwork.retrieveNode(7).getDependencies().contains((long) 3));

    assertEquals(false, testNetwork.setDependencies(7, new HashSet<>(Collections.singletonList((long) 200))));
    assertEquals(false, testNetwork.retrieveNode(7).getDependencies().contains((long) 200));
  }

  /**
   * Verify that the correct node is returned when requested, as well as an empty node being returned when the given
   * node ID does not correspond to any node in the current list.
   */
  @Test
  public void testNodeRetrieval() {
    assertEquals(true, testNetwork.insertNode(new ActivityNode(0, "Working Wings", "Wings are working", 5, 10, 15)));
    assertEquals(0, testNetwork.retrieveNode(0).getNodeId());
    assertEquals("Working Wings", testNetwork.retrieveNode(0).getName());
  }

  /**
   * Verify that the nodes are sorted correctly after insertion and deletion. The history of the expected network is
   * below.
   * <p>
   * (Nodes Without Dependencies)
   * t = 0: 9 ----------- 7 ----------- 3
   * (index 0) --- (index 1) ---(index 2)
   * <p>
   * .
   * .
   * .
   * <p>
   * (After Setting Dependencies)
   * t = 2: 9 ----------> 3 ----------> 7
   * (index 0) --> (index 1) --> (index 2)
   * <p>
   * (Deletion of Node 3)
   * t = 3: 9 ----------> 7
   * (index 0) --> (index 1)
   */
  @Test
  public void testNodesSortedAfterModification() {
    testNetwork.insertNode(new ActivityNode(9, "Working Wings", "Wings are working", 5, 10, 15));
    testNetwork.insertNode(new ActivityNode(7, "Working Head", "Head is working", 10, 15, 16));
    testNetwork.insertNode(new ActivityNode(3, "Working Legs", "Legs are working", 12, 60, 80));

    // Each insertion triggers a sort, but without dependencies the sort should be stable.
    assertEquals(9, testNetwork.getStartNodeId());
    assertEquals(7, testNetwork.getNodeList().get(1).getNodeId());
    assertEquals(3, testNetwork.getNodeList().get(2).getNodeId());

    // 7 depends on 3, 3 depends on 9, 9 is start node.
    assertEquals(true, testNetwork.setDependencies(7, new HashSet<>(Collections.singletonList((long) 3))));
    assertEquals(true, testNetwork.setDependencies(3, new HashSet<>(Collections.singletonList((long) 9))));

    // By setting our dependencies, we change the order (because it sorts inside here).
    assertEquals(9, testNetwork.getStartNodeId());
    assertEquals(3, testNetwork.getNodeList().get(1).getNodeId());
    assertEquals(7, testNetwork.getNodeList().get(2).getNodeId());

    // 7 now depends on 9, 9 is start node.
    assertEquals(true, testNetwork.deleteNode(3));

    // Deletion also triggers another sort.
    assertEquals(9, testNetwork.getStartNodeId());
    assertEquals(7, testNetwork.getNodeList().get(1).getNodeId());
  }
}