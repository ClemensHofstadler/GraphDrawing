package Embeddings;
import Graph.Graph;
import Graph.Node;

/**
 * Class to align a graph according to a random embedding. This means
 * that all nodes are placed randomly in the unit square.
 * 
 * @author Lukas W&oumlgerer
 * @version 1.0.0, 31st May 2019
 *
 */
public class RandomEmbedding {
	/**
	 * Aligns the nodes of a graph G randomly in the
	 * unit square.
	 * 
	 * @param G A graph.
	 */
	public static void defineLayout(Graph G) {
		for(Node node: G.nodes()) {
			node.setPosition(Math.random(),Math.random());
		}
	}
	
}