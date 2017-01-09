package com.lead.infosystems.schooldiary.SchoolDiary;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Generic.MyVolley;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.ramotion.foldingcell.FoldingCell;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class FoldingCellListAdapter  extends ArrayAdapter<Item> {
    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    UserDataSP userDataSp = new UserDataSP(getContext());
    private MyVolley myVolley;
    List<Item> list = new ArrayList<>();

    public FoldingCellListAdapter(Context context, List<Item> objects) {
        super(context, 0, objects);
        this.list = objects;
    }


    public void addItemNew(Item item)
    {
        list.add(item);
        sortData();
    }

    public void sortData()
    {
        Collections.sort(list,new MyComparator());
        notifyDataSetChanged();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Item item = list.get(position);
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.home_work_diery_item, parent, false);
            cell.initialize(1000, Color.DKGRAY, 6);
            viewHolder.homework_title = (TextView) cell.findViewById(R.id.title_home);
            viewHolder.homework_contents = (TextView) cell.findViewById(R.id.content_home);
            viewHolder.deleteHomework = (TextView)cell.findViewById(R.id.content_request_btn);
            viewHolder.subject = (TextView) cell.findViewById(R.id.subject_home);
            viewHolder.lastDate_submission = (TextView) cell.findViewById(R.id.lastSubmission_date);
            viewHolder.homeworkDate = (TextView) cell.findViewById(R.id.homework_date);
            viewHolder.circleImage = (ImageView) cell.findViewById(R.id.circlecell);
            viewHolder.homework_title_content = (TextView) cell.findViewById(R.id.title_home_content);
            viewHolder.subject_home_content = (TextView) cell.findViewById(R.id.subject_home_content);
            viewHolder.circleImage_content = (ImageView) cell.findViewById(R.id.circlecell_content);
            if(item.getUserUpload()== Integer.parseInt(userDataSp.getUserData(UserDataSP.NUMBER_USER))) {
                viewHolder.deleteHomework.setVisibility(View.VISIBLE);
               viewHolder.deleteHomework.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteHomework(position, item);
                    }
                });
            }
            cell.setTag(viewHolder);
        } else {
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }

        viewHolder.homework_title.setText(item.getHomework_title() + "");
        viewHolder.homework_contents.setText(item.getHomework_contents() + "");
        viewHolder.subject.setText(item.getSubject() + "");
        viewHolder.lastDate_submission.setText(item.getLastDate_submission().split(" ")[0] + "");
        viewHolder.homeworkDate.setText(item.getHomeworkDate().split(" ")[0] + "");
        String firstletter = String.valueOf(item.getHomework_title().charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(getItem(position));
        TextDrawable drawable = TextDrawable.builder().buildRound(firstletter.toUpperCase(), color);
        viewHolder.circleImage.setImageDrawable(drawable);
        viewHolder.circleImage_content.setImageDrawable(drawable);
        viewHolder.subject_home_content.setText(item.getSubject());
        viewHolder.homework_title_content.setText(item.getHomework_title());

        return cell;
    }

    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }

 public void deleteHomework(final int position, final Item item)
 {               android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getContext());
                 alert.setTitle("Alert");
                 alert.setMessage("Are you sure to delete record");
                 alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         myVolley = new MyVolley(getContext(), new IVolleyResponse() {
                             @Override
                             public void volleyResponse(String result) {
                                 Log.e("after delete", result);


                                 if(result.contains("DONE"))
                                 {remove(getItem(position));
                                     notifyDataSetChanged();
                                     Toast.makeText(getContext(), ""+result, Toast.LENGTH_SHORT).show();
                                 }

                             }
                         });

                         myVolley.setUrl(Utils.HOMEWORK_DELETE);
                         myVolley.setParams(UserDataSP.NUMBER_USER, userDataSp.getUserData(UserDataSP.NUMBER_USER) );
                         myVolley.setParams(UserDataSP.SCHOOL_NUMBER, userDataSp.getUserData(UserDataSP.SCHOOL_NUMBER));
                         myVolley.setParams("homework_number", item.getHomework_number());
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

    private class MyComparator implements Comparator<Item>{

        @Override
        public int compare(Item lhs, Item rhs) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                Date date1 = dateFormat.parse(rhs.getHomeworkDate());
                Date date2 = dateFormat.parse(lhs.getHomeworkDate());
                Log.e("date1",date2.compareTo(date1)+"") ;
                return date1.compareTo(date2);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }



    // View lookup cache
    public static class ViewHolder {
        TextView homework_title;
        TextView homework_contents;
        TextView lastDate_submission;
        TextView subject;
        TextView homeworkDate;
        ImageView circleImage;
        TextView homework_title_content;
        TextView subject_home_content;
        ImageView circleImage_content;
        TextView deleteHomework;

    }
}
