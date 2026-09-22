package com.chatapp.dao;

import com.chatapp.database.DBConnection;
import com.chatapp.model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public boolean saveMessage(Message message) {
        String sql = "INSERT INTO messages(sender, receiver, message, is_private) VALUES(?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, message.getSender());
            ps.setString(2, message.getReceiver()); // NULL allowed for broadcast
            ps.setString(3, message.getMessage());
            ps.setBoolean(4, message.isPrivate());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages ORDER BY sent_time";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public List<Message> getGroupMessages() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE is_private = false ORDER BY sent_time";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public List<Message> getPrivateMessages(String user1, String user2) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE is_private = true " +
                "AND ((sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?)) " +
                "ORDER BY sent_time";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user1);
            ps.setString(2, user2);
            ps.setString(3, user2);
            ps.setString(4, user1);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToMessage(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("id"));
        message.setSender(rs.getString("sender"));
        message.setReceiver(rs.getString("receiver"));
        message.setMessage(rs.getString("message"));
        message.setPrivate(rs.getBoolean("is_private"));
        message.setSentTime(rs.getTimestamp("sent_time"));
        return message;
    }
}