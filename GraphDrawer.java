import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public abstract class GraphDrawer {
	private static final int ARR_SIZE = 10;
	
	public static void linearEdges(Graphics g, Graph G) {
		
		ArrayList<Node> nodes = G.nodes();
		ArrayList<int[]> edges = G.edges();
		int radius = getRadius(g, G);
		
		for(Node node: nodes) {
			drawNode(g, node, radius);
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
		
		g.fillOval(n.x() - radius, n.y() - radius, 2 * radius, 2 * radius);

	}
	
	private static int getRadius(Graphics g, Graph G) {
		return 10;
	}
	
	
	
	
}
