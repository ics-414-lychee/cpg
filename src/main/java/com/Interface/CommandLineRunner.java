package com.Interface;

import com.ActivityNetwork.NetworkController;
import com.ActivityNetwork.NetworkStorage;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.System.exit;

public class CommandLineRunner {
  /**
   * Executes the runner. Any arguments passed are not used.
   *
   * @param args Arguments passed. This is not used.
   */
  public static void main(String[] args) throws IOException {
    CommandLineInterface r = new CommandLineInterface();
    Console c = System.console();

    // We need the console for this to run!
    if (c == null) {
      System.err.println("No console.");
      exit(1);
    }

    boolean desiredExit = false;
    while (!desiredExit) {

      // We first present the welcome screen. User has access to command space: [Y, N]
      ArrayList<String> userInfo = r.welcomeScreen(c);
      assert !userInfo.isEmpty();

      // Once passed, the user has access to the projects screen, and command space: [add, delete, edit, exit].
      NetworkController nc = new NetworkController(userInfo.get(0), userInfo.get(1));
      long desiredNetworkID = r.projectOverviewScreen(c, nc, userInfo.get(2));

      // We are now in the single project screen. User has access to commands: [add, edit, delete, exit].
      if (desiredNetworkID != 0) {
        r.projectSpecificScreen(c, nc, desiredNetworkID);

      } else {
        desiredExit = true;

      }
    }
  }
}
