
public class RandomEmbedding {

	public static void defineLayout(int width, int height, Graph G) {
		for (Node node : G.nodes()) {
			node.setPosition(Math.random() * width, Math.random() * height);
		}
	};

}
