package com.lead.infosystems.schooldiary.Data;

import com.lead.infosystems.schooldiary.Generic.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Faheem on 17-01-2017.
 */

public class SubMarksData {
    private String examName;
    private String subName;
    private String dateString;
    private Float marks;

    public SubMarksData(String examName, String subName, Float marks,String date ) {
        this.subName = subName;
        this.marks = marks;
        this.dateString = date;
        this.examName = examName;
    }

    public Date getDate() {
        Date date = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Utils.DATE_FORMAT);
            date = dateFormat.parse(dateString+" 10:00:00");
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getExamName() {return examName;}

    public String getSubName() {
        return subName;
    }

    public Float getMarks() {
        return marks;
    }
}
