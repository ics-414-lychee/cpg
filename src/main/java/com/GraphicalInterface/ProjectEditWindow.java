package com.GraphicalInterface;

import com.ActivityNetwork.ActivityNetwork;
import com.ActivityNetwork.ActivityNode;
import com.ActivityNetwork.NetworkController;
import com.BaseInterface.UserAccount;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.abs;

public class ProjectEditWindow {
  /** Button to add an activity to the current project. */
  private JButton addAnActivityButton;

  /** List containing all of the current activities associated with the current project. */
  private JList<String> currentActivityList;

  /** Button to delete an activity. */
  private JButton deleteAnActivityButton;

  /** Button to edit an activity. */
  private JButton editAnActivityButton;

  /** Button to submit the entire project. */
  private JButton addProjectButton;

  /** Button to undo the past action done to your network. */
  private JButton undoButton;

  /** Button to redo your past action done to your network. */
  private JButton redoButton;

  /** Spinner field containing the current deadline. */
  private JSpinner deadlineSpinner;

  /** Our project editing pane. */
  private JPanel projectEditPane;

  /** Project name label. */
  private JLabel projectLabel;

  /** Location of our icon. */
  private JLabel iconLabel;

  /** Table of our network graph. */
  private JTable graphTable;

  /** The activity list in the slack tab. */
  private JList<String> activityListInSlack;

  /** Output of our selected node's total slack. */
  private JLabel totalSlackOutput;

  /** Output of our selected node's free slack. */
  private JLabel freeSlackOutput;

  /** Output of our selected node's safety slack. */
  private JLabel safetySlackOutput;

  /** Scroll pane for the graph. */
  private JScrollPane graphScrollPane;

  /** Our main frame. */
  private JFrame frame = new JFrame("Team Lychee AON");

  /** List model for our current activity list. */
  DefaultListModel<String> m = new DefaultListModel<>();

  /** Our working network. */
  private ActivityNetwork a;

  /**
   * Constructor for editing an existing project.
   *
   * @param nc        Working network controller to manage our projects.
   * @param networkID ID of the network to work on.
   */
  ProjectEditWindow(NetworkController nc, long networkID) {
    a = nc.retrieveNetwork(networkID);
    projectLabel.setText("Project: " + a.getNetworkName());

    // Display our current activities.
    if (!a.getNodeList().isEmpty()) {
      a.getNodeList().forEach(n -> m.addElement(n.getName()));
      currentActivityList.setModel(m);
      activityListInSlack.setModel(m);
    }

    // Display our current deadline.
    deadlineSpinner.setModel(new SpinnerNumberModel(1.00, 0.01, (double) Integer.MAX_VALUE, 0.01));
    deadlineSpinner.setValue(a.getHoursDeadline());

    addProjectButton.addActionListener(
        e -> {
          if (verifyInput()) {
//            // Verify that the user didn't change the name to an existing project name.
//            if (UserAccount.namesFromProjectJSON(nc.getProjectJSON()).contains(nameField.getText())) {
//              JOptionPane.showMessageDialog(frame, "There exists a project with that name. Please choose a different " +
//                  "one.", "Error", JOptionPane.ERROR_MESSAGE);
//              return;
//            }

            // Move our local network to the network controller. Destroy this frame.
            nc.modifyNetwork(a);
            nc.storeNetwork(a.getNetworkId());
            frame.dispose();
          }
        }
    );

    addActivityButtonListeners(nc);
    addSlackListeners();
    setupEditProjectFrame();
    setupGraphTable();

    frame.setContentPane(projectEditPane);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setResizable(false);
    frame.pack();
    frame.setLocationRelativeTo(null);
  }

  /**
   * Constructor for adding a new project.
   *
   * @param nc   Working network controller to manage our projects.
   * @param name Name to attach to our new network.
   */
  ProjectEditWindow(NetworkController nc, String name) {
    ArrayList<String> names = UserAccount.namesFromProjectJSON(nc.getProjectJSON());
    a = nc.retrieveNetwork(UserAccount.idsFromProjectJSON(nc.getProjectJSON()).get(names.indexOf(name)));
    projectLabel.setText("Project: " + name);

    addProjectButton.addActionListener(
        e -> {
          if (verifyInput()) {
            // Move our local network to the network controller. Destroy (and I mean OBLITERATE) this frame.
            nc.modifyNetwork(a);
            nc.storeNetwork(a.getNetworkId());
            frame.dispose();
          }
        }
    );

    addActivityButtonListeners(nc);
    addSlackListeners();
    setupAddProjectFrame();
    setupGraphTable();

    deadlineSpinner.setModel(new SpinnerNumberModel(1.00, 0.01, (double) Integer.MAX_VALUE, 0.01));
    frame.setContentPane(projectEditPane);
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.setResizable(false);
    frame.pack();
    frame.setLocationRelativeTo(null);
  }

  /**
   * Add the action listeners for the activity buttons.
   *
   * @param nc Working network controller instance.
   */
  private void addActivityButtonListeners(NetworkController nc) {
    editAnActivityButton.addActionListener(
        e -> {
          if (!currentActivityList.isSelectionEmpty()) {
            String activityName = currentActivityList.getSelectedValue();
            ActivityEditWindow w = new ActivityEditWindow(nc, a, activityName, this);
            w.setVisible();
          }
        }
    );

    addAnActivityButton.addActionListener(
        e -> {
          ActivityEditWindow w = new ActivityEditWindow(nc, a, this);
          w.setVisible();
        }
    );

    deleteAnActivityButton.addActionListener(
        e -> {
          if (!currentActivityList.isSelectionEmpty()) {
            if (!a.deleteNode(a.nodeIdFromName(currentActivityList.getSelectedValue()))) {
              JOptionPane.showMessageDialog(frame, "Activity not deleted. An error has occurred.", "Error",
                  JOptionPane.ERROR_MESSAGE);

            } else {
              if (!nc.modifyNetwork(a)) {
                JOptionPane.showMessageDialog(frame, "Activity not deleted. An error has occurred.", "Error",
                    JOptionPane.ERROR_MESSAGE);

              } else {
                updateActivityList(nc);
                setupGraphTable();
              }
            }
          }
        }
    );

    undoButton.addActionListener(
        e -> {
          if (nc.undoNetworkChange(a.getNetworkId())) {
            updateActivityList(nc);
            setupGraphTable();
          }
        }
    );

    redoButton.addActionListener(
        e -> {
          if (nc.redoNetworkChange(a.getNetworkId())) {
            updateActivityList(nc);
            setupGraphTable();
          }
        }
    );
  }

  /**
   * Add action listeners for the slack tab.
   */
  private void addSlackListeners() {
    activityListInSlack.addListSelectionListener(
        e -> {
          totalSlackOutput.setText(Double.toString(abs(a.computeTotalSlack(
              a.nodeIdFromName(activityListInSlack.getSelectedValue())))));
          freeSlackOutput.setText(Double.toString(abs(a.computeFreeSlack(
              a.nodeIdFromName(activityListInSlack.getSelectedValue())))));
          safetySlackOutput.setText(Double.toString(abs(a.computeSafetySlack(
              a.nodeIdFromName(activityListInSlack.getSelectedValue())))));
        }
    );
  }

  /**
   * Setup the graph table (wonderful documentation isn't it?).
   */
  void setupGraphTable() {
    DefaultTableModel m_2 = new DefaultTableModel();

    // Attributes are the activity names, and the hours.
    m_2.addColumn("Activity Name");
    for (int i = 0; i < a.getHoursDeadline() + 1; i++) {
      m_2.addColumn(Integer.toString(i));
    }

    // Collect the data for this table.
    ArrayList<ActivityNode> u = a.getNodeList();
    for (ActivityNode a_1 : u) {
      Object[] row = new Object[m_2.getColumnCount()];

      // For each corresponding time, we mark the cell with an 'X".
      row[0] = a_1.getName();
      for (int j = (int) a.computeEarliestStartTime(a_1.getNodeId());
           j < (int) a.computeEarliestFinishTime(a_1.getNodeId()); j++) {
        row[j + 1] = "X";
      }
      m_2.addRow(row);
    }

    graphTable.setModel(m_2);
  }

  /**
   * Reflect updates to activity list.
   *
   * @param nc Working network controller.
   */
  void updateActivityList(NetworkController nc) {
    m.clear();
    a = nc.retrieveNetwork(a.getNetworkId());
    a.getNodeList().forEach(n -> m.addElement(n.getName()));
    currentActivityList.setModel(m);
    activityListInSlack.setModel(m);
  }

  /**
   * If the user presses the submit button, verify that their response is correct.
   *
   * @return True if all fields are correct. False otherwise.
   */
  private boolean verifyInput() {
    try {
      // Verify that any manually entered values are actually numbers.
      deadlineSpinner.commitEdit();

    } catch (java.text.ParseException e1) {
      JOptionPane.showMessageDialog(frame, "Deadline must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }

    // We verify that our deadline is reasonable.
    if (!a.setHoursDeadline((Double) deadlineSpinner.getValue())) {
      JOptionPane.showMessageDialog(frame, "Deadline is not reasonable. Please enter a time greater than " +
          a.computeCriticalPathTime() + " hours.", "Error", JOptionPane.ERROR_MESSAGE);
      return false;
    }

    return true;
  }

  /**
   * Make our frame visible.
   */
  void setVisible() {
    frame.setVisible(true);
  }

  /**
   * Configure the current instance of our frame to add a new project.
   */
  private void setupAddProjectFrame() {
    // Set our icon.
    ImageIcon icon = new ImageIcon(new ImageIcon(
        getClass().getResource("logo-2.png")).getImage().getScaledInstance(60, 60, Image.SCALE_DEFAULT));
    iconLabel.setIcon(icon);

    graphScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    graphTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    addProjectButton.setText("Add a New Project");
  }

  /**
   * Configure the current instance of our frame to edit existing projects.
   */
  private void setupEditProjectFrame() {
    // Set our icon.
    ImageIcon icon = new ImageIcon(new ImageIcon(
        getClass().getResource("logo-2.png")).getImage().getScaledInstance(60, 60, Image.SCALE_DEFAULT));
    iconLabel.setIcon(icon);

    graphScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    graphTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    addProjectButton.setText("Submit Changes");
  }
}
