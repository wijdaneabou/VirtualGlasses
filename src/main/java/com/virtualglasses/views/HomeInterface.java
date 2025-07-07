package com.virtualglasses.views;

import com.virtualglasses.controllers.HomeController;
import com.virtualglasses.controllers.SceneController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class HomeInterface {
    private Scene scene;

    public HomeInterface(SceneController sceneController) {
    	
        StackPane root = new StackPane();
        root.getStyleClass().add("root");

        ImageView backgroundImg = new ImageView(new Image(getClass().getResourceAsStream("/images/home.PNG")));
        backgroundImg.setPreserveRatio(false);
        backgroundImg.setFitWidth(800);
        backgroundImg.setFitHeight(600);

        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/logo.jpg")));
        logo.setFitHeight(80);
        logo.setPreserveRatio(true);

        AnchorPane logoplace = new AnchorPane(logo);
        AnchorPane.setTopAnchor(logo, 0.0);
        AnchorPane.setLeftAnchor(logo, 0.0);

        Label txt = new Label("Découvrez la paire idéale qui vous correspond");
        txt.getStyleClass().add("txt");

        Button tryBtn = new Button("Essayage virtuel");
        Button recommendBtn = new Button("Recommandation AI");
        tryBtn.getStyleClass().addAll("actionbtn", "trybtn");
        recommendBtn.getStyleClass().addAll("actionbtn", "recommendbtn");

        HBox buttonBox = new HBox(20, tryBtn, recommendBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox footer = new VBox(10, txt, buttonBox);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20));

        BorderPane zone = new BorderPane();
        zone.setTop(logoplace);
        zone.setBottom(footer);

        root.getChildren().addAll(backgroundImg, zone);

        this.scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/home.css").toExternalForm());

        backgroundImg.fitWidthProperty().bind(scene.widthProperty());
        backgroundImg.fitHeightProperty().bind(scene.heightProperty());

        HomeController controller = new HomeController(sceneController);
        tryBtn.setOnAction(e -> controller.goToTryGlasses());
        recommendBtn.setOnAction(e -> controller.goToRecommendation());
    }

    public Scene getScene() {
        return scene;
    }
}