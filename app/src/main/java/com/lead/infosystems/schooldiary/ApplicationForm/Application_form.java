package com.lead.infosystems.schooldiary.ApplicationForm;

/**
 * Created by Naseem on 28-11-2016.
 */

public class Application_form {

    String name,link;

    public Application_form(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }
}
