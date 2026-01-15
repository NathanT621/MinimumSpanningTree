import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.regex.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class GraphTester {
	@Test
	/*
	 * Tester method using Prim's MST with integers as the data type, and edge
	 * weights calculated by the absolute difference between the nodes incident to
	 * the respective edge
	 */
	public void PrimTester1() {
		MSTNode<Integer> two = new MSTNode<Integer>(2);
		MSTNode<Integer> seven = new MSTNode<Integer>(7);
		MSTNode<Integer> five = new MSTNode<Integer>(5);
		MSTNode<Integer> six = new MSTNode<Integer>(6);
		MSTNode<Integer> four = new MSTNode<Integer>(4);
		MSTNode<Integer> three = new MSTNode<Integer>(3);
		MSTEdge<Integer> twoSeven = new MSTEdge<Integer>(5, two, seven);
		MSTEdge<Integer> twoFive = new MSTEdge<Integer>(3, two, five);
		MSTEdge<Integer> sevenFour = new MSTEdge<Integer>(3, seven, four);
		MSTEdge<Integer> sevenSix = new MSTEdge<Integer>(1, seven, six);
		MSTEdge<Integer> fourThree = new MSTEdge<Integer>(1, four, three);
		MSTEdge<Integer> sixFour = new MSTEdge<Integer>(2, six, four);
		ArrayList<MSTNode> nodes = new ArrayList<MSTNode>();
		ArrayList<MSTEdge> edges = new ArrayList<MSTEdge>();
		nodes.add(two);
		nodes.add(three);
		nodes.add(five);
		nodes.add(six);
		nodes.add(four);
		nodes.add(seven);
		Graph graph = new Graph(nodes);
		String actual = graph.prim(two);

		// Set up checks

		// Minimal Node
		assertTrue(two.edgesEntering.size() == 0);
		assertTrue(two.edgesLeaving.size() == 2);

		// Node with one incoming edge and two outgoing edges
		assertEquals(1, seven.edgesEntering.size());
		assertEquals(2, seven.edgesLeaving.size());

		// Maximal Node
		assertTrue(three.edgesEntering.size() == 1);
		assertTrue(three.edgesLeaving.size() == 0);

		// Algorithm Checks

		// Check for proper weight
		assertTrue(actual.contains("Total MST Weight: 12"));
		// Check for correct nodes and edges
		assertTrue(actual.contains("[Start Node: 2, End Node: 5, Weight: 3.0]"));
		assertTrue(actual.contains("[Start Node: 2, End Node: 7, Weight: 5.0]"));
		assertTrue(actual.contains("[Start Node: 7, End Node: 6, Weight: 1.0]"));
		assertTrue(actual.contains("[Start Node: 6, End Node: 4, Weight: 2.0]"));
		assertTrue(actual.contains("[Start Node: 4, End Node: 3, Weight: 1.0]"));

		System.out.println(actual);
	}

	@Test
	public void PrimTester2() {
		MSTNode<Integer> two = new MSTNode<Integer>(2);
		ArrayList<MSTNode> nodes = new ArrayList<MSTNode>();
		nodes.add(two);
		Graph graph = new Graph(nodes);
		String actual = graph.prim(two);
		// Actual will only contain the startNode value as a double if it also the total
		// MST weight
		assertTrue(actual.contains("2.0"));
		System.out.println(actual);
	}
}
