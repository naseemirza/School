package com.lead.infosystems.schooldiary.Main;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.Data.QuestionData;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragTabNotifications extends Fragment {

    View rootview;
    ListView list;
    List<QuestionData> items = new ArrayList<QuestionData>();
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


    //////////////////
    private void populateListView(){
        items.add(new QuestionData("Umar Mujale","1 hour ago","New Assignment for maths E2.3 questions",getResources().getDrawable(R.drawable.student1)));
        items.add(new QuestionData("Ramesh Bhat","2 hour ago","No English Homework 2moro",getResources().getDrawable(R.drawable.student2)));
        items.add(new QuestionData("Naseem Mirza","3 hour ago","Day after 2moro is holiday",getResources().getDrawable(R.drawable.student3)));
        items.add(new QuestionData("Suresh Patil","4 hour ago","Social Science 2nd chapter questions as homework",getResources().getDrawable(R.drawable.student4)));
        items.add(new QuestionData("Nitin Gauda","5 hour ago","2moro is english extra class after 2pm",getResources().getDrawable(R.drawable.student2)));
        items.add(new QuestionData("Abrar Khan","6 hour ago","Day after 2moro white dress code",getResources().getDrawable(R.drawable.student7)));
        adapter = new MyListAdapter();
        list.setAdapter(adapter);

    }

    private class MyListAdapter extends ArrayAdapter<QuestionData>{

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
            QuestionData item = items.get(position);

            TextView name = (TextView) itemView.findViewById(R.id.name);
            name.setText(item.getname());

            TextView date = (TextView) itemView.findViewById(R.id.date);
            date.setText(item.getDate());

            TextView message = (TextView) itemView.findViewById(R.id.question_text);
            message.setText(item.getQuestion());

            ImageView propic = (ImageView) itemView.findViewById(R.id.propic);
            propic.setImageDrawable(item.getDrawable());

            return itemView;
        }
    }
}
