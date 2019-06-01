import java.awt.Color;
public class Node {

	private String name;
	private double[] position;
	private Color c;

	Node(String name_, double[] position_) {
		name = name_;
		position = position_;
		c = Color.BLACK;
	}

	Node(String name_) {
		name = name_;
		position = new double[] {0, 0};
		c = Color.BLACK;
	}

	public String name() {
		return name;
	}

	public double[] position() {
		return position;
	}
	
	public Color color() {
		return c;
	}
	
	public void setColor(Color c) {
		this.c = c;
	}

	public void setPosition(double x, double y) {
		position[0] = x;
		position[1] = y;
	}

	public double x() {
		return position[0];
	}

	public double y() {
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
