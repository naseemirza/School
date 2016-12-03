package com.lead.infosystems.schooldiary.Data;

/**
 * Created by Faheem on 28-11-2016.
 */

public class ChatItemData {
    private String chatId,userId,message,time;

    public ChatItemData(String chatId, String userId, String message, String time) {
        this.userId = userId;
        this.message = message;
        this.time = time;
        this.chatId = chatId;
    }

    public String getChatId() {
        return chatId;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }
}
