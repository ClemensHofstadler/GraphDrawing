package Graph;
import java.util.ArrayList;

/**
 * Class to represent graphs. A graph is represented as a list
 * of nodes and as a list of edges. An edge is a pair of non-negative integers 
 * (i,j) determining an edge starting at node i and ending at node j. 
 * The index of a node is given by its position in the list of nodes.
 * 
 * @author Clemens Hofstadler, Lukas Wögerer
 * @version 1.0.0, 1st June 2019
 *
 */
public class Graph {
	/**
	 * The list of nodes of the graph.
	 */
	private ArrayList<Node> nodes;
	/**
	 * The list of edges of the graph.
	 */
	private ArrayList<int[]> edges;
	/**
	 * Determines whether the graph is considered as directed or undirected.
	 */
	private boolean directed; 
//=======================================================================
// Constructor
//=======================================================================
	/**
	 * Default constructor for an empty graph.
	 */
	public Graph(){ 
		nodes = new ArrayList<Node>();
		edges = new ArrayList<int[]>();
		directed = true;
	}
//=======================================================================
// Adding nodes and edges
//=======================================================================
	/**
	 * Adds a node to the graph if it is not already contained.
	 * The nodes are compared via their names.
	 * 
	 * @param n A node.
	 */
	public void addNode(Node n){
		if(!nodes.contains(n))
			nodes.add(n);	
	}
	
	/**
	 * Adds an edge between Node n1 and Node n2 to the graph
	 * if it is not already contained. If n1 or n2 is not 
	 * contained in the graph, then of course no edge is
	 * added.
	 * 
	 * @param n1 A node.
	 * @param n2 A node.
	 */
	public void addEdge(Node n1, Node n2) {
		int from = nodes.indexOf(n1);
		int to = nodes.indexOf(n2);
		if(from == -1 || to == -1)
			return;
		
		int[] edge = {from,to};
		for(int[] e: edges)
			if(e[0] == edge[0] && e[1] == edge[1])
				return;
	
		edges.add(edge);
	}
//=======================================================================
// Getter for the fields
//=======================================================================	
	/**
	 * Getter for the list of nodes.
	 * 
	 * @return The list of nodes of the graph.
	 */
	public ArrayList<Node> nodes(){
		return nodes;
	}
	
	/**
	 * Getter for the list of edges.
	 * 
	 * @return The list of edges of the graph.
	 */
	public ArrayList<int[]> edges(){
		return edges;
	}
	
	/**
	 * Getter for the field directed
	 * 
	 * @return Boolean value indicating whether the graph is considered
	 * as a directed graph.
	 */
	public boolean directed() {
		return directed;
	}
//=======================================================================
// Auxiliary functions
//=======================================================================	
	/**
	 * Setter for the directed field
	 * 
	 * @param b Boolean value determining whether the
	 * graph should be considered as directed
	 */
	public void setDirected(boolean b) {
		directed = b;
	}
	
	/**
	 * Returns the nearest node to the position (x,y).
	 * 
	 * @param x x-coordinate of the point that is considered.
	 * @param y y-coordinate of the point that is considered.
	 * 
	 * @return The nearest node to the point (x,y)
	 */
	public Node nearestNode(double x, double y) {
		Node n = nodes.get(0);
		double minDistance = Math.sqrt(Math.pow(x-n.x(), 2)+Math.pow(y-n.y(), 2));
		double testDistance;
		for(Node node: nodes) {
			testDistance = Math.sqrt(Math.pow(x-node.x(), 2)+Math.pow(y-node.y(), 2));
			if(testDistance < minDistance) {
				minDistance = testDistance;
				n = node;
			}
		}
		return n;
	}
	
	/**
	 * Given a node n, this method returns a list of all nodes to which an edges
	 * goes out from n.
	 * 
	 * @param n The node whose out edges should be computed.
	 * @return A list of indices determining that there is an edge
	 * from n to this node. 
	 */
	public ArrayList<Integer> outEdges(Node n) {
		ArrayList<Integer> out = new ArrayList<Integer>();
		int index = nodes.indexOf(n);
		for(int[] edge: edges) 
			if(edge[0] == index) {
				out.add(edge[1]);
			}
		return(out);
	}
	
	/**
	 * Given a node n, this method returns a list of all nodes from which an edges
	 * goes in to n.
	 * 
	 * @param n The node whose in edges should be computed.
	 * @return A list of indices determining that there is an edge
	 * going from this edge to n.
	 */
	public ArrayList<Integer> inEdges(Node n) {
		ArrayList<Integer> in = new ArrayList<Integer>();
		int index = nodes.indexOf(n);
		for(int[] edge: edges) 
			if(edge[1] == index) {
				in.add(edge[0]);
			}
		return(in);
	}
}
