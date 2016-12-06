package com.lead.infosystems.schooldiary.Data;

/**
 * Created by Faheem on 04-12-2016.
 */

public class NotificationData {
    private String title,time,text;

    public NotificationData(String title, String time, String text) {
        this.title = title;
        this.time = time;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getText() {
        return text;
    }
}
