package com.lead.infosystems.schooldiary.Data;

/**
 * Created by Faheem on 26-11-2016.
 */

public class ChatContact {

    private String userID,firstName,lastName;

    public ChatContact(String userID, String firstName, String lastName) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUserID() {
        return userID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return firstName+" "+lastName;
    }
}
