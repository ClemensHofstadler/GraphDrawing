import java.awt.EventQueue;
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
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.border.BevelBorder;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.event.MouseMotionListener;

import java.awt.GridLayout;
import javax.swing.SwingConstants;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import java.awt.Color;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JRadioButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import Embeddings.SpringEmbedding;
import Embeddings.CircularEmbedding;
import Embeddings.GridEmbedding;
import Embeddings.LayeredEmbedding;
import Embeddings.LinearEmbedding;
import Embeddings.RandomEmbedding;
import Embeddings.SpectralEmbedding;
import Graph.Graph;
import Graph.Node;
import Graph.Node3D;
import GraphDrawing.DrawingArea;
import GraphDrawing.GraphDrawer;
/**
 * Graphical user interface to draw graphs in different layouts.
 * Graphs are loaded from a txt-file and can be visualized with the
 * following layouts:
 * <ul>
 * <li> {@link CircularEmbedding CircularEmbedding}
 * <li> {@link GridEmbedding GridEmbedding}
 * <li> {@link LayeredEmbedding LayeredEmbedding}
 * <li> {@link LinearEmbedding LinearEmbedding}
 * <li> {@link RandomEmbedding RandomEmbedding}
 * <li> {@link SpectralEmbedding SpectralEmbedding}
 * <li> {@link AdaptiveSpringEmbedding SpringEmbedding}	
 * </ul>
 * 
 * Additional functionality such as displaying information about the graph 
 * and/or certain nodes, saving the currently displayed layout as a picture 
 * or zooming is also provided.
 * 
 * @author Clemens Hofstadler, Lukas Wögerer
 * @version 1.0.1, 13rd June 2019
 *
 */
public class GUI {
//===========================================================================================
//Fields
//===========================================================================================
	/**
	 * The currently loaded graph.
	 */
	Graph G;
	/**
	 * The area on which the graph will be drawn.
	 */
	private JFrame frmGraphDrawing;
	/**
	 * The node the user has last clicked on.
	 */
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
		DrawingArea drawingArea = new DrawingArea();
		drawingArea.setBackground(Color.WHITE);
		JMenu mnLayout = new JMenu("Layout");
		JMenuItem mntmHelp = new JMenuItem("Help");
		ButtonGroup bg = new ButtonGroup();
		JTextArea infoField = new JTextArea();
		JRadioButton rdbtnDirected = new JRadioButton("Directed");
		JRadioButton rdbtnUndirected = new JRadioButton("Undirected");
		JPanel directedButtonsContainer = new JPanel();
		JPanel saveButtonContainer = new JPanel();
		JButton randomButton = new JButton("Random start position");
		JButton gridLayoutButton = new JButton("Grid as start position");
		JPanel springButtonsContainer = new JPanel();
		JButton saveButton = new JButton("Save as png");
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
				//resize settings area
				settingsArea.setBounds(size + 12, 6, width - (size + 6), height);
				//resize and redraw graph
				drawingArea.setSize(size);
				drawingArea.paint(drawingArea.getGraphics());
			}
		});
		frmGraphDrawing.setTitle("Graph Drawing");
		frmGraphDrawing.setBounds(100, 100, 850, 613);
		frmGraphDrawing.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		frmGraphDrawing.setJMenuBar(menuBar);
//===========================================================================================
//Zooming & Dragging
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
				double size = (double)drawingArea.sizeDrawing();
				drawingArea.firstClick(pt.getX()/size, pt.getY()/size);
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
				        	if(!n1.equals(n2)) {
				        		G.addNode(n1);
				        		G.addNode(n2);
				        		G.addEdge(n1, n2);
				        	}
				        	line = in.readLine();	
				        }
				        
				        //adapt all stats
						drawingArea.reset();
						drawingArea.setLinearEdges(true);
						randomButton.setVisible(false);
						gridLayoutButton.setVisible(false);
						markedNode = null;
						drawingArea.setThreeDLayout(false);
						G.setDirected(rdbtnDirected.isSelected());
						
						//set new graph
				        GridEmbedding.defineLayout(G);
				        drawingArea.setGraph(G);
				        
				        //print everything needed
				        displayGraphInfo(infoField);
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
				if(G==null)
					return;
				
				//adapt all stats
				drawingArea.reset();
				randomButton.setVisible(false);
				gridLayoutButton.setVisible(false);
				drawingArea.setThreeDLayout(false);
				drawingArea.setLinearEdges(true);
				//print new layout
				RandomEmbedding.defineLayout(G);
				drawingArea.setGraph(G);
		        drawingArea.paint(drawingArea.getGraphics());
			}
		});
		
		JMenuItem mntmCircularEmbedding = new JMenuItem("Circular embedding");
		mntmCircularEmbedding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(G==null)
					return;
				//adapt all stats
				drawingArea.reset();
				drawingArea.setThreeDLayout(false);
				randomButton.setVisible(false);
				gridLayoutButton.setVisible(false);
		        drawingArea.setLinearEdges(true);
		        //print new layout
				CircularEmbedding.defineLayout(G);
				drawingArea.setGraph(G);
		        drawingArea.paint(drawingArea.getGraphics());
			}
		});
		mnLayout.add(mntmCircularEmbedding);
		
		JMenuItem mntmGridEmbedding = new JMenuItem("Grid embedding");
		mntmGridEmbedding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(G==null)
					return;
				//adapt all stats
				drawingArea.reset();
				drawingArea.setThreeDLayout(false);
				randomButton.setVisible(false);
				gridLayoutButton.setVisible(false);
				drawingArea.setLinearEdges(true);
				//print new layout
				GridEmbedding.defineLayout(G);
				drawingArea.setGraph(G);
		        drawingArea.paint(drawingArea.getGraphics());
			}
		});
		mnLayout.add(mntmGridEmbedding);
		
		JMenuItem mntmLayeredEmbedding = new JMenuItem("Layered embedding");
		mntmLayeredEmbedding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(G==null)
					return;
				//adapt all stats
				drawingArea.reset();
				drawingArea.setThreeDLayout(false);
				randomButton.setVisible(false);
				gridLayoutButton.setVisible(false);
				drawingArea.setLinearEdges(true);
				//print new layout
				LayeredEmbedding.defineLayout(G);
				drawingArea.setGraph(G);
		        drawingArea.paint(drawingArea.getGraphics());
			}
		});
		mnLayout.add(mntmLayeredEmbedding);
		
		JMenuItem mntmLinearEmbedding = new JMenuItem("Linear embedding");
		mntmLinearEmbedding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(G==null)
					return;
				//adapt all stats
				drawingArea.reset();
				drawingArea.setThreeDLayout(false);
				randomButton.setVisible(false);
				gridLayoutButton.setVisible(false);
				drawingArea.setLinearEdges(false);
				 //print new layout
				LinearEmbedding.defineLayout(G);
				drawingArea.setGraph(G);
		        drawingArea.paint(drawingArea.getGraphics());
			}
		});
		mnLayout.add(mntmLinearEmbedding);
		mnLayout.add(mntmRandomEmbedding);
		
		JMenuItem mntmSpectralEmbedding2D = new JMenuItem("2D layout");
		mntmSpectralEmbedding2D.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(G==null)
					return;
				//adapt all stats
				drawingArea.reset();
				drawingArea.setThreeDLayout(false);
				randomButton.setVisible(false);
				gridLayoutButton.setVisible(false);
		        drawingArea.setLinearEdges(true);
		        //print new layout
				SpectralEmbedding.defineLayout(G,2);
				drawingArea.setGraph(G);
		        drawingArea.paint(drawingArea.getGraphics());
			}
		});
		
		JMenuItem mntmSpectralEmbedding3D = new JMenuItem("3D layout");
		mntmSpectralEmbedding3D.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(G==null)
					return;
				//adapt all stats
				drawingArea.reset();
				drawingArea.setThreeDLayout(true);
				randomButton.setVisible(false);
				gridLayoutButton.setVisible(false);
				drawingArea.setLinearEdges(true);
				//print new layout
				SpectralEmbedding.defineLayout(G,3);
				drawingArea.setGraph(G);
		        drawingArea.paint(drawingArea.getGraphics());
			}
		});
		
		JMenu spectralLayoutMenu = new JMenu("Spectral embedding");
		spectralLayoutMenu.add(mntmSpectralEmbedding2D);
		spectralLayoutMenu.add(mntmSpectralEmbedding3D);
		mnLayout.add(spectralLayoutMenu);
		
		JMenuItem mntmSpringEmbedding2D = new JMenuItem("2D layout");
		mntmSpringEmbedding2D.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(G==null)
					return;
				//adapt all stats
				drawingArea.reset();
				drawingArea.setThreeDLayout(false);
				randomButton.setVisible(true);
				gridLayoutButton.setVisible(true);
				drawingArea.setLinearEdges(true);
				//print new layout
				//AdaptiveSpringEmbedding.defineLayout(G, 0);
				SpringEmbedding.defineLayout(G, 2, 0);
				drawingArea.setGraph(G);
		        drawingArea.paint(drawingArea.getGraphics());
			}
		});
		
		JMenuItem mntmSpringEmbedding3D = new JMenuItem("3D layout");
		mntmSpringEmbedding3D.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(G==null)
					return;
				//adapt all stats
				drawingArea.reset();
				drawingArea.setThreeDLayout(true);
				drawingArea.setLinearEdges(true);
				randomButton.setVisible(false);
				gridLayoutButton.setVisible(false);
				//print new layout
				//SpringEmbedding3D.defineLayout(G);
				SpringEmbedding.defineLayout(G, 3, 0);
				drawingArea.setGraph(G);
		        drawingArea.paint(drawingArea.getGraphics());
			}
		});
		
		JMenu springEmbeddingMenu = new JMenu("Spring embedding");
		springEmbeddingMenu.add(mntmSpringEmbedding2D);
		springEmbeddingMenu.add(mntmSpringEmbedding3D);
		mnLayout.add(springEmbeddingMenu);
		
		JMenuItem mntmSpringEmbeddingAnimation = new JMenuItem("Spring embedding (animated)");
		mntmSpringEmbeddingAnimation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(G==null)
					return;
				//set new stats
				drawingArea.reset();
				drawingArea.setLinearEdges(true);
				drawingArea.setThreeDLayout(false);
				//print new layout
				GridEmbedding.defineLayout(G);// Random or Grid
				drawingArea.setGraph(G);
				drawingArea.paint(drawingArea.getGraphics());

				//AnimatedSpringEmbedding animation = new AnimatedSpringEmbedding(G);
				
				/*Thread thread = new Thread() {
					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						int iter = 0;
						while (!animation.converged && iter < AnimatedSpringEmbedding.maximumIterations) {
							iter++;
							try {
								Thread.sleep(150);
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
//===========================================================================================
//Help page
//===========================================================================================		
		frmGraphDrawing.getContentPane().setLayout(null);
		drawingArea.setBorder(null);
		
		mntmHelp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//set up the new help frame
				JFrame helpFrame = new JFrame();
				helpFrame.setTitle("Help Page");
				helpFrame.setBounds(140, 140, 682, 460);
				helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				//set up the area to write text
				JEditorPane textArea = new JEditorPane();
				textArea.setEditable(false);
				HTMLEditorKit kit = new HTMLEditorKit();
				textArea.setEditorKit(kit);
				
				//load the text
				File f = new File("helpText.html");
				try {
			    textArea.setPage(f.toURI().toURL());
				}catch(Exception ex) {
					ex.printStackTrace();
				}
				textArea.setCaretPosition(0);
				
				//add everything and display it
				JScrollPane sp = new JScrollPane(textArea);
				helpFrame.getContentPane().add(sp);
				helpFrame.setVisible(true);
			}
		});
		menuBar.add(mntmHelp);
		
		drawingArea.setBounds(6, 6, 557, 557);
		frmGraphDrawing.getContentPane().add(drawingArea);
		drawingArea.setLayout(new BorderLayout(0, 0));
		
		settingsArea.setBounds(570, 6, 273, 557);
		frmGraphDrawing.getContentPane().add(settingsArea);
		GridBagLayout gbl_settingsArea = new GridBagLayout();
		gbl_settingsArea.columnWidths = new int[]{196, 0};
		gbl_settingsArea.rowHeights = new int[]{86, 86, 86, 0, 0, 0, 0, 0, 86, 0, 0};
		gbl_settingsArea.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_settingsArea.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		settingsArea.setLayout(gbl_settingsArea);
		JLabel lblSettings = new JLabel("Settings");
		lblSettings.setHorizontalAlignment(SwingConstants.CENTER);
		lblSettings.setHorizontalTextPosition(SwingConstants.CENTER);
		GridBagConstraints gbc_lblSettings = new GridBagConstraints();
		gbc_lblSettings.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblSettings.insets = new Insets(0, 0, 5, 0);
		gbc_lblSettings.gridx = 0;
		gbc_lblSettings.gridy = 0;
		settingsArea.add(lblSettings, gbc_lblSettings);
		JScrollPane scrollPane = new JScrollPane();

				
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		gbc_scrollPane.weighty = 1;
		gbc_scrollPane.gridheight = 5;
		settingsArea.add(scrollPane, gbc_scrollPane);
		scrollPane.setViewportView(infoField);
		infoField.setEditable(false);
				
		infoField.setLineWrap(true);
		infoField.setWrapStyleWord(true);
		infoField.setText("\n\n" + "Click on \"Load\" to load a graph");
		directedButtonsContainer.setMaximumSize(new Dimension(100, 100));
		rdbtnDirected.setSelected(true);	
//===========================================================================================
//Auxiliary functions
//===========================================================================================
		rdbtnDirected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(G != null && G.directed() == false) {
					G.setDirected(true);
					if(markedNode != null)
						GraphDrawer.markAdjacentNodes(G, markedNode);
					
					drawingArea.paint(drawingArea.getGraphics());
					displayNodeInfo(infoField,markedNode);
				}
			}
		});
		directedButtonsContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		bg.add(rdbtnDirected);
		directedButtonsContainer.add(rdbtnDirected);
		rdbtnUndirected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(G != null && G.directed() == true) {
					G.setDirected(false);
					if(markedNode != null)
						GraphDrawer.markAdjacentNodes(G, markedNode);
					
					drawingArea.paint(drawingArea.getGraphics());
					displayNodeInfo(infoField,markedNode);
				}
			}
		});
		bg.add(rdbtnUndirected);
		directedButtonsContainer.add(rdbtnUndirected);
						
		GridBagConstraints gbc_directedButtonsContainer = new GridBagConstraints();
		gbc_directedButtonsContainer.fill = GridBagConstraints.BOTH;
		gbc_directedButtonsContainer.insets = new Insets(0, 0, 5, 0);
		gbc_directedButtonsContainer.gridx = 0;
		gbc_directedButtonsContainer.gridy = 7;
		settingsArea.add(directedButtonsContainer, gbc_directedButtonsContainer);
		
		GridBagConstraints gbc_springButtonsContainer = new GridBagConstraints();
		gbc_springButtonsContainer.insets = new Insets(0, 0, 5, 0);
		gbc_springButtonsContainer.gridx = 0;
		gbc_springButtonsContainer.gridy = 8;
		settingsArea.add(springButtonsContainer, gbc_springButtonsContainer);
		springButtonsContainer.setLayout(new GridLayout(0, 1, 0, 0));
		springButtonsContainer.add(randomButton);
		springButtonsContainer.add(gridLayoutButton);
//===========================================================================================
//Start Spring embedding with grid as start position
//===========================================================================================
		gridLayoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingArea.reset();
				SpringEmbedding.defineLayout(G, 2, 0);
				drawingArea.setGraph(G);
				drawingArea.setLinearEdges(true);
				drawingArea.paint(drawingArea.getGraphics());
			}
		});
		gridLayoutButton.setVisible(false);
				
//===========================================================================================
//Start Spring embedding with random start position
//===========================================================================================
		randomButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingArea.reset();
				SpringEmbedding.defineLayout(G, 2, 1);
				drawingArea.setGraph(G);
				drawingArea.setLinearEdges(true);
				drawingArea.paint(drawingArea.getGraphics());
			}
		});
		randomButton.setVisible(false);
		
		GridBagConstraints gbc_saveButtonContainer = new GridBagConstraints();
		gbc_saveButtonContainer.insets = new Insets(0, 0, 5, 0);
		gbc_saveButtonContainer.gridx = 0;
		gbc_saveButtonContainer.gridy = 9;
		settingsArea.add(saveButtonContainer, gbc_saveButtonContainer);
		saveButtonContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		saveButtonContainer.add(saveButton);
		saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
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
//===========================================================================================
//Rotate 3D image
//===========================================================================================
		class KeyboardAction extends AbstractAction {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent tf) {
				if(drawingArea.threeDLayout())
				{
					System.out.println(tf.getActionCommand());
					Node3D.rotate(tf.getActionCommand(), G);
					//drawingArea.reset();
					//drawingArea.setGraph(G);
					//drawingArea.setLinearEdges(true);
					drawingArea.paint(drawingArea.getGraphics());
				}
			}
		}
		settingsArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put( KeyStroke.getKeyStroke("W"), "doKeyboardAction");
		settingsArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put( KeyStroke.getKeyStroke("A"), "doKeyboardAction");
		settingsArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put( KeyStroke.getKeyStroke("S"), "doKeyboardAction");
		settingsArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put( KeyStroke.getKeyStroke("D"), "doKeyboardAction");
		settingsArea.getActionMap().put( "doKeyboardAction", new KeyboardAction() );
//===========================================================================================
//Functionality to click on nodes
//===========================================================================================
		drawingArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(G != null && !G.nodes().isEmpty()) {
					Point pt = e.getPoint();					
					double x = drawingArea.realX(pt.getX());
					double y = drawingArea.realY(pt.getY());
					Node n = GraphDrawer.nearestNode(G, x, y, drawingArea.sizeDrawing());
					double distance = GraphDrawer.distanceToNode(n, x, y, drawingArea.sizeDrawing());
					
					if(markedNode != null) {
						GraphDrawer.unmarkNode(markedNode);
						GraphDrawer.unmarkAdjacentNodes(G, markedNode);
					}
			
					if (distance < 1.05*GraphDrawer.getRadius(drawingArea.sizeDrawing(),G)) {
						GraphDrawer.markNode(n);
						GraphDrawer.markAdjacentNodes(G, n);
						markedNode = n;
						
						displayNodeInfo(infoField,markedNode);
					}
					else {
						displayGraphInfo(infoField);
					}
				
					drawingArea.paint(drawingArea.getGraphics());
				}
			}
		});
	}
//===============================================================================	
// Other methods of this class
//===============================================================================
	/**
	 * Prints information about the graph on a JTextArea. This information includes
	 * the names of all nodes of the graph as well as all edges.
	 * 
	 * @param t The JTextArea on which the information will be displayed.
	 */
	private void displayGraphInfo(JTextArea t) {
		if(G == null)
			return;
		
		//Print the nodes
		String str = "V = {" + G.nodes().get(0).name();
		for(int i = 1; i < G.nodes().size(); i++)
			str += ", " + G.nodes().get(i).name();
		
		//Print the edges
		str += "}\n\n" + "E = {";
		for(int i = 0; i < G.edges().size(); i++){
			if(i > 0)
				str += ", ";
			int[] edge = G.edges().get(i);
			str += "(" + G.nodes().get(edge[0]).name() + "," + G.nodes().get(edge[1]).name() + ")";
		}
		str += "}";
		
		t.setText(str);
		t.setCaretPosition(0);
	}
	
	/**
	 * Prints information about the node 'n' on a JTextArea. This information includes
	 * the name of the node as well as all information about incoming and outgoing edges.
	 * 
	 * @param t The JTextArea on which the information will be displayed.
	 * @param n A node.
	 */
	private void displayNodeInfo(JTextArea t, Node n) {
		if(n == null)
			return;
		
		//print information for the user
		String text = "Node name: " + n.name() + "\n";
		String out = "";
		for(int i: G.outEdges(n)) {
			if(out != "")
			out += ", ";
			out += G.nodes().get(i).name();
		}
		String in = "";
		for(int i: G.inEdges(n)) {
			if(in != "")
				in += ", ";
			in += G.nodes().get(i).name();
		}
		
			
		//if the graph is directed distinguish between 
		//in and out edges
		if(G.directed()) {
			text += "Outgoing edges to: " + out + "\n";
			text += "Incoming edges from: " + in + "\n";					
		}
		else {
			text += "Connected to: " + in + (in == ""?  "" : ", ") + out;
		}
		t.setText(text);
	}
}
