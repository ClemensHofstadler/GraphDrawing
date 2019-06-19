package Tests;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;
import org.junit.BeforeClass;
import Embeddings.SpectralEmbedding;
import Embeddings.SpringEmbedding;
import Graph.Graph;
import Graph.Node;
import Graph.Node3D;

public class TestSpectralEmbedding {
	private static Graph G;
	private static Node[] nodes;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		G = new Graph();
		nodes = new Node[4];
		for(int i = 0; i < 4; i++)
			nodes[i] = new Node(Integer.toString(i));
	}

	@Test
	public void testEmptyGraph() {
		SpectralEmbedding.defineLayout(G,2);
	}
	
	@Test
	public void testUsualGraph2D() {
		//set up graph
		for(Node n: nodes)
			G.addNode(n);
		G.addEdge(nodes[0], nodes[1]);
		G.addEdge(nodes[1], nodes[2]);
		G.addEdge(nodes[2], nodes[3]);
		G.addEdge(nodes[3], nodes[0]);
		SpectralEmbedding.defineLayout(G,2);
		//The laplacian has eigenvectors
		//{0, 1, 0, -1}, {-1, 0, 1, 0}
		double[][] expectedPos = new double[4][];
		expectedPos[0] = new double[] {0.5,0};
		expectedPos[1] = new double[] {1,0.5};
		expectedPos[2] = new double[] {0.5,1};
		expectedPos[3] = new double[] {0,0.5};
		//due to rounding error when computing eigenvalues
		//we only compare whether they are close together
		for(int i = 0; i < G.nodes().size();i++) {
			double[] realPos = G.nodes().get(i).position();
			double diff = Math.sqrt(Math.pow(realPos[0] - expectedPos[i][0],2) + Math.pow(realPos[1] - expectedPos[i][1],2));
			assertTrue(diff < Math.pow(10, -10));
		}
	}
	
	@Test
	public void testGraph3D() {
		//set up graph
		for(Node n: nodes)
			G.addNode(n);
		G.addEdge(nodes[0], nodes[1]);
		G.addEdge(nodes[1], nodes[2]);
		G.addEdge(nodes[2], nodes[3]);
		G.addEdge(nodes[3], nodes[0]);
		SpectralEmbedding.defineLayout(G,3);
		//The laplacian has eigenvectors
		//{0, 1, 0, -1}, {-1, 0, 1, 0}, {-1, 1, -1, 1}
		double[][] expectedPos = new double[4][];
		expectedPos[0] = new double[] {0.5,0,0};
		expectedPos[1] = new double[] {1,0.5,1};
		expectedPos[2] = new double[] {0.5,1,0};
		expectedPos[3] = new double[] {0,0.5,1};
		//due to rounding error when computing eigenvalues
		//we only compare whether they are close together
		for(int i = 0; i < G.nodes().size();i++) {
			double[] realPos = ((Node3D) G.nodes().get(i)).getposition3D();
			double diff = Math.sqrt(Math.pow(realPos[0] - expectedPos[i][0],2) + Math.pow(realPos[1] - expectedPos[i][1],2) + Math.pow(realPos[2] - expectedPos[i][2],2));
			assertTrue(diff < Math.pow(10, -10));
		}
	}
	
	@Test
	public void testGraph3Nodes() {
		//also make a copy of G
		Graph G2 = new Graph();
		for(int i = 0; i < 3; i++) {
			G.addNode(nodes[i]);
			G2.addNode(nodes[i]);
		}
		G.addEdge(nodes[0], nodes[1]);
		G.addEdge(nodes[1], nodes[2]);
		G.addEdge(nodes[2], nodes[0]);
		G2.addEdge(nodes[0], nodes[1]);
		G2.addEdge(nodes[1], nodes[2]);
		G2.addEdge(nodes[2], nodes[0]);
		
		SpectralEmbedding.defineLayout(G, 2);
		SpringEmbedding.defineLayout(G2, 2, 1);
		for(int i = 0; i < 3; i++)
			assertTrue(G.nodes().get(i).equals(G2.nodes().get(i)));

	}
}
