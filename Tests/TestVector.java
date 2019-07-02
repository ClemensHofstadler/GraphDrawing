package Tests;

import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import Graph.Vector;

/**
 * JUnit 4 test class for the class {@link Graph.Vector Vector}.
 * 
 * @author Lukas W&oumlgerer
 * @version 1.0.0, 23rd June 2019
 *
 */
public class TestVector {
	private static Vector v1;
	private static Vector v2;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		v1 = new Vector(3);
		v2 = new Vector(3);
	}

	@Test
	public void testSetCoordinates() {
		v1.setCoordinates(new double[] {1,2,3});
		double[] pos = v1.coordinates();
		assertTrue(pos[0] == 1 && pos[1] == 2 && pos[2] == 3);
	}
	
	@Test
	public void testAddTwoVectors() {
		v1.setCoordinates(new double[] {1,2,3});
		v2.setCoordinates(new double[] {0,3,-5});
		v1.add(v2);
		double[] pos = v1.coordinates();
		assertTrue(pos[0] == 1 && pos[1] == 5 && pos[2] == -2);
	}

	@Test
	public void testMultiplyWithConstant() {
		v1.setCoordinates(new double[] {1,2,3});
		v1.multiply(-2);
		double[] pos = v1.coordinates();
		assertTrue(pos[0] == -2 && pos[1] == -4 && pos[2] == -6);
	}
	
	@Test
	public void testLength() {
		v1.setCoordinates(new double[] {1,2,2});
		double length = v1.length();
		assertTrue(length==3);
	}
	
	@Test
	public void testCrossProduct() {
		v1.setCoordinates(new double[] {1,0,0});
		v2.setCoordinates(new double[] {0,1,0});
		Vector v3 = Vector.crossProduct(v1, v2);
		double[] pos = v3.coordinates();
		assertTrue(pos[0] == 0 && pos[1] == 0 && pos[2] == 1);
	}
	
	@Test
	public void testDotProduct() {
		v1.setCoordinates(new double[] {1,2,0});
		v2.setCoordinates(new double[] {2,-3,5});
		double prod  = Vector.dotProduct(v1, v2);
		assertTrue(prod == -4);
	}

}
