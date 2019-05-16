import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Graph {
	private final int ARR_SIZE = 10;
	
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
	
	public void draw(Graphics g) {
		for(Node node: nodes) {
			node.draw(g);
			System.out.println("(" + node.x() + "," + node.y() + ")");
			
		}
		for(int[] edge: edges) {
				Graphics2D g1 = (Graphics2D) g.create();
				int x1 = nodes.get(edge[0]).x();
				int y1 = nodes.get(edge[0]).y();
				int x2 = nodes.get(edge[1]).x();
				int y2 = nodes.get(edge[1]).y();
			
				int dx = x2 - x1, dy = y2 - y1;
                double angle = Math.atan2(dy, dx);
                int len = (int) Math.sqrt(dx*dx + dy*dy)-nodes.get(edge[1]).radius();
                AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
                at.concatenate(AffineTransform.getRotateInstance(angle));
                g1.transform(at);

                g1.drawLine(0, 0, len, 0);
                g1.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
                              new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
          }
			
	}	

}
