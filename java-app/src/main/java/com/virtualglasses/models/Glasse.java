package com.virtualglasses.models;

public class Glasse {
    private int id;
    private String name;
    private int typeId;
    private int faceShapeId;
    private String imagePath;
    private byte[] imageData; 

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getFaceShapeId() {
        return faceShapeId;
    }

    public void setFaceShapeId(int faceShapeId) {
        this.faceShapeId = faceShapeId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public byte[] getImageData() {
        return imageData;
    }
    
    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}