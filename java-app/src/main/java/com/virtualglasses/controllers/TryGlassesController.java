package com.virtualglasses.controllers;

import com.virtualglasses.models.FaceShapeModel;
import com.virtualglasses.models.Glasse;
import com.virtualglasses.models.GlassesDao;
import com.virtualglasses.services.FaceShapeClient;
import com.virtualglasses.views.HomeInterface;
import com.virtualglasses.views.RecommendationInterface;
import com.virtualglasses.views.TryGlassesInterface;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;

public class TryGlassesController {
    static {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        } catch (UnsatisfiedLinkError e) {
            System.err.println( e.getMessage());
            System.exit(1);
        }
    }

    private SceneController sceneController;
    private VideoCapture camera;
    private boolean cameraActive;
    private CascadeClassifier faceCascade;
    private CascadeClassifier eyesCascade;
    private Image selectedGlassesImage;
    private TryGlassesInterface tryGlassesInterface;
    private GlassesDao glassesDao;
    private boolean isstaticImg = false;
    private Mat staticImage = null;

    public TryGlassesController(SceneController sceneController) {
        this.sceneController = sceneController;
        this.glassesDao = new GlassesDao();
        loadFaceDetection();
    }

    public void setTryGlassesInterface(TryGlassesInterface tryGlassesInterface) {
        this.tryGlassesInterface = tryGlassesInterface;
    }

    public void goBack() {
        stopCamera();
        HomeInterface homeUI = new HomeInterface(sceneController);
        sceneController.setScene(homeUI.getScene());
    }

    public void startCamera(ImageUpdateCallback callback) {
        stopCamera();
        
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        camera = new VideoCapture(0);
       
        cameraActive = true;

        Thread cameraThread = new Thread(() -> {
            Mat frame = new Mat();
            while (cameraActive) {
                if (camera.read(frame)) {
                    Mat processedFrame = processFrame(frame);
                    Image image = mat2Image(processedFrame);
                    Platform.runLater(() -> callback.updateImage(image));
                }
            }
        });
        cameraThread.setDaemon(true);
        cameraThread.start();
    }

    public void stopCamera() {
        cameraActive = false;
        if (camera != null && camera.isOpened()) {
            camera.release();
        }
    }

    public void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisissez une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            try {
                final Image image = new Image(selectedFile.toURI().toURL().toString());
                boolean wasCameraActive = cameraActive;
                if (wasCameraActive) {
                    stopCamera();
                    try { Thread.sleep(100); } catch (Exception e) {}
                }
                Platform.runLater(() -> {
                    tryGlassesInterface.updateImageView(image);
                });
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    public void setSelectedGlassesImage(Image image) {
        this.selectedGlassesImage = image;
        if (isstaticImg && staticImage  != null) {
            Mat processedFrame = processFrame(staticImage .clone());
            Image processedImage = mat2Image(processedFrame);
            tryGlassesInterface.updateImageView(processedImage);
        }
    }

    private Mat processFrame(Mat frame) {
        Mat processedFrame = frame.clone();
        if (selectedGlassesImage != null && faceCascade != null && !faceCascade.empty()) {
            detectAndOverlayGlasses(processedFrame);
        }
        return processedFrame;
    }

    private void detectAndOverlayGlasses(Mat frame) {
        MatOfRect faceDetections = new MatOfRect();
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
      
        faceCascade.detectMultiScale(grayFrame, faceDetections, 1.1, 8, 0, new Size(30, 30), new Size());

        Rect[] faceArray = faceDetections.toArray();
        if (faceArray.length > 0) {
            Rect faceRect = faceArray[0];

            Rect eyesRegion = new Rect(
                faceRect.x, 
                faceRect.y + (int)(faceRect.height * 0.2), 
                faceRect.width,
                (int)(faceRect.height * 0.25) 
            );
        
            Mat eyesROI = grayFrame.submat(eyesRegion);
            Mat equalizedEyesROI = new Mat();
            Imgproc.equalizeHist(eyesROI, equalizedEyesROI);
            
            MatOfRect eyesDetections = new MatOfRect();
            if (eyesCascade != null && !eyesCascade.empty()) {
                eyesCascade.detectMultiScale(equalizedEyesROI, eyesDetections, 1.1, 5, 0, 
                                             new Size(eyesRegion.width * 0.1, eyesRegion.height * 0.2), 
                                             new Size(eyesRegion.width * 0.45, eyesRegion.height * 0.9));
            }
            
            int glassesWidth = (int)(faceRect.width * 0.9);
            BufferedImage glassesBufferedImage = SwingFXUtils.fromFXImage(selectedGlassesImage, null);
            Mat glassesMat = bufferedImageToMat(glassesBufferedImage);
            
            double aspectRatio = (double)glassesMat.cols() / glassesMat.rows();
            int glassesHeight = (int)(glassesWidth / aspectRatio);
            
            int glassesX, glassesY;
          
            int prevGlassesX = -1;
            int prevGlassesY = -1;
            
            if (!eyesDetections.empty() && eyesDetections.toArray().length >= 2) {
                Rect[] eyesArray = eyesDetections.toArray();
                
                Arrays.sort(eyesArray, (e1, e2) -> Integer.compare(e1.x, e2.x));
                
                int leftEyeX = eyesRegion.x + eyesArray[0].x + eyesArray[0].width/2;
                int rightEyeX = eyesRegion.x + eyesArray[1].x + eyesArray[1].width/2;
                int eyeY = eyesRegion.y + (eyesArray[0].y + eyesArray[1].y)/2 + eyesArray[0].height/2;
                
                glassesX = (leftEyeX + rightEyeX)/2 - glassesWidth/2;
                
                glassesY = eyeY - (int)(glassesHeight * 0.6);
               
                if (prevGlassesX >= 0 && prevGlassesY >= 0) {
                   
                    glassesX = (int)(0.8 * glassesX + 0.2 * prevGlassesX);
                    glassesY = (int)(0.8 * glassesY + 0.2 * prevGlassesY);
                }
                
            
                prevGlassesX = glassesX;
                prevGlassesY = glassesY;
            } else {
       
                glassesX = faceRect.x + (faceRect.width - glassesWidth)/2; 
                glassesY = faceRect.y + (int)(faceRect.height * 0.22);
                
                prevGlassesX = -1;
                prevGlassesY = -1;
            }
           
            if (glassesX < 0) glassesX = 0;
            if (glassesY < 0) glassesY = 0;
            if (glassesX + glassesWidth > frame.cols()) glassesWidth = frame.cols() - glassesX;
            if (glassesY + glassesHeight > frame.rows()) glassesHeight = frame.rows() - glassesY;
            
            try {
                Mat roi = frame.submat(new Rect(glassesX, glassesY, glassesWidth, glassesHeight));
                Mat resizedGlasses = new Mat();
                Imgproc.resize(glassesMat, resizedGlasses, new Size(glassesWidth, glassesHeight));

                if (resizedGlasses.channels() != roi.channels()) {
                    Imgproc.cvtColor(resizedGlasses, resizedGlasses, Imgproc.COLOR_BGR2RGB);
                }
          
                Mat mask = new Mat();
           
                Mat hsvGlasses = new Mat();
                Imgproc.cvtColor(resizedGlasses, hsvGlasses, Imgproc.COLOR_BGR2HSV);
                
                Mat mask1 = new Mat();
                Core.inRange(hsvGlasses, new Scalar(0, 0, 210), new Scalar(180, 30, 255), mask1);
            
                Mat mask2 = new Mat();
                Core.inRange(hsvGlasses, new Scalar(0, 0, 180), new Scalar(180, 15, 210), mask2);
                
        
                Core.bitwise_or(mask1, mask2, mask);
                

                Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
                Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel);
                Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel);

                Imgproc.GaussianBlur(mask, mask, new Size(3, 3), 0);

                if (glassesBufferedImage.getColorModel().hasAlpha()) {
                    BufferedImage alphaMaskImage = new BufferedImage(glassesBufferedImage.getWidth(), 
                                                                    glassesBufferedImage.getHeight(), 
                                                                    BufferedImage.TYPE_BYTE_GRAY);
                    
                    for (int y = 0; y < glassesBufferedImage.getHeight(); y++) {
                        for (int x = 0; x < glassesBufferedImage.getWidth(); x++) {
                            int alpha = (glassesBufferedImage.getRGB(x, y) >> 24) & 0xff;
                            alphaMaskImage.setRGB(x, y, (alpha << 16) | (alpha << 8) | alpha);
                        }
                    }
                    
                    Mat alphaMask = bufferedImageToMat(alphaMaskImage);
                    Mat resizedAlphaMask = new Mat();
                    Imgproc.resize(alphaMask, resizedAlphaMask, new Size(glassesWidth, glassesHeight));

                    Mat invertedAlphaMask = new Mat();
                    Core.bitwise_not(resizedAlphaMask, invertedAlphaMask);

                    Core.bitwise_or(mask, invertedAlphaMask, mask);
                }

                Mat invMask = new Mat();
                Core.bitwise_not(mask, invMask);

                Mat foreground = new Mat();
                Mat background = new Mat();

                resizedGlasses.copyTo(foreground, invMask);
                roi.copyTo(background, mask);

                Core.add(foreground, background, roi);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private void loadFaceDetection() {
        faceCascade = new CascadeClassifier();
        InputStream faceCascadeStream = getClass().getResourceAsStream("/haarcascades/haarcascade_frontalface_alt.xml");
        try {
            File tempFile = File.createTempFile("face_cascade", ".xml");
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = faceCascadeStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            faceCascade = new CascadeClassifier(tempFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                faceCascadeStream.close();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    public Image mat2Image(Mat frame) {
        Mat rgbFrame = new Mat();
        Imgproc.cvtColor(frame, rgbFrame, Imgproc.COLOR_BGR2RGB);
        
        BufferedImage bufferedImage = new BufferedImage(rgbFrame.cols(), rgbFrame.rows(), BufferedImage.TYPE_INT_RGB);
        
        byte[] data = new byte[rgbFrame.channels() * rgbFrame.cols() * rgbFrame.rows()];
        rgbFrame.get(0, 0, data);
        
        int[] intData = new int[rgbFrame.cols() * rgbFrame.rows()];
        for (int i = 0; i < data.length; i += 3) {
            intData[i/3] = (0xFF << 24) | 
                           ((data[i] & 0xFF) << 16) | 
                           ((data[i+1] & 0xFF) << 8) | 
                           (data[i+2] & 0xFF);
        }
        
        bufferedImage.setRGB(0, 0, rgbFrame.cols(), rgbFrame.rows(), intData, 0, rgbFrame.cols());
        
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    private Mat bufferedImageToMat(BufferedImage bi) {
        BufferedImage convertedImg = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        convertedImg.getGraphics().drawImage(bi, 0, 0, null);
        Mat mat = new Mat(convertedImg.getHeight(), convertedImg.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) convertedImg.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    public void generateAIRecommendation() {
        Mat currentFrame = captureCurrentFrame();
        
        if (!currentFrame.empty()) {
            String faceShape = detectFaceShape(currentFrame);
            
            List<Glasse> recommendedGlasses = new ArrayList<>();
            try {
                recommendedGlasses = glassesDao.findByFaceShape(faceShape);
           
            } catch (Exception e) {
             
                e.printStackTrace();
            }
            
            RecommendationInterface recommendationInterface = new RecommendationInterface(sceneController);
       
            recommendationInterface.setFaceShapeData(faceShape, recommendedGlasses);
            
            Image faceImage = mat2Image(currentFrame);
            recommendationInterface.setFaceImage(faceImage);
            
            Scene recommendationScene = recommendationInterface.getScene();
            sceneController.setScene(recommendationScene);
        } 
    }
    private Mat captureCurrentFrame() {
        Mat frame = new Mat();
        if (camera != null && camera.isOpened()) {
            camera.read(frame);
        }
        return frame;
    }

    private String detectFaceShape(Mat frame) {
        try {
            String tempFilePath = "temp_image.jpg";
            BufferedImage buffImage = mat2BufferedImage(frame);
            File outputFile = new File(tempFilePath);
            ImageIO.write(buffImage, "jpg", outputFile);

            FaceShapeClient flaskClient = FaceShapeClient.getInstance();
            FaceShapeModel prediction = flaskClient.predict(tempFilePath);

            outputFile.delete();
            
            if (prediction != null) {
                System.out.println("La forme de votre visage est " + prediction.getPredictedLabel());
                return mapToEnglishShapeName(prediction.getPredictedLabel());
            } else {
                return "inconnu";
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return "inconnu";
        }
    }

    private BufferedImage mat2BufferedImage(Mat mat) {
        Mat convertedMat = mat.clone();
        if (mat.channels() > 1) {
            Imgproc.cvtColor(mat, convertedMat, Imgproc.COLOR_BGR2RGB);
        }
        int width = convertedMat.cols();
        int height = convertedMat.rows();
        int channels = convertedMat.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        convertedMat.get(0, 0, sourcePixels);
        
        BufferedImage image;
        if (channels > 1) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        } else {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
        
        return image;
    }

    private String mapToEnglishShapeName(String frenchName) {
        Map<String, String> translationMap = new HashMap<>();
        translationMap.put("Coeur", "heart");
        translationMap.put("Rectangle", "oblong");
        translationMap.put("Oval", "oval");
        translationMap.put("Rond", "round");
        translationMap.put("Carré", "square");
        translationMap.put("Inconnu", "unknown");
        
        return translationMap.getOrDefault(frenchName, "unknown");
    }

    public interface ImageUpdateCallback {
        void updateImage(Image image);
    }
}