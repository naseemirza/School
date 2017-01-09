package com.lead.infosystems.schooldiary.Management;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.Generic.ServerConnect;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.ramotion.foldingcell.FoldingCell;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ManagmentSchool extends Fragment implements IVolleyResponse{

    private MyVolley myVolley;
    private UserDataSP userDataSp;
    private MyDataBase myDataBase;
    private ExpandableHeightListView list;
    private ProgressBar progressBar;
    private TextView notAvailable;
    private FoldingCell firstCell, secondCell;
    private ArrayList<ItemDetail> items;
    private TextView principalNameContent, directorNameContent,  mobileNP,  gmailIdP, qualificationsP,
            interests_fieldP, contact_detailP,  mobileND,  gmailIdD, qualificationsD, interests_fieldD, contact_detailD;
    private ImageView principalImageTitle, directorImageTitle, principalImageContent, directorImageContent;
    private boolean firstfold = true;
    private boolean secondfold = true;


    public ManagmentSchool() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.managment_school, container, false);
        // Inflate the layout for this fragment
        myVolley = new MyVolley(getActivity().getApplicationContext(), this);
        userDataSp = new UserDataSP(getActivity().getApplicationContext());
        myDataBase = new MyDataBase(getActivity().getApplicationContext());
        progressBar = (ProgressBar) rootView.findViewById(R.id.management_progress);

        notAvailable = (TextView) rootView.findViewById(R.id.detailNotAvailable);
        list = (ExpandableHeightListView) rootView.findViewById(R.id.listF);
        firstCell = (FoldingCell)rootView.findViewById(R.id.firstCellView);
        secondCell = (FoldingCell)rootView.findViewById(R.id.secondCellView);
        principalImageTitle = (ImageView)firstCell.findViewById(R.id.principal_profile_title);
        principalImageContent = (ImageView)firstCell.findViewById(R.id.principal_profile_content);

        principalNameContent = (TextView)firstCell.findViewById(R.id.principal_name_content);

        mobileNP = (TextView) firstCell.findViewById(R.id.Pmobile_no);
        gmailIdP = (TextView) firstCell.findViewById(R.id.Pgmail);
        qualificationsP = (TextView)firstCell.findViewById(R.id.Pqualification_t);
        interests_fieldP= (TextView)firstCell.findViewById(R.id.Pfield);
        contact_detailP = (TextView)firstCell.findViewById(R.id.PcontactDetail);
        directorImageTitle = (ImageView)secondCell.findViewById(R.id.director_profile_title);

        directorNameContent = (TextView)secondCell.findViewById(R.id.director_name_content);
        directorImageContent = (ImageView)secondCell.findViewById(R.id.director_profile_content);
        mobileND = (TextView) secondCell.findViewById(R.id.Dmobile_no);
        gmailIdD = (TextView) secondCell.findViewById(R.id.Dgmail);
        qualificationsD = (TextView)secondCell.findViewById(R.id.Dqualification_t);
        interests_fieldD= (TextView)secondCell.findViewById(R.id.Dfield);
        contact_detailD = (TextView)secondCell.findViewById(R.id.DcontactDetail);

        firstCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    firstCell.toggle(false);
            }
        });
        secondCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    secondCell.toggle(false);
                }
        });
        getTeacherDetail();
        return rootView;
    }

    public void getTeacherDetail()
    {   progressBar.setVisibility(View.VISIBLE);
        myVolley.setUrl(Utils.MANAGEMENT_DETAIL);
        myVolley.setParams(UserDataSP.SCHOOL_NUMBER, userDataSp.getUserData(UserDataSP.SCHOOL_NUMBER));
        myVolley.connect();
    }
    @Override
    public void volleyResponse(String result)
    {
        progressBar.setVisibility(View.GONE);
        try {
            notAvailable.setVisibility(View.GONE);
            getJsonData(result);
        }
        catch (JSONException e){
            e.printStackTrace();
            notAvailable.setVisibility(View.VISIBLE);
        }
    }


    public void getJsonData(String res) throws JSONException
    {
        myDataBase.clearManagementData();
        JSONArray jsonArray = new JSONArray(res);
        for(int j = 0 ; j<jsonArray.length(); j++) {
            JSONObject job_data = jsonArray.getJSONObject(j);
                String firstName = job_data.getString(UserDataSP.FIRST_NAME);
                Log.e(UserDataSP.FIRST_NAME, firstName);
                String lastName = job_data.getString("last_name");
                String mobile = job_data.getString("mobile");
                String gmail = job_data.getString("gmail_id");
                String pic = job_data.getString("profilePic_link");
                String completeDetail = job_data.getString("complete_detail");

                JSONArray jsonDetailArray = new JSONArray(completeDetail);

                for (int k = 0; k < jsonDetailArray.length(); k++) {

                    JSONObject jsonObjDetail = jsonDetailArray.getJSONObject(k);
                     if (jsonObjDetail.getString("designation").contains("Principal")) {
                         //principalNameTitle.setText(firstName+lastName+"");
                         principalNameContent.setText(firstName+lastName+"");
                         mobileNP.setText(mobile);
                         gmailIdP.setText(gmail);
                         Picasso.with(getContext())
                                 .load(Utils.SERVER_URL+pic)
                                 .placeholder(R.drawable.defaultpropic)
                                 .networkPolicy(ServerConnect.checkInternetConenction(getActivity())?
                                         NetworkPolicy.NO_CACHE:NetworkPolicy.OFFLINE)
                                 .into(principalImageTitle);
                         Picasso.with(getContext())
                                 .load(Utils.SERVER_URL+pic)
                                 .placeholder(R.drawable.defaultpropic)
                                 .networkPolicy(ServerConnect.checkInternetConenction(getActivity())?
                                         NetworkPolicy.NO_CACHE:NetworkPolicy.OFFLINE)
                                 .into(principalImageContent);
                         //designationP.setText(jsonObjDetail.getString("designation"));
                         qualificationsP.setText(jsonObjDetail.getString("qualifications"));
                         interests_fieldP.setText(jsonObjDetail.getString("interests_field"));
                         contact_detailP.setText(jsonObjDetail.getString("contact_detail"));
                         // for existing cell set valid valid state(without animation)

                     } else if (jsonObjDetail.getString("designation").contains("Director")) {

                         directorNameContent.setText(firstName+lastName+"");
                         mobileND.setText(mobile);
                         gmailIdD.setText(gmail);
                         Picasso.with(getContext())
                                 .load(Utils.SERVER_URL+pic)
                                 .placeholder(R.drawable.defaultpropic)
                                 .networkPolicy(ServerConnect.checkInternetConenction(getActivity())?
                                         NetworkPolicy.NO_CACHE:NetworkPolicy.OFFLINE)
                                 .into(directorImageTitle);
                         Picasso.with(getContext())
                                 .load(Utils.SERVER_URL+pic)
                                 .placeholder(R.drawable.defaultpropic)
                                 .networkPolicy(ServerConnect.checkInternetConenction(getActivity())?
                                         NetworkPolicy.NO_CACHE:NetworkPolicy.OFFLINE)
                                 .into(directorImageContent);
                         qualificationsD.setText(jsonObjDetail.getString("qualifications"));
                         interests_fieldD.setText(jsonObjDetail.getString("interests_field"));
                         contact_detailD.setText(jsonObjDetail.getString("contact_detail"));

                        }
                     else {
                         myDataBase.insertManagementData(firstName, lastName, mobile, gmail, pic, jsonObjDetail.getString("designation"), jsonObjDetail.getString("qualifications"), jsonObjDetail.getString("interests_field"), jsonObjDetail.getString("contact_detail"));
                      }
                    }
                }
        putMdataIntoList();

    }

    public void putMdataIntoList()
    {
        Cursor data = myDataBase.getManagementData();
        items = new ArrayList<>();
        if(data.getCount()>0)
        {
            while (data.moveToNext())
            {

               items.add(new ItemDetail(data.getString(1), data.getString(2)
                       , data.getString(3), data.getString(4), data.getString(5)
                       , data.getString(6), data.getString(7), data.getString(8)
                       , data.getString(9)));
            }
        }
        else{
            Toast.makeText(getActivity().getApplicationContext(),"No Management Data",Toast.LENGTH_SHORT).show();
        }
        final ExpandableCellListAdapter adapter = new ExpandableCellListAdapter(getActivity(), items);
        list.setAdapter(adapter);
        list.setExpanded(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // toggle clicked cell state
                ((FoldingCell) view).toggle(true);
                // register in adapter that state for selected cell is toggled
                adapter.registerToggle(pos);
            }
        });

    }

}
