package com.lead.infosystems.schooldiary.Main;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.ServerConnect;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Faheem on 23-11-2016.
 */

public class QuestionDialog extends DialogFragment {

    View rootView;
    TextView questionText;
    Button submit;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.answer_dialog,container,false);
        getDialog().setTitle("New Question");
        questionText = (TextView) rootView.findViewById(R.id.question_text);
        submit = (Button) rootView.findViewById(R.id.submit);

        questionText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(questionText.getText().length() > 0){
                    submitQuestion(questionText.getText().toString());
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"No text..",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }

    private void submitQuestion(final String questionText){
        final UserDataSP userDataSP = new UserDataSP(getActivity().getApplicationContext());
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        final StringRequest request = new StringRequest(Request.Method.POST, Utils.Q_SUBMIT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response != null){
                    if(!response.contains("ERROR")){
                        try {
                            getDialog().dismiss();
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            FragTabQA.addItem(questionText,jsonObject.getString("question_number")
                                    ,jsonObject.getString("date"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),ServerConnect.connectionError(error),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("student_number", userDataSP.getUserData(UserDataSP.STUDENT_NUMBER));
                map.put("question",questionText);
                return map;
            }
        };
        RetryPolicy retryPolicy = new DefaultRetryPolicy(2000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        requestQueue.add(request);
    }
}
