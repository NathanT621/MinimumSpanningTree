import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class GraphTester {

    @Test
    /*
     * Tester method using Prim's MST with integers as the data type.
     */
    public void PrimTester1() {
        Graph<Integer> graph = new Graph<>();

        MSTNode<Integer> two = graph.addNode(2);
        MSTNode<Integer> seven = graph.addNode(7);
        MSTNode<Integer> five = graph.addNode(5);
        MSTNode<Integer> six = graph.addNode(6);
        MSTNode<Integer> four = graph.addNode(4);
        MSTNode<Integer> three = graph.addNode(3);

        graph.addEdge(5, two, seven);
        graph.addEdge(3, two, five);
        graph.addEdge(3, seven, four);
        graph.addEdge(1, seven, six);
        graph.addEdge(1, four, three);
        graph.addEdge(2, six, four);

        String actual = graph.prim(two);

        // Minimal node
        assertEquals(0, two.getEdgesEntering().size());
        assertEquals(2, two.getEdgesLeaving().size());

        // Node with one incoming edge and two outgoing edges
        assertEquals(1, seven.getEdgesEntering().size());
        assertEquals(2, seven.getEdgesLeaving().size());

        // Maximal node
        assertEquals(1, three.getEdgesEntering().size());
        assertEquals(0, three.getEdgesLeaving().size());

        // Algorithm checks
        assertTrue(actual.contains("Total MST Weight: 12.0"));

        assertTrue(actual.contains("[Start Node: 2, End Node: 5, Weight: 3.0]"));
        assertTrue(actual.contains("[Start Node: 2, End Node: 7, Weight: 5.0]"));
        assertTrue(actual.contains("[Start Node: 7, End Node: 6, Weight: 1.0]"));
        assertTrue(actual.contains("[Start Node: 6, End Node: 4, Weight: 2.0]"));
        assertTrue(actual.contains("[Start Node: 4, End Node: 3, Weight: 1.0]"));

        System.out.println(actual);
    }

    @Test
    public void PrimTester2() {
        Graph<Integer> graph = new Graph<>();

        MSTNode<Integer> two = graph.addNode(2);

        String actual = graph.prim(two);

        /*
         * For a graph with one node and no edges, the MST has total weight 0.
         * The node's value is 2, but the MST weight is not 2.
         */
        assertTrue(actual.contains("Total MST Weight: 0.0"));

        System.out.println(actual);
    }
}