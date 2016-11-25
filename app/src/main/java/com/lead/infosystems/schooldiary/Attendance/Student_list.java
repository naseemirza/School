package com.lead.infosystems.schooldiary.Attendance;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Student_list extends AppCompatActivity {


    private boolean flagA=false;
    private boolean flagL=false;
    ListView list;
    List<String> students;

    List<String> roll_number;

    RadioButton absent,leave;

    SPData spdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        spdata = new SPData(getApplicationContext());
        try {
            getJsonData(spdata.getData(SPData.STU));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        students = new ArrayList<String>();
        roll_number = new ArrayList<String>();



        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            students.add(jsonobj.getString("first_name") + " " + jsonobj.getString("last_name"));
            roll_number.add(jsonobj.getString("roll_number"));




            Log.e("students", String.valueOf(students));

        }


        list = (ListView) findViewById(R.id.list);

        list.setAdapter(new myadapter());

    }
    class myadapter extends ArrayAdapter {

        public myadapter() {
            super(getApplicationContext(), R.layout.list, students);
        }

        @NonNull


        @Override
        public View getView( int position, View convertView, ViewGroup parent) {

            View view=convertView;
            if(view==null){

                view= getLayoutInflater().inflate(R.layout.list,parent,false);



            }

            final String current_name= students.get(position);
            final String current_roll= roll_number.get(position);
             TextView textView= (TextView) view.findViewById(R.id.textView_name);
            TextView textView1=(TextView) view.findViewById(R.id.textView_roll);
            absent=(RadioButton) view.findViewById(R.id.radioA) ;
            leave=(RadioButton) view.findViewById(R.id.radioL) ;
            textView.setText(current_name);
            textView1.setText(current_roll);


            absent.setOnClickListener(new View.OnClickListener() {
                //boolean isChecked = true;
                @Override
                public void onClick(View v) {

                    if(absent.isChecked()) {
                        if (!flagA) {
                            absent.setChecked(true);
                            leave.setChecked(false);
                            flagA = true;
                            flagL = false;

                            String send= "A";

                        } else {
                            flagA = false;
                            absent.setChecked(false);
                            leave.setChecked(false);
                        }
                    }


                }
            });

            leave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (leave.isChecked()) {
                        if (!flagL) {
                            leave.setChecked(true);
                            absent.setChecked(false);
                            flagL = true;
                            flagA = false;

                            String send= "L";


                        } else {
                            flagL = false;
                            leave.setChecked(false);
                            absent.setChecked(false);
                        }
                    }
                }

            });
            return view;
        }
    }

}
