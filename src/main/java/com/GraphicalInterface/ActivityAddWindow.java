package com.GraphicalInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import com.ActivityNetwork.ActivityNode;
import com.ActivityNetwork.NetworkController;

public class ActivityAddWindow {
  /** Panel containing the activity addition pane. */
  private JPanel activityAddPane;

  /** Field holding the desired name. */
  private JTextField nameField;

  /** Field holding the desired description. */
  private JTextField descriptionField;

  /** Field holding the desired optimal time. */
  private JTextField optimalTimeField;

  /** Field holding the desired normal time. */
  private JTextField normalTimeField;

  /** Field holding the desired pessimistic time. */
  private JTextField pessimisticTimeField;

  /** Place to insert our output. */
  private JTextPane outputPane;

  /** List of dependencies to display. */
  private JList dependencyList;

  /** Button to submit the activity. */
  private JButton submitActivityButton;

  /** Network controller to handle activity adding. */
  private NetworkController nc;

  /**
   * Constructor. We set our network controller here.
   *
   * @param nc
   */
  @SuppressWarnings("BoundFieldAssignment")
  public ActivityAddWindow(NetworkController nc) {
    this.nc = nc;

    submitActivityButton.addActionListener(new SubmitButtonClicked());

    dependencyList = new JList(nc.);
    dependencyList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    dependencyList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
  }

  private class SubmitButtonClicked implements ActionListener {
    private ActivityNode n;

    public SubmitButtonClicked() {
      this.n = new ActivityNode(0, nameField.getText(), descriptionField.getText(), Double.valueOf(optimalTimeField
          .getText()), Double.valueOf(normalTimeField.getText()), new HashSet<>(dependencyList.as));
    }

    @Override
    public void actionPerformed(ActionEvent e) {


      if (leftOperand == null || leftOperand == 0.0) {
        value = resultsTxt.getText() + value;
      } else {
        rightOperand = Double.valueOf(value);
      }
      resultsTxt.setText(value);
    }
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("Add an Activity");
    frame.setContentPane(new ActivityAddWindow().activityAddPane);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
}
