package Tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import Graph.Graph;
import Graph.Node;

/**
 * JUnit 4 test class for the class {@link Graph.Graph Graph}.
 * 
 * @author Clemens Hofstadler
 * @version 1.0.0, 29rd June 2019
 *
 */
public class TestGraph {
	Graph G;
	Node n1;
	Node n2;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		G = new Graph();
		n1 = new Node("n1");
		n2 = new Node("n2",new double[] {0.5,0.5});
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddExistsingNode() {
		G.addNode(n1);
		n2 = new Node("n1",new double[] {0.5,0.5});
		G.addNode(n2);
		assertEquals(G.nodes().size(),1);
		assertEquals(G.nodes().get(0),n1);
	}
	
	@Test
	public void testAddExistingEdge() {
		G.addNode(n1);
		G.addNode(n2);
		G.addEdge(n1, n2);
		G.addEdge(n1, n2);
		assertEquals(G.edges().size(),1);
		assertArrayEquals(G.edges().get(0),new int[] {0,1});
	}
	
	@Test
	public void testAddNonExistingEdge() {
		G.addNode(n1);
		G.addEdge(n1, n2);
		assertEquals(G.edges().size(),0);		
	}
	
	@Test
	public void testNearestNodeEmptyGraph() {
		G.nearestNode(0.0, 0.0);
	}
	
	@Test
	public void testNearestNodeUsualCase() {
		G.addNode(n1);
		G.addNode(n2);
		assertEquals(G.nearestNode(0.1, 0.1),n1);
	}
	
	@Test
	public void testOutEdgesNotInGraph() {
		assertEquals(G.outEdges(n1).size(),0);
	}
	
	@Test
	public void testOutEdgesUsualCase() {
		Node n3 = new Node("n3",new double[] {0.9,0.9});
		G.addNode(n1);
		G.addNode(n2);
		G.addNode(n3);
		G.addEdge(n1, n2);
		G.addEdge(n2, n3);
	
		ArrayList<Integer> outEdges = G.outEdges(n2);
		assertEquals(outEdges.size(),1);
		assertEquals(outEdges.get(0),Integer.valueOf(2));
	}
	
	@Test
	public void testInEdgesNotInGraph() {
		assertEquals(G.inEdges(n1).size(),0);
	}
	
	@Test
	public void testInEdgesUsualCase() {
		Node n3 = new Node("n3",new double[] {0.9,0.9});
		G.addNode(n1);
		G.addNode(n2);
		G.addNode(n3);
		G.addEdge(n1, n2);
		G.addEdge(n2, n3);
	
		ArrayList<Integer> inEdges = G.inEdges(n2);
		assertEquals(inEdges.size(),1);
		assertEquals(inEdges.get(0),Integer.valueOf(0));
	}
}
