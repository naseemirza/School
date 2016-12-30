package com.lead.infosystems.schooldiary.ExamTest;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.MyVolley;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExamDetails extends Fragment implements IVolleyResponse{
     UserDataSP userDataSp;
    private MyVolley myVolley;
    ListView examList;
    private MyAdaptor adaptor;
    private FloatingActionButton button;
    List<ExamData> exam_detail = new ArrayList<>();
    public ExamDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_exam_details, container, false);
        userDataSp = new UserDataSP(getActivity().getApplicationContext());
        myVolley = new MyVolley(getActivity().getApplicationContext(), this);
        getActivity().setTitle("EXAM DETAILS");
        button = (FloatingActionButton)rootView.findViewById(R.id.add_exam);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.FragmentManager manager = getActivity().getFragmentManager();
                Dialog_exam dialog_exam = new Dialog_exam();
                dialog_exam.show(manager, "Dialog_exam");
            }
        });
        examList = (ListView)rootView.findViewById(R.id.exam_list);
        adaptor = new MyAdaptor();
        examList.setAdapter(adaptor);
        getExamData();
        return rootView;
    }

    public void getExamData()
    {
        myVolley.setUrl(Utils.EXAM_DETAIL);
        myVolley.setParams(UserDataSP.SCHOOL_NUMBER, userDataSp.getUserData(UserDataSP.SCHOOL_NUMBER));
        myVolley.connect();
    }
    @Override
    public void volleyResponce(String result)
    {
        try {

            getJsonData(result);
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    public void getJsonData(String res) throws JSONException
    {   exam_detail.clear();
        JSONArray jsonArray = new JSONArray(res);
        for(int i =0; i<jsonArray.length(); i++)
        {
            JSONObject jobj = jsonArray.getJSONObject(i);
            exam_detail.add(new ExamData(jobj.getString("exam_name"), jobj.getString("exam_date"), jobj.getString("exam_description"), jobj.getString("exam_pdf_link")));
        }

        adaptor.notifyDataSetChanged();
        examList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String link=Utils.SERVER_URL + exam_detail.get(position).getExam_pdf_link();
                String pdfLink = link.replace(" ","%20");
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(pdfLink),"application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                try {
                    getActivity().startActivity(intent);
                }catch (Exception e){
                    intent = new Intent(Intent.ACTION_VIEW,Uri.parse(Utils.GOOGLE_DRIVE_VIEWER + pdfLink));
                    getActivity().startActivity(intent);
                }
            }
        });

    }
    class MyAdaptor extends ArrayAdapter<ExamData> {

        public MyAdaptor() {
            super(getActivity().getApplicationContext(), R.layout.activity_exam_view, exam_detail);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ItemView = convertView;
            if (ItemView == null) {
                ItemView = getActivity().getLayoutInflater().inflate(R.layout.activity_exam_view, parent, false);
            }

            ExamData currentItem = exam_detail.get(position);
            TextView exam_name = (TextView) ItemView.findViewById(R.id.exam_name);
            TextView exam_date = (TextView) ItemView.findViewById(R.id.exam_date);
            ImageView image = (ImageView) ItemView.findViewById(R.id.examcell);
            String firstletter = String.valueOf(currentItem.getExam_name().charAt(0));
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(getItem(position));
            TextDrawable drawable = TextDrawable.builder().buildRound(firstletter.toUpperCase(),color);
            image.setImageDrawable(drawable);
            exam_name.setText(currentItem.getExam_name()+"");
            exam_date.setText(currentItem.getExam_date()+"");


            return ItemView;

        }
    }

}
