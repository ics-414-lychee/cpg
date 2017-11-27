package com.BaseInterface;

import com.ActivityNetwork.NetworkController;
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
    assertFalse(errorMessage.equals("Invalid parameters"));
    assertFalse(errorMessage.equals("IO Exception Somewhere..."));
    assertFalse(errorMessage.equals("Response not correctly parsed."));

    String errorMessage2 = UserAccount.createAccount(randomUsername, randomPassword);
    assertEquals("Username already exists", errorMessage2);
  }

  @Test
  public void testUserLogin() {
    String randomUsername = UUID.randomUUID().toString();
    String randomPassword = UUID.randomUUID().toString();
    UserAccount.createAccount(randomUsername, randomPassword);

    ArrayList<String> userInfo = UserAccount.verifyLoginInfo(randomUsername, randomPassword);
    assertEquals(3, userInfo.size());
    assertEquals(randomUsername, userInfo.get(0));

    assertEquals(0, UserAccount.idsFromProjectJSON(userInfo.get(2)).size());
    assertEquals(0, UserAccount.namesFromProjectJSON(userInfo.get(2)).size());
    assertEquals(0, UserAccount.deadlinesFromProjectJSON(userInfo.get(2)).size());

    NetworkController nc = new NetworkController(randomUsername, userInfo.get(1), userInfo.get(2));
    assertFalse(0 == nc.createNetwork("Some Random Name"));
  }
}