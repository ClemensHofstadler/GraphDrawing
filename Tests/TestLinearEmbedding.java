package Tests;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import Embeddings.LinearEmbedding;
import Graph.Graph;
import Graph.Node;
/**
 * JUnit 4 test class for the class {@link Embeddings.LinearEmbedding LinearEmbedding}.
 * 
 * @author Clemens Hofstadler
 * @version 1.0.0, 11th June 2019
 *
 */
public class TestLinearEmbedding {
	private static Graph G;
	private static Node[] nodes;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		G = new Graph();
		nodes = new Node[3];
		for(int i = 0; i < 3; i++)
			nodes[i] = new Node(Integer.toString(i));
	}

	@Test
	public void testEmptyGraph() {
		LinearEmbedding.defineLayout(G);
	}
	
	@Test
	public void testUsualGraph() {
		for(Node n: nodes)
			G.addNode(n);
		LinearEmbedding.defineLayout(G);
		double[][] expectedPos = new double[3][];
		expectedPos[0] = new double[] {0,0.5};
		expectedPos[1] = new double[] {0.5,0.5};
		expectedPos[2] = new double[] {1,0.5};
		double[][] realPos = new double[3][];
		for(int i = 0; i < G.nodes().size();i++)
			realPos[i] = G.nodes().get(i).position();
		
		assertArrayEquals(expectedPos,realPos);
	}
}
