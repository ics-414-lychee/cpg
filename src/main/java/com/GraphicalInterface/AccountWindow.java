package com.GraphicalInterface;

import com.BaseInterface.UserAccount;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AccountWindow {
  /** Text field to hold what the user enters as their username. */
  private JTextField usernameField;

  /** Text field to hold what the user enters as their password. */
  private JPasswordField passwordField;

  /** The login button. */
  private JButton loginButton;

  /** The create an account button. Completion here should direct you back to this window. */
  private JButton createAnAccountButton;

  /** Our login window. */
  private JPanel loginPane;

  /** Title of our current window. */
  private JLabel titleLabel;

  /** Secondary password field for account creation. */
  private JPasswordField passwordAgainField;

  /** Secondary password label for account creation. */
  private JLabel passwordAgainLabel;

  /** Icon label for account creation. */
  private JLabel iconLabel;

  /** Our main frame. */
  private JFrame frame = new JFrame("Team Lychee AON");

  /** Flag to indicate if a user is currently creating an account. */
  private boolean isCreateAccount = false;

  /**
   * Executes the program. Any arguments passed are not used.
   *
   * @param args Arguments passed. This is not used.
   */
  public static void main(String[] args) {
    try {
      // Using the default system look.
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
        UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }

    AccountWindow l = new AccountWindow();
    l.setVisible();
  }

  /**
   * Constructor. We set our actions here.
   */
  private AccountWindow() {
    addButtonListeners();

    // Set our icon.
    ImageIcon icon = new ImageIcon(new ImageIcon(
        getClass().getResource("logo.png")).getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT));
    titleLabel.setIcon(icon);

    // We default to our login frame.
    setupLoginFrame();
    frame.setContentPane(loginPane);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.pack();
    frame.setLocationRelativeTo(null);
  }

  /**
   * Add the action listeners for the login and create an account buttons.
   */
  private void addButtonListeners () {
    loginButton.addActionListener(
        e -> {
          // Verify that the user has actually entered something in.
          if (!usernameField.getText().equals("") && passwordField.getPassword().length != 0) {
            // Obtain the response of our login attempt.
            ArrayList<String> response = UserAccount.verifyLoginInfo(usernameField.getText().trim(),
                new String(passwordField.getPassword()));

            if (response.isEmpty()) {
              JOptionPane.showMessageDialog(frame, "Incorrect login information. Please try again.",
                  "Error", JOptionPane.ERROR_MESSAGE);

            } else {
              // Display our project overview window.
              frame.dispose();
              ProjectOverviewWindow p = new ProjectOverviewWindow(response);
              p.setVisible();
            }
          }
        }
    );

    createAnAccountButton.addActionListener(
        e -> {
          // Open the create an account window.
          if (!isCreateAccount) {
            AccountWindow l = new AccountWindow();
            l.setupAccountCreationFrame();
            l.frame.pack();
            l.frame.setLocationRelativeTo(null);
            l.setVisible();

          } else {
            // Ensure that both password fields match.
            if (!(new String(passwordField.getPassword()).equals(new String(passwordAgainField.getPassword())))) {
              JOptionPane.showMessageDialog(frame, "Passwords do not match. ", "Error", JOptionPane.ERROR_MESSAGE);
              return;
            }

            // Obtain the response of our login attempt.
            String response = UserAccount.createAccount(usernameField.getText().trim(),
                new String(passwordField.getPassword()));

            if (response.equals("")) {
              // If successful, notify user and move back to the login screen.
              JOptionPane.showMessageDialog(frame, "Account successfully created. Please login.",
                  "Success", JOptionPane.PLAIN_MESSAGE);
              frame.dispose();

            } else {
              // Otherwise, we display the error message.
              JOptionPane.showMessageDialog(frame, "Error: " + response, "Error", JOptionPane.ERROR_MESSAGE);
            }
          }
        }
    );
  }

  /**
   * Make our frame visible.
   */
  private void setVisible() {
    frame.setVisible(true);
  }

  /**
   * Configure the current instance of our frame to login.
   */
  private void setupLoginFrame() {
    titleLabel.setText("Activity on Node");
    titleLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 36));

    iconLabel.setVisible(false);
    passwordAgainField.setVisible(false);
    passwordAgainLabel.setVisible(false);
    isCreateAccount = false;
  }

  /**
   * Configure the current instance of our frame to create accounts.
   */
  private void setupAccountCreationFrame() {
    titleLabel.setText("Create an Account");
    titleLabel.setIcon(null);
    titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
    titleLabel.setHorizontalAlignment(JLabel.LEFT);

    ImageIcon icon = new ImageIcon(new ImageIcon(
        getClass().getResource("logo.png")).getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
    iconLabel.setIcon(icon);
    iconLabel.setVisible(true);

    passwordAgainField.setVisible(true);
    passwordAgainLabel.setVisible(true);
    loginButton.setVisible(false);
    isCreateAccount = true;
  }
}