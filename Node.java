import java.awt.Graphics;

public class Node {
	
	private String name;
	private int[] position;
	private int radius;
	
	Node(String name_, int[] position_){
		name = name_;
		position = position_;
		radius = 10;
	}
	
	Node(String name_){
		name = name_;
		position = new int[] {(int)(Math.random()*300),(int)(Math.random()*300)};
		radius = 10;
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
	
	public int radius() {
		return radius;
	}
	
	public void setRadius(int r) {
		radius = r;
	}
	
	public int x(){
		return position[0];
	}
	
	public int y(){
		return position[1];
	}
	
	public void draw(Graphics g) {
		g.fillOval(position[0]-radius, position[1]-radius, 2*radius, 2*radius);
		
	}
	
	@Override
	public boolean equals(Object o) {
		boolean result = false;

		if (o instanceof Node){
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
