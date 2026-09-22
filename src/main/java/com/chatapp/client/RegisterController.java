package com.chatapp.client;

import com.chatapp.dao.UserDAO;
import com.chatapp.model.User;
import javafx.concurrent.Task;
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

    private final UserDAO userDAO = new UserDAO();

    @FXML
    protected void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String email = emailField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        setInputsDisabled(true);
        errorLabel.setText("Processing registration...");

        // Run validation & registration on background thread
        Task<String> registerTask = new Task<>() {
            @Override
            protected String call() {
                if (userDAO.usernameExists(username)) {
                    return "Username already taken.";
                }
                if (userDAO.emailExists(email)) {
                    return "Email is already registered.";
                }

                User newUser = new User(username, password, email);
                boolean success = userDAO.registerUser(newUser);

                return success ? "SUCCESS" : "Registration failed. Try again.";
            }
        };

        registerTask.setOnSucceeded(event -> {
            String result = registerTask.getValue();
            if ("SUCCESS".equals(result)) {
                navigateToLogin();
            } else {
                errorLabel.setText(result);
                setInputsDisabled(false);
            }
        });

        registerTask.setOnFailed(event -> {
            errorLabel.setText("Database connection error.");
            setInputsDisabled(false);
        });

        new Thread(registerTask).start();
    }

    @FXML
    protected void handleBackToLogin() {
        navigateToLogin();
    }

    private void navigateToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);
            stage.setScene(scene);
            stage.setTitle("Chat Application - Login");
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Failed to load login screen.");
        }
    }

    private void setInputsDisabled(boolean disabled) {
        usernameField.setDisable(disabled);
        passwordField.setDisable(disabled);
        emailField.setDisable(disabled);
    }
}