package com.lead.infosystems.schooldiary.Data;

import android.util.Log;

/**
 * Created by Faheem on 26-11-2016.
 */

public class ChatListItems {

    private String chat_id, user1, user2, date, last_message;

    public ChatListItems(String chat_id, String user1, String user2, String date, String last_message) {
        this.chat_id = chat_id;
        this.user1 = user1;
        this.user2 = user2;
        this.date = date;
        this.last_message = last_message;
    }

    public String getChat_id() {
        return chat_id;
    }

    public String getChatUserName(String me){
        Log.e("me",me+" user1: "+ user1);
        if(me.contentEquals(user1)){
            return user2;
        }else{
            return user1;
        }
    }

    public String getDate() {
        return date;
    }

    public String getLast_message() {
        return last_message;
    }
}
