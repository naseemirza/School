package com.lead.infosystems.schooldiary.Events;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.ServerConnect;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventDailog extends DialogFragment{
    public static final String INTENT_FILTER ="intent_filter";
    public static final String EVENT_NAME = "event_name";
    public static final String EVENT_DETAIL = "event_detail";
    public static final String EVENT_DATE = "event_date";
    public static final String SUBMIT_DATE = "submit_date";

    View rootView;
    TextView eventTitle;
    TextView eventDetail;
    Button submit;
    String event_date;
    String submit_date;
    UserDataSP userDataSP;
    public EventDailog() {

    }


    public EventDailog(String event_date) {
        this.event_date = event_date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_event_dialog,container,false);
        userDataSP = new UserDataSP(getActivity().getApplicationContext());
        getDialog().setTitle("New Event");
        eventTitle = (TextView) rootView.findViewById(R.id.text_event);
        eventDetail = (TextView)rootView.findViewById(R.id.text_eventdetails);
        submit = (Button) rootView.findViewById(R.id.submit_event);

        eventTitle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });
        eventDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(eventTitle.getText().length() > 0){
                    if(eventDetail.getText().length()>0) {
                        submitEvents(eventTitle.getText().toString(), eventDetail.getText().toString());
                    }
                    else
                    {
                        Toast.makeText(getActivity().getApplicationContext(),"Please enter the event details..",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"No text..",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private void submitEvents(final String eventText, final String eventDetails){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        final StringRequest request = new StringRequest(Request.Method.POST, Utils.EVENT_INSERT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response != null){
                    if(!response.contains("ERROR")){
                        getDialog().dismiss();
                        Toast.makeText(getActivity().getApplicationContext(), "Event Inserted", Toast.LENGTH_SHORT).show();
                        try {
                            parseDataEvent(eventText, eventDetails, response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("response_event", response);
                       // Intent intent = new Intent(INTENT_FILTER);
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
                HashMap<String, String> map = new HashMap<>();
                map.put("event_name", eventText);
                map.put("event_details", eventDetails);
                map.put("event_date",event_date);
                map.put("school_number", userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
                return map;
            }
        };
        RetryPolicy retryPolicy = new DefaultRetryPolicy(2000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        requestQueue.add(request);
    }

    private void parseDataEvent(String event_name, String event_details, String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            submit_date = jsonobj.getString("submit_date");

        }
        Intent intent = new Intent(INTENT_FILTER);
        intent.putExtra(EVENT_NAME, event_name);
        intent.putExtra(EVENT_DETAIL, event_details);
        intent.putExtra(EVENT_DATE, event_date);
        intent.putExtra(SUBMIT_DATE, submit_date);
        intent.putExtra("school_number",userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
        getActivity().getApplicationContext().sendBroadcast(intent);


    }





  }
