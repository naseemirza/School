package com.lead.infosystems.schooldiary.Model_Paper;


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
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.MyVolley;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ModelQuestionPapers extends Fragment implements IVolleyResponse {

    private FloatingActionButton button;
    private ListView list_model;
    private View rootView;
    private UserDataSP userdatasp;
    private MyAdaptor adaptor;
    private ProgressBar progressBar;
    private MyVolley myVolley;
    private TextView notAvailable;
    private ImageButton delete_button;
    List<Model_paper> items = new ArrayList<>();

    public ModelQuestionPapers() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.frag_pdf, container, false);
        userdatasp=new UserDataSP(getActivity().getApplicationContext());
        progressBar = (ProgressBar) rootView.findViewById(R.id.pdf_progress);
        notAvailable = (TextView) rootView.findViewById(R.id.not_available);
        getActivity().setTitle("Model Question Paper");
        list_model = (ListView) rootView.findViewById(R.id.list);
        adaptor = new MyAdaptor();
        list_model.setAdapter(adaptor);
        myVolley = new MyVolley(getActivity().getApplicationContext(),this);
        getDataFromServer();
        button = (FloatingActionButton) rootView.findViewById(R.id.add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.FragmentManager manager = getActivity().getFragmentManager();
                Dialog_model dialog_model = new Dialog_model();
                dialog_model.show(manager, "Dialog_model");
            }
        });
        return rootView;
    }


    private void getDataFromServer(){
          progressBar.setVisibility(View.VISIBLE);
          myVolley.setUrl(Utils.MODEL_PAPER);
          myVolley.setParams(UserDataSP.CLASS,userdatasp.getUserData(UserDataSP.CLASS));
          myVolley.connect();
    }
    @Override
    public void volleyResponce(String result) {
        try {
            notAvailable.setVisibility(View.GONE);
            getJsonData(result);
        } catch (JSONException e) {
            e.printStackTrace();
            notAvailable.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);
    }

    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            items.add(new Model_paper(jsonobj.getString("paper_name"), jsonobj.getString("paper_link"), jsonobj.getString(UserDataSP.NUMBER_USER)));
        }
        adaptor.notifyDataSetChanged();

        list_model.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String link=Utils.SERVER_URL + items.get(position).getLink();
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

    class MyAdaptor extends ArrayAdapter<Model_paper> {
        public MyAdaptor() {super(getActivity().getApplicationContext(), R.layout.pdf_names, items);}

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View ItemView = convertView;
            if (ItemView == null) {
                ItemView = getActivity().getLayoutInflater().inflate(R.layout.pdf_names, parent, false);
            }

            final Model_paper currentItem = items.get(position);
            delete_button = (ImageButton)ItemView.findViewById(R.id.paper_delete);
            TextView name = (TextView) ItemView.findViewById(R.id.pdf_name);
            name.setText(currentItem.getName());
            ImageView imageName = (ImageView) ItemView.findViewById(R.id.image_text);

            String firstletter = String.valueOf(currentItem.getName().charAt(0));
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(getItem(position));
            TextDrawable drawable = TextDrawable.builder().buildRound(firstletter.toUpperCase(),color);
            imageName.setImageDrawable(drawable);

            if(currentItem.getUserUpload()== Integer.parseInt(userdatasp.getUserData(UserDataSP.NUMBER_USER)))
            {
                delete_button.setVisibility(View.VISIBLE);
                delete_button.setOnClickListener(new View.OnClickListener() {
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
                                    public void volleyResponce(String result) {
                                        Log.e("after delete", result);


                                        if(result.contains("DONE"))
                                        {
                                            items.remove(position);
                                            adaptor.notifyDataSetChanged();
                                            Toast.makeText(getActivity().getApplicationContext(), ""+result, Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                                myVolley.setUrl(Utils.MODELPAPER_DELETE);
                                myVolley.setParams(UserDataSP.NUMBER_USER, userdatasp.getUserData(UserDataSP.NUMBER_USER) );
                                myVolley.setParams("paper_link",currentItem.getLink() );
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
                delete_button.setVisibility(View.GONE);
            }

            return ItemView;
        }
    }
}




