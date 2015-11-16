/*
 * Copyright 2014 Simone Filice and Giuseppe Castellucci and Danilo Croce and Roberto Basili
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.uniroma2.sag.kelp.kernel.tree.deltamatrix;


/**
 * Sparse Delta Matrix
 * 
 * @author Danilo Croce
 * 
 */
public interface DeltaMatrix {
	
	public static final int NO_RESPONSE = -1;

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
	public void add(int i, int j, float v);

	/**
	 * Get a value from the matrix
	 * 
	 * @param i
	 *            row index
	 * @param j
	 *            column index
	 * @return value to retrieve from the delta_matrix[[i][j]
	 */
	public float get(int i, int j);

	/**
	 * Clear the delta matrix
	 */
	public void clear();

}
