import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Graph {
	
	private ArrayList<Node> nodes;
	private ArrayList<int[]> edges;
	
	Graph(){ 
		nodes = new ArrayList<Node>();
		edges = new ArrayList<int[]>();
	}
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
		if(!edges.contains(edge))
			edges.add(edge);
	}
	
	public ArrayList<Node> nodes(){
		return nodes;
	};
	
	public ArrayList<int[]> edges(){
		return edges;
	};
	
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

}
