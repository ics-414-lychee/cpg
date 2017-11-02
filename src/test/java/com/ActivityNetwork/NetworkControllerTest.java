package com.ActivityNetwork;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class NetworkControllerTest {
  /**
   * Verify that the network retrieval method returns a copy of the correct network if the node ID exists, otherwise
   * an empty network is returned.
   */
  @Test
  public void testRetrieveNetwork() {
    NetworkController nc = new NetworkController();
    ArrayList<Long> networkIDList = new ArrayList<>(Arrays.asList(nc.createNetwork()));

    for (int i = 0; i < 10; i++) {
      networkIDList.add(nc.createNetwork());
    }

    ActivityNetwork a = nc.retrieveNetwork(networkIDList.get(0));
    assertEquals((long) networkIDList.get(0), a.getNetworkId());

    a.insertNode(new ActivityNode(0, "Working Wings", "Wings are working", 5, 10, 15));
    assertEquals(0, nc.retrieveNetwork(networkIDList.get(0)).getNodeList().size());

    assertEquals(0, nc.retrieveNetwork(-1).getNetworkId());
  }

  /**
   * Verify that the chain adding method adds links as intended, and performs FIFO when we reach our defined limit.
   */
  @Test
  public void testAddChainLink() {
    NetworkController nc = new NetworkController(100);
    ArrayList<Long> networkIDList = new ArrayList<>(Arrays.asList(nc.createNetwork()));

    for (int i = 0; i < 100; i++) {
      networkIDList.add(nc.createNetwork());
    }
    assertEquals((long) networkIDList.get(0), nc.retrieveNetwork(networkIDList.get(0)).getNetworkId());
    assertEquals((long) networkIDList.get(1), nc.retrieveNetwork(networkIDList.get(1)).getNetworkId());

    networkIDList.add(nc.createNetwork());
    assertEquals(0, nc.retrieveNetwork(networkIDList.get(0)).getNetworkId());
    assertEquals((long) networkIDList.get(1), nc.retrieveNetwork(networkIDList.get(1)).getNetworkId());
  }

  /**
   * Verify that the network modification method only works with networks that are currently in the network chain.
   */
  @Test
  public void testModifyNetwork() {
    NetworkController nc = new NetworkController(100);
    ArrayList<Long> networkIDList = new ArrayList<>(Arrays.asList(nc.createNetwork()));

    for (int i = 0; i < 5; i++) {
      networkIDList.add(nc.createNetwork());
    }
    assertEquals(0, nc.retrieveNetwork(networkIDList.get(0)).getNodeList().size());

    ActivityNetwork a = nc.retrieveNetwork(networkIDList.get(0));
    a.insertNode(new ActivityNode(0, "Working Wings", "Wings are working", 5, 10, 15));
    assertEquals(true, nc.modifyNetwork(a));
    assertEquals(1, nc.retrieveNetwork(networkIDList.get(0)).getNodeList().size());

    ActivityNetwork b = new ActivityNetwork(123);
    assertEquals(false, nc.modifyNetwork(b));
  }

  /**
   * Verify that the network 'undo' method only works with networks that are currently in the network chain, and that it
   * actually removes the node.
   */
  @Test
  public void testUndoNetwork() {
    NetworkController nc = new NetworkController(100);
    ArrayList<Long> networkIDList = new ArrayList<>(Arrays.asList(nc.createNetwork()));

    for (int i = 0; i < 5; i++) {
      networkIDList.add(nc.createNetwork());
    }
    assertEquals(0, nc.retrieveNetwork(networkIDList.get(0)).getNodeList().size());

    ActivityNetwork a = nc.retrieveNetwork(networkIDList.get(0));
    a.insertNode(new ActivityNode(0, "Working Wings", "Wings are working", 5, 10, 15));
    assertEquals(true, nc.modifyNetwork(a));
    assertEquals(1, nc.retrieveNetwork(networkIDList.get(0)).getNodeList().size());

    assertEquals(true, nc.undoNetworkChange(networkIDList.get(0)));
    assertEquals(0, nc.retrieveNetwork(networkIDList.get(0)).getNodeList().size());

    assertEquals(true, nc.undoNetworkChange(networkIDList.get(0)));
    assertEquals(0, nc.retrieveNetwork(networkIDList.get(0)).getNetworkId());
    assertEquals(false, nc.undoNetworkChange(networkIDList.get(0)));
  }

  /**
   * Verify that the timestamp associated with the retrieved node changes upon modification.
   */
  @Test
  public void testTimestampRetrieval() {
    NetworkController nc = new NetworkController(100);
    ArrayList<Long> networkIDList = new ArrayList<>(Arrays.asList(nc.createNetwork()));
    long t = nc.retrieveTimestamp(networkIDList.get(0));

    ActivityNetwork a = nc.retrieveNetwork(networkIDList.get(0));
    a.insertNode(new ActivityNode(0, "Working Wings", "Wings are working", 5, 10, 15));
    assertEquals(true, nc.modifyNetwork(a));

    assertNotSame(t, nc.retrieveNetwork(networkIDList.get(0)));
  }
}