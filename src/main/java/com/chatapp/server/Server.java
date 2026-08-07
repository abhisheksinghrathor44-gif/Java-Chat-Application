package com.chatapp.server;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {

        ServerSocket serverSocket = null;

        try {

            serverSocket = new ServerSocket(5000);

            System.out.println("Server Started on port 5000...");


            while (true) {

                Socket socket = serverSocket.accept();


                System.out.println(
                        "Client Connected : "
                                + socket.getPort()
                );


                ClientHandler handler =
                        new ClientHandler(socket);


                Thread thread =
                        new Thread(handler);


                thread.start();
            }


        } catch(Exception e) {

            e.printStackTrace();

        } finally {

            try {

                if(serverSocket != null) {
                    serverSocket.close();
                }

            } catch(Exception e) {

                e.printStackTrace();
            }
        }
    }
}