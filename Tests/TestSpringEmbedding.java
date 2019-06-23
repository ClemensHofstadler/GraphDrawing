package Tests;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.BeforeClass;

import Embeddings.GridEmbedding;
import Embeddings.SpringEmbedding;
import Graph.Graph;
import Graph.Node;

/**
 * JUnit 4 test class for the class {@link SpringEmbedding SpringEmbedding}.
 * 
 * @author Lukas Wögerer
 * @version 1.0.0, 23rd June 2019
 *
 */
public class TestSpringEmbedding {
	private static Graph G;
	private static Node[] nodes;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		G = new Graph();
		nodes = new Node[4];
		for (int i = 0; i < 4; i++)
			nodes[i] = new Node(Integer.toString(i));
	}

	@Test
	public void testEmptyGraph() {
		SpringEmbedding.defineLayout(G, 2, 0);
	}

	@Test
	public void testNormaluseCase2D() {
		for (Node n : nodes)
			G.addNode(n);
		G.addEdge(nodes[0], nodes[1]);
		G.addEdge(nodes[1], nodes[2]);
		G.addEdge(nodes[2], nodes[3]);
		G.addEdge(nodes[3], nodes[0]);

		SpringEmbedding.defineLayout(G, 2, 0);

		double[][] expectedPos = new double[4][];
		expectedPos[0] = new double[] { 0.0, 0.40490199911312436 };
		expectedPos[1] = new double[] { 0.5950973716498683, 0.0 };
		expectedPos[2] = new double[] { 0.9999993428671403, 0.5950979647476852 };
		expectedPos[3] = new double[] { 0.4049018130840427, 1.0 };
		// Due to rounding error we only compare whether they are close together.
		for (int i = 0; i < G.nodes().size(); i++) {
			double[] realPos = G.nodes().get(i).position();
			double diff = Math
					.sqrt(Math.pow(realPos[0] - expectedPos[i][0], 2) + Math.pow(realPos[1] - expectedPos[i][1], 2));
			assertTrue(diff < 1e-12);
		}
	}
}
