package Embeddings;

import java.util.ArrayList;

import Graph.Graph;
import Graph.Node;
/**
 * Class to align a graph according to a linear embedding. This means
 * that all nodes are placed on a horizontal line within the unit square.
 * 
 * @author Clemens Hofstadler
 * @version 1.0.0, 31st May 2019
 *
 */
public class LinearEmbedding {
	/**
	 * Aligns the nodes of a graph G on a horizontal line
	 * in the unit square.
	 * 
	 * @param G A graph.
	 */
	public static void defineLayout(Graph G) {
		ArrayList<Node> nodes = G.nodes();
		double n = nodes.size()-1;
		for(int i = 0; i < nodes.size(); i++) {
			nodes.get(i).setPosition(i/n, 0.5);
		}
	}
}
