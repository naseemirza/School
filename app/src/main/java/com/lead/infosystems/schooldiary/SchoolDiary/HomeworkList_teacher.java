package com.lead.infosystems.schooldiary.SchoolDiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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


public class HomeworkList_teacher extends AppCompatActivity implements IVolleyResponse{
    private FloatingActionButton button;
    String class_list;
    String division_list;
    String subject_list;
    ListView list;
    private ArrayList<Item> items_homework;
    private MyVolley myVolley;
    UserDataSP userDataSp;
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
        class_list = intent.getStringExtra(UserDataSP.CLASS);
        division_list = intent.getStringExtra(UserDataSP.DIVISION);
        subject_list = intent.getStringExtra("subject");
        setTitle("HOME WORK");
        list = (ListView)findViewById(R.id.homeworkList);
        button = (FloatingActionButton)findViewById(R.id.addHomeWork);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), StudentDiary_DatePicker.class);
                intent.putExtra("class", class_list);
                intent.putExtra("division", division_list);
                intent.putExtra("subject", subject_list);
                startActivity(intent);
            }
        });
        getHomeWorkList();
    }


    public void getHomeWorkList()
    {
        progressBar.setVisibility(View.VISIBLE);
        myVolley.setUrl(Utils.HOMEWORK_FETCH);
        myVolley.setParams(UserDataSP.SCHOOL_NUMBER, userDataSp.getUserData(UserDataSP.SCHOOL_NUMBER));
        myVolley.setParams(UserDataSP.CLASS, class_list);
        myVolley.setParams(UserDataSP.DIVISION, division_list);
        myVolley.setParams("subject_name", subject_list);
        myVolley.connect();

    }
    @Override
    public void volleyResponce(String result) {
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
            items_homework.add(new Item(jsonobj.getString("homework_title"), jsonobj.getString("homework_contents"), jsonobj.getString("lastDate_submission"), jsonobj.getString("subject"), jsonobj.getString("homeworkDate"), jsonobj.getString(UserDataSP.NUMBER_USER)));

        }
        items_homework.get(0).setRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeworkList_teacher.this, "CUSTOM HANDLER FOR FIRST BUTTON", Toast.LENGTH_SHORT).show();

            }
        });


        final FoldingCellListAdapter adapter = new FoldingCellListAdapter(HomeworkList_teacher.this, items_homework);

        adapter.setDefaultRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeworkList_teacher.this, "DEFAULT HANDLER FOR ALL BUTTONS", Toast.LENGTH_SHORT).show();
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
