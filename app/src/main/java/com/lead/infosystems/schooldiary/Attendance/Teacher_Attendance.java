package com.lead.infosystems.schooldiary.Attendance;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.ServerConnect;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Teacher_Attendance extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

UserDataSP userDataSP;
    SPData spData;
    Spinner student_class,student_divisio;

    private static final String[] Stu_cls=null;
    private static final String[] Stu_div=null;
    String a,b;

public  Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher__attendance);
        bt=(Button)findViewById(R.id.button_sub);
        userDataSP=new UserDataSP(this);
        spData =new SPData(this);




        student_class=(Spinner)findViewById(R.id.spinner_cls);
        student_divisio=(Spinner)findViewById(R.id.spinner_div);
        ArrayAdapter<String> adapter_class=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Stu_cls);
        ArrayAdapter<String> adapter_division=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,Stu_div);
        student_class.setAdapter(adapter_class);
        student_divisio.setAdapter(adapter_division);

        student_class.setOnItemSelectedListener(this);
        student_divisio.setOnItemSelectedListener(this);

    }

    public void Attend(View v){
        if(ServerConnect.checkInternetConenction(this)){
            if(a!="Select" && b!="Select"){
                new backgrd(this).execute();
                Intent it=new Intent(Teacher_Attendance.this,Student_list.class);
                startActivity(it);

            }else {
                Toast.makeText(getApplicationContext(),"Please Enter all fields",Toast.LENGTH_SHORT).show();
            }
        }else {

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        switch (parent.getId()){
            case R.id.spinner_cls:
                 a=parent.getSelectedItem().toString();
                Toast.makeText(Teacher_Attendance.this,"Class Selected"+a,Toast.LENGTH_SHORT).show();
                break;
            case R.id.spinner_div:
                 b=parent.getSelectedItem().toString();
                Toast.makeText(Teacher_Attendance.this,"Division Selected"+b,Toast.LENGTH_SHORT).show();
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

  private class backgrd extends AsyncTask<Void,Void,String>{

        String json_url;
        Activity activity;
        backgrd(Activity activity) {
            this.activity = activity;
        }



      @Override
        protected void onPreExecute() {
            json_url = "leadinfosystems.com/school_diary/SchoolDiary/student_list_fetch.php";
        }
        @Override
        protected String doInBackground(Void... params) {

            try {
                URL url = new URL("http://leadinfosystems.com/school_diary/SchoolDiary/attendance_insert.php");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                Uri.Builder builder = new Uri.Builder();

                builder.appendQueryParameter("school_number",userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));


               // builder.appendQueryParameter("class", a);
                //builder.appendQueryParameter("division", b);

                String abc = builder.build().getQuery();
                bufferedWriter.write(abc);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);

                }
                bufferedReader.close();
                inputStream.close();

                return stringBuilder.toString().trim();


            }catch (IOException e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String result) {

            SPData spData = new SPData(getApplicationContext());
            spData.storeData(result);
            Log.e("my",result);

        }
    }


}
