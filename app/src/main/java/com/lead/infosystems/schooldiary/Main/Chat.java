package com.lead.infosystems.schooldiary.Main;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.Data.ChatItemData;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;

public class Chat extends AppCompatActivity {

    public static String USER_ID = "user_id";
    public static String FIRST_NAME = "first_name";
    public static String LAST_NAME = "last_name";
    public static String FULL_NAME = "full_name";
    private String userID,firstName,lastName,fullName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.title);
        toolbar.setTitle("");
        getExtras();
        title.setText(firstName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void getExtras(){
        Bundle extras = getIntent().getExtras();
        userID = extras.getString(USER_ID);
        firstName = extras.getString(FIRST_NAME);
        lastName = extras.getString(LAST_NAME);
        fullName = extras.getString(FULL_NAME);
    }

    class MyAdaptor extends ArrayAdapter<ChatItemData>{

        public MyAdaptor() {
            super(getApplicationContext(), R.layout.chat_item   );
        }
    }
}
