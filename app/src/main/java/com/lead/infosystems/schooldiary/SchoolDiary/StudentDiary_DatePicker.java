package com.lead.infosystems.schooldiary.SchoolDiary;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.MyVolley;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import java.util.Calendar;

public class StudentDiary_DatePicker extends AppCompatActivity implements IVolleyResponse{
    String class_list;
    String division_list;
    String subject_list;
    UserDataSP userDataSp;
    int year_h, month_h, day_h;
    static final int DIALOG_ID = 0;
    Button btn, submitB;
    EditText editTitle, editContent;
    private MyVolley myVolley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_diary__date_picker);
        userDataSp = new UserDataSP(this);
        Intent intent = getIntent();
        class_list = intent.getStringExtra("class");
        division_list = intent.getStringExtra("division");
        subject_list = intent.getStringExtra("subject");
        editTitle = (EditText)findViewById(R.id.title_home);
        editContent = (EditText)findViewById(R.id.content_home);
        submitB = (Button)findViewById(R.id.submit_home);
        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitHomeWork();
            }
        });
        final Calendar cal = Calendar.getInstance();
        year_h = cal.get(Calendar.YEAR);
        month_h = cal.get(Calendar.MONTH);
        day_h = cal.get(Calendar.DAY_OF_MONTH);
        myVolley = new MyVolley(getApplicationContext(), this);
        showDialoOnButtonClick();

    }

    public void showDialoOnButtonClick()
    {
        btn = (Button)findViewById(R.id.button_date);
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
        return new DatePickerDialog(this, dpickerListener, year_h, month_h, day_h);

        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener()
    {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_h = year;
            month_h = monthOfYear+1;
            day_h = dayOfMonth;
           // Toast.makeText(StudentDiary_DatePicker.this, year_h+"/"+month_h+"/"+day_h, Toast.LENGTH_SHORT).show();
        }
    };

public void submitHomeWork()
{   if(editTitle.getText().toString().isEmpty())
   {
    Toast.makeText(this, "fill the entries", Toast.LENGTH_SHORT).show();

   }
    else if (editContent.getText().toString().isEmpty())
{
    Toast.makeText(this, "fill the entries", Toast.LENGTH_SHORT).show();
}
    else {
    myVolley.setUrl(Utils.HOMEWORK_INSERT);
    myVolley.setParams(UserDataSP.SCHOOL_NUMBER, userDataSp.getUserData(UserDataSP.SCHOOL_NUMBER));
    myVolley.setParams("class", class_list);
    myVolley.setParams("division", division_list);
    myVolley.setParams("subject_name", subject_list);
    myVolley.setParams("lastDate_submission", year_h + "-" + month_h + "-" + day_h + " " + 00 + ":" + 00 + ":" + 00);
    myVolley.setParams("homework_title", editTitle.getText().toString());
    myVolley.setParams("homework_content", editContent.getText().toString());

    myVolley.setParams(UserDataSP.NUMBER_USER, userDataSp.getUserData(UserDataSP.NUMBER_USER));
    myVolley.connect();
}
}
    @Override
    public void volleyResponce(String result) {

            Log.e("response", result);
        if(result.equals("null")) {

            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Submited", Toast.LENGTH_SHORT).show();

        }
    }





}
