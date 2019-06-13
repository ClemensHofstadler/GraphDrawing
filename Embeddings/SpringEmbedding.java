package Embeddings;

import java.util.ArrayList;

import Graph.Graph;
import Graph.Node;
import Graph.Node3D;
import GraphDrawing.Vector;

/**
 * Implements an force-based graph drawing algorithm. Edges are considered as
 * springs and Nodes repel each other (like electrical charges). By iterating
 * over all nodes and moving them according to forces acting on them, a stable
 * constellation is sought. In each step the algorithm tries to reduce the total
 * energy of the system, but stops after at most "MAXIMUM_ITERATIONS"
 * iterations.
 * 
 * @author Lukas Wögerer
 * @version 1.0.1, 13rd June 2019
 */
public class SpringEmbedding {
	static final double K = 1.;
	static final double C = 1.;
	static final double INITIAL_STEP_LENGTH = 0.1;
	static final double TOLERANCE = 0.000001d;
	static final double T = 0.9;
	static final int MAXIMUM_ITERATIONS = 1000;

	static int progress;
	static double step;
	static double E;

	/**
	 * Aligns the nodes of a Graph G either within in the unit square, or within the
	 * unit cube. Nodes are aligned according to a force-based layout algorithm.
	 * 
	 * @param G          A Graph
	 * @param dimension  Can be 2 or 3, and decides whether the nodes should lie
	 *                   within the unit square (dim=2) or within the unit cube
	 *                   (dim=3)
	 * @param layoutType Sets the initial positions of the nodes either to a grid
	 *                   layout (only when dim=2 and layoutType=0) or randomly.
	 */
	public static void defineLayout(Graph G, int dimension, int layoutType) {
		progress = 0;
		step = INITIAL_STEP_LENGTH;
		E = Double.MAX_VALUE;

		if (dimension == 2) {
			if (layoutType == 0)
				GridEmbedding.defineLayout(G);
			else
				RandomEmbedding.defineLayout(G);
			defineLayout2D(G);
		}
		if (dimension == 3)
			defineLayout3D(G);
	}

	/**
	 * Aligns the nodes of a Graph G either within in the unit square, according to
	 * a force-based layout algorithm.
	 * 
	 * @param G A Graph
	 */
	private static void defineLayout2D(Graph G) {

		for (int i = 0; i < G.nodes().size(); i++) {
			Node oldNode = G.nodes().get(i);
			Node3D newNode = new Node3D(oldNode.name(), oldNode.position(), oldNode.position());
			newNode.setColor(oldNode.color());
			G.nodes().set(i, newNode);
		}

		boolean converged = false;
		int iter = 0;
		while (!converged && iter < MAXIMUM_ITERATIONS) {
			converged = iterateOnce(G, 2);
			iter++;
		}
		for (int i = 0; i < G.nodes().size(); i++) {
			G.nodes().get(i).setPosition(((Node3D) G.nodes().get(i)).getposition3D()[0],
					((Node3D) G.nodes().get(i)).getposition3D()[1]);
		}
		Node3D.scale(G);
	}

	/**
	 * Aligns the nodes of a Graph G either within in the unit cube, according to a
	 * force-based layout algorithm.
	 * 
	 * @param G A Graph
	 */
	private static void defineLayout3D(Graph G) {
		for (int i = 0; i < G.nodes().size(); i++) {
			double[] coord = { Math.random(), Math.random(), Math.random() };
			Node oldNode = G.nodes().get(i);
			Node3D newNode = new Node3D(oldNode.name(), oldNode.position(), coord);
			newNode.setColor(oldNode.color());
			G.nodes().set(i, newNode);
		}
		boolean converged = false;
		int iter = 0;
		while (!converged && iter < MAXIMUM_ITERATIONS) {
			converged = iterateOnce(G, 3);
			iter++;
		}
		Node3D.project3DPoints(G);
	}

	/**
	 * TODO
	 * 
	 * @param G         A Graph
	 * @param dimension Can be 2 or 3, and decides whether the nodes should lie
	 *                  within the unit square (dim=2) or within the unit cube
	 *                  (dim=3)
	 * @return boolean True if process converged within this step, otherwise false.
	 */
	private static boolean iterateOnce(Graph G, int dimension) {
		int numberOfNodes = G.nodes().size();

		double E0;
		double dist;
		Vector force = new Vector(dimension);
		Vector f = new Vector(dimension);

		double[][] oldPositions = new double[numberOfNodes][dimension];
		for (int i = 0; i < numberOfNodes; i++) {
			for (int j = 0; j < dimension; j++)
				oldPositions[i][j] = ((Node3D) G.nodes().get(i)).getposition3D()[j];
		}

		E0 = E;
		E = 0.;
		for (int i = 0; i < numberOfNodes; i++) {
			// find adjacent nodes
			ArrayList<Integer> posOfAdjacentNodes = new ArrayList<>();
			for (int[] edge : G.edges()) {
				if (edge[0] != edge[1]) {
					if (edge[0] == i) {
						if (posOfAdjacentNodes.indexOf(edge[1]) == -1)
							posOfAdjacentNodes.add(edge[1]);
					} else if (edge[1] == i) {
						if (posOfAdjacentNodes.indexOf(edge[0]) == -1)
							posOfAdjacentNodes.add(edge[0]);
					}
				}
			}

			double[] zeros = { 0, 0, 0 };
			force.setCoordinates(zeros);
			// sum up all attracting forces acting on the current node
			for (int j : posOfAdjacentNodes) {
				dist = distance(((Node3D) G.nodes().get(i)).getposition3D(),
						((Node3D) G.nodes().get(j)).getposition3D());
				double[] diff = new double[dimension];
				for (int k = 0; k < dimension; k++)
					diff[k] = ((Node3D) G.nodes().get(j)).getposition3D()[k]
							- ((Node3D) G.nodes().get(i)).getposition3D()[k];
				f.setCoordinates(diff);
				f.multiply(attractiveForce(dist) / dist);
				force.add(f);
			}
			// add all repelling forces acting on the current node
			for (int j = 0; j < numberOfNodes; j++) {
				if (i != j) {
					dist = distance(((Node3D) G.nodes().get(i)).getposition3D(),
							((Node3D) G.nodes().get(j)).getposition3D());
					double[] diff = new double[dimension];
					for (int k = 0; k < dimension; k++)
						diff[k] = ((Node3D) G.nodes().get(j)).getposition3D()[k]
								- ((Node3D) G.nodes().get(i)).getposition3D()[k];
					f.setCoordinates(diff);
					f.multiply(repulsiveForce(dist) / dist);
					force.add(f);
				}
			}

			// set new position of current node
			double magnitude = force.length();
			if (magnitude > 0) {
				E += Math.pow(magnitude, 2);
				force.multiply(step / magnitude);

				double[] newPosition = new double[dimension];
				for (int k = 0; k < dimension; k++) {
					newPosition[k] = ((Node3D) G.nodes().get(i)).getposition3D()[k] + force.coordinates()[k];
				}
				((Node3D) G.nodes().get(i)).setPosition3D(newPosition);
			}
		}

		step = updateSteplength(step, E, E0);
		double measure = 0.;
		for (int i = 0; i < numberOfNodes; i++) {
			for (int k = 0; k < dimension; k++)
				measure += Math.pow(oldPositions[i][k] - ((Node3D) G.nodes().get(i)).getposition3D()[k], 2);
		}
		measure = Math.sqrt(measure);
		if (measure < K * TOLERANCE) {
			System.out.println("Converged!");
			return true;
		}

		return false;
	}

	/**
	 * Returns the Euclidean distance between two points.
	 * 
	 * @param v1 First point
	 * @param v2 Second point
	 * @return double
	 */
	private static double distance(double[] v1, double[] v2) {
		double sum = 0;
		for (int k = 0; k < v1.length; k++)
			sum += Math.pow(v1[k] - v2[k], 2);
		return Math.sqrt(sum);
	}

	/**
	 * TODO
	 * 
	 * @param step
	 * @param Energy
	 * @param Energy0
	 * @return
	 */
	private static double updateSteplength(double step, double Energy, double Energy0) {
		if (Energy < Energy0) {
			progress++;
			if (progress > 4) {
				progress = 0;
				return (step / T);
			}
			return step;
		} else {
			progress = 0;
			return (T * step);
		}
	}

	/**
	 * Two nodes repel each other by a specific amount. This force, whose magnitude
	 * is calculated by this method, only depends on the distance of those two
	 * nodes.
	 * 
	 * @param d Distance between two points
	 * @return double Magnitude of force between two points with distance d.
	 */
	private static double repulsiveForce(double d) {
		return (-C * Math.pow(K, 2) / d);
	}

	/**
	 * Two nodes which are connected by an edge attract each other by a specific
	 * amount. This force, whose magnitude is calculated by this method, only
	 * depends on the distance of those two nodes.
	 * 
	 * @param d Distance between two points
	 * @return double Magnitude of force between two points with distance d.
	 */
	private static double attractiveForce(double d) {
		return (Math.pow(d, 2) / K);
	}

}
