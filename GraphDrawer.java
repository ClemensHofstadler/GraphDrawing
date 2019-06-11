import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
/**
 * Class responsible for drawing graphs with different layouts. More
 * precisely, it is possible to draw graphs with straight edges and
 * graphs with semicircular edges.
 * 
 * @author Clemens Hofstadler, Lukas Wögerer
 * @version 1.0.0, 31st May 2019
 *
 */
public abstract class GraphDrawer {
	/**
	 * Size of the tip of the arrow drawn at the end of
	 * every edge.
	 */
	private static final int ARR_SIZE = 8;
	/**
	 * Color of a node when the user clicks on it.
	 */
	private static final Color markedNodeColor = Color.orange;
	private static final Color arrowTo = Color.RED;
	private static final Color arrowFrom = Color.BLUE;
	private static final Color arrowTwoWay = Color.MAGENTA;
//======================================================================================
//Draw graph
//======================================================================================
	/**
	 * Draws a graph G on a graphics area of a certain size. Depending on the value of 
	 * linearEdges the edges between the nodes are drawn as straight lines or as 
	 * semicircles.
	 * 
	 * @param g The drawing area on which the graph is drawn.
	 * @param G The graph to be drawn.
	 * @param size The size of the drawing area.
	 * @param linearEdges Boolean value determining whether edges between the nodes are drawn as 
	 * straight lines or as semicircles.
	 */
	public static void drawGraph(Graphics g, Graph G, int size, boolean linearEdges) {
		
		if(linearEdges)
			linearEdges(g,size,G);
		else
			circularEdges(g,size,G);
	}	
//======================================================================================
//Draw graph with linear edges
//======================================================================================
	/**
	 * Draws a graph G on a graphics area of a certain size, where the edges of the graph
	 * are drawn as straight edges.
	 * 
	 * @param g The drawing area on which the graph is drawn.
	 * @param size The size of the drawing area.
	 * @param G The graph to be drawn.
	 */
	public static void linearEdges(Graphics g, int size, Graph G) {
		
		//clear the drawing area
		g.clearRect(0,0,size,size);
		
		ArrayList<Node> nodes = G.nodes();
		double[][] oldPositions = new double [nodes.size()][2];
		ArrayList<int[]> edges = G.edges();
		int radius = getRadius(size, G);
		
		//scaling the unit square accordingly
		for(int i = 0; i < nodes.size(); i++) {
			//set node to new position
			double oldX = nodes.get(i).x();
			double oldY = nodes.get(i).y();
			oldPositions[i][0] = oldX;
			oldPositions[i][1] = oldY;
			double newX = 0.8*size*oldX + 0.1*size;
			double newY = 0.8*size*oldY + 0.1*size;
			nodes.get(i).setPosition(newX, newY);
		}
		
		//draw edges
		for(int[] edge: edges)
			drawLinearEdge(g,G,edge,radius);
		
		//draw nodes - important to do this AFTER drawing the edges!!
		for(Node n: nodes)
			drawNode(g, n, radius);
		
		//set nodes back
		for(int i = 0; i < nodes.size(); i++) {
			nodes.get(i).setPosition(oldPositions[i][0], oldPositions[i][1]);
		}
	}
//======================================================================================
	/**
	 * Draws an edge of the graph G on the graphics area g. The edge is drawn as 
	 * a straight line with an arrow at the tip.
	 * 
	 * @param g The drawing area on which the graph is drawn.
	 * @param G The graph to be drawn.
	 * @param edge The edge to be drawn. The edge is given
	 * by two integer values (i,j), meaning that the edge starts
	 * at node_i and ends at node_j.
	 * @param radius The radius of the nodes.
	 */
	private static void drawLinearEdge(Graphics g, Graph G, int[] edge, int radius) {
		ArrayList<Node> nodes = G.nodes();
		Graphics2D g1 = (Graphics2D) g.create();
		g1.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
			
		double x1 = nodes.get(edge[0]).x();
		double y1 = nodes.get(edge[0]).y();
		double x2 = nodes.get(edge[1]).x();
		double y2 = nodes.get(edge[1]).y();
		
		//drawLine(g1, x1, y1, x2, y2);
		
		double dx = x2 - x1, dy = y2 - y1;
	    double angle = Math.atan2(dy, dx);
	    int len = (int) Math.sqrt(dx*dx + dy*dy)-radius;
	    AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
	    at.concatenate(AffineTransform.getRotateInstance(angle));
	    g1.transform(at);

	    g1.drawLine(0, 0, len, 0);
	    g1.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
	                      new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
	}
//======================================================================================
//Draw graph with circular edges
//======================================================================================
	/**
	 * Draws a graph G on a graphics area of a certain size, where the edges of the graph
	 * are drawn as semicircles.
	 * 
	 * @param g The drawing area on which the graph is drawn.
	 * @param size The size of the drawing area.
	 * @param G The graph to be drawn.
	 */
	public static void circularEdges(Graphics g, int size, Graph G) {
		g.clearRect(0,0,size,size);
		
		ArrayList<Node> nodes = G.nodes();
		double[][] oldPositions = new double [nodes.size()][2];
		ArrayList<int[]> edges = G.edges();
		int radius = getRadius(size, G);
		
		//scaling the unit square accordingly
		for(int i = 0; i < nodes.size(); i++) {
			//set node to new position
			double oldX = nodes.get(i).x();
			double oldY = nodes.get(i).y();
			oldPositions[i][0] = oldX;
			oldPositions[i][1] = oldY;
			double newX = 0.8*size*oldX + 0.1*size;
			double newY = 0.8*size*oldY + 0.1*size;
			nodes.get(i).setPosition(newX, newY);
		}
		
		//draw edges
		for(int[] edge: edges)
			if(Math.abs(edge[0]-edge[1]) < 2)
				drawLinearEdge(g,G,edge,radius);
			else
				drawCircularEdge(g,G,edge,radius);
		
		//draw nodes - important to do this AFTER drawing the edges!!
		for(Node n: nodes)
			drawNode(g, n, radius);
		
		//set nodes back
		for(int i = 0; i < nodes.size(); i++) {
			nodes.get(i).setPosition(oldPositions[i][0], oldPositions[i][1]);
		}
			
	}
//======================================================================================
	/**
	 * Draws an edge of the graph G on the graphics area g. The edge is drawn as 
	 * a semicircle with an arrow at the tip.
	 * 
	 * @param g The drawing area on which the graph is drawn.
	 * @param G The graph to be drawn.
	 * @param edge The edge to be drawn. The edge is given
	 * by two integer values (i,j), meaning that the edge starts
	 * at node_i and ends at node_j.
	 * @param radius The radius of the nodes.
	 */
	private static void drawCircularEdge(Graphics g, Graph G, int[] edge,int radius) {
		ArrayList<Node> nodes = G.nodes();
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		
		double x1 = nodes.get(edge[0]).x();
		double y1 = nodes.get(edge[0]).y()-radius;
		double x2 = nodes.get(edge[1]).x();
		double y2 = nodes.get(edge[1]).y()-radius;
		
		int diameter = (int)Math.abs(x2-x1);
		double angle = Math.atan2(0, diameter);
		if(x1 < x2) {
			g2d.translate(x1, y1);
	        g2d.drawArc(0, -diameter/2, diameter, diameter, 0, 180);
	        g2d.translate(x2-x1, y2-y1);
	        g2d.rotate(angle);
		}
	    else {
	        g2d.translate(x2,y2);
	        g2d.rotate(angle);
	        g2d.drawArc(0, -diameter/2, diameter, diameter, 0, 180);
	    }
	    g2d.fill(new Polygon(new int[] {0,ARR_SIZE,-ARR_SIZE}, new int[] {0,-ARR_SIZE,-ARR_SIZE}, 3));
	    g2d.rotate(-angle);
	    g2d.translate(-x2, -y2);
	}	
//======================================================================================
//For coloring one specific node (or uncolor it)
//======================================================================================
	public static void markNode(Node n) {
		n.setColor(markedNodeColor);
	}
	
	public static void unmarkNode(Node n) {
		n.setColor(Color.BLACK);
	}
//======================================================================================
//Auxiliary functions
//======================================================================================
	/**
	 * Draws a node as a black circle filled with a smaller circle of a specific
	 * color (to visualize certain marked nodes) and radius on a graphics area.
	 * @param g The graphics area on which the node is drawn.
	 * @param n The node to be drawn.
	 * @param radius The radius of the node.
	 */
	private static void drawNode(Graphics g, Node n, int radius) {
		int x = ((int)n.x())-radius+1;
		int y = ((int)n.y())-radius+1;
		int w = 2*radius + 1;
		int h = 2*radius + 1;
		g.fillOval(x, y, w, h);
		
		if(n.color() != Color.BLACK) {
			int innerRadius = (int)(0.6*radius);
			x = ((int)n.x())-innerRadius+1;
			y = ((int)n.y())-innerRadius+1;
			w = 2*innerRadius + 1;
			h = 2*innerRadius + 1;
			g.setColor(n.color());
			g.fillOval(x, y, w, h);
			g.setColor(Color.BLACK);
		}
	}
//======================================================================================
	/**
	 * Determines the radius of nodes of a graph G in relation to the size of the 
	 * graphics area g.
	 * @param g The graphics area on which the graph should be drawn. 
	 * @param G The graph that should be drawn.
	 * @return The radius of the nodes.
	 */
	public static int getRadius(int size, Graph G) {
		//int r = (int)(0.9*size/(G.nodes().size()*3));
		//int r = (int) Math.sqrt((3.5*size)/G.nodes().size());
		int r = (int) (size/Math.sqrt(10*Math.PI*G.nodes().size()));
		return r;
	}	
//======================================================================================
	public static Node nearestNode(Graph G, double x, double y, double size) {
		return G.nearestNode((x/size-0.1)/0.8, (y/size-0.1)/0.8);
	}
//======================================================================================	
	public static double distanceToNode(Node n, double x, double y, double size) {
		double nodeNewX = 0.8 * size * n.x() + 0.1 * size;
		double nodeNewY = 0.8 * size * n.y() + 0.1 * size;
		return Math.sqrt(Math.pow(nodeNewX-x, 2)+Math.pow(nodeNewY-y, 2));
	}
//======================================================================================
	public static void markAdjacentNodes(Graph G, Node n) {
		ArrayList<Integer> outEdges = G.outEdges(n);
		ArrayList<Integer> inEdges = G.inEdges(n);
		for(int j=0; j<G.edges().size(); j++) {
			if(G.nodes().indexOf(n) != j) {
				if(outEdges.contains(j) && inEdges.contains(j))
					G.nodes().get(j).setColor(arrowTwoWay);
				else {
					if(outEdges.contains(j))
						G.nodes().get(j).setColor(arrowTo);
					if(inEdges.contains(j))
						G.nodes().get(j).setColor(arrowFrom);
				}
			}
		}
	}
//======================================================================================
	public static void unmarkAdjacentNodes(Graph G, Node n) {
		
		int i = G.nodes().indexOf(n);
		ArrayList<Integer> posOfAdjacentNodes = new ArrayList<>();
		for (int[] edge : G.edges()) {
			if (edge[0] != edge[1]) {
				if (edge[0] == i) {
					if (posOfAdjacentNodes.indexOf(edge[1]) == -1)
						posOfAdjacentNodes.add(edge[1]);
				} else if (edge[1] == i) {
					if (posOfAdjacentNodes.indexOf(edge[0]) == -1)
						posOfAdjacentNodes.add(edge[0]);
				}
			}
		}
		for (int j : posOfAdjacentNodes) {
			G.nodes().get(j).setColor(Color.BLACK);
		}
	}

}
