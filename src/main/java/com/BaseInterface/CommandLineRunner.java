package com.BaseInterface;

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
    Scanner sc = new Scanner(System.in);
    CommandLineLogin l = new CommandLineLogin(sc);
    CommandLineProject p = new CommandLineProject(sc);

    // We first present the welcome screen. User has access to command space: [Y, N]
    ArrayList<String> userInfo = l.welcomeScreen();
    assert !userInfo.isEmpty();
    boolean desiredExit = false;

    NetworkController nc = new NetworkController(userInfo.get(0), userInfo.get(1), userInfo.get(2));
    while (!desiredExit) {
      // Once passed, the user has access to the projects screen.
      long desiredNetworkID = p.projectOverviewScreen(nc);

      // We are now in the single project screen.
      if (desiredNetworkID != 0) {
        p.projectSpecificScreen(nc, desiredNetworkID);

      } else {
        desiredExit = true;

      }
    }
  }
}
