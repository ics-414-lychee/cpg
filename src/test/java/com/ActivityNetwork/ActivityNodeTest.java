package com.ActivityNetwork;

import java.util.Arrays;
import java.util.HashSet;
import java.lang.Math;

import org.junit.*;
import static org.junit.Assert.*;

public class ActivityNodeTest {
  /**
   * Verify that the constructor modifies the correct expected time, and that the dependencies are an empty list.
   */
  @Test
  public void testConstructorTimesAndDependencies() {
    ActivityNode a = new ActivityNode(0, "Finish Wings", "The wings should be working", 5, 10, 20);
    assertEquals(0, a.getDependencies().size());
    assertEquals((20 + 10 * 4 + 5) / 6.0, a.getTimes()[3], Math.ulp((20 + 10 * 4 + 5) / 6.0));
  }

  /**
   * Verify that the expected time is updated when the pessimistic, normal, or optimistic times are updated.
   */
  @Test
  public void testTimeUpdatesWithAccess() {
    ActivityNode a = new ActivityNode(0, "Finish Wings", "The wings should be working", 5, 10, 20);

    a.setNormalTime(11);
    assertEquals((20 + 11 * 4 + 5) / 6.0, a.getTimes()[3], Math.ulp((20 + 11 * 4 + 5) / 6.0));

    a.setPessimisticTime(21);
    assertEquals((21 + 11 * 4 + 5) / 6.0, a.getTimes()[3], Math.ulp((21 + 11 * 4 + 5) / 6.0));

    a.setOptimisticTime(4);
    assertEquals((21 + 11 * 4 + 4) / 6.0, a.getTimes()[3], Math.ulp((21 + 11 * 4 + 4) / 6.0));
  }

  /**
   * Verify that the dependencies are set correctly when the node IDs in the dependency set do not contain the node
   * ID itself, and that the inverse does not occur (node ID in dependency set).
   */
  @Test
  public void testNodeIDInDependencySet() {
    ActivityNode a = new ActivityNode(0, "Finish Wings", "The wings should be working", 5, 10, 20);

    assertEquals(false, a.setDependencies(new HashSet<>(Arrays.asList((long) 0, (long) 1, (long) 2, (long) 3))));
    assertEquals(0, a.getDependencies().size());

    assertEquals(true, a.setDependencies(new HashSet<>(Arrays.asList((long) 1, (long) 2, (long) 3, (long) 4))));
    assertEquals(4, a.getDependencies().size());
  }
}