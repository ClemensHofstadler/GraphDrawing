package Embeddings;

import java.util.ArrayList;
import java.util.Collections;

import Graph.Graph;
import Graph.Node;

/**
 * Class to align a graph according to a layered embedding. This means
 * that all nodes are placed on a horizontal layers such that (almost) all 
 * edges of the graph show downwards. In case of acyclic graphs, really
 * all edges show downwards.
 * 
 * @author Clemens Hofstadler
 * @version 1.0.0, 11th June 2019
 *
 */
public class LayeredEmbedding {
	
	/**
	 * Aligns the nodes of a graph G on horizontal layers
	 * in the unit square such that (almost) all edges of G show 
	 * downwards.
	 * 
	 * @param G A graph.
	 */
	public static void defineLayout(Graph G) {
		//make copies
		ArrayList<Node> nodes = new ArrayList<Node>();
		for(Node n: G.nodes())
			nodes.add(n);
		ArrayList<Node[]> edges = new ArrayList<Node[]>();
		for(int[] e: G.edges())
			edges.add(new Node[] {nodes.get(e[0]),nodes.get(e[1])});
		//make the graph acyclic if it is not
		makeAcyclic(nodes,edges);
		//assign layers
		ArrayList<ArrayList<Node>> layers = assignLayers(nodes,edges);
		//try to reduce crossings
		reduceCrossings(G,layers);
		//position the nodes of the real graph
		for(int i = 0; i < layers.size(); i++) {
			ArrayList<Node> layer = layers.get(i);
			for(int j = 0; j < layer.size(); j++) {
				int index = G.nodes().indexOf(layer.get(j));
				double x = (double)(j+1)/(layer.size()+1);
				double y = (double)(i)/(layers.size()-1);
				G.nodes().get(index).setPosition(x,y);
			}
		}
	}
	
	/**
	 * Make a cyclic graph acyclic by removing loops (this is
	 * done by reverting edges).
	 * 
	 * @param nodes The nodes of a graph.
	 * @param edges The edges of a graph.
	 */
	private static void makeAcyclic(ArrayList<Node> nodes, ArrayList<Node[]> edges) {
		
		ArrayList<Node> nodesCopy = new ArrayList<Node>();
		for(Node n: nodes)
			nodesCopy.add(n);
		ArrayList<Node[]> edgesCopy = new ArrayList<Node[]>();
		for(Node[] e: edges)
			edgesCopy.add(new Node[] {e[0],e[1]});
		
		ArrayList<Node[]> toReverse = edgesToReverse(nodesCopy,edgesCopy);
		for(Node[] edge : toReverse) {
			edges.removeIf(e->(e[0].equals(edge[0]) && e[1].equals(edge[1])));
			edges.add(new Node[] {edge[1],edge[0]});
		}
	}
	
	/**
	 * Computes the edges of a cyclic graph which have to 
	 * be reversed in order to obtain an acyclic graph.
	 * 
	 * @param nodes Nodes of the graph.
	 * @param edges Edges of the graph.
	 * @return All edges of the graph that have to be reversed
	 * to obtain an acyclic graph.
	 */
	private static ArrayList<Node[]> edgesToReverse(ArrayList<Node> nodes, ArrayList<Node[]> edges) {
		ArrayList<Node[]> toReverse = new ArrayList<Node[]>();
		while(nodes.size() > 0) {
			ArrayList<Node> leaves = new ArrayList<Node>();
			ArrayList<Node> sources = new ArrayList<Node>();
			//find all leaves & sources
			do {
				leaves.clear();
				sources.clear();
				for(Node n : nodes) {
					boolean leaf = true;
					boolean source = true;
					for(Node[] edge: edges) {
						if(edge[0].equals(n))
							leaf = false;
						if(edge[1].equals(n))
							source = false;
					}
					if(leaf)
						leaves.add(n);
					if(source)
						sources.add(n);
				}
			
				//delete all leaves and their edges
				for(Node n: leaves) {
					nodes.remove(n);
					edges.removeIf(e -> (e[1] == n));
				}
			
				//delete all sources and their edges
				for(Node n: sources) {
					if(nodes.contains(n))
						nodes.remove(n);
					edges.removeIf(e -> (e[0] == n));
				}
			}while(leaves.size() > 0 || sources.size() > 0);
			//revert one edge
			if(edges.size() > 0) {
				toReverse.add(edges.get(0));
				edges.remove(0);
			}	
		}
		return(toReverse);
	}
	
	/**
	 * Divides the nodes of G into layers such that all edges of
	 * G show in the same direction.
	 * 
	 * @param G An acyclic graph.
	 * @return A list of lists of nodes, where each list of nodes 
	 * corresponds to one layer.
	 */
	private static ArrayList<ArrayList<Node>> assignLayers(ArrayList<Node> nodes,ArrayList<Node[]> edges){
		
		ArrayList<ArrayList<Node>> sorted = new ArrayList<ArrayList<Node>>();
		ArrayList<Node> start = nodesWithoutInEdges(nodes,edges);
		while(start.size() > 0) {
			sorted.add(start);
			nodes.removeAll(start);
			for(int i = edges.size()-1; i >= 0; i--)
				for(Node n: start)
					if(n == edges.get(i)[0]) {
						edges.remove(i);
						break;
					}
			start = nodesWithoutInEdges(nodes,edges);
		}
		return sorted;
	}
	
	/**
	 * Returns all nodes in the set "nodes" which have no incoming edges.
	 * 
	 * @param nodes A set of nodes.
	 * @param edges The set of all edges between the nodes of "nodes".
	 * @return A subset of "nodes" containing all nodes with no incoming edges.
	 */
	private static ArrayList<Node> nodesWithoutInEdges(ArrayList<Node> nodes, ArrayList<Node[]> edges) {
		ArrayList<Node> result = new ArrayList<Node>();
		for(Node n: nodes) {
			boolean flag = true;
			for(Node[] edge: edges)
				if(edge[1] == n)
					flag = false;
				if(flag)
					result.add(n);
		}
		return result;
	}
	
	/**
	 * Tries to reduce the crossings in the layers given by the set "layers". This is done by
	 * going through the layers from top to bottom and sorting each layer with some kind of
	 * bubble sort algorithm that minimizes the total number of crossings for the current
	 * layer with the previous layer.
	 * 
	 * @param G An acyclic graph.
	 * @param layers A list of layers of the nodes of G.
	 */
	private static void reduceCrossings(Graph G,ArrayList<ArrayList<Node>> layers){
		for(int i = 0; i < layers.size()-1; i++) {
			ArrayList<Node> upper = layers.get(i);
			ArrayList<Node> lower = layers.get(i+1);
			boolean flag = true;
			while(flag) {
				flag = false;
				for(int j = 0; j < lower.size()-1; j++)
					if(crossings(lower.get(j),lower.get(j+1),G,upper) > crossings(lower.get(j+1),lower.get(j),G,upper)) {
						Collections.swap(lower, j, j+1);
						flag = true;
					}
			}
		}
	}
	
	/**
	 * Computes the number of crossings of edges starting somewhere in
	 * the layer "upper" and reaching n1 with edges starting in upper and 
	 * reaching n2. n1 and n2 have to be in the same layer in the layered 
	 * graph and n1 is supposed to be to the left of n2.
	 * 
	 * @param n1 Node in a graph.
	 * @param n2 Node in a graph.
	 * @param G A graph containing n1 and n2.
	 * @param upper The layer above the layer where n1 and n2 are from.
	 * @return The number of crossings of edges going out from n1 with
	 * edges going out from n2.
	 */
	private static int crossings(Node n1, Node n2,Graph G, ArrayList<Node> upper) {
		int index1 = G.nodes().indexOf(n1);
		int index2 = G.nodes().indexOf(n2);
	
		int numberCrossings = 0;
		ArrayList<int[]> edges = G.edges();
		for(int i = 0; i < edges.size(); i++)
			for(int j = 0; j < edges.size(); j++)
				//we have edges (v1,n1) and (v2,n2)
				if(edges.get(i)[1] == index1 &&  edges.get(j)[1] == index2) {
					Node v1 = G.nodes().get(edges.get(i)[0]);
					Node v2 = G.nodes().get(edges.get(j)[0]);
					if(upper.indexOf(v1) > upper.indexOf(v2))
						numberCrossings++;
				}
		return(numberCrossings);				
	}
}
