package com.lead.infosystems.schooldiary.Main;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
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
import com.lead.infosystems.schooldiary.Data.ChatItemData;
import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.ServerConnect;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Chat extends AppCompatActivity {

    public static String USER_ID = "user_id";
    public static String CHAT_ID = "chat_id";
    public static String FIRST_NAME = "first_name";
    public static String LAST_NAME = "last_name";
    public static String FULL_NAME = "full_name";
    private String myId,userID,firstName,lastName,fullName;

    private UserDataSP userDataSP;
    private MyDataBase myDataBase;
    private EditText chatText;
    private FloatingActionButton sendBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.title);
        chatText = (EditText) findViewById(R.id.chat_text);
        sendBtn = (FloatingActionButton) findViewById(R.id.chatBtn);
        toolbar.setTitle("");
        getExtras();
        title.setText(firstName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chatText.getText().length()>0)
                connect(chatText.getText().toString());
            }
        });

    }

    private void getExtras(){
        userDataSP = new UserDataSP(getApplicationContext());
        myDataBase = new MyDataBase(getApplicationContext());
        Bundle extras = getIntent().getExtras();

        userID = extras.getString(USER_ID);
        firstName = extras.getString(FIRST_NAME);
        lastName = extras.getString(LAST_NAME);
        fullName = extras.getString(FULL_NAME);
        myId = userDataSP.getUserData(userDataSP.STUDENT_NUMBER);////////////////////////
    }

    private void connect(final String msg){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, Utils.NOTIFY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("data",response);
                        if(response != null && !response.contains("ERROR")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.getBoolean("success")){
                                    myDataBase.newChat(jsonObject.getString("chat_id"),myId,userID
                                            ,msg,jsonObject.getString("time"));
                                    myDataBase.chatMessage(jsonObject.getString("chat_id"),myId,msg,jsonObject.getString("time"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            myDataBase.getChatID(myId,userID);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                Toast.makeText(getApplicationContext(), ServerConnect.connectionError(error),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map =  new HashMap<>();
                map.put("to_user",userID);
                map.put("from_user",userDataSP.getUserData(UserDataSP.STUDENT_NUMBER));//neds to be changd
                map.put("message",msg);
                return map;
            }
        };
        int socketTimeout = 20000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);

    }

    class MyAdaptor extends ArrayAdapter<ChatItemData>{

        public MyAdaptor() {
            super(getApplicationContext(), R.layout.chat_item   );
        }
    }
}
