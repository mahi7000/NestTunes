package com.example.app.controllers;

import com.example.app.database.DatabaseManager;
import com.example.app.models.User;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
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
    private DatabaseManager dbManager = new DatabaseManager();
    private User currentUser;
    @FXML private Button followButton;
    private int currentUserId;

    private List<String> userPosts = new ArrayList<>();
    private List<String> userLibrary = new ArrayList<>();

    @FXML
    public void initialize(User profileUser, int currentUserId) {
        this.currentUser = profileUser;
        this.currentUserId = currentUserId;
        updateUI();
        setupButtons();
    }

    public void setUser(User user) {
        this.currentUser = user;
        updateUI();
    }

    private void updateUI() {
        if (currentUser != null) {
            userName.setText(currentUser.getUsername());
            userHandle.setText("@" + currentUser.getUsername());

            // Load profile picture
            if (currentUser.getProfilePic() != null && !currentUser.getProfilePic().isEmpty()) {
                try {
                    profilePicture.setImage(new Image("file:" + currentUser.getProfilePic()));
                } catch (Exception e) {
                    // Load default image if there's an error
                    profilePicture.setImage(new Image(getClass().getResourceAsStream("/images/default_profile.png")));
                }
            }

            // Load user posts
            List<String> userPosts = dbManager.getUserPosts(currentUser.getId());
            postsListView.getItems().setAll(userPosts);

            libraryListView.getItems().clear();
            setupFollowButton();
        }
    }


    private void setupFollowButton() {
        boolean isFollowing = dbManager.isFollowing(currentUserId, currentUser.getId());

        if (currentUserId == currentUser.getId()) {
            followButton.setVisible(false); // Can't follow yourself
        } else {
            followButton.setText(isFollowing ? "Following" : "Follow");
            followButton.setStyle(isFollowing ?
                    "-fx-background-color: #d81b60; " :
                    "-fx-background-color: transparent; -fx-border-color: #d81b60;");
        }
    }

    @FXML
    private void handleFollowAction() {
        boolean success;
        boolean currentlyFollowing = dbManager.isFollowing(currentUserId, currentUser.getId());

        if (currentlyFollowing) {
            success = dbManager.unfollowUser(currentUserId, currentUser.getId());
        } else {
            success = dbManager.followUser(currentUserId, currentUser.getId());
        }

        if (success) {
            setupFollowButton();
        } else {
            showAlert("Error", "Could not update follow status");
        }
    }
    private void setupButtons() {
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}