package Tests;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import Embeddings.GridEmbedding;
import Graph.Graph;
import Graph.Node;
/**
 * JUnit 4 test class for the class {@link Embeddings.GridEmbedding GridEmbedding}.
 * 
 * @author Lukas W&oumlgerer
 * @version 1.0.0, 23rd June 2019
 *
 */
public class TestGridEmbedding {
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
		GridEmbedding.defineLayout(G);
	}
	
	@Test
	public void testOneNodePerRow() {
		for(Node n: nodes)
			G.addNode(n);
		GridEmbedding.defineLayout(G, 1);
		double[][] realPos = new double[3][];
		for(int i = 0; i < G.nodes().size();i++)
			realPos[i] = G.nodes().get(i).position();
		
		double[][] expectedPos = new double[3][];
		expectedPos[0] = new double[] {0.5,0.};
		expectedPos[1] = new double[] {0.5,0.5};
		expectedPos[2] = new double[] {0.5,1.};
		
		assertArrayEquals(realPos,expectedPos);
	}
	
	@Test
	public void testAllNodesInOneRow() {
		for(Node n: nodes)
			G.addNode(n);
		GridEmbedding.defineLayout(G, 3);
		double[][] realPos = new double[3][];
		for(int i = 0; i < G.nodes().size();i++)
			realPos[i] = G.nodes().get(i).position();
		
		double[][] expectedPos = new double[3][];
		expectedPos[0] = new double[] {0.,0.5};
		expectedPos[1] = new double[] {0.5,0.5};
		expectedPos[2] = new double[] {1.,0.5};
		
		assertArrayEquals(realPos,expectedPos);
	}
	
	@Test
	public void testNormalUseCase() {
		for(Node n: nodes)
			G.addNode(n);
		GridEmbedding.defineLayout(G);
		double[][] realPos = new double[3][];
		for(int i = 0; i < G.nodes().size();i++)
			realPos[i] = G.nodes().get(i).position();
		
		double[][] expectedPos = new double[3][];
		expectedPos[0] = new double[] {0.,0.};
		expectedPos[1] = new double[] {1.,0.};
		expectedPos[2] = new double[] {0.,1.};
		
		assertArrayEquals(realPos,expectedPos);
	}
}
