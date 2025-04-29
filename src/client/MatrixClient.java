package client;

import common.MatrixOperations;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class MatrixClient {
    private JFrame frame;
    private JComboBox<String> matrixSizeCombo;
    private JPanel matrixPanel;
    private JTextField[][] matrixFields;
    private JButton calculateButton;
    private JComboBox<String> operationCombo;
    private JTextArea resultArea;
    
    private MatrixOperations matrixOps;
    private int matrixSize;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MatrixClient().initialize();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public void initialize() throws Exception {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            matrixOps = (MatrixOperations) registry.lookup("MatrixOperations");
            System.out.println("Connected to Matrix Operations Server");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
            throw e;
        }
        
        frame = new JFrame("Matrix Operations");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Matrix Size:"));
        matrixSizeCombo = new JComboBox<>(new String[]{"2x2", "3x3", "4x4"});
        topPanel.add(matrixSizeCombo);
        
        matrixPanel = new JPanel();
        matrixSize = 2;
        createMatrixInputs(matrixSize);
        
        JPanel operationPanel = new JPanel();
        operationPanel.add(new JLabel("Operation:"));
        operationCombo = new JComboBox<>(new String[]{"Determinant", "Inverse", "Diagonalize"});
        operationPanel.add(operationCombo);
        calculateButton = new JButton("Calculate");
        operationPanel.add(calculateButton);
        
        resultArea = new JTextArea(10, 40);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(matrixPanel, BorderLayout.CENTER);
        frame.add(operationPanel, BorderLayout.SOUTH);
        frame.add(scrollPane, BorderLayout.EAST);
        
        matrixSizeCombo.addActionListener(e -> {
            String selected = (String) matrixSizeCombo.getSelectedItem();
            switch (selected) {
                case "2x2": matrixSize = 2; break;
                case "3x3": matrixSize = 3; break;
                case "4x4": matrixSize = 4; break;
            }
            createMatrixInputs(matrixSize);
            frame.revalidate();
            frame.repaint();
        });
        
        calculateButton.addActionListener(e -> performCalculation());
        
        frame.setVisible(true);
    }
    
    private void createMatrixInputs(int size) {
        matrixPanel.removeAll();
        matrixPanel.setLayout(new GridLayout(size, size, 5, 5));
        
        matrixFields = new JTextField[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrixFields[i][j] = new JTextField(5);
                matrixFields[i][j].setText("0");
                matrixPanel.add(matrixFields[i][j]);
            }
        }
    }
    
    private void performCalculation() {
        try {
            double[][] matrix = new double[matrixSize][matrixSize];
            for (int i = 0; i < matrixSize; i++) {
                for (int j = 0; j < matrixSize; j++) {
                    try {
                        matrix[i][j] = Double.parseDouble(matrixFields[i][j].getText());
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(frame, 
                                "Invalid number at position [" + (i+1) + "," + (j+1) + "]", 
                                "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            
            String operation = (String) operationCombo.getSelectedItem();
            StringBuilder result = new StringBuilder();
            
            switch (operation) {
                case "Determinant":
                    double det = matrixOps.calculateDeterminant(matrix);
                    result.append("Determinant = ").append(det);
                    break;
                    
                case "Inverse":
                    try {
                        double[][] inverse = matrixOps.calculateInverse(matrix);
                        result.append("Inverse Matrix:\n");
                        appendMatrix(result, inverse);
                    } catch (Exception e) {
                        result.append("Error: ").append(e.getMessage());
                    }
                    break;
                    
                case "Diagonalize":
                    try {
                        double[][] diagonal = matrixOps.diagonalize(matrix);
                        if (diagonal != null) {
                            result.append("Diagonal Matrix:\n");
                            appendMatrix(result, diagonal);
                        } else {
                            result.append("Matrix cannot be diagonalized");
                        }
                    } catch (Exception e) {
                        result.append("Error: ").append(e.getMessage());
                    }
                    break;
            }
            
            resultArea.setText(result.toString());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, 
                    "Error performing calculation: " + e.getMessage(), 
                    "Calculation Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void appendMatrix(StringBuilder sb, double[][] matrix) {
        for (double[] row : matrix) {
            for (double val : row) {
                sb.append(String.format("%10.4f ", val));
            }
            sb.append("\n");
        }
    }
} 