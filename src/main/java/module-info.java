module com.example.dalrental {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires kotlin.stdlib;

    opens com.example.dalrental to javafx.fxml;
    exports com.example.dalrental;
    exports com.example.dalrental.controllers;
    opens com.example.dalrental.controllers to javafx.fxml;
}