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

import java.util.ArrayList;
import java.util.List;


public class CustomList extends ArrayAdapter<Datalist> {


    private boolean flagA=false;
    private boolean flagL=false;
    private final Activity context;
    public static List<Datalist> items=new ArrayList<>();
    public static final String PRESENT = "P";
    public static final String LEAVE = "L";
    public static final String ABSENT = "A";


    public class ListViewHolder {
        TextView text_name;
        TextView text_roll;
        RadioButton Rabsent;
        RadioButton Rleave;
    }

    public CustomList( Activity context, List<Datalist> items) {
        super(context, R.layout.list,items);
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

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
        }else{
            rowView = convertView;
            listViewHolder = (ListViewHolder) rowView.getTag();
        }

        final Datalist currentItem = items.get(position);

        listViewHolder.text_roll.setText(currentItem.getStudent_roll());
        listViewHolder.text_name.setText(currentItem.getStudent_name());

        if(currentItem.getAttendance().contentEquals(ABSENT)){
            listViewHolder.Rabsent.setChecked(true);
            listViewHolder.Rleave.setChecked(false);
        }else if(currentItem.getAttendance().contentEquals(LEAVE)){
            listViewHolder.Rabsent.setChecked(false);
            listViewHolder.Rleave.setChecked(true);
        }else{
            listViewHolder.Rabsent.setChecked(false);
            listViewHolder.Rleave.setChecked(false);
        }

        listViewHolder.Rabsent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(listViewHolder.Rabsent.isChecked()) {
                    if (!flagA) {
                        listViewHolder.Rabsent.setChecked(true);
                        listViewHolder.Rleave.setChecked(false);
                        flagA = true;
                        flagL = false;
                        currentItem.setAttendance(ABSENT);

                    } else {
                        flagA = false;
                        listViewHolder.Rabsent.setChecked(false);
                        listViewHolder.Rleave.setChecked(false);
                        currentItem.setAttendance(PRESENT);
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


                        currentItem.setAttendance(LEAVE);

                    } else {
                        flagL = false;
                        listViewHolder.Rleave.setChecked(false);
                        listViewHolder.Rabsent.setChecked(false);
                        currentItem.setAttendance(PRESENT);
                    }
                }

            }
        });
        return rowView;
    }
}
