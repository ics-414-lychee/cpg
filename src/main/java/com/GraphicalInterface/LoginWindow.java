package com.GraphicalInterface;

import com.BaseInterface.UserAccount;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class LoginWindow {
  /** Text field to hold what the user enters as their username. */
  private JTextField usernameField;

  /** Text field to hold what the user enters as their password. */
  private JPasswordField passwordField;

  /** The login button. */
  private JButton loginButton;

  /** The create an account button. Completion here should direct you back to this window. */
  private JButton createAnAccountButton;

  /** Our login window. */
  JPanel loginPane;

  /** The label we output to. */
  private JLabel outputLabel;

  /** Username, authentication token, and project JSON. */
  private ArrayList<String> userInfo = new ArrayList<>();

  /**
   * Constructor. We set our actions here.
   */
  LoginWindow() {
    loginButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            // Obtain the response of our login attempt.
            ArrayList<String> response = UserAccount.verifyLoginInfo(usernameField.getText().trim(),
                new String(passwordField.getPassword()));

            if (response.isEmpty()) {
              outputLabel.setText("Incorrect login information. Please try again.");

            } else {
              userInfo = response;
            }
          }
        }
    );

    createAnAccountButton.addActionListener(
        new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {

          }
        }
    );
  }

  /**
   * Access method for the user information string set.
   *
   * @return The user information (username, auth, and project JSON).
   */
  public ArrayList<String> getUserInfo() {
    return userInfo;
  }
}
