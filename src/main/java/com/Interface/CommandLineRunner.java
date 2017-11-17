package com.Interface;

import com.ActivityNetwork.NetworkController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class CommandLineRunner {
  /**
   * Executes the runner. Any arguments passed are not used.
   *
   * @param args Arguments passed. This is not used.
   */
  public static void main(String[] args) throws IOException {
    CommandLineInterface r = new CommandLineInterface(new Scanner(System.in));

    boolean desiredExit = false;
    while (!desiredExit) {

      // We first present the welcome screen. User has access to command space: [Y, N]
      ArrayList<String> userInfo = r.welcomeScreen();
      assert !userInfo.isEmpty();

      // Once passed, the user has access to the projects screen, and command space: [add, delete, edit, exit].
      NetworkController nc = new NetworkController(userInfo.get(0), userInfo.get(1), userInfo.get(2));
      long desiredNetworkID = r.projectOverviewScreen(nc, userInfo.get(2));

      // We are now in the single project screen. User has access to commands: [add, edit, delete, exit].
      if (desiredNetworkID != 0) {
        r.projectSpecificScreen(nc, desiredNetworkID);

      } else {
        desiredExit = true;

      }
    }
  }
}
