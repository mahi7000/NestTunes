package com.example.app.controllers;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class OthersProfileController {
    @FXML private ImageView profilePicture;
    @FXML private Label userName;
    @FXML private Label userHandle;
    @FXML private TabPane profileTabs;
    @FXML private ListView<String> postsListView;
    @FXML private ListView<String> libraryListView;

    private List<String> followers = new ArrayList<>();
    private List<String> following = new ArrayList<>();
    private List<String> userPosts = new ArrayList<>();
    private List<String> userLibrary = new ArrayList<>();

    @FXML
    public void initialize() {
        // Initialize with sample data
        followers.add("user1");
        followers.add("user2");
        following.add("user3");
        following.add("user4");
        userPosts.add("My Song 1");
        userPosts.add("My Song 2");
        userLibrary.add("Liked Song 1");
        userLibrary.add("Liked Song 2");

        // Set up UI
        updateUI();
        setupButtons();
    }

    private void updateUI() {
        userName.setText("John Doe");
        userHandle.setText("@johndoe");
        postsListView.getItems().setAll(userPosts);
        libraryListView.getItems().setAll(userLibrary);
    }

    private void setupButtons() {
    }


    private void showUserList(String title, List<String> users) {
        ListView<String> listView = new ListView<>();
        listView.getItems().setAll(users);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setScene(new Scene(new StackPane(listView), 200, 300));
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}