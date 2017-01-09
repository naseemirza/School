package com.lead.infosystems.schooldiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Main.MainActivity;

public class Splash extends Activity {

    UserDataSP userDataSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        userDataSP =new UserDataSP(getApplicationContext());
        if(userDataSP.isUserLoggedIn()){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }else {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        },1000);
        }
    }
}