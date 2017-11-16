package com.ActivityNetwork;

import java.util.Arrays;
import java.util.HashSet;
import java.lang.Math;

import org.junit.*;

import static org.junit.Assert.*;

public class ActivityNodeTest {
  /** Test node for all tests to operate on. */
  private static ActivityNode testNode;

  /**
   * Create our test node, to be updated before each test.
   */
  @Before
  public void createTestNode() {
    testNode = new ActivityNode(0, "Finish Wings", "The wings should be working", 5, 10, 20);
  }

  /**
   * Verify that the cloning creates a separate, but identical instance of our node.
   */
  @Test
  public void testCloning() {
    ActivityNode testNode2 = testNode.twin();

    assertEquals(testNode.getName(), testNode2.getName());
    assertEquals(testNode.getDescription(), testNode2.getDescription());
    assertArrayEquals(testNode.getTimes(), testNode.getTimes(), Math.ulp(testNode.getTimes()[0]));
    assertArrayEquals(testNode.getDependencies().toArray(), testNode2.getDependencies().toArray());

    testNode2.setName("New Name");
    assertNotSame(testNode.getName(), testNode2.getName());
  }

  /**
   * Verify that the constructor modifies the correct expected time, and that the dependencies are an empty list.
   */
  @Test
  public void testConstructorTimesAndDependencies() {
    assertEquals(0, testNode.getDependencies().size());
    assertEquals((20 + 10 * 4 + 5) / 6.0, testNode.getTimes()[3], Math.ulp((20 + 10 * 4 + 5) / 6.0));
  }

  /**
   * Verify that the expected time is updated when the pessimistic, normal, or optimistic times are updated.
   */
  @Test
  public void testTimeUpdatesWithAccess() {
    testNode.setNormalTime(11);
    assertEquals((20 + 11 * 4 + 5) / 6.0, testNode.getTimes()[3], Math.ulp((20 + 11 * 4 + 5) / 6.0));

    testNode.setPessimisticTime(21);
    assertEquals((21 + 11 * 4 + 5) / 6.0, testNode.getTimes()[3], Math.ulp((21 + 11 * 4 + 5) / 6.0));

    testNode.setOptimisticTime(4);
    assertEquals((21 + 11 * 4 + 4) / 6.0, testNode.getTimes()[3], Math.ulp((21 + 11 * 4 + 4) / 6.0));
  }

  /**
   * Verify that the dependencies are set correctly when the node IDs in the dependency set do not contain the node
   * ID itself, and that the inverse does not occur (node ID in dependency set).
   */
  @Test
  public void testNodeIDInDependencySet() {
    assertEquals(false, testNode.setDependencies(new HashSet<>(Arrays.asList((long) 0, (long) 1, (long) 2, (long) 3))));
    assertEquals(0, testNode.getDependencies().size());

    assertEquals(true, testNode.setDependencies(new HashSet<>(Arrays.asList((long) 1, (long) 2, (long) 3, (long) 4))));
    assertEquals(4, testNode.getDependencies().size());
  }
}