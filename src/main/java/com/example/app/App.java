package com.example.app;

import com.example.app.controllers.LoginController;
import com.example.app.controllers.NavigationController;
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
        // Load main layout which contains the navigation system
        FXMLLoader mainLoader = new FXMLLoader(App.class.getResource("/com/example/app/views/Login.fxml"));
        Parent root = mainLoader.load();

        // Store the NavigationController reference in the root
//        NavigationController navController = mainLoader.getController();
//        root.getProperties().put("controller", navController);

        LoginController controller = mainLoader.getController();

        Scene scene = new Scene(root, 800, 600);

        // Add CSS
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