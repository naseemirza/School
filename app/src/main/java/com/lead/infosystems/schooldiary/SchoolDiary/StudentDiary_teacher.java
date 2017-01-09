package com.lead.infosystems.schooldiary.SchoolDiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.Generic.ServerConnect;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StudentDiary_teacher extends Fragment implements IVolleyResponse {

    UserDataSP userDataSP;
    private MyVolley myVolley;
    ListView list_class;
    private ProgressBar progressBar;
    private TextView notAvailable;
    private TextView noInternet;
    ArrayList<String> classes = new ArrayList<String>();


    public StudentDiary_teacher() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_student_diary_teacher, container, false);
        getActivity().setTitle("HOME WORK");
        userDataSP=new UserDataSP(getActivity());
        myVolley = new MyVolley(getActivity().getApplicationContext(), this);

        list_class=(ListView)rootView.findViewById(R.id.class_list_home);
        progressBar = (ProgressBar)rootView.findViewById(R.id.homework_progress);
        notAvailable = (TextView)rootView.findViewById(R.id.homeworkNotAvailable);
        noInternet = (TextView)rootView.findViewById(R.id.homenoInternet);
        checkInternetConnection();
        return rootView;

    }

    public void checkInternetConnection()
    {
        if(ServerConnect.checkInternetConenction(getActivity()))
        {  progressBar.setVisibility(View.VISIBLE);
            getClassData();
        }
        else {
            noInternet.setVisibility(View.VISIBLE);
        }

    }
    public void getClassData(){
        myVolley.setUrl(Utils.HOMEWORK_INSERT);
        myVolley.setParams(UserDataSP.SCHOOL_NUMBER, userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
        myVolley.connect();
    }

    @Override
    public void volleyResponse(String result) {

        try {
            progressBar.setVisibility(View.GONE);
            noInternet.setVisibility(View.GONE);
            getJsonData(result);
        } catch (JSONException e) {
            e.printStackTrace();
            notAvailable.setVisibility(View.VISIBLE);
        }
    }

    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        classes.clear();

        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            classes.add(jsonobj.getString("class"));

        }

        list_class.setAdapter(new MyAdaptor());
        list_class.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Log.e("class", classes.get(position));
                Intent intent = new Intent(view.getContext(), StudentDivision.class);
                intent.putExtra("class", classes.get(position));
                startActivity(intent);

            }
        });


    }
    class MyAdaptor extends ArrayAdapter<String> {

        public MyAdaptor() {
            super(getActivity(), R.layout.class_div,classes);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ItemView = convertView;
            if (ItemView == null) {
                ItemView = getActivity().getLayoutInflater().inflate(R.layout.class_div, parent, false);
            }

            TextView class_text=(TextView)ItemView.findViewById(R.id.class_id) ;
            class_text.setText("class"+"  "+classes.get(position));





            return ItemView;

        }
    }







}
