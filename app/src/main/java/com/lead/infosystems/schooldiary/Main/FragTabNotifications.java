package com.lead.infosystems.schooldiary.Main;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lead.infosystems.schooldiary.Data.NotificationData;
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
    public FragTabNotifications() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_tab_notification, container, false);
        list = (ListView) rootview.findViewById(R.id.list_three);
        populateListView();
        return rootview;
    }


    private void getDataFromServer(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.POST, Utils.UPDATES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response != null && !response.contentEquals("ERROR")){
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(response);
                        for(int i = 0;i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            //items.add(new NotificationData(jsonObject));
                        }
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

                return map;
            }
        };
    }

    private void populateListView(){
        adapter = new MyListAdapter();
        list.setAdapter(adapter);

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
            NotificationData item = items.get(position);

            TextView title = (TextView) itemView.findViewById(R.id.title);
            TextView date = (TextView) itemView.findViewById(R.id.date);
            TextView text = (TextView) itemView.findViewById(R.id.text);



            return itemView;
        }
    }
}
