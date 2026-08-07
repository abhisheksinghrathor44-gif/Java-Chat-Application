package com.chatapp.dao;

import com.chatapp.database.DBConnection;
import com.chatapp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    // ===============================
    // REGISTER USER
    // ===============================
    public boolean registerUser(User user) {

        String sql = "INSERT INTO chat_users(username, password, email) VALUES(?, ?, ?)";

        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());

            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ===============================
    // LOGIN USER
    // ===============================
    public boolean loginUser(String username, String password) {

        String sql = "SELECT * FROM chat_users WHERE username = ? AND password = ?";

        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ===============================
    // CHECK IF USERNAME EXISTS
    // ===============================
    public boolean usernameExists(String username) {

        String sql = "SELECT * FROM chat_users WHERE username = ?";

        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ===============================
    // CHECK IF EMAIL EXISTS
    // ===============================
    public boolean emailExists(String email) {

        String sql = "SELECT * FROM chat_users WHERE email = ?";

        try {

            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}