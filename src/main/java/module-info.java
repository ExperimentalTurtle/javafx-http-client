module com.example.javafxhttpclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires reactfx;
    requires wellbehavedfx;
    requires java.datatransfer;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;

    exports com.example.javafxhttpclient.controllers;
    opens com.example.javafxhttpclient.controllers to javafx.fxml;

    exports com.example.javafxhttpclient;
    opens com.example.javafxhttpclient to javafx.fxml;

    exports com.example.javafxhttpclient.core.fxml;
    opens com.example.javafxhttpclient.core.fxml to javafx.fxml;

    exports com.example.javafxhttpclient.controllers.tabs;
    opens com.example.javafxhttpclient.controllers.tabs to javafx.fxml;
}