package com.lead.infosystems.schooldiary.Data;

import android.util.Log;

/**
 * Created by Faheem on 26-11-2016.
 */

public class ChatListItems {

    private String chat_id, user1Name,user1ID, user2Name,user2ID, date, last_message, profilePic_link;

    public ChatListItems(String chat_id, String user1Name, String user1ID, String user2Name
            , String user2ID, String last_message, String date, String profilePic_link) {
        this.chat_id = chat_id;
        this.user1Name = user1Name;
        this.user1ID = user1ID;
        this.user2Name = user2Name;
        this.user2ID = user2ID;
        this.date = date;
        this.last_message = last_message;
        this.profilePic_link = profilePic_link;
    }

    public String getProfilePic_link() {
        return profilePic_link;
    }

    public String getChat_id() {
        return chat_id;
    }

    public String getUser1Name() {
        return user1Name;
    }

    public String getUser1ID() {
        return user1ID;
    }

    public String getUser2Name() {
        return user2Name;
    }

    public String getUser2ID() {
        return user2ID;
    }

    public String getDate() {
        return date;
    }

    public String getLast_message() {
        return last_message;
    }

    public String getChatUserName(String myName) {
        if(myName.contentEquals(user1Name)){
            return user2Name;
        }else{
            return user1Name;
        }
    }

    public String getOtherUserId(String myId) {
        if(myId.contentEquals(user1ID)){
            return user2ID;
        }else {
            return user1ID;
        }
    }
}
