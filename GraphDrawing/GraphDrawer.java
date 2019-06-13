package GraphDrawing;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import Graph.Node;
import Graph.Graph;
import Graph.Node3D;

import java.awt.RenderingHints;
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
	 * Color of a node when the user clicks on it.
	 * Currently: orange.
	 */
	private static final Color MARKED_NODE_COLOR = Color.ORANGE;
	/**
	 * Color of adjancent nodes of a clicked node 'n' in 
	 * directed graphs which have an edge coming from 'n'.
	 * Currently: red.
	 */
	private static final Color ARROW_TO = Color.RED;
	/**
	 * Color of adjancent nodes of a clicked node 'n' in 
	 * directed graphs which have an edge going to 'n'.
	 * Currently: red.
	 */
	private static final Color ARROW_FROM = Color.BLUE;
	/**
	 * Color of adjancent nodes of a clicked node 'n' in 
	 * undirected graphs and of nodes which have an edge 
	 * coming from  'n' and an edge going to 'n' in a
	 * directed graph, respectively.
	 * Currently: magenta.
	 */
	private static final Color ARROW_TWO_WAY = Color.MAGENTA;
//======================================================================================
//Draw graph
//======================================================================================
	/**
	 * Draws a graph G on a graphics area of a certain size. Depending on the value of 
	 * linearEdges the edges between the nodes are drawn as straight lines or as 
	 * semicircles. The boolean value threeDim, additionally defines whether the
	 * graph will be drawn in a 3 dimensional perspective.
	 * 
	 * @param g The drawing area on which the graph is drawn.
	 * @param G The graph to be drawn; non-zero
	 * @param size The size of the drawing area.
	 * @param linearEdges Boolean value determining whether edges between the nodes are drawn as 
	 * straight lines or as semicircles.
	 * @param threeDim Boolean value determining whether the graph will be drawn
	 * three dimensionally.
	 */
	public static void drawGraph(Graphics g, Graph G, int size, boolean linearEdges, boolean threeDim) {
		
		//clear the drawing area
		g.clearRect(0,0,size,size);
		
		//scale nodes up
		double[][] oldPositions = scaleUp(G,size);
				
		//draw the graph
		if(threeDim)
			draw3DLayout(g,G,size,linearEdges);
		else
			draw2DLayout(g,G,size,linearEdges);
		
		//scale nodes back down
		for(int i = 0; i < G.nodes().size(); i++) {
			G.nodes().get(i).setPosition(oldPositions[i][0], oldPositions[i][1]);
		}
	}	
//======================================================================================
//2D layout
//======================================================================================
	/**
	 * Draws a graph G on a graphics area of a certain size in a 2D perspective. 
	 * Depending on the value of linearEdges the edges between the nodes are drawn 
	 * as straight lines or as semicircles.
	 * 
	 * @param g The drawing area on which the graph is drawn.
	 * @param G The graph to be drawn; non-zero
	 * @param size The size of the drawing area.
	 * @param linearEdges Boolean value determining whether edges between the nodes are drawn as 
	 * straight lines or as semicircles.
	 */
	private static void draw2DLayout(Graphics g, Graph G, int size, boolean linearEdges) {
		ArrayList<Node> nodes = G.nodes();
		ArrayList<int[]> edges = G.edges();
		int radius = getRadius(size,G);
		
		//draw edges
		for(int[] edge: edges)
			if(linearEdges)
				drawLinearEdge(g,G,edge,radius);
			else
				drawCircularEdge(g,G,edge,radius);
				
		//draw nodes - important to do this AFTER drawing the edges!!
		for(Node n: nodes)
			drawNode(g, n, radius);
	}
	
//======================================================================================
//3D layout
//======================================================================================	
	/**
	 * Draws a graph G on a graphics area of a certain size in a 3D perspective. 
	 * Depending on the value of linearEdges the edges between the nodes are drawn 
	 * as straight lines or as semicircles.
	 * 
	 * @param g The drawing area on which the graph is drawn.
	 * @param G The graph to be drawn; non-zero
	 * @param size The size of the drawing area.
	 * @param linearEdges Boolean value determining whether edges between the nodes are drawn as 
	 * straight lines or as semicircles.
	 */
	private static void draw3DLayout(Graphics g, Graph G, int size, boolean linearEdges) {
		ArrayList<Node> nodes = G.nodes();
		ArrayList<int[]> edges = G.edges();
		int radius = getRadius(size,G);
		
		//draw edges
		for(int[] edge: edges) {
			double dist = ((Node3D) nodes.get(edge[1])).distance;
			double factor = 0.2+0.8*dist;
			if(linearEdges)
				drawLinearEdge(g,G,edge,(int)(factor*radius));
			else
				drawCircularEdge(g,G,edge,(int)(factor*radius));
		}
		
		//draw nodes - important to do this AFTER drawing the edges!!
		for(Node n: nodes) {
			double dist = ((Node3D) n).distance;
			double factor = 0.2+0.8*dist;
			drawNode(g, n, (int)(factor*radius));
		}
	}
	
//======================================================================================
//Different edge drawing styles
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
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		int arrowSize = getArrowSize(radius);
			
		double x1 = nodes.get(edge[0]).x();
		double y1 = nodes.get(edge[0]).y();
		double x2 = nodes.get(edge[1]).x();
		double y2 = nodes.get(edge[1]).y();
		
		double dx = x2 - x1, dy = y2 - y1;
	    double angle = Math.atan2(dy, dx);
	    int len = (int) Math.sqrt(dx*dx + dy*dy)-radius;
	    AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
	    at.concatenate(AffineTransform.getRotateInstance(angle));
	    g2d.transform(at);

	    g2d.drawLine(0, 0, len, 0);
	    if(G.directed())
	    	g2d.fillPolygon(new int[] {len, len-arrowSize, len-arrowSize, len},
	                      new int[] {0, -arrowSize, arrowSize, 0}, 4);
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
		if(Math.abs(edge[0]-edge[1]) < 2) {
			drawLinearEdge(g,G,edge,radius);
			return;
		}
		
		ArrayList<Node> nodes = G.nodes();
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		int arrowSize = getArrowSize(radius);
		
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
		if(G.directed())
			g2d.fill(new Polygon(new int[] {0,arrowSize,-arrowSize}, new int[] {0,-arrowSize,-arrowSize}, 3));
	    g2d.rotate(-angle);
	    g2d.translate(-x2, -y2);
	}	
//======================================================================================
// For coloring specific nodes (or uncoloring them)
//======================================================================================
	/**
	 * Colors a certain node in the color specified by the constant
	 * MARKED_NODE_COLOR.
	 * 
	 * @param n The node to be colored.
	 */
	public static void markNode(Node n) {
		n.setColor(MARKED_NODE_COLOR);
	}
	
	/**
	 * Colors a certain node black.
	 * 
	 * @param n The node to be colored.
	 */
	public static void unmarkNode(Node n) {
		n.setColor(Color.BLACK);
	}
	
	/**
	 * Colors adjacent nodes of a certain node 'n' in specific colors.
	 * In case of an undirected graph, all adjacent nodes are colored
	 * in the color defined by the constant ARROW_TWO_WAY. In case of
	 * a directed graph, nodes which have an edge coming from 'n' are
	 * colored in the color defined by ARROW_TO, nodes which have an 
	 * edge going to 'n' are colored in the color defined by ARROW_FROM 
	 * and nodes which have both, an edge going to 'n' and an edge coming
	 * from 'n', are colored in the color defined by the constant ARROW_TWO_WAY.
	 * 
	 * @param G The graph containing 'n'.
	 * @param n A node of 'G'.
	 */
	public static void markAdjacentNodes(Graph G, Node n) {	
		ArrayList<Integer> outEdges = G.outEdges(n);
		ArrayList<Integer> inEdges = G.inEdges(n);
		for(int j=0; j<G.edges().size(); j++) {
			if(G.nodes().indexOf(n) != j) {
				if(outEdges.contains(j) && inEdges.contains(j))
					G.nodes().get(j).setColor(ARROW_TWO_WAY);
				else {
					if(outEdges.contains(j))
						if(G.directed())
							G.nodes().get(j).setColor(ARROW_TO);
						else
							G.nodes().get(j).setColor(ARROW_TWO_WAY);
					if(inEdges.contains(j))
						if(G.directed())
							G.nodes().get(j).setColor(ARROW_FROM);
						else
							G.nodes().get(j).setColor(ARROW_TWO_WAY);
				}
			}
		}
	}
	
	/**
	 * Colors all adjacent nodes of a certain node 'n' black.
	 * 
	 * @param G The graph containing 'n'.
	 * @param n A node of 'G'.
	 */
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
	 * Scales the positions of all nodes from the unit square up to 
	 * a square of size "size".
	 * 
	 * @param G The graph to be scaled up.
	 * @param size The size of the new bounding square
	 * of the graph
	 * 
	 * @return A list of all old node positions of the graph. 
	 * To later be able to scale the graph back.
	 */
	private static double[][] scaleUp(Graph G, int size){
		ArrayList<Node> nodes = G.nodes();
		double[][] oldPositions = new double [nodes.size()][2];
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
		return(oldPositions);
	}
//======================================================================================
	/**
	 * Determines the radius of nodes of a graph G in relation to the size of the 
	 * graphics area g.
	 * @param size The size of the graphics area on which the graph should be drawn. 
	 * @param G The graph that should be drawn.
	 * @return The radius of the nodes.
	 */
	public static int getRadius(int size, Graph G) {
		int r = (int) Math.sqrt((3.5*size)/G.nodes().size());
		return r;
	}	
	
	/**
	 * Compute the size of the arrows w.r.t to the current
	 * size of the nodes.
	 * @param radius The current radius of the nodes.
	 * @return The size of the arrows
	 **/
	private static int getArrowSize(int radius) {
		return (int)(0.8*radius);
	}
//======================================================================================
	/**
	 * Returns the node of a graph which is closest to a certain position (x,y).
	 * 
	 * @param G A graph.
	 * @param x x-coordinate of the position. The value of x can not exceed the
	 * value of size
	 * @param y y-coordinate of the position. The value of y can not exceed the
	 * value of size
	 * @param size Maximal value of the x and y coordinate.
	 * @return The node of G which is closest to (x,y).
	 */
	public static Node nearestNode(Graph G, double x, double y, double size) {
		return G.nearestNode((x/size-0.1)/0.8, (y/size-0.1)/0.8);
	}
//======================================================================================	
	/**
	 * Computes the distance of a node to a certain point (x,y) where the node
	 * as well as the point (x,y) are located in a square with length size.
	 * 
	 * @param n The node to be considered.
	 * @param x x-coordinate of the position. The value of x can not exceed the
	 * value of size
	 * @param y y-coordinate of the position. The value of y can not exceed the
	 * value of size
	 * @param size Maximal value of the x and y coordinate.
	 * @return The euclidean distance from 'n' to the point (x,y).
	 */
	public static double distanceToNode(Node n, double x, double y, double size) {
		double nodeNewX = 0.8 * size * n.x() + 0.1 * size;
		double nodeNewY = 0.8 * size * n.y() + 0.1 * size;
		return Math.sqrt(Math.pow(nodeNewX-x, 2)+Math.pow(nodeNewY-y, 2));
	}
}
