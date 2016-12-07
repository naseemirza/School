package com.lead.infosystems.schooldiary.Main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lead.infosystems.schooldiary.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainSearch extends Fragment {


    public MainSearch() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_search, container, false);
    }

}
