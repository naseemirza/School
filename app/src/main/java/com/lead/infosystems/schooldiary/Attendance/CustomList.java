package com.lead.infosystems.schooldiary.Attendance;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.R;

import java.util.List;

/**
 * Created by Naseem on 25-11-2016.
 */

public class CustomList extends ArrayAdapter<String> {
    private boolean flagA=false;
    private boolean flagL=false;
    private final Activity context;
    private final List<String> student_name;
    private final List<String> rollnumber;
    private final List<String> studentnumber;

    public CustomList( Activity context, List<String> student_name, List<String> rollnumber, List<String> studentnumber) {
        super(context, R.layout.list,student_name);
        this.context = context;
        this.student_name = student_name;
        this.rollnumber = rollnumber;
        this.studentnumber = studentnumber;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        View rowView;
        final ListViewHolder listViewHolder;
        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = layoutInflater.inflate(R.layout.list,parent,false);
            listViewHolder = new ListViewHolder();
            listViewHolder.text_name = (TextView) rowView.findViewById(R.id.textView_name);
            listViewHolder.text_roll = (TextView) rowView.findViewById(R.id.textView_roll);
            listViewHolder.Rabsent=(RadioButton) rowView.findViewById(R.id.radio_buttonA);
            listViewHolder.Rleave = (RadioButton)rowView.findViewById(R.id.radio_buttonL);
            rowView.setTag(listViewHolder);
        }
        else
        {
            rowView = convertView;
            listViewHolder = (ListViewHolder) rowView.getTag();
        }
        listViewHolder.text_roll.setText(rollnumber.get(position));
        listViewHolder.text_name.setText(student_name.get(position));
        listViewHolder.Rabsent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(listViewHolder.Rabsent.isChecked()) {
                    if (!flagA) {
                        listViewHolder.Rabsent.setChecked(true);
                        listViewHolder.Rleave.setChecked(false);
                        flagA = true;
                        flagL = false;

                        String send= "A";

                    } else {
                        flagA = false;
                        listViewHolder.Rabsent.setChecked(false);
                        listViewHolder.Rleave.setChecked(false);
                    }
                }

            }
        });
        listViewHolder.Rleave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (listViewHolder.Rleave.isChecked()) {
                    if (!flagL) {
                        listViewHolder.Rleave.setChecked(true);
                        listViewHolder.Rabsent.setChecked(false);
                        flagL = true;
                        flagA = false;

                        String send= "L";


                    } else {
                        flagL = false;
                        listViewHolder.Rleave.setChecked(false);
                        listViewHolder.Rabsent.setChecked(false);
                    }
                }

            }
        });
        return rowView;
    }
}
