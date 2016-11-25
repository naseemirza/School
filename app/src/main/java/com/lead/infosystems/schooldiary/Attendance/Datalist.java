package com.lead.infosystems.schooldiary.Attendance;

/**
 * Created by Naseem on 04-11-2016.
 */

public class Datalist {

    int roll;
    String name;


    public Datalist(int roll, String name) {
        this.roll = roll;
        this.name = name;
    }

    public int getRoll() {
        return roll;
    }

    public String getName() {
        return name;
    }
}
