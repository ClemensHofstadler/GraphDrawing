package Graph;
import java.awt.Color;
/**
 * Class to represent the nodes of a graph. Each node has a name (given
 * as a string), a position within the unit square and a color.
 * 
 * @author Clemens Hofstadler
 * @version 1.0.0, 1st June 2019
 *
 */
public class Node {
	/**
	 * The name of the node as given in the input txt-file.
	 */
	private String name;
	/**
	 * The position of the node within the unit square.
	 */
	private double[] position;
	/**
	 * The color of the node.
	 */
	private Color c;
//=======================================================================
// Constructors
//=======================================================================
	/**
	 * Constructor for a node with given name and position. This node will
	 * be drawn black.
	 * 
	 * @param name Name of the node.
	 * @param position Position of the node within the unit square.
	 */
	public Node(String name, double[] position) {
		this.name = name;
		this.position = position;
		c = Color.BLACK;
	}

	/**
	 * Constructor for a node with given name. This node will
	 * be positioned at the origin and drawn black.
	 * 
	 * @param name Name of the node.
	 */
	public Node(String name) {
		this.name = name;
		position = new double[] {0, 0};
		c = Color.BLACK;
	}
//=======================================================================
// Getter for the fields
//=======================================================================
	/**
	 * Getter for the name of the node. 
	 * @return The name of the node. 
	 */
	public String name() {
		return name;
	}
	
	/**
	 * Getter for the position of the node. 
	 * @return The position of the node. 
	 */
	public double[] position() {
		return position;
	}
	
	/**
	 * Getter for the x-coordinate of the node.
	 * @return The x-coordinate of the node.
	 */
	public double x() {
		return position[0];
	}

	/**
	 * Getter for the y-coordinate of the node.
	 * @return The y-coordinate of the node.
	 */
	public double y() {
		return position[1];
	}
	
	/**
	 * Getter for the color of the node. 
	 * @return The color of the node. 
	 */
	public Color color() {
		return c;
	}
//=======================================================================
// Setter for some fields
//=======================================================================
	/**
	 * Setter for the color of the node.
	 * @param c The new color of the node.
	 */
	public void setColor(Color c) {
		this.c = c;
	}
	/**
	 * Setter for the position of the node within the unit square.
	 * @param x New x-coordinate of the node.
	 * @param y New y-coordinate of the node.
	 */
	public void setPosition(double x, double y) {
		position[0] = x;
		position[1] = y;
	}
//=======================================================================
// Equality of nodes
//=======================================================================
	/**
	 * Tests equality of a node with another object (intended to be
	 * another node). Two nodes are equal if and only if they have the
	 * same name.
	 *  
	 * @param o Another object with which the node is compared. Intended
	 * to be a node as well.
	 *  
	 * @return True if and only if the two compared nodes have the same name;
	 * False otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		boolean result = false;

		if (o instanceof Node) {
			Node n = (Node) o;
			result = (n.name().equals(this.name));
		}
		return result;
	}

	/**
	 * Overriding hashCode method. Just calls the hashCode 
	 * method of the name of the node.
	 * 
	 * @return The result of name.hashCode().
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}
}
