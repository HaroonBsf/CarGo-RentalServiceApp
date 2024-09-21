package com.example.fyp.notifications;

public class FCMMessage {
    Sender sender;

    public FCMMessage(Sender sender) {
        this.sender = sender;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }
}
