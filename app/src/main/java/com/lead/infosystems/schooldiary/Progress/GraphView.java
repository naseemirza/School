package com.lead.infosystems.schooldiary.Progress;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.lead.infosystems.schooldiary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class GraphView extends AppCompatActivity {
    BarChart barChart;
    BarData barData;
    SPData spdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);


        spdata = new SPData(getApplicationContext());
        barChart = (BarChart) findViewById(R.id.chart);

        barChart.animateXY(2000,2000);
        barChart.invalidate();
        if ((getJsonExam(spdata.getData(SPData.SUB))==null) && (getJsonDataSet(spdata.getData(SPData.SUB))==null)){

        }
        else {
            barData = new BarData(getJsonExam(spdata.getData(SPData.SUB)), getJsonDataSet(spdata.getData(SPData.SUB)));
            barChart.setData(barData);
        }



    }

    private ArrayList<String> getJsonExam(String data) {
        ArrayList<String> allExamNames = new ArrayList<>();
        HashSet<String> unique= new HashSet<String>() ;
        ArrayList<String> uniqueExamNames = new ArrayList<String>();

        try {
            JSONArray json = new JSONArray(data);
            for(int i = 0 ; i< json.length();i++) {
                JSONObject job = json.getJSONObject(i);
                String sub_details = job.getString("sub_name");
                Log.e("subject", sub_details);
                String exam_details = job.getString("sub_data");

                JSONArray jsub = new JSONArray(exam_details);
                for(int j = 0 ; j< jsub.length();j++) {
                    JSONObject jsubobj = jsub.getJSONObject(j);
                    String exam_name = jsubobj.getString("exam_name");
                    allExamNames.add(exam_name);
                }
            }
            unique = new HashSet<String>(allExamNames);

            uniqueExamNames = new ArrayList<String>(unique);
            Collections.sort(uniqueExamNames);
            Log.e("UNIQUE",uniqueExamNames.toString());
            return uniqueExamNames;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    private ArrayList<BarDataSet> getJsonDataSet(String data) {
        ArrayList<BarDataSet> dataSets = null;


        try {
            
            dataSets = new ArrayList<>();
            JSONArray json_data = new JSONArray(data);

            for (int j = 0; j < json_data.length(); j++) {
                ArrayList<BarEntry> valueSet = new ArrayList<>();
                JSONObject job_data = json_data.getJSONObject(j);
                String sub_name = job_data.getString("sub_name");
                Log.e("subject", sub_name);
                String sub_data_exam = job_data.getString("sub_data");
                JSONArray json_exam_data = new JSONArray(sub_data_exam);
                for (int i = 0; i < json_exam_data.length(); i++) {
                    JSONObject json_obj_exam_data = json_exam_data.getJSONObject(i);
                    String exam_name = json_obj_exam_data.getString("exam_name");
                    Log.e("exam_name", exam_name);
                    String exam_data = json_obj_exam_data.getString("exam_data");
                    Log.e("exam_data", exam_data);

                    JSONArray json_marks = new JSONArray(exam_data);
                    Log.e("marksJso", String.valueOf(json_marks));
                    for (int k = 0; k < json_marks.length(); k++) {
                        JSONObject json_obj_marks = json_marks.getJSONObject(k);
                        String marks_exam = json_obj_marks.getString("marks");
                        Log.e("marks", marks_exam);



                            BarEntry barEntry = new BarEntry(Float.parseFloat(marks_exam), i);
                            valueSet.add(barEntry);




                    }

                }




                    BarDataSet barDataSet = new BarDataSet(valueSet, sub_name);
                    barDataSet.setColor(Color.rgb(j * 100, 155, j * 50));
                    barDataSet.setValueTextSize(13);
                    barDataSet.setValueTextColor(Color.RED);



                    dataSets.add(barDataSet);



                    Log.e("datasetlength", dataSets.size() + "");

            }

            return dataSets;
        } catch (JSONException e) {

            e.printStackTrace();
            return null;
        }
    }
}
