package com.lead.infosystems.schooldiary.Events;

/**
 * Created by MADHU on 12/8/2016.
 */

public class EventsData {
    String event_name, event_detail, event_date, submit_date, school_number;



    public EventsData(String event_name, String event_detail, String event_date, String submit_date, String school_number) {
        this.event_name = event_name;
        this.event_detail = event_detail;
        this.event_date= event_date;
        this.submit_date = submit_date;
        this.school_number = school_number;
    }
    public String getEvent_name() {
        return event_name;
    }

    public String getEvent_detail() {
        return event_detail;
    }

    public String getEvent_date() {
        return event_date;
    }

    public String getSubmit_date() {
        return submit_date;
    }

    public String getSchool_number() {
        return school_number;
    }
}
