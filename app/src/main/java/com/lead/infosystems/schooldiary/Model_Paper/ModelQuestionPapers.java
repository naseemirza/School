package com.lead.infosystems.schooldiary.Model_Paper;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;

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


public class ModelQuestionPapers extends Fragment {

    FloatingActionButton button;
    ListView list_model;
    View rootView;
UserDataSP userdatasp;

    List<Model_paper> items = new ArrayList<>();

    public ModelQuestionPapers() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_model_question_papers, container, false);

        userdatasp=new UserDataSP(getActivity().getApplicationContext());


        new Model(getActivity()).execute();
        button = (FloatingActionButton) rootView.findViewById(R.id.add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.FragmentManager manager = getActivity().getFragmentManager();
                Dialog_model dialog_model = new Dialog_model();
                dialog_model.show(manager, "Dialog_model");
            }
        });
        list_model = (ListView) rootView.findViewById(R.id.list);


        return rootView;
    }


    class Model extends AsyncTask<Void,Void,String> {

        String json_url;
        Activity activity;

        Model(Activity activity) {
            this.activity = activity;
        }


        @Override
        protected void onPreExecute() {

            json_url = "leadinfosystems.com/school_diary/SchoolDiary/model_paper_insert.php";

        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL url = new URL("http://leadinfosystems.com/school_diary/SchoolDiary/model_paper_insert.php");
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

                builder.appendQueryParameter("class",userdatasp.getUserData(UserDataSP.CLASS) );

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
        Log.e("data",result);
            String[] res = result.split("@@@");
            try {
                getJsonData(res[0]);



            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        private void getJsonData(String re) throws JSONException {
            JSONArray json = new JSONArray(re);
            for (int i = 0; i <= json.length() - 1; i++) {
                JSONObject jsonobj = json.getJSONObject(i);
                items.add(new Model_paper(jsonobj.getString("paper_name"),jsonobj.getString("paper_link")));
            }

            list_model.setAdapter(new MyAdaptor());

            list_model.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    Intent it=new Intent(Intent.ACTION_VIEW);
                    String link="http://leadinfosystems.com/school_diary/SchoolDiary/"+items.get(position).getLink();
                    it.setDataAndType(Uri.parse(link.replace(" ","%20")),"application/pdf");

                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getActivity().startActivity(it);


                }
            });

        }



    }
    class MyAdaptor extends ArrayAdapter<Model_paper> {
        public MyAdaptor() {

            super(getActivity().getApplicationContext(), R.layout.pdf_names, items);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ItemView = convertView;
            if (ItemView == null) {
                ItemView = getActivity().getLayoutInflater().inflate(R.layout.pdf_names, parent, false);
            }

            Model_paper currentItem = items.get(position);
            TextView name = (TextView) ItemView.findViewById(R.id.pdf_name);
            name.setText(currentItem.getName());
            return ItemView;
        }
    }
}




