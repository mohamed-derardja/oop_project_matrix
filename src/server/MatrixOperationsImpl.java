package server;

import common.MatrixOperations;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MatrixOperationsImpl extends UnicastRemoteObject implements MatrixOperations {
    
    public MatrixOperationsImpl() throws RemoteException {
        super();
    }
    
    @Override
    public double calculateDeterminant(double[][] matrix) throws RemoteException {
        int n = matrix.length;
        
        if (n == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        }
        
        double det = 0;
        for (int i = 0; i < n; i++) {
            det += Math.pow(-1, i) * matrix[0][i] * calculateDeterminant(getSubMatrix(matrix, 0, i));
        }
        
        return det;
    }
    
    private double[][] getSubMatrix(double[][] matrix, int row, int col) {
        int n = matrix.length;
        double[][] subMatrix = new double[n-1][n-1];
        int r = 0;
        
        for (int i = 0; i < n; i++) {
            if (i == row) continue;
            
            int c = 0;
            for (int j = 0; j < n; j++) {
                if (j == col) continue;
                subMatrix[r][c] = matrix[i][j];
                c++;
            }
            r++;
        }
        
        return subMatrix;
    }
    
    @Override
    public double[][] calculateInverse(double[][] matrix) throws RemoteException {
        int n = matrix.length;
        double det = calculateDeterminant(matrix);
        
        if (Math.abs(det) < 1e-10) {
            throw new RemoteException("Matrix is not invertible (determinant is zero)");
        }
        
        double[][] inverse = new double[n][n];
        
        if (n == 2) {
            inverse[0][0] = matrix[1][1] / det;
            inverse[0][1] = -matrix[0][1] / det;
            inverse[1][0] = -matrix[1][0] / det;
            inverse[1][1] = matrix[0][0] / det;
            return inverse;
        }
        
        double[][] cofactors = new double[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cofactors[i][j] = Math.pow(-1, i+j) * calculateDeterminant(getSubMatrix(matrix, i, j));
            }
        }
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inverse[i][j] = cofactors[j][i] / det;
            }
        }
        
        return inverse;
    }
    
    @Override
    public double[][] diagonalize(double[][] matrix) throws RemoteException {
        int n = matrix.length;
        double[][] diagonal = new double[n][n];
        
        boolean isDiagonal = true;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && Math.abs(matrix[i][j]) > 1e-10) {
                    isDiagonal = false;
                    break;
                }
            }
        }
        
        if (isDiagonal) {
            for (int i = 0; i < n; i++) {
                diagonal[i][i] = matrix[i][i];
            }
            return diagonal;
        }
        
        for (int i = 0; i < n; i++) {
            diagonal[i][i] = matrix[i][i];
        }
        
        return diagonal;
    }
} 