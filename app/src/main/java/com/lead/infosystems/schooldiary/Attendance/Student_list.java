package com.lead.infosystems.schooldiary.Attendance;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Student_list extends AppCompatActivity {


   // private boolean flagA=false;
   // private boolean flagL=false;
    ListView list;
    List<String> students;
    List<String> snumber;
    List<String> roll_number;

    //RadioButton absent,leave;

    SPData spdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        spdata = new SPData(getApplicationContext());
        try {
            getJsonData(spdata.getData(SPData.STU));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        students = new ArrayList<String>();
        roll_number = new ArrayList<String>();


        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            students.add(jsonobj.getString("first_name") + " " + jsonobj.getString("last_name"));
            roll_number.add(jsonobj.getString("roll_number"));
            snumber.add(jsonobj.getString("student_number"));

            Log.e("students", String.valueOf(students));

        }

        CustomList adapter = new CustomList(Student_list.this, students, roll_number, snumber);
        list = (ListView) findViewById(R.id.list);

        list.setAdapter(adapter);

    }
}
