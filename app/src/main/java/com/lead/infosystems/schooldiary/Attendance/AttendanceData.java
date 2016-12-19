package com.lead.infosystems.schooldiary.Attendance;

import com.lead.infosystems.schooldiary.ServerConnection.Utils;

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
    public String getAttendance() {
        return attendance;
    }

    public long getTimeInMili(){
       return Utils.getTimeInMili(year + "-" + month + "-" + day + " " + 10 + ":" + 20 + ":" + 12);
    }
}
