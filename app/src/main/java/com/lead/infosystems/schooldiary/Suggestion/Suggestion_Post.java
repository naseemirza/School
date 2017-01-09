package com.lead.infosystems.schooldiary.Suggestion;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;

public class Suggestion_Post extends DialogFragment {
    private MyVolley myVolley;
    EditText edit_content;
    EditText edit_title;
    public Button sugg_post;
    UserDataSP userdatasp;

    View rootview;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.suggestion_post, null);

        sugg_post=(Button)rootview.findViewById(R.id.button_post);
        edit_title=(EditText)rootview.findViewById(R.id.editText_title);
        edit_content=(EditText)rootview.findViewById(R.id.editText_content);
        userdatasp=new UserDataSP(getActivity().getApplicationContext());
        getDialog().setTitle("New Suggestion");
        sugg_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 InsertData();
            }
        });

        return rootview;
    }

    private void InsertData() {
        myVolley = new MyVolley(getActivity().getApplicationContext(), new IVolleyResponse() {
            @Override
            public void volleyResponse(String result) {
                if (result.contains("Done")){
                    Toast.makeText(getActivity().getApplicationContext(),"Done", Toast.LENGTH_SHORT).show();
                }

            }
        });
        myVolley.setUrl(Utils.SUGGESTION_COMPLAIN);
        myVolley.setParams("title",edit_title.getText().toString());
        myVolley.setParams("content",edit_content.getText().toString());
        myVolley.setParams(UserDataSP.NUMBER_USER,userdatasp.getUserData(UserDataSP.NUMBER_USER));
        myVolley.setParams(UserDataSP.SCHOOL_NUMBER,userdatasp.getUserData(UserDataSP.SCHOOL_NUMBER));
        myVolley.connect();
        getDialog().dismiss();
    }





}
