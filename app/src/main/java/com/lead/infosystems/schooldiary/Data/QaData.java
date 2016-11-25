package com.lead.infosystems.schooldiary.Data;

/**
 * Created by Faheem on 18-11-2016.
 */

public class QaData {
    private String student_number, name, questionText, qNum, numAnswers,time;

    public QaData(String student_number, String name, String questionText, String qNum, String numAnswers, String time) {
        this.student_number = student_number;
        this.name = name;
        this.questionText = questionText;
        this.qNum = qNum;
        this.numAnswers = numAnswers;
        this.time = time;
    }

    public String getStudent_number() {
        return student_number;
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
