package com.lead.infosystems.schooldiary.Progress;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.lead.infosystems.schooldiary.Data.SubMarksData;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class GraphView extends AppCompatActivity {
    BarChart barChart;
    BarData barData;
    UserDataSP userDataSP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);
        userDataSP = new UserDataSP(getApplicationContext());
        barChart = (BarChart) findViewById(R.id.chart);
        barChart.animateY(2000);
        barChart.invalidate();
        if ((getJsonExam(userDataSP.getUserData(UserDataSP.SUBJECTS))!=null) &&
                (getJsonDataSet(userDataSP.getUserData(UserDataSP.SUBJECTS))!=null)){
            barData = new BarData(getJsonExam(userDataSP.getUserData(UserDataSP.SUBJECTS)),
                    getJsonDataSet(userDataSP.getUserData(UserDataSP.SUBJECTS)));
            barChart.setData(barData);
        }
    }

//    private ArrayList<BarDataSet> getJsonDataSet(String userData) {
//        ArrayList<BarEntry> valueset = new ArrayList<>();
//        ArrayList<BarDataSet> dataSets = new ArrayList<>();
//        JSONArray jsonArray = null;
//        try {
//            jsonArray = new JSONArray(userData);
//            ArrayList<SubMarksData> subNames = new ArrayList<>();
//            for (int i = 0; i< jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                String examName = jsonObject.getString("exam_name");
//                JSONArray examDataArray = new JSONArray(jsonObject.getString("exam_data"));
//                BarEntry barEntry = null;
//
//                for(int j = 0; j< examDataArray.length(); j++) {
//                    String subName = examDataArray.getJSONObject(j).getString("sub_name");
//                    String subData = examDataArray.getJSONObject(j).getString("sub_data");
//                    JSONArray subjectDataArray = new JSONArray(subData);
//                    JSONObject subDataObj = subjectDataArray.getJSONObject(0);
//                    Float avgMarks = (Float.valueOf(subDataObj.getString("marks"))/Float.valueOf(subDataObj.getString("total_marks")))*100;
//                    //barEntry = new BarEntry(avgMarks,1);
////                    valueset.add(barEntry);
//                    subNames.add(new SubMarksData(examName,subName,avgMarks));
//                }
//
//
////                BarDataSet barDataSet = new BarDataSet(valueset, "a");
////                barDataSet.setColor(Color.rgb(i*20, i*40, i*80));
////                barDataSet.setValueTextSize(10);
////                barDataSet.setValueTextColor(Color.BLUE);
////                dataSets.add(barDataSet);
//            }
//
//            return getDataSet(subNames);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }

//    private ArrayList<BarDataSet> getDataSet(ArrayList<SubMarksData> subNames) {
//        for(int i=0 ; i<subNames.size();i++){
//            Log.e("data: "+subNames.get(i).getExamName(),subNames.get(i).getSubName()+" : "+subNames.get(i).getMarks());
//        }
//        Collections.sort(subNames, new Comparator<SubMarksData>() {
//            @Override
//            public int compare(SubMarksData lhs, SubMarksData rhs) {
//                return lhs.getDate().compareTo(rhs.getDate());
//            }
//        });
//        ArrayList<String> examNames = new ArrayList<>();
//        for(int i=0 ; i<subNames.size();i++){
//            Log.e("data sorted: "+subNames.get(i).getExamName(),subNames.get(i).getSubName()+" : "+subNames.get(i).getMarks());
//            examNames.add(subNames.get(i).getExamName());
//        }
//        LinkedHashSet<String> unique = new LinkedHashSet<>(examNames);
//        examNames = new ArrayList<>(unique);
//        Log.e("exa",unique.toString());
//        return null;
//    }
//
//    private ArrayList<String> getJsonExam(String userData) {
//        try {
//            ArrayList<String> examNames = new ArrayList<>();
//            JSONArray jsonArray = new JSONArray(userData);
//            for (int i = 0; i< jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                examNames.add(jsonObject.getString("exam_name"));
//            }
//           // Log.e("name",examNames.size()+"");
//            return examNames;
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    private ArrayList<String> getJsonExam(String data) {
        ArrayList<String> allExamNames = new ArrayList<>();
        LinkedHashSet<String> unique= new LinkedHashSet<String>() ;
        ArrayList<String> uniqueExamNames = new ArrayList<String>();

        try {
            JSONArray json = new JSONArray(data);
            for(int i = 0 ; i< json.length();i++) {
                JSONObject job = json.getJSONObject(i);
                String exam_details = job.getString("sub_data");
                if(exam_details != "null") {
                    JSONArray jsub = new JSONArray(exam_details);
                    for (int j = 0; j < jsub.length(); j++) {
                        JSONObject jsubobj = jsub.getJSONObject(j);
                        String exam_name = jsubobj.getString("exam_name");
                        allExamNames.add(exam_name);
                    }
                }
            }
            unique = new LinkedHashSet<>(allExamNames);

            uniqueExamNames = new ArrayList<>(unique);
         //   Collections.sort(uniqueExamNames);
            return uniqueExamNames;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    private ArrayList<BarDataSet> getJsonDataSet(String data) {
        ArrayList<BarDataSet> dataSets = null;
        int count = 0;
        try {
            dataSets = new ArrayList<>();
            JSONArray json_data = new JSONArray(data);
            for (int j = 0; j < json_data.length(); j++) {
                ArrayList<BarEntry> valueSet = new ArrayList<>();
                JSONObject job_data = json_data.getJSONObject(j);
                String sub_name = job_data.getString("sub_name");
                String sub_data_exam = job_data.getString("sub_data");
                if(sub_data_exam != "null"){
                JSONArray json_exam_data = new JSONArray(sub_data_exam);
                for (int i = 0; i < json_exam_data.length(); i++) {
                    JSONObject json_obj_exam_data = json_exam_data.getJSONObject(i);
                    String examName = json_obj_exam_data.getString("exam_name");
                    String exam_data = json_obj_exam_data.getString("exam_data");
                    JSONArray json_marks = new JSONArray(exam_data);
                    BarEntry barEntry;
                    for (int k = 0; k < json_marks.length(); k++) {
                        JSONObject json_obj_marks = json_marks.getJSONObject(k);
                        String total_marks = json_obj_marks.getString("total_marks");
                        String marks_exam = json_obj_marks.getString("marks");
                        String date = json_obj_marks.getString("date");
                        Float marks = (Float.parseFloat(marks_exam) / Float.parseFloat(total_marks) * 100);
                        barEntry = new BarEntry(marks, i);
                        valueSet.add(barEntry);
                    }
                }
                }
                    BarDataSet barDataSet = new BarDataSet(valueSet, sub_name);
                    barDataSet.setColor(Color.rgb(j*20, j*40, j*80));
                    barDataSet.setValueTextSize(10);
                    barDataSet.setValueTextColor(Color.BLUE);
                    dataSets.add(barDataSet);
            }

          //  getDataSet(marksData);
            return dataSets;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
