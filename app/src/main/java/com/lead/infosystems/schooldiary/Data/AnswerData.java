package com.lead.infosystems.schooldiary.Data;

import android.widget.ImageView;

/**
 * Created by Faheem on 22-11-2016.
 */

public class AnswerData {
    private String profilePic_link,name, time, text,studentNumber,answerNumber;
    private ImageView propic;

    public AnswerData(String profilePic_link,String studentNumber,String answerNumber ,String name, String time, String text, ImageView propic) {
        this.profilePic_link = profilePic_link;
        this.name = name;
        this.time = time;
        this.text = text;
        this.propic = propic;
        this.studentNumber = studentNumber;
        this.answerNumber = answerNumber;
    }

    public String getProfilePic_link() {
        return profilePic_link;
    }

    public String getAnswerNumber() {
        return answerNumber;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public ImageView getPropic() {
        return propic;
    }
}
