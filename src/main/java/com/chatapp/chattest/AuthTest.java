package com.chatapp.chattest;

import com.chatapp.dao.UserDAO;
import com.chatapp.model.User;

import java.util.Scanner;

public class AuthTest {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        UserDAO dao = new UserDAO();

        while (true) {

            System.out.println("\n========== CHAT APPLICATION ==========");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number (1-3).");
                continue;
            }

            switch (choice) {

                case 1:
                    System.out.print("Username: ");
                    String username = sc.nextLine().trim();

                    if (username.isEmpty()) {
                        System.out.println("Username cannot be empty.");
                        break;
                    }

                    if (dao.usernameExists(username)) {
                        System.out.println("Username already exists.");
                        break;
                    }

                    System.out.print("Email: ");
                    String email = sc.nextLine().trim();

                    if (dao.emailExists(email)) {
                        System.out.println("Email already registered.");
                        break;
                    }

                    System.out.print("Password: ");
                    String password = sc.nextLine().trim();

                    if (password.isEmpty()) {
                        System.out.println("Password cannot be empty.");
                        break;
                    }

                    User user = new User(username, password, email);

                    if (dao.registerUser(user)) {
                        System.out.println("Registration Successful.");
                    } else {
                        System.out.println("Registration Failed. Please try again.");
                    }

                    break;

                case 2:
                    System.out.print("Username: ");
                    username = sc.nextLine().trim();

                    System.out.print("Password: ");
                    password = sc.nextLine().trim();

                    if (dao.loginUser(username, password)) {
                        System.out.println("Login Successful.");
                    } else {
                        System.out.println("Invalid Username or Password.");
                    }

                    break;

                case 3:
                    System.out.println("Thank you.");
                    sc.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid Choice. Select 1, 2, or 3.");
            }
        }
    }
}