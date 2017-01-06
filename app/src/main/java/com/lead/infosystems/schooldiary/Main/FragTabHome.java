package com.lead.infosystems.schooldiary.Main;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.Post_Data;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.IPostInterface;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.Generic.ServerConnect;
import com.lead.infosystems.schooldiary.Generic.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragTabHome extends Fragment implements IPostInterface,SwipeRefreshLayout.OnRefreshListener{

    View rootview;
    private static ArrayList<Post_Data> itemlist = new ArrayList<Post_Data>();;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static PostAdaptor postAdaptor;
    private String POST_MIN = "0";
    static UserDataSP userDataSP;
    private JSONArray jsonPost,jsonLikes;
    boolean backPressed = false;
    boolean noMorePost = false;

    public FragTabHome() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        itemlist.clear();
        noMorePost = false;
        if(ServerConnect.checkInternetConenction(getActivity())&& !backPressed){
            loadData(getActivity());
        }else{
            if(userDataSP.getPostData()!=""){
                swipeRefreshLayout.setRefreshing(false);
                postAdaptor.addAll(getJsonData(userDataSP.getPostData()));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        backPressed = true;
    }

    @Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.frag_tab_home, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.swipeRefresh);
        RecyclerView recyclerView = (RecyclerView) rootview.findViewById(R.id.rvList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        postAdaptor = new PostAdaptor(this, getActivity());
        postAdaptor.setLinearLayoutManager(linearLayoutManager);
        postAdaptor.setRecyclerView(recyclerView);
        recyclerView.setAdapter(postAdaptor);
        recyclerView.getItemAnimator().setChangeDuration(0);
        userDataSP = new UserDataSP(getActivity().getApplicationContext());
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(this);
        return rootview;
    }

    public void loadData(Activity activity) {
        if(ServerConnect.checkInternetConenction(activity)){
            POST_MIN = "0";
            postDataLoad(POST_MIN);
        }
    }

    @Override
    public void onLoadMore() {
        if(ServerConnect.checkInternetConenction(getActivity()) && !noMorePost){
            postDataLoad(POST_MIN);
            postAdaptor.setProgressMore(true);
        }
    }

    @Override
    public void onCommentClick(PostAnimData postAnimData) {
        backPressed = true;
        Intent intent = new Intent(getActivity(),PostComments.class);
        ActivityOptionsCompat activityOptionsCompat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Pair<View,String> p1 = Pair.create(postAnimData.getName(), postAnimData.getName().getTransitionName());
            Pair<View,String> p2 = Pair.create(postAnimData.getTime(), postAnimData.getTime().getTransitionName());
            Pair<View,String> p3 = Pair.create(postAnimData.getText(), postAnimData.getText().getTransitionName());
            Pair<View,String> p4 = Pair.create(postAnimData.getPropic(), postAnimData.getPropic().getTransitionName());
            if(postAnimData.isImageAvailable()){
                Pair<View,String> p5 = Pair.create(postAnimData.getPost_img(), postAnimData.getPost_img().getTransitionName());
                activityOptionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(getActivity(),p1,p2,p3,p4,p5);
            }else{
                activityOptionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(getActivity(),p1,p2,p3,p4);
            }
            startActivity(intent,activityOptionsCompat.toBundle());
        }else{
            startActivity(intent);
        }
    }

    public static void addPostedItem(String text, String link,String dateString,String postid){
        itemlist.clear();
        itemlist.add(new Post_Data(userDataSP.getUserData(UserDataSP.NUMBER_USER)
                                ,userDataSP.getUserData(UserDataSP.PROPIC_URL)
                                ,userDataSP.getUserData(UserDataSP.FIRST_NAME)
                                ,userDataSP.getUserData(UserDataSP.LAST_NAME)
                                ,postid,text,link,dateString,false,new ArrayList<String>(),"0"));
        postAdaptor.addItemAtTop(itemlist);
        prefixItemData(postid,text,link,dateString);
    }

    private static void prefixItemData(String postid, String text, String link, String dateString) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(UserDataSP.NUMBER_USER,userDataSP.getUserData(UserDataSP.NUMBER_USER));
            jsonObject.put(UserDataSP.FIRST_NAME,userDataSP.getUserData(UserDataSP.FIRST_NAME));
            jsonObject.put(UserDataSP.LAST_NAME,userDataSP.getUserData(UserDataSP.LAST_NAME));
            jsonObject.put("post_id",postid);
            jsonObject.put("text_message",text);
            jsonObject.put("src_link",link);
            jsonObject.put("date",dateString);
            jsonObject.put("like",JSONObject.NULL);
            jsonObject.put("num_comment",0);

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);

            userDataSP.prefixPostData(jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onRefresh() {
        POST_MIN = "0";
        noMorePost = false;
        postAdaptor.setMoreLoading(false);
        loadData(getActivity());
    }

    private void postDataLoad(String min){
        MyVolley volley = new MyVolley(getActivity().getApplicationContext(), new IVolleyResponse() {
            @Override
            public void volleyResponse(String result) {
                try {
                    storeData(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        volley.setUrl(Utils.POST_FETCH);
        volley.setParams(Utils.POST_FETCH_PARAM,min);
        volley.connect();

    }
    private void storeData(String s) throws JSONException {
        if(s.contains(UserDataSP.NUMBER_USER)) {
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
            Toast.makeText(getActivity().getApplicationContext(),"No more posts...",Toast.LENGTH_SHORT).show();
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
                        String sNo = jsonLikeObj.getString(UserDataSP.NUMBER_USER);
                        if(Integer.parseInt(sNo) == Integer.parseInt(userDataSP.getUserData(UserDataSP.NUMBER_USER)))
                        {isLiked = true;}
                        likedStudents.add(sNo);
                    }
                }

                itemlist.add(new Post_Data(jsonPostObj.getString(UserDataSP.NUMBER_USER),jsonPostObj.getString(UserDataSP.PROPIC_URL)
                        ,jsonPostObj.getString(UserDataSP.FIRST_NAME), jsonPostObj.getString(UserDataSP.LAST_NAME),
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
}