package com.chatapp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (
                Socket socket = new Socket(HOST, PORT);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connected to server.");

            // Get username input
            System.out.print("Enter Username: ");
            String username = scanner.nextLine().trim();
            writer.println(username);

            // Message receiver thread
            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            receiveThread.setDaemon(true);
            receiveThread.start();

            // Message sender loop
            while (true) {
                String message = scanner.nextLine();
                writer.println(message);

                if (message.equalsIgnoreCase("/quit")) {
                    System.out.println("Exiting chat...");
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Client Error: " + e.getMessage());
        }
    }
}