package Embeddings;

import java.util.ArrayList;

import Graph.Graph;
import Graph.Node;
/**
 * Class to align a graph according to a circular embedding. This means
 * that all nodes are placed equidistant on a circle.
 * 
 * @author Clemens Hofstadler
 * @version 1.0.0, 31st May 2019
 *
 */
public class CircularEmbedding {
	/**
	 * Aligns the nodes of a graph G on a circle in the 
	 * unit square according to a circular embedding.
	 * 
	 * @param G A graph.
	 */
	public static void defineLayout(Graph G) {
		ArrayList<Node> nodes = G.nodes();
		double n = 2*Math.PI/nodes.size();
		for(int i = 0; i < nodes.size(); i++)
			nodes.get(i).setPosition(0.5 + 0.5*Math.cos(i*n),0.5 + 0.5*Math.sin(i*n));
	}	
}
