package com.lead.infosystems.schooldiary.Data;

import com.lead.infosystems.schooldiary.ServerConnection.Utils;

/**
 * Created by Faheem on 07-12-2016.
 */

public class DieryHomeWorkData {
    private String subject,givenDate,dueDate,text;

    public DieryHomeWorkData(String subject, String givenDate, String dueDate, String text) {
        this.subject = subject;
        this.givenDate = givenDate;
        this.dueDate = dueDate;
        this.text = text;
    }

    public String getSubject() {
        return subject;
    }

    public String getGivenDate() {
        return Utils.getTimeString(givenDate);
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getText() {
        return text;
    }
}
