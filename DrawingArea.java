import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class DrawingArea extends JPanel{
	
	private int size;
	private boolean linearEdges;
	private Graph G;
	public double zoom;
	public double x;
	public double y;
	
	public double xMoveReference;
	public double xMoveReferenceScaled;
	public double yMoveReference;
	public double yMoveReferenceScaled;
	/**
	 * Default constructor. Initializes an empty graph,
	 * sets the zoom to 1 and the initial position
	 * to the origin (upper left corner)
	 */
	public DrawingArea() {
		super();
		G = null;
		zoom = 1;
		x = y = 0;
		xMoveReference = yMoveReference = 0;
	}
	
	/**
	 * Changes the size of the drawing area to s.
	 * @param s Non-negative integer. The new size of
	 * the drawing area.
	 */
	public void setSize(int s) {
		size = s;
		super.setSize(size, size);
	}
	/**
	 * Returns the current size of the drawing area.
	 * @return The current size of the drawing area.
	 */
	public int sizeDrawing() {return size;}
	
	/**
	 * Sets the graph which will be drawn on the drawing area.
	 * @param G The graph which will be drawn on the drawing area.
	 */
	public void setGraph(Graph G) {this.G = G;}
	
	/**
	 * Sets the boolean value of the field linearEdges.
	 * @param b The new value of the field linearEdges.
	 */
	public void setLinearEdges(boolean b) {linearEdges = b;}
	
	/**
	 * Resets the zoom to 1 and the position to the
	 * origin.
	 */
	public void reset() {
		x = y = 0;
		zoom = 1;
	}
	
	public double realX(double x0) {
		return (x0 - (-zoom*x+x)*size)/zoom; 
	}
	
	public double realY(double y0) {
		return (y0 - (-zoom*y+y)*size)/zoom; 
	}
	
	public void zoom(double d,double x0, double y0) {
		if(d > 0) {
			double dx = x0 - this.x;
			double dy = y0 - this.y;
			x += dx;
			y += dy;
			
			zoom *= 1.08;
		}
		else if(d < 0)
			zoom /= 1.08;
		
		if(zoom < 1) {
			zoom = 1;
		}
	
		paint(getGraphics());
	}
	
	public void move(double xNew, double yNew) {
		double dx = xMoveReference - xNew;
		double dy = yMoveReference - yNew;
		x = xMoveReferenceScaled + dx/zoom;
		y = yMoveReferenceScaled + dy/zoom;
		paint(getGraphics());
	}
	
	@Override
	public void paint(Graphics g) {
		if(G != null) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.clearRect(0, 0, size, size);
			g2d.translate((-zoom*x+x)*size, (-zoom*y+y)*size);
			super.paint(g2d);
			GraphDrawer.drawGraph(g2d,G,(int)(size*zoom),linearEdges);
		}
	}
	
	public void save(File f) {
		try {
			BufferedImage image = new BufferedImage(size,size, BufferedImage.TYPE_INT_ARGB);
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
