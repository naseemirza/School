package com.lead.infosystems.schooldiary.Attendance;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.MyVolley;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.graphics.Color.RED;
import static android.graphics.Color.YELLOW;


public class Attendance_student extends Fragment implements IVolleyResponse{

    private MyVolley myVolley;
    TextView presentView, absentView, leavesView;
    UserDataSP userDataSP;
     CompactCalendarView calendarView;
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    List<AttendanceData> attendance = new ArrayList<>();


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
        myVolley = new MyVolley(getActivity().getApplicationContext(), this);
        getAttendanceData();
        return rootView;
    }


    public void getAttendanceData(){

        myVolley.setUrl(Utils.ATTENDANCE_FETCH);
        myVolley.setParams(UserDataSP.NUMBER_USER, userDataSP.getUserData(UserDataSP.NUMBER_USER));
        myVolley.connect();
    }

    @Override
    public void volleyResponce(String result) {

        try {

            getJsonData(result);
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);


        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            attendance.add(new AttendanceData(jsonobj.getString("year"), jsonobj.getString("day"), jsonobj.getString("month"), jsonobj.getString("attendance")));

        }
        getDataValues();
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
