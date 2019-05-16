import java.awt.Graphics;

public class Node {

	private String name;
	private int[] position;

	Node(String name_, int[] position_) {
		name = name_;
		position = position_;
	}

	Node(String name_) {
		name = name_;
		position = new int[] {0, 0};
	}

	public String name() {
		return name;
	}

	public int[] position() {
		return position;
	}

	public void setPosition(int x, int y) {
		position[0] = x;
		position[1] = y;
	}

	public int x() {
		return position[0];
	}

	public int y() {
		return position[1];
	}

	@Override
	public boolean equals(Object o) {
		boolean result = false;

		if (o instanceof Node) {
			Node n = (Node) o;
			result = (n.name().equals(this.name));
		}

		return result;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
