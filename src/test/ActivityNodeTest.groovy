
package test;
import main.ActivityNode;

class ActivityNodeTest extends GroovyTestCase {
    /**
     * Verify that the constructor modifies the correct expected time, and that the dependencies are an empty list.
     */
    void testConstructorTimesAndDependencies() {
        ActivityNode a = new ActivityNode(0, "Finish Wings", "The wings should be working", 5, 10, 20);
        assertEquals(0, a.getDependencies().size());
        assertEquals((20 + 10 * 4 + 5) / 6.0, a.getTimes()[3]);
    }

    /**
     * Verify that the expected time is updated when the pessimistic, normal, or optimistic times are updated.
     */
    void testTimeUpdatesWithAccess() {
        ActivityNode a = new ActivityNode(0, "Finish Wings", "The wings should be working", 5, 10, 20);

        a.setNormalTime(11);
        assertEquals((20 + 11 * 4 + 5) / 6.0, a.getTimes()[3]);

        a.setOptimisticTime(21);
        assertEquals((21 + 11 * 4 + 5) / 6.0, a.getTimes()[3]);

        a.setPessimisticTime(4);
        assertEquals((21 + 11 * 4 + 4) / 6.0, a.getTimes()[3]);
    }

    /**
     * Verify that the dependencies are set correctly when the node IDs in the dependency set do not contain the node
     * ID itself, and that the inverse does not occur (node ID in dependency set).
     */
    void testNodeIDInDependencySet() {
        ActivityNode a = new ActivityNode(0, "Finish Wings", "The wings should be working", 5, 10, 20);

        assertEquals(false, a.setDependencies(new HashSet<Long>([0, 1, 2, 3])));
        assertEquals(0, a.getDependencies().size());

        assertEquals(true, a.setDependencies(new HashSet<Long>([1, 2, 3, 4])));
        assertEquals(4, a.getDependencies().size());
    }
}
