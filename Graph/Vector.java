package Graph;

/**
 * Represents vectors in Euclidean space of given dimension. It provides
 * functions such as adding two vectors, multiplying them by constants,
 * calculating the dot product of two vectors and more.
 * 
 * @author Lukas Wögerer
 * @version 1.0.1, 13rd June 2019
 */
public class Vector {
	/**
	 * Specifies the dimension of the space where this vector should live in.
	 */
	private int dimension;
	/**
	 * Contains all coordinates.
	 */
	private double[] coordinates;

	/**
	 * Constructor for a zero-vector of dimension dim.
	 * 
	 * @param dim Dimension/Length of the vector.
	 */
	public Vector(int dim) {
		dimension = dim;
		coordinates = new double[dim];
	}

	/**
	 * Constructor for a vector of dimension dim with given entries.
	 * 
	 * @param dim   Dimension/Length of the vector.
	 * @param coord Entries of the vector.
	 */
	public Vector(int dim, double[] coord) {
		dimension = dim;
		coordinates = new double[dim];
		for (int i = 0; i < dim; i++)
			coordinates[i] = coord[i];
	}

	/**
	 * Setter for coordinates of vector.
	 * 
	 * @param coord New coordinates.
	 */
	public void setCoordinates(double[] coord) {
		for (int i = 0; i < dimension; i++)
			coordinates[i] = coord[i];
	}

	/**
	 * Returns the dimension/length of the vector
	 * 
	 * @return int Dimension of the vector.
	 */
	public int dimension() {
		return dimension;
	}

	/**
	 * Returns the coordinates of the vector
	 * 
	 * @return double[] Coordinates of the vector.
	 */
	public double[] coordinates() {
		return coordinates;
	}

	/**
	 * Vector addition.
	 * 
	 * @param v Vector that gets added
	 */
	public void add(Vector v) {
		if (dimension == v.dimension())
			for (int i = 0; i < dimension; i++)
				coordinates[i] += v.coordinates[i];

	}

	/**
	 * Multiplies this vector with a scalar.
	 * 
	 * @param c value that this vector should be multiplied with.
	 */
	public void multiply(double c) {
		for (int i = 0; i < dimension; i++)
			coordinates[i] *= c;
	}

	/**
	 * Calculates the Euclidean norm of his vector.
	 * 
	 * @return double Euclidean norm of this vector.
	 */
	public double length() {
		double sum = 0;
		for (int i = 0; i < dimension; i++)
			sum += Math.pow(coordinates[i], 2);
		return Math.sqrt(sum);
	}

	/**
	 * If given vectors are of dimension three, this method returns the cross
	 * product of those two vectors.
	 * 
	 * @param v1 Vector
	 * @param v2 Vector
	 * @return w Cross product of v1 and v2.
	 */
	public static Vector crossProduct(Vector v1, Vector v2) {
		Vector crossProd = new Vector(v1.dimension);
		if (v1.dimension == 3 && v1.dimension == v2.dimension) {
			double[] a = v1.coordinates();
			double[] b = v2.coordinates();
			double[] product = new double[3];
			product[0] = a[1] * b[2] - a[2] * b[1];
			product[1] = a[2] * b[0] - a[0] * b[2];
			product[2] = a[0] * b[1] - a[1] * b[0];
			crossProd.setCoordinates(product);
		}
		return crossProd;
	}

	/**
	 * Calculates and returns the dot product of two vectors.
	 * 
	 * @param v1 Vector
	 * @param v2 Vector
	 * @return double Dot product of v1 and v2.
	 */
	public static double dotProduct(Vector v1, Vector v2) {
		double sum = 0;
		if (v1.dimension == v2.dimension) {
			double[] a = v1.coordinates();
			double[] b = v2.coordinates();
			for (int k = 0; k < v1.dimension; k++)
				sum += (a[k] * b[k]);
		}
		return sum;
	}

}
