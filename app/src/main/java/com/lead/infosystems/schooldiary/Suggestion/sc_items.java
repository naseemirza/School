package com.lead.infosystems.schooldiary.Suggestion;

/**
 * Created by Naseem on 02-01-2017.
 */

public class sc_items {

    private String first_name,last_name,class_t,division_t,profilePic_link,subject,content,date;

    public sc_items(String first_name, String last_name, String class_t, String division_t, String profilePic_link, String subject, String content, String date) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.class_t = class_t;
        this.division_t = division_t;
        this.profilePic_link = profilePic_link;
        this.subject = subject;
        this.content = content;
        this.date = date;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getClass_t() {
        return class_t;
    }

    public String getDivision_t() {
        return division_t;
    }

    public String getProfilePic_link() {
        return profilePic_link;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

}
