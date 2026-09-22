package com.chatapp.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatController {

    @FXML private TextArea chatArea;
    @FXML private TextField messageField;
    @FXML private ListView<String> userList;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String currentUsername;

    public void initUser(String username) {
        this.currentUsername = username;
        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 5000);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Register username with server
            writer.println(currentUsername);

            // Listen for incoming server messages
            Thread listenerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = reader.readLine()) != null) {
                        final String msg = serverMessage;
                        Platform.runLater(() -> chatArea.appendText(msg + "\n"));
                    }
                } catch (IOException e) {
                    Platform.runLater(() -> chatArea.appendText("Disconnected from server.\n"));
                } finally {
                    closeResources();
                }
            });

            listenerThread.setDaemon(true);
            listenerThread.start();

        } catch (Exception e) {
            // Must use Platform.runLater if called outside primary UI thread setup
            Platform.runLater(() -> chatArea.appendText("Unable to connect to server.\n"));
        }
    }

    @FXML
    protected void handleSendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && writer != null) {
            writer.println(message);
            messageField.clear();
        }
    }

    /**
     * Call this method when closing the window / stopping the application stage
     */
    public void disconnect() {
        if (writer != null) {
            writer.println("/quit");
        }
        closeResources();
    }

    private void closeResources() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}