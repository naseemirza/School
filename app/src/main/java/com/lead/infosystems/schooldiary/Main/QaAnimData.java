package com.lead.infosystems.schooldiary.Main;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Faheem on 22-11-2016.
 */

public class QaAnimData {
    private TextView name, time, text;
    private ImageView propic;
    private String questionNum;

    public QaAnimData(TextView name, TextView time, TextView text, ImageView propic, String questionNum) {
        this.name = name;
        this.time = time;
        this.text = text;
        this.propic = propic;
        this.questionNum = questionNum;
    }

    public String getQuestionNum() {
        return questionNum;
    }

    public TextView getName() {
        return name;
    }

    public TextView getTime() {
        return time;
    }

    public TextView getText() {
        return text;
    }

    public ImageView getPropic() {
        return propic;
    }
}
