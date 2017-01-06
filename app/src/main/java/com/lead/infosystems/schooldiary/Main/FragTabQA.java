package com.lead.infosystems.schooldiary.Main;


import android.content.Intent;
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

import com.lead.infosystems.schooldiary.Data.QaData;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.Generic.ServerConnect;
import com.lead.infosystems.schooldiary.Generic.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragTabQA extends Fragment implements QaAdaptor.OnLoadMoreListener,SwipeRefreshLayout.OnRefreshListener {

    View rootview;
    SwipeRefreshLayout swipeRefreshLayout;
    static QaAdaptor qaAdaptor;
    static UserDataSP userDataSP;
    String QA_MIN = "0";
    boolean noMoreItems = false;
    private boolean backPressed = false;
    static List<QaData> items = new ArrayList<>();
    public static QaAnimData qaAnimData;
    public FragTabQA() {
    }

    @Override
    public void onStart() {
        super.onStart();
        items.clear();
        noMoreItems = false;
        if(ServerConnect.checkInternetConenction(getActivity())&& !backPressed){
            QA_MIN = "0";
            loadData(QA_MIN);
        }else{
            if(userDataSP.getPostData()!=""){
                swipeRefreshLayout.setRefreshing(false);
                qaAdaptor.addAll(parseJson(userDataSP.getQaData()));
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
        rootview =  inflater.inflate(R.layout.fragment_tab_qa, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.swipeRefresh);
        RecyclerView recyclerView = (RecyclerView) rootview.findViewById(R.id.rvList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        qaAdaptor = new QaAdaptor(this,getActivity());
        qaAdaptor.setLinearLayoutManager(linearLayoutManager);
        qaAdaptor.setRecyclerView(recyclerView);
        recyclerView.setAdapter(qaAdaptor);
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
    public void onRefresh() {
        QA_MIN = "0";
        noMoreItems = false;
        qaAdaptor.setMoreLoading(false);
        loadData(QA_MIN);
    }

    @Override
    public void onLoadMore() {
        if(!noMoreItems){
            qaAdaptor.setProgressMore(true);
            loadData(QA_MIN);
        }
    }

    @Override
    public void onAnswerClick(QaAnimData animData) {
        backPressed = true;
        this.qaAnimData = animData;
        ActivityOptionsCompat activityOptionsCompat = null;
        Intent intent = new Intent(getActivity(), Answer.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Pair<View,String> p1 = Pair.create((View) qaAnimData.getName(), qaAnimData.getName().getTransitionName());
            Pair<View,String> p2 = Pair.create((View) qaAnimData.getTime(), qaAnimData.getTime().getTransitionName());
            Pair<View,String> p3 = Pair.create((View) qaAnimData.getText(), qaAnimData.getText().getTransitionName());
            Pair<View,String> p4 = Pair.create((View) qaAnimData.getPropic(), qaAnimData.getPropic().getTransitionName());
            activityOptionsCompat = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(getActivity(),p1,p2,p3,p4);
            startActivity(intent,activityOptionsCompat.toBundle());
        }else{
            startActivity(intent);
        }

    }

    public static void addItem(String questionText,String questionNum,String date){
        items.clear();
        String name = userDataSP.getUserData(UserDataSP.FIRST_NAME)+" "+
                userDataSP.getUserData(UserDataSP.LAST_NAME);
        items.add(new QaData(userDataSP.getUserData(UserDataSP.NUMBER_USER)
                            , userDataSP.getUserData(UserDataSP.PROPIC_URL), name,questionText,questionNum,"0",Utils.getTimeString(date)));
        qaAdaptor.addItemAtTop(items);
        prifixItemData(questionText,name,questionNum,date);

    }

    private static void prifixItemData(String questionText, String name, String questionNum, String date) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(UserDataSP.NUMBER_USER,userDataSP.getUserData(UserDataSP.NUMBER_USER));
            jsonObject.put("name",name);
            jsonObject.put("question_number",questionNum);
            jsonObject.put("question_text",questionText);
            jsonObject.put("date",date);
            jsonObject.put("answer","0");

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);

            userDataSP.prefixQaData(jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadData(final String min){
        MyVolley volley = new MyVolley(getActivity().getApplicationContext(), new IVolleyResponse() {
            @Override
            public void volleyResponse(String result) {
                if(result.contains(UserDataSP.NUMBER_USER)){

                    if(min == "0"){
                        swipeRefreshLayout.setRefreshing(false);
                        qaAdaptor.addAll(parseJson(result));
                        userDataSP.storeQaData(result);
                    }else {
                        qaAdaptor.setProgressMore(false);
                        qaAdaptor.addItemMore(parseJson(result));
                        qaAdaptor.setMoreLoading(false);
                        userDataSP.appendQaData(result);
                    }

                }else{
                    Toast.makeText(getActivity().getApplicationContext(),"No more Questions..",Toast.LENGTH_SHORT).show();
                    qaAdaptor.setProgressMore(false);
                    qaAdaptor.setMoreLoading(false);
                    noMoreItems = true;
                }
            }
        });
        volley.setUrl(Utils.QA_FETCH);
        volley.setParams("min",min);
        volley.connect();
    }

    private List<QaData> parseJson(String data){
        items.clear();
        try {
            JSONArray jsonArray = new JSONArray(data);
            for(int i = 0 ; i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                items.add(new QaData(jsonObject.getString(UserDataSP.NUMBER_USER),jsonObject.getString(UserDataSP.PROPIC_URL),jsonObject.getString("name"),
                        jsonObject.getString("question_text"),jsonObject.getString("question_number"),
                        jsonObject.getString("answer"),Utils.getTimeString(jsonObject.getString("date"))));
                if(i == jsonArray.length()-1){
                    QA_MIN = jsonObject.getString("question_number");
                }
            }
            return items;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
