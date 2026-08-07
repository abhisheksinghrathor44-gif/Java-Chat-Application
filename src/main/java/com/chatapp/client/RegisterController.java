package com.chatapp.client;

import com.chatapp.dao.UserDAO;
import com.chatapp.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    protected void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String email = emailField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        // Check if username or email already exists
        if (userDAO.usernameExists(username)) {
            errorLabel.setText("Username already taken.");
            return;
        }

        if (userDAO.emailExists(email)) {
            errorLabel.setText("Email is already registered.");
            return;
        }

        // Create User model and register
        User newUser = new User(username, password, email);
        boolean success = userDAO.registerUser(newUser);

        if (success) {
            try {
                // Redirect back to login screen
                Stage stage = (Stage) usernameField.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                Scene scene = new Scene(loader.load(), 400, 300);
                stage.setScene(scene);
                stage.setTitle("Chat Application - Login");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            errorLabel.setText("Registration failed. Try again.");
        }
    }

    @FXML
    protected void handleBackToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);
            stage.setScene(scene);
            stage.setTitle("Chat Application - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}