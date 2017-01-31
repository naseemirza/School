package com.lead.infosystems.schooldiary.Attendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.Generic.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student_list extends AppCompatActivity {
    String class_list;
    String division_list;
    Button submit;
    ListView list;
    UserDataSP userDataSP;
    JSONArray jsonArray=new JSONArray();
    List<Datalist> items = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        setTitle("Attendance");
        Intent intent = getIntent();
        userDataSP=new UserDataSP(this);
        class_list = intent.getStringExtra("class");
        division_list = intent.getStringExtra("division");
        getStudentData();

        submit=(Button)findViewById(R.id.button_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        for(int i = 0 ; i< CustomList.items.size();i++){

                            try {
                                JSONObject jsonObject=new JSONObject();
                                jsonObject.put("number_user",CustomList.items.get(i).getStudent_number());
                                jsonObject.put("attendance",CustomList.items.get(i).getAttendance());
                                jsonArray.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                Log.e("attendance",jsonArray.toString());
                sendAttendanceData();


            }
        });
    }

    public void getStudentData(){

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utils.ATTENDANCE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response != null && !response.contentEquals("ERROR")){

                    try {
                        getJsonData(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();

                params.put("school_number",userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
                params.put("class",class_list);
                params.put("division",division_list);

                return params;
            }
        };
        RetryPolicy retryPolicy = new DefaultRetryPolicy(2000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        requestQueue.add(request);


    }
    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);



        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);

            items.add(new Datalist(jsonobj.getString("first_name") + " " + jsonobj.getString("last_name")
                    ,jsonobj.getString("roll_number"),jsonobj.getString("number_user"),"p"));

        }

        CustomList adapter = new CustomList(Student_list.this,items);
        list = (ListView) findViewById(R.id.list);

        list.setAdapter(adapter);

    }
    public void sendAttendanceData(){

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utils.ATTENDANCE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

               if(response.contains("DONE")) {
                   Toast.makeText(Student_list.this, "" + response, Toast.LENGTH_SHORT).show();
                   finish();
               }
                else
               {
                   Toast.makeText(Student_list.this, "Attendance is not submitted properly", Toast.LENGTH_SHORT).show();
               }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();

                params.put("school_number",userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
                params.put("class",class_list);
                params.put("division",division_list);
                params.put("jsonString", jsonArray.toString());

                return params;
            }
        };
        RetryPolicy retryPolicy = new DefaultRetryPolicy(2000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        requestQueue.add(request);
    }
}
