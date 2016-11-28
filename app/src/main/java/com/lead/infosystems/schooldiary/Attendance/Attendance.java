package com.lead.infosystems.schooldiary.Attendance;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.lead.infosystems.schooldiary.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Attendance extends Fragment {

    public Attendance() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attendance, container, false);
        Button button_tech = (Button) rootView.findViewById(R.id.button_tech);
        button_tech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getActivity(), com.lead.infosystems.schooldiary.Attendance.Teacher_Attendance.class);
                getActivity().startActivity(it);

            }
        });

        Button button_stud = (Button) rootView.findViewById(R.id.button_student);
        button_stud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getActivity(), com.lead.infosystems.schooldiary.Attendance.ShowAttendance.class);
                getActivity().startActivity(it);

            }
        });

        return rootView;
    }

}


