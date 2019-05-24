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
	public double x = 0;
	public double y = 0; 
	public double x0 = 0;
	public double y0 = 0;
	
	public DrawingArea() {
		super();
		G = null;
		zoom = 1;
	}
	
	public void setSize(int s) {
		size = s;
		super.setSize(size, size);
	}
	
	public int sizeDrawing() {return size;}
	
	public void setGraph(Graph G) {this.G = G;}
	
	public void setLinearEdges(boolean b) {linearEdges = b;}
	
	public void reset() {
		x = 0;
		y = 0;
		zoom = 1;
	}
	
	public void zoom(double d,double x0, double y0) {
		if(d > 0)
			zoom *= 1.1;
		else if(d < 0)
			zoom /= 1.1;
		
		if(zoom < 1)
			zoom = 1;
		
		double dx = x0 - this.x;
		double dy = y0 - this.y;
		
		x += dx/zoom;
		y += dy/zoom;
		
		this.x0 = x0;
		this.y0 = y0;
		
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
			g2d.translate(-(-zoom*x+x)*size, -(-zoom*y+y)*size);
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
