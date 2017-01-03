package com.lead.infosystems.schooldiary.Attendance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
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
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.Generic.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Teacher_Attendance extends AppCompatActivity {

    private UserDataSP userDataSP;
    private ListView clist;

    public static List<String> classes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher__attendance);
        userDataSP=new UserDataSP(this);
        clist=(ListView)findViewById(R.id.class_list);
        getClassData();
    }

    public void getClassData(){

        MyVolley volley = new MyVolley(getApplicationContext(), new IVolleyResponse() {
            @Override
            public void volleyResponse(String result) {
                try {
                    getJsonData(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        volley.setUrl(Utils.ATTENDANCE);
        volley.setParams(UserDataSP.SCHOOL_NUMBER,userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
        volley.connect();
    }

    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        classes.clear();

        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            classes.add(jsonobj.getString(UserDataSP.CLASS));
        }

        clist.setAdapter(new MyAdaptor());
        clist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    Intent intent = new Intent(view.getContext(), Division.class);
                    intent.putExtra(UserDataSP.CLASS, classes.get(position));
                    startActivity(intent);
            }
        });


    }
    class MyAdaptor extends ArrayAdapter<String> {

        public MyAdaptor() {
            super(getApplicationContext(), R.layout.class_div,classes);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ItemView = convertView;
            if (ItemView == null) {
                ItemView = getLayoutInflater().inflate(R.layout.class_div, parent, false);
            }

            String currentItem=classes.get(position);
            TextView class_text=(TextView)ItemView.findViewById(R.id.class_id) ;
            class_text.setText("class"+"  "+currentItem);
            return ItemView;

        }
    }
}
