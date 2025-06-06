package com.example.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
<<<<<<< HEAD
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/example/app/views/login.fxml"));
=======
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/example/app/views/Main.fxml"));
>>>>>>> b7a333f (Add navigation)
        Parent fxmlContent = fxmlLoader.load();

        StackPane root = new StackPane();
        root.getChildren().addAll(fxmlContent);

        Scene scene = new Scene(root, 800, 600);
        String cssPath = App.class.getResource("/com/example/app/css/styles.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        stage.setTitle("NestTunes");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}