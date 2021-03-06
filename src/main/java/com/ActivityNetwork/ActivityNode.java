package com.ActivityNetwork;

import java.util.Set;
import java.util.HashSet;

/**
 * The ActivityNode class, which represents an activity node in the Critical Path Method.
 */
public class ActivityNode {
  /** Unique identifier for this specific node. */
  private long nodeId;

  /** User-assigned name corresponding to this activity. */
  private String name;

  /** User-assigned description corresponding to this activity. */
  private String description;

  /** User-assigned normal time in hours to activity completion. */
  private double normalTime;

  /** User-assigned optimistic time in hours to activity completion. */
  private double optimisticTime;

  /** User-assigned pessimistic time in hours to activity completion. */
  private double pessimisticTime;

  /** Computed expected time in hours to activity completion. */
  private double expectedTime;

  /** Set of nodeIds associated that this activity depends on. */
  private Set<Long> dependencies;

  /**
   * Computes the expected time using the current normal, optimistic, and pessimistic times. Using the formula defined
   * here: http://www.devx.com/enterprise/project-management-time-analysis.html
   *
   * @return The normal time to activity completion in hours.
   */
  private double computeExpectedTime() {
    return (optimisticTime + 4 * normalTime + pessimisticTime) / 6.0;
  }

  /**
   * Constructor without dependencies given. Sets the nodeId, name, description, and all times. Computes and sets the
   * normal time as well.
   *
   * @param nodeId          Unique (with respect to network) identifier for this specific node.
   * @param name            Name associated with the activity.
   * @param description     Description associated with the activity in hours.
   * @param optimisticTime  Optimistic time to activity completion in hours.
   * @param normalTime      Normal time to activity completion in hours.
   * @param pessimisticTime Pessimistic time to activity completion in hours.
   */
  public ActivityNode(long nodeId, String name, String description, double optimisticTime, double normalTime,
                      double pessimisticTime) {
    this.nodeId = nodeId;
    this.name = name;
    this.description = description;
    this.normalTime = normalTime;
    this.optimisticTime = optimisticTime;
    this.pessimisticTime = pessimisticTime;
    this.expectedTime = computeExpectedTime();
    this.dependencies = new HashSet<>();
  }

  /**
   * Cloning method, using for creating a new instance of the current node.
   */
  ActivityNode twin() {
    ActivityNode n = new ActivityNode(this.getNodeId(), this.getName(), this.getDescription(),
        this.getTimes()[0], this.getTimes()[1], this.getTimes()[2]);
    n.setDependencies(this.dependencies);

    return n;
  }

  /**
   * Accessor method for node's ID.
   *
   * @return The node's nodeId.
   */
  public long getNodeId() {
    return nodeId;
  }

  /**
   * Accessor method for the node's name.
   *
   * @return The node's name.
   */
  public String getName() {
    return name;
  }

  /**
   * Accessor method for the node's description.
   *
   * @return The node's description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Accessor method for the node's dependencies.
   *
   * @return The node's dependencies.
   */
  Set<Long> getDependencies() {
    return dependencies;
  }

  /**
   * Mutator method for the node's name.
   *
   * @param name The new name for the node.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Mutator method for the node's description.
   *
   * @param description The new description for the node.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Return all times associated with the node in order of: optimistic, normal, pessimistic, expected.
   *
   * @return All times associated with the node.
   */
  public double[] getTimes() {
    return new double[]{optimisticTime, normalTime, pessimisticTime, expectedTime};
  }

  /**
   * Mutator method for the node's normal time to competition. Re-computes the expected time.
   *
   * @param normalTime The new normal time for the node.
   */
  public void setNormalTime(double normalTime) {
    this.normalTime = normalTime;
    this.expectedTime = computeExpectedTime();
  }

  /**
   * Mutator method for the node's optimistic time to competition. Re-computes the expected time.
   *
   * @param optimisticTime The new optimistic time for the node.
   */
  public void setOptimisticTime(double optimisticTime) {
    this.optimisticTime = optimisticTime;
    this.expectedTime = computeExpectedTime();
  }

  /**
   * Mutator method for the node's pessimistic time to competition. Re-computes the expected time.
   *
   * @param pessimisticTime The new pessimistic time for the node.
   */
  public void setPessimisticTime(double pessimisticTime) {
    this.pessimisticTime = pessimisticTime;
    this.expectedTime = computeExpectedTime();
  }

  /**
   * Mutator method for the node's dependencies. For all dependencies d, d != nodeId. Breaking this condition indicates
   * an activity depending on itself.
   *
   * @param dependencies The new node's dependencies.
   * @return True if the dependencies were set. False otherwise.
   */
  public boolean setDependencies(Set<Long> dependencies) {
    for (Long dependency : dependencies) {
      if (dependency == this.nodeId) {
        return false;
      }
    }

    this.dependencies = dependencies;
    return true;
  }

}
