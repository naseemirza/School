package com.lead.infosystems.schooldiary.SchoolDiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.MyVolley;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StudentSubject_list extends AppCompatActivity implements IVolleyResponse{
    UserDataSP userDataSP;

    private MyVolley myVolley;
    ListView list_subject;
    String className;
    String divisionName;
    ArrayList<String> subjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_subject_list);

        Intent intent = getIntent();
        className = intent.getStringExtra("class");
        divisionName = intent.getStringExtra("division");
        list_subject=(ListView)findViewById(R.id.list_subject);
        userDataSP=new UserDataSP(this);
        myVolley = new MyVolley(getApplicationContext(), this);

        getSubjectData();

    }




    public void getSubjectData(){
        myVolley.setUrl(Utils.HOMEWORK_INSERT);
        myVolley.setParams(UserDataSP.SCHOOL_NUMBER, userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
        myVolley.setParams("class", className);
        myVolley.setParams("division", divisionName);
        myVolley.connect();

    }
    @Override
    public void volleyResponce(String result) {

        try {

            getJsonData(result);
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    private void getJsonData(String re) throws JSONException {
        Log.e("res", re);
        JSONArray json = new JSONArray(re);
        subjects.clear();

        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            subjects.add(jsonobj.getString("sub_name"));

        }

        list_subject.setAdapter(new MyAdaptor());
        list_subject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(view.getContext(), HomeworkList_teacher.class);
                intent.putExtra("class", className);
                intent.putExtra("division", divisionName);
                intent.putExtra("subject", subjects.get(position));
                startActivity(intent);
                Log.e("res", subjects.get(position).toString());

            }
        });


    }
    class MyAdaptor extends ArrayAdapter<String> {

        public MyAdaptor() {
            super(getApplicationContext(), R.layout.class_div,subjects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ItemView = convertView;
            if (ItemView == null) {
                ItemView = getLayoutInflater().inflate(R.layout.class_div, parent, false);
            }
            TextView class_text=(TextView)ItemView.findViewById(R.id.class_id) ;
            class_text.setText(subjects.get(position));

            return ItemView;

        }
    }



}
