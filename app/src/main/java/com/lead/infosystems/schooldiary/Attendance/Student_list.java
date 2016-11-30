package com.lead.infosystems.schooldiary.Attendance;

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
import com.lead.infosystems.schooldiary.Main.FragTabHome;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student_list extends AppCompatActivity {

    Button submit;
    ListView list;
    JSONArray jsonArray=new JSONArray();

    List<Datalist> items = new ArrayList<>();

    SPData spdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);



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


            }
        });




        spdata = new SPData(getApplicationContext());
        try {
            getJsonData(spdata.getData(SPData.STU));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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


    public void makeRequest(final String jsonData){
        RequestQueue requestQueue = Volley.newRequestQueue(Student_list.this);

        StringRequest request = new StringRequest(Request.Method.POST, Utils.NEW_POST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String> map =  new HashMap<>();

                return null;
            }
        };
        int socketTimeout = 20000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);

    }
}
