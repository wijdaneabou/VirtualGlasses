package com.virtualglasses.services;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;

import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.virtualglasses.models.FaceShapeModel;

public class FaceShapeClient {

    private static FaceShapeClient instance;

    private FaceShapeClient() {}

    public static FaceShapeClient getInstance() {
        if (instance == null) {
            instance = new FaceShapeClient();
        }
        return instance;
    }

    private static final Map<Integer, String> classToShapeMap = new HashMap<>();
    static {
        classToShapeMap.put(0, "Coeur");
        classToShapeMap.put(1, "Rectangle");
        classToShapeMap.put(2, "Oval");
        classToShapeMap.put(3, "Rond");
        classToShapeMap.put(4, "Carré");
    }
    public FaceShapeModel predict(String imagePath) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("http://127.0.0.1:5000/predict");
            File imageFile = new File(imagePath);
          
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", imageFile, ContentType.DEFAULT_BINARY, imageFile.getName());
            
            HttpEntity entity = builder.build();
            post.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                

                JSONObject jsonResponse = new JSONObject(responseBody);
                int predictedClass = jsonResponse.getInt("predicted_class");



                String shapeName = classToShapeMap.getOrDefault(predictedClass, "Inconnu");

                return new FaceShapeModel(shapeName); 
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
 