package com.Interface;

import com.ActivityNetwork.NetworkController;
import com.ActivityNetwork.NetworkStorage;
import org.junit.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;

public class UserAccountTest {
  @Test
  public void testAccountCreation() {
    String randomUsername = UUID.randomUUID().toString();
    String randomPassword = UUID.randomUUID().toString();

    String errorMessage = UserAccount.createAccount(randomUsername, randomPassword);
    assertNotSame("Invalid parameters", errorMessage);
    assertNotSame("IO Exception Somewhere...", errorMessage);
    assertNotSame("Response not correctly parsed.", errorMessage);

    // TODO: check for other assertions in account creation.

    // TODO: ensure that we cannot add the same account with the same info.
  }

  @Test
  public void testUserLogin() {
    String randomUsername = UUID.randomUUID().toString();
    String randomPassword = UUID.randomUUID().toString();
    UserAccount.createAccount(randomUsername, randomPassword);

    ArrayList<String> userInfo = UserAccount.verifyLoginInfo(randomUsername, randomPassword);
    assertSame(3, userInfo.size());
    assertSame(randomUsername, userInfo.get(0));

    assertSame(0, UserAccount.idsFromProjectJSON(userInfo.get(2)).size());
    assertSame(0, UserAccount.namesFromProjectJSON(userInfo.get(2)).size());

    NetworkController nc = new NetworkController(randomUsername, userInfo.get(1));
    assertNotSame(0, nc.createNetwork("Some Random Name"));
  }
}