package com.lead.infosystems.schooldiary.Attendance;

/**
 * Created by MADHU on 12/6/2016.
 */

public class AttendanceData {
    String year, day, month, attendance;

    public AttendanceData(String year, String day, String month, String attendance) {
        this.year = year;
        this.day = day;
        this.month = month;
        this.attendance = attendance;
    }

    public String getYear() {
        return year;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getAttendance() {
        return attendance;
    }
}
