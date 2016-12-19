package com.lead.infosystems.schooldiary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lead.infosystems.schooldiary.CloudMessaging.MyFirebaseInstanceIDService;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Main.MainActivity;
import com.lead.infosystems.schooldiary.ServerConnection.MyVolley;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

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
    public void volleyResponce(String result) {
        //teacher login
        Log.e("res",result);
         if(result.contains(UserDataSP.NUMBER_USER) && !result.contains(UserDataSP.STUDENT_NUMBER)){
                try {
                    userDataSP.storeLoggedInUser(result);
                    Log.e("reg_id",userDataSP.getUserData(UserDataSP.CLOUD_ID)+" id");
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
                 }else{
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