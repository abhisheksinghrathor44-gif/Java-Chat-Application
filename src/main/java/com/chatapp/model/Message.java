package com.chatapp.model;

import java.sql.Timestamp;

public class Message {

    private int id;
    private String sender;
    private String receiver;      // NEW
    private String message;
    private boolean isPrivate;    // NEW
    private Timestamp sentTime;

    public Message() {
    }

    // Constructor for group messages
    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.receiver = null;
        this.isPrivate = false;
    }

    // Constructor for private messages
    public Message(String sender, String receiver, String message, boolean isPrivate) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isPrivate = isPrivate;
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

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Timestamp getSentTime() {
        return sentTime;
    }

    public void setSentTime(Timestamp sentTime) {
        this.sentTime = sentTime;
    }
}