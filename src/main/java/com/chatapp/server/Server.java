package com.chatapp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 5000;

    public static void main(String[] args) {
        System.out.println("Starting Chat Server on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started successfully. Listening for incoming connections...");

            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected from: " + socket.getRemoteSocketAddress());

                ClientHandler handler = new ClientHandler(socket);
                Thread clientThread = new Thread(handler);
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Server socket exception: " + e.getMessage());
        }
    }
}