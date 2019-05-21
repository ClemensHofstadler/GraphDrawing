import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public abstract class GraphDrawer {
	private static final int ARR_SIZE = 10;
	
	public static void linearEdges(Graphics g, int w, int h, Graph G) {
		
		g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.BLACK);
		
		ArrayList<Node> nodes = G.nodes();
		ArrayList<int[]> edges = G.edges();
		int radius = getRadius(g, G);
		
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
		
		for(Node node: nodes) {
			drawNode(g, node, radius);
			System.out.println("(" + node.x() + "," + node.y() + ")");
			
		}
		for(int[] edge: edges) {
				Graphics2D g1 = (Graphics2D) g.create();
				int x1 = (int)nodes.get(edge[0]).x();
				int y1 = (int)nodes.get(edge[0]).y();
				int x2 = (int)nodes.get(edge[1]).x();
				int y2 = (int)nodes.get(edge[1]).y();
			
				int dx = x2 - x1, dy = y2 - y1;
                double angle = Math.atan2(dy, dx);
                int len = (int) Math.sqrt(dx*dx + dy*dy)-radius;
                AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
                at.concatenate(AffineTransform.getRotateInstance(angle));
                g1.transform(at);

                g1.drawLine(0, 0, len, 0);
                g1.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
                              new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
          }
	};
	
	public static void circularEdges(Graphics g, Graph G) {
		
	};
	
	private static void drawNode(Graphics g, Node n, int radius) {
		
		g.fillOval((int)n.x() - radius, (int)n.y() - radius, 2 * radius, 2 * radius);

	}
	
	private static int getRadius(Graphics g, Graph G) {
		return 10;
	}
	
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
	
	private static double fpart(double x) {
		if (x < 0)
			return (1 - (x - Math.floor(x)));
		else
			return (x - Math.floor(x));
	}

	private static double rfpart(double x) {
		return (1 - fpart(x));
	}
	
	// Draws a single Black(!) point at position (x,y) with brightness c (from 0 to 1)
	private static void drawPoint(Graphics2D g, int x, int y, double c) {
		g.setColor(new Color(0f, 0f, 0f, (float) c));
		g.drawLine(x, y, x, y);
	}
	
	
	
}
