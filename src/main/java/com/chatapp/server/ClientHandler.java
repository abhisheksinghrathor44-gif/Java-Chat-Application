package com.chatapp.server;

import com.chatapp.dao.MessageDAO;
import com.chatapp.model.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler implements Runnable {

    // Thread-safe list for active client connections
    public static final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    private final Socket socket;
    private final MessageDAO messageDAO = new MessageDAO();
    private PrintWriter writer;
    private BufferedReader reader;
    private String username;
    private String privateChatUser = null;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // First line sent by client is treated as username registration
            username = reader.readLine();

            if (username == null || username.trim().isEmpty()) {
                writer.println("Invalid username. Connection terminated.");
                return;
            }

            username = username.trim();

            if (findClientByUsername(username) != null) {
                writer.println("Username already exists. Connection closed.");
                return;
            }

            clients.add(this);
            System.out.println("User registered: " + username);

            // Send past public group messages
            sendGroupHistory();

            // Notify other connected clients
            broadcastSystemMessage(username + " joined the chat.");

            String rawMessage;
            while ((rawMessage = reader.readLine()) != null) {
                String message = rawMessage.trim();
                if (message.isEmpty()) continue;

                if (message.equalsIgnoreCase("/users")) {
                    handleListUsers();
                } else if (message.startsWith("/private")) {
                    handleStartPrivateChat(message);
                } else if (message.equalsIgnoreCase("/exit")) {
                    handleExitPrivateChat();
                } else if (message.startsWith("/history")) {
                    handlePrivateHistory(message);
                } else if (message.startsWith("/msg")) {
                    handleDirectMessage(message);
                } else if (message.equalsIgnoreCase("/quit")) {
                    break;
                } else if (privateChatUser != null) {
                    sendPrivateMessage(privateChatUser, message);
                } else {
                    sendGroupMessage(message);
                }
            }

        } catch (IOException e) {
            System.out.println("Connection reset for user: " + (username != null ? username : "Unknown"));
        } finally {
            cleanup();
        }
    }

    private void sendGroupHistory() {
        writer.println("------ Previous Messages ------");
        for (Message msg : messageDAO.getGroupMessages()) {
            writer.println(msg.getSender() + " : " + msg.getMessage());
        }
        writer.println("-------------------------------");
    }

    private void handleListUsers() {
        writer.println("------ Online Users ------");
        for (ClientHandler client : clients) {
            writer.println(client.username);
        }
        writer.println("--------------------------");
    }

    private void handleStartPrivateChat(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 2) {
            writer.println("Usage: /private <username>");
            return;
        }
        String targetUsername = parts[1];
        ClientHandler target = findClientByUsername(targetUsername);

        if (target != null) {
            privateChatUser = target.username;
            writer.println("Private chat mode active with " + privateChatUser);
        } else {
            writer.println("User " + targetUsername + " is offline.");
        }
    }

    private void handleExitPrivateChat() {
        if (privateChatUser != null) {
            writer.println("Exited private chat mode with " + privateChatUser);
            privateChatUser = null;
        } else {
            writer.println("You are currently in public group chat mode.");
        }
    }

    private void handlePrivateHistory(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 2) {
            writer.println("Usage: /history <username>");
            return;
        }
        String targetUser = parts[1];
        writer.println("------ Private History with " + targetUser + " ------");
        for (Message msg : messageDAO.getPrivateMessages(username, targetUser)) {
            writer.println(msg.getSender() + " : " + msg.getMessage());
        }
        writer.println("--------------------------------");
    }

    private void handleDirectMessage(String command) {
        String[] parts = command.split("\\s+", 3);
        if (parts.length < 3) {
            writer.println("Usage: /msg <username> <message>");
            return;
        }
        sendPrivateMessage(parts[1], parts[2]);
    }

    private void sendPrivateMessage(String recipient, String text) {
        ClientHandler target = findClientByUsername(recipient);

        if (target != null) {
            Message privateMsg = new Message(username, recipient, text, true);
            messageDAO.saveMessage(privateMsg);

            target.writer.println("[Private] " + username + " : " + text);
            writer.println("[Private to " + recipient + "] " + text);
        } else {
            writer.println("User " + recipient + " is offline.");
        }
    }

    private void sendGroupMessage(String text) {
        Message groupMsg = new Message(username, text);
        messageDAO.saveMessage(groupMsg);

        String fullMessage = username + " : " + text;
        System.out.println(fullMessage);

        for (ClientHandler client : clients) {
            client.writer.println(fullMessage);
        }
    }

    private void broadcastSystemMessage(String systemMsg) {
        for (ClientHandler client : clients) {
            if (client != this) {
                client.writer.println("[Server]: " + systemMsg);
            }
        }
    }

    private ClientHandler findClientByUsername(String targetUsername) {
        for (ClientHandler client : clients) {
            if (client.username != null && client.username.equalsIgnoreCase(targetUsername)) {
                return client;
            }
        }
        return null;
    }

    private void cleanup() {
        clients.remove(this);
        if (username != null) {
            broadcastSystemMessage(username + " left the chat.");
            System.out.println(username + " disconnected.");
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}