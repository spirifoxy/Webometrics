package com.spirifoxy.app;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("MainView.fxml"));

        Parent root = loader.load();

        primaryStage.setTitle("Webometrics");
        primaryStage.setResizable(false);

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        /*MainViewController controller = loader.getController();
        controller.setMain(this);*/
    }

    public static void main(String[] args) {
        launch(args);
    }
}
