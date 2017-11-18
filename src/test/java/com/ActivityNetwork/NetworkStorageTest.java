package com.ActivityNetwork;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.UUID;

import com.Interface.UserAccount;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class NetworkStorageTest {
  /** User information retrieved upon login. */
  private ArrayList<String> userInfo;

  /** Password for the current user information. */
  private String password;

  /**
   * Create an account, and login to this account for each test to use.
   */
  @Before
  public void createAccount() {
    String randomUsername = UUID.randomUUID().toString();
    password = UUID.randomUUID().toString();

    UserAccount.createAccount(randomUsername, password);
    userInfo = UserAccount.verifyLoginInfo(randomUsername, password);
  }

  /**
   * Verify that networks can be created from our backend correctly.
   */
  @Test
  public void testNetworkCreation() {
    NetworkController nc = new NetworkController(userInfo.get(0), userInfo.get(1), userInfo.get(2));

    assertNotSame(0, nc.createNetwork("Project Creation"));
  }

  /**
   * Verify that networks can be retrieved properly.
   */
  @Test
  public void testNetworkRetrieval() {
    NetworkController nc = new NetworkController(userInfo.get(0), userInfo.get(1), userInfo.get(2));
    long networkID = nc.createNetwork("Project Retrieval");
    nc.storeNetwork(networkID);

    ArrayList<String> sameUserInfo = UserAccount.verifyLoginInfo(userInfo.get(0), password);
    NetworkController nc2 = new NetworkController(sameUserInfo.get(0), sameUserInfo.get(1), sameUserInfo.get(2));

    ActivityNetwork a = nc2.retrieveNetwork(networkID);
    assertEquals(networkID, a.getNetworkId());
    assertEquals(0, a.getHoursDeadline(), Math.ulp(a.getHoursDeadline()));
    assertEquals("Project Retrieval", a.getNetworkName());
    assertEquals(-1, a.getStartNodeId());
  }

  /**
   * Verify that networks can be deleted properly.
   */
  @Test
  public void testNetworkDeletion() {
    NetworkController nc = new NetworkController(userInfo.get(0), userInfo.get(1), userInfo.get(2));
    long networkID = nc.createNetwork("Project Deletion");

    ActivityNetwork a = nc.retrieveNetwork(networkID);
    assertEquals(networkID, a.getNetworkId());

    assertEquals(true, nc.deleteNetwork(networkID));
    assertEquals("Bad", nc.retrieveNetwork(networkID).getNetworkName());
  }

  /**
   * Verify that networks can be saved properly.
   */
  @Test
  public void testNetworkStorage() {
    NetworkController nc = new NetworkController(userInfo.get(0), userInfo.get(1), userInfo.get(2));
    long networkID = nc.createNetwork("Project Storage");

    ActivityNetwork a = nc.retrieveNetwork(networkID);
    a.insertNode(new ActivityNode(1, "Test Node", "Test Description", 5, 10, 15));
    a.setHoursDeadline(20);

    assertTrue(nc.modifyNetwork(a));
    assertTrue(nc.storeNetwork(networkID));

    ActivityNetwork b = nc.retrieveNetwork(networkID);
    assertEquals(networkID, b.getNetworkId());
    assertEquals(20, b.getHoursDeadline(), Math.ulp(b.getHoursDeadline()));
    assertEquals("Project Storage", a.getNetworkName());
    assertEquals(1, a.getStartNodeId());
  }
}