package com.lead.infosystems.schooldiary.ApplicationForm;

/**
 * Created by Naseem on 28-11-2016.
 */

public class ApplicationFormData {

    String name,link;

    public ApplicationFormData(String name, String link) {
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
