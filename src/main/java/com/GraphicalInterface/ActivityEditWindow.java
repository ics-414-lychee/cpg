package com.GraphicalInterface;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import com.ActivityNetwork.ActivityNetwork;
import com.ActivityNetwork.ActivityNode;
import com.ActivityNetwork.NetworkController;

public class ActivityEditWindow {
  /** Panel containing the activity addition pane. */
  private JPanel activityEditPane;

  /** Field holding the desired name. */
  private JTextField nameField;

  /** Field holding the desired description. */
  private JTextField descriptionField;

  /** List of dependencies to display. */
  private JList<String> dependencyList;

  /** Button to submit the activity. */
  private JButton submitActivityButton;

  /** Spinner to hold the optimal time. */
  private JSpinner optimisticTimeSpinner;

  /** Spinner to hold the normal time. */
  private JSpinner normalTimeSpinner;

  /** Spinner to hold the pessimistic time. */
  private JSpinner pessimisticTimeSpinner;

  /** Location of our icon. */
  private JLabel iconLabel;

  /** The node we are working with. */
  private ActivityNode n;

  /** Our main frame. */
  private JFrame frame = new JFrame("Team Lychee AON");

  /**
   * Constructor for adding new activities.
   *
   * @param nc     Working network controller.
   * @param a      Network to operate on. Should exist in network controller.
   * @param parent Parent window to update upon exit.
   */
  ActivityEditWindow(NetworkController nc, ActivityNetwork a, ProjectEditWindow parent) {
    setActivityFrame();

    DefaultListModel<String> m = new DefaultListModel<>();
    a.getNodeList().forEach(n1 -> m.addElement(n1.getName()));
    dependencyList.setModel(m);

    submitActivityButton.addActionListener(
        e -> {
          if (verifyInfo(a, false)) {
            // The new node ID is incremented from the node with the current largest node ID. Start at 1 if this is empty.
            long nodeID = a.getNodeList().stream().max(
                Comparator.comparingLong(ActivityNode::getNodeId)).orElse(
                new ActivityNode(1, "", "", 0, 0, 0)).getNodeId() + 1;

            // Pull all the good data...
            String name = nameField.getText();
            String description = descriptionField.getText();
            double optimisticTime = (Double) optimisticTimeSpinner.getValue();
            double normalTime = (Double) normalTimeSpinner.getValue();
            double pessimisticTime = (Double) pessimisticTimeSpinner.getValue();
            Set<Long> dependencies = new HashSet<>();
            dependencyList.getSelectedValuesList().forEach(d -> dependencies.add(a.nodeIdFromName(d)));

            // Insert the node into our network.
            n = new ActivityNode(nodeID, name, description, optimisticTime, normalTime, pessimisticTime);
            n.setDependencies(dependencies);

            // We append the node's normal time to the network's deadline.
            a.insertNode(n);
            a.setHoursDeadline(a.getHoursDeadline() + n.getTimes()[3]);

            nc.modifyNetwork(a);
            nc.storeNetwork(a.getNetworkId());
            parent.updateActivityList(nc);
            parent.setupGraphTable();
            frame.dispose();
          }
        }
    );

    frame.setContentPane(activityEditPane);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setResizable(false);
    frame.pack();
    frame.setLocationRelativeTo(null);
  }

  /**
   * Constructor for editing existing activities.
   *
   * @param nc     Working network controller.
   * @param a      Network to operate on. Should exist in network controller.
   * @param name   Name of the activity to edit.
   * @param parent Parent window to update upon exit.
   */
  ActivityEditWindow(NetworkController nc, ActivityNetwork a, String name, ProjectEditWindow parent) {
    this.n = a.retrieveNode(a.nodeIdFromName(name));
    setActivityFrame();

    // Display the current activity information.
    nameField.setText(name);
    descriptionField.setText(n.getDescription());
    optimisticTimeSpinner.setValue(n.getTimes()[0]);
    normalTimeSpinner.setValue(n.getTimes()[1]);
    pessimisticTimeSpinner.setValue(n.getTimes()[2]);

    DefaultListModel<String> m = new DefaultListModel<>();
    a.getNodeList().forEach(n1 -> m.addElement(n1.getName()));
    dependencyList.setModel(m);

    submitActivityButton.addActionListener(
        e -> {
          if (verifyInfo(a, true)) {
            // Pull all the good data...
            String newName = nameField.getText();
            String description = descriptionField.getText();
            double optimisticTime = (Double) optimisticTimeSpinner.getValue();
            double normalTime = (Double) normalTimeSpinner.getValue();
            double pessimisticTime = (Double) pessimisticTimeSpinner.getValue();
            Set<Long> dependencies = new HashSet<>();
            dependencyList.getSelectedValuesList().forEach(d -> dependencies.add(a.nodeIdFromName(d)));

            // Insert the node into our network.
            n = new ActivityNode(n.getNodeId(), newName, description, optimisticTime, normalTime, pessimisticTime);
            n.setDependencies(dependencies);

            a.insertNode(n);
            nc.modifyNetwork(a);
            nc.storeNetwork(a.getNetworkId());
            parent.m.addElement(name);
            parent.updateActivityList(nc);
            parent.setupGraphTable();
            frame.dispose();
          }
        }
    );

    frame.setContentPane(activityEditPane);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setResizable(false);
    frame.pack();
    frame.setLocationRelativeTo(null);
  }

  /**
   * Verify the current user input on the form.
   *
   * @param a Activity network to verify against.
   * @param isEdit Flag to check for user duplicates or not.
   * @return True if the content is valid. False otherwise.
   */
  private boolean verifyInfo(ActivityNetwork a, boolean isEdit) {
    try {
      // Verify that any manually entered values are actually numbers.
      optimisticTimeSpinner.commitEdit();
      normalTimeSpinner.commitEdit();
      pessimisticTimeSpinner.commitEdit();

    } catch (java.text.ParseException e1) {
      JOptionPane.showMessageDialog(frame, "Deadline must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }

    // Verify the condition optimistic < normal < pessimistic.
    if (((Double) optimisticTimeSpinner.getValue() >= (Double) normalTimeSpinner.getValue()) ||
        ((Double) normalTimeSpinner.getValue() >= (Double) pessimisticTimeSpinner.getValue()) ||
        ((Double) optimisticTimeSpinner.getValue() >= (Double) pessimisticTimeSpinner.getValue())) {
      JOptionPane.showMessageDialog(frame, "Optimistic time must be the smallest. Normal time is the next smallest. " +
          "The largest time should be the pessimistic time.", "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }

    // Verify that the name is not taken by other nodes.
    if (!isEdit && a.getNodeList().stream().anyMatch(n1 -> n1.getName().equals(nameField.getText()))) {
      JOptionPane.showMessageDialog(frame, "There exists an activity with that name. Please choose another.",
          "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }

    return !nameField.getText().equals("") && !descriptionField.getText().equals("");
  }

  /**
   * Configure the current instance of our frame to edit/add activities.
   */
  private void setActivityFrame() {
    // Set our icon.
    ImageIcon icon = new ImageIcon(new ImageIcon(
        getClass().getResource("logo-2.png")).getImage().getScaledInstance(60, 60, Image.SCALE_DEFAULT));
    iconLabel.setIcon(icon);

    optimisticTimeSpinner.setModel(new SpinnerNumberModel(1.00, 0.01, (double) Integer.MAX_VALUE, 0.01));
    normalTimeSpinner.setModel(new SpinnerNumberModel(1.00, 0.01, (double) Integer.MAX_VALUE, 0.01));
    pessimisticTimeSpinner.setModel(new SpinnerNumberModel(1.00, 0.01, (double) Integer.MAX_VALUE, 0.01));
  }

  /**
   * Make our frame visible.
   */
  void setVisible() {
    frame.setVisible(true);
  }
}
