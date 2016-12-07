package com.lead.infosystems.schooldiary.Profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.Post_Data;
import com.lead.infosystems.schooldiary.Data.QuestionData;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Main.FragTabHome;
import com.lead.infosystems.schooldiary.Main.PostAdaptor;
import com.lead.infosystems.schooldiary.Main.PostAnimData;
import com.lead.infosystems.schooldiary.Main.PostComments;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.ServerConnect;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Profile extends AppCompatActivity implements PostAdaptor.OnLoadMoreListener,SwipeRefreshLayout.OnRefreshListener {

    private static ArrayList<Post_Data> itemlist = new ArrayList<Post_Data>();;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static PostAdaptor postAdaptor;
    String POST_MIN = "0";
    static UserDataSP userDataSP;
    JSONArray jsonPost,jsonLikes;
    public static PostAnimData postAnimData;
    RecyclerView recyclerView;
    boolean backPressed = false;
    boolean noMorePost = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        userDataSP = new UserDataSP(getApplicationContext());
        toolbar.setTitle(userDataSP.getUserData(UserDataSP.FIRST_NAME)+" "+userDataSP.getUserData(UserDataSP.LAST_NAME));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initilize();
    }

    private void initilize(){
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        postAdaptor = new PostAdaptor(this, this);
        postAdaptor.setLinearLayoutManager(linearLayoutManager);
        postAdaptor.setRecyclerView(recyclerView);
        recyclerView.setAdapter(postAdaptor);
        recyclerView.getItemAnimator().setChangeDuration(0);
        userDataSP = new UserDataSP(getApplicationContext());
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(this);
    }
    public void loadData(Activity activity) {
        if(ServerConnect.checkInternetConenction(activity)){
            new PostDataLoad().execute("0");
        }
    }

    @Override
    public void onStart() {
        itemlist.clear();
        noMorePost = false;
        if(ServerConnect.checkInternetConenction(this)&& !backPressed){
            loadData(this);
        }else{
            if(userDataSP.getPostData()!=""){
                swipeRefreshLayout.setRefreshing(false);
                postAdaptor.addAll(getJsonData(userDataSP.getPostData()));
            }
        }
        super.onStart();
    }


    private class PostDataLoad extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            Uri.Builder builder = new Uri.Builder();
            builder.appendQueryParameter(Utils.POST_FETCH_PARAM, params[0]);
            builder.appendQueryParameter(UserDataSP.NUMBER_USER,userDataSP.getUserData(UserDataSP.NUMBER_USER));
            String query = builder.build().getQuery();
            try {
                return ServerConnect.downloadUrl(Utils.POST_FETCH, query);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                storeData(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private void storeData(String s) throws JSONException {
        if(!s.isEmpty() && s != "ERROR") {
            itemlist.clear();
            if (POST_MIN == "0") {
                swipeRefreshLayout.setRefreshing(false);
                postAdaptor.addAll(getJsonData(s));
                userDataSP.storePostData(s);
            } else {
                postAdaptor.setProgressMore(false);
                postAdaptor.addItemMore(getJsonData(s));
                postAdaptor.setMoreLoading(false);
                userDataSP.appendToPostData(s);
            }

        }else{
            Toast.makeText(getApplicationContext(),"No more posts...",Toast.LENGTH_SHORT).show();
            noMorePost = true;
            postAdaptor.setProgressMore(false);
            postAdaptor.setMoreLoading(false);
        }
    }
    private ArrayList<Post_Data> getJsonData(String jsonString){

        JSONObject jsonPostObj = null;
        JSONObject jsonLikeObj = null;
        ArrayList<String> likedStudents = new ArrayList<>();
        try {
            jsonPost = new JSONArray(jsonString);
            for (int i = 0; i <= jsonPost.length() - 1; i++) {
                boolean isLiked = false;
                jsonPostObj = jsonPost.getJSONObject(i);
                //get likes list
                likedStudents = new ArrayList<>();
                if(jsonPostObj.getString("like") != "null"){
                    jsonLikes = new JSONArray(jsonPostObj.getString("like"));
                    for(int j = 0 ; j < jsonLikes.length();j++){
                        jsonLikeObj = jsonLikes.getJSONObject(j);
                        String sNo = jsonLikeObj.getString("number_user");
                        if(Integer.parseInt(sNo) == Integer.parseInt(userDataSP.getUserData(UserDataSP.NUMBER_USER)))
                        {isLiked = true;}
                        likedStudents.add(sNo);
                    }
                }

                itemlist.add(new Post_Data(jsonPostObj.getString("number_user"),jsonPostObj.getString("first_name"), jsonPostObj.getString("last_name"),
                        jsonPostObj.getString("post_id"), jsonPostObj.getString("text_message"),
                        jsonPostObj.getString("src_link"), jsonPostObj.getString("date"),isLiked,likedStudents,
                        jsonPostObj.getString("num_comment")));
                if (i == jsonPost.length() - 1) {
                    POST_MIN = jsonPostObj.getString("post_id");
                }
            }
            return itemlist;
        } catch (JSONException e) {
            e.printStackTrace();
            itemlist.clear();
            return itemlist;
        }
    }
    @Override
    public void onRefresh() {
        POST_MIN = "0";
        noMorePost = false;
        postAdaptor.setMoreLoading(false);
        loadData(this);
    }

    @Override
    public void onLoadMore() {
        if(ServerConnect.checkInternetConenction(this) && !noMorePost){
            new PostDataLoad().execute(POST_MIN);
            postAdaptor.setProgressMore(true);
        }
    }

    @Override
    public void onCommentClick(PostAnimData postAnimData) {
        backPressed = true;
        Intent intent = new Intent(getApplicationContext(),PostComments.class);
        ActivityOptionsCompat activityOptionsCompat = null;
        this.postAnimData = postAnimData;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Pair<View,String> p1 = Pair.create(postAnimData.getName(), postAnimData.getName().getTransitionName());
            Pair<View,String> p2 = Pair.create(postAnimData.getTime(), postAnimData.getTime().getTransitionName());
            Pair<View,String> p3 = Pair.create(postAnimData.getText(), postAnimData.getText().getTransitionName());
            Pair<View,String> p4 = Pair.create(postAnimData.getPropic(), postAnimData.getPropic().getTransitionName());
            if(postAnimData.isImageAvailable()){
                Pair<View,String> p5 = Pair.create(postAnimData.getPost_img(), postAnimData.getPost_img().getTransitionName());
                activityOptionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(this,p1,p2,p3,p4,p5);
            }else{
                activityOptionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(this,p1,p2,p3,p4);
            }
            startActivity(intent,activityOptionsCompat.toBundle());
        }else{
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile,menu);
        return true;
    }
}
