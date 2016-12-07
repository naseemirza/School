package com.lead.infosystems.schooldiary.Progress;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Marks extends AppCompatActivity {

    public Button btn;
    SPData spdata;
    ListView marks;
    String sub_name_list;
    public static List<MarksData> items = new ArrayList<MarksData>();
    public void init(){
        btn=(Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SinlGraph.class);
                intent.putExtra("sub_name", sub_name_list);
                startActivity(intent);


            }
        });

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);

        Intent intent = getIntent();

        sub_name_list = intent.getStringExtra("sub_name");
        spdata = new SPData(getApplicationContext());
        getJsonExam(spdata.getData(SPData.SUB));

        marks =(ListView)findViewById(R.id.marks);
        marks.setAdapter(new MyAdaptor());
        init();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void getJsonExam(String data) {
        try {
            items.clear();
            JSONArray json_data = new JSONArray(data);
            for(int j = 0 ; j<json_data.length(); j++) {

                JSONObject job_data = json_data.getJSONObject(j);
                String sub_name = job_data.getString("sub_name");
                if(Objects.equals(sub_name_list, sub_name)) {
                    String sub_data_exam = job_data.getString("sub_data");
                    JSONArray json_exam_data = new JSONArray(sub_data_exam);

                    for (int i = 0; i < json_exam_data.length(); i++) {
                        JSONObject json_obj_exam_data = json_exam_data.getJSONObject(i);
                        String exam_name = json_obj_exam_data.getString("exam_name");
                        String exam_data = json_obj_exam_data.getString("exam_data");

                        JSONArray json_marks = new JSONArray(exam_data);
                        for (int k = 0; k < json_marks.length(); k++) {
                            JSONObject json_obj_marks = json_marks.getJSONObject(k);
                            String marks_exam = json_obj_marks.getString("marks");
                            int marks = (int) Float.parseFloat(marks_exam);
                            String total_marks = json_obj_marks.getString("total_marks");
                            int total = Integer.parseInt(total_marks);
                            String date = json_obj_marks.getString("date");
                            Float percentage = (float) ((marks * 100) / total);
                            items.add(new MarksData(date, exam_name, total, marks, percentage));


                        }

                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
    class MyAdaptor extends ArrayAdapter<MarksData> {

        public MyAdaptor() {
            super(getApplicationContext(), R.layout.layout, items);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ItemView = convertView;
            if (ItemView == null) {
                ItemView = getLayoutInflater().inflate(R.layout.layout, parent, false);
            }




                MarksData currentItem = items.get(position);
                TextView exam_name_list = (TextView) ItemView.findViewById(R.id.exam_name_list);
                TextView total_marks_sub = (TextView) ItemView.findViewById(R.id.total_marks);
                TextView obtained_marks_sub = (TextView) ItemView.findViewById(R.id.obtained_marks);
                TextView percentage_marks_sub = (TextView) ItemView.findViewById(R.id.percentage_marks);
                TextView date_marks_sub = (TextView) ItemView.findViewById(R.id.date_exam);

                date_marks_sub.setText(currentItem.getDate());
                exam_name_list.setText(currentItem.getExam_name());
                total_marks_sub.setText(currentItem.getTotal_max() + "");
                obtained_marks_sub.setText(currentItem.getObtained_max() + "");
                percentage_marks_sub.setText(currentItem.getPercentage() + "");


                return ItemView;

        }
    }
}
