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

public class GUI {
//===========================================================================================
//Fields
//===========================================================================================
	Graph G;
	private boolean linearEdges = true;
	private JFrame frmGraphDrawing;
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
		JTextArea txtrInputFromFile = new JTextArea();
		JPanel drawingArea = new JPanel();
		JMenu mnLayout = new JMenu("Layout");
//===========================================================================================
//Resizing of the components
//===========================================================================================
		frmGraphDrawing.getContentPane().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int width = frmGraphDrawing.getWidth()-12;
				int height = frmGraphDrawing.getHeight() - 56;
				int size = width < height ? width : height;
				drawingArea.setSize(size, size);
				settingsArea.setBounds(size+12, 6, width-(size+6), height);
				try {
					GraphDrawer.drawGraph(drawingArea.getGraphics(),G,size,linearEdges);
				}catch(Exception ex) {System.out.println("Error");}
			}
		});
		frmGraphDrawing.setTitle("Graph Drawing");
		frmGraphDrawing.setBounds(100, 100, 567, 409);
		frmGraphDrawing.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		
		frmGraphDrawing.setJMenuBar(menuBar);
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
				    	
				    	txtrInputFromFile.setText("");
				        String line = in.readLine();
				        while (line != null) {
				        	int space = line.indexOf(' ');
				        	Node n1 = new Node(line.substring(0, space));
				        	Node n2 = new Node(line.substring(space+1, line.length()));
				        	G.addNode(n1);
				        	G.addNode(n2);
				        	G.addEdge(n1,n2);
				        	txtrInputFromFile.setText(txtrInputFromFile.getText() + line + "\n");
				        	line = in.readLine();	
				        }
				        GridEmbedding.defineLayout(drawingArea.getWidth(), drawingArea.getHeight(), G);
				        GraphDrawer.drawGraph(drawingArea.getGraphics(),G,drawingArea.getWidth(),linearEdges);
				        
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
				linearEdges = true;
				RandomEmbedding.defineLayout(G);
		        GraphDrawer.linearEdges(drawingArea.getGraphics(), drawingArea.getWidth(), drawingArea.getHeight(), G);
			}
		});
		mnLayout.add(mntmRandomEmbedding);
		
		JMenuItem mntmGridEmbedding = new JMenuItem("Grid embedding");
		mntmGridEmbedding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("The algorithm has to be adapted to work on the unit square!");
				linearEdges = true;
				//GridEmbedding.defineLayout(G);
		        //GraphDrawer.linearEdges(drawingArea.getGraphics(), drawingArea.getWidth(), drawingArea.getHeight(), G);
			}
		});
		mnLayout.add(mntmGridEmbedding);
		
		JMenuItem mntmLinearEmbedding = new JMenuItem("Linear embedding");
		mntmLinearEmbedding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				linearEdges = false;
				LinearEmbedding.defineLayout(G);
				GraphDrawer.drawGraph(drawingArea.getGraphics(),G,drawingArea.getWidth(),linearEdges);
			}
		});
		mnLayout.add(mntmLinearEmbedding);
		
		JMenuItem mntmSpectralEmbedding = new JMenuItem("Spectral embedding");
		mntmSpectralEmbedding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				linearEdges = true;
				SpectralEmbedding.defineLayout(G);
		        GraphDrawer.linearEdges(drawingArea.getGraphics(), drawingArea.getWidth(), drawingArea.getHeight(), G);
			}
		});
		mnLayout.add(mntmSpectralEmbedding);
		frmGraphDrawing.getContentPane().setLayout(null);
		drawingArea.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
//===========================================================================================
//Auxiliary functions
//===========================================================================================
		drawingArea.setBounds(6, 6, 350, 350);
		frmGraphDrawing.getContentPane().add(drawingArea);
		drawingArea.setLayout(new BorderLayout(0, 0));
		
		settingsArea.setBounds(360, 6, 200, 350);
		frmGraphDrawing.getContentPane().add(settingsArea);
		settingsArea.setLayout(new GridLayout(0, 1, 0, 0));
				JLabel lblSettings = new JLabel("Settings");
				settingsArea.add(lblSettings);
		
				txtrInputFromFile.setLineWrap(true);
				txtrInputFromFile.setWrapStyleWord(true);
				txtrInputFromFile.setText("Input from file will be shown here");
				settingsArea.add(txtrInputFromFile);
//===========================================================================================
//Functionality to click on nodes
//===========================================================================================

		drawingArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(G != null && !G.nodes().isEmpty()) {
					Point pt = e.getPoint();
					Node n = G.nearestNode(pt.x/(double)(drawingArea.getWidth()), pt.y/(double)(drawingArea.getHeight()));
					System.out.println("Mouse clicked at position:" + pt);
					System.out.println("Nearest node:" + n.name());
			}
		}
		});
	}
}

