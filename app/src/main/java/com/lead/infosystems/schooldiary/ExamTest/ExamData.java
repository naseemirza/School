package com.lead.infosystems.schooldiary.ExamTest;

/**
 * Created by MADHU on 12/28/2016.
 */

public class ExamData {
    String exam_name, exam_date, exam_description, exam_pdf_link;

    public ExamData(String exam_name, String exam_date, String exam_description, String exam_pdf_link) {
        this.exam_name = exam_name;
        this.exam_date = exam_date;
        this.exam_description = exam_description;
        this.exam_pdf_link = exam_pdf_link;
    }

    public String getExam_name() {
        return exam_name;
    }

    public String getExam_date() {
        return exam_date;
    }

    public String getExam_description() {
        return exam_description;
    }

    public String getExam_pdf_link() {
        return exam_pdf_link;
    }
}
