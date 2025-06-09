package com.example.app.controllers;

import com.example.app.database.DatabaseManager;
import com.example.app.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
import java.util.Stack;


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

    private DatabaseManager databaseManager = new DatabaseManager();
    private CurrentUserSession currentUserSession = CurrentUserSession.getInstance();
    private User currentProfileUser;

    @FXML
    public void initialize() {
        // Initialize with sample data
        userLibrary.add("Liked Song 1");
        userLibrary.add("Liked Song 2");

        // Set up UI
        updateUI();
        setupButtons();
    }

    public void setUserData(String username) {
        this.loggedInUsername = username;
        this.currentProfileUser = databaseManager.getUserByUsername(username);

        // Fetch user data from the database
        loadUserDataFromDatabase(username);

        loadFollowerData();
    
        // Update the profile UI
        updateUI();
    }

    private void loadFollowerData() {
        if (currentProfileUser != null) {
            // Get follower and following lists
            followers = databaseManager.getFollowers(currentProfileUser.getId());
            following = databaseManager.getFollowing(currentProfileUser.getId());
        }
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

                Image image = new Image(destFile.toURI().toString(), false);
                if (image.isError()) {
                    System.out.println("Image failed to load: " + image.getException());
                } else {
                    profilePicture.setImage(image);
                }

                // 🔽 Apply circular clipping and styling
                // Apply circular clipping
                double radius = 75;
                profilePicture.setFitWidth(radius * 2);
                profilePicture.setFitHeight(radius * 2);
                profilePicture.setPreserveRatio(false);
                profilePicture.setSmooth(true);

                Circle clip = new Circle(radius, radius, radius);
                profilePicture.setClip(clip);


                // Save the image filename to the database
                saveProfilePicPathToDatabase(uniqueFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUI() {
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
        }
    }

    private void showUserList(String title, List<String> users) {
        ListView<String> listView = new ListView<>();

        // Custom cell factory to add follow/unfollow buttons
        listView.setCellFactory(lv -> new ListCell<String>() {
            private final Button actionButton = new Button();
            private final HBox hbox = new HBox(10, new Label(), actionButton);

            {
                hbox.setAlignment(Pos.CENTER_LEFT);
                actionButton.setOnAction(e -> {
                    String username = getItem();
                });
            }

            @Override
            protected void updateItem(String username, boolean empty) {
                super.updateItem(username, empty);

                if (empty || username == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // Don't show button for current user
                    if (username.equals(currentUserSession.getCurrentUser().getUsername())) {
                        setGraphic(null);
                        setText(username);
                        return;
                    }

                    User targetUser = databaseManager.getUserByUsername(username);
                    boolean isFollowing = databaseManager.isFollowing(
                            currentUserSession.getCurrentUser().getId(),
                            targetUser.getId()
                    );

                    actionButton.setText(isFollowing ? "Unfollow" : "Follow");
                    actionButton.setStyle(isFollowing ?
                            "-fx-background-color: #ff4444; -fx-text-fill: white;" :
                            "-fx-background-color: #4CAF50; -fx-text-fill: white;");

                    ((Label)hbox.getChildren().get(0)).setText(username);
                    setGraphic(hbox);
                    setText(null);
                }
            }
        });

        listView.getItems().setAll(users);

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.getChildren().addAll(
                new Label(title + " (" + users.size() + ")"),
                listView
        );

        Scene scene = new Scene(root, 300, 400);
        scene.getStylesheets().add(getClass().getResource("/com/example/app/css/profile.css").toExternalForm());

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }
}