package com.chatapp.server;

import com.chatapp.dao.MessageDAO;
import com.chatapp.model.Message;

import java.io.*;
import java.net.Socket;
import java.util.Vector;

public class ClientHandler implements Runnable {


    private Socket socket;
    private PrintWriter writer;
    private String username;

    // Current private chat user
    private String privateChatUser = null;


    public static Vector<ClientHandler> clients = new Vector<>();



    public ClientHandler(Socket socket) {


        this.socket = socket;


        try {


            writer =
                    new PrintWriter(
                            socket.getOutputStream(),
                            true
                    );


        }
        catch(IOException e) {


            e.printStackTrace();

        }


    }





    // Find user by username

    private ClientHandler findClientByUsername(String username) {


        for(ClientHandler client : clients) {


            if(client.username != null &&
                    client.username.equalsIgnoreCase(username)) {


                return client;

            }


        }


        return null;

    }





    @Override
    public void run() {



        try {


            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );




            // First message is username

            username =
                    reader.readLine();




            // Duplicate username check

            if(findClientByUsername(username) != null) {


                writer.println(
                        "Username already exists. Connection closed."
                );


                socket.close();

                return;

            }





            clients.add(this);



            MessageDAO dao =
                    new MessageDAO();






            // ===============================
            // GROUP HISTORY
            // ===============================


            writer.println(
                    "------ Previous Messages ------"
            );



            for(Message msg : dao.getGroupMessages()) {


                writer.println(
                        msg.getSender()
                                + " : "
                                + msg.getMessage()
                );


            }



            writer.println(
                    "-------------------------------"
            );





            System.out.println(
                    username + " joined the chat."
            );





            // Notify join


            for(ClientHandler client : clients) {


                if(client != this) {


                    client.writer.println(
                            username
                                    + " joined the chat."
                    );


                }


            }







            String message;



            while((message = reader.readLine()) != null) {



                if(message.trim().isEmpty()) {


                    continue;

                }







                // ===============================
                // ONLINE USERS
                // /users
                // ===============================


                if(message.equalsIgnoreCase("/users")) {



                    writer.println(
                            "------ Online Users ------"
                    );



                    for(ClientHandler client : clients) {


                        writer.println(
                                client.username
                        );


                    }



                    writer.println(
                            "--------------------------"
                    );



                    continue;

                }









                // ===============================
                // START PRIVATE CHAT
                // /private Rahul
                // ===============================


                if(message.startsWith("/private ")) {



                    String targetUsername =
                            message.split("\\s+")[1];



                    ClientHandler target =
                            findClientByUsername(
                                    targetUsername
                            );




                    if(target != null) {


                        privateChatUser =
                                targetUsername;



                        writer.println(
                                "Private chat started with "
                                        + targetUsername
                        );


                    }
                    else {


                        writer.println(
                                "User "
                                        + targetUsername
                                        + " is offline."
                        );


                    }



                    continue;

                }










                // ===============================
                // EXIT PRIVATE CHAT
                // /exit
                // ===============================


                if(message.equalsIgnoreCase("/exit")) {



                    if(privateChatUser != null) {


                        writer.println(
                                "Exited private chat with "
                                        + privateChatUser
                        );



                        privateChatUser = null;



                    }
                    else {


                        writer.println(
                                "You are already in group chat."
                        );


                    }



                    continue;

                }









                // ===============================
                // PRIVATE HISTORY
                // /history Rahul
                // ===============================


                if(message.startsWith("/history ")) {



                    String targetUser =
                            message.split("\\s+")[1];




                    writer.println(
                            "------ Private History with "
                                    + targetUser
                                    + " ------"
                    );




                    for(Message msg :
                            dao.getPrivateMessages(
                                    username,
                                    targetUser
                            )) {



                        writer.println(
                                msg.getSender()
                                        + " : "
                                        + msg.getMessage()
                        );


                    }



                    writer.println(
                            "--------------------------------"
                    );



                    continue;

                }









                // ===============================
                // PRIVATE MESSAGE
                // /msg Rahul Hello
                // ===============================


                if(message.startsWith("/msg ")) {



                    String parts[] =
                            message.split("\\s+",3);



                    if(parts.length < 3) {



                        writer.println(
                                "Usage: /msg <username> <message>"
                        );



                        continue;

                    }






                    String targetUsername =
                            parts[1];



                    String privateMessage =
                            parts[2];





                    ClientHandler target =
                            findClientByUsername(
                                    targetUsername
                            );






                    if(target != null) {



                        Message privateMsg =
                                new Message(
                                        username,
                                        targetUsername,
                                        privateMessage,
                                        true
                                );



                        dao.saveMessage(privateMsg);





                        target.writer.println(
                                "[Private] "
                                        + username
                                        + " : "
                                        + privateMessage
                        );





                        writer.println(
                                "[Private to "
                                        + targetUsername
                                        + "] "
                                        + privateMessage
                        );



                    }
                    else {


                        writer.println(
                                "User "
                                        + targetUsername
                                        + " is offline."
                        );


                    }




                    continue;

                }









                // ===============================
                // PRIVATE CHAT MODE MESSAGE
                // ===============================


                if(privateChatUser != null) {



                    ClientHandler target =
                            findClientByUsername(
                                    privateChatUser
                            );




                    if(target != null) {



                        Message privateMsg =
                                new Message(
                                        username,
                                        privateChatUser,
                                        message,
                                        true
                                );



                        dao.saveMessage(privateMsg);





                        target.writer.println(
                                "[Private] "
                                        + username
                                        + " : "
                                        + message
                        );




                        writer.println(
                                "[Private to "
                                        + privateChatUser
                                        + "] "
                                        + message
                        );



                    }



                    continue;

                }









                // ===============================
                // GROUP MESSAGE
                // ===============================


                Message groupMsg =
                        new Message(
                                username,
                                message
                        );



                dao.saveMessage(groupMsg);




                String fullMessage =
                        username
                                + " : "
                                + message;




                System.out.println(
                        fullMessage
                );





                for(ClientHandler client : clients) {


                    client.writer.println(
                            fullMessage
                    );


                }





            }




        }
        catch(Exception e) {


            e.printStackTrace();

        }






        finally {



            clients.remove(this);




            for(ClientHandler client : clients) {


                client.writer.println(
                        username
                                + " left the chat."
                );


            }




            try {


                socket.close();


            }
            catch(IOException e) {


                e.printStackTrace();

            }



        }



    }


}