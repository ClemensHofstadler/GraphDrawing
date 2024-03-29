package Graph;

import java.util.ArrayList;

/**
 * Extends the class Node in a way that Nodes can be positioned in 3 dimensional
 * space. It provides functions for projecting those 3 dimensional coordinates
 * onto a 2D plane. This plane is determined by the two vectors X and Y (which
 * are always normal to each other) and the origin of the coordinate system.
 * This plane can also be rotated in different directions. When such a
 * projection is done, also the distance of the point two the specified 2D plane
 * is stored.
 * 
 * @author Lukas W&oumlgerer
 * @version 1.0.2, 23rd June 2019
 */
public class Node3D extends Node {

	/**
	 * Positions of node in three dimensions.
	 */
	private double[] position3D;
	/**
	 * A number that corresponds to the distance of this node to a plane determined
	 * by the two vectors X and Y (which are always normal to each other) and the
	 * origin of the coordinate system.
	 */
	private double distance;

	/**
	 * The two vectors X and Y (which are always normal to each other) and the
	 * origin of the coordinate system determine a plane lying in three dimensional
	 * space.
	 */
	private static Vector X = new Vector(3, new double[] { 0, 1, 0 });
	/**
	 * The two vectors X and Y (which are always normal to each other) and the
	 * origin of the coordinate system determine a plane lying in three dimensional
	 * space.
	 */
	private static Vector Y = new Vector(3, new double[] { 0, 0, 1 });

	/**
	 * See constructor Node(String name).
	 * 
	 * @param name Name of the node.
	 */
	public Node3D(String name) {
		super(name);
	}

	/**
	 * Constructor for a 3D node with a specific name, coordinates within the unit
	 * square, and coordinates within the 3 dimensional real space.
	 * 
	 * @param name  Name of the node.
	 * @param pos   Position of the node within the unit square.
	 * @param pos3d 3 dimensional position of the node
	 */
	public Node3D(String name, double[] pos, double[] pos3d) {
		super(name, pos);
		position3D = pos3d;
	}

	/**
	 * Setter for 3 dimensional position of this node.
	 * 
	 * @param pos3d The new position of this node.
	 */
	public void setPosition3D(double[] pos3d) {
		position3D = pos3d;
	}

	/**
	 * Getter for 3 dimensional position of this node.
	 * 
	 * @return double[] The three dimensional position of this node.
	 */
	public double[] getposition3D() {
		return position3D;
	}

	public double getDistance() {
		return distance;
	}

	/**
	 * Projects the 3D point onto a 2D plane determined by the two vectors X and Y
	 * (which are always normal to each other) and the origin of the coordinate
	 * system. This projection will be stored inside the 2D positions of the nodes.
	 * It also calculates for each node the distance to the specified 2D plane.
	 * Positions on the 2D plane are scaled such that all 2D coordinates of the
	 * nodes lie within the unit square.
	 * 
	 * @param G A Graph
	 */
	public static void project3DPoints(Graph G) {
		Vector normalVector = Vector.crossProduct(X, Y);
		for (int i = 0; i < G.nodes().size(); i++) {
			Vector pos = new Vector(3, ((Node3D) G.nodes().get(i)).getposition3D());
			double newX = Vector.dotProduct(X, pos);
			double newY = Vector.dotProduct(Y, pos);
			G.nodes().get(i).setPosition(newX, newY);

			((Node3D) G.nodes().get(i)).distance = -Vector.dotProduct(normalVector, pos);
		}

		double[] pos = ((Node3D) G.nodes().get(0)).position3D;
		double xMin = pos[0];
		double xMax = pos[0];
		double yMin = pos[1];
		double yMax = pos[1];
		double zMin = pos[2];
		double zMax = pos[2];
		double maxDist = ((Node3D) G.nodes().get(0)).distance;

		for (int i = 1; i < G.nodes().size(); i++) {
			pos = ((Node3D) G.nodes().get(i)).position3D;
			if (pos[0] < xMin)
				xMin = pos[0];
			if (pos[0] > xMax)
				xMax = pos[0];
			if (pos[1] < yMin)
				yMin = pos[1];
			if (pos[1] > yMax)
				yMax = pos[1];
			if (pos[2] < zMin)
				zMin = pos[2];
			if (pos[2] > zMax)
				zMax = pos[2];
			if (((Node3D) G.nodes().get(i)).distance > maxDist)
				maxDist = ((Node3D) G.nodes().get(i)).distance;
		}

		double reference = Math.sqrt(Math.pow(xMax - xMin, 2) + Math.pow(yMax - yMin, 2) + Math.pow(zMax - zMin, 2));
		for (int i = 0; i < G.nodes().size(); i++) {
			double dist = ((Node3D) G.nodes().get(i)).distance;
			double newDist = 0;
			if(reference!=0)
				newDist = 1 - (maxDist - dist) / reference;
			((Node3D) G.nodes().get(i)).distance = newDist;
		}

		scale(G);
	}

	/**
	 * Rotates the 2D plane specified by the two vectors X and Y (which are always
	 * normal to each other) and the origin of the coordinate system. It also
	 * recalculates all positions of all nodes relative to the new 2D plane.
	 * 
	 * @param c Keyboard symbol that got hit
	 * @param G A Graph
	 */
	public static void rotate(String c, Graph G) {
		if (c.equals("d") || c.equals("D") || c.equals("a") || c.equals("A")) {
			double alpha = 0.05;
			if (c.equals("a") || c.equals("A"))
				alpha = -0.05;

			Vector xRot = new Vector(3, X.coordinates());
			xRot.multiply(Math.cos(alpha));
			Vector dummy = Vector.crossProduct(Y, X);
			dummy.multiply(Math.sin(alpha));
			xRot.add(dummy);
			dummy.setCoordinates(Y.coordinates());
			dummy.multiply(Vector.dotProduct(Y, X) * (1 - Math.cos(alpha)));
			xRot.add(dummy);

			X.setCoordinates(xRot.coordinates());
		}

		if (c.equals("w") || c.equals("W") || c.equals("s") || c.equals("S")) {
			double alpha = 0.05;
			if (c.equals("s") || c.equals("S"))
				alpha = -0.05;

			Vector yRot = new Vector(3, Y.coordinates());
			yRot.multiply(Math.cos(alpha));
			Vector dummy = Vector.crossProduct(X, Y);
			dummy.multiply(Math.sin(alpha));
			yRot.add(dummy);
			dummy.setCoordinates(X.coordinates());
			dummy.multiply(Vector.dotProduct(X, Y) * (1 - Math.cos(alpha)));
			yRot.add(dummy);

			Y.setCoordinates(yRot.coordinates());
		}

		project3DPoints(G);
	}

	/**
	 * Positions on the 2D plane are scaled such that all 2D coordinates of the
	 * nodes lie within the unit square.
	 * 
	 * @param G A Graph
	 */
	public static void scale(Graph G) {
		ArrayList<Node> nodes = G.nodes();

		double minX = nodes.get(0).x();
		for (Node node : nodes)
			if (node.x() < minX)
				minX = node.x();
		double maxX = nodes.get(0).x();
		for (Node node : nodes)
			if (node.x() > maxX)
				maxX = node.x();
		double minY = nodes.get(0).y();
		for (Node node : nodes)
			if (node.y() < minY)
				minY = node.y();
		double maxY = nodes.get(0).y();
		for (Node node : nodes)
			if (node.y() > maxY)
				maxY = node.y();

		double dx = maxX - minX;
		double dy = maxY - minY;
		if (dx < dy)
			dx = dy;

		for (Node node : nodes) {
			double newX = ((node.x() - minX) / dx);
			double newY = ((node.y() - minY) / dx);
			node.setPosition(newX, newY);
		}
	}

}
