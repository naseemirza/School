package com.lead.infosystems.schooldiary.SchoolDiary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class StudentDivision extends AppCompatActivity implements IVolleyResponse{

    UserDataSP userDataSP;
    private MyVolley myVolley;
    ListView list_div;
    String className;
    ArrayList<String> division = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_division);


        Intent intent = getIntent();
        className = intent.getStringExtra("class");

        list_div=(ListView)findViewById(R.id.list_div);


        userDataSP=new UserDataSP(this);
        myVolley = new MyVolley(getApplicationContext(), this);
        getDivisionData();

    }

    public void getDivisionData(){
        myVolley.setUrl(Utils.HOMEWORK_INSERT);
        myVolley.setParams(UserDataSP.SCHOOL_NUMBER, userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
        myVolley.setParams("class", className);
        myVolley.connect();

    }
    @Override
    public void volleyResponse(String result) {

        try {

            getJsonData(result);
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }




    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        division.clear();

        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            division.add(jsonobj.getString("division"));

        }

        list_div.setAdapter(new MyAdaptor());
        list_div.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


                Intent intent = new Intent(view.getContext(), StudentSubject_list.class);
                intent.putExtra("division", division.get(position));
                intent.putExtra("class", className);
                startActivity(intent);

            }
        });


    }
    class MyAdaptor extends ArrayAdapter<String> {

        public MyAdaptor() {
            super(getApplicationContext(), R.layout.class_div,division);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ItemView = convertView;
            if (ItemView == null) {
                ItemView = getLayoutInflater().inflate(R.layout.class_div, parent, false);
            }

           // ClassPage currentItem=division.get(position);
            TextView class_text=(TextView)ItemView.findViewById(R.id.class_id) ;
            class_text.setText("Division");

            String firstletter = String.valueOf(division.get(position).charAt(0));
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(getItem(position));
            TextDrawable drawable = TextDrawable.builder().buildRoundRect(firstletter.toUpperCase(),color,20);
            ((ImageView) ItemView.findViewById(R.id.class_image)).setImageDrawable(drawable);

            return ItemView;

        }
    }



}
