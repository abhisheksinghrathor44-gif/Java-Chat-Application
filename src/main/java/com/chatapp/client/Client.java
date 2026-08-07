package com.chatapp.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class Client {
    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket(
                    "localhost",
                    5000
            );


            System.out.println(
                    "Connected to server"
            );



            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );



            PrintWriter writer =
                    new PrintWriter(
                            socket.getOutputStream(),
                            true
                    );



            Scanner scanner =
                    new Scanner(System.in);





            // ==========================
            // USERNAME
            // ==========================

            System.out.print(
                    "Enter Username: "
            );


            String username =
                    scanner.nextLine();



            writer.println(username);






            // ==========================
            // RECEIVE MESSAGE THREAD
            // ==========================

            Thread receiveThread =
                    new Thread(() -> {


                        try {


                            String message;



                            while(
                                    (message = reader.readLine())
                                            != null
                            ) {


                                System.out.println(
                                        message
                                );


                            }


                        }
                        catch(IOException e) {


                            System.out.println(
                                    "Disconnected from server"
                            );


                        }


                    });



            receiveThread.start();







            // ==========================
            // SEND MESSAGE
            // ==========================

            while(true) {


                String message =
                        scanner.nextLine();



                /*
                   /exit
                   -> handled by server
                   -> leaves private chat only

                   /quit
                   -> closes application
                */


                writer.println(message);




                if(message.equalsIgnoreCase("/quit")) {


                    break;


                }


            }






        }
        catch(Exception e) {


            e.printStackTrace();


        }
        finally {


            try {


                if(socket != null) {


                    socket.close();


                }


            }
            catch(IOException e) {


                e.printStackTrace();

            }


        }


    }

}