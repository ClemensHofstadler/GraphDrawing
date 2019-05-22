import java.util.ArrayList;

public class LinearEmbedding {
	
	public static void defineLayout(Graph G) {
		ArrayList<Node> nodes = G.nodes();
		double n = nodes.size();
		for(int i = 0; i < nodes.size(); i++) {
			nodes.get(i).setPosition(i/n, 0.5);
		}
	}

}
