package com.lead.infosystems.schooldiary.Attendance;

/**
 * Created by Naseem on 04-11-2016.
 */

public class Datalist {

    String student_name,number_user,student_roll,attendance;

    public Datalist(String student_name,String student_roll, String student_number, String attendance) {
        this.student_name = student_name;
        this.number_user = student_number;
        this.student_roll = student_roll;
        this.attendance = attendance;
    }

    public String getStudent_name() {
        return student_name;
    }

    public String getStudent_number() {
        return number_user;
    }

    public String getStudent_roll() {
        return student_roll;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }
}
