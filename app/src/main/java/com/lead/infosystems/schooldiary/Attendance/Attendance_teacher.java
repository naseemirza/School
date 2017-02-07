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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
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
import java.util.List;

public class Attendance_teacher extends Fragment {
    private UserDataSP userDataSP;
    private TextView noInternet;
    private ProgressBar progressBar;
    private TextView notAvailable;
    private ListView clist;
    public static List<String> classes = new ArrayList<>();



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
        clist=(ListView)rootView.findViewById(R.id.class_list);
        noInternet = (TextView)rootView.findViewById(R.id.noInternet);
        progressBar = (ProgressBar)rootView.findViewById(R.id.attendance_progress);
        notAvailable = (TextView)rootView.findViewById(R.id.attendanceNotAvailable);
        checkInternetConnection();
        return rootView;
    }



    public  void checkInternetConnection()
    {
        if(ServerConnect.checkInternetConenction(getActivity()))
        {  progressBar.setVisibility(View.VISIBLE);
            getClassData();
        }
        else
        {
            noInternet.setVisibility(View.VISIBLE);
        }
    }
    public void getClassData(){
        MyVolley volley = new MyVolley(getActivity().getApplicationContext(), new IVolleyResponse() {
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
            super(getActivity().getApplicationContext(), R.layout.class_div,classes);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.class_div, parent, false);
            }

            String className=classes.get(position);
            ImageView img = (ImageView) itemView.findViewById(R.id.class_image);
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(getItem(position));
            TextDrawable drawable = TextDrawable.builder().buildRoundRect(className.toUpperCase(),color,20);
            img.setImageDrawable(drawable);
            return itemView;

        }
    }
}
