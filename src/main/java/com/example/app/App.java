package com.example.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import javafx.scene.media.*;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        String mediaPath = "src/main/resources/com/example/app/sounds/LaDS.mp3";
//
//        Media media = new Media(new File(mediaPath).toURI().toString());
//        MediaPlayer mediaPlayer = new MediaPlayer(media);
//        mediaPlayer.play();
//
//        MediaView mediaView = new MediaView(mediaPlayer);
//
//        StackPane root = new StackPane(mediaView);
//        Scene scene = new Scene(root, 640, 480);


        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/example/app/views/Home.fxml"));
        Parent fxmlContent = fxmlLoader.load();

        StackPane root = new StackPane();
        root.getChildren().addAll(fxmlContent);

        Scene scene = new Scene(root, 800, 600);
        String cssPath = App.class.getResource("/com/example/app/css/application.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.setTitle("Music Player");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}