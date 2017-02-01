package com.lead.infosystems.schooldiary.Progress;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.MyDataBase;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static com.lead.infosystems.schooldiary.Progress.Marks.items;


/**
 * A simple {@link Fragment} subclass.
 */
public class Progress_Report extends Fragment {
    private UserDataSP userDataSP;
    private MyDataBase myDataBase;
    private ListAdapter object;
    private ListView list;
    private View rootView;
    private String examData;

    private ProgressBar progressBar;
    private TextView notAvailable;
    public Button btn1;
    ArrayList<String> subjects = new ArrayList<>();

    public Progress_Report() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_progress__report, container, false);
        btn1=(Button)rootView.findViewById(R.id.button_prog);
        userDataSP=new UserDataSP(getActivity().getApplicationContext());
        myDataBase = new MyDataBase(getActivity().getApplicationContext());
        getActivity().setTitle("Progress Report");
        progressBar = (ProgressBar)rootView.findViewById(R.id.report_progress);
        notAvailable = (TextView)rootView.findViewById(R.id.reportnot_available);
        subjects.clear();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (examData == null){
                    Toast.makeText(getActivity(),"There is no data for Showing Graph..",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent it = new Intent(getActivity().getApplicationContext(), GraphView.class);
                    startActivity(it);
                }
            }
        });
        checkInternetConnection();
        return rootView;
    }


    public void checkInternetConnection()
    {
        if(ServerConnect.checkInternetConenction(getActivity()))
        {
            progressBar.setVisibility(View.VISIBLE);
            subjects.clear();
            getDataFromServer();
        }
        else
        {
           putIntoList();
        }
    }
    private void getDataFromServer(){
        MyVolley volley = new MyVolley(getActivity().getApplicationContext(), new IVolleyResponse() {
            @Override
            public void volleyResponse(String result) {
                if(result != MyVolley.RESPONSE_ERROR){
                    userDataSP.storeMarksData(result);
                    try {
                        getJsonData(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        notAvailable.setVisibility(View.VISIBLE);
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
        volley.setUrl(Utils.MARKS);
        volley.setParams(UserDataSP.NUMBER_USER,userDataSP.getUserData(UserDataSP.NUMBER_USER));
        volley.connect();
    }

    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        myDataBase.clearSubjectData();
        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            myDataBase.insertSubjectData(jsonobj.getString("sub_name"));
          examData = jsonobj.getString("sub_data");
        }
        putIntoList();

    }

//    private void getJsonData(String re) throws JSONException {
//        JSONArray json = new JSONArray(re);
//        ArrayList<String> subNames = new ArrayList<>();
//        myDataBase.clearSubjectData();
//        for (int i = 0; i <= json.length() - 1; i++) {
//            JSONObject jsonobj = json.getJSONObject(i);
//            JSONArray examNames = new JSONArray(jsonobj.getString("exam_data"));
//            for(int j = 0; j <= examNames.length() - 1; j++){
//                subNames.add(examNames.getJSONObject(j).getString("sub_name"));
//            }
//            examData = jsonobj.getString("exam_data");
//        }
//        HashSet<String> uniqueValues = new HashSet<>(subNames);
//        subNames.clear();
//        subNames = new ArrayList<>(uniqueValues);
//        for(int k = 0; k<= subNames.size() - 1 ; k++) {
//            myDataBase.insertSubjectData(subNames.get(k));
//        }
//        putIntoList();
//
//    }

    private void activateButton(){
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (examData == null){
                    Toast.makeText(getActivity(),"There is no data for Showing Graph..",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent it = new Intent(getActivity().getApplicationContext(), GraphView.class);
                    startActivity(it);
                }
            }
        });
    }
    public void putIntoList()
    {
        Cursor data = myDataBase.getSubjectData();
        if(data.getCount()>0)
        {
            subjects.clear();
            while (data.moveToNext())
            {
                subjects.add(data.getString(1));
            }
            activateButton();
        }
        else{
            btn1.setVisibility(View.GONE);
            notAvailable.setVisibility(View.VISIBLE);
        }
        object = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, subjects);
        list = (ListView)rootView.findViewById(R.id.list);
        list.setAdapter(object);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (examData == null){
                    Toast.makeText(getActivity(),"There is no data in this Subject...",Toast.LENGTH_SHORT).show();

                }else {
                    Intent intent = new Intent(view.getContext(), Marks.class);
                    intent.putExtra("sub_name", subjects.get(position));
                    startActivity(intent);
                }

            }
        });
    }
}
