package com.lead.infosystems.schooldiary.Model_Paper;

/**
 * Created by Naseem on 17-11-2016.
 */

public class Model_paper {

    String name,link;
    int userUpload;

    public Model_paper(String name, String link, String userUpload) {
        this.name = name;
        this.link = link;
        this.userUpload = Integer.parseInt(userUpload);
    }

    public int getUserUpload() {
        return userUpload;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }
}
