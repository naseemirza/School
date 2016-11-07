package com.lead.infosystems.schooldiary.Main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lead.infosystems.schooldiary.R;

public class PostComments extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);
        getSupportActionBar().setTitle("Comment");
    }



}
