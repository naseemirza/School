package com.lead.infosystems.schooldiary.SchoolDiary;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.IVolleyResponse;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.MyVolley;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FoldingCellListAdapter  extends ArrayAdapter<Item> {
    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;
     UserDataSP userDataSp = new UserDataSP(getContext());
     private MyVolley myVolley;
    private ArrayList<Item> items = new ArrayList<>();
    FoldingCellListAdapter adapter;
    public FoldingCellListAdapter(Context context, List<Item> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Item item = getItem(position);
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.home_work_diery_item, parent, false);
            cell.initialize(1000, Color.DKGRAY, 2);
            viewHolder.homework_title = (TextView) cell.findViewById(R.id.title_home);
            viewHolder.homework_contents = (TextView) cell.findViewById(R.id.content_home);
            viewHolder.delete_homework = (ImageButton) cell.findViewById(R.id.homework_delete_content);
           // viewHolder.delete_homework_title = (ImageButton)cell.findViewById(R.id.homework_delete_title);
            viewHolder.subject = (TextView) cell.findViewById(R.id.subject_home);
            viewHolder.lastDate_submission = (TextView) cell.findViewById(R.id.lastSubmission_date);
            viewHolder.homeworkDate = (TextView) cell.findViewById(R.id.homework_date);
            viewHolder.circleImage = (ImageView)cell.findViewById(R.id.circlecell);
            viewHolder.homework_title_content = (TextView)cell.findViewById(R.id.title_home_content);
            viewHolder.subject_home_content = (TextView)cell.findViewById(R.id.subject_home_content);
            viewHolder.circleImage_content = (ImageView)cell.findViewById(R.id.circlecell_content);
            cell.setTag(viewHolder);
        } else {
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }

        viewHolder.homework_title.setText(item.getHomework_title()+"");
        viewHolder.homework_contents.setText(item.getHomework_contents()+"");
        viewHolder.subject.setText(item.getSubject()+"");
        viewHolder.lastDate_submission.setText(item.getLastDate_submission()+"");
        viewHolder.homeworkDate.setText(item.getHomeworkDate()+"");
        String firstletter = String.valueOf(item.getHomework_title().charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(getItem(position));
        TextDrawable drawable = TextDrawable.builder().buildRound(firstletter.toUpperCase(),color);
         viewHolder.circleImage.setImageDrawable(drawable);
        viewHolder.circleImage_content.setImageDrawable(drawable);
        viewHolder.subject_home_content.setText(item.getSubject());
        viewHolder.homework_title_content.setText(item.getHomework_title());

        if(item.getUserUpload()== Integer.parseInt(userDataSp.getUserData(UserDataSP.NUMBER_USER))) {
            viewHolder.delete_homework.setVisibility(View.VISIBLE);
            viewHolder.delete_homework.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteHomework(position, item);
                }
            });
//            viewHolder.delete_homework_title.setVisibility(View.VISIBLE);
//            viewHolder.delete_homework_title.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    deleteHomework(position, item);
//                }
//            });
        }

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

    public View.OnClickListener getDefaultRequestBtnClickListener() {
        return defaultRequestBtnClickListener;
    }

    public void setDefaultRequestBtnClickListener(View.OnClickListener defaultRequestBtnClickListener) {
        this.defaultRequestBtnClickListener = defaultRequestBtnClickListener;
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
                             public void volleyResponce(String result) {
                                 Log.e("after delete", result);


                                 if(result.contains("DONE"))
                                 {
                                     items.remove(position);
                                     adapter.notifyDataSetChanged();
                                     Toast.makeText(getContext(), ""+result, Toast.LENGTH_SHORT).show();
                                 }

                             }
                         });

                         myVolley.setUrl(Utils.HOMEWORK_DELETE);
                         myVolley.setParams(UserDataSP.NUMBER_USER, userDataSp.getUserData(UserDataSP.NUMBER_USER) );
                         myVolley.setParams(UserDataSP.SCHOOL_NUMBER, userDataSp.getUserData(UserDataSP.SCHOOL_NUMBER));
                         myVolley.setParams(UserDataSP.CLASS, userDataSp.getUserData(UserDataSP.CLASS));
                         myVolley.setParams(UserDataSP.DIVISION, userDataSp.getUserData(UserDataSP.DIVISION));
                         myVolley.setParams("subject", item.getSubject());
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
        ImageButton delete_homework;
       // ImageButton delete_homework_title;

    }
}
