package com.lead.infosystems.schooldiary.Main;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.ApplicationForm.ApplicationForm;
import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.NotificationData;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Events.EventAll;
import com.lead.infosystems.schooldiary.ExamTest.ExamDetails;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.Model_Paper.ModelQuestionPapers;
import com.lead.infosystems.schooldiary.Progress.Progress_Report;
import com.lead.infosystems.schooldiary.R;

import com.lead.infosystems.schooldiary.Generic.ServerConnect;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.SchoolDiary.StudentDiary_student;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.lead.infosystems.schooldiary.Main.MainActivity.BACK_STACK_TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragTabNotifications extends Fragment {

    private View rootview;
    private ListView list;
    private List<NotificationData> items = new ArrayList<NotificationData>();
    private ArrayAdapter adapter;
    private UserDataSP userDataSP;
    private MyDataBase myDataBase;
    private static boolean loaded = false;
    private ProgressBar progressBar;
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
        progressBar = (ProgressBar) rootview.findViewById(R.id.progressBar);
        myDataBase = new MyDataBase(getActivity().getApplicationContext());
        userDataSP = new UserDataSP(getActivity().getApplicationContext());
        if(ServerConnect.checkInternetConenction(getActivity())&& !loaded){
            getDataFromServer();
        }else{
            putDataIntoList();
        }
        setItemClick();
        getActivity().registerReceiver(receiver,new IntentFilter(MainTabAdapter.NOTIFICATION_BC_FILTER));
        return rootview;
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getDataFromServer();
        }
    };

    private void getDataFromServer(){
        progressBar.setVisibility(View.VISIBLE);
        MyVolley volley = new MyVolley(getActivity().getApplicationContext(), new IVolleyResponse() {
            @Override
            public void volleyResponse(String result) {
                myDataBase.clearNotifications();
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(result);
                    for(int i = 0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        myDataBase.incertNotification(jsonObject.getString("notification_number")
                                ,jsonObject.getString("date")
                                ,jsonObject.getString("class")
                                ,jsonObject.getString("division")
                                ,jsonObject.getString("notification_text").replace("+"," ")
                                ,jsonObject.getString("type"));
                    }
                    loaded = true;
                    putDataIntoList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
        volley.setUrl(Utils.NOTIFICATION_FETCH);
        UserDataSP user = new UserDataSP(getActivity().getApplicationContext());
        if(user.isStudent()) {
            volley.setParams(UserDataSP.CLASS, user.getUserData(UserDataSP.CLASS));
            volley.setParams(UserDataSP.DIVISION, user.getUserData(UserDataSP.DIVISION));
        }
        volley.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if( userDataSP.getNotificationNumber(UserDataSP.NOTIFICATION_NUM)>0){
            getDataFromServer();
        }
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
            Collections.sort(items,new MyComparator());
           adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(getActivity().getApplicationContext(),"No notifications",Toast.LENGTH_SHORT).show();
            //change....
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
            ((ImageView) itemView.findViewById(R.id.profile_image)).setVisibility(View.GONE);
            String title_text = current.getType().replace("_"," ");
            title.setText(title_text);
            date.setText(Utils.getTimeString(current.getDate()));
            text.setText(current.getNotificationText());
            return itemView;
        }
    }

    private class MyComparator implements Comparator<NotificationData> {

        @Override
        public int compare(NotificationData lhs, NotificationData rhs) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Utils.DATE_FORMAT);
            try {
                Date date1 = dateFormat.parse(lhs.getDate());
                Date date2 = dateFormat.parse(rhs.getDate());
                return date2.compareTo(date1);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
    private void setItemClick(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (items.get(position).getType()){
                    case NotificationData.HOME_WORK:
                        StudentDiary_student frag1 = new StudentDiary_student();
                        FragmentTransaction transaction1 = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction1.replace(R.id.main_con,frag1);
                        transaction1.addToBackStack(BACK_STACK_TAG);
                        transaction1.commit();
                        break;
                    case NotificationData.MARKS:
                        if(userDataSP.isStudent()){
                            Progress_Report frag = new Progress_Report();
                            FragmentTransaction transaction2 = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction2.replace(R.id.main_con,frag);
                            transaction2.addToBackStack(BACK_STACK_TAG);
                            transaction2.commit();
                        }
                        break;
                    case NotificationData.MODEL_QP:
                        ModelQuestionPapers frag2 = new ModelQuestionPapers();
                        FragmentTransaction transaction2 = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction2.replace(R.id.main_con,frag2);
                        transaction2.addToBackStack(BACK_STACK_TAG);
                        transaction2.commit();
                        break;
                    case NotificationData.TEST_EXAM:
                        ExamDetails frag3 = new ExamDetails();
                        FragmentTransaction transaction3 = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction3.replace(R.id.main_con,frag3);
                        transaction3.addToBackStack(BACK_STACK_TAG);
                        transaction3.commit();
                        break;
                    case NotificationData.EVENT:
                        EventAll frag4 = new EventAll();
                        FragmentTransaction transaction4 = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction4.replace(R.id.main_con,frag4);
                        transaction4.addToBackStack(BACK_STACK_TAG);
                        transaction4.commit();
                        break;
                    case NotificationData.APPLICATION_FORM:
                        ApplicationForm frag5 = new ApplicationForm();
                        FragmentTransaction transaction5 = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction5.replace(R.id.main_con,frag5);
                        transaction5.addToBackStack(BACK_STACK_TAG);
                        transaction5.commit();
                        break;
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            getActivity().unregisterReceiver(receiver);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
