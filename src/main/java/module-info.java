module com.lab.laboratorsapte {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.lab.laboratorsapte to javafx.fxml;
    exports com.lab.laboratorsapte;
    exports com.lab.laboratorsapte.controller;
    exports com.lab.laboratorsapte.domain;
    opens com.lab.laboratorsapte.controller to javafx.fxml;
}