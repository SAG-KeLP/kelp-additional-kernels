package it.uniroma2.sag.kelp.kernel.tree.deltamatrix;


public class StaticDeltaMatrix implements DeltaMatrix {

	private final static int DEFAULTSIZE = 400;

	/**
	 * Sparse implementation of a matrix
	 */
	private float[][] matrix = new float[DEFAULTSIZE][DEFAULTSIZE];
	
	private static final StaticDeltaMatrix instance = new StaticDeltaMatrix(DEFAULTSIZE);

	public StaticDeltaMatrix() {

	}

	public StaticDeltaMatrix(int maxElementSize) {
		matrix = new float[maxElementSize][maxElementSize];
	}

	/**
	 * Insert a value in the matrix
	 * 
	 * @param i
	 *            row index
	 * @param j
	 *            column index
	 * @param v
	 *            value to insert in delta_matrix[i][j]
	 */
	public void add(int i, int j, float v) {
		this.matrix[i][j] = v;
	}

	/**
	 * Get a value from the matrix
	 * 
	 * @param i
	 *            row index
	 * @param j
	 *            column index
	 * @return value to retrieve from the delta_matrix[[i][j]
	 */
	public float get(int i, int j) {
		return matrix[i][j];
	}

	/**
	 * Clear the delta matrix
	 */
	public void clear() {

	}
	
	public static StaticDeltaMatrix getInstance(){
		return instance;
	}

}
