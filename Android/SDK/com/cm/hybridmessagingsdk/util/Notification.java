package com.cm.hybridmessagingsdk.util;

/**
 * Created by Dion on 04/12/14
 */
public class Notification {

    private String messageId;
    private String notificationId;
    private String message;

    public Notification(String messageId, String notificationId, String message) {
        this.messageId = messageId;
        this.notificationId = notificationId;
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getMessage() {
        return message;
    }
}
