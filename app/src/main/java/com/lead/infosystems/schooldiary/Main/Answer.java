package com.lead.infosystems.schooldiary.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.lead.infosystems.schooldiary.Data.AnswerData;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.ServerConnect;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Answer extends AppCompatActivity {

    private ImageView propic;
    private TextView name,time,text,answer;
    private ExpandableHeightListView list;
    private EditText answerText;
    private FloatingActionButton answerBtn;
    protected QaAnimData qaAnimData;
    private ProgressBar progressBar;
    private Activity activity = this;
    private MyAdaptor adaptor;
    UserDataSP userDataSP;
    private List<AnswerData> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        getSupportActionBar().setTitle("Answers");
        propic = (ImageView) findViewById(R.id.profile_image);
        name = (TextView) findViewById(R.id.name);
        text = (TextView) findViewById(R.id.question_text);
        time = (TextView) findViewById(R.id.time_rcv);
        list = (ExpandableHeightListView) findViewById(R.id.list);
        progressBar = (ProgressBar) findViewById(R.id.answers_progress);
        answer = (TextView) findViewById(R.id.answer_noanswer);
        answerText = (EditText) findViewById(R.id.answer_text);
        answerBtn = (FloatingActionButton) findViewById(R.id.answerBtn);
        userDataSP = new UserDataSP(getApplicationContext());

        populateView();

        answerText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                answerText.setFocusable(true);
                answerText.setFocusableInTouchMode(true);
                return false;
            }
        });

        answerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answerText.getText().length()>0){
                    connect(Utils.ANSWER_SUBMIT,answerText.getText().toString(),null,null,null, 0);
                    answerText.setText("");
                }else{
                    Toast.makeText(getApplicationContext(),"No answer Text..",Toast.LENGTH_SHORT).show();
                }
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.e("1",items.get(position).getStudentNumber());
                Log.e("2",userDataSP.getUserData(UserDataSP.NUMBER_USER));
                if(Integer.parseInt(items.get(position).getStudentNumber()) == Integer.parseInt(userDataSP.getUserData(UserDataSP.NUMBER_USER))){
                    final CharSequence[] item = { "Delete"};
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setItems(item, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            connect(Utils.QA_DELETE,null,items.get(position).getStudentNumber(),
                                    items.get(position).getAnswerNumber(),qaAnimData.getQuestionNum(),position);
                        }
                    });
                    dialog.show();
                }
                return false;
            }
        });
    }


    private void populateView(){
        qaAnimData = FragTabQA.qaAnimData;
        propic.setImageDrawable(qaAnimData.getPropic().getDrawable());
        name.setText(qaAnimData.getName().getText().toString());
        text.setText(qaAnimData.getText().getText().toString());
        time.setText(qaAnimData.getTime().getText().toString());
        connect(Utils.QA_FETCH,null, null, null, null, 0);
    }
    private void connect(final String url, final String answerText, final String studentNumber, final String answerNumber, final String questionNum, final int position){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response != ""){
                    if(url == Utils.QA_FETCH){
                        parseJson(response);
                        adaptor = new MyAdaptor();
                        list.setAdapter(adaptor);
                        list.setExpanded(true);
                    }else if(url == Utils.ANSWER_SUBMIT){
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            addItem(jsonObject.getString("answer_number"),Utils.getTimeString(jsonObject.getString("date")),answerText);
                            Snackbar.make(findViewById(android.R.id.content),"Long press to delete",Snackbar.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(url == Utils.QA_DELETE){
                        if(response.contains("DONE")){
                            deleteItem(position);
                        }
                    }
                }
                doneLoading();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), ServerConnect.connectionError(error),Toast.LENGTH_SHORT).show();
                doneLoading();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();

                if(answerText == null && answerNumber == null){
                    params.put("question_number",qaAnimData.getQuestionNum().trim());
                }else if(answerNumber == null){
                    params.put(UserDataSP.NUMBER_USER,userDataSP.getUserData(UserDataSP.NUMBER_USER));
                    params.put("question_number",qaAnimData.getQuestionNum());
                    params.put("answer",answerText);
                }else if(answerNumber != null && studentNumber != null){
                    params.put("question_number",questionNum);
                    params.put("number_user",studentNumber);
                    params.put("answer_number",answerNumber);
                }
                return params;
            }
        };
        RetryPolicy retryPolicy = new DefaultRetryPolicy(2000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        requestQueue.add(request);
    }

    private void deleteItem(int position) {
        items.remove(position);
        adaptor.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
    }

    private void addItem(String answer_number, String time, String answerText) {
        items.add(new AnswerData(userDataSP.getUserData(UserDataSP.NUMBER_USER),answer_number
                ,userDataSP.getUserData(UserDataSP.FIRST_NAME)+" "+
                userDataSP.getUserData(UserDataSP.LAST_NAME),time,answerText,null));
        adaptor.notifyDataSetChanged();
    }

    private void doneLoading(){
        progressBar.setVisibility(View.GONE);
        if(items.size()>0){
            answer.setText("Answers");
        }else{
            answer.setText("No Answers");
        }
    }
    class MyAdaptor extends ArrayAdapter<AnswerData>{

        public MyAdaptor() {
            super(getApplicationContext(), R.layout.answer_item,items);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView name,time,text;
            ImageView propic;
            View v = convertView;
            if(v == null){
                v = getLayoutInflater().inflate(R.layout.answer_item,parent,false);
            }
            name = (TextView) v.findViewById(R.id.name);
            time = (TextView) v.findViewById(R.id.time_rcv);
            text = (TextView) v.findViewById(R.id.question_text);
            propic = (ImageView) v.findViewById(R.id.profile_image);

            AnswerData currentItem = items.get(position);
            name.setText(currentItem.getName().toString());
            text.setText(currentItem.getText().toString());
            time.setText(currentItem.getTime().toString());

            return v;
        }
    }
    private void parseJson(String data){
        items.clear();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for(int i = 0 ; i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                items.add(new AnswerData(jsonObject.getString("number_user"),jsonObject.getString("answer_number")
                        ,jsonObject.getString("student_name")
                        ,Utils.getTimeString(jsonObject.getString("date"))
                        ,jsonObject.getString("answer_text"),null));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
