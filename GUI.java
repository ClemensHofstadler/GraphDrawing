import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.border.BevelBorder;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import javax.swing.JButton;

public class GUI {
//===========================================================================================
//Fields
//===========================================================================================
	Graph G;
	private JFrame frmGraphDrawing;
	private Node markedNode;
//===========================================================================================
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmGraphDrawing.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}
//===========================================================================================
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
//===========================================================================================
//GUI components
//===========================================================================================
		frmGraphDrawing = new JFrame();
		JPanel settingsArea = new JPanel();
		settingsArea.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		JMenuBar menuBar = new JMenuBar();
		JMenu mnLoad = new JMenu("Load");
		JTextArea infoField = new JTextArea();
		infoField.setEditable(false);
		DrawingArea drawingArea = new DrawingArea();
		JMenu mnLayout = new JMenu("Layout");
		JLabel lblSettings = new JLabel("Settings");
		JButton saveButton = new JButton("Save as png");
		JPanel panel = new JPanel();
//===========================================================================================
//Resizing of the components
//===========================================================================================
		 	frmGraphDrawing.getContentPane().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				//get new size
				int width = frmGraphDrawing.getWidth()-12;
				int height = frmGraphDrawing.getHeight() - 56;
				int size = width < height ? width : height;
				//resize settings area
				settingsArea.setBounds(size+12, 6, width-(size+6), height);
				//resize and redraw graph
				drawingArea.setSize(size);
				drawingArea.paint(drawingArea.getGraphics());
			}
		});
		frmGraphDrawing.setTitle("Graph Drawing");
		frmGraphDrawing.setBounds(100, 100, 567, 409);
		frmGraphDrawing.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		
		frmGraphDrawing.setJMenuBar(menuBar);
//===========================================================================================
//Zooming TODO
//===========================================================================================	
		drawingArea.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				double x = e.getPoint().x/(double)drawingArea.sizeDrawing();
				double y = e.getPoint().y/(double)drawingArea.sizeDrawing();
				drawingArea.zoom(e.getPreciseWheelRotation(),x,y);
			}
		});
//===========================================================================================
//Read in a file
//===========================================================================================
		mnLoad.addMouseListener(new MouseAdapter() {
		@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Choose file to be imported");  
				
				int val = fileChooser.showOpenDialog(frmGraphDrawing);
				
				if (val == JFileChooser.APPROVE_OPTION) {
				    File file = fileChooser.getSelectedFile();
				    try (BufferedReader in = new BufferedReader(new FileReader(file))) {
				    	
				    	G = new Graph();
				    	
				    	infoField.setText("");
				        String line = in.readLine();
				        while (line != null) {
				        	int space = line.indexOf(' ');
				        	Node n1 = new Node(line.substring(0, space));
				        	Node n2 = new Node(line.substring(space+1, line.length()));
				        	G.addNode(n1);
				        	G.addNode(n2);
				        	G.addEdge(n1,n2);
				        	infoField.setText(infoField.getText() + line + "\n");
				        	line = in.readLine();	
				        }
						drawingArea.reset();
				        GridEmbedding.defineLayout(G);
				        drawingArea.setGraph(G);
				        drawingArea.setLinearEdges(true);
				        drawingArea.paint(drawingArea.getGraphics());
				            
				    } catch (Exception ex) {
				    	ex.printStackTrace();
				    }
				}
			}
			});
		menuBar.add(mnLoad);
		
		menuBar.add(mnLayout);
//===========================================================================================
//The different layout choices
//===========================================================================================
		JMenuItem mntmRandomEmbedding = new JMenuItem("Random embedding");
		mntmRandomEmbedding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingArea.reset();
				RandomEmbedding.defineLayout(G);
				drawingArea.setGraph(G);
		        drawingArea.setLinearEdges(true);
		        drawingArea.paint(drawingArea.getGraphics());
			}
		});
		mnLayout.add(mntmRandomEmbedding);
		
		JMenuItem mntmGridEmbedding = new JMenuItem("Grid embedding");
		mntmGridEmbedding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingArea.reset();
				GridEmbedding.defineLayout(G);
				drawingArea.setGraph(G);
		        drawingArea.setLinearEdges(true);
		        drawingArea.paint(drawingArea.getGraphics());
			}
		});
		mnLayout.add(mntmGridEmbedding);
		
		JMenuItem mntmLinearEmbedding = new JMenuItem("Linear embedding");
		mntmLinearEmbedding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingArea.reset();
				LinearEmbedding.defineLayout(G);
				drawingArea.setGraph(G);
		        drawingArea.setLinearEdges(false);
		        drawingArea.paint(drawingArea.getGraphics());
			}
		});
		mnLayout.add(mntmLinearEmbedding);
		
		JMenuItem mntmSpectralEmbedding = new JMenuItem("Spectral embedding");
		mntmSpectralEmbedding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingArea.reset();
				SpectralEmbedding.defineLayout(G);
				drawingArea.setGraph(G);
		        drawingArea.setLinearEdges(true);
		        drawingArea.paint(drawingArea.getGraphics());
			}
		});
		mnLayout.add(mntmSpectralEmbedding);
		
		JMenuItem mntmSpringEmbedding = new JMenuItem("Spring embedding");
		mntmSpringEmbedding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingArea.reset();
				AdaptiveSpringEmbedding.defineLayout(G);
				drawingArea.setGraph(G);
		        drawingArea.setLinearEdges(true);
		        drawingArea.paint(drawingArea.getGraphics());
			}
		});
		mnLayout.add(mntmSpringEmbedding);
		
		JMenuItem mntmSpringEmbeddingAnimation = new JMenuItem("Spring embedding (animated)");
		mntmSpringEmbeddingAnimation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingArea.reset();
		        drawingArea.setLinearEdges(true);
				for(int i=1; i<200; i++) {
					if(i>100)
						i+=2;
					AdaptiveSpringEmbedding.defineLayout(G, i);
					drawingArea.setGraph(G);
			        drawingArea.paint(drawingArea.getGraphics());
			        try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		mnLayout.add(mntmSpringEmbeddingAnimation);
		
		JMenuItem mntmCircularEmbedding = new JMenuItem("Circular embedding");
		mntmCircularEmbedding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CircularEmbedding.defineLayout(G);
				drawingArea.setGraph(G);
		        drawingArea.setLinearEdges(true);
		        drawingArea.paint(drawingArea.getGraphics());
			}
		});
		mnLayout.add(mntmCircularEmbedding);
		
		
		
		frmGraphDrawing.getContentPane().setLayout(null);
		drawingArea.setBorder(null);
//===========================================================================================
//Save as picture
//===========================================================================================
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int val = fileChooser.showSaveDialog(frmGraphDrawing);
				if (val == JFileChooser.APPROVE_OPTION) 
					drawingArea.save(fileChooser.getSelectedFile());
			}
		});
		saveButton.setBounds(45, 40, 117, 29);
		panel.add(saveButton);
//===========================================================================================
//Auxiliary functions
//===========================================================================================
		drawingArea.setBounds(6, 6, 350, 350);
		frmGraphDrawing.getContentPane().add(drawingArea);
		drawingArea.setLayout(new BorderLayout(0, 0));
		
		settingsArea.setBounds(360, 6, 200, 350);
		frmGraphDrawing.getContentPane().add(settingsArea);
		settingsArea.setLayout(new GridLayout(0, 1, 0, 0));
		lblSettings.setHorizontalAlignment(SwingConstants.CENTER);
		lblSettings.setHorizontalTextPosition(SwingConstants.CENTER);
		settingsArea.add(lblSettings);
		
		infoField.setLineWrap(true);
		infoField.setWrapStyleWord(true);
		infoField.setText("Click on a node for more information");
		settingsArea.add(infoField);
		
		settingsArea.add(panel);
		panel.setLayout(null);
		
//===========================================================================================
//Functionality to click on nodes
//===========================================================================================
		drawingArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(G != null && !G.nodes().isEmpty()) {
					Point pt = e.getPoint();					
					int w = drawingArea.getWidth();
					int h = drawingArea.getHeight();
					Node n = GraphDrawer.nearestNode(G, pt.getX(), pt.getY(), w, h);
					double distance = GraphDrawer.distanceToNode(n, pt.getX(), pt.getY(), w, h);
					Graphics g = drawingArea.getGraphics();
							
					if(markedNode != null) {
						GraphDrawer.unmarkNode(g, w, h, markedNode);
						//GraphDrawer.unmarkAdjacentNodes(g, w, h, markedNode, G);
					}
							
					if(distance < 12) {
						GraphDrawer.markNode(g, w, h, n);
						//GraphDrawer.markAdjacentNodes(g, w, h, n, G);
						markedNode = n;
						String text = "Node name: " + n.name() + "\n";
						String out = "";
						for(int i: G.outEdges(n)) {
							if(out != "")
							out += ", ";
							out += G.nodes().get(i).name();
						}
						text += "Outgoing edges to: " + out + "\n";
						String in = "";
						for(int i: G.inEdges(n)) {
							if(in != "")
							in += ", ";
							in += G.nodes().get(i).name();
						}
						text += "Incoming edges from: " + in + "\n";
						infoField.setText(text);					
					}
					else {
						System.out.println("Clicked outside of graph");
					}
			}
		}
		});
	}
}
