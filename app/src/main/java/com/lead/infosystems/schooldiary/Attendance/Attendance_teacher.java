package com.lead.infosystems.schooldiary.Attendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Attendance_teacher extends Fragment {



    UserDataSP userDataSP;

    SPData spData;
    ListView clist;
    public static List<ClassPage> classes = new ArrayList<ClassPage>();



    public Attendance_teacher() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_teacher__attendance, container, false);
        getActivity().setTitle("Attendance");
        userDataSP=new UserDataSP(getActivity());
        spData =new SPData(getActivity());
        clist=(ListView)rootView.findViewById(R.id.class_list);
        getClassData();

        return rootView;
    }

    public void getClassData(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
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

                return params;
            }
        };
        RetryPolicy retryPolicy = new DefaultRetryPolicy(2000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        requestQueue.add(request);


    }

    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        classes.clear();

        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            classes.add(new ClassPage(jsonobj.getString("class")));

        }

        clist.setAdapter(new MyAdaptor());
        clist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                Intent intent = new Intent(view.getContext(), Division.class);
                intent.putExtra("class", classes.get(position).getClassName());
                startActivity(intent);

            }
        });


    }
    class MyAdaptor extends ArrayAdapter<ClassPage> {

        public MyAdaptor() {
            super(getActivity().getApplicationContext(), R.layout.class_div,classes);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ItemView = convertView;
            if (ItemView == null) {
                ItemView = getActivity().getLayoutInflater().inflate(R.layout.class_div, parent, false);
            }

            ClassPage currentItem=classes.get(position);
            TextView class_text=(TextView)ItemView.findViewById(R.id.class_id) ;
            class_text.setText("class"+"  "+currentItem.getClassName());





            return ItemView;

        }
    }






}
