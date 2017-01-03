package com.lead.infosystems.schooldiary.Model_Paper;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.Main.MainActivity;
import com.lead.infosystems.schooldiary.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherMQP extends Fragment {

    private UserDataSP userDataSP;
    private ListView clist;
    private MyAdaptor adaptor;
    public static List<String> classes = new ArrayList<>();

    public TeacherMQP() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_teacher_mqp, container, false);
        getActivity().setTitle("Model Question Paper");
        userDataSP=new UserDataSP(getActivity());
        clist=(ListView)rootView.findViewById(R.id.list);
        adaptor = new MyAdaptor();
        clist.setAdapter(adaptor);
        getClassData();
        return rootView;
    }
    public void getClassData(){
        MyVolley volley = new MyVolley(getActivity().getApplicationContext(), new IVolleyResponse() {
            @Override
            public void volleyResponse(String result) {
                try {
                    getJsonData(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        volley.setUrl(Utils.ATTENDANCE);
        volley.setParams(UserDataSP.SCHOOL_NUMBER,userDataSP.getUserData(UserDataSP.SCHOOL_NUMBER));
        volley.connect();
    }

    private void getJsonData(String re) throws JSONException {
        JSONArray json = new JSONArray(re);
        classes.clear();

        for (int i = 0; i <= json.length() - 1; i++) {
            JSONObject jsonobj = json.getJSONObject(i);
            classes.add(jsonobj.getString(UserDataSP.CLASS));
        }

        adaptor.notifyDataSetChanged();
        clist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ModelQuestionPapers blankFragment = new ModelQuestionPapers(classes.get(position));
                FragmentTransaction frag = getActivity().getSupportFragmentManager().beginTransaction();
                frag.replace(R.id.main_con,blankFragment);
                MainActivity.setTag(MainActivity.BACK_STACK_TMQP);
                frag.addToBackStack(MainActivity.BACK_STACK_TMQP);
                frag.commit();
            }
        });


    }
    class MyAdaptor extends ArrayAdapter<String> {

        public MyAdaptor() {
            super(getActivity().getApplicationContext(), R.layout.class_div,classes);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.class_div, parent, false);
            }

            String className=classes.get(position);
            ImageView img = (ImageView) itemView.findViewById(R.id.class_image);
            String firstletter = String.valueOf(className.charAt(0));
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(getItem(position));
            TextDrawable drawable = TextDrawable.builder().buildRoundRect(firstletter.toUpperCase(),color,20);
            img.setImageDrawable(drawable);
            return itemView;

        }
    }
}
