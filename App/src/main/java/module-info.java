module com.example.map_proiect_extins {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.example.map_proiect_extins to javafx.fxml;
    exports com.example.map_proiect_extins;

    exports com.example.map_proiect_extins.controller;
    opens com.example.map_proiect_extins.controller to javafx.fxml;
    opens com.example.map_proiect_extins.domain to javafx.base;

    requires org.apache.pdfbox;
}