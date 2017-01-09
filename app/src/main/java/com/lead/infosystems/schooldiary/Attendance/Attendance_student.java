package com.lead.infosystems.schooldiary.Attendance;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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


public class Attendance_student extends Fragment implements IVolleyResponse{

    private MyVolley myVolley;
    private UserDataSP userDataSP;
    private MyDataBase myDatabase;
    private ProgressBar progressBar;
    private TextView notAvailable;
    CompactCalendarView calendarView;
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    List<AttendanceData> attendance = new ArrayList<>();
    TextView presentView, absentView, leavesView;
     public final int RED= Color.RED;
    public final int YELLOW = Color.YELLOW;


    public Attendance_student() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attendance_student, container, false);
        userDataSP=new UserDataSP(getActivity());
        calendarView = (CompactCalendarView)rootView.findViewById(R.id.compactcalendar_view);
        getActivity().setTitle("Attendance");
        getActivity().setTitle(dateFormatForMonth.format(calendarView.getFirstDayOfCurrentMonth()));
        presentView = (TextView)rootView.findViewById(R.id.total_present);
        absentView = (TextView)rootView.findViewById(R.id.total_absent);
        leavesView= (TextView)rootView.findViewById(R.id.total_leaves);
        progressBar = (ProgressBar)rootView.findViewById(R.id.attendance_loading);
        notAvailable = (TextView)rootView.findViewById(R.id.attendanceStdNotAvailable);
        myVolley = new MyVolley(getActivity().getApplicationContext(), this);
        myDatabase = new MyDataBase(getActivity().getApplicationContext());
        checkInternetConnection();
        return rootView;


    }
     public void checkInternetConnection()
     {
         if(ServerConnect.checkInternetConenction(getActivity()))
         {
             getAttendanceData();
         }
         else
         {
             putAttendanceIntoList();
         }
     }

    public void getAttendanceData(){
        myDatabase.clearContacts();
        attendance.clear();
        progressBar.setVisibility(View.VISIBLE);
        myVolley.setUrl(Utils.ATTENDANCE_FETCH);
        myVolley.setParams(UserDataSP.NUMBER_USER, userDataSP.getUserData(UserDataSP.NUMBER_USER));
        myVolley.connect();
    }

    @Override
    public void volleyResponse(String result) {

        try {
            progressBar.setVisibility(View.GONE);
            notAvailable.setVisibility(View.GONE);
            JSONArray json = new JSONArray(result);
            myDatabase.clearAttendanceData();
            for (int i = 0; i <= json.length() - 1; i++) {
                JSONObject jsonobj = json.getJSONObject(i);
                myDatabase.insertAttendanceData(jsonobj.getString("year"), jsonobj.getString("day"), jsonobj.getString("month"), jsonobj.getString("attendance"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
            notAvailable.setVisibility(View.VISIBLE);
        }
        putAttendanceIntoList();
    }

    public void putAttendanceIntoList()
    {   if(myDatabase.getAttendanceData().getCount()>0)
    {
        attendance.clear();
        Cursor data = myDatabase.getAttendanceData();
        while (data.moveToNext())
        {
            attendance.add(new AttendanceData(data.getString(1), data.getString(2), data.getString(3), data.getString(4)));
        }
        getDataValues();
    }
        else
    {
        notAvailable.setVisibility(View.VISIBLE);
    }


    }

    private void getDataValues()
    {
        Event e;
      for(int i=0; i<attendance.size(); i++) {
          AttendanceData allAttendance = attendance.get(i);
              if(allAttendance.getAttendance().contains("A")) {

                  e = new Event(RED, allAttendance.getTimeInMili(), allAttendance.getAttendance());
                  calendarView.addEvent(e);
              }
              else if(allAttendance.getAttendance().contains("L"))
              {
                  e = new Event(YELLOW, allAttendance.getTimeInMili(), allAttendance.getAttendance());
                  calendarView.addEvent(e);
              }
              else
              {

                  e = new Event(Color.TRANSPARENT,allAttendance.getTimeInMili(), allAttendance.getAttendance());
                  calendarView.addEvent(e);
              }
          }

        calendarView.setVisibility(View.VISIBLE);
        calendarView.showCalendar();
        List<Event> list = (List<Event>) calendarView.getEventsForMonth(calendarView.getFirstDayOfCurrentMonth());
        getMonthData(list);

       //calendarView.refreshDrawableState();
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Toast.makeText(getActivity(), "There is no data", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                getActivity().setTitle(dateFormatForMonth.format(firstDayOfNewMonth.getTime())+"");
                List<Event> li= calendarView.getEventsForMonth(firstDayOfNewMonth);
                getMonthData(li);
                Log.e("event", String.valueOf(li));

            }
        });




    }


private void getMonthData(List<Event> li)
{
    int present=0, absent=0, leaves=0;
    for(int j=0; j<li.size(); j++)
    {
        if(li.get(j).getColor()==RED)
        {
            absent++;
        }
        else if(li.get(j).getColor()==YELLOW)
        {
            leaves++;
        }
        else
        {
            present++;
        }
    }

    presentView.setText("Total Present:"+" "+present+"");
    absentView.setText("Total Absent:"+" "+absent+"");
    leavesView.setText("Total Leaves:"+" "+leaves+"");

}
}
