package Tests;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import Graph.Graph;
import Graph.Node3D;

/**
 * JUnit 4 test class for the class {@link Node3D Node3D}.
 * 
 * @author Lukas W&oumlgerer
 * @version 1.0.0, 23rd June 2019
 *
 */
public class TestNode3D {
	private static Node3D n1;
	private static Node3D n2;
	private static Graph G;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		n1 = new Node3D("n1", new double[] { 0, 0 }, new double[] { 1, 0, 0 });
		n2 = new Node3D("n2", new double[] { 0, 0 }, new double[] { 0, 1, 0 });
		G = new Graph();
		G.addNode(n1);
		G.addNode(n2);
	}

	@Test
	public void testSetPosition() {
		n1.setPosition3D(new double[] { 1, 2, 2 });
		double[] pos = n1.getposition3D();
		assertTrue(pos[0] == 1 && pos[1] == 2 && pos[2] == 2);
	}

	@Test
	public void testProjection() {
		n1.setPosition(5, 5);
		n1.setPosition3D(new double[] { 5, 4, 3 });
		n1.project3DPoints(G);
		double[] newPos = n1.position();
		assertTrue(newPos[0] == 1 && newPos[1] == 1);
	}

}
