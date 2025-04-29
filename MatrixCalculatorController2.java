package com.matrixcalculator.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MatrixCalculatorController {

    @FXML private Spinner<Integer> rowsSpinnerA;
    @FXML private Spinner<Integer> colsSpinnerA;
    @FXML private Spinner<Integer> rowsSpinnerB;
    @FXML private Spinner<Integer> colsSpinnerB;
    @FXML private Button createMatrixAButton;
    @FXML private Button createMatrixBButton;
    @FXML private GridPane matrixAGrid;
    @FXML private GridPane matrixBGrid;
    @FXML private GridPane resultMatrixGrid;
    @FXML private ComboBox<String> operationComboBox;
    @FXML private Label statusLabel;
    @FXML private Label matrixALabel;
    @FXML private Label matrixBLabel;
    @FXML private Label resultLabel;
    
    private List<List<TextField>> matrixAFields = new ArrayList<>();
    private List<List<TextField>> matrixBFields = new ArrayList<>();
    private double[][] matrixA;
    private double[][] matrixB;
    private double[][] resultMatrix;
    
    @FXML
    public void initialize() {
        // Initialize spinners with value factories
        SpinnerValueFactory<Integer> valueFactoryRowsA = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3);
        SpinnerValueFactory<Integer> valueFactoryColsA = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3);
        SpinnerValueFactory<Integer> valueFactoryRowsB = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3);
        SpinnerValueFactory<Integer> valueFactoryColsB = 
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3);
        
        rowsSpinnerA.setValueFactory(valueFactoryRowsA);
        colsSpinnerA.setValueFactory(valueFactoryColsA);
        rowsSpinnerB.setValueFactory(valueFactoryRowsB);
        colsSpinnerB.setValueFactory(valueFactoryColsB);
        
        // Initialize operation combo box
        operationComboBox.getItems().addAll(
                "Addition (A + B)",
                "Subtraction (A - B)",
                "Multiplication (A × B)",
                "Determinant of A",
                "Transpose of A",
                "Inverse of A"
        );
        operationComboBox.getSelectionModel().selectFirst();
    }
    
    @FXML
    private void createMatrixA(ActionEvent event) {
        int rows = rowsSpinnerA.getValue();
        int cols = colsSpinnerA.getValue();
        createMatrix(matrixAGrid, rows, cols, matrixAFields);
        matrixA = new double[rows][cols];
        matrixALabel.setText("Matrix A (" + rows + "×" + cols + ")");
    }
    
    @FXML
    private void createMatrixB(ActionEvent event) {
        int rows = rowsSpinnerB.getValue();
        int cols = colsSpinnerB.getValue();
        createMatrix(matrixBGrid, rows, cols, matrixBFields);
        matrixB = new double[rows][cols];
        matrixBLabel.setText("Matrix B (" + rows + "×" + cols + ")");
    }
    
    private void createMatrix(GridPane grid, int rows, int cols, List<List<TextField>> matrixFields) {
        grid.getChildren().clear();
        matrixFields.clear();
        
        for (int i = 0; i < rows; i++) {
            List<TextField> rowFields = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                TextField field = new TextField("0");
                field.getStyleClass().add("matrix-cell");
                field.setPrefWidth(60);
                field.setPrefHeight(40);
                grid.add(field, j, i);
                rowFields.add(field);
            }
            matrixFields.add(rowFields);
        }
    }
    
    @FXML
    private void calculateResult(ActionEvent event) {
        try {
            readMatrixValues();
            String operation = operationComboBox.getValue();
            
            switch (operation) {
                case "Addition (A + B)":
                    performAddition();
                    break;
                case "Subtraction (A - B)":
                    performSubtraction();
                    break;
                case "Multiplication (A × B)":
                    performMultiplication();
                    break;
                case "Determinant of A":
                    calculateDeterminant();
                    break;
                case "Transpose of A":
                    calculateTranspose();
                    break;
                case "Inverse of A":
                    calculateInverse();
                    break;
            }
            
            statusLabel.setText("Operation completed successfully");
            statusLabel.getStyleClass().remove("error-text");
            statusLabel.getStyleClass().add("success-text");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.getStyleClass().remove("success-text");
            statusLabel.getStyleClass().add("error-text");
        }
    }
    
    private void readMatrixValues() {
        // Read Matrix A values
        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixA[0].length; j++) {
                try {
                    matrixA[i][j] = Double.parseDouble(matrixAFields.get(i).get(j).getText());
                } catch (NumberFormatException e) {
                    throw new NumberFormatException("Invalid number in Matrix A at position (" + (i+1) + "," + (j+1) + ")");
                }
            }
        }
        
        // Read Matrix B values if needed
        if (matrixB != null && operationComboBox.getValue().contains("B")) {
            for (int i = 0; i < matrixB.length; i++) {
                for (int j = 0; j < matrixB[0].length; j++) {
                    try {
                        matrixB[i][j] = Double.parseDouble(matrixBFields.get(i).get(j).getText());
                    } catch (NumberFormatException e) {
                        throw new NumberFormatException("Invalid number in Matrix B at position (" + (i+1) + "," + (j+1) + ")");
                    }
                }
            }
        }
    }
    
    private void performAddition() {
        if (matrixA.length != matrixB.length || matrixA[0].length != matrixB[0].length) {
            throw new IllegalArgumentException("Matrices must have the same dimensions for addition");
        }
        
        int rows = matrixA.length;
        int cols = matrixA[0].length;
        resultMatrix = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultMatrix[i][j] = matrixA[i][j] + matrixB[i][j];
            }
        }
        
        displayResultMatrix(rows, cols);
        resultLabel.setText("Result: A + B (" + rows + "×" + cols + ")");
    }
    
    private void performSubtraction() {
        if (matrixA.length != matrixB.length || matrixA[0].length != matrixB[0].length) {
            throw new IllegalArgumentException("Matrices must have the same dimensions for subtraction");
        }
        
        int rows = matrixA.length;
        int cols = matrixA[0].length;
        resultMatrix = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultMatrix[i][j] = matrixA[i][j] - matrixB[i][j];
            }
        }
        
        displayResultMatrix(rows, cols);
        resultLabel.setText("Result: A - B (" + rows + "×" + cols + ")");
    }
    
    private void performMultiplication() {
        if (matrixA[0].length != matrixB.length) {
            throw new IllegalArgumentException("Number of columns in Matrix A must equal number of rows in Matrix B");
        }
        
        int rows = matrixA.length;
        int cols = matrixB[0].length;
        int common = matrixA[0].length;
        resultMatrix = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultMatrix[i][j] = 0;
                for (int k = 0; k < common; k++) {
                    resultMatrix[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        
        displayResultMatrix(rows, cols);
        resultLabel.setText("Result: A × B (" + rows + "×" + cols + ")");
    }
    
    private void calculateDeterminant() {
        if (matrixA.length != matrixA[0].length) {
            throw new IllegalArgumentException("Matrix must be square to calculate determinant");
        }
        
        double det = determinant(matrixA);
        resultMatrixGrid.getChildren().clear();
        
        Label detLabel = new Label("Determinant of A = " + det);
        detLabel.getStyleClass().add("result-value");
        detLabel.setPadding(new Insets(20));
        resultMatrixGrid.add(detLabel, 0, 0);
        
        resultLabel.setText("Result: det(A)");
    }
    
    private double determinant(double[][] matrix) {
        // Simple implementation for 2x2 and 3x3 matrices
        int n = matrix.length;
        
        if (n == 1) {
            return matrix[0][0];
        }
        
        if (n == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        }
        
        if (n == 3) {
            return matrix[0][0] * (matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1])
                 - matrix[0][1] * (matrix[1][0] * matrix[2][2] - matrix[1][2] * matrix[2][0])
                 + matrix[0][2] * (matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0]);
        }
        
        throw new UnsupportedOperationException("Determinant calculation for matrices larger than 3x3 is not implemented");
    }
    
    private void calculateTranspose() {
        int rows = matrixA.length;
        int cols = matrixA[0].length;
        resultMatrix = new double[cols][rows];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                resultMatrix[j][i] = matrixA[i][j];
            }
        }
        
        displayResultMatrix(cols, rows);
        resultLabel.setText("Result: A^T (" + cols + "×" + rows + ")");
    }
    
    private void calculateInverse() {
        if (matrixA.length != matrixA[0].length) {
            throw new IllegalArgumentException("Matrix must be square to calculate inverse");
        }
        
        // Only implement for 2x2 and 3x3 matrices for simplicity
        int n = matrixA.length;
        double det = determinant(matrixA);
        
        if (Math.abs(det) < 1e-10) {
            throw new ArithmeticException("Matrix is singular, inverse does not exist");
        }
        
        resultMatrix = new double[n][n];
        
        if (n == 2) {
            resultMatrix[0][0] = matrixA[1][1] / det;
            resultMatrix[0][1] = -matrixA[0][1] / det;
            resultMatrix[1][0] = -matrixA[1][0] / det;
            resultMatrix[1][1] = matrixA[0][0] / det;
        } else if (n == 3) {
            // A more complex algorithm for 3x3 matrix inverse
            // (simplified implementation)
            double a = matrixA[0][0], b = matrixA[0][1], c = matrixA[0][2];
            double d = matrixA[1][0], e = matrixA[1][1], f = matrixA[1][2];
            double g = matrixA[2][0], h = matrixA[2][1], i = matrixA[2][2];
            
            resultMatrix[0][0] = (e*i - f*h) / det;
            resultMatrix[0][1] = (c*h - b*i) / det;
            resultMatrix[0][2] = (b*f - c*e) / det;
            resultMatrix[1][0] = (f*g - d*i) / det;
            resultMatrix[1][1] = (a*i - c*g) / det;
            resultMatrix[1][2] = (c*d - a*f) / det;
            resultMatrix[2][0] = (d*h - e*g) / det;
            resultMatrix[2][1] = (b*g - a*h) / det;
            resultMatrix[2][2] = (a*e - b*d) / det;
        } else {
            throw new UnsupportedOperationException("Inverse calculation for matrices larger than 3x3 is not implemented");
        }
        
        displayResultMatrix(n, n);
        resultLabel.setText("Result: A^(-1) (" + n + "×" + n + ")");
    }
    
    private void displayResultMatrix(int rows, int cols) {
        resultMatrixGrid.getChildren().clear();
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                TextField field = new TextField(String.format("%.2f", resultMatrix[i][j]));
                field.getStyleClass().add("result-cell");
                field.setEditable(false);
                field.setPrefWidth(60);
                field.setPrefHeight(40);
                resultMatrixGrid.add(field, j, i);
            }
        }
    }
    
    @FXML
    private void saveMatrix(ActionEvent event) {
        if (resultMatrix == null) {
            statusLabel.setText("No result to save");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Matrix");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Matrix Files", "*.mtx"));
        File file = fileChooser.showSaveDialog(null);
        
        if (file != null) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(resultMatrix);
                statusLabel.setText("Matrix saved successfully");
                statusLabel.getStyleClass().remove("error-text");
                statusLabel.getStyleClass().add("success-text");
            } catch (IOException e) {
                statusLabel.setText("Error: " + e.getMessage());
                statusLabel.getStyleClass().remove("success-text");
                statusLabel.getStyleClass().add("error-text");
            }
        }
    }
    
    @FXML
    private void loadMatrix(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Matrix");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Matrix Files", "*.mtx"));
        File file = fileChooser.showOpenDialog(null);
        
        if (file != null) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                double[][] loadedMatrix = (double[][]) in.readObject();
                matrixA = loadedMatrix;
                int rows = matrixA.length;
                int cols = matrixA[0].length;
                
                // Update UI
                rowsSpinnerA.getValueFactory().setValue(rows);
                colsSpinnerA.getValueFactory().setValue(cols);
                createMatrix(matrixAGrid, rows, cols, matrixAFields);
                
                // Fill the UI with loaded values
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        matrixAFields.get(i).get(j).setText(String.valueOf(matrixA[i][j]));
                    }
                }
                
                matrixALabel.setText("Matrix A (" + rows + "×" + cols + ")");
                statusLabel.setText("Matrix loaded successfully");
                statusLabel.getStyleClass().remove("error-text");
                statusLabel.getStyleClass().add("success-text");
            } catch (IOException | ClassNotFoundException e) {
                statusLabel.setText("Error: " + e.getMessage());
                statusLabel.getStyleClass().remove("success-text");
                statusLabel.getStyleClass().add("error-text");
            }
        }
    }
    
    @FXML
    private void clearMatrices(ActionEvent event) {
        matrixAGrid.getChildren().clear();
        matrixBGrid.getChildren().clear();
        resultMatrixGrid.getChildren().clear();
        matrixAFields.clear();
        matrixBFields.clear();
        matrixA = null;
        matrixB = null;
        resultMatrix = null;
        matrixALabel.setText("Matrix A");
        matrixBLabel.setText("Matrix B");
        resultLabel.setText("Result");
        statusLabel.setText("Matrices cleared");
    }
}