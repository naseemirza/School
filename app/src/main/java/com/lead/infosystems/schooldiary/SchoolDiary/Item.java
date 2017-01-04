package com.lead.infosystems.schooldiary.SchoolDiary;

import android.view.View;

public class Item {
    private String homework_title, homework_contents, lastDate_submission, subject,homeworkDate;
    int userUpload;

    private View.OnClickListener requestBtnClickListener;

    public Item(String homework_title, String homework_contents, String lastDate_submission, String subject, String homeworkDate, String userUpload) {
        this.homework_title = homework_title;
        this.homework_contents = homework_contents;
        this.lastDate_submission = lastDate_submission;
        this.subject=subject;
        this.homeworkDate = homeworkDate;
        this.userUpload = Integer.parseInt(userUpload);
    }

    public int getUserUpload() {
        return userUpload;
    }

    public String getHomework_title() {
        return homework_title;
    }

    public String getHomework_contents() {
        return homework_contents;
    }

    public String getLastDate_submission() {
        return lastDate_submission;
    }

    public String getSubject() {
        return subject;
    }

    public String getHomeworkDate() {
        return homeworkDate;
    }

    public View.OnClickListener getRequestBtnClickListener() {
        return requestBtnClickListener;
    }

    public void setRequestBtnClickListener(View.OnClickListener requestBtnClickListener) {
        this.requestBtnClickListener = requestBtnClickListener;
    }


}
