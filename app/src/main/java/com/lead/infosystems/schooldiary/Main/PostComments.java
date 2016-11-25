package com.lead.infosystems.schooldiary.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
import com.lead.infosystems.schooldiary.Data.PostCommentData;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.ServerConnect;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostComments extends AppCompatActivity {

    private ImageView post_img,propic;
    private TextView name,time,text,commentNoComment;
    private EditText commentText;
    private ProgressBar progressBar;
    private MyAdaptor adaptor;
    private boolean canClickLike = true;
    private UserDataSP user;
    Activity activity = this;
    private ExpandableHeightListView commentsList;
    PostAnimData postAnimData = FragTabHome.postAnimData;
    private ArrayList<PostCommentData> items = new ArrayList<>();

    PostComments(){}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comments);
        getSupportActionBar().setTitle("Comment");

        propic = (ImageView) findViewById(R.id.propic);
        post_img = (ImageView) findViewById(R.id.postimage);
        name = (TextView) findViewById(R.id.name);
        time = (TextView) findViewById(R.id.time);
        text = (TextView) findViewById(R.id.question_text);
        commentsList = (ExpandableHeightListView) findViewById(R.id.comment_list);
        commentText = (EditText) findViewById(R.id.comment_text);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView2);
        commentNoComment = (TextView) findViewById(R.id.comment_nocomment);
        progressBar = (ProgressBar) findViewById(R.id.comment_progress);

        user = new UserDataSP(getApplicationContext());
        commentText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                return false;
            }

        });
        populateViews();

        commentsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final CharSequence[] item = { "Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                dialog.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        connect(Utils.DELETE,position);
                    }
                });
                dialog.show();
                return false;
            }
        });

    }


    private void populateViews(){
        propic.setImageDrawable(((ImageView) postAnimData.getPropic()).getDrawable());
        name.setText(((TextView) postAnimData.getName()).getText());
        time.setText(((TextView) postAnimData.getTime()).getText());
        text.setText(((TextView) postAnimData.getText()).getText());


        if(postAnimData.isImageAvailable()){
            post_img.setVisibility(View.VISIBLE);
            post_img.setImageBitmap(postAnimData.getPostImageBitmap());
        }else{
            post_img.setVisibility(View.GONE);
        }
        connect(Utils.COMMENTS,0);
    }


    public void commentBtn(View v){
        if(commentText.getText().length()>0){
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest request = new StringRequest(Request.Method.POST, Utils.LIKE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response != ""){
                        if(response.contains("DONE")){

                            // add only one item
                            connect(Utils.COMMENTS,0);
                            commentText.setText("");
                            Snackbar.make(findViewById(android.R.id.content),"Long press to delete your comment",Snackbar.LENGTH_LONG)
                                    .show();
                            commentNoComment.setText("Comments");
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),ServerConnect.connectionError(error),Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    UserDataSP userDataSP = new UserDataSP(getApplicationContext());
                    HashMap<String,String> map = new HashMap<>();
                    map.put("student_number", userDataSP.getUserData(UserDataSP.STUDENT_NUMBER));
                    map.put("post_id", postAnimData.getPostID());
                    map.put("comment_text",commentText.getText().toString());
                    return map;
                }
            };

            RetryPolicy policy = new DefaultRetryPolicy(2000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            request.setRetryPolicy(policy);
            requestQueue.add(request);
        }else{
            Toast.makeText(getApplicationContext(),"No Comment text...",Toast.LENGTH_SHORT).show();
        }
    }

    private void connect(final String url, final int position){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response != ""){
                    if(url == Utils.COMMENTS){
                        if(response != "NULL"){
                            items.clear();
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for(int i = 0 ;i<jsonArray.length();i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    items.add(new PostCommentData(jsonObject.getString("student_name"),
                                            Utils.getTimeString(jsonObject.getString("date")),
                                            jsonObject.getString("comment_text"),jsonObject.getString("like"),
                                            jsonObject.getString("comment_id"),jsonObject.getString("user_liked")));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                commentNoComment.setText("No comments");
                            }
                            adaptor = new MyAdaptor();
                            commentsList.setAdapter(adaptor);
                            commentsList.setExpanded(true);
                        }
                    }else if(url == Utils.DELETE){
                        if(response.contains("DONE")){
                            items.remove(position);
                            adaptor.notifyDataSetChanged();

                    }
                }
                }else{
                    commentNoComment.setText("No comments");
                }
                if(items.size()>0){
                    commentNoComment.setText("Comments");
                }else{
                    commentNoComment.setText("No comments");
                }
                progressBar.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               Toast.makeText(getApplicationContext(),ServerConnect.connectionError(error),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                UserDataSP userDataSP = new UserDataSP(getApplicationContext());
                HashMap<String,String> map = new HashMap<>();
                if(url == Utils.COMMENTS){
                    map.put("post_id", postAnimData.getPostID());
                    map.put("student_number", userDataSP.getUserData(UserDataSP.STUDENT_NUMBER));
                } else if(url == Utils.DELETE){
                    map.put("comment_id", items.get(position).getComment_id());
                }
                return map;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(2000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);
    }

    class MyAdaptor extends ArrayAdapter<PostCommentData>{

        public MyAdaptor() {
            super(getApplicationContext(), R.layout.post_comment_item,items);
        }
        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView==null){
                itemView = getLayoutInflater().inflate(R.layout.post_comment_item,parent,false);
            }

            TextView comment_name = (TextView) itemView.findViewById(R.id.name);
            TextView time = (TextView) itemView.findViewById(R.id.time);
            TextView text = (TextView) itemView.findViewById(R.id.question_text);
            final TextView likes = (TextView) itemView.findViewById(R.id.comment_likes_num);
            final LinearLayout likeView = (LinearLayout) itemView.findViewById(R.id.comments_likes);
            final TextView Like_btn = (TextView) itemView.findViewById(R.id.like);

            final PostCommentData currentItem = items.get(position);
            comment_name.setText(currentItem.getName());
            time.setText(currentItem.getTime());
            text.setText(currentItem.getText());
            likes.setText(currentItem.getLikes()+"");
            setLikeView(currentItem.getLikes(),likes,likeView);

            Like_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(!currentItem.isUser_liked()) {
                        connect(Utils.LIKE,currentItem.getComment_id(),Like_btn,currentItem,likeView,likes);
                    }else{
                        connect(Utils.DELETE,currentItem.getComment_id(),Like_btn,currentItem,likeView,likes);
                    }
                }
            });
            return itemView;
        }



    }
    private void connect(final String url, final String commentNum, final TextView like_btn
            , final PostCommentData currentItem, final LinearLayout likeView, final TextView likes){
        RequestQueue requestQueue = Volley.newRequestQueue(activity.getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response != null && response.contains("DONE")){
                    if(url == Utils.LIKE){
                        like_btn.setText("Unlike");
                        currentItem.setUser_liked(true);
                        currentItem.setLikes(currentItem.getLikes() + 1);
                    }else if(url == Utils.DELETE){
                        like_btn.setText("Like");
                        currentItem.setUser_liked(false);
                        currentItem.setLikes(currentItem.getLikes() - 1);
                    }
                }
                setLikeView(currentItem.getLikes(),likes,likeView);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity.getApplicationContext(),ServerConnect.connectionError(error),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("student_number",user.getUserData(UserDataSP.STUDENT_NUMBER));
                map.put("comment_id",commentNum);
                return map;
            }
        };
        RetryPolicy retry = new DefaultRetryPolicy(2000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retry);
        requestQueue.add(request);
    }

    private void setLikeView(int likeNum,TextView likes,LinearLayout likeView){
        if(likeNum > 0){
            likes.setText(likeNum+"");
            likeView.setVisibility(View.VISIBLE);
        }else{
            likeView.setVisibility(View.INVISIBLE);
        }
    }
}
