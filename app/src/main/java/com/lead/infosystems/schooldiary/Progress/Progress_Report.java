package com.lead.infosystems.schooldiary.Progress;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.Generic.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Progress_Report extends Fragment {
   private UserDataSP userDataSP;
    private MyDataBase myDataBase;
    SPData spData;
    ListAdapter object;
    ListView list;
    public Button btn1;
    View rootView;
    String examData;

    public Progress_Report() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_progress__report, container, false);
        btn1=(Button)rootView.findViewById(R.id.button_prog);
        userDataSP=new UserDataSP(getActivity().getApplicationContext());
        myDataBase = new MyDataBase(getActivity().getApplicationContext());
        getActivity().setTitle("Progress Report");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (examData == null){
                    Toast.makeText(getActivity(),"There is no data for Showing Graph..",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent it = new Intent(getActivity().getApplicationContext(), GraphView.class);
                    startActivity(it);
                }
            }
        });
        new backg(getActivity()).execute();

        return rootView;
    }

    class backg extends AsyncTask<Void,Void,String> {

        Activity activity;

        backg(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL url = new URL(Utils.MARKS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(10000);
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter("number_user",userDataSP.getUserData(UserDataSP.NUMBER_USER));

                String abc = builder.build().getQuery();
                bufferedWriter.write(abc);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);

                }
                bufferedReader.close();
                inputStream.close();


                return stringBuilder.toString().trim();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String result) {
            spData = new SPData(getActivity().getApplicationContext());
            spData.storeData(result);
            String[] res = result.split("@@@");
            try {
                getJsonData(res[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        private void getJsonData(String re) throws JSONException {
            JSONArray json = new JSONArray(re);
            final List<String> subjects = new ArrayList<String>();
            for (int i = 0; i <= json.length() - 1; i++) {
                JSONObject jsonobj = json.getJSONObject(i);
                myDataBase.insertSubjectData(jsonobj.getString("sub_name"));
                examData=jsonobj.getString("sub_data");
                putIntoList();

            }

        }

        public void putIntoList()
        {
            final List<String> subjects = new ArrayList<String>();
            Cursor data = myDataBase.getSubjectData();
            if(data.getCount()>0)
            {
                while (data.moveToNext())
                {
                    subjects.add(data.getString(1));
                }
            }
            else{
                Toast.makeText(getActivity().getApplicationContext(),"No Home Work Data",Toast.LENGTH_SHORT).show();
            }
            object = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, subjects);
            list = (ListView)rootView.findViewById(R.id.list);
            list.setAdapter(object);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (examData.equals("null")){
                        Toast.makeText(getActivity(),"There is no data in this Subject...",Toast.LENGTH_SHORT).show();

                    }else {
                        Intent intent = new Intent(view.getContext(), Marks.class);
                        intent.putExtra("sub_name", subjects.get(position));
                        startActivity(intent);
                    }

                }
            });


        }

    }

}
