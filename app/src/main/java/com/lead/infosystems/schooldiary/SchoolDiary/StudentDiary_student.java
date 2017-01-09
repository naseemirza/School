package com.lead.infosystems.schooldiary.SchoolDiary;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.ramotion.foldingcell.FoldingCell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class StudentDiary_student extends Fragment implements IVolleyResponse {
    ListView list;
    private ArrayList<Item> items;
    private MyVolley myVolley;
    private UserDataSP userDataSp;
    private MyDataBase myDataBase;
    private ProgressBar progressBar;
    private TextView notAvailable;
    public StudentDiary_student() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_diary_student, container, false);
        userDataSp = new UserDataSP(getActivity());
        progressBar = (ProgressBar)view.findViewById(R.id.homework_progress);
        notAvailable = (TextView) view.findViewById(R.id.homeworknot_available);
        myVolley = new MyVolley(getActivity().getApplicationContext(), this);
        myDataBase = new MyDataBase(getActivity().getApplicationContext());
        getActivity().setTitle("HOME WORK");
        list = (ListView) view.findViewById(R.id.list_detail);
        getHomeWorkData();
        return view;

    }

    public void getHomeWorkData()
    {   progressBar.setVisibility(View.VISIBLE);
        myVolley.setUrl(Utils.HOMEWORK_FETCH);
        myVolley.setParams(UserDataSP.SCHOOL_NUMBER, userDataSp.getUserData(UserDataSP.SCHOOL_NUMBER));
        myVolley.setParams(UserDataSP.CLASS, userDataSp.getUserData(UserDataSP.CLASS));
        myVolley.setParams(UserDataSP.DIVISION, userDataSp.getUserData(UserDataSP.DIVISION));
        myVolley.connect();

    }

    @Override
    public void volleyResponse(String result) {

        try {
            notAvailable.setVisibility(View.GONE);
            Log.e("res....", result);
            getJsonData(result);
        } catch (JSONException e) {
            e.printStackTrace();
            notAvailable.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }


    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            myDataBase.insertHomeWorkData(jsonobj.getString("homework_title"), jsonobj.getString("homework_contents"), jsonobj.getString("lastDate_submission"), jsonobj.getString("subject"), jsonobj.getString("homeworkDate"), jsonobj.getString(UserDataSP.NUMBER_USER), jsonobj.getString("homework_number"));
            //items.add(new Item(jsonobj.getString("homework_title"), jsonobj.getString("homework_contents"), jsonobj.getString("lastDate_submission"), jsonobj.getString("subject"), jsonobj.getString("homeworkDate"), jsonobj.getString(UserDataSP.NUMBER_USER), jsonobj.getString("homework_number")));
        }
         putHomeWorkDataintoList();


    }
    public void putHomeWorkDataintoList()
    {
        Cursor data = myDataBase.getHomeWorkData();
        Log.e("cursor data", data.toString()+" ..");
        if(data.getCount()>0)
        {
            items = new ArrayList<>();
            while (data.moveToNext())
            {
                items.add(new Item(data.getString(1), data.getString(2), data.getString(3),
                        data.getString(4), data.getString(5), data.getString(6), data.getString(7)));
            }

        }
        else{
            Toast.makeText(getActivity().getApplicationContext(),"No Home Work Data",Toast.LENGTH_SHORT).show();
        }
        final FoldingCellListAdapter adapter = new FoldingCellListAdapter(getActivity(), items);
        adapter.sortData();
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
