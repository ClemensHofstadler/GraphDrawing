import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * Class to align a graph according to a spectral embedding. This means
 * that the nodes of the graph are aligned according to the 2 eigenvectors
 * corresponding to the largest eigenvalues of the Lagrangian matrix of 
 * the graph.
 * 
 * @author clemenshofstadler
 * @version 1.0.0
 *
 */
public class SpectralEmbedding {
	
	/**
	 * Aligns the nodes of a graph G on the unit square
	 * according to a spectral embedding.
	 * 
	 * @param G A graph.
	 */
	public static void defineLayout(Graph G) {
		
		//Determine eigenvectors for the 2 largest eigenvalues
		//of the Lagrangian
		Matrix L = degreeMatrix(G).minus(adjacencyMatrix(G));
		double[][] eig = getMaxEigenvectors(L);
		
		//Determine min and max values appearing in the eigenvectors
		double minX = eig[0][0];
		double minY = eig[1][0];
		double maxX = eig[0][0];
		double maxY = eig[1][0];
		for(int i = 0; i < eig[0].length; i++) {
			if(eig[0][i] < minX) minX = eig[0][i];
			if(eig[0][i] > maxX) maxX = eig[0][i];
			if(eig[1][i] < minY) minY = eig[1][i];
			if(eig[1][i] > maxY) maxY = eig[1][i];
		}
		
		//place the nodes according to the eigenvectors 
		//but with values scaled to [0,1]
		for(int i = 0; i < G.nodes().size(); i++) {
			double x = (eig[0][i] - minX)/(maxX - minX);
			double y = (eig[1][i] - minY)/(maxY - minY);
		
			G.nodes().get(i).setPosition(x, y);
		}
	}
	
	/**
	 * Computes the adjacency matrix of the graph G.
	 * 
	 * @param G A graph.
	 * @return The adjacency matrix of G.
	 */
	private static Matrix adjacencyMatrix(Graph G) {
		int n = G.nodes().size();
		Matrix A = new Matrix(n,n);
		for(int[] edge: G.edges())
			A.set(edge[0], edge[1], 1);
		
		return A;
	}
	
	/**
	 * Computes the degree matrix of the Graph G. i.e. a diagonal matrix where the
	 * i-th diagonal element is the out-degree of the i-th node of G.
	 * 
	 * @param G A graph.
	 * @return The degree matrix of G.
	 */
	private static Matrix degreeMatrix(Graph G) {
		int n = G.nodes().size();
		Matrix D = new Matrix(n,n);
		for(int[] edge: G.edges())
			D.set(edge[0], edge[0], D.get(edge[0],edge[0]) + 1);
	
		return D;
	}
	
	/**
	 * Computes the eigenvectors for the 2 largest eigenvalues
	 * of M.
	 * 
	 * @return The eigenvectors for the 2 largest eigenvalues 
	 * of M
	 */
	private static double[][] getMaxEigenvectors(Matrix M){

		//compute the eigenvalues of M
		EigenvalueDecomposition eig = M.eig();
		
		//compute absolute value of the eigenvalues
		double [] realEig = eig.getRealEigenvalues();
		double [] imEig = eig.getImagEigenvalues();
		double [] abs = new double[imEig.length];
		for(int i = 0; i < abs.length; i++)
			abs[i] = realEig[i]*realEig[i] + imEig[i]*imEig[i];
	
		//get position of largest eigenvalue
		int max = 0;
		for(int i = 0; i < abs.length; i++)
			if(abs[i] > abs[max])
				max = i;
		
		//get position of second largest eigenvalue
		int max2 = (max==0? 1 : 0);
		for(int i = 0; i < abs.length; i++)
			if(abs[i] > abs[max2] && i != max)
				max2 = i;
		
		//get the corresponding eigenvectors
		double[][] eigVectors = new double[2][M.getColumnDimension()];
		Matrix eigV = eig.getV().transpose();
		eigVectors[0] = eigV.getArray()[max];
		eigVectors[1] = eigV.getArray()[max2];
		
		return eigVectors;
	}
}
