package com.lead.infosystems.schooldiary.ShareButton;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.lead.infosystems.schooldiary.R;
import com.sromku.simple.fb.SimpleFacebook;

public class MainPage extends AppCompatActivity {
    SimpleFacebook fb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);


        SimpleFacebook.setConfiguration(new MyConfig().getMyConfigs());
        fb=SimpleFacebook.getInstance(this);

        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.loginfb);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Login_fb(MainPage.this,fb).login();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fb=SimpleFacebook.getInstance(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fb.onActivityResult(requestCode,resultCode,data);

    }
}
