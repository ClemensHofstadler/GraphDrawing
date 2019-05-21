
public class GridEmbedding {
	
	public static void defineLayout(int width, int height, Graph G) {
		defineLayout(width, height, G, 2);
	};
	
	public static void defineLayout(int width, int height, Graph G, int nodesPerRow) {
		int nodesPerColumn = G.nodes().size()/nodesPerRow;
		if(G.nodes().size()%nodesPerRow>0)
			nodesPerColumn++;
		
		double dx = (width/(nodesPerRow+1.));
		double dy = (height/(nodesPerColumn+1.));
		
		int row = 1;
		int column = 1;
		
		for(Node node: G.nodes()) {
			System.out.println(node.name());
			node.setPosition((int) (row*dx), (int) (column*dy));
			row++;
			if(row > nodesPerRow) {
				row = 1;
				column++;
			}
		}
		
		scaleToUnitSquare(G);
	};
	
	private static void scaleToUnitSquare(Graph G){
		
		double minX = G.nodes().get(0).x();
		for (Node node : G.nodes())
			if (node.x() < minX)
				minX = node.x();
		double maxX = G.nodes().get(0).x();
		for (Node node : G.nodes())
			if (node.x() > maxX)
				maxX = node.x();
		double minY = G.nodes().get(0).y();
		for (Node node : G.nodes())
			if (node.y() < minY)
				minY = node.y();
		double maxY = G.nodes().get(0).y();
		for (Node node : G.nodes())
			if (node.y() > maxY)
				maxY = node.y();
		
		double dx = maxX-minX;
		double dy = maxY-minY;
		
		if(dx>dy)
			dy=dx;
		else
			dx=dy;
		
		for(Node node: G.nodes()) {
			double newX = node.x();
			double newY = node.y();
			
			newX-=minX;
			newX/=dx;
			newY-=minY;
			newY/=dy;
			
			node.setPosition(newX, newY);
		}
	}
	
}
