package com.lead.infosystems.schooldiary.Data;

/**
 * Created by Faheem on 26-11-2016.
 */

public class ChatContact {

    private String userID,firstName,lastName, profilePic_link;

    public ChatContact(String userID, String firstName, String lastName, String profilePic_link) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePic_link = profilePic_link;
    }

    public String getProfilePic_link() {
        return profilePic_link;
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
