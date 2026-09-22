package com.chatapp.model;

import java.sql.Timestamp;

public class Message {

    private int id;
    private String sender;
    private String receiver;
    private String message;
    private boolean isPrivate;
    private Timestamp sentTime;

    public Message() {
    }

    // Constructor for public broadcast messages
    public Message(String sender, String message) {
        this(sender, null, message, false);
    }

    // Constructor for targeted/private messages
    public Message(String sender, String receiver, String message, boolean isPrivate) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isPrivate = isPrivate;
    }

    // Full Constructor
    public Message(int id, String sender, String receiver, String message, boolean isPrivate, Timestamp sentTime) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isPrivate = isPrivate;
        this.sentTime = sentTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public Timestamp getSentTime() {
        return sentTime;
    }

    public void setSentTime(Timestamp sentTime) {
        this.sentTime = sentTime;
    }

    @Override
    public String toString() {
        if (isPrivate) {
            return String.format("[%s -> %s]: %s", sender, receiver, message);
        }
        return String.format("[%s]: %s", sender, message);
    }
}