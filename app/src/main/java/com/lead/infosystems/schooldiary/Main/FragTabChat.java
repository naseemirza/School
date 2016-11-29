package com.lead.infosystems.schooldiary.Main;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.lead.infosystems.schooldiary.Data.ChatListItems;
import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.Data.QuestionData;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FragTabChat extends Fragment {


    View rootview;
    ListView list;
    private UserDataSP userDataSP;
    List<ChatListItems> items = new ArrayList<>();
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
        if(dataBase.getActiveChats().getCount()>0){
            getDataIntoList();
            Log.e("chat",dataBase.getActiveChats().getCount()+"");
        }else{
            Log.e("chat","conect");
            connect();
        }
        return rootview;
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

            TextView name = (TextView) itemView.findViewById(R.id.name);
            TextView date = (TextView) itemView.findViewById(R.id.date);
            TextView message = (TextView) itemView.findViewById(R.id.question_text);
            ImageView propic = (ImageView) itemView.findViewById(R.id.propic);

            String myName = userDataSP.getUserData(UserDataSP.FIRST_NAME)+" "+userDataSP.getUserData(UserDataSP.LAST_NAME);
            name.setText(currentItem.getChatUserName(myName));
            date.setText(Utils.getTimeString(currentItem.getDate()));
            message.setText(currentItem.getLast_message());

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
                                                                ,jsonObject.getString("user2"),jsonObject.getString("date")
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
                map.put("user",userDataSP.getUserData(UserDataSP.STUDENT_NUMBER));
                return map;
            }
        };
        int socketTimeout = 20000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);

    }

    private void getDataIntoList() {
        if(dataBase.getActiveChats().getCount() >0){
            Cursor data = dataBase.getActiveChats();
            while (data.moveToNext()){
                items.add(new ChatListItems(data.getString(1)
                        ,data.getString(2),data.getString(3)
                        ,data.getString(4),data.getString(5)));
            }
            populateListView();
        }
    }

    private void populateListView(){
        ArrayAdapter adapter = new MyListAdapter();
        list.setAdapter(adapter);

    }
}
