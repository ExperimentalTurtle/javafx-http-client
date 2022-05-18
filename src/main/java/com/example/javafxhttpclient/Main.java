package com.example.javafxhttpclient;

import com.example.javafxhttpclient.core.utils.Constants;
import com.example.javafxhttpclient.core.utils.FileManipulator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * TODO -> send http request
 * TODO -> show http response
 * TODO -> save everything in local sqlite using jdbc and add when exiting show alert
 * TODO -> create folder, request (from above treeview button and context menu)
 * TODO -> filter tree items
 *
 * TODO -> tree view single click toggle not two
 */
public class Main extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader mainFxml = FileManipulator.fxmlLoader(Constants.mainFXML);
        String mainCss = FileManipulator.css(Constants.mainCss);

        // creating scene
        Scene scene = new Scene(mainFxml.load());
        scene.getStylesheets().add(mainCss);

        // initializing
        stage.setTitle("Http Client");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }
}