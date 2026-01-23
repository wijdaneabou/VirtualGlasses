package com.virtualglasses.views;

import com.virtualglasses.controllers.SceneController;
import com.virtualglasses.controllers.TryGlassesController;
import com.virtualglasses.controllers.TryGlassesController.ImageUpdateCallback;
import com.virtualglasses.models.Glasse;
import com.virtualglasses.models.GlassesDao;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class TryGlassesInterface {

    private Scene scene;
    private ImageView img;
    private SceneController sceneController;
    private VBox glassesList;
    private List<Image> GlassesTrouvee;
    private Button GlassesSlectioner;
    private TryGlassesController controller;
	private Image GlassesImage;

    public TryGlassesInterface(SceneController sceneController) {
        this.sceneController = sceneController;
        this.controller = new TryGlassesController(sceneController);
        this.controller.setTryGlassesInterface(this);

        BorderPane root = new BorderPane();
        
        HBox topBar = createTopBar();
        root.setTop(topBar);

        StackPane camera = new StackPane();
        
        camera.getStyleClass().add("camera");
        Rectangle clip = new Rectangle();
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        
        img = new ImageView();
        img.setFitWidth(600);
        img.setFitHeight(450);
        img.setPreserveRatio(true);
        img.setClip(clip);
        
        clip.widthProperty().bind(img.fitWidthProperty());
        clip.heightProperty().bind(img.fitHeightProperty());
        
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.2, 0.2, 0.2, 0.5));
        
        camera.setEffect(dropShadow);
        camera.getChildren().add(img);
        camera.setPadding(new Insets(15));
       
        StackPane center = new StackPane(camera);
        center.getStyleClass().add("center");
        
        camera.prefWidthProperty().bind(center.widthProperty().multiply(0.95));
        camera.prefHeightProperty().bind(center.heightProperty().multiply(0.90));
        root.setCenter(center);

        Label photo= new Label("Prenez une photo ou choisissez-en une depuis la galerie");
        photo.getStyleClass().add("label");
        
        HBox bottomBar = createBottomBar();
        
        VBox bottom = new VBox(10, photo, bottomBar);
        bottom.getStyleClass().add("bottom");
        bottom.setAlignment(Pos.CENTER);
        root.setBottom(bottom);

        VBox rightContainer = createSidebar();
        root.setRight(rightContainer);
        
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/TryGlassesInterface.css").toExternalForm());
        startCamera();
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.getStyleClass().add("top-bar");
        topBar.setPadding(new Insets(10, 20, 10, 20));
    
        Label titleLabel = new Label("Lunettes virtuelles");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");
        titleLabel.setPadding(new Insets(0, 0, 0, 15));

        
        HBox rightControls = new HBox(10);
        rightControls.setAlignment(Pos.CENTER_RIGHT);
        
        Button helpButton = new Button("?");
        helpButton.getStyleClass().add("help");
        
        rightControls.getChildren().add(helpButton);
        
        topBar.getChildren().addAll(titleLabel);
      
        HBox.setHgrow(rightControls, Priority.ALWAYS);
        topBar.getChildren().add(rightControls);
        
        return topBar;
    }
    
    private HBox createBottomBar() {
        HBox bottomBar = new HBox();
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(15));
        bottomBar.getStyleClass().add("bottom-bar");
        
        HBox leftSection = new HBox(10);
        HBox centerSection = new HBox(20);
        HBox rightSection = new HBox(10);
       
        HBox.setHgrow(leftSection, Priority.ALWAYS);
        HBox.setHgrow(centerSection, Priority.ALWAYS);
        HBox.setHgrow(rightSection, Priority.ALWAYS);
      
        leftSection.setAlignment(Pos.CENTER_LEFT);
        centerSection.setAlignment(Pos.CENTER);
        rightSection.setAlignment(Pos.CENTER_RIGHT);
        
        Button backBtn = new Button("Retour");
        backBtn.getStyleClass().addAll("controlbtn", "backbtn");
       
        try {
            ImageView backIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/back-arrow.png")));
            backIcon.setFitHeight(16);
            backIcon.setFitWidth(16);
            backBtn.setGraphic(backIcon);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } 
        
        backBtn.setOnAction(e -> {
            controller.goBack();
        });

        Button cameraBtn = new Button();
        cameraBtn.getStyleClass().addAll("controlbtn", "camerabtn");
        try {
            ImageView cameraIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/webcam.png")));
            cameraIcon.setFitHeight(24);
            cameraIcon.setFitWidth(24);
            cameraBtn.setGraphic(cameraIcon);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        cameraBtn.setOnAction(e -> {
            restartCamera();
        });
        
        
     
        Button galleryBtn = new Button();
        galleryBtn.getStyleClass().addAll("controlbtn", "gallerybtn");
        try {
            ImageView galleryIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/gallery-icon.png")));
            galleryIcon.setFitHeight(24);
            galleryIcon.setFitWidth(24);
            galleryBtn.setGraphic(galleryIcon);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        galleryBtn.setOnAction(e -> {
            controller.selectImage();
        });

        leftSection.getChildren().add(backBtn);
        centerSection.getChildren().add(cameraBtn);
        rightSection.getChildren().add(galleryBtn);

        bottomBar.getChildren().addAll(leftSection, centerSection, rightSection);
        
        return bottomBar;
    }
 

    private VBox createSidebar() {
    	GlassesTrouvee = loadAvailableGlasses();

        glassesList = new VBox(15);
        glassesList.setPadding(new Insets(15));
        glassesList.setAlignment(Pos.TOP_CENTER);
        glassesList.getStyleClass().add("glasses-list");

        Label glassesTitle = new Label("Choisissez vos lunettes");
        glassesTitle.getStyleClass().add("glastitle");

        populateGlassesList();

        ScrollPane glassesScrollPane = new ScrollPane(glassesList);
        glassesScrollPane.setFitToWidth(true);
        glassesScrollPane.getStyleClass().add("glasses-scroll");

        VBox actionButtons = createActionButtons();

        VBox rightContainer = new VBox(15);
        rightContainer.getStyleClass().add("rightsidebar");
        rightContainer.setPrefWidth(250);

        rightContainer.getChildren().addAll(
            glassesTitle,
            new Separator(),
            glassesScrollPane,
            new Separator(),
            actionButtons
        );
        
        return rightContainer;
    }
    
    private List<Image> loadAvailableGlasses() {
        List<Image> glasses = new ArrayList<>();
        try {

            GlassesDao glassesDao = new GlassesDao();

            List<Glasse> glassesList = glassesDao.getRandomGlasses(8); 

            for (Glasse glasse : glassesList) {
                if (glasse.getImageData() != null) {
                    Image image = new Image(new java.io.ByteArrayInputStream(glasse.getImageData()));
                    glasses.add(image);
                }
            }
            
            if (glasses.isEmpty()) {
                glasses.add(new Image(getClass().getResourceAsStream("/images/glasses.png")));
                glasses.add(new Image(getClass().getResourceAsStream("/images/glasses2.jpg")));
                glasses.add(new Image(getClass().getResourceAsStream("/images/glasses3.jpg")));
                glasses.add(new Image(getClass().getResourceAsStream("/images/glasses4.jpg")));
                glasses.add(new Image(getClass().getResourceAsStream("/images/glasses5.jpg")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return glasses;
    }
    
    private void populateGlassesList() {
        glassesList.getChildren().clear();
        List<Button> glassesButtons = new ArrayList<>();
        for (int i = 0; i < GlassesTrouvee.size(); i++) {
            Image glassesImage = GlassesTrouvee.get(i);
         
            VBox glassesCard = new VBox(5);
            glassesCard.getStyleClass().add("glasses-card");
            glassesCard.setPadding(new Insets(10));
      
            ImageView glassesImageView = new ImageView(glassesImage);
            glassesImageView.setFitWidth(150);
            glassesImageView.setFitHeight(50);
            glassesImageView.setPreserveRatio(true);
            
            Label modelName = new Label("Modèle " + (i + 1));
            modelName.getStyleClass().add("model-name");
          
            glassesCard.getChildren().addAll(glassesImageView, modelName);
            glassesCard.setAlignment(Pos.CENTER);
          
            Button glassesButton = new Button();
            glassesButton.setGraphic(glassesCard);
            glassesButton.getStyleClass().add("glasses-button");
            
            glassesButton.setOnAction(e -> {
                controller.setSelectedGlassesImage(glassesImage);
               
                if (GlassesSlectioner != null) {
                	GlassesSlectioner.getStyleClass().remove("selected-glasses");
                }
                glassesButton.getStyleClass().add("glassSelected");
                GlassesSlectioner = glassesButton;
            });
            
            glassesButtons.add(glassesButton);
            glassesList.getChildren().add(glassesButton);
        }
    }

    public Scene getScene() {
        return scene;
    }
    public void restartCamera() {

        startCamera();
    }

    private void startCamera() {
        img.setVisible(true);
        img.setOpacity(1.0);

        controller.startCamera(new ImageUpdateCallback() {
            @Override
            public void updateImage(Image image) {
                img.setImage(image);
            }
        });
    }
    public void updateImageView(Image image) {
        if (image != null) {
            this.img.setImage(image);

            this.img.setFitWidth(600);
            this.img.setFitHeight(450);
            this.img.setVisible(true);
            this.img.setOpacity(1.0);
            
            Rectangle clip = new Rectangle(
                0, 0, this.img.getFitWidth(), this.img.getFitHeight());
            this.img.setClip(clip);

            
        }
    }
    
    private VBox createActionButtons() {
        VBox actionButtons = new VBox(10);
        actionButtons.setAlignment(Pos.CENTER);
        actionButtons.setPadding(new Insets(10, 10, 20, 10));
        
        Button aiRecommendationButton = new Button("Recommendation AI");
        aiRecommendationButton.getStyleClass().add("AiBtn");
        aiRecommendationButton.setOnAction(e -> {
            controller.generateAIRecommendation();
        });
        
        actionButtons.getChildren().addAll(aiRecommendationButton);
        
        return actionButtons;
    }
    public void setSelectedGlassesImage(Image image) {
        this.GlassesImage = image;
    }
    public void setGlassesImage(Image glassesImage) {
        if (glassesImage != null) {
            this.GlassesImage = glassesImage;
           
            controller.setSelectedGlassesImage(glassesImage);

            if (GlassesSlectioner != null) {
            	GlassesSlectioner.getStyleClass().remove("selected-glasses");
            	GlassesSlectioner = null; 
            }

        } 
    }
}