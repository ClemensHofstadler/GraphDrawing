import java.util.ArrayList;

public class CircularEmbedding {
	
	public static void defineLayout(Graph G) {
		ArrayList<Node> nodes = G.nodes();
		double n = 2*Math.PI/nodes.size();
		for(int i = 0; i < nodes.size(); i++)
			nodes.get(i).setPosition(0.5 + 0.5*Math.cos(i*n),0.5 + 0.5*Math.sin(i*n));
	}
		
}
