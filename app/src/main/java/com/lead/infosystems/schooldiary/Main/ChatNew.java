package com.lead.infosystems.schooldiary.Main;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
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
import com.lead.infosystems.schooldiary.Data.ChatContact;
import com.lead.infosystems.schooldiary.Data.ChatListItems;
import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.ServerConnect;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatNew extends AppCompatActivity {

    private UserDataSP userDataSP;
    private List<ChatContact> items = new ArrayList<>();
    private List<ChatContact> orignalList ;
    private List<ChatContact> displayedList;
    private MyListAdapter myAdaptor;
    private MyDataBase dataBase;
    private ProgressBar progressBar;
    private ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_new);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        list = (ListView) findViewById(R.id.contact_list);
        progressBar = (ProgressBar) findViewById(R.id.contact_loading);
        userDataSP = new UserDataSP(getApplicationContext());
        dataBase = new MyDataBase(getApplicationContext());
        getDataToList();
    }

    private void connect(){
        dataBase.clearContacts();
        items.clear();
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, Utils.CHAT_CONTACT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response != null && !response.contains("ERROR")) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    dataBase.insertIntoCOntact(jsonObject.getString("number_user")
                                            ,jsonObject.getString("first_name")
                                            ,jsonObject.getString("last_name"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                       getDataToList();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getStackTrace();
                Toast.makeText(getApplicationContext(), ServerConnect.connectionError(error),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map =  new HashMap<>();
                map.put("school_number",userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
                map.put("user_number",userDataSP.getUserData(UserDataSP.STUDENT_NUMBER));
                return map;
            }
        };
        int socketTimeout = 20000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        requestQueue.add(request);

    }

    private void getDataToList(){
        if(dataBase.getContacts().getCount()>0){
            Cursor data= dataBase.getContacts();
            while (data.moveToNext()){
                items.add(new ChatContact(data.getString(1)
                        ,data.getString(2),data.getString(3)));
            }
            populateListAdaptor();
        }else{
            connect();
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

            TextView name = (TextView) itemView.findViewById(R.id.name);
            ImageView propic = (ImageView) itemView.findViewById(R.id.propic);

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
                                        ,orignalList.get(i).getFirstName(),orignalList.get(i).getLastName()));
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
