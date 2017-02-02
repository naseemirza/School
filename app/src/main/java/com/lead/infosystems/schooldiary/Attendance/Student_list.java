package com.lead.infosystems.schooldiary.Attendance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import java.util.Collections;
import java.util.Comparator;
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
    private Activity activity = this;
    private ProgressDialog progressDialog;

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
        submit = (Button)findViewById(R.id.button_submit);
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



                android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
                alert.setTitle("Alert");
                alert.setMessage("Confirm Attendance submission");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendAttendanceData();
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
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
                    submit.setVisibility(View.VISIBLE);
                    noSubs.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    submit.setVisibility(View.GONE);
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
            items.add(new Datalist(jsonobj.getString(UserDataSP.FIRST_NAME) + " " + jsonobj.getString(UserDataSP.LAST_NAME)
                    ,jsonobj.getString(UserDataSP.ROLL_NO),jsonobj.getString(UserDataSP.NUMBER_USER),CustomList.PRESENT));
        }
        Collections.sort(items,new MyComparator());
        CustomList adapter = new CustomList(Student_list.this,items);
        list.setAdapter(adapter);

    }

    public void sendAttendanceData(){
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        volley = new MyVolley(getApplicationContext(), new IVolleyResponse() {
            @Override
            public void volleyResponse(String result) {
                if(result.contains("DONE")) {
                    Toast.makeText(Student_list.this, "Successfully Submitted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(Student_list.this, "Attendance is not submitted properly", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
        volley.setUrl(Utils.ATTENDANCE);
        volley.setParams(UserDataSP.SCHOOL_NUMBER,userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
        volley.setParams(UserDataSP.CLASS,class_list);
        volley.setParams(UserDataSP.DIVISION,division_list);
        volley.setParams("jsonString", jsonArray.toString());
        volley.connect();
    }

    private class MyComparator implements Comparator<Datalist>{

        @Override
        public int compare(Datalist lhs, Datalist rhs) {
            return Integer.parseInt(lhs.getStudent_roll()) - Integer.parseInt(rhs.getStudent_roll());
        }
    }
}
