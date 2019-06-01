import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
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
			
		double x1 = nodes.get(edge[0]).x();
		double y1 = nodes.get(edge[0]).y();
		double x2 = nodes.get(edge[1]).x();
		double y2 = nodes.get(edge[1]).y();
		
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
		g.fillOval(((int)(n.x())) - radius, ((int)(n.y())) - radius, 2 * radius, 2 * radius);
		if(n.color() != Color.BLACK) {
			g.setColor(n.color());
			g.fillOval((int)(n.x() - 0.6*radius), (int)(n.y() - 0.6*radius), (int)(1.2 * radius), (int)(1.2 * radius));
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
	private static int getRadius(int size, Graph G) {
		int r = (int)(0.9*size/(G.nodes().size()*3));
		return r;
	}	
//======================================================================================
	public static void linearEdgesAntiAliasing(Graphics g, int w, int h, Graph G) {

		ArrayList<Node> nodes = G.nodes();
		ArrayList<int[]> edges = G.edges();
		int radius = 10;
		
		//scaling the unit square accordingly
		if(w<h)
			h=w;
		else
			w=h;
		for(Node node: nodes) {
			double newX = node.x();
			double newY = node.y();
			newX*=(0.8*w);
			newX += (0.1*w);
			newY*=(0.8*w);
			newY += (0.1*w);
			node.setPosition(newX, newY);
		}
		
		
		Graphics2D g1 = (Graphics2D) g.create();
		g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.BLACK);
        
        // draw nodes
        for (Node node : nodes) {
			drawNode(g, node, radius);
		}
        
		for (int[] edge : edges) {
			double x0 = nodes.get(edge[0]).x();
			double y0 = nodes.get(edge[0]).y();
			double x1 = nodes.get(edge[1]).x();
			double y1 = nodes.get(edge[1]).y();
			
			//draw edges
			drawLine(g1, x0, y0, x1, y1);
			
			// draw tips of arrows
			
			//TODO
		}
	};
//======================================================================================	
	//Some additional (needed) functions for drawing a line
	private static void drawLine(Graphics2D g, double x0, double y0, double x1, double y1) {
		boolean steep = (Math.abs(y1 - y0) > Math.abs(x1 - x0));

		if (steep) {
			double val = x0;
			x0 = y0;
			y0 = val;

			val = x1;
			x1 = y1;
			y1 = val;
		}

		if (x0 > x1) {
			double val = x0;
			x0 = x1;
			x1 = val;

			val = y0;
			y0 = y1;
			y1 = val;
		}

		double dx = x1 - x0;
		double dy = y1 - y0;
		double gradient = dy / dx;

		// Vorgehen fuer ersten Endpunkt
		double xend = (int) (x0 + 0.5);
		double yend = y0 + gradient * (xend - x0);
		double xgap = rfpart(x0 + 0.5);
		double xpxl1 = xend;
		double ypxl1 = (int) yend;

		if (steep) {
			drawPoint(g, (int) ypxl1, (int) xpxl1, rfpart(yend) * xgap);
			drawPoint(g, (int) ypxl1 + 1, (int) xpxl1, fpart(yend) * xgap);
		} else {
			drawPoint(g, (int) xpxl1, (int) ypxl1, rfpart(yend) * xgap);
			drawPoint(g, (int) xpxl1, (int) ypxl1 + 1, fpart(yend) * xgap);
		}

		double intery = yend + gradient;
		xend = (int) (x1 + 0.5);
		yend = y1 + gradient * (xend - x1);
		xgap = fpart(x1 + 0.5);
		double xpxl2 = xend;
		double ypxl2 = (int) yend;

		if (steep) {
			drawPoint(g, (int) ypxl2, (int) xpxl2, rfpart(yend) * xgap);
			drawPoint(g, (int) ypxl2 + 1, (int) xpxl2, fpart(yend) * xgap);
		} else {
			drawPoint(g, (int) xpxl2, (int) ypxl2, rfpart(yend) * xgap);
			drawPoint(g, (int) xpxl2, (int) ypxl2 + 1, fpart(yend) * xgap);
		}

		for (double x = (xpxl1 + 1); x <= (xpxl2 - 1); x++) {
			if(steep) {
				drawPoint(g, (int)(intery)  , (int)x, rfpart(intery));
                drawPoint(g, (int)(intery+1), (int)x,  fpart(intery));
			}
			else {
				drawPoint(g, (int)x, (int)(intery),  rfpart(intery));
                drawPoint(g, (int)x, (int)(intery+1), fpart(intery));
			}
			intery += gradient;
		}
	}
//======================================================================================
	private static double fpart(double x) {
		if (x < 0)
			return (1 - (x - Math.floor(x)));
		else
			return (x - Math.floor(x));
	}
//======================================================================================
	private static double rfpart(double x) {
		return (1 - fpart(x));
	}
//======================================================================================
	// Draws a single Black(!) point at position (x,y) with brightness c (from 0 to
	// 1)
	private static void drawPoint(Graphics2D g, int x, int y, double c) {
		g.setColor(new Color(0f, 0f, 0f, (float) c));
		g.drawLine(x, y, x, y);
	}
//======================================================================================
	public static Node nearestNode(Graph G, double x, double y, double w, double h) {
		w = Math.min(w, h);
		return G.nearestNode((x/w-0.1)/0.8, (y/w-0.1)/0.8);
	}
//======================================================================================	
	public static double distanceToNode(Node n, double x, double y, double w, double h) {
		w = Math.min(w, h);
		double nodeNewX = 0.8 * w * n.x() + 0.1 * w;
		double nodeNewY = 0.8 * w * n.y() + 0.1 * w;
		return Math.sqrt(Math.pow(nodeNewX-x, 2)+Math.pow(nodeNewY-y, 2));
	}

}
