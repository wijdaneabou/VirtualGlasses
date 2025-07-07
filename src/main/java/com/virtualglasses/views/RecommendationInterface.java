package com.virtualglasses.views;

import com.virtualglasses.controllers.RecommendationController;
import com.virtualglasses.controllers.SceneController;
import com.virtualglasses.models.Glasse;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RecommendationInterface {
    static {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (UnsatisfiedLinkError e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private Scene scene;
    private SceneController sceneController;
    private RecommendationController controller;
    private ImageView faceImg;
    private FlowPane recommendations;
    private Image uploadedImage;
    private CascadeClassifier faceCascade;
    private Label txtLabel;
    private ProgressIndicator indProg;
    private File tempimg;

    public RecommendationInterface(SceneController sceneController) {
        this.sceneController = sceneController;
        this.controller = new RecommendationController(sceneController);
        this.controller.setView(this);

        BorderPane root = new BorderPane();
       
        HBox header = createHeader();
        root.setTop(header);

        BorderPane main = new BorderPane();
        main.setPadding(new Insets(20));

        VBox photoSection = createPhotoSection();
        main.setLeft(photoSection);

        VBox recommendationSection = createRecommendationSection();
        main.setCenter(recommendationSection);

        root.setCenter(main);

        HBox footerBox = createFooter();
        root.setBottom(footerBox);

        initializeOpenCV();

        scene = new Scene(root, 900, 700);
       
        scene.getStylesheets().add(getClass().getResource("/css/recommendation.css").toExternalForm());
    }

    private HBox createHeader() {
        HBox header= new HBox();
        header.setPadding(new Insets(15, 25, 15, 25));
        header.getStyleClass().add("header");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Recommandation de Lunettes");
        title.getStyleClass().add("tlt");
        title.setPadding(new Insets(0, 0, 0, 15));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button helpBtn = new Button("?");
        helpBtn.getStyleClass().add("helpBtn");
       
        header.getChildren().addAll(title, spacer, helpBtn);
        return header;
    }

    private VBox createPhotoSection() {
        VBox photo = new VBox(20);
        photo.setPadding(new Insets(20));
        photo.setAlignment(Pos.TOP_CENTER);
        photo.setMaxWidth(350);
        photo.getStyleClass().add("section-card");

        Label titre = new Label("Votre Photo");
        titre.getStyleClass().add("titre");

        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("image-container");
        imageContainer.setMinHeight(250);
        imageContainer.setPrefHeight(250);

        faceImg = new ImageView();
        faceImg.setFitWidth(250);
        faceImg.setFitHeight(250);
        faceImg.setPreserveRatio(true);

        Text placeTxt = new Text("Téléchargez ou prenez une photo\npour obtenir des recommandations");
        placeTxt.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        placeTxt.getStyleClass().add("placeholder-text");

        indProg = new ProgressIndicator();
        indProg.setVisible(false);
        indProg.setMaxSize(50, 50);
        indProg.getStyleClass().add("progress-indicator");
        imageContainer.getChildren().addAll(placeTxt, faceImg, indProg);
 
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button TelechargerBtn = createButton("Télécharger", "action-button");
        Button picBtn = createButton("Prendre une Photo", "action-button");

        buttonBox.getChildren().addAll(TelechargerBtn, picBtn);

        txtLabel = new Label("");
        txtLabel.getStyleClass().add("txtlabel");
        txtLabel.setWrapText(true);
        txtLabel.setAlignment(Pos.CENTER);

        TelechargerBtn.setOnAction(e -> uploadPhoto());
        picBtn.setOnAction(e -> takePicture());

        photo.getChildren().addAll(titre, imageContainer, buttonBox, txtLabel);
        return photo;
    }

    private VBox createRecommendationSection() {
        VBox recommendationSection = new VBox(15);
        recommendationSection.setPadding(new Insets(20));
        recommendationSection.setAlignment(Pos.TOP_CENTER);
        recommendationSection.getStyleClass().add("section-card");

        Label RecTitle = new Label("Recommendations Personnalisées");
        RecTitle.getStyleClass().add("rectitle");

        Label waitingLabel = new Label("Téléchargez une photo pour voir les recommandations adaptées à votre visage");
        waitingLabel.setWrapText(true);
        waitingLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        waitingLabel.getStyleClass().add("reclabel");

        recommendations = new FlowPane();
        recommendations.setHgap(20);
        recommendations.setVgap(20);
        recommendations.setAlignment(Pos.CENTER);
        recommendations.setPadding(new Insets(10));
        recommendations.setVisible(false);
        recommendations.getStyleClass().add("recommendation");

        ScrollPane scrollPane = new ScrollPane(recommendations);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(400);
        scrollPane.getStyleClass().add("recommendations-scroll");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        StackPane recommendationContainer = new StackPane();
        recommendationContainer.getChildren().addAll(waitingLabel, scrollPane);

        recommendationSection.getChildren().addAll(RecTitle, recommendationContainer);
        return recommendationSection;
    }

    private HBox createFooter() {
        HBox footerBox = new HBox();
        footerBox.setPadding(new Insets(15, 25, 15, 25));
        footerBox.getStyleClass().add("footer");
        footerBox.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = createButton("Retour", "back-button");
        backBtn.setOnAction(e -> controller.goBack());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label copyrightLabel = new Label("© 2025 Virtual Glasses - Tous droits réservés");
        copyrightLabel.getStyleClass().add("copyright-label");

        footerBox.getChildren().addAll(backBtn, spacer, copyrightLabel);
        return footerBox;
    }

    private Button createButton(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        return button;
    }

    private void initializeOpenCV() {
        faceCascade = new CascadeClassifier();
        InputStream faceCascadeStream = getClass().getResourceAsStream("/haarcascades/haarcascade_frontalface_alt.xml");
        try {
            File tempFile = File.createTempFile("face_cascade", ".xml");
            tempFile.deleteOnExit();

            try (java.io.FileOutputStream out = new java.io.FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = faceCascadeStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            faceCascade = new CascadeClassifier(tempFile.getAbsolutePath());
            
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        } finally {
            try {
                faceCascadeStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void uploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Télécharger une Photo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
        	indProg.setVisible(true);
        	txtLabel.setText("Analyse de votre photo...");
            controller.uploadPhoto(selectedFile);
        }
    }

    private void takePicture() {
    	txtLabel.setText("Préparation de la caméra...");
        
        WebcamCaptureDialog webcamDialog = new WebcamCaptureDialog();
        Image capturedImage = webcamDialog.captureImage();

        if (capturedImage != null) {
        	indProg.setVisible(true);
        	txtLabel.setText("Analyse de votre photo...");
            uploadedImage = capturedImage;
            tempimg = saveImageToTemp(capturedImage);
            
            if (tempimg != null) {
                controller.takePicture(capturedImage, tempimg);
            } else {
            	indProg.setVisible(false);
            	txtLabel.setText("Erreur lors de la sauvegarde de l'image. Réessayez.");
            }
        } else {
        	txtLabel.setText("Problème avec la caméra");
        }
    }
    
    private File saveImageToTemp(Image image) {
        try {
          
            Mat frame = imageToMat(image);
            
            File tempFile = File.createTempFile("captured_image", ".jpg");
            tempFile.deleteOnExit();
            
            Imgcodecs.imwrite(tempFile.getAbsolutePath(), frame);
            
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Mat imageToMat(Image image) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        byte[] pixels = new byte[width * height * 4];

        javafx.scene.image.PixelReader pixelReader = image.getPixelReader();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                pixels[y * width * 4 + x * 4] = (byte) (color.getBlue() * 255);
                pixels[y * width * 4 + x * 4 + 1] = (byte) (color.getGreen() * 255);
                pixels[y * width * 4 + x * 4 + 2] = (byte) (color.getRed() * 255);
                pixels[y * width * 4 + x * 4 + 3] = (byte) (color.getOpacity() * 255);
            }
        }

        Mat mat = new Mat(height, width, CvType.CV_8UC4);
        mat.put(0, 0, pixels);

        Mat matBGR = new Mat();
        Imgproc.cvtColor(mat, matBGR, Imgproc.COLOR_RGBA2BGR);

        return matBGR;
    }

   
    public void setFaceShapeData(String faceShape, List<Glasse> recommendedGlasses) {
        if (faceShape != null && recommendedGlasses != null) {
            Platform.runLater(() -> {
            	txtLabel.setText("La forme de votre visage est : " + faceShape);
                generateRecommendations(faceShape, recommendedGlasses);
                
                recommendations.getParent().setVisible(true);
                recommendations.setVisible(true);
                indProg.setVisible(false);
           
            });
        }
    }
    private void generateRecommendations(String faceShape, List<Glasse> recommendedGlasses) {
        recommendations.getChildren().clear();
     
        if (recommendedGlasses.isEmpty()) {
            Label noResultsLabel = new Label("Aucune recommandation .");
            noResultsLabel.getStyleClass().add("no-results-label");
            return;
        }

        for (int i = 0; i < recommendedGlasses.size(); i++) {
            Glasse glasses = recommendedGlasses.get(i);
            boolean isBestMatch = (i == 0); 
            VBox recommendation = createRecommendation(glasses, isBestMatch);
            recommendations.getChildren().add(recommendation);
        }
    }
    public void setFaceImage(Image faceImage) {
        if (faceImage != null) {
            Platform.runLater(() -> {
                faceImg.setImage(faceImage);
            });
        }
    }

    public void handleError(String errorMessage) {
        Platform.runLater(() -> {
        	indProg.setVisible(false);
        	txtLabel.setText(errorMessage);
        });
    }

    private VBox createRecommendation(Glasse glasses, boolean bestMatch) {
        VBox recommendation = new VBox(8);
        recommendation.setAlignment(Pos.CENTER);
        recommendation.setPadding(new Insets(12));
        recommendation.setMaxWidth(200);
        recommendation.getStyleClass().add("recommendation-card");

        if (bestMatch) {
            recommendation.getStyleClass().add("bestt");
            Label bestMatchLabel = new Label("MEILLEUR CHOIX");
            bestMatchLabel.getStyleClass().add("best");
            recommendation.getChildren().add(bestMatchLabel);
        }

        Image glassesImage = null;
        try {
            if (glasses.getImageData() != null && glasses.getImageData().length > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(glasses.getImageData());
                glassesImage = new Image(bis);
                
            } 

            else if (glasses.getImagePath() != null && !glasses.getImagePath().isEmpty()) {
                try {
                   
                    File imageFile = new File(glasses.getImagePath());
                    if (imageFile.exists()) {
                        glassesImage = new Image(imageFile.toURI().toString());
                      
                    } else {
                       
                        InputStream is = getClass().getResourceAsStream(glasses.getImagePath());
                        if (is != null) {
                            glassesImage = new Image(is);
                          
                        }
                    }
                } catch (Exception e) {
                    System.err.println( e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        ImageView glassesImageView = new ImageView(glassesImage);
        glassesImageView.setFitWidth(150);
        glassesImageView.setFitHeight(80);
        glassesImageView.setPreserveRatio(true);
        glassesImageView.getStyleClass().add("glasses-image");

        Label nameLabel = new Label(glasses.getName());
        nameLabel.getStyleClass().add("glasses-name");

        Button tryButton = new Button("Essayer");
        tryButton.getStyleClass().add("try-button");

        tryButton.setOnAction(e -> {
            controller.tryOnGlasses(glasses);
        });

        recommendation.getChildren().addAll(glassesImageView, nameLabel, tryButton);
        return recommendation;
    }
    
    public Scene getScene() {
        return scene;
    }

    private class WebcamCaptureDialog {
        private Image capturedImage;

        public Image captureImage() {
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Capture de Photo");

            VideoCapture camera = new VideoCapture(0);

            if (!camera.isOpened()) {
                System.err.println("Erreur ");
                return null;
            }

            ImageView webcamView = new ImageView();
            webcamView.setPreserveRatio(true);
            webcamView.setFitWidth(640);
            webcamView.getStyleClass().add("webcam-view");
            
            Rectangle face = new Rectangle(200, 300);
            face.getStyleClass().add("face");
            
            StackPane webcamContainer = new StackPane();
            webcamContainer.getChildren().addAll(webcamView, face);
            webcamContainer.getStyleClass().add("webcam-container");

            Button captureButton = createButton("Capturer", "capture-button");
            Button cancelButton = createButton("Annuler", "cancel-button");

            HBox buttonBox = new HBox(15, captureButton, cancelButton);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(15));

            VBox dialogLayout = new VBox(10);
            dialogLayout.setAlignment(Pos.CENTER);
            dialogLayout.getChildren().addAll(webcamContainer, buttonBox);
            dialogLayout.getStyleClass().add("webcam-dialog");

            Scene dialogScene = new Scene(dialogLayout, 640, 520);
            dialogScene.getStylesheets().add(getClass().getResource("/css/recommendation.css").toExternalForm());
            dialogStage.setScene(dialogScene);

            Thread cameraThread = new Thread(() -> {
                Mat frame = new Mat();
                while (dialogStage.isShowing()) {
                    if (camera.read(frame)) {
                        Core.flip(frame, frame, 1);
                        Image image = mat2Image(frame);
                        Platform.runLater(() -> webcamView.setImage(image));
                    }
                    try {
                        Thread.sleep(30);  
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                camera.release();
            });
            cameraThread.setDaemon(true);
            cameraThread.start();

            captureButton.setOnAction(e -> {
                capturedImage = webcamView.getImage();
                dialogStage.close();
            });
            
            cancelButton.setOnAction(e -> {
                capturedImage = null;
                dialogStage.close();
            });

            dialogStage.showAndWait();
            return capturedImage;
        }
        
        private Image mat2Image(Mat frame) {
            MatOfByte buffer = new MatOfByte();
            Imgcodecs.imencode(".png", frame, buffer);
            byte[] byteArray = buffer.toArray();
            InputStream inputStream = new ByteArrayInputStream(byteArray);
            return new Image(inputStream);
        }
    }
}