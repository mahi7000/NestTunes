package com.example.app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ProfileController {
    @FXML private ImageView profilePicture;
    @FXML private Label userName;
    @FXML private Label userHandle;
    @FXML private Button followersButton;
    @FXML private Button followingButton;
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
        followersButton.setText(followers.size() + " Followers");
        followingButton.setText(following.size() + " Following");
        postsListView.getItems().setAll(userPosts);
        libraryListView.getItems().setAll(userLibrary);
    }

    private void setupButtons() {
        followersButton.setOnAction(e -> showUserList("Followers", followers));
        followingButton.setOnAction(e -> showUserList("Following", following));
    }

    @FXML
    private void changeProfilePicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(profilePicture.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Create images directory if it doesn't exist
                File imagesDir = new File("/com/example/app/images");
                if (!imagesDir.exists()) {
                    imagesDir.mkdir();
                }

                // Copy the image to the images folder
                File destFile = new File("images/profile_" + selectedFile.getName());
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Update profile picture
                profilePicture.setImage(new Image(destFile.toURI().toString()));
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Could not update profile picture");
            }
        }
    }

    @FXML
    private void createNewPost() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/app/views/NewPost.fxml"));
            Parent root = loader.load();
            NewPostController controller = loader.getController();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Create New Post");
            stage.setScene(new Scene(root));

            controller.setPostHandler(post -> {
                userPosts.add(0, post); // Add new post at the beginning
                postsListView.getItems().add(0, post);
                stage.close();
            });

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not open post creator");
        }
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