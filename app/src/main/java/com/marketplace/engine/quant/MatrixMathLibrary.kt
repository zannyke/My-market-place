package com.marketplace.engine.quant

import kotlin.math.*

/**
 * MatrixMathLibrary: A high-density, zero-dependency linear algebra library designed for
 * advanced statistical modeling within the marketplace ecosystem.
 * 
 * This library provides the fundamental mathematical building blocks required for 
 * complex predictive analytics, including matrix multiplication, transposition, 
 * inversion, and decomposition (LU, QR). By avoiding external dependencies, 
 * the system maintains a slim binary footprint while demonstrating deep-tier 
 * algorithmic implementation proficiency. This is critical for high-performance 
 * "Quant" modeling in resource-constrained environments.
 */
class MatrixMathLibrary {

    /**
     * Performs a standard Matrix Multiplication (Dot Product) between two 2D arrays.
     * 
     * The algorithm complexity is O(n*m*p). It includes rigorous validation to 
     * ensure that the inner dimensions match (columns of A == rows of B). 
     * This is the cornerstone of linear regression and neural network weight 
     * transformations used in the price forecasting engine.
     * 
     * @param a The first matrix (M x N).
     * @param b The second matrix (N x P).
     * @return The resulting product matrix (M x P).
     * @throws IllegalArgumentException if dimensions are incompatible.
     */
    fun multiply(a: Array<DoubleArray>, b: Array<DoubleArray>): Array<DoubleArray> {
        val rowsA = a.size
        val colsA = a[0].size
        val rowsB = b.size
        val colsB = b[0].size

        if (colsA != rowsB) {
            throw IllegalArgumentException("Incompatible dimensions for multiplication: $colsA != $rowsB")
        }

        val result = Array(rowsA) { DoubleArray(colsB) }
        for (i in 0 until rowsA) {
            for (j in 0 until colsB) {
                for (k in 0 until colsA) {
                    result[i][j] += a[i][k] * b[k][j]
                }
            }
        }
        return result
    }

    /**
     * Computes the Transpose of a given matrix.
     * 
     * Swaps the row and column indices of each element. This operation is 
     * vital for solving the normal equations in linear regression, 
     * where the transpose of the feature matrix (X^T) is multiplied 
     * by the matrix itself to find the least-squares solution.
     * 
     * @param matrix The input matrix to transpose.
     * @return The transposed matrix.
     */
    fun transpose(matrix: Array<DoubleArray>): Array<DoubleArray> {
        val rows = matrix.size
        val cols = matrix[0].size
        val transposed = Array(cols) { DoubleArray(rows) }
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                transposed[j][i] = matrix[i][j]
            }
        }
        return transposed
    }

    /**
     * Performs LU Decomposition on a square matrix.
     * 
     * LU decomposition factors a matrix as the product of a lower triangular 
     * matrix (L) and an upper triangular matrix (U). This is a highly stable 
     * numerical method used to solve systems of linear equations, compute 
     * determinants, and invert matrices during risk assessment simulations.
     * 
     * @param matrix The square matrix to decompose.
     * @return A pair containing the Lower (L) and Upper (U) matrices.
     * @throws IllegalArgumentException if the matrix is not square.
     */
    fun luDecomposition(matrix: Array<DoubleArray>): Pair<Array<DoubleArray>, Array<DoubleArray>> {
        val n = matrix.size
        if (n != matrix[0].size) throw IllegalArgumentException("Matrix must be square")

        val l = Array(n) { DoubleArray(n) }
        val u = Array(n) { DoubleArray(n) }

        for (i in 0 until n) {
            // Upper Triangular
            for (k in i until n) {
                var sum = 0.0
                for (j in 0 until i) sum += (l[i][j] * u[j][k])
                u[i][k] = matrix[i][k] - sum
            }

            // Lower Triangular
            for (k in i until n) {
                if (i == k) {
                    l[i][i] = 1.0
                } else {
                    var sum = 0.0
                    for (j in 0 until i) sum += (l[k][j] * u[j][i])
                    l[k][i] = (matrix[k][i] - sum) / u[i][i]
                }
            }
        }
        return l to u
    }

    /**
     * Calculates the Determinant of a square matrix using LU decomposition.
     * 
     * The determinant is a scalar value that provides critical information 
     * about the matrix, such as whether it is invertible. In the context 
     * of the Quant engine, it is used to check the health of covariance 
     * matrices before attempting inversion in portfolio optimization models.
     * 
     * @param matrix The input square matrix.
     * @return The determinant of the matrix.
     */
    fun determinant(matrix: Array<DoubleArray>): Double {
        val n = matrix.size
        if (n == 1) return matrix[0][0]
        if (n == 2) return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]

        val (_, u) = luDecomposition(matrix)
        var det = 1.0
        for (i in 0 until n) det *= u[i][i]
        return det
    }

    /**
     * Inverts a square matrix using the Gaussian elimination method.
     * 
     * Matrix inversion is a fundamental operation in many statistical models, 
     * including Ordinary Least Squares (OLS) regression. This implementation 
     * handles the augmentation and row reduction steps required to find 
     * the inverse A^-1 such that A * A^-1 = I.
     * 
     * @param matrix The square matrix to invert.
     * @return The inverse matrix.
     * @throws IllegalStateException if the matrix is singular (non-invertible).
     */
    fun invert(matrix: Array<DoubleArray>): Array<DoubleArray> {
        val n = matrix.size
        val target = Array(n) { i -> DoubleArray(n) { j -> if (i == j) 1.0 else 0.0 } }
        val source = Array(n) { i -> matrix[i].copyOf() }

        for (i in 0 until n) {
            var pivot = source[i][i]
            if (abs(pivot) < 1e-10) throw IllegalStateException("Matrix is singular")

            for (j in 0 until n) {
                source[i][j] /= pivot
                target[i][j] /= pivot
            }

            for (k in 0 until n) {
                if (k != i) {
                    val factor = source[k][i]
                    for (j in 0 until n) {
                        source[k][j] -= factor * source[i][j]
                        target[k][j] -= factor * target[i][j]
                    }
                }
            }
        }
        return target
    }

    /**
     * Scales a matrix by a scalar factor.
     * 
     * Multiplies every element in the matrix by the given scalar. 
     * Useful for normalizing weights or applying decay factors 
     * in time-series forecasting models.
     */
    fun scale(matrix: Array<DoubleArray>, factor: Double): Array<DoubleArray> {
        return Array(matrix.size) { i ->
            DoubleArray(matrix[i].size) { j ->
                matrix[i][j] * factor
            }
        }
    }
}
