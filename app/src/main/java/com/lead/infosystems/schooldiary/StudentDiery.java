package com.lead.infosystems.schooldiary;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.Data.DieryHomeWorkData;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentDiery extends Fragment {


    ListView list;
    private List<DieryHomeWorkData> items = new ArrayList<>();


    public StudentDiery() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_diery, container, false);
        list = (ListView) view.findViewById(R.id.list);
        return view;
    }

    private class MyAdaptor extends ArrayAdapter<DieryHomeWorkData>{
        public MyAdaptor() {
            super(getActivity().getApplicationContext(), R.layout.home_work_diery_item,items);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if(v ==null){
                v = getActivity().getLayoutInflater().inflate(R.layout.home_work_diery_item,parent,false);
            }

            DieryHomeWorkData current = items.get(position);

            TextView subject = (TextView) v.findViewById(R.id.subject);
            TextView given_date = (TextView) v.findViewById(R.id.given_date);
            TextView due_date = (TextView) v.findViewById(R.id.due_date);
            TextView text = (TextView) v.findViewById(R.id.text);

            // to  do

            return v;
        }
    }
}
