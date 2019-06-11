package Graph;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit 4 test class for the class {@link Node Node}. At least
 * for the non-trivial method(s).
 * 
 * @author Clemens Hofstadler
 * @version 1.0.0, 11th June 2019
 *
 */
public class TestNode {
	private static Node n1;
	private static Node n2;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		n1 = new Node("n1",new double[] {1,1});
	}
	
	@Test
	public void testEqualsTrue() {
		n2 = new Node("n1",new double[] {0.5,0.5});
		assert(n1.equals(n2));
	}
	
	@Test
	public void testEqualsFalse() {
		n2 = new Node("n2",new double[] {1,1});
		assert(!n1.equals(n2));
	}

}
