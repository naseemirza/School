package com.lead.infosystems.schooldiary.SchoolDiary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import static com.lead.infosystems.schooldiary.SchoolDiary.StudentDiary_DatePicker.HOMEWORKDATE;
import static com.lead.infosystems.schooldiary.SchoolDiary.StudentDiary_DatePicker.HOMEWORK_CONTENTS;
import static com.lead.infosystems.schooldiary.SchoolDiary.StudentDiary_DatePicker.HOMEWORK_NUMBER;
import static com.lead.infosystems.schooldiary.SchoolDiary.StudentDiary_DatePicker.HOMEWORK_TITLE;
import static com.lead.infosystems.schooldiary.SchoolDiary.StudentDiary_DatePicker.INTENTFILTER;
import static com.lead.infosystems.schooldiary.SchoolDiary.StudentDiary_DatePicker.LASTDATE_SUBMISSION;
import static com.lead.infosystems.schooldiary.SchoolDiary.StudentDiary_DatePicker.NUMBER_USER;
import static com.lead.infosystems.schooldiary.SchoolDiary.StudentDiary_DatePicker.SUBJECT;


public class HomeworkList_teacher extends AppCompatActivity implements IVolleyResponse{

    private FloatingActionButton button;
    private FoldingCellListAdapter adapter;
    private String className;
    private String divisionName;
    private String subjectName;
    private ListView list;
    private ArrayList<Item> items_homework;
    private MyVolley myVolley;
    private UserDataSP userDataSp;
    private ProgressBar progressBar;
    private TextView notAvailable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_list_teacher);
        userDataSp = new UserDataSP(this);
        progressBar = (ProgressBar)findViewById(R.id.homeworkProgress);
        notAvailable = (TextView)findViewById(R.id.homeworkNotAvailable);
        myVolley = new MyVolley(getApplicationContext(),this);
        Intent intent = getIntent();
        className = intent.getStringExtra(UserDataSP.CLASS);
        divisionName = intent.getStringExtra(UserDataSP.DIVISION);
        subjectName = intent.getStringExtra("subject");
        setTitle("HOME WORK");
        list = (ListView)findViewById(R.id.homeworkList);
        button = (FloatingActionButton)findViewById(R.id.addHomeWork);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StudentDiary_DatePicker.class);
                intent.putExtra("class", className);
                intent.putExtra("division", divisionName);
                intent.putExtra("subject", subjectName);
                startActivity(intent);
            }
        });

        getHomeWorkList();
    }

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter(INTENTFILTER));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           adapter.addItemNew(new Item(intent.getStringExtra(HOMEWORK_TITLE),intent.getStringExtra(HOMEWORK_CONTENTS)
                   ,intent.getStringExtra(LASTDATE_SUBMISSION),intent.getStringExtra(SUBJECT),
                   intent.getStringExtra(HOMEWORKDATE), intent.getStringExtra(NUMBER_USER),
                   intent.getStringExtra(HOMEWORK_NUMBER)));
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }



    public void getHomeWorkList()
    {
        progressBar.setVisibility(View.VISIBLE);
        myVolley.setUrl(Utils.HOMEWORK_FETCH);
        myVolley.setParams(UserDataSP.SCHOOL_NUMBER, userDataSp.getUserData(UserDataSP.SCHOOL_NUMBER));
        myVolley.setParams(UserDataSP.CLASS, className);
        myVolley.setParams(UserDataSP.DIVISION, divisionName);
        myVolley.setParams("subject_name", subjectName);
        myVolley.connect();

    }
    @Override
    public void volleyResponse(String result) {
        try {
            notAvailable.setVisibility(View.GONE);
            Log.e("res....", result);
            getJsonHome(result);
        } catch (JSONException e) {
            e.printStackTrace();
            notAvailable.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void getJsonHome(String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        Log.e("json ", re);
          items_homework = new ArrayList<>();
        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            items_homework.add(new Item(jsonobj.getString("homework_title"), jsonobj.getString("homework_contents"), jsonobj.getString("lastDate_submission"), jsonobj.getString("subject"), jsonobj.getString("homeworkDate"), jsonobj.getString(UserDataSP.NUMBER_USER), jsonobj.getString("homework_number")));

        }

        adapter = new FoldingCellListAdapter(HomeworkList_teacher.this, items_homework);
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
