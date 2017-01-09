package com.lead.infosystems.schooldiary.Main;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.Data.ChatContact;
import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.ServerConnect;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatNew extends AppCompatActivity implements IVolleyResponse{

    private UserDataSP userDataSP;
    private List<ChatContact> items = new ArrayList<>();
    private List<ChatContact> orignalList ;
    private List<ChatContact> displayedList;
    private MyListAdapter myAdaptor;
    private MyDataBase dataBase;
    private ProgressBar progressBar;
    private ListView list;
    private MyVolley myVolley;
    private Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_new);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        list = (ListView) findViewById(R.id.contact_list);
        progressBar = (ProgressBar) findViewById(R.id.contact_loading);
        userDataSP = new UserDataSP(getApplicationContext());
        dataBase = new MyDataBase(getApplicationContext());
        myVolley = new MyVolley(getApplicationContext(),this);
        activity = this;
        populateListAdaptor();
        getDataToList(true);
    }

    private void connect(){
        dataBase.clearContacts();
        items.clear();
        myAdaptor.notifyDataSetChanged();
        progressBar.setVisibility(View.VISIBLE);
        myVolley.setUrl(Utils.CHAT_CONTACT);
        myVolley.setParams(UserDataSP.SCHOOL_NUMBER,userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
        myVolley.setParams(UserDataSP.NUMBER_USER,userDataSP.getUserData(UserDataSP.NUMBER_USER));
        myVolley.connect();
    }

    @Override
    public void volleyResponse(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    dataBase.insertIntoCOntact(jsonObject.getString(UserDataSP.NUMBER_USER)
                            ,jsonObject.getString(UserDataSP.FIRST_NAME)
                            ,jsonObject.getString(UserDataSP.LAST_NAME)
                            ,jsonObject.getString(UserDataSP.PROPIC_URL));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getDataToList(false);
            progressBar.setVisibility(View.GONE);
    }

    private void getDataToList(boolean reload){
        if(dataBase.getContacts().getCount()>0){
            items.clear();
            Cursor data= dataBase.getContacts();
            while (data.moveToNext()){
                items.add(new ChatContact(data.getString(1)
                        ,data.getString(2),data.getString(3), data.getString(4)));
            }
            myAdaptor.notifyDataSetChanged();
        }else{
            if(reload){connect();}
        }
    }
    private void populateListAdaptor() {
        myAdaptor = new MyListAdapter();
        list.setAdapter(myAdaptor);
        onItemClick();
    }

    private void onItemClick(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),Chat.class);
                intent.putExtra(Chat.USER_ID,displayedList.get(position).getUserID());
                intent.putExtra(Chat.FIRST_NAME,displayedList.get(position).getFirstName());
                intent.putExtra(Chat.PROPIC_LINK,displayedList.get(position).getProfilePic_link());
                startActivity(intent);
            }
        });
    }


    private class MyListAdapter extends ArrayAdapter<ChatContact> implements Filterable {


        public MyListAdapter() {
            super(getApplicationContext(), R.layout.contact_item, items);
            orignalList = items;
            displayedList = items;
        }

        @Override
        public int getCount() {
            return displayedList.size();
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if(itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.contact_item,parent,false);
            }
            ChatContact currentItem = displayedList.get(position);

            TextView name = (TextView) itemView.findViewById(R.id.title);
            ImageView propic = (ImageView) itemView.findViewById(R.id.propic);
            Picasso.with(getApplicationContext())
                    .load(Utils.SERVER_URL+currentItem.getProfilePic_link().replace("profilepic","propic_thumb"))
                    .networkPolicy(ServerConnect.checkInternetConenction(activity) ?
                            NetworkPolicy.NO_CACHE : NetworkPolicy.OFFLINE)
                    .into(propic);
            name.setText(currentItem.getName());

            return itemView;
        }

        @NonNull
        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    ArrayList<ChatContact> filteredList = new ArrayList<>();
                    if(orignalList == null){
                        orignalList = new ArrayList<ChatContact>(displayedList);
                    }

                    if (constraint == null || constraint.length() == 0) {
                        results.count = orignalList.size();
                        results.values = orignalList;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < orignalList.size(); i++) {
                            String data = orignalList.get(i).getName();
                            if (data.toLowerCase().startsWith(constraint.toString())) {
                                filteredList.add(new ChatContact(orignalList.get(i).getUserID()
                                        ,orignalList.get(i).getFirstName(),orignalList.get(i).getLastName()
                                        , orignalList.get(i).getProfilePic_link()));
                            }
                        }
                        results.count = filteredList.size();
                        results.values = filteredList;
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    displayedList = (ArrayList<ChatContact>) results.values;
                    notifyDataSetChanged();
                }
            };
            return filter;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_contact_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView search = (SearchView) MenuItemCompat.getActionView(menuItem);

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                myAdaptor.getFilter().filter(newText);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the FragTabHome/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==R.id.search){
            return true;
        }else if(id == R.id.refresh){
            connect();
        }
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
}
