package com.lead.infosystems.schooldiary.Events;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventAll extends Fragment {


    UserDataSP userDataSP;
    CompactCalendarView calendarView;
    ListView listview;
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    public EventAll() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_all, container, false);
        userDataSP=new UserDataSP(getActivity());

        calendarView = (CompactCalendarView)rootView.findViewById(R.id.compactcalendar_view);
        getActivity().setTitle(dateFormatForMonth.format(calendarView.getFirstDayOfCurrentMonth()));

        getEventData();
        return rootView;
    }

    public void getEventData()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utils.ATTENDANCE_FETCH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    getJsonData(response);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();

                params.put("number_user",userDataSP.getUserData(UserDataSP.NUMBER_USER));

                return params;
            }
        };
        RetryPolicy retryPolicy = new DefaultRetryPolicy(2000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        requestQueue.add(request);


    }

    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);


        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
           // attendance.add(new AttendanceData(jsonobj.getString("year"), jsonobj.getString("day"), jsonobj.getString("month"), jsonobj.getString("attendance")));

        }
        getEventsDetail();
    }

    private void getEventsDetail()
    {
        Event e;
//        for(int i=0; i<attendance.size(); i++) {
//            AttendanceData allAttendance = attendance.get(i);
//            String year = allAttendance.getYear();
//            String day= allAttendance.getDay();
//            String month =  allAttendance.getMonth();
//            if(allAttendance.getAttendance().contains("A")) {
//                e = new Event(Color.RED, Utils.getTimeMiliSec(year + "-" + month + "-" + day + " " + 10 + ":" + 20 + ":" + 12), allAttendance.getAttendance());
//                calendarView.addEvent(e);
//            }
//            else if(allAttendance.getAttendance().contains("L"))
//            {
//                e = new Event(Color.YELLOW, Utils.getTimeMiliSec(year + "-" + month + "-" + day + " " + 10 + ":" + 20 + ":" + 12), allAttendance.getAttendance());
//                calendarView.addEvent(e);
//            }
//            else
//            {
//                e = new Event(Color.GREEN, Utils.getTimeMiliSec(year + "-" + month + "-" + day + " " + 10 + ":" + 20 + ":" + 12), allAttendance.getAttendance());
//                calendarView.addEvent(e);
//            }
//        }
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                //List<Event> events = calendarView.getEvents(dateClicked);
                Toast.makeText(getActivity(), "There is no data", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                getActivity().setTitle(dateFormatForMonth.format(firstDayOfNewMonth.getTime())+"");
            }
        });


    }









}
