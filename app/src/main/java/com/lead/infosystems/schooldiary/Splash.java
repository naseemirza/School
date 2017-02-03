package com.lead.infosystems.schooldiary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Main.MainActivity;
import com.lead.infosystems.schooldiary.Main.PostComments;

public class Splash extends Activity {

    UserDataSP userDataSP;
    TextView t1,t2,t3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        userDataSP =new UserDataSP(getApplicationContext());
        t1 = (TextView) findViewById(R.id.login_logo_name);
        t2 = (TextView) findViewById(R.id.pwd_by);
        t3 = (TextView) findViewById(R.id.lis_name);

        if(userDataSP.isUserLoggedIn()){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }else {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startLogin();
            }
        },2000);
        }
    }

    private void startLogin(){
        Intent intent = new Intent(getApplicationContext(),Login.class);
        ActivityOptionsCompat activityOptionsCompat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Pair<View,String> p1 = Pair.create((View)t1, t1.getTransitionName());
            Pair<View,String> p2 = Pair.create((View)t2, t2.getTransitionName());
            Pair<View,String> p3 = Pair.create((View)t3, t3.getTransitionName());
                activityOptionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(this,p1,p2,p3);
            startActivity(intent,activityOptionsCompat.toBundle());
        }
    }
}