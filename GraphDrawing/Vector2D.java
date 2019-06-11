package GraphDrawing;
public class Vector2D {
	private double x;
	private double y;

	public Vector2D() {
		x = 0.;
		y = 0.;
	}

	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void setXY(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double[] getCoordinates() {
		double[] f = new double[] { x, y };
		return f;
	}

	public void add(Vector2D v) {
		x += v.x;
		y += v.y;
	}
	
	public void multiply(double c) {
		x *= c;
		y *= c;
	}

	public double length() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

}
