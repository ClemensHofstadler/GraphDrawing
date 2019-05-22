
public class RandomEmbedding {
	
	public static void defineLayout(Graph G) {
		for(Node node: G.nodes()) {
			node.setPosition(Math.random(),Math.random());
		}
	};
	
}