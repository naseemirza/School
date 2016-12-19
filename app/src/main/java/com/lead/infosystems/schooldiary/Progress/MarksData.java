package com.lead.infosystems.schooldiary.Progress;

/**
 * Created by Naseem on 12-11-2016.
 */

public class MarksData {

    String date;
    String exam_name;
    String total_max;
    String obtained_max;
    String percentage;

    public MarksData(String date, String exam_name, String total_max, String obtained_max, String percentage) {
        this.date = date;
        this.exam_name = exam_name;
        this.total_max = total_max;
        this.obtained_max = obtained_max;
        this.percentage = percentage;
    }

    public String getDate() {
        return date;
    }

    public String getExam_name() {
        return exam_name;
    }

    public String getTotal_max() {
        return total_max;
    }

    public String getObtained_max() {
        return obtained_max;
    }

    public String getPercentage() {
        return percentage;
    }
}
