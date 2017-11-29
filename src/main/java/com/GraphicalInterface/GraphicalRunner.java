package com.GraphicalInterface;

import com.ActivityNetwork.NetworkController;

import javax.swing.*;
import java.util.ArrayList;

public class GraphicalRunner {
  /**
   * Executes the runner. Any arguments passed are not used.
   *
   * @param args Arguments passed. This is not used.
   */
  public static void main(String[] args) {
    JFrame frame = new JFrame("Lychee Activity on Node Networks");
    LoginWindow l = new LoginWindow();

    // We start with the login pane.
    frame.setContentPane(l.loginPane);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);

    //noinspection StatementWithEmptyBody
    while(l.getUserInfo().isEmpty()) {
      // Wait for the correct user info to be entered.
    }

    // We save the login info. Create a new network controller.

    //
  }
}
