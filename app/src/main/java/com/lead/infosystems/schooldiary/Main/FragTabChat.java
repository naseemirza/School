package com.lead.infosystems.schooldiary.Main;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import com.lead.infosystems.schooldiary.Data.ChatListItems;
import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.ServerConnect;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FragTabChat extends Fragment {


    View rootview;
    ListView list;
    private UserDataSP userDataSP;
    private MyListAdapter myListAdapter;
    List<ChatListItems> items = new ArrayList<>();
    String myName;
    private MyDataBase dataBase;
    public FragTabChat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview =  inflater.inflate(R.layout.fragment_tab_chat, container, false);
        list = (ListView) rootview.findViewById(R.id.list_two);
        userDataSP = new UserDataSP(getActivity().getApplicationContext());
        dataBase = new MyDataBase(getActivity().getApplicationContext());
        myName = userDataSP.getUserData(UserDataSP.FIRST_NAME)+" "+userDataSP.getUserData(UserDataSP.LAST_NAME);
        myListAdapter = new MyListAdapter();
        list.setAdapter(myListAdapter);
        setItemClicks();
        if(ServerConnect.checkInternetConenction(getActivity())){
            connect();
        }else{
            getDataIntoList();
        }
        getActivity().registerReceiver(receiver,new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER_CHAT));
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
            ChatListItems currentItem = items.get(position);

            TextView name = (TextView) itemView.findViewById(R.id.title);
            TextView date = (TextView) itemView.findViewById(R.id.date);
            TextView message = (TextView) itemView.findViewById(R.id.text);
            ImageView propic = (ImageView) itemView.findViewById(R.id.propic);


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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.POST, Utils.CHAT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        items.clear();
                        if(response != null && !response.contains("ERROR")) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for(int i = 0; i< jsonArray.length();i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    dataBase.newChat(jsonObject.getString("chat_id"),jsonObject.getString("user1")
                                            ,jsonObject.getString("user1_id"),jsonObject.getString("user2")
                                            ,jsonObject.getString("user2_id"),jsonObject.getString("date")
                                            ,jsonObject.getString("last_message"));
                                }
                                getDataIntoList();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                Toast.makeText(getActivity(),"Failed",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map =  new HashMap<>();
                map.put("user",userDataSP.getUserData(UserDataSP.NUMBER_USER));
                return map;
            }
        };
        int socketTimeout = 20000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);

    }

    private void getDataIntoList() {
        items.clear();
        if(dataBase.getActiveChats().getCount() >0){
            Cursor data = dataBase.getActiveChats();
            while (data.moveToNext()){
                items.add(new ChatListItems(data.getString(1)
                        ,data.getString(2),data.getString(3)
                        ,data.getString(4),data.getString(5)
                        ,data.getString(6),data.getString(7)));
            }
            Collections.sort(items,new MyComparator());
            myListAdapter.notifyDataSetChanged();
        }
    }

    private class MyComparator implements Comparator<ChatListItems>{

        @Override
        public int compare(ChatListItems lhs, ChatListItems rhs) {
            return (int) (Utils.getTimeInMili(rhs.getDate()) - Utils.getTimeInMili(lhs.getDate()));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }
}
