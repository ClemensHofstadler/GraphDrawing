package Embeddings;
import Graph.Graph;
import Graph.Node;

public class GridEmbedding {

	public static void defineLayout(Graph G) {
		int n = 1 + (int) Math.sqrt(G.nodes().size());
		defineLayout(G, n);
	};

	public static void defineLayout(Graph G, int nodesPerRow) {
		int size = G.nodes().size();

		// Special case: only one node
		if (size == 1) {
			G.nodes().get(0).setPosition(0.5, 0.5);
			return;
		}

		// Special case: one node per row
		if (nodesPerRow == 1) {
			int row = 1;
			double dy = 1. / (size - 1);
			for (Node node : G.nodes()) {
				node.setPosition(0.5, (row - 1) * dy);
				row++;
			}
			return;
		}

		// Special case: all nodes in one row
		if (nodesPerRow >= size) {
			LinearEmbedding.defineLayout(G);
			return;
		}

		int nodesPerColumn = G.nodes().size() / nodesPerRow;
		if (G.nodes().size() % nodesPerRow > 0)
			nodesPerColumn++;

		double dx = (1. / (nodesPerRow - 1.));
		double dy = (1. / (nodesPerColumn - 1.));
		if (dx > dy)
			dx = dy;

		int column = 1;
		int row = 1;

		for (Node node : G.nodes()) {
			node.setPosition((column - 1) * dx, (row - 1) * dx);
			column++;
			if (column > nodesPerRow) {
				column = 1;
				row++;
			}
		}
	};
}
