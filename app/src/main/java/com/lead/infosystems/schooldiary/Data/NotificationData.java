package com.lead.infosystems.schooldiary.Data;

/**
 * Created by Faheem on 04-12-2016.
 */

public class NotificationData {
    private String notificationNumber,date,mClass,division,notificationText,type;
    public static final String HOME_WORK = "HOME_WORK";
    public static final String MARKS = "MARKS";
    public static final String MODEL_QP = "M_Q_P";
    public static final String TEST_EXAM = "EXAM";
    public static final String EVENT = "EVENT";
    public static final String APPLICATION_FORM = "APPLICATION_FORM";

    public NotificationData(String notificationNumber,String date, String mClass, String division, String notificationText,String type) {
        this.notificationNumber = notificationNumber;
        this.mClass = mClass;
        this.division = division;
        this.notificationText = notificationText;
        this.type = type;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getNotificationNumber() {
        return notificationNumber;
    }

    public String getmClass() {
        return mClass;
    }

    public String getDivision() {
        return division;
    }

    public String getNotificationText() {
        return notificationText;
    }
}
