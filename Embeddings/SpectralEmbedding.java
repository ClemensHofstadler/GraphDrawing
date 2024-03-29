package Embeddings;

import Graph.Graph;
import Graph.Node;
import Graph.Node3D;
import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * Class to align a graph according to a spectral embedding. This means
 * that the nodes of the graph are aligned according to the eigenvectors
 * corresponding to the smallest non-zero eigenvalues of the Lagrangian matrix 
 * of the graph. This class provides a 2D layout and a 3D layout.
 * 
 * @author Clemens Hofstadler
 * @version 1.0.0, 31st May 2019
 *
 */
public class SpectralEmbedding {
	/**
	 * Aligns the nodes of a graph G according to a spectral 
	 * embedding. Either in 2 dimensions (in the unit square)
	 * or in 3 dimensions (in the unit cube).
	 * 
	 * @param G The graph to be aligned.
	 * @param dim Defines the dimension in which the embedding
	 * will be computed. Either 2 or 3.
	 */
	public static void defineLayout(Graph G, int dim) {
		if(G.nodes().size() == 0)
			return;
		//if less than 4 nodes, we have not enough eigenvectors
		//use springEmbedding instead
		if(G.nodes().size() < 4) {
			SpringEmbedding.defineLayout(G, dim, 0);
			return;
		}
		
		if(dim == 2)
			defineLayout2D(G);
		if(dim == 3)
			defineLayout3D(G);
	}
//==============================================================================
// 2D and 3D layout
//==============================================================================
	/**
	 * Aligns the nodes of a graph G according to a spectral 
	 * embedding in 2 dimensions (in the unit square).
	 * 
	 * @param G The graph to be aligned.
	 */
	private static void defineLayout2D(Graph G) {
		//Compute the Lagrangian matrix of G 
		//and its eigenvectors
		Matrix L = degreeMatrix(G).minus(adjacencyMatrix(G));		
		double[][] eig = getMinEigenvectors(L,2);
		
		//Determine min and max values appearing in the eigenvectors
		double[] minMaxX = minMax(eig[0]);
		double[] minMaxY = minMax(eig[1]);
		
		//place the nodes according to the eigenvectors 
		//but with values scaled to [0,1]
		for(int i = 0; i < G.nodes().size(); i++) {
			double x = (eig[0][i] - minMaxX[0])/(minMaxX[1] - minMaxX[0]);
			double y = (eig[1][i] - minMaxY[0])/(minMaxY[1] - minMaxY[0]);
			G.nodes().get(i).setPosition(x, y);
		}
	}
//==============================================================================
	/**
	 * Aligns the nodes of a graph G according to a spectral 
	 * embedding in 3 dimensions (in the unit cube).
	 * 
	 * @param G The graph to be aligned.
	 */
	private static void defineLayout3D(Graph G) {
		//Compute the Lagrangian matrix of G
		//and its eigenvectors
		Matrix L = degreeMatrix(G).minus(adjacencyMatrix(G));	
		double[][] eig = getMinEigenvectors(L,3);
		
		//Determine min and max values appearing in the eigenvectors
		double[] minMaxX = minMax(eig[0]);
		double[] minMaxY = minMax(eig[1]);
		double[] minMaxZ = minMax(eig[2]);
				
		//place the nodes according to the eigenvectors 
		//but with values scaled to [0,1]
		for(int i = 0; i < G.nodes().size(); i++) {
			double x = (eig[0][i] - minMaxX[0])/(minMaxX[1] - minMaxX[0]);
			double y = (eig[1][i] - minMaxY[0])/(minMaxY[1] - minMaxY[0]);
			double z = (eig[2][i] - minMaxZ[0])/(minMaxZ[1] - minMaxZ[0]);
			Node oldNode = G.nodes().get(i);
			Node3D newNode = new Node3D(oldNode.name(), oldNode.position(), new double[] {x,y,z});
			newNode.setColor(oldNode.color());
			G.nodes().set(i, newNode);
		}
		
		//project nodes down
		Node3D.project3DPoints(G);
	}
//==============================================================================
// Matrix & eigenvector computations 
//==============================================================================	
	/**
	 * Computes the adjacency matrix of the graph G. To compute 
	 * this matrix, we consider the graph G to be undirected. This
	 * means that the resulting adjacency matrix is always symmetric.
	 * 
	 * @param G A graph.
	 * @return The adjacency matrix of G.
	 */
	private static Matrix adjacencyMatrix(Graph G) {
		int n = G.nodes().size();
		Matrix A = new Matrix(n,n);
		for(int[] edge: G.edges()) {
			A.set(edge[0], edge[1], 1);
			if(edge[0] != edge[1])
				A.set(edge[1], edge[0], 1);
		}
		
		return A;
	}
//==============================================================================	
	/**
	 * Computes the degree matrix of the Graph G. i.e. a diagonal matrix where the
	 * i-th diagonal element is the degree of the i-th node of G, where G is considered
	 * as an undirected graph.
	 * 
	 * @param G A graph.
	 * @return The degree matrix of G.
	 */
	private static Matrix degreeMatrix(Graph G) {
		int n = G.nodes().size();
		Matrix D = new Matrix(n,n);
		for(int[] edge: G.edges()) {
			D.set(edge[0], edge[0], D.get(edge[0],edge[0]) + 1);
			if(edge[0] != edge[1])
				D.set(edge[1], edge[1], D.get(edge[1],edge[1]) + 1);
		}
		return D;
	}
//==============================================================================	
	/**
	 * Computes eigenvectors corresponding to the "dim"
	 * smallest non-zero eigenvalues of a symmetric matrix.
	 * 
	 * @param M A symmetric matrix.
	 * @param dim Number of eigenvectors to be computed.
	 * @return The eigenvectors for the "dim" smallest
	 * non-zero eigenvalues of M.
	 */
	private static double[][] getMinEigenvectors(Matrix M,int dim) {
		//compute the eigensystem of M
		EigenvalueDecomposition eig = M.eig();
		
		//eigenvectors are sorted in ascending order
		//ignore eigenvector corresponding to eigenvalue 0
		double[][] eigVectors = new double[dim][M.getColumnDimension()];
		Matrix eigV = eig.getV().transpose();
		for(int i = 0; i < dim; i++)
			eigVectors[i] = eigV.getArray()[i+1];
		
		return eigVectors;
	}
//==============================================================================	
	/**
	 * Computes the smallest and largest element of a 
	 * double array.
	 * 
	 * @param a An array.
	 * @return A pair (min,max) consisting of the
	 * minimal and maximal element of a.
	 */
	private static double[] minMax(double[] a) {
		double min = a[0];
		double max = a[0];
		for(int i = 0; i < a.length; i++) {
			if(a[i] < min) min = a[i];
			if(a[i] > max) max = a[i];
		}
		return(new double[] {min,max});
	}
}