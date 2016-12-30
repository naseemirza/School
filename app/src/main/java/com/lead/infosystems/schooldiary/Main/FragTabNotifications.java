package com.lead.infosystems.schooldiary.Main;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.NotificationData;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.ServerConnect;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragTabNotifications extends Fragment {

    View rootview;
    ListView list;
    List<NotificationData> items = new ArrayList<NotificationData>();
    ArrayAdapter adapter;
    private MyDataBase myDataBase;
    public FragTabNotifications() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_tab_notification, container, false);

        list = (ListView) rootview.findViewById(R.id.list_three);
        adapter = new MyListAdapter();
        list.setAdapter(adapter);
        myDataBase = new MyDataBase(getActivity().getApplicationContext());

        if(ServerConnect.checkInternetConenction(getActivity())){
            getDataFromServer();
        }else{
            putDataIntoList();
        }
        setItemClick();
        return rootview;
    }


    private void getDataFromServer(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.POST, Utils.NOTIFICATION_FETCH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("res",response);
                if(response != null && !response.contentEquals("ERROR")){
                    myDataBase.clearNotifications();
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(response);
                        for(int i = 0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            myDataBase.incertNotification(jsonObject.getString("notification_number")
                                    ,jsonObject.getString("date")
                                    ,jsonObject.getString("class")
                                    ,jsonObject.getString("division")
                                    ,jsonObject.getString("notification_text")
                                    ,jsonObject.getString("type"));
                        }
                        putDataIntoList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), ServerConnect.connectionError(error),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                UserDataSP user = new UserDataSP(getActivity().getApplicationContext());
                if(user.isStudent()){
                    map.put(UserDataSP.CLASS,user.getUserData(UserDataSP.CLASS));
                    map.put(UserDataSP.DIVISION,user.getUserData(UserDataSP.DIVISION));
                }
                return map;
            }
        };
        RetryPolicy retry = new DefaultRetryPolicy(2000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retry);
        requestQueue.add(request);
    }

    private void putDataIntoList() {
        Cursor data = myDataBase.getNotifications();
        if(data.getCount()>0){
            items.clear();
            while (data.moveToNext()){
                items.add(new NotificationData(data.getString(1),data.getString(2)
                        ,data.getString(3),data.getString(4),
                        data.getString(5),data.getString(6)));
            }
           adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(getActivity().getApplicationContext(),"No notifications",Toast.LENGTH_SHORT).show();
        }
    }

    private class MyListAdapter extends ArrayAdapter<NotificationData>{

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
            NotificationData current = items.get(position);

            TextView title = (TextView) itemView.findViewById(R.id.title);
            TextView date = (TextView) itemView.findViewById(R.id.date);
            TextView text = (TextView) itemView.findViewById(R.id.text);
            String title_text = current.getType().replace("_"," ");
            title.setText(title_text);
            date.setText(Utils.getTimeString(current.getDate()));
            text.setText(current.getNotificationText());
            return itemView;
        }
    }

    private void setItemClick(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (items.get(position).getType()){
                    case NotificationData.HOME_WORK:
                        StudentDiary_student frag = new StudentDiary_student();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_con,frag);
                        transaction.addToBackStack(MainActivity.BACK_STACK_TAG);
                        transaction.commit();
                        break;
                    case NotificationData.MARKS:
                        Log.e("selected",NotificationData.MARKS);
                        break;
                    case NotificationData.MODEL_QP:
                        Log.e("selected",NotificationData.MODEL_QP);
                        break;
                    case NotificationData.TEST_EXAM:
                        Log.e("selected",NotificationData.TEST_EXAM);
                        break;
                    case NotificationData.EVENT:
                        Log.e("selected",NotificationData.EVENT);
                        break;
                    case NotificationData.APPLICATION_FORM:
                        Log.e("selected",NotificationData.APPLICATION_FORM);
                        break;
                }
            }
        });
    }
}
