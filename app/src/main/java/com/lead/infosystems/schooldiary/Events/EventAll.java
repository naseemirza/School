package com.lead.infosystems.schooldiary.Events;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.Generic.ServerConnect;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.lead.infosystems.schooldiary.Events.EventDailog.EVENT_DATE;
import static com.lead.infosystems.schooldiary.Events.EventDailog.EVENT_DETAIL;
import static com.lead.infosystems.schooldiary.Events.EventDailog.EVENT_NAME;
import static com.lead.infosystems.schooldiary.Events.EventDailog.INTENT_FILTER;
import static com.lead.infosystems.schooldiary.Events.EventDailog.SUBMIT_DATE;

public class EventAll extends Fragment {

    private Date selectedDate;
    private MyDataBase myDataBase;
    private View rootView;
    private UserDataSP userDataSP;
    private ProgressBar progressBar;
    private TextView notAvailable, noEvents;
    private CompactCalendarView calendarView;
    private ListView listview;
    private List<EventsData> eventList = new ArrayList<>();
    private MyAdapter adapter;
    private Boolean dateDoubleClick = false;

    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());

    public EventAll() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_all, container, false);
        getActivity().getApplicationContext().registerReceiver(receiver, new IntentFilter(INTENT_FILTER));
        userDataSP=new UserDataSP(getActivity());
        myDataBase = new MyDataBase(getActivity().getApplicationContext());
        progressBar = (ProgressBar)rootView.findViewById(R.id.event_progress);
        notAvailable = (TextView)rootView.findViewById(R.id.eventnot_available);
        listview = (ListView)rootView.findViewById(R.id.list_event);
        noEvents = (TextView) rootView.findViewById(R.id.no_events);
        calendarView = (CompactCalendarView)rootView.findViewById(R.id.compactcalendar_view);
        getActivity().setTitle(dateFormatForMonth.format(calendarView.getFirstDayOfCurrentMonth()));
        eventList.clear();
        adapter = new MyAdapter();
        listview.setAdapter(adapter);
        if(ServerConnect.checkInternetConenction(getActivity()))
        {
            progressBar.setVisibility(View.VISIBLE);
            getData();
        }
        else {
            putEventDataList();
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().getApplicationContext().registerReceiver(receiver, new IntentFilter(INTENT_FILTER));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            eventList.clear();
            eventList.add(new EventsData(intent.getStringExtra(EVENT_NAME),intent.getStringExtra(EVENT_DETAIL),
                    intent.getStringExtra(EVENT_DATE),intent.getStringExtra(SUBMIT_DATE),
                    intent.getStringExtra(UserDataSP.STUDENT_NUMBER)));
           getDataValues();
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        try{
            getActivity().unregisterReceiver(receiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void getData(){
        myDataBase.clearEventData();
        eventList.clear();
        MyVolley volley = new MyVolley(getActivity().getApplicationContext(), new IVolleyResponse() {
            @Override
            public void volleyResponse(String result) {
                try {
                    notAvailable.setVisibility(View.GONE);
                    getJsonData(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                    notAvailable.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
        volley.setUrl(Utils.EVENT_FETCH);
        volley.setParams(UserDataSP.SCHOOL_NUMBER,userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
        volley.connect();
    }

    private void getJsonData(String re) throws JSONException {
        myDataBase.clearEventData();
        JSONArray json = new JSONArray(re);
        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            myDataBase.insertEventData(jsonobj.getString("event_name"), jsonobj.getString("event_details"), jsonobj.getString("event_date"), jsonobj.getString("submit_date"), jsonobj.getString("school_number"));

        }
        putEventDataList();

    }

    public void putEventDataList()
    {
        if(myDataBase.getEventData().getCount()>0)
        {
            eventList.clear();
            Cursor data = myDataBase.getEventData();
            while(data.moveToNext())
            {
                eventList.add(new EventsData(data.getString(1), data.getString(2), data.getString(3)
                        , data.getString(4), data.getString(5)));
            }
            getDataValues();
        }
        else
        {
            notAvailable.setVisibility(View.VISIBLE);
        }

    }

    private void getDataValues() {
        Event e;
        for(int i=0; i<eventList.size(); i++) {
            EventsData allEvent = eventList.get(i);
            e = new Event(Color.BLACK, Utils.getTimeInMili(allEvent.getEvent_date()+" 10:00:00"), allEvent);
                calendarView.addEvent(e);
        }
        eventList.clear();
        calendarView.setVisibility(View.VISIBLE);
        calendarView.showCalendar();

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(final Date dateClicked) {
                if(dateDoubleClick && !userDataSP.isStudent()){
                    if(selectedDate.getTime() == dateClicked.getTime()){
                        if(ServerConnect.checkInternetConenction(getActivity())) {
                            SimpleDateFormat dateFormater = new SimpleDateFormat(Utils.DATE_FORMAT);
                            String formatted = dateFormater.format(dateClicked);
                            loadEventFragDialog(formatted);
                        }
                        else
                        {
                            Snackbar.make(rootView, "No Internet Connection", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }else {
                    selectedDate = dateClicked;
                    dateDoubleClick = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dateDoubleClick = false;
                            selectedDate = null;
                        }
                    },1000);
                }
                eventList.clear();
                List<Event> listDataOnDate = (List<Event>) calendarView.getEvents(dateClicked);
                if(listDataOnDate.size() > 0) {
                    noEvents.setVisibility(View.GONE);
                    for (int i = 0; i < listDataOnDate.size(); i++) {
                        eventList.add((EventsData) listDataOnDate.get(i).getData());
                    }
                }else{
                    noEvents.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                getActivity().setTitle(dateFormatForMonth.format(firstDayOfNewMonth.getTime())+"");
                List<Event> li= calendarView.getEventsForMonth(firstDayOfNewMonth);
                adapter.clear();
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
            eventName.setText(currentItem.getEvent_name());
            eventDetail.setText(currentItem.getEvent_detail());
            return ItemView;
        }

    }
}
