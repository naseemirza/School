package com.lead.infosystems.schooldiary.ExamTest;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.Generic.ServerConnect;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.lead.infosystems.schooldiary.ExamTest.Dialog_exam.EXAM_DATE;
import static com.lead.infosystems.schooldiary.ExamTest.Dialog_exam.EXAM_DESCRIPTION;
import static com.lead.infosystems.schooldiary.ExamTest.Dialog_exam.EXAM_NAME;
import static com.lead.infosystems.schooldiary.ExamTest.Dialog_exam.EXAM_PDFLINK;
import static com.lead.infosystems.schooldiary.ExamTest.Dialog_exam.EXAM_SUBMISSION_DATE;
import static com.lead.infosystems.schooldiary.ExamTest.Dialog_exam.EXAM_UPLOAD_USER;
import static com.lead.infosystems.schooldiary.ExamTest.Dialog_exam.INTENTFILTEREXAM;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExamDetails extends Fragment implements IVolleyResponse{
    private UserDataSP userDataSp;
    private MyVolley myVolley;
    private ListView examList;
    private MyAdaptor adaptor;
    private ProgressBar progressBar;
    private TextView notAvailable;
    private TextView noInternet;
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
        getActivity().setTitle("Exam Details");
        progressBar = (ProgressBar)rootView.findViewById(R.id.testExam_progress);
        notAvailable = (TextView)rootView.findViewById(R.id.testExamnot_available);
        noInternet = (TextView)rootView.findViewById(R.id.testExamnoInternet);
        button = (FloatingActionButton)rootView.findViewById(R.id.add_exam);
        if(userDataSp.isStudent())
        {
            button.setVisibility(View.GONE);
        }
        else {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ServerConnect.checkInternetConenction(getActivity())) {
                        android.app.FragmentManager manager = getActivity().getFragmentManager();
                        Dialog_exam dialog_exam = new Dialog_exam();
                        dialog_exam.show(manager, "Dialog_exam");
                    }
                }
            });
        }
        examList = (ListView)rootView.findViewById(R.id.exam_list);
        adaptor = new MyAdaptor();
        examList.setAdapter(adaptor);
        checkInternetConnection();

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(receiver, new IntentFilter(INTENTFILTEREXAM));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            exam_detail.add(0, new ExamData(intent.getStringExtra(EXAM_NAME),
                    intent.getStringExtra(EXAM_DATE),intent.getStringExtra(EXAM_DESCRIPTION),
                    intent.getStringExtra(EXAM_PDFLINK), intent.getStringExtra(EXAM_SUBMISSION_DATE),
                    intent.getStringExtra(EXAM_UPLOAD_USER)));
            adaptor.notifyDataSetChanged();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(receiver);
        }catch(Exception e){
            e.printStackTrace();
        }
    }




    public void checkInternetConnection()
    { if(ServerConnect.checkInternetConenction(getActivity()))
    {   progressBar.setVisibility(View.VISIBLE);
        getExamData();
    }
        else {
        noInternet.setVisibility(View.VISIBLE);
    }

    }

    public void getExamData()
    {
        myVolley.setUrl(Utils.EXAM_DETAIL);
        myVolley.setParams(UserDataSP.SCHOOL_NUMBER, userDataSp.getUserData(UserDataSP.SCHOOL_NUMBER));
        myVolley.connect();
    }
    @Override
    public void volleyResponse(String result)
    {
        try {
             noInternet.setVisibility(View.GONE);
            getJsonData(result);
        } catch (JSONException e) {
            e.printStackTrace();
           notAvailable.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }

    public void getJsonData(String res) throws JSONException
    {   exam_detail.clear();
        JSONArray jsonArray = new JSONArray(res);
        for(int i =0; i<jsonArray.length(); i++)
        {
            JSONObject jobj = jsonArray.getJSONObject(i);
            exam_detail.add(new ExamData(jobj.getString("exam_name"), jobj.getString("exam_date"),
                    jobj.getString("exam_description"), jobj.getString("exam_pdf_link"),
                    jobj.getString("submission_date"), jobj.getString("number_user")));
        }
        sortData();
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
            TextView exam_description = (TextView) ItemView.findViewById(R.id.exam_description);
            ImageView image = (ImageView) ItemView.findViewById(R.id.examcell);
            String firstletter = String.valueOf(currentItem.getExam_name().charAt(0));
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(getItem(position));
            TextDrawable drawable = TextDrawable.builder().buildRound(firstletter.toUpperCase(),color);
            image.setImageDrawable(drawable);
            exam_name.setText(currentItem.getExam_name()+"");
            exam_date.setText(currentItem.getExam_date().split(" ")[0]);
            if(!currentItem.getExam_description().isEmpty())
            {
                exam_description.setVisibility(View.VISIBLE);
                exam_description.setText(currentItem.getExam_description()+"");
            }
            else
            {
                exam_description.setVisibility(View.GONE);
            }
            return ItemView;
        }
    }


    public void sortData()
    {
        Collections.sort(exam_detail,new MyComparator());
        adaptor.notifyDataSetChanged();
    }
    private class MyComparator implements Comparator<ExamData> {
        @Override
        public int compare(ExamData lhs, ExamData rhs) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                Date date1 = dateFormat.parse(rhs.getSubmission_date());
                Date date2 = dateFormat.parse(lhs.getSubmission_date());
                Log.e("date1",date2.compareTo(date1)+"") ;
                return date1.compareTo(date2);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        }


    }


}
