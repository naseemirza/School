package com.lead.infosystems.schooldiary.SchoolDiary;

public class Item {
    private String homework_title, homework_contents, lastDate_submission, subject,homeworkDate, homework_number;
    int userUpload;

    public Item(String homework_title, String homework_contents, String lastDate_submission, String subject, String homeworkDate, String userUpload, String homework_number) {
        this.homework_title = homework_title;
        this.homework_contents = homework_contents;
        this.lastDate_submission = lastDate_submission;
        this.subject=subject;
        this.homeworkDate = homeworkDate;
        this.userUpload = Integer.parseInt(userUpload);
        this.homework_number = homework_number;
    }

    public String getHomework_number() {
        return homework_number;
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

}
