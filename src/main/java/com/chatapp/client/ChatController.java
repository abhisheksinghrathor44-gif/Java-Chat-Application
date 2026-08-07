package com.chatapp.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
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

            // Send username to server first (as expected by ClientHandler)
            writer.println(currentUsername);

            // Background thread to listen for server broadcasts
            Thread listenerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = reader.readLine()) != null) {
                        final String msg = serverMessage;
                        Platform.runLater(() -> chatArea.appendText(msg + "\n"));
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> chatArea.appendText("Disconnected from server.\n"));
                }
            });
            listenerThread.setDaemon(true);
            listenerThread.start();

        } catch (Exception e) {
            chatArea.appendText("Unable to connect to server.\n");
        }
    }

    @FXML
    protected void handleSendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            writer.println(message);
            messageField.clear();
        }
    }
}