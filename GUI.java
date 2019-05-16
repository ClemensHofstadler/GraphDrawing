import java.awt.EventQueue;

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

public class GUI {
	
	Graph G;

	private JFrame frame;
	JTextArea txtrInputFromFile = new JTextArea();
	JPanel drawingArea = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
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

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 538, 366);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnLoad = new JMenu("Load");
		mnLoad.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Choose file to be imported");  
				
				int val = fileChooser.showOpenDialog(frame);
				
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
				        GraphDrawer.linearEdges(drawingArea.getGraphics(), G);
				        
				    } catch (Exception ex) {
				    	ex.printStackTrace();
				    }
				   
				}
			}
			});
		menuBar.add(mnLoad);
		frame.getContentPane().setLayout(null);
		
		drawingArea.setBounds(6, 6, 343, 310);
		frame.getContentPane().add(drawingArea);
		drawingArea.setLayout(null);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(353, 6, 179, 310);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblSettings = new JLabel("Settings");
		lblSettings.setBounds(56, 25, 61, 16);
		panel_1.add(lblSettings);
		
		txtrInputFromFile.setLineWrap(true);
		txtrInputFromFile.setWrapStyleWord(true);
		txtrInputFromFile.setText("Input from file will be shown here");
		txtrInputFromFile.setBounds(17, 59, 156, 217);
		panel_1.add(txtrInputFromFile);
	}
}

