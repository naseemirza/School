package com.lead.infosystems.schooldiary.Main;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.Post_Data;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class FragTabHome extends Fragment implements AdapterItem.OnLoadMoreListener,SwipeRefreshLayout.OnRefreshListener{

    View rootview;
    private ArrayList<Post_Data> itemlist = new ArrayList<Post_Data>();;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdapterItem adapterItem;
    String POST_MIN = "0";
    Activity activity;
    UserDataSP userDataSP;
    JSONArray jsonPost,jsonLikes;
    boolean backPressed = false;

    public FragTabHome() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        if(ServerConnect.checkInternetConenction(getActivity())&& !backPressed){
             loadData(getActivity());
        }else{
            if(userDataSP.getPostData()!=""){
                swipeRefreshLayout.setRefreshing(false);
                adapterItem.addAll(getJsonData(userDataSP.getPostData()));
            }
        }
        super.onStart();
    }

    @Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.frag_tab_home, container, false);
        Log.e("onCreate","called");
        swipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.swipeRefresh);
        RecyclerView recyclerView = (RecyclerView) rootview.findViewById(R.id.rvList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        activity = getActivity();
        adapterItem = new AdapterItem(this, getActivity());
        adapterItem.setLinearLayoutManager(linearLayoutManager);
        adapterItem.setRecyclerView(recyclerView);
        recyclerView.setAdapter(adapterItem);
        recyclerView.getItemAnimator().setChangeDuration(0);
        userDataSP = new UserDataSP(getActivity().getApplicationContext());
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(this);
        return rootview;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void loadData(Activity activity) {
        if(ServerConnect.checkInternetConenction(activity)){
            new PostDataLoad().execute("0");
        }
    }

    @Override
    public void onLoadMore() {
            adapterItem.setProgressMore(true);
        if(ServerConnect.checkInternetConenction(getActivity())){
            new PostDataLoad().execute(POST_MIN);
        }
    }

    @Override
    public void onCommentClick(String post_id) {
        backPressed = true;
        startActivity(new Intent(getActivity(),PostComments.class));
    }


    @Override
    public void onRefresh() {
        POST_MIN = "0";
        loadData(getActivity());
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
        if(s != null && s != "ERROR") {
            itemlist.clear();
            if (POST_MIN == "0") {
                swipeRefreshLayout.setRefreshing(false);
                adapterItem.addAll(getJsonData(s));
                userDataSP.storePostData(s);
            } else {
                adapterItem.setProgressMore(false);
                adapterItem.addItemMore(getJsonData(s));
                adapterItem.setMoreLoading(false);
                userDataSP.appendToPostData(s);
            }

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
                //parse date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date date = dateFormat.parse(jsonPostObj.getString("date"));

                //get likes list
                likedStudents = new ArrayList<>();
                if(jsonPostObj.getString("like") != "null"){
                jsonLikes = new JSONArray(jsonPostObj.getString("like"));
                    for(int j = 0 ; j < jsonLikes.length();j++){
                        jsonLikeObj = jsonLikes.getJSONObject(j);
                        String sNo = jsonLikeObj.getString("student_number");
                        if(Integer.parseInt(sNo) == Integer.parseInt(userDataSP.getUserData(UserDataSP.STUDENT_NUMBER)))
                        {isLiked = true;}
                        likedStudents.add(sNo);
                    }
                }

                itemlist.add(new Post_Data(jsonPostObj.getString("first_name"), jsonPostObj.getString("last_name"),
                                            jsonPostObj.getString("post_id"), jsonPostObj.getString("text_message"),
                                            jsonPostObj.getString("src_link"), date.getTime(),isLiked,likedStudents,"5"));
                if (i == jsonPost.length() - 1) {
                    POST_MIN = jsonPostObj.getString("post_id");
                }
            }
            return itemlist;
        } catch (JSONException e) {
            e.printStackTrace();
            itemlist.clear();
            Toast.makeText(getActivity().getApplicationContext(),"No more posts...",Toast.LENGTH_SHORT).show();
            return itemlist;
        } catch (ParseException e) {
            e.printStackTrace();
            itemlist.clear();
            return itemlist;
        }
    }
}