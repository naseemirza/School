package com.lead.infosystems.schooldiary.ApplicationForm;


import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ApplicationForm extends Fragment implements IVolleyResponse {

    private FloatingActionButton button;
    private ListView list_model;
    private View rootView;
    private UserDataSP userdatasp;
    private MyVolley myVolley;
    private MyAdaptor myAdaptor;
    private TextView notAvailable;
    private ProgressBar progressBar;

    List<ApplicationFormData> items = new ArrayList<>();
    public ApplicationForm() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.frag_pdf, container, false);
        getActivity().setTitle("Application Forms");

        userdatasp=new UserDataSP(getActivity().getApplicationContext());
        myVolley = new MyVolley(getActivity().getApplicationContext(),this);
        button = (FloatingActionButton) rootView.findViewById(R.id.add);
        if(userdatasp.isStudent()){
            button.setVisibility(View.GONE);
        }
        else {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.app.FragmentManager manager = getActivity().getFragmentManager();
                    Dialog_form dialog_form = new Dialog_form();
                    dialog_form.show(manager, "Dialog_form");
                }
            });
        }
        notAvailable = (TextView) rootView.findViewById(R.id.not_available);
        progressBar = (ProgressBar) rootView.findViewById(R.id.pdf_progress);
        list_model = (ListView) rootView.findViewById(R.id.list);
        myAdaptor = new MyAdaptor();
        list_model.setAdapter(myAdaptor);
        connect();
        return rootView;
    }

    private void connect(){
        progressBar.setVisibility(View.VISIBLE);
        myVolley.setUrl(Utils.APPLICATION_FORMS);
        myVolley.setParams(UserDataSP.SCHOOL_NUMBER,userdatasp.getUserData(UserDataSP.SCHOOL_NUMBER));
        myVolley.connect();
    }

    @Override
    public void volleyResponse(String result) {
        progressBar.setVisibility(View.GONE);
        try {
            notAvailable.setVisibility(View.GONE);
            getJsonData(result);
        } catch (JSONException e) {
            e.printStackTrace();
            notAvailable.setVisibility(View.VISIBLE);
        }
    }

    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            items.add(new ApplicationFormData(jsonobj.getString("form_name"),jsonobj.getString("form_link"),jsonobj.getString(UserDataSP.NUMBER_USER)));
        }
        myAdaptor.notifyDataSetChanged();
        list_model.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String link= Utils.SERVER_URL+items.get(position).getLink();
                String pdfLink = link.replace(" ","%20");
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(pdfLink),"application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                    startActivity(intent);
                }catch (Exception e){
                    intent = new Intent(Intent.ACTION_VIEW,Uri.parse(Utils.GOOGLE_DRIVE_VIEWER + pdfLink));
                    startActivity(intent);
                }
            }
        });

    }

    class MyAdaptor extends ArrayAdapter<ApplicationFormData> {
        public MyAdaptor() {

            super(getActivity().getApplicationContext(), R.layout.pdf_names, items);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View ItemView = convertView;
            if (ItemView == null) {
                ItemView = getActivity().getLayoutInflater().inflate(R.layout.pdf_names, parent, false);
            }

            final ApplicationFormData currentItem = items.get(position);
            TextView name = (TextView) ItemView.findViewById(R.id.pdf_name);
            ImageView delete = (ImageView) ItemView.findViewById(R.id.paper_delete);
            name.setText(currentItem.getName());
            ImageView imageName = (ImageView) ItemView.findViewById(R.id.image_text);

            String firstletter = String.valueOf(currentItem.getName().charAt(0));
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(getItem(position));
            TextDrawable drawable = TextDrawable.builder().buildRound(firstletter.toUpperCase(),color);
            imageName.setImageDrawable(drawable);
            if(currentItem.getDeleteUser()== Integer.parseInt(userdatasp.getUserData(UserDataSP.NUMBER_USER)))
            {
                delete.setVisibility(View.VISIBLE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getActivity());
                    alert.setTitle("Alert");
                    alert.setMessage("Are you sure to delete record");
                    alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myVolley = new MyVolley(getActivity().getApplicationContext(), new IVolleyResponse() {
                                @Override
                                public void volleyResponse(String result) {
                                    if(result.contains("DONE"))
                                    {
                                        items.remove(position);
                                        myAdaptor.notifyDataSetChanged();
                                        Toast.makeText(getActivity().getApplicationContext(), "DONE", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                            myVolley.setUrl(Utils.APPLICATIONFORM_DELETE);
                            myVolley.setParams(UserDataSP.NUMBER_USER, userdatasp.getUserData(UserDataSP.NUMBER_USER) );
                            myVolley.setParams("form_link", currentItem.getLink() );
                            myVolley.connect();
                            dialog.dismiss();

                        }
                    });
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                    }
                });

            }
            else{
                delete.setVisibility(View.GONE);
            }
            return ItemView;
        }
    }

}
