package com.lead.infosystems.schooldiary;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Main.MainActivity;
import com.lead.infosystems.schooldiary.ServerConnection.ServerConnect;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONException;

import java.io.IOException;

public class Login extends AppCompatActivity {

    EditText eUsername;
    EditText ePassword;
    String username;
    String password;
    UserDataSP userDataSP;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        eUsername = (EditText) findViewById(R.id.username);
        ePassword = (EditText) findViewById(R.id.password);
    }
    public void login_btn(View v){
        username = eUsername.getText().toString().trim();
        password = ePassword.getText().toString().trim();
        if(ServerConnect.checkInternetConenction(this)){
            if(!username.isEmpty() && !password.isEmpty()){
                new ProfileLogin().execute();
            }else {
                Toast.makeText(getApplicationContext(),"Please Enter all fields",Toast.LENGTH_SHORT).show();
                eUsername.setText("");
                ePassword.setText("");
            }
        }else {

            }

    }

    public Context getContext(){
        return getApplicationContext();
    }


    private class ProfileLogin extends AsyncTask<String,Void,String>{

        ProgressDialog progressDialog = new ProgressDialog(Login.this);
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Logging In...");
            progressDialog.setCancelable(true);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter("username",username);
            builder.appendQueryParameter("password",password);
            try {
                return ServerConnect.downloadUrl(Utils.LOGIN,builder.build().getEncodedQuery());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            if(s != null){
                if(!s.contains("ERROR")){
                try {
                    userDataSP = new UserDataSP(getApplicationContext());
                    userDataSP.storeLoggedInUser(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    Log.e("Data",s);
                }else{
                    Toast.makeText(getApplicationContext(),"Wrong Username or Password",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(),"Connection Error try again..",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
