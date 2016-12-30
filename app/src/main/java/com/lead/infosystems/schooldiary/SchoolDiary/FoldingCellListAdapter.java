package com.lead.infosystems.schooldiary.SchoolDiary;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.lead.infosystems.schooldiary.R;
import com.ramotion.foldingcell.FoldingCell;

import java.util.HashSet;
import java.util.List;

public class FoldingCellListAdapter  extends ArrayAdapter<Item> {
    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;


    public FoldingCellListAdapter(Context context, List<Item> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Item item = getItem(position);
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.home_work_diery_item, parent, false);
            cell.initialize(1000, Color.DKGRAY, 2);
            viewHolder.homework_title = (TextView) cell.findViewById(R.id.title_home);
            viewHolder.homework_contents = (TextView) cell.findViewById(R.id.content_home);
            viewHolder.subject = (TextView) cell.findViewById(R.id.subject_home);
            viewHolder.lastDate_submission = (TextView) cell.findViewById(R.id.lastSubmission_date);
            viewHolder.homeworkDate = (TextView) cell.findViewById(R.id.homework_date);
            viewHolder.circleImage = (ImageView)cell.findViewById(R.id.circlecell);
            viewHolder.homework_title_content = (TextView)cell.findViewById(R.id.title_home_content);
            viewHolder.subject_home_content = (TextView)cell.findViewById(R.id.subject_home_content);
            viewHolder.circleImage_content = (ImageView)cell.findViewById(R.id.circlecell_content);
            cell.setTag(viewHolder);
        } else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }

        // bind data from selected element to view through view holder

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

        return cell;
    }

    // simple methods for register cell state changes
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

    // View lookup cache
    private static class ViewHolder {
        TextView homework_title;
        TextView homework_contents;
        TextView lastDate_submission;
        TextView subject;
        TextView homeworkDate;
        ImageView circleImage;
        TextView homework_title_content;
        TextView subject_home_content;
        ImageView circleImage_content;

    }
}
