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
    private Stage primaryStage;
    private NavigationController navController;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        showLoginView(); // Start with login view
    }

    public void showLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/app/views/login.fxml"));
        Parent root = loader.load();

        // Pass reference to app to login controller
        LoginController loginController = loader.getController();
        loginController.setMainApp(this);

        Scene scene = new Scene(root, 800, 600);
        String cssPath = getClass().getResource("/com/example/app/css/styles.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        primaryStage.setTitle("NestTunes - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showMainApplication(String username) throws IOException {
        // Load main layout
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/example/app/views/Main.fxml"));
        Parent root = mainLoader.load();

        // Get navigation controller
        navController = mainLoader.getController();
        root.getProperties().put("controller", navController);

        navController.setLoggedInUsername(username);

        // Set up main scene
        Scene scene = new Scene(root, 800, 600);
        String cssPath = getClass().getResource("/com/example/app/css/styles.css").toExternalForm();
        scene.getStylesheets().add(cssPath);

        primaryStage.setTitle("NestTunes");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Navigate to profile with username
        navController.navigateToProfileWithUser(username);
    }

    public static void main(String[] args) {
        launch();
    }
}