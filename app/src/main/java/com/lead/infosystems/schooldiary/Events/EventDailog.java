package com.lead.infosystems.schooldiary.Events;

import android.app.DialogFragment;
import android.app.ProgressDialog;
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
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.Generic.ServerConnect;
import com.lead.infosystems.schooldiary.Generic.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventDailog extends DialogFragment{
    public static final String INTENT_FILTER="intent_filter";
    public static final String EVENT_NAME = "event_name";
    public static final String EVENT_DETAIL = "event_details";
    public static final String EVENT_DATE = "event_date";
    public static final String SUBMIT_DATE = "submit_date";

    private View rootView;
    private TextView eventTitle;
    private TextView eventDetail;
    private Button submit;
    private String event_date;
    private String submit_date;
    private UserDataSP userDataSP;

    public EventDailog() {}

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
                        submitEvent(eventTitle.getText().toString(), eventDetail.getText().toString());
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

    private void submitEvent(final String eventText, final String eventDetails){
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        MyVolley myVolley = new MyVolley(getActivity().getApplicationContext(), new IVolleyResponse() {
            @Override
            public void volleyResponse(String result) {
                Toast.makeText(getActivity().getApplicationContext(), "Event Submitted", Toast.LENGTH_SHORT).show();
                try {
                    parseDataEvent(eventText, eventDetails, result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
                getDialog().dismiss();
            }
        });
        myVolley.setUrl(Utils.EVENT_INSERT);

        myVolley.setParams(EVENT_NAME, eventText);
        myVolley.setParams(EVENT_DETAIL, eventDetails);
        myVolley.setParams(EVENT_DATE,event_date);
        myVolley.setParams(UserDataSP.SCHOOL_NUMBER, userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
        myVolley.setParams(UserDataSP.NUMBER_USER, userDataSP.getUserData(UserDataSP.NUMBER_USER));
        myVolley.connect();
    }
    private void parseDataEvent(String event_name, String event_details, String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            submit_date = jsonobj.getString(SUBMIT_DATE);

        }
        Intent intent = new Intent(INTENT_FILTER);
        intent.putExtra(EVENT_NAME, event_name);
        intent.putExtra(EVENT_DETAIL, event_details);
        intent.putExtra(EVENT_DATE, event_date);
        intent.putExtra(SUBMIT_DATE, submit_date);
        intent.putExtra(UserDataSP.STUDENT_NUMBER,userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
        getActivity().getApplicationContext().sendBroadcast(intent);
    }
  }
