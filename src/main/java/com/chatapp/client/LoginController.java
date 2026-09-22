package com.chatapp.client;

import com.chatapp.dao.UserDAO;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    protected void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        // Disable input controls while authenticating
        errorLabel.setText("Logging in...");
        setInputsDisabled(true);

        // Run DB operation on background thread to prevent UI freezing
        Task<Boolean> loginTask = new Task<>() {
            @Override
            protected Boolean call() {
                return userDAO.loginUser(username, password);
            }
        };

        loginTask.setOnSucceeded(event -> {
            boolean success = loginTask.getValue();
            if (success) {
                openChatView(username);
            } else {
                errorLabel.setText("Invalid username or password.");
                setInputsDisabled(false);
            }
        });

        loginTask.setOnFailed(event -> {
            errorLabel.setText("Database error. Please try again.");
            setInputsDisabled(false);
        });

        new Thread(loginTask).start();
    }

    private void openChatView(String username) {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/chat-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);

            ChatController controller = loader.getController();
            controller.initUser(username);

            // Handle clean socket shutdown when user closes window
            stage.setOnCloseRequest(e -> controller.disconnect());

            stage.setScene(scene);
            stage.setTitle("Chat Room - " + username);
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Failed to load chat view.");
            setInputsDisabled(false);
        }
    }

    @FXML
    protected void handleSwitchToRegister() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/register.fxml"));
            Scene scene = new Scene(loader.load(), 400, 350);
            stage.setScene(scene);
            stage.setTitle("Chat Application - Register");
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Failed to load register view.");
        }
    }

    private void setInputsDisabled(boolean disabled) {
        usernameField.setDisable(disabled);
        passwordField.setDisable(disabled);
    }
}