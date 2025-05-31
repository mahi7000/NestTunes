package com.example.app.controllers;

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

    @FXML
    public void initialize() {
        // Initialize with login form visible
        showLoginForm();
    }

    @FXML
    public void showLoginForm() {
        // Show login form
        loginForm.setVisible(true);
        loginForm.setManaged(true);

        // Hide signup form
        signupForm.setVisible(false);
        signupForm.setManaged(false);

        // Update button styles
        setActiveButton(loginToggleBtn);
        setInactiveButton(signupToggleBtn);
    }

    @FXML
    public void showSignupForm() {
        // Show signup form
        signupForm.setVisible(true);
        signupForm.setManaged(true);

        // Hide login form
        loginForm.setVisible(false);
        loginForm.setManaged(false);

        // Update button styles
        setActiveButton(signupToggleBtn);
        setInactiveButton(loginToggleBtn);
    }

    @FXML
    public void handleLogin() {
        // Simple validation
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            System.out.println("Please fill in all fields");
            return;
        }

        System.out.println("Logging in with: " + usernameField.getText());
        redirectToHome();
    }

    @FXML
    public void handleSignup() {
        // Simple validation
        if (signupUsernameField.getText().isEmpty() ||
                signupEmailField.getText().isEmpty() ||
                signupPasswordField.getText().isEmpty()) {
            System.out.println("Please fill in all fields");
            return;
        }

        if (!signupPasswordField.getText().equals(confirmPasswordField.getText())) {
            System.out.println("Passwords don't match!");
            return;
        }

        System.out.println("Creating account for: " + signupEmailField.getText());
        redirectToHome();
    }

    private void redirectToHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/music/views/NowPlaying.fxml"));
            Stage stage = (Stage) loginForm.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load home page: " + e.getMessage());
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
}