package com.example.app;

import com.example.app.controllers.MainController;
import com.example.app.controllers.NavBarController;
import com.example.app.models.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private static User currentUser;
    private static MainController mainController;
    private static NavBarController navBarController;

    public static void setCurrentUser(User user) {
        currentUser = user;
        if (navBarController != null) {
            navBarController.setUsername(user.getUsername());
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Load the main view
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/com/example/app/views/main.fxml"));
        Parent mainRoot = mainLoader.load();
        mainController = mainLoader.getController();

        // Get the NavBarController from the namespace
        navBarController = (NavBarController) mainLoader.getNamespace().get("navBarController");
        
        // Connect the controllers
        if (navBarController != null) {
            navBarController.setMainController(mainController);
        }

        // Set up the scene
        Scene scene = new Scene(mainRoot);
        scene.getStylesheets().add(getClass().getResource("/com/example/app/styles/navbar.css").toExternalForm());
        
        stage.setTitle("NestTunes");
        stage.setScene(scene);
        stage.show();

        // Load the home view as the initial view
        mainController.loadView("home");
    }

    public static void main(String[] args) {
        launch(args);
    }
}