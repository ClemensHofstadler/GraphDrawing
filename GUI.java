import java.awt.EventQueue;
import java.awt.Point;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.FlowLayout;

/**
 * Graphical user interface to draw graphs in different layouts. Graphs are
 * loaded from a txt-file and can be visualized with the following layouts:
 * <ul>
 * <li>{@link RandomEmbedding RandomEmbedding}
 * <li>{@link SpectralEmbedding SpectralEmbedding}
 * <li>{@link LinearEmbedding LinearEmbedding}
 * <li>{@link CircularEmbedding CircularEmbedding}
 * <li>{@link SpringEmbedding AdaptiveSpringEmbedding}
 * <li>{@link GridEmbedding GridEmbedding}
 * </ul>
 * 
 * @author Clemens Hofstadler, Lukas Wögerer
 * @version 1.0.0, 31st May 2019
 *
 */
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
	 * 
	 * @param args Can be ignored.
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
		JScrollPane scrollPane = new JScrollPane(infoField);
		DrawingArea drawingArea = new DrawingArea();
		JMenu mnLayout = new JMenu("Layout");
		JLabel lblSettings = new JLabel("Settings");
		JButton saveButton = new JButton("Save as png");
		JButton randomButton = new JButton("Random start position");
		JButton gridLayoutButton = new JButton("Grid as start position");
		JPanel panel = new JPanel();
//===========================================================================================
//Resizing of the components
//===========================================================================================
		frmGraphDrawing.getContentPane().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				// get new size
				int width = frmGraphDrawing.getWidth() - 12;
				int height = frmGraphDrawing.getHeight() - 56;
				int size = width < height ? width : height;
				// resize settings area
				settingsArea.setBounds(size + 12, 6, width - (size + 6), height);
				// resize and redraw graph
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
				double x = e.getPoint().x / (double) drawingArea.sizeDrawing();
				double y = e.getPoint().y / (double) drawingArea.sizeDrawing();
				drawingArea.zoom(e.getPreciseWheelRotation(), x, y);
			}
		});
		
		//registrates first click and saves it
		drawingArea.addMouseListener(new MouseAdapter() {
			@Override
		    public void mousePressed(MouseEvent e) {
				Point pt = e.getPoint();
		        drawingArea.xMoveReference = (pt.getX()/ (double) drawingArea.sizeDrawing());
		        drawingArea.yMoveReference = (pt.getY()/ (double) drawingArea.sizeDrawing());
		        drawingArea.xMoveReferenceScaled = drawingArea.x;
		        drawingArea.yMoveReferenceScaled = drawingArea.y;
		    } 
		});
		
		drawingArea.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point pt = e.getPoint();
				double size = (double)drawingArea.sizeDrawing();
				drawingArea.move(pt.getX()/size, pt.getY()/size);
			}
			@Override
			public void mouseMoved(MouseEvent e) {}
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
							Node n2 = new Node(line.substring(space + 1, line.length()));
							G.addNode(n1);
							G.addNode(n2);
							G.addEdge(n1, n2);
							infoField.setText(infoField.getText() + line + "\n");
							line = in.readLine();
						}
						drawingArea.reset();
						GridEmbedding.defineLayout(G);
						drawingArea.setGraph(G);
						drawingArea.setLinearEdges(true);
						drawingArea.paint(drawingArea.getGraphics());
						randomButton.setVisible(false);
						gridLayoutButton.setVisible(false);

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
				randomButton.setVisible(false);
				gridLayoutButton.setVisible(false);
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
				randomButton.setVisible(false);
				gridLayoutButton.setVisible(false);
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
				randomButton.setVisible(false);
				gridLayoutButton.setVisible(false);
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
				randomButton.setVisible(false);
				gridLayoutButton.setVisible(false);
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
				randomButton.setVisible(true);
				gridLayoutButton.setVisible(true);
				drawingArea.reset();
				AdaptiveSpringEmbedding.defineLayout(G, 0);
				drawingArea.setGraph(G);
				drawingArea.setLinearEdges(true);
				drawingArea.paint(drawingArea.getGraphics());
			}
		});
		mnLayout.add(mntmSpringEmbedding);

		JMenuItem mntmSpringEmbeddingAnimation = new JMenuItem("Spring embedding (animated)");
		mntmSpringEmbeddingAnimation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*drawingArea.reset();
				drawingArea.setLinearEdges(true);

				GridEmbedding.defineLayout(G);// Random or Grid
				drawingArea.setGraph(G);
				drawingArea.paint(drawingArea.getGraphics());

				AnimatedSpringEmbedding animation = new AnimatedSpringEmbedding(G);
				
				Thread thread = new Thread() {
					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						int iter = 0;
						while (!animation.converged && iter < AdaptiveSpringEmbedding.maximumIterations) {
							iter++;
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							animation.iterateOnce();

							Graph g = new Graph();
							for (Node node : animation.graph().nodes()) {
								Node n = new Node(node.name());
								n.setPosition(node.x(), node.y());
								g.addNode(n);
							}
							for (int[] edge : animation.graph().edges()) {
								int n1 = edge[0];
								int n2 = edge[1];
								g.addEdge(g.nodes().get(n1), g.nodes().get(n2));
							}
							AdaptiveSpringEmbedding.scale(g);
							drawingArea.setGraph(g);
							drawingArea.paint(drawingArea.getGraphics());
						}
						this.stop();
						
					}
				};
				thread.start();*/
			}
		});
		mnLayout.add(mntmSpringEmbeddingAnimation);

		JMenuItem mntmCircularEmbedding = new JMenuItem("Circular embedding");
		mntmCircularEmbedding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				randomButton.setVisible(false);
				gridLayoutButton.setVisible(false);
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
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel.add(saveButton);
//===========================================================================================
//Start Spring embedding with random start position
//===========================================================================================
		randomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingArea.reset();
				AdaptiveSpringEmbedding.defineLayout(G, 1);
				drawingArea.setGraph(G);
				drawingArea.setLinearEdges(true);
				drawingArea.paint(drawingArea.getGraphics());
			}
		});
		randomButton.setVisible(false);
		panel.add(randomButton);
//===========================================================================================
//Start Spring embedding with grid as start position
//===========================================================================================
		gridLayoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingArea.reset();
				AdaptiveSpringEmbedding.defineLayout(G, 0);
				drawingArea.setGraph(G);
				drawingArea.setLinearEdges(true);
				drawingArea.paint(drawingArea.getGraphics());
			}
		});
		gridLayoutButton.setVisible(false);
		panel.add(gridLayoutButton);
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
		scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		settingsArea.add(scrollPane, BorderLayout.CENTER);

		settingsArea.add(panel);

//===========================================================================================
//Functionality to click on nodes
//===========================================================================================
		drawingArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (G != null && !G.nodes().isEmpty()) {
					Point pt = e.getPoint();
					double x = drawingArea.realX(pt.getX());
					double y = drawingArea.realY(pt.getY());
					Node n = GraphDrawer.nearestNode(G, x, y, drawingArea.sizeDrawing());
					double distance = GraphDrawer.distanceToNode(n, x, y, drawingArea.sizeDrawing());

					if (markedNode != null) {
						GraphDrawer.unmarkNode(markedNode);
						GraphDrawer.unmarkAdjacentNodes(G, markedNode);
					}
					
					if (distance < 1.05*GraphDrawer.getRadius(drawingArea.sizeDrawing(), G)) {
						GraphDrawer.markNode(n);
						GraphDrawer.markAdjacentNodes(G, n);
						markedNode = n;

						// print information for the user
						String text = "Node name: " + n.name() + "\n";
						String out = "";
						for (int i : G.outEdges(n)) {
							if (out != "")
								out += ", ";
							out += G.nodes().get(i).name();
						}
						text += "Outgoing edges to: " + out + "\n";
						String in = "";
						for (int i : G.inEdges(n)) {
							if (in != "")
								in += ", ";
							in += G.nodes().get(i).name();
						}
						text += "Incoming edges from: " + in + "\n";
						infoField.setText(text);
					} else {
						System.out.println("Clicked outside of graph");
					}

					drawingArea.paint(drawingArea.getGraphics());
				}
			}
		});
	}
}
