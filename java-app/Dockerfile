FROM eclipse-temurin:21-jdk-jammy

# Installation des dépendances JavaFX
RUN apt-get update && apt-get install -y openjfx && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/VirtualGlassesApp1-1.0.0.jar app.jar

EXPOSE 8080

# Ajout des modules JavaFX requis
CMD ["java", "--module-path", "C:\Program Files\Java\javafx-sdk-21.0.6\lib", "--add-modules", "javafx.controls,javafx.fxml,javafx.graphics", "-jar", "app.jar"]