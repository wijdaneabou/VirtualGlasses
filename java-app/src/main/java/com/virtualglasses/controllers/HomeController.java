package com.virtualglasses.controllers;




import com.virtualglasses.views.RecommendationInterface;
import com.virtualglasses.views.TryGlassesInterface;

public class HomeController {
    private SceneController sceneController;

    public HomeController(SceneController sceneController) {
        this.sceneController = sceneController;
    }

    public void goToTryGlasses() {
        sceneController.setScene(new TryGlassesInterface(sceneController).getScene());
    }

    public void goToRecommendation() {
        sceneController.setScene(new RecommendationInterface(sceneController).getScene());
    }
}
