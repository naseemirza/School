package com.lead.infosystems.schooldiary.Management;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lead.infosystems.schooldiary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManagmentSchool extends Fragment {


    public ManagmentSchool() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.managment_school, container, false);
    }

}
