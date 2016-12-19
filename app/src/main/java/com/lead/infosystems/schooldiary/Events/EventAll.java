package com.lead.infosystems.schooldiary.Events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.lead.infosystems.schooldiary.Events.EventDailog.EVENT_DATE;
import static com.lead.infosystems.schooldiary.Events.EventDailog.EVENT_DETAIL;
import static com.lead.infosystems.schooldiary.Events.EventDailog.EVENT_NAME;
import static com.lead.infosystems.schooldiary.Events.EventDailog.INTENT_FILTER;
import static com.lead.infosystems.schooldiary.Events.EventDailog.SUBMIT_DATE;

public class EventAll extends Fragment {

    UserDataSP userDataSP;
    CompactCalendarView calendarView;
    ListView listview;
    List<EventsData> eventList = new ArrayList<>();
    MyAdapter adapter;
    Boolean dateDoubleClick = false;

    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    public EventAll() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_all, container, false);
        getActivity().getApplicationContext().registerReceiver(receiver, new IntentFilter(INTENT_FILTER));
        userDataSP=new UserDataSP(getActivity());
       listview = (ListView)rootView.findViewById(R.id.list_event);
        calendarView = (CompactCalendarView)rootView.findViewById(R.id.compactcalendar_view);
        getActivity().setTitle(dateFormatForMonth.format(calendarView.getFirstDayOfCurrentMonth()));
        adapter = new MyAdapter();
        listview.setAdapter(adapter);
        getEventData();
        return rootView;
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            eventList.clear();
            eventList.add(new EventsData(intent.getStringExtra(EVENT_NAME),intent.getStringExtra(EVENT_DETAIL),intent.getStringExtra(EVENT_DATE),intent.getStringExtra(SUBMIT_DATE), intent.getStringExtra("school_number")));
           getDataValues();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    public void getEventData()
    {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, Utils.EVENT_FETCH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               Log.e("event", response);

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

                params.put("school_number",userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));

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
            eventList.add(new EventsData(jsonobj.getString("event_name"), jsonobj.getString("event_details"), jsonobj.getString("event_date"), jsonobj.getString("submit_date"), jsonobj.getString("school_number")));

        }
        getDataValues();
    }

    private void getDataValues()
    {
        Event e;
        for(int i=0; i<eventList.size(); i++) {
            EventsData allEvent = eventList.get(i);
            e = new Event(Color.BLACK, Utils.getTimeInMili(allEvent.getEvent_date()), allEvent);
                calendarView.addEvent(e);
        }

        calendarView.setVisibility(View.VISIBLE);
        calendarView.showCalendar();

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date dateClicked) {
                if(dateDoubleClick){
                    SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formatted = dateFormater.format(dateClicked);
                    loadEventFragDialog(formatted);

                }else {
                    dateDoubleClick = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dateDoubleClick = false;
                        }
                    },1000);
                }
                eventList.clear();
                List<Event> listDataOnDate = (List<Event>) calendarView.getEvents(dateClicked);
                for(int i=0; i<listDataOnDate.size(); i++)
                {
                    eventList.add((EventsData) listDataOnDate.get(i).getData());
                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                getActivity().setTitle(dateFormatForMonth.format(firstDayOfNewMonth.getTime())+"");
                List<Event> li= calendarView.getEventsForMonth(firstDayOfNewMonth);
                adapter.clear();
                Log.e("event", String.valueOf(li));

            }
        });
    }
    private void loadEventFragDialog( String dateFormatted){
        android.app.FragmentManager fragmentManager = getActivity().getFragmentManager();
        EventDailog dialog;
        dialog = new EventDailog(dateFormatted);
        dialog.show(fragmentManager,"frag");
    }


    class MyAdapter extends ArrayAdapter<EventsData> {

        public MyAdapter() {
            super(getActivity().getApplicationContext(), R.layout.activity_event_list, eventList);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ItemView = convertView;
            if (ItemView == null) {
                ItemView = getActivity().getLayoutInflater().inflate(R.layout.activity_event_list, parent, false);
            }

            EventsData currentItem = eventList.get(position);
            TextView eventName = (TextView) ItemView.findViewById(R.id.event_name);
            TextView eventDetail = (TextView) ItemView.findViewById(R.id.event_detail);
            eventName.setText(currentItem.getEvent_name()+":");
            eventDetail.setText(" "+currentItem.getEvent_detail());


            return ItemView;

        }

    }





}
