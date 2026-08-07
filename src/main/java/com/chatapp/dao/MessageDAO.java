package com.chatapp.dao;

import com.chatapp.database.DBConnection;
import com.chatapp.model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {


    // ===============================
    // SAVE MESSAGE
    // ===============================

    public boolean saveMessage(Message message) {

        String sql =
                "INSERT INTO messages(sender, receiver, message, is_private) VALUES(?, ?, ?, ?)";


        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);


            ps.setString(1, message.getSender());

            // receiver can be NULL for group message
            ps.setString(2, message.getReceiver());

            ps.setString(3, message.getMessage());

            ps.setBoolean(4, message.isPrivate());


            int rows = ps.executeUpdate();


            return rows > 0;


        } catch (Exception e) {

            e.printStackTrace();
        }


        return false;
    }




    // ===============================
    // GET ALL MESSAGES
    // ===============================

    public List<Message> getAllMessages() {


        List<Message> messages = new ArrayList<>();


        String sql =
                "SELECT * FROM messages ORDER BY sent_time";


        try {


            Connection con = DBConnection.getConnection();


            PreparedStatement ps =
                    con.prepareStatement(sql);


            ResultSet rs = ps.executeQuery();



            while(rs.next()) {


                Message message = new Message();


                message.setId(
                        rs.getInt("id")
                );


                message.setSender(
                        rs.getString("sender")
                );


                message.setReceiver(
                        rs.getString("receiver")
                );


                message.setMessage(
                        rs.getString("message")
                );


                message.setPrivate(
                        rs.getBoolean("is_private")
                );


                message.setSentTime(
                        rs.getTimestamp("sent_time")
                );


                messages.add(message);

            }



        } catch(Exception e) {

            e.printStackTrace();
        }



        return messages;
    }





    // ===============================
    // GET ONLY GROUP CHAT HISTORY
    // ===============================

    public List<Message> getGroupMessages() {


        List<Message> messages = new ArrayList<>();


        String sql =
                "SELECT * FROM messages " +
                        "WHERE is_private=false " +
                        "ORDER BY sent_time";



        try {


            Connection con =
                    DBConnection.getConnection();



            PreparedStatement ps =
                    con.prepareStatement(sql);



            ResultSet rs =
                    ps.executeQuery();




            while(rs.next()) {


                Message message =
                        new Message();



                message.setId(
                        rs.getInt("id")
                );


                message.setSender(
                        rs.getString("sender")
                );


                message.setMessage(
                        rs.getString("message")
                );


                message.setPrivate(false);


                message.setSentTime(
                        rs.getTimestamp("sent_time")
                );



                messages.add(message);

            }



        } catch(Exception e) {

            e.printStackTrace();
        }



        return messages;
    }






    // ===============================
    // GET PRIVATE CHAT BETWEEN USERS
    // ===============================

    public List<Message> getPrivateMessages(
            String user1,
            String user2
    ) {



        List<Message> messages =
                new ArrayList<>();



        String sql =
                "SELECT * FROM messages " +
                        "WHERE is_private=true " +
                        "AND ((sender=? AND receiver=?) " +
                        "OR (sender=? AND receiver=?)) " +
                        "ORDER BY sent_time";



        try {


            Connection con =
                    DBConnection.getConnection();



            PreparedStatement ps =
                    con.prepareStatement(sql);



            ps.setString(1, user1);
            ps.setString(2, user2);

            ps.setString(3, user2);
            ps.setString(4, user1);



            ResultSet rs =
                    ps.executeQuery();




            while(rs.next()) {



                Message message =
                        new Message();



                message.setId(
                        rs.getInt("id")
                );



                message.setSender(
                        rs.getString("sender")
                );



                message.setReceiver(
                        rs.getString("receiver")
                );



                message.setMessage(
                        rs.getString("message")
                );



                message.setPrivate(true);



                message.setSentTime(
                        rs.getTimestamp("sent_time")
                );



                messages.add(message);

            }



        } catch(Exception e) {


            e.printStackTrace();

        }




        return messages;

    }

}