package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MatrixOperations extends Remote {
    double calculateDeterminant(double[][] matrix) throws RemoteException;
    double[][] calculateInverse(double[][] matrix) throws RemoteException;
    double[][] diagonalize(double[][] matrix) throws RemoteException;
} 