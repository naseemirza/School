package com.lead.infosystems.schooldiary.Data;

/**
 * Created by Faheem on 18-11-2016.
 */

public class QaData {
    private String number_user, name, questionText, qNum, numAnswers,time;

    public QaData(String number_user, String name, String questionText, String qNum, String numAnswers, String time) {
        this.number_user = number_user;
        this.name = name;
        this.questionText = questionText;
        this.qNum = qNum;
        this.numAnswers = numAnswers;
        this.time = time;
    }

    public String getStudent_number() {
        return number_user;
    }

    public String getName() {
        return name;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getqNum() {
        return qNum;
    }

    public String getNumAnswers() {
        return numAnswers;
    }

    public String getTime() {
        return time;
    }
}
