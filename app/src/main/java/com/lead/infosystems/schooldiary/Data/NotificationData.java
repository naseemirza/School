package com.lead.infosystems.schooldiary.Data;

/**
 * Created by Faheem on 04-12-2016.
 */

public class NotificationData {
    private String notificationNumber,date,mClass,division,notificationText,type;
    public static final String HOME_WORK = "Home_Work";
    public static final String MARKS = "Marks_Uploaded";
    public static final String MODEL_QP = "New_Model_Question_paper";
    public static final String TEST_EXAM = "Test_And_Exam";
    public static final String EVENT = "New_Event";
    public static final String APPLICATION_FORM = "New_Application_Form";

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
