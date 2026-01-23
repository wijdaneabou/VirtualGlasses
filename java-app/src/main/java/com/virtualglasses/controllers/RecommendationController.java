package com.virtualglasses.controllers;

import com.virtualglasses.views.RecommendationInterface;
import com.virtualglasses.views.TryGlassesInterface;
import com.virtualglasses.models.FaceShapeModel;
import com.virtualglasses.models.Glasse;
import com.virtualglasses.models.GlassesDao;
import com.virtualglasses.services.FaceShapeClient;

import javafx.application.Platform;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RecommendationController {
    private SceneController sceneController;
    private RecommendationInterface view;
    private FaceShapeClient faceClient;
    private GlassesDao glassesDao;

    public RecommendationController(SceneController sceneController) {
        this.sceneController = sceneController;
        this.faceClient = FaceShapeClient.getInstance();
        this.glassesDao = new GlassesDao();
    }

    public void setView(RecommendationInterface view) {
        this.view = view;
    }

    public void goBack() {
        com.virtualglasses.views.HomeInterface homeUI = new com.virtualglasses.views.HomeInterface(sceneController);
        sceneController.setScene(homeUI.getScene());
    }

    public void uploadPhoto(File selectedFile) {
        if (selectedFile != null) {
            try {
                new Thread(() -> {
                    try {
                        Image uploadedImage = new Image(selectedFile.toURI().toURL().toString());
                        
                        FaceShapeModel prediction = faceClient.predict(selectedFile.getAbsolutePath());
                        
                        if (prediction != null) {
                            List<Glasse> recommendedGlasses = getRecommendedGlassesFromDB(prediction.getPredictedLabel());
                            
                            Platform.runLater(() -> {
                                view.setFaceImage(uploadedImage);
                                view.setFaceShapeData(prediction.getPredictedLabel(), recommendedGlasses);
                            });
                        } else {
                            Platform.runLater(() -> {
                                view.handleError("prob lors de l'analyse de l'image.");
                            });
                        }
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            view.handleError("prob lors du chargement de l'image.");
                        });
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception e) {
                view.handleError("prob  lors du traitement de l'image.");
                e.printStackTrace();
            }
        }
    }

    public void takePicture(Image capturedImage, File tempFile) {
        if (capturedImage != null && tempFile != null) {
            new Thread(() -> {
                try {
                    FaceShapeModel prediction = faceClient.predict(tempFile.getAbsolutePath());
                    
                    if (prediction != null) {
                        List<Glasse> recommendedGlasses = getRecommendedGlassesFromDB(prediction.getPredictedLabel());
                        
                        Platform.runLater(() -> {
                            view.setFaceImage(capturedImage);
                            view.setFaceShapeData(prediction.getPredictedLabel(), recommendedGlasses);
                        });
                    } else {
                        Platform.runLater(() -> {
                            view.handleError("Erreur lors de l'analyse de l'image");
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
    
    private List<Glasse> getRecommendedGlassesFromDB(String faceShape) {
        try {

            String dbFaceShape = convertFaceShapeName(faceShape);
            return glassesDao.findByFaceShape(dbFaceShape);
        } catch (Exception e) {
            e.printStackTrace();

            return new ArrayList<>();
        }
    }

    private String convertFaceShapeName(String faceShape) {
      
        switch(faceShape.toLowerCase()) {
            case "coeur":
                return "heart";
            case "rectangle":
                return "oblong";
            case "oval":
                return "oval";
            case "rond":
                return "round";
            case "carré":
                return "square";
            default:
                return faceShape.toLowerCase();
        }
    }
    
    public void tryOnGlasses(Glasse glasses) {
        try {
            Image glassesImage = null;
            if (glasses.getImageData() != null && glasses.getImageData().length > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(glasses.getImageData());
                glassesImage = new Image(bis);
            } 
          
            else if (glasses.getImagePath() != null && !glasses.getImagePath().isEmpty()) {
                File imageFile = new File(glasses.getImagePath());
                if (imageFile.exists()) {
                    glassesImage = new Image(imageFile.toURI().toString());
                } else {

                    InputStream is = getClass().getResourceAsStream(glasses.getImagePath());
                    if (is != null) {
                        glassesImage = new Image(is);
                    }
                }
            }
            
  
            TryGlassesInterface tryGlassesInterface = new TryGlassesInterface(sceneController);
            tryGlassesInterface.setGlassesImage(glassesImage);
            sceneController.setScene(tryGlassesInterface.getScene());
            
        } catch (Exception e) {
           
            e.printStackTrace();

        }
    }
}