import java.util.ArrayList;

//===========================================================================================
//Implements an force-based graph drawing algorithm.
//Edges are considered as springs and Nodes repel each other (like electrical charges).
//By iterating over all nodes and moving them according to forces acting on them, a stable
//constellation is sought. In each step the algorithm tries to reduce the total energy of the
//system, but stops after at most "maxIter" (or any other desired number of) iterations.
//===========================================================================================
public class AdaptiveSpringEmbedding {
	static final double K = 1.;
	static final double c = 1.;
	static final double initialStepLength = 0.1;
	static final double tol = 0.000001d;
	static final double t = 0.9;
	static final int maximumIterations = 1000;
	static int progress;
	

	public static boolean iterateOnce(Graph G) {

		ArrayList<Node> nodes = G.nodes();
		int numberOfNodes = nodes.size();
		progress = 0;

		boolean converged = false;
		double step = initialStepLength;
		double E = Double.MAX_VALUE;

		double E0;
		double dist;
		Vector2D force = new Vector2D();
		Vector2D f = new Vector2D();
		double[][] oldPositions = new double[numberOfNodes][2];

		for (int i = 0; i < numberOfNodes; i++) {
			oldPositions[i][0] = nodes.get(i).x();
			oldPositions[i][1] = nodes.get(i).y();
		}
		E0 = E;
		E = 0.;
		for (int i = 0; i < nodes.size(); i++) {
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

			Node node = nodes.get(i);
			force.setXY(0., 0.);
			//sum up all attracting forces acting on the current node
			for (int j : posOfAdjacentNodes) {
				Node node2 = nodes.get(j);
				dist = Math.sqrt(Math.pow(node.x() - node2.x(), 2) + Math.pow(node.y() - node2.y(), 2));
				f.setXY(node2.x() - node.x(), node2.y() - node.y());
				f.multiply(attractiveForce(dist) / dist);
				force.add(f);
			}
			//add all repelling forces acting on the current node
			for (int j = 0; j < nodes.size(); j++) {
				if (i != j) {
					Node node2 = nodes.get(j);
					dist = Math.sqrt(Math.pow(node.x() - node2.x(), 2) + Math.pow(node.y() - node2.y(), 2));
					f.setXY(node2.x() - node.x(), node2.y() - node.y());
					f.multiply(repulsiveForce(dist) / dist);
					force.add(f);
				}
			}
			
			// set new position of current node
			double magnitude = force.length();
			if (magnitude > 0) {
				E += Math.pow(magnitude, 2);
				force.multiply(step / magnitude);
				double newX = node.x();
				newX += force.getCoordinates()[0];
				double newY = node.y();
				newY += force.getCoordinates()[1];
				node.setPosition(newX, newY);
			}
		}

		step = updateSteplength(step, E, E0);
		double measure = 0.;
		for (int i = 0; i < numberOfNodes; i++) {
			measure += Math.pow(oldPositions[i][0] - nodes.get(i).x(), 2);
			measure += Math.pow(oldPositions[i][1] - nodes.get(i).y(), 2);
		}
		measure = Math.sqrt(measure);
		if (measure < K * tol) {
			converged = true;
			System.out.println("Converged!");
		}
		
		return converged;
	}

//===========================================================================================
// Iterates at most maximumIterations times to find a stable configuration
//===========================================================================================
	public static void defineLayout(Graph G, int layoutType) {
		defineLayout(G, maximumIterations, layoutType);
	}

//===========================================================================================
// Stops after at most maxIter number of iterations.
//===========================================================================================
	public static void defineLayout(Graph G, int maxIter, int layoutType) {
		if(layoutType == 0)
			GridEmbedding.defineLayout(G);
		else
			RandomEmbedding.defineLayout(G);

		ArrayList<Node> nodes = G.nodes();
		int numberOfNodes = nodes.size();
		progress = 0;

		boolean converged = false;
		double step = initialStepLength;
		double E = Double.MAX_VALUE;

		double E0;
		double dist;
		Vector2D force = new Vector2D();
		Vector2D f = new Vector2D();
		double[][] oldPositions = new double[numberOfNodes][2];

		int iter = 0;
		while (!converged && iter < maxIter) {
			iter++;
			for (int i = 0; i < numberOfNodes; i++) {
				oldPositions[i][0] = nodes.get(i).x();
				oldPositions[i][1] = nodes.get(i).y();
			}
			E0 = E;
			E = 0.;
			for (int i = 0; i < nodes.size(); i++) {
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

				Node node = nodes.get(i);
				force.setXY(0., 0.);
				//sum up all attracting forces acting on the current node
				for (int j : posOfAdjacentNodes) {
					Node node2 = nodes.get(j);
					dist = Math.sqrt(Math.pow(node.x() - node2.x(), 2) + Math.pow(node.y() - node2.y(), 2));
					f.setXY(node2.x() - node.x(), node2.y() - node.y());
					f.multiply(attractiveForce(dist) / dist);
					force.add(f);
				}
				//add all repelling forces acting on the current node
				for (int j = 0; j < nodes.size(); j++) {
					if (i != j) {
						Node node2 = nodes.get(j);
						dist = Math.sqrt(Math.pow(node.x() - node2.x(), 2) + Math.pow(node.y() - node2.y(), 2));
						f.setXY(node2.x() - node.x(), node2.y() - node.y());
						f.multiply(repulsiveForce(dist) / dist);
						force.add(f);
					}
				}
				
				// set new position of current node
				double magnitude = force.length();
				if (magnitude > 0) {
					E += Math.pow(magnitude, 2);
					force.multiply(step / magnitude);
					double newX = node.x();
					newX += force.getCoordinates()[0];
					double newY = node.y();
					newY += force.getCoordinates()[1];
					node.setPosition(newX, newY);
				}
			}

			step = updateSteplength(step, E, E0);
			double measure = 0.;
			for (int i = 0; i < numberOfNodes; i++) {
				measure += Math.pow(oldPositions[i][0] - nodes.get(i).x(), 2);
				measure += Math.pow(oldPositions[i][1] - nodes.get(i).y(), 2);
			}
			measure = Math.sqrt(measure);
			if (measure < K * tol) {
				converged = true;
				System.out.println("Converged within " + iter + " steps!");
			}
		}

		if (iter == maxIter)
			System.out.println("Reached MaxIter=" + maxIter);
		scale(G);

	}

//===========================================================================================
//Calculates the step-length based on the progress it has made.
//===========================================================================================
	private static double updateSteplength(double step, double Energy, double Energy0) {
		if (Energy < Energy0) {
			progress++;
			if (progress > 4) {
				progress = 0;
				return (step / t);
			}
			return step;
		} else {
			progress = 0;
			return (t * step);
		}
	}

//===========================================================================================
// Two nodes repel each other by a specific amount. This force, whose magnitude is given by
// this method, only depends on the distance of those two nodes.
//===========================================================================================
	private static double repulsiveForce(double d) {
		return (-c * Math.pow(K, 2) / d);
	}

//===========================================================================================
// Two nodes which are connected by an edge attract each other by a specific amount. This
// force, whose magnitude is given by this method, only depends on the distance of those two
// nodes.
//===========================================================================================
	private static double attractiveForce(double d) {
		return (Math.pow(d, 2) / K);
	}

//===========================================================================================
// Scales the positions of the nodes to the unit square.
//===========================================================================================
	public static void scale(Graph G) {
		ArrayList<Node> nodes = G.nodes();

		double minX = nodes.get(0).x();
		for (Node node : nodes)
			if (node.x() < minX)
				minX = node.x();
		double maxX = nodes.get(0).x();
		for (Node node : nodes)
			if (node.x() > maxX)
				maxX = node.x();
		double minY = nodes.get(0).y();
		for (Node node : nodes)
			if (node.y() < minY)
				minY = node.y();
		double maxY = nodes.get(0).y();
		for (Node node : nodes)
			if (node.y() > maxY)
				maxY = node.y();

		double dx = maxX - minX;
		double dy = maxY - minY;
		if (dx < dy)
			dx = dy;

		for (Node node : nodes) {
			double newX = ((node.x() - minX) / dx);
			double newY = ((node.y() - minY) / dx);
			node.setPosition(newX, newY);
		}
	}

}
