package com.example.app.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    @FXML private String loggedInUsername;

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

    public void setUserData(String username) {
        this.loggedInUsername = username;
    
        // Fetch user data from the database
        loadUserDataFromDatabase(username);
    
        // Update the profile UI
        updateUI();
    }

    // Load user data from the database and display on profile page
private void loadUserDataFromDatabase(String username) {
    try {
        java.sql.Connection connection = com.example.app.utils.DatabaseConnection.getConnection();
        String query = "SELECT * FROM users WHERE username = ?";
        java.sql.PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, username);
        java.sql.ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String name = rs.getString("username");
            String handle = rs.getString("email");
            String imagePath = rs.getString("profile_pic");

            userName.setText(name);
            userHandle.setText(handle);

            // Load profile picture from local user_images folder
            if (imagePath != null && !imagePath.isEmpty()) {
                File file = new File("user_images", imagePath);
                if (file.exists()) {
                    profilePicture.setImage(new Image(file.toURI().toString()));
                } else {
                    System.out.println("Image not found: " + file.getAbsolutePath());
                }
            }
        }

        rs.close();
        stmt.close();
        connection.close();
    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Error", "Failed to load user data");
    }
}

// Save profile picture filename to the database
private void saveProfilePicPathToDatabase(String path) {
    try {
        java.sql.Connection connection = com.example.app.utils.DatabaseConnection.getConnection();
        String query = "UPDATE users SET profile_pic = ? WHERE username = ?";
        java.sql.PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, path); // Just the filename, not full path
        stmt.setString(2, loggedInUsername); // You must define this globally or pass it
        stmt.executeUpdate();
        stmt.close();
        connection.close();
    } catch (Exception e) {
        e.printStackTrace();
        showAlert("Error", "Could not save profile picture path");
    }
}

// Allow user to change their profile picture
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
            // Create user_images folder if it doesn't exist
            File imagesDir = new File("user_images");
            if (!imagesDir.exists()) {
                imagesDir.mkdir();
            }

            // Generate a unique filename to avoid overwriting
            String uniqueFileName = "profile_" + System.currentTimeMillis() + "_" + selectedFile.getName();

            // Copy the selected file to user_images directory
            File destFile = new File(imagesDir, uniqueFileName);
            Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Load and display the selected image in ImageView
            Image image = new Image(destFile.toURI().toString(), false);
            profilePicture.setImage(image);

            // 🔽 Apply circular clipping and styling
            profilePicture.setFitWidth(100);
            profilePicture.setFitHeight(100);
            profilePicture.setPreserveRatio(false);
            profilePicture.setSmooth(true);
            Circle clip = new Circle(50, 50, 50); // x, y, radius = width/2
            //profilePicture.setClip(clip);
            clip.setStroke(Color.RED);  // For debugging
    clip.setStrokeWidth(2);
    clip.setFill(Color.TRANSPARENT);
    profilePicture.setClip(clip);

            // Save the image filename to the database
            saveProfilePicPathToDatabase(uniqueFileName);

            showAlert("Success", "Profile picture updated!");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Could not update profile picture.");
        }
    }
}



    private void updateUI() {
       // userName.setText("John Doe");
        //userHandle.setText("@johndoe");
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