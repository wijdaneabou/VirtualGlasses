package com.virtualglasses.models;

public class FaceShapeModel {
    private String predictedLabel;
  

    public FaceShapeModel(String predictedLabel) {
        this.predictedLabel = predictedLabel;
       
    }

    public String getPredictedLabel() {
        return predictedLabel;
    }

    public void setPredictedLabel(String predictedLabel) {
        this.predictedLabel = predictedLabel;
    }

 
}
