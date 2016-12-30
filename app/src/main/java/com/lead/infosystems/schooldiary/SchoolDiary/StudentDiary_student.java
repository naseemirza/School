package com.lead.infosystems.schooldiary.SchoolDiary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.MyVolley;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;
import com.ramotion.foldingcell.FoldingCell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class StudentDiary_student extends Fragment implements IVolleyResponse {
    ListView list;
    private ArrayList<Item> items;
    private MyVolley myVolley;
    UserDataSP userDataSp;
    public StudentDiary_student() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_diary_student, container, false);
        userDataSp = new UserDataSP(getActivity());
        myVolley = new MyVolley(getActivity().getApplicationContext(), this);
        getActivity().setTitle("HOME WORK");
        list = (ListView) view.findViewById(R.id.list_detail);
        getHomeWorkData();
        return view;

    }

    public void getHomeWorkData()
    {
        myVolley.setUrl(Utils.HOMEWORK_FETCH);
        myVolley.setParams(UserDataSP.SCHOOL_NUMBER, userDataSp.getUserData(UserDataSP.SCHOOL_NUMBER));
        myVolley.setParams(UserDataSP.CLASS, userDataSp.getUserData(UserDataSP.CLASS));
        myVolley.setParams(UserDataSP.DIVISION, userDataSp.getUserData(UserDataSP.DIVISION));
        myVolley.connect();
        Log.e("detail", userDataSp.getUserData(UserDataSP.SCHOOL_NUMBER));
        Log.e("detail 2", userDataSp.getUserData(UserDataSP.CLASS));
        Log.e("detail 3", userDataSp.getUserData(UserDataSP.DIVISION));

    }

    @Override
    public void volleyResponce(String result) {

        try {
            Log.e("res....", result);
            getJsonData(result);
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }


    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        items = new ArrayList<>();

        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            items.add(new Item(jsonobj.getString("homework_title"), jsonobj.getString("homework_contents"), jsonobj.getString("lastDate_submission"), jsonobj.getString("subject"), jsonobj.getString("homeworkDate")));

        }
        items.get(0).setRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "CUSTOM HANDLER FOR FIRST BUTTON", Toast.LENGTH_SHORT).show();

            }
        });


        final FoldingCellListAdapter adapter = new FoldingCellListAdapter(getActivity(), items);
        adapter.setDefaultRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "DEFAULT HANDLER FOR ALL BUTTONS", Toast.LENGTH_SHORT).show();
            }
        });

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // toggle clicked cell state
                ((FoldingCell) view).toggle(false);
                // register in adapter that state for selected cell is toggled
                adapter.registerToggle(pos);
            }
        });


    }

}
