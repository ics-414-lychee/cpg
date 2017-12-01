package com.GraphicalInterface;

import com.ActivityNetwork.NetworkController;
import com.BaseInterface.UserAccount;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ProjectOverviewWindow {
  /** The button to add a new project. */
  private JButton addANewProjectButton;

  /** The button to edit an existing project. */
  private JButton editAProjectButton;

  /** The button to delete an existing project. */
  private JButton deleteAProjectButton;

  /** List containing all of the current projects. */
  private JList<String> currentProjectList;

  /** Our project overview window. */
  private JPanel projectOverviewPane;

  /** Title of our current window. */
  private JLabel titleLabel;

  /** Our main frame. */
  private JFrame frame = new JFrame("Team Lychee AON");

  /** Network controller for the user. */
  private NetworkController nc;

  /**
   * Constructor. We set the user info list obtained from a successful login.
   *
   * @param userInfo User information obtained from a successful login.
   */
  @SuppressWarnings("BoundFieldAssignment")
  ProjectOverviewWindow(ArrayList<String> userInfo) {
    nc = new NetworkController(userInfo.get(0), userInfo.get(1), userInfo.get(2));
    DefaultListModel<String> m = new DefaultListModel<>();

    try {
      // Using the default system look.
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
        UnsupportedLookAndFeelException e) {
      e.printStackTrace();
    }

    // Display our projects.
    if (!UserAccount.namesFromProjectJSON(nc.getProjectJSON()).isEmpty()) {
      UserAccount.namesFromProjectJSON(nc.getProjectJSON()).forEach(m::addElement);
      currentProjectList.setModel(m);
    }

    // Set our icon.
    ImageIcon icon = new ImageIcon(new ImageIcon(
        getClass().getResource("logo-2.png")).getImage().getScaledInstance(60, 60, Image.SCALE_DEFAULT));
    titleLabel.setIcon(icon);

    addProjectButtonListeners(m);
    frame.setContentPane(projectOverviewPane);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.pack();
    frame.setLocationRelativeTo(null);
  }

  /**
   * Add the action listeners for the add, delete, and edit buttons.
   *
   * @param m List of projects.
   */
  private void addProjectButtonListeners(DefaultListModel<String> m) {
    deleteAProjectButton.addActionListener(
        e -> {
          // First, verify that this is what the user really wants to do.
          int response = JOptionPane.showConfirmDialog(frame, "Do you really want to delete your project? This is " +
              "irreversible!", "Warning", JOptionPane.YES_NO_OPTION);

          // Check if the user has selected anything, and if our deletion was successful.
          if (!currentProjectList.isSelectionEmpty() && response == JOptionPane.YES_OPTION) {
            ArrayList<String> names = UserAccount.namesFromProjectJSON(nc.getProjectJSON());
            if (nc.deleteNetwork(UserAccount.idsFromProjectJSON(nc.getProjectJSON()).get(names.indexOf
                (currentProjectList.getSelectedValue())))) {

              // We need to remove this element from our JList as well.
              m.removeElement(currentProjectList.getSelectedValue());
              currentProjectList.setModel(m);

            } else {
              // Something has gone wrong... I don't know what.
              JOptionPane.showMessageDialog(frame, "Project not deleted. An error has occurred.",
                  "Error", JOptionPane.ERROR_MESSAGE);
            }
          }
        }
    );

    addANewProjectButton.addActionListener(
        e -> {
          boolean isValidInput = false;
          String response = "";

          while (!isValidInput) {
            response = (String) JOptionPane.showInputDialog(frame, "Please enter the name for your network. " +
                    "This cannot be changed, so choose carefully!", "Message", JOptionPane.QUESTION_MESSAGE, null,
                null, "My Project");

            // If there is no name, repeat.
            if (response.equals("")) {
              JOptionPane.showMessageDialog(frame, "Please enter a name.", "Error",
                  JOptionPane.ERROR_MESSAGE);

            } else if (nc.createNetwork(response) == 0) {
              // If the network already exists, repeat.
              JOptionPane.showMessageDialog(frame, "Network already exists. Please choose another name.", "Error",
                  JOptionPane.ERROR_MESSAGE);

            } else {
              isValidInput = true;
            }
          }

          // Open a project add window.
          ProjectEditWindow p = new ProjectEditWindow(nc, response);
          p.setVisible();
        }
    );

    editAProjectButton.addActionListener(
        e -> {
          // Check if the user has selected anything.
          if (!currentProjectList.isSelectionEmpty()) {
            ArrayList<String> names = UserAccount.namesFromProjectJSON(nc.getProjectJSON());
            long networkID = UserAccount.idsFromProjectJSON(nc.getProjectJSON()).get(names.indexOf(currentProjectList
                .getSelectedValue()));

            ProjectEditWindow p = new ProjectEditWindow(nc, networkID);
            p.setVisible();
          }
        }
    );
  }

  /**
   * Make our frame visible.
   */
  void setVisible() {
    frame.setVisible(true);
  }
}
