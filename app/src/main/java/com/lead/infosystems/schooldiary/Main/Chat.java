package com.lead.infosystems.schooldiary.Main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import com.lead.infosystems.schooldiary.CloudMessaging.MyFirebaseMessagingService;
import com.lead.infosystems.schooldiary.Data.ChatItemData;
import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.ServerConnect;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat extends AppCompatActivity {

    public static String USER_ID = "user_id";
    public static String CHAT_ID = "chat_id";
    public static String FIRST_NAME = "first_name";

    private String myName,myId,userID,firstName,chatId;


    private List<ChatItemData> items = new ArrayList<>();
    private MyAdaptor myAdaptor;
    private UserDataSP userDataSP;
    private ListView list;
    private MyDataBase myDataBase;
    private EditText chatText;
    private FloatingActionButton sendBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView title = (TextView) toolbar.findViewById(R.id.title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = (ListView) findViewById(R.id.chat_list);
        chatText = (EditText) findViewById(R.id.chat_text);
        sendBtn = (FloatingActionButton) findViewById(R.id.chatBtn);
        prepare();
        title.setText(firstName);

        registerReceiver(receiver, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER_CHAT));
        chatText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                chatText.setFocusableInTouchMode(true);
                chatText.setFocusable(true);
                return false;
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chatText.getText().length()>0)
                connect(chatText.getText().toString());
            }
        });

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getDataIntoList(intent.getExtras().getString(MyFirebaseMessagingService.CHAT_ID));
        }
    };

    private void prepare(){
        myAdaptor = new MyAdaptor();
        userDataSP = new UserDataSP(getApplicationContext());
        myDataBase = new MyDataBase(getApplicationContext());
        list.setAdapter(myAdaptor);

        Bundle extras = getIntent().getExtras();
        userID = extras.getString(USER_ID);
        firstName = extras.getString(FIRST_NAME);

        myId = userDataSP.getUserData(userDataSP.NUMBER_USER);
        myName = userDataSP.getUserData(userDataSP.FIRST_NAME)+" "+userDataSP.getUserData(userDataSP.LAST_NAME);

        if(extras.getString(CHAT_ID) != null && userID == null){
            chatId = extras.getString(CHAT_ID);
        }else {
            chatId = myDataBase.getChatID(myId, userID);
        }

        getDataIntoList(chatId);
    }

    private void connect(final String msg){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, Utils.NOTIFY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response != null && !response.contains("ERROR")) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(jsonObject.getBoolean("success")){
                                    chatText.setText("");
                                    myDataBase.newChat(jsonObject.getString("chat_id"),myName,myId,firstName
                                            ,userID,jsonObject.getString("time"),msg);
                                    myDataBase.chatMessage(jsonObject.getString("chat_id"),myId,msg,jsonObject.getString("time"));

                                    getDataIntoList(jsonObject.getString("chat_id"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            chatText.setText("");
                            Toast.makeText(getApplicationContext(), "sending failed", Toast.LENGTH_SHORT ).show();
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
                map.put("from_user",userDataSP.getUserData(UserDataSP.NUMBER_USER));
                map.put("message",msg);
                return map;
            }
        };
        int socketTimeout = 20000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);

    }

    private void getDataIntoList(String chatId){
        Cursor data = myDataBase.getChatMessages(chatId);
        items.clear();
        if(data.getCount()>0){
            while (data.moveToNext()){
                items.add(new ChatItemData(data.getString(1),data.getString(2),data.getString(3),data.getString(4)));
            }
            myAdaptor.notifyDataSetChanged();
        }

    }

    class MyAdaptor extends ArrayAdapter<ChatItemData>{

        public MyAdaptor() {
            super(getApplicationContext(), R.layout.chat_item,items);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if(v==null){
                v = getLayoutInflater().inflate(R.layout.chat_item,parent,false);
            }

            ChatItemData currentData = items.get(position);

            CardView rcvCard = (CardView) v.findViewById(R.id.card_rec);
            TextView rcvMessage = (TextView) v.findViewById(R.id.message_text_rcv);
            TextView rcvtime = (TextView) v.findViewById(R.id.time_rcv);

            CardView sndCard = (CardView) v.findViewById(R.id.card_send);
            TextView sndMessage = (TextView) v.findViewById(R.id.message_text_snd);
            TextView sndtime = (TextView) v.findViewById(R.id.time_snd);

            if(currentData.getUserId().contentEquals(userDataSP.getUserData(UserDataSP.NUMBER_USER))){
                rcvCard.setVisibility(View.GONE);
                sndCard.setVisibility(View.VISIBLE);
                sndMessage.setText(currentData.getMessage());
                sndtime.setText(Utils.getTimeString(currentData.getTime()));

            }else{
                rcvCard.setVisibility(View.VISIBLE);
                sndCard.setVisibility(View.GONE);
                rcvMessage.setText(currentData.getMessage());
                rcvtime.setText(Utils.getTimeString(currentData.getTime()));
            }

            return v;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
