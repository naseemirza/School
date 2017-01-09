package com.lead.infosystems.schooldiary.Suggestion;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.Generic.ServerConnect;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.ramotion.foldingcell.FoldingCell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Suggestion_Complain extends Fragment implements IVolleyResponse {
    private FloatingActionButton button;
    private View rootView;
    ListView list;
    private ArrayList<sc_items> scItem;
    private MyVolley myVolley;
    private ProgressBar progressBar;
    private TextView notAvailable;
    private TextView noInternet;
    UserDataSP userDataSp;
    public Suggestion_Complain() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.suggestion__complain, container, false);

        userDataSp = new UserDataSP(getActivity());
        myVolley = new MyVolley(getActivity().getApplicationContext(), this);
        getActivity().setTitle("Suggestion_Complain");
        list = (ListView) rootView.findViewById(R.id.list_detail);
        progressBar = (ProgressBar)rootView.findViewById(R.id.suggestion_loading);
        notAvailable = (TextView)rootView.findViewById(R.id.suggestionNotAvailable);
        noInternet = (TextView)rootView.findViewById(R.id.suggestionNoInternet);
        button = (FloatingActionButton) rootView.findViewById(R.id.add);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.FragmentManager manager = getActivity().getFragmentManager();
                Suggestion_Post suggestion_post = new Suggestion_Post();
                suggestion_post.show(manager, "Suggestion_Post");
            }
        });
        checkInternetConnection();
        return rootView;
    }


    public void checkInternetConnection()
    {
        if(ServerConnect.checkInternetConenction(getActivity()))
        {
            progressBar.setVisibility(View.VISIBLE);
            getSuggestionData();
        }
        else
        {
            noInternet.setVisibility(View.VISIBLE);
        }
    }
    public void getSuggestionData() {
        myVolley.setUrl(Utils.SUGGESTION_COMPLAIN);
        myVolley.setParams(UserDataSP.SCHOOL_NUMBER, userDataSp.getUserData(UserDataSP.SCHOOL_NUMBER));
        myVolley.connect();
        Log.e("school",userDataSp.getUserData(UserDataSP.SCHOOL_NUMBER));
    }

    @Override
    public void volleyResponse(String result) {

        try {
            notAvailable.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            Log.e("result",result);
            getJsonData(result);
        } catch (JSONException e) {
            e.printStackTrace();
            notAvailable.setVisibility(View.VISIBLE);
        }

    }

    public void getJsonData(String re) throws JSONException{
        JSONArray json = new JSONArray(re);

        scItem = new ArrayList<>();

        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            scItem.add(new sc_items(jsonobj.getString(UserDataSP.FIRST_NAME),jsonobj.getString(UserDataSP.LAST_NAME),jsonobj.getString(UserDataSP.CLASS),jsonobj.getString(UserDataSP.DIVISION),jsonobj.getString("profilePic_link"),jsonobj.getString("subject"),jsonobj.getString("content"),jsonobj.getString("date")));

        }

        final FloadingListAdapter adapter = new FloadingListAdapter(getActivity(), scItem);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // toggle clicked cell state
                ((FoldingCell) view).toggle(false);
                // register in adapter that state for selected cell is toggled
                adapter.registerToggle(pos);
            }
        });
    }
}
