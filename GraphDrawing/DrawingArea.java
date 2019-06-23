package GraphDrawing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Graph.Graph;

/**
 * Subclass of the JPanel class. A drawing area on which graphs can be drawn.
 * 
 * @author Clemens Hofstadler, Lukas Wögerer
 * @version 1.0.0, 1st June 2019
 *
 */
public class DrawingArea extends JPanel {
	/**
	 * Size of the drawing area in pixel.
	 */
	private int size;
	/**
	 * The graph to be drawn on the drawing area.
	 */
	private Graph G;
	/**
	 * Boolean value determining whether the current layout of the graph requires
	 * linear edges or semicircular edges.
	 */
	private boolean linearEdges;
	/**
	 * Zooming factor
	 */
	private double zoom;
	/**
	 * x-coordinate of the point which is fixed during the zoom (coordinate is given
	 * scaled to the unit square)
	 */
	private double x;
	/**
	 * y-coordinate of the point which is fixed during the zoom (coordinate is given
	 * scaled to the unit square)
	 */
	private double y;

	/**
	 * x-coordinate of the point which is used as a reference during moving the
	 * image by click and drag
	 */
	private double xMoveReference;
	/**
	 * y-coordinate of the point which is used as a reference during moving the
	 * image by click and drag
	 */
	private double yMoveReference;
	/**
	 * x-coordinate of the point where the image was at the current session of the
	 * click-and drag-movement.
	 */
	private double xMoveCurrentMousePosition;
	/**
	 * y-coordinate of the point where the image was at the current session of the
	 * click-and drag-movement.
	 */
	private double yMoveCurrentMousePosition;

	/**
	 * Determines whether the currently displayed layout is 3D. This enables
	 * rotations.
	 */
	private boolean threeDLayout;

//=======================================================================
// Constructor
//=======================================================================		
	/**
	 * Default constructor. Initializes an empty graph, sets the zoom to 1 and the
	 * initial position to the origin (upper left corner)
	 */
	public DrawingArea() {
		super();
		G = null;
		zoom = 1;
		x = y = 0;
		xMoveReference = yMoveReference = 0;
		threeDLayout = false;
	}

//=======================================================================
// Getters for some fields
//=======================================================================	
	/**
	 * Returns the current size of the drawing area.
	 * 
	 * @return The current size of the drawing area.
	 */
	public int sizeDrawing() {
		return size;
	}

	/**
	 * Returns whether the currently displayed layout is 3D.
	 * 
	 * @return The value of the field "threeDLayout".
	 */
	public boolean threeDLayout() {
		return threeDLayout;
	}

//=======================================================================
// Setters for some fields
//=======================================================================	
	/**
	 * Changes the size of the drawing area to s.
	 * 
	 * @param s Non-negative integer. The new size of the drawing area.
	 */
	public void setSize(int s) {
		size = s;
		super.setSize(size, size);
	}

	/**
	 * Sets the graph which will be drawn on the drawing area.
	 * 
	 * @param G The graph which will be drawn on the drawing area.
	 */
	public void setGraph(Graph G) {
		this.G = G;
	}

	/**
	 * Sets the boolean value of the field linearEdges.
	 * 
	 * @param b The new value of the field linearEdges.
	 */
	public void setLinearEdges(boolean b) {
		linearEdges = b;
	}

	/**
	 * Sets the boolean value of the field threeDLayout.
	 * 
	 * @param b The new value of the field threeDLayout.
	 */
	public void setThreeDLayout(boolean b) {
		threeDLayout = b;
	}

//=======================================================================
// Zooming related functions
//=======================================================================		
	/**
	 * Resets the zoom to 1 and the position to the origin.
	 */
	public void reset() {
		x = y = 0;
		zoom = 1;
	}

	/**
	 * Given an x-coordinate of the current displayed (possibly zoomed in) graph
	 * section, this method returns the x-coordinate w.r.t. the whole graph. Note
	 * that the x-coordinate has to be given scaled down to [0,1].
	 * 
	 * @param x0 x-coordinate of a point from the currently shown graph section but
	 *           scaled down to [0,1].
	 * 
	 * @return x-coordinate w.r.t. the whole graph of the points which are currently
	 *         shown at a position having x-coordinate x0.
	 */
	public double realX(double x0) {
		return (x0 - (-zoom * x + x) * size) / zoom;
	}

	/**
	 * Given a y-coordinate of the current displayed (possibly zoomed in) graph
	 * section, this method returns the y-coordinate w.r.t. the whole graph. Note
	 * that the y-coordinate has to be given scaled down to [0,1].
	 * 
	 * @param y0 y-coordinate of a point from the currently shown graph section but
	 *           scaled down to [0,1].
	 * 
	 * @return y-coordinate w.r.t. the whole graph of the points which are currently
	 *         shown at a position having y-coordinate y0.
	 */
	public double realY(double y0) {
		return (y0 - (-zoom * y + y) * size) / zoom;
	}

	/**
	 * This method handles the zooming. The double value d determines whether the
	 * user wants to zoom in or out (d > 0 => zoom in; d < 0 => zoom out). During
	 * this zooming process the point (x0,y0) will be fixed.
	 * 
	 * @param d  Determines whether the user wants to zoom in our out.
	 * @param x0 x-coordinate of the fixed point of the zoom scaled to [0,1].
	 * @param y0 y-coordinate of the fixed point of the zoom scaled to [0,1],
	 */
	public void zoom(double d, double x0, double y0) {
		// if we have already completely zoomed out
		// but want to zoom out further, do nothing
		if (d < 0 && d == 1)
			return;
		// zooming in
		if (d > 0) {
			double dx = x0 - this.x;
			double dy = y0 - this.y;
			x += dx;
			y += dy;

			zoom *= 1.08;
		}
		// zooming out
		else if (d < 0)
			zoom /= 1.08;

		// zooming factor has to be at least 1
		if (zoom < 1) {
			zoom = 1;
		}

		// repaint everything
		paint(getGraphics());
	}

//=======================================================================
// Click & drag related functions
//=======================================================================		
	/**
	 * Saves the position of the first click of the mouse somewhere inside the
	 * drawing area
	 * 
	 * @param ptX x-coordinate of the position where the mouse was clicked but
	 *            scaled down to the unit square.
	 * @param ptY y-coordinate of the position where the mouse was clicked but
	 *            scaled down to the unit square.
	 */
	public void firstClick(double ptX, double ptY) {
		xMoveReference = ptX;
		yMoveReference = ptY;
		xMoveCurrentMousePosition = x;
		yMoveCurrentMousePosition = y;
	}

	/**
	 * Moves the currently displayed picture to the position (xNew,yNew).
	 * 
	 * @param xNew New x-coordinate.
	 * @param yNew New y-coordinate.
	 */
	public void move(double xNew, double yNew) {
		double dx = xMoveReference - xNew;
		double dy = yMoveReference - yNew;
		x = xMoveCurrentMousePosition + dx / zoom;
		y = yMoveCurrentMousePosition + dy / zoom;
		paint(getGraphics());
	}

	/**
	 * Overriden paint method. Paints the currently saved graph G on the graphics
	 * object g using the current zoom which fixes the currently saved point (x,y).
	 * If linearEdges is set to true, the graph will be drawn having linear edges.
	 * Otherwise edges are drawn as semicircles.
	 * 
	 * @param g The graphics object on which the graph will be drawn.
	 */
	@Override
	public void paint(Graphics g) {
		if (G != null) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setBackground(Color.WHITE);
			g2d.translate((-zoom * x + x) * size, (-zoom * y + y) * size);
			super.paint(g2d);
			GraphDrawer.drawGraph(g2d, G, (int) (size * zoom), linearEdges, threeDLayout);
		}
	}

	/**
	 * Saves a picture of the currently drawn graph (including any zoom or maked
	 * nodes) in a png-file.
	 * 
	 * @param f The file in which the picture will be saved.
	 */
	public void save(File f) {
		try {
			BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D pic = image.createGraphics();
			pic.setBackground(Color.WHITE);
			pic.setColor(Color.BLACK);
			paint(pic);
			pic.dispose();
			ImageIO.write(image, "PNG", new File(f.getAbsolutePath() + ".png"));
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
}
