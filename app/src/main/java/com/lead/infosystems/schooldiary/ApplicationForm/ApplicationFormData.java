package com.lead.infosystems.schooldiary.ApplicationForm;

/**
 * Created by Naseem on 28-11-2016.
 */

public class ApplicationFormData {

    String name,link;
    int deleteUser;

    public ApplicationFormData(String name, String link, String number_user ) {
        this.name = name;
        this.link = link;
        this.deleteUser= Integer.parseInt(number_user);
    }

    public int getDeleteUser() {
        return deleteUser;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }
}
