package com.virtualglasses.VirtualGlassesApp1;

import com.virtualglasses.controllers.SceneController;
import com.virtualglasses.views.HomeInterface;

import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        SceneController sceneController = new SceneController(primaryStage);
        HomeInterface homeInterface = new HomeInterface(sceneController);
        sceneController.setScene(homeInterface.getScene());
        primaryStage.setTitle("Virtual Glasses App");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
