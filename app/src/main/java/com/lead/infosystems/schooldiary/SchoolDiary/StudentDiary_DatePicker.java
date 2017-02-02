package com.lead.infosystems.schooldiary.SchoolDiary;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class StudentDiary_DatePicker extends AppCompatActivity implements IVolleyResponse{
    public static final String INTENTFILTER ="intent_filter_h";
    public static final String HOMEWORK_NUMBER ="homework_number";
    public static final String HOMEWORK_TITLE= "homework_title";
    public static final String HOMEWORK_CONTENTS = "homework_contents";
    public static final String LASTDATE_SUBMISSION= "lastDate_submission";
    public static final String SUBJECT = "subject";
    public static final String HOMEWORKDATE = "homeworkDate";
    public static final String NUMBER_USER = "number_user";

    private String className;
    private String divisionName;
    private String subjectName;
    private UserDataSP userDataSp;
    private int year_h;
    private int month_h;
    private int day_h;
    private static final int DIALOG_ID = 0;
    private Button submitB;
    private ImageView btn;
    private EditText editTitle, editContent;
    private MyVolley myVolley;
    private TextView date_display;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_diary__date_picker);
        userDataSp = new UserDataSP(this);
        Intent intent = getIntent();
        className = intent.getStringExtra("class");
        divisionName = intent.getStringExtra("division");
        subjectName = intent.getStringExtra("subject");
        editTitle = (EditText)findViewById(R.id.title_home);
        editContent = (EditText)findViewById(R.id.content_home);
        date_display = (TextView) findViewById(R.id.date_display);
        submitB = (Button)findViewById(R.id.submit_home);
        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitHomeWork();
            }
        });
        final Calendar cal = Calendar.getInstance();
        year_h = cal.get(Calendar.YEAR);
        month_h = cal.get(Calendar.MONTH)+1;
        day_h = cal.get(Calendar.DAY_OF_MONTH);
        date_display.setText(day_h+"/"+month_h+"/"+year_h);
        myVolley = new MyVolley(getApplicationContext(), this);
        showDialoOnButtonClick();

    }

    public void showDialoOnButtonClick()
    {
        btn = (ImageView)findViewById(R.id.button_date);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             showDialog(DIALOG_ID);
            }
        });
    }


    @Override
    protected Dialog onCreateDialog(int id)
    {
        if(id==DIALOG_ID)
        return new DatePickerDialog(this, dpickerListener, year_h, month_h-1, day_h);
        return null;
    }
    private DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener()
    {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_h = year;
            month_h = monthOfYear+1;
            day_h = dayOfMonth;
            date_display.setText(day_h+"/"+month_h+"/"+year_h);
        }
    };

    public void submitHomeWork() {
        if(editTitle.getText().toString().isEmpty()) {
            Toast.makeText(this, "fill the entries", Toast.LENGTH_SHORT).show();
                }
        else if (editContent.getText().toString().isEmpty()) {
            Toast.makeText(this, "fill the entries", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            myVolley.setUrl(Utils.HOMEWORK_INSERT);
            myVolley.setParams(UserDataSP.SCHOOL_NUMBER, userDataSp.getUserData(UserDataSP.SCHOOL_NUMBER));
            myVolley.setParams("class", className);
            myVolley.setParams("division", divisionName);
            myVolley.setParams("subject_name", subjectName);
            myVolley.setParams("lastDate_submission", date_display.getText().toString());
            myVolley.setParams("homework_title", editTitle.getText().toString());
            myVolley.setParams("homework_content", editContent.getText().toString());
            myVolley.setParams(UserDataSP.NUMBER_USER, userDataSp.getUserData(UserDataSP.NUMBER_USER));
            myVolley.connect();
    }
}
    @Override
    public void volleyResponse(String result) {
        if(result != null){
            if(!result.contains("ERROR")){
                Toast.makeText(this, "Submited", Toast.LENGTH_SHORT).show();
                try {
                    parseData(result, editTitle.getText().toString(), editContent.getText().toString(),
                            subjectName, date_display.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
            }
        }
    progressDialog.dismiss();
    }
    public void parseData(String re,  String homeTitle, String homeContent,  String subject, String lastDate ) throws JSONException {
        JSONArray json = new JSONArray(re);
        String homeNumber = null;
        String homeDate = null;

        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            homeNumber = jsonobj.getString(HOMEWORK_NUMBER);
            homeDate = jsonobj.getString(HOMEWORKDATE);
        }
        Intent intent = new Intent(INTENTFILTER);
        intent.putExtra(HOMEWORK_TITLE, homeTitle);
        intent.putExtra(HOMEWORK_CONTENTS,homeContent );
        intent.putExtra(LASTDATE_SUBMISSION, lastDate);
        intent.putExtra(HOMEWORK_NUMBER, homeNumber );
        intent.putExtra(HOMEWORKDATE, homeDate);
        intent.putExtra(SUBJECT, subject);
        intent.putExtra(NUMBER_USER, userDataSp.getUserData(UserDataSP.NUMBER_USER));
        sendBroadcast(intent);
    }
}
