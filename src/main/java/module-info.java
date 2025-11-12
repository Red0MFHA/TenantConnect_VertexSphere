module com.example.tenantconnect {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.dlsc.formsfx;

    opens com.example.tenantconnect to javafx.fxml;
    opens com.example.tenantconnect.controllers to javafx.fxml;

    exports com.example.tenantconnect;
    exports com.example.tenantconnect.controllers;
}