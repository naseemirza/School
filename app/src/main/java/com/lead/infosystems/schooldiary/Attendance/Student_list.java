package com.lead.infosystems.schooldiary.Attendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.sromku.simple.fb.entities.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student_list extends AppCompatActivity {
    private String class_list;
    private String division_list;
    private Button submit;
    private ListView list;
    private UserDataSP userDataSP;
    private TextView noSubs;
    private JSONArray jsonArray=new JSONArray();
    private List<Datalist> items = new ArrayList<>();
    private ProgressBar progressBar;
    private MyVolley volley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        setTitle("Attendance");
        Intent intent = getIntent();
        userDataSP=new UserDataSP(this);
        class_list = intent.getStringExtra("class");
        division_list = intent.getStringExtra("division");
        list = (ListView) findViewById(R.id.list);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        noSubs = (TextView) findViewById(R.id.no_students);
        getStudentData();
        submit=(Button)findViewById(R.id.button_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        for(int i = 0 ; i< CustomList.items.size();i++){
                            try {
                                JSONObject jsonObject=new JSONObject();
                                jsonObject.put(UserDataSP.NUMBER_USER,CustomList.items.get(i).getStudent_number());
                                jsonObject.put("attendance",CustomList.items.get(i).getAttendance());
                                jsonArray.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                sendAttendanceData();
            }
        });
    }

    public void getStudentData(){
        progressBar.setVisibility(View.VISIBLE);
        volley = new MyVolley(getApplicationContext(), new IVolleyResponse() {
            @Override
            public void volleyResponse(String result) {
                try {
                    getJsonData(result);
                    noSubs.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    noSubs.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
        volley.setUrl(Utils.ATTENDANCE);
        volley.setParams(UserDataSP.SCHOOL_NUMBER,userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
        volley.setParams(UserDataSP.CLASS,class_list);
        volley.setParams(UserDataSP.DIVISION,division_list);
        volley.connect();
    }


    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            items.add(new Datalist(jsonobj.getString("first_name") + " " + jsonobj.getString("last_name")
                    ,jsonobj.getString("roll_number"),jsonobj.getString("number_user"),"p"));
        }
        CustomList adapter = new CustomList(Student_list.this,items);
        list.setAdapter(adapter);

    }

    public void sendAttendanceData(){

        volley = new MyVolley(getApplicationContext(), new IVolleyResponse() {
            @Override
            public void volleyResponse(String result) {
                if(result.contains("DONE")) {
                    Toast.makeText(Student_list.this, "Successfully Submitted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Student_list.this, "Attendance is not submitted properly", Toast.LENGTH_SHORT).show();
                }
            }
        });
        volley.setUrl(Utils.ATTENDANCE);
        volley.setParams(UserDataSP.SCHOOL_NUMBER,userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
        volley.setParams(UserDataSP.CLASS,class_list);
        volley.setParams(UserDataSP.DIVISION,division_list);
        volley.setParams("jsonString", jsonArray.toString());
        volley.connect();
    }
}
