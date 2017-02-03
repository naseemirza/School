package com.lead.infosystems.schooldiary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Main.MainActivity;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.Generic.Utils;

import org.json.JSONException;

public class Login extends AppCompatActivity implements IVolleyResponse {

    private EditText eUsername;
    private EditText ePassword;
    private String username;
    private String password;
    private UserDataSP userDataSP;
    private MyVolley myVolley;
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private ProgressDialog progressDialog;
    private static boolean backExit = false;
    public static final String DEVICE_NUM = "device_num";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        eUsername = (EditText) findViewById(R.id.username);
        ePassword = (EditText) findViewById(R.id.password);
        userDataSP = new UserDataSP(getApplicationContext());
    }

    public void login_btn(View v){
        username = eUsername.getText().toString().trim();
        password = ePassword.getText().toString().trim();
            if(!username.isEmpty() && !password.isEmpty()){
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Logging In...");
                progressDialog.setCancelable(true);
                progressDialog.show();

                myVolley = new MyVolley(getApplicationContext(),this);
                myVolley.setUrl(Utils.LOGIN);
                myVolley.setParams(USERNAME,username);
                myVolley.setParams(PASSWORD,password);
                myVolley.connect();
            }else {
                Toast.makeText(getApplicationContext(),"Please Enter all fields",Toast.LENGTH_SHORT).show();
                eUsername.setText("");
                ePassword.setText("");
            }
    }

    @Override
    public void onBackPressed() {
        if(backExit){
            moveTaskToBack(true);
        }else{
            backExit = true;
            Toast.makeText(getApplicationContext(),"Press back button one more time to exit",Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backExit = false;
                }
            },1000);
        }
    }

    @Override
    public void volleyResponse(String result) {
        //user login
         if(result.contains(UserDataSP.NUMBER_USER) && !result.contains(UserDataSP.STUDENT_NUMBER)){
                try {
                    userDataSP.storeLoggedInUser(result);
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    myVolley.setUrl(Utils.REGESTRATION);
                    myVolley.setParams(UserDataSP.NUMBER_USER,userDataSP.getUserData(UserDataSP.NUMBER_USER));
                    myVolley.setParams(UserDataSP.CLOUD_ID,userDataSP.getUserData(UserDataSP.CLOUD_ID));
                    myVolley.setParams(DEVICE_NUM,telephonyManager.getDeviceId());
                    myVolley.connect();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        // student login
         }else if(result.contains(UserDataSP.STUDENT_NUMBER)){
             try {
                 userDataSP.storeStudentData(result);
                 progressDialog.dismiss();
                 startActivity(new Intent(getApplicationContext(), MainActivity.class));
             } catch (JSONException e) {
                 e.printStackTrace();
             }
             //FCM regestratation
         }else{
             if(result.contains("DONE")){
                 if(userDataSP.isStudent()){
                     myVolley.setUrl(Utils.LOGIN);
                     myVolley.setParams(UserDataSP.NUMBER_USER,userDataSP.getUserData(UserDataSP.NUMBER_USER));
                     myVolley.connect();
                 }else{//teacher login
                     progressDialog.dismiss();
                     startActivity(new Intent(getApplicationContext(), MainActivity.class));
                 }
             }else{
                 progressDialog.dismiss();
                 Toast.makeText(getApplicationContext(), "Wrong Username or Password", Toast.LENGTH_SHORT).show();
             }
         }
    }
}