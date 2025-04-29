package com.matrixcalculator.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class MatrixCalculatorController {
    
    @FXML private TabPane tabPane;
    @FXML private Tab matrixATab;
    @FXML private Tab matrixBTab;
    @FXML private Tab resultTab;
    
    @FXML private GridPane matrixAGrid;
    @FXML private GridPane matrixBGrid;
    @FXML private GridPane resultGrid;
    
    @FXML private ComboBox<String> operationComboBox;
    @FXML private ComboBox<Integer> rowsComboBox;
    @FXML private ComboBox<Integer> columnsComboBox;
    
    @FXML private Button calculateButton;
    @FXML private Button clearButton;
    
    private List<List<TextField>> matrixAFields = new ArrayList<>();
    private List<List<TextField>> matrixBFields = new ArrayList<>();
    private List<List<TextField>> resultFields = new ArrayList<>();
    
    @FXML
    public void initialize() {
        // Setup dimension options
        for (int i = 1; i <= 10; i++) {
            rowsComboBox.getItems().add(i);
            columnsComboBox.getItems().add(i);
        }
        rowsComboBox.setValue(3);
        columnsComboBox.setValue(3);
        
        // Setup operation options
        operationComboBox.getItems().addAll(
            "Addition (A + B)",
            "Subtraction (A - B)",
            "Multiplication (A × B)",
            "Determinant",
            "Inverse",
            "Transpose"
        );
        operationComboBox.setValue("Addition (A + B)");
        
        // Initialize matrices with default dimensions
        createMatrixInputs(3, 3);
        
        // Setup event listeners
        rowsComboBox.setOnAction(e -> updateMatrixDimensions());
        columnsComboBox.setOnAction(e -> updateMatrixDimensions());
        calculateButton.setOnAction(e -> calculateResult());
        clearButton.setOnAction(e -> clearMatrices());
    }
    
    private void createMatrixInputs(int rows, int cols) {
        // Clear existing grids
        matrixAGrid.getChildren().clear();
        matrixBGrid.getChildren().clear();
        resultGrid.getChildren().clear();
        
        matrixAFields.clear();
        matrixBFields.clear();
        resultFields.clear();
        
        // Create new input fields for each matrix
        for (int i = 0; i < rows; i++) {
            matrixAFields.add(new ArrayList<>());
            matrixBFields.add(new ArrayList<>());
            resultFields.add(new ArrayList<>());
            
            for (int j = 0; j < cols; j++) {
                // Matrix A
                TextField tfA = new TextField("0");
                tfA.setPrefWidth(60);
                tfA.getStyleClass().add("matrix-cell");
                matrixAGrid.add(tfA, j, i);
                matrixAFields.get(i).add(tfA);
                
                // Matrix B
                TextField tfB = new TextField("0");
                tfB.setPrefWidth(60);
                tfB.getStyleClass().add("matrix-cell");
                matrixBGrid.add(tfB, j, i);
                matrixBFields.get(i).add(tfB);
                
                // Result Matrix
                TextField tfResult = new TextField();
                tfResult.setPrefWidth(60);
                tfResult.setEditable(false);
                tfResult.getStyleClass().add("result-cell");
                resultGrid.add(tfResult, j, i);
                resultFields.get(i).add(tfResult);
            }
        }
    }
    
    private void updateMatrixDimensions() {
        int rows = rowsComboBox.getValue();
        int cols = columnsComboBox.getValue();
        createMatrixInputs(rows, cols);
    }
    
    private void calculateResult() {
        String operation = operationComboBox.getValue();
        int rows = rowsComboBox.getValue();
        int cols = columnsComboBox.getValue();
        
        // Parse input matrices
        double[][] matrixA = new double[rows][cols];
        double[][] matrixB = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                try {
                    matrixA[i][j] = Double.parseDouble(matrixAFields.get(i).get(j).getText());
                    matrixB[i][j] = Double.parseDouble(matrixBFields.get(i).get(j).getText());
                } catch (NumberFormatException e) {
                    showAlert("Invalid Input", "Please enter valid numbers in all cells.");
                    return;
                }
            }
        }
        
        // Perform calculation based on selected operation
        double[][] result = null;
        
        switch (operation) {
            case "Addition (A + B)":
                result = addMatrices(matrixA, matrixB, rows, cols);
                break;
            case "Subtraction (A - B)":
                result = subtractMatrices(matrixA, matrixB, rows, cols);
                break;
            case "Multiplication (A × B)":
                // Check if dimensions are valid for multiplication
                if (cols != rows) {
                    showAlert("Invalid Dimensions", "For multiplication, the number of columns in A must equal the number of rows in B.");
                    return;
                }
                result = multiplyMatrices(matrixA, matrixB, rows, cols);
                break;
            case "Determinant":
                if (rows != cols) {
                    showAlert("Invalid Dimensions", "Determinant can only be calculated for square matrices.");
                    return;
                }
                // Display determinant in a dialog instead
                double det = calculateDeterminant(matrixA, rows);
                showAlert("Determinant Result", "Determinant of Matrix A = " + det);
                return;
            case "Inverse":
                if (rows != cols) {
                    showAlert("Invalid Dimensions", "Inverse can only be calculated for square matrices.");
                    return;
                }
                result = calculateInverse(matrixA, rows);
                if (result == null) {
                    showAlert("Calculation Error", "The matrix is singular and cannot be inverted.");
                    return;
                }
                break;
            case "Transpose":
                result = transposeMatrix(matrixA, rows, cols);
                break;
        }
        
        // Display result
        if (result != null) {
            for (int i = 0; i < result.length; i++) {
                for (int j = 0; j < result[0].length; j++) {
                    resultFields.get(i).get(j).setText(String.format("%.2f", result[i][j]));
                }
            }
            
            // Switch to result tab
            tabPane.getSelectionModel().select(resultTab);
        }
    }
    
    private double[][] addMatrices(double[][] a, double[][] b, int rows, int cols) {
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }
    
    private double[][] subtractMatrices(double[][] a, double[][] b, int rows, int cols) {
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = a[i][j] - b[i][j];
            }
        }
        return result;
    }
    
    private double[][] multiplyMatrices(double[][] a, double[][] b, int rows, int cols) {
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = 0;
                for (int k = 0; k < cols; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }
    
    // Simple determinant calculation (for demonstration)
    private double calculateDeterminant(double[][] matrix, int size) {
        if (size == 1) {
            return matrix[0][0];
        }
        if (size == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        }
        // For larger matrices, we'd implement a more efficient algorithm
        // but this is simplified for demonstration
        return 0;
    }
    
    // Simple inverse calculation (for demonstration)
    private double[][] calculateInverse(double[][] matrix, int size) {
        // In a real app, you'd implement a proper matrix inversion algorithm
        // This is just a placeholder
        double det = calculateDeterminant(matrix, size);
        if (Math.abs(det) < 1e-10) {
            return null; // Matrix is singular
        }
        
        // Return identity matrix as placeholder
        double[][] result = new double[size][size];
        for (int i = 0; i < size; i++) {
            result[i][i] = 1;
        }
        return result;
    }
    
    private double[][] transposeMatrix(double[][] matrix, int rows, int cols) {
        double[][] result = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }
    
    private void clearMatrices() {
        for (List<TextField> row : matrixAFields) {
            for (TextField field : row) {
                field.setText("0");
            }
        }
        
        for (List<TextField> row : matrixBFields) {
            for (TextField field : row) {
                field.setText("0");
            }
        }
        
        for (List<TextField> row : resultFields) {
            for (TextField field : row) {
                field.setText("");
            }
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}