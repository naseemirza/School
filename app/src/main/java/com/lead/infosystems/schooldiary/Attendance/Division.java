package com.lead.infosystems.schooldiary.Attendance;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
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
import com.lead.infosystems.schooldiary.Progress.Marks;
import com.lead.infosystems.schooldiary.Progress.SinlGraph;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Division extends AppCompatActivity {

    UserDataSP userDataSP;

    SPData spData;

    ListView dlist;
    String class_list;
    public static List<ClassPage> division= new ArrayList<ClassPage>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_division);


        Intent intent = getIntent();
        class_list = intent.getStringExtra("class");

        dlist=(ListView)findViewById(R.id.div_list);


        userDataSP=new UserDataSP(this);
        spData =new SPData(this);
        getDivisionData();

    }

    public void getDivisionData(){

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utils.ATTENDANCE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    getJsonData(response);


                } catch (JSONException e) {
                    e.printStackTrace();
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

                return params;
            }
        };
        RetryPolicy retryPolicy = new DefaultRetryPolicy(2000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        requestQueue.add(request);


    }





    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        division.clear();

        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            division.add(new ClassPage(jsonobj.getString("division")));

        }

        dlist.setAdapter(new MyAdaptor());
        dlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                Intent intent = new Intent(view.getContext(), Student_list.class);
                intent.putExtra("division", division.get(position).getClassName());
                intent.putExtra("class", class_list);
                startActivity(intent);

            }
        });


    }
    class MyAdaptor extends ArrayAdapter<ClassPage> {

        public MyAdaptor() {
            super(getApplicationContext(), R.layout.class_div,division);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ItemView = convertView;
            if (ItemView == null) {
                ItemView = getLayoutInflater().inflate(R.layout.class_div, parent, false);
            }

            ClassPage currentItem=division.get(position);
            TextView class_text=(TextView)ItemView.findViewById(R.id.class_id) ;
            class_text.setText("Division"+"  "+currentItem.getClassName());





            return ItemView;

        }
    }
}
