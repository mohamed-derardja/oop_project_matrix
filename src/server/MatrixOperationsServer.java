package server;

import common.MatrixOperations;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MatrixOperationsServer {
    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "localhost");
            
            MatrixOperationsImpl matrixOps = new MatrixOperationsImpl();
            
            Registry registry = null;
            try {
                registry = LocateRegistry.createRegistry(1099);
                System.out.println("Java RMI registry created.");
            } catch (Exception e) {
                System.out.println("Using existing registry");
                registry = LocateRegistry.getRegistry();
            }
            
            registry.rebind("MatrixOperations", matrixOps);
            System.out.println("MatrixOperationsImpl bound in registry");
            System.out.println("Matrix Operations Server is running...");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
} 