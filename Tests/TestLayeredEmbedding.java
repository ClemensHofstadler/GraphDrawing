package Tests;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import Embeddings.LayeredEmbedding;
import Graph.Graph;
import Graph.Node;
/**
 * JUnit 4 test class for the class {@link LayeredEmbedding LayeredEmbedding}.
 * 
 * @author Clemens Hofstadler
 * @version 1.0.0, 11th June 2019
 *
 */
public class TestLayeredEmbedding {
	private static Graph G;
	private static Node[] nodes;

	@Before
	public void setUp() throws Exception {
		G = new Graph();
		nodes = new Node[3];
		for(int i = 0; i < 3; i++)
			nodes[i] = new Node(Integer.toString(i));
		for(Node n: nodes)
			G.addNode(n);
	}

	@Test
	public void testEmptyGraph() {
		G = new Graph();
		LayeredEmbedding.defineLayout(G);
	}
	
	@Test
	public void testAcyclicGraph() {
		//1->2->3
		G.addEdge(nodes[0], nodes[1]);
		G.addEdge(nodes[1], nodes[2]);
		LayeredEmbedding.defineLayout(G);
		double[][] expectedPos = new double[3][];
		expectedPos[0] = new double[] {0.5,0};
		expectedPos[1] = new double[] {0.5,0.5};
		expectedPos[2] = new double[] {0.5,1};
		double[][] realPos = new double[3][];
		for(int i = 0; i < G.nodes().size();i++)
			realPos[i] = G.nodes().get(i).position();
		
		assertArrayEquals(expectedPos,realPos);
	}
	
	@Test
	public void testCyclicGraph() {
		//1->2->3->1
		G.addEdge(nodes[0], nodes[1]);
		G.addEdge(nodes[1], nodes[2]);
		G.addEdge(nodes[2], nodes[0]);
		
		//should be horizontally aligned as
		//2->3->1
		LayeredEmbedding.defineLayout(G);
		double[][] expectedPos = new double[3][];
		expectedPos[0] = new double[] {0.5,1};
		expectedPos[1] = new double[] {0.5,0};
		expectedPos[2] = new double[] {0.5,0.5};
		double[][] realPos = new double[3][];
		for(int i = 0; i < G.nodes().size();i++)
			realPos[i] = G.nodes().get(i).position();
		
		assertArrayEquals(expectedPos,realPos);
	}
	
	@Test
	public void testCrossingEdgesGraph() {
		//1->4 2->3
		G.addNode(new Node("4"));
		G.addEdge(nodes[0], G.nodes().get(3));
		G.addEdge(nodes[1], nodes[2]);
		
		LayeredEmbedding.defineLayout(G);
		double[][] expectedPos = new double[4][];
		expectedPos[0] = new double[] {1./3,0};
		expectedPos[1] = new double[] {2./3,0};
		expectedPos[2] = new double[] {2./3,1};
		expectedPos[3] = new double[] {1./3,1};
		double[][] realPos = new double[4][];
		for(int i = 0; i < G.nodes().size();i++)
			realPos[i] = G.nodes().get(i).position();
		
		assertArrayEquals(expectedPos,realPos);
	}
	
}
