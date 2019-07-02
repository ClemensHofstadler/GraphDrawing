package Tests;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import Embeddings.CircularEmbedding;
import Graph.Graph;
import Graph.Node;

/**
 * JUnit 4 test class for the class {@link Embeddings.CircularEmbedding CircularEmbedding}.
 * 
 * @author Clemens Hofstadler
 * @version 1.0.0, 11th June 2019
 *
 */
public class TestCircularEmbedding {
	private static Graph G;
	private static Node[] nodes;

	@Before
	public void setUp() throws Exception {
		G = new Graph();
		nodes = new Node[4];
		for(int i = 0; i < 4; i++)
			nodes[i] = new Node(Integer.toString(i));
		for(Node n: nodes)
			G.addNode(n);
	}

	@Test
	public void testEmptyGraph() {
		G = new Graph();
		CircularEmbedding.defineLayout(G);
	}
	
	@Test
	public void testUsualGraph() {
		//1->2->3
		G.addEdge(nodes[0], nodes[1]);
		G.addEdge(nodes[1], nodes[2]);
		CircularEmbedding.defineLayout(G);
		double[][] expectedPos = new double[4][];
		expectedPos[0] = new double[] {1.0,0.5};
		expectedPos[1] = new double[] {0.5,1.0};
		expectedPos[2] = new double[] {0.0,0.5};
		expectedPos[3] = new double[] {0.5,0.0};
		
		//due to rounding error when using trigonometric functions
		//we only compare whether they are close together
		for(int i = 0; i < G.nodes().size();i++) {
			double[] realPos = G.nodes().get(i).position();
			double diff = Math.sqrt(Math.pow(realPos[0] - expectedPos[i][0],2) + Math.pow(realPos[1] - expectedPos[i][1],2));
			assertTrue(diff < Math.pow(10, -10));
		}
	}

}
