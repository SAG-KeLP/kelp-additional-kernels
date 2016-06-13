package it.uniroma2.sag.kelp.kernel.tree.deltamatrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticDeltaMatrix implements DeltaMatrix {

	private Logger logger = LoggerFactory.getLogger(StaticDeltaMatrix.class);

	private final static int DEFAULTSIZE = 100;

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
		do {
			try {
				this.matrix[i][j] = v;
				return;
			} catch (ArrayIndexOutOfBoundsException e) {
				int maxElementSize = this.matrix.length;
				int newSize = Math.max(i, j) + 1;
				logger.warn("Increasing delta matrix size from " + maxElementSize + " to "
						+ newSize);
				matrix = resizeArray(matrix, newSize);
			}
		} while (true);
	}

	private static float[][] resizeArray(float[][] oldArray, int newSize) {
		int oldSize = oldArray.length;
		float[][] newArray = new float[newSize][newSize];
		for (int i = 0; i < oldSize; i++) {
			for (int j = 0; j < oldSize; j++) {
				newArray[i][j] = oldArray[i][j];
			}
		}
		return newArray;
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

	public static StaticDeltaMatrix getInstance() {
		return instance;
	}

}
