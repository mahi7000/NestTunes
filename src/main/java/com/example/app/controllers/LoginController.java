package com.example.app.controllers;


import com.example.app.utils.DatabaseConnection;
import com.example.app.utils.PasswordUtils;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LoginController {

    @FXML private VBox loginForm;
    @FXML private VBox signupForm;
    @FXML private Button loginToggleBtn;
    @FXML private Button signupToggleBtn;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField signupUsernameField;
    @FXML private TextField signupEmailField;
    @FXML private PasswordField signupPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button googleLoginBtn;


    @FXML
    public void initialize() {
        showLoginForm(); // Show login form by default
    }


    @FXML
    public void showLoginForm() {
        loginForm.setVisible(true);
        loginForm.setManaged(true);
        signupForm.setVisible(false);
        signupForm.setManaged(false);
        setActiveButton(loginToggleBtn);
        setInactiveButton(signupToggleBtn);
    }

    @FXML
    public void showSignupForm() {
        signupForm.setVisible(true);
        signupForm.setManaged(true);
        loginForm.setVisible(false);
        loginForm.setManaged(false);
        setActiveButton(signupToggleBtn);
        setInactiveButton(loginToggleBtn);
    }

    @FXML
    public void handleLogin() {
        System.out.println("✅ Login button clicked");
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Please fill in all fields");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT password_hash FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password_hash");

                if (PasswordUtils.checkPassword(password, hashedPassword)) {
                    System.out.println("Login successful!");
                    redirectToProfile(username);
                } else {
                    System.out.println("Incorrect password!");
                }
            } else {
                System.out.println("User not found!");
            }

        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSignup() {
        System.out.println("✅ Signup button clicked");
        String username = signupUsernameField.getText();
        String email = signupEmailField.getText();
        String password = signupPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            System.out.println("Please fill in all fields");
            return;
        }

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords don't match!");
            return;
        }

        String hashedPassword = PasswordUtils.hashPassword(password);



        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if username or email exists
            String checkSql = "SELECT id FROM users WHERE username = ? OR email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            checkStmt.setString(2, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("Username or email already taken");
                return;
            }

            // Insert new user
            String sql = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.executeUpdate();

            System.out.println("Signup successful!");
            redirectToLoginpage();

        } catch (SQLException e) {
            System.err.println("Signup error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void redirectToProfile() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/app/views/profile.fxml"));
            Stage stage = (Stage) loginForm.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load profile page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void redirectToLoginpage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/app/views/login.fxml"));
            Stage stage = (Stage) signupForm.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load LOGIN page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button button) {
        button.getStyleClass().remove("inactive-toggle");
        if (!button.getStyleClass().contains("active-toggle")) {
            button.getStyleClass().add("active-toggle");
        }
    }

    private void setInactiveButton(Button button) {
        button.getStyleClass().remove("active-toggle");
        if (!button.getStyleClass().contains("inactive-toggle")) {
            button.getStyleClass().add("inactive-toggle");
        }
    }

    private void redirectToProfile(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/app/views/profile.fxml"));
            Parent root = loader.load();

            // Access the ProfileController and pass the username
            ProfileController controller = loader.getController();
            controller.setUserData(username);  // We'll define this method in ProfileController

            // Open profile scene
            Stage stage = (Stage) loginForm.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load profile page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
