module com.matrixcalc.gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;

    opens com.matrixcalc.gui to javafx.fxml;
    exports com.matrixcalc.gui;
    opens com.matrixcalculator.view to javafx.fxml;
    exports com.matrixcalculator.view;
}