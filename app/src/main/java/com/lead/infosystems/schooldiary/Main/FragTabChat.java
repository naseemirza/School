package com.lead.infosystems.schooldiary.Main;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Config;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.lead.infosystems.schooldiary.Data.ChatListItems;
import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.Generic.ServerConnect;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FragTabChat extends Fragment {


    private View rootview;
    private ListView list;
    private UserDataSP userDataSP;
    private MyListAdapter myListAdapter;
    private List<ChatListItems> items = new ArrayList<>();
    private String myName;
    private ProgressBar progressBar;
    private MyDataBase dataBase;
    private TextView noChats;
    private static boolean back = false;
    public FragTabChat() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(receiver,new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER_CHAT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview =  inflater.inflate(R.layout.fragment_tab_chat, container, false);
        list = (ListView) rootview.findViewById(R.id.list_two);
        progressBar = (ProgressBar) rootview.findViewById(R.id.progressBar);
        userDataSP = new UserDataSP(getActivity().getApplicationContext());
        dataBase = new MyDataBase(getActivity().getApplicationContext());
        myName = userDataSP.getUserData(UserDataSP.FIRST_NAME)+" "+userDataSP.getUserData(UserDataSP.LAST_NAME);
        myListAdapter = new MyListAdapter();
        noChats = (TextView) rootview.findViewById(R.id.no_chats);
        list.setAdapter(myListAdapter);
        setItemClicks();
        if(ServerConnect.checkInternetConenction(getActivity()) && !back){
            connect();
        }else{
            getDataIntoList();
        }
        return rootview;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getDataIntoList();
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        getDataIntoList();
    }

    private void setItemClicks(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),Chat.class);
                intent.putExtra(Chat.USER_ID,items.get(position).getOtherUserId(userDataSP.getUserData(UserDataSP.NUMBER_USER)));
                intent.putExtra(Chat.FIRST_NAME,items.get(position).getChatUserName(myName));
                intent.putExtra(Chat.PROPIC_LINK,items.get(position).getProfilePic_link());
                startActivity(intent);
            }
        });
    }

    private class MyListAdapter extends ArrayAdapter<ChatListItems>{

        public MyListAdapter() {
            super(getActivity().getApplicationContext(), R.layout.messageview_item, items);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null){
                itemView = getActivity().getLayoutInflater().inflate(R.layout.messageview_item,parent,false);
            }
            final ChatListItems currentItem = items.get(position);

            TextView name = (TextView) itemView.findViewById(R.id.title);
            TextView date = (TextView) itemView.findViewById(R.id.date);
            TextView message = (TextView) itemView.findViewById(R.id.text);
            final ImageView propic = (ImageView) itemView.findViewById(R.id.profile_image);

            if(currentItem.getProfilePic_link() != null && currentItem.getProfilePic_link().contains("jpeg")){
                Picasso.with(getActivity().getApplicationContext())
                        .load(Utils.SERVER_URL+currentItem.getProfilePic_link().replace("profilepic","propic_thumb"))
                        .placeholder(R.drawable.defaultpropic)
                        .networkPolicy(ServerConnect.checkInternetConenction(getActivity()) ?
                                NetworkPolicy.NO_CACHE : NetworkPolicy.OFFLINE)
                        .into(propic);
            }else{
                propic.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.defaultpropic));
            }

            name.setText(currentItem.getChatUserName(myName));
            date.setText(Utils.getTimeString(currentItem.getDate()));
            message.setText(currentItem.getLast_message());

            if(currentItem.getUser1ID().contentEquals(userDataSP.getUserData(UserDataSP.NUMBER_USER))){
                message.setTextColor(getResources().getColor(R.color.colorBlue));
            }else{
                message.setTextColor(getResources().getColor(R.color.colorGreen));
            }

            return itemView;
        }
    }

    private void connect(){
        progressBar.setVisibility(View.VISIBLE);
        MyVolley volley = new MyVolley(getActivity().getApplicationContext(), new IVolleyResponse() {
            @Override
            public void volleyResponse(String result) {
                items.clear();
                if(result != MyVolley.RESPONSE_ERROR) {
                    noChats.setVisibility(View.GONE);
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for(int i = 0; i< jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            dataBase.newChat(jsonObject.getString("chat_id"),jsonObject.getString("user1")
                                    ,jsonObject.getString("user1_id"),jsonObject.getString("user2")
                                    ,jsonObject.getString("user2_id"),jsonObject.getString("date")
                                    ,jsonObject.getString("last_message"),jsonObject.getString("profilePic_link"));
                        }
                        getDataIntoList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    noChats.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
                back = true;
            }
        });
        volley.setUrl(Utils.CHAT_LIST);
        volley.setParams(UserDataSP.NUMBER_USER,userDataSP.getUserData(UserDataSP.NUMBER_USER));
        volley.connect();
    }

    private void getDataIntoList() {
        items.clear();
        if(dataBase.getActiveChats().getCount() >0){
            Cursor data = dataBase.getActiveChats();
            while (data.moveToNext()){
                items.add(new ChatListItems(data.getString(1)
                        ,data.getString(2),data.getString(3)
                        ,data.getString(4),data.getString(5)
                        ,data.getString(6),data.getString(7)
                        ,data.getString(8)));
            }
            Collections.sort(items,new MyComparator());
            myListAdapter.notifyDataSetChanged();
        }
    }

    private class MyComparator implements Comparator<ChatListItems>{

        @Override
        public int compare(ChatListItems lhs, ChatListItems rhs) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Utils.DATE_FORMAT);
            try {
                Date date1 = dateFormat.parse(lhs.getDate());
                Date date2 = dateFormat.parse(rhs.getDate());
                return date2.compareTo(date1);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }
}
