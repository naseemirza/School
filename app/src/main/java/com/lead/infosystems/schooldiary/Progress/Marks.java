package com.lead.infosystems.schooldiary.Progress;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.ServerConnect;
import com.lead.infosystems.schooldiary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Marks extends AppCompatActivity {

    public Button btn;
    private MyDataBase myDataBase;
    private UserDataSP userDataSP;
    private ProgressBar progressBar;
    private TextView notAvailable;
    private MyAdaptor adaptor;
    ListView marks;
    String subName;
    public static List<MarksData> items = new ArrayList<MarksData>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marks);
        myDataBase = new MyDataBase(this);
        Intent intent = getIntent();
        subName = intent.getStringExtra("sub_name");
        userDataSP = new UserDataSP(getApplicationContext());
        getSupportActionBar().setTitle(subName);
        marks =(ListView)findViewById(R.id.marks);
        progressBar= (ProgressBar)findViewById(R.id.marks_progress);
        notAvailable = (TextView)findViewById(R.id.marksnot_available);
        checkInternetConnection();
        items.clear();
        adaptor = new MyAdaptor();
        marks.setAdapter(adaptor);
        init();
    }


    public void init(){
        btn=(Button)findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SinlGraph.class);
                intent.putExtra("sub_name", subName);
                startActivity(intent);
            }
        });

    }

    private void getJsonExam(String data) {
        try {
            myDataBase.clearMarksData();
            JSONArray json_data = new JSONArray(data);
            for(int j = 0 ; j<json_data.length(); j++) {

                JSONObject job_data = json_data.getJSONObject(j);
                String sub_name = job_data.getString("sub_name");
                if(subName.contentEquals(sub_name)) {
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
                            items.add(new MarksData(date, exam_name, total+"", marks+"", percentage+""));
                        }
                        putMarksDataList();
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    public void checkInternetConnection()
    {
        if(ServerConnect.checkInternetConenction(this))
        {
            progressBar.setVisibility(View.VISIBLE);
            getJsonExam(userDataSP.getUserData(UserDataSP.SUBJECTS));
        }
        else {
            putMarksDataList();
        }

    }
    public  void putMarksDataList()
    {
        Cursor data = myDataBase.getMarksData();
        items.clear();
        if(data.getCount()>0)
        {
            while (data.moveToNext())
            {
                items.add(new MarksData(data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getString(5)));
            }
        }
        else
        {
            notAvailable.setVisibility(View.VISIBLE);
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
                TextView examName = (TextView) ItemView.findViewById(R.id.exam_name);
                TextView totalMarks = (TextView) ItemView.findViewById(R.id.total_marks);
                TextView obtMarks = (TextView) ItemView.findViewById(R.id.obtained_marks);
                TextView percentage = (TextView) ItemView.findViewById(R.id.percentage_marks);
                TextView examDate = (TextView) ItemView.findViewById(R.id.date_exam);
                examDate.setText(currentItem.getDate());
                examName.setText(currentItem.getExam_name());
                totalMarks.setText(currentItem.getTotal_max());
                obtMarks.setText(currentItem.getObtained_max());
                percentage.setText(currentItem.getPercentage());
                return ItemView;

        }
    }
}
