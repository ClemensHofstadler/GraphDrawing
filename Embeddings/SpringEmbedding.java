package Embeddings;

import java.util.ArrayList;

import Graph.Graph;
import Graph.Node;
import Graph.Node3D;
import Graph.Vector;

/**
 * Implements an force-based graph drawing algorithm. Edges are considered as
 * springs and Nodes repel each other (like electrical charges). By iterating
 * over all nodes and moving them according to forces acting on them, a stable
 * constellation is sought. In each step the algorithm tries to reduce the total
 * energy of the system, but stops after at most "MAXIMUM_ITERATIONS"
 * iterations.
 * 
 * @author Lukas Wögerer
 * @version 1.0.2, 23rd June 2019
 */
public class SpringEmbedding {
	/**
	 * No matter how K and C are chosen, this way of calculating a layout will
	 * always produce equivalent results. (Equivalent up to scaling. But since at
	 * the end the layout is scaled such that it fits inside the unit square/cube,
	 * the choice of K and C does not matter)
	 */
	static final double K = 1.;
	/**
	 * No matter how K and C are chosen, this way of calculating a layout will
	 * always produce equivalent results. (Equivalent up to scaling. But since at
	 * the end the layout is scaled such that it fits inside the unit square/cube,
	 * the choice of K and C does not matter)
	 */
	static final double C = 1.;
	/**
	 * Initial step length is just the initial value with which the iteration should
	 * start. For further information on what the step length is for, read more
	 * about the method 'updateSteplength'.
	 */
	static final double INITIAL_STEP_LENGTH = 0.1;
	/**
	 * The algorithm stops and has reached its final state when a change in the
	 * layout between two consecutive iterations is less than K*TOLERANCE.
	 */
	static final double TOLERANCE = 0.000001d;
	/**
	 * This is the factor by which the step size can be adjusted (new step length is
	 * either equal to step/T or step*T),
	 */
	static final double T = 0.9;
	/**
	 * If each node is moved MAXIMUM_ITERATIONS times, the process stops and the
	 * current layout is chosen as the final layout. In other words: not more than
	 * MAXIMUM_ITERATIONS iterations are performed.
	 */
	static final int MAXIMUM_ITERATIONS = 1000;

	/**
	 * Represents how effective the last iterations were.
	 */
	static int progress;
	/**
	 * For further information on what the step length is for, read more about the
	 * method 'updateSteplength'.
	 */
	static double step;
	/**
	 * Represents the total energy of the system. The change of this value has
	 * influence on how the variable 'step' changes.
	 */
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
		if(G.nodes().size()==0)
			return;

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
	 * Iterates once over all nodes and for each node the sum of all forces acting
	 * on it is calculated. As soon as all forces acting on one specific node are
	 * sum up, this node is moved a tiny bit in the direction of the total force.
	 * The magnitude of this movement is influenced by a variable called 'step'. It
	 * gets bigger or smaller depending on how effective the last movement was, and
	 * is changed at the very end of this method. This method returns whether the
	 * procedure converged (i.e if the final embedding is reached). This can happen
	 * either if the maximum number of iterations MAXIMUM_ITERATIONS is reached, or
	 * the movement of the nodes were very small.
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
	 * This method updates the static variable 'step', which has affect on how much
	 * each nodes is moved within an iteration. A high value of 'step' corresponds
	 * to much greater movement than a smaller value. How the value will change
	 * depends on the performance of the last iteration, and thus on the previously
	 * used value of 'step' and the total energy of the system before and after an
	 * iteration.
	 * 
	 * @param step
	 * @param Energy  Total energy before iteration.
	 * @param Energy0 Total energy after iteration.
	 * @return double New step-length
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
