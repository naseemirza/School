package com.lead.infosystems.schooldiary.Suggestion;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.SchoolDiary.FoldingCellListAdapter;
import com.lead.infosystems.schooldiary.SchoolDiary.Item;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;
import com.ramotion.foldingcell.FoldingCell;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Naseem on 02-01-2017.
 */

public class FloadingListAdapter extends ArrayAdapter<sc_items>{
    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;

    public FloadingListAdapter(Context context, List<sc_items> objects) {
        super(context,0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        sc_items item = getItem(position);
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.cell_suggestion, parent, false);
            cell.initialize(1000, Color.DKGRAY, 2);

            viewHolder.name_title = (TextView) cell.findViewById(R.id.sugg_name);
            viewHolder.name_content = (TextView)cell.findViewById(R.id.sugg_name_content);
            viewHolder.sug_title_t= (TextView)cell.findViewById(R.id.sugg_title) ;
            viewHolder.class_title=(TextView)cell.findViewById(R.id.sugg_class);
            viewHolder.division_title=(TextView)cell.findViewById(R.id.sugg_division);
            viewHolder.sugg_title_c = (TextView) cell.findViewById(R.id.sugg_title_content);
            viewHolder.sugg_content = (TextView)cell.findViewById(R.id.suggestion_content);
            viewHolder.sugg_date = (TextView)cell.findViewById(R.id.sugg_date);
            viewHolder.class_content=(TextView)cell.findViewById(R.id.sugg_class_content);
            viewHolder.division_content=(TextView)cell.findViewById(R.id.sugg_division_content);
            viewHolder.title_image=(ImageView)cell.findViewById(R.id.circlecell_sugT);
            viewHolder.content_image=(ImageView)cell.findViewById(R.id.circlecell_sugC);
            cell.setTag(viewHolder);
        }
        else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (FloadingListAdapter.ViewHolder) cell.getTag();
        }


         viewHolder.name_title.setText(item.getFirst_name()+" "+item.getLast_name()+"");
        viewHolder.name_content.setText(item.getFirst_name()+" "+item.getLast_name()+"");
        viewHolder.class_title.setText("Class: "+""+item.getClass_t()+"");
        viewHolder.division_title.setText("Division: "+""+item.getDivision_t()+"");
        viewHolder.sug_title_t.setText(item.getSubject()+"");
        viewHolder.sugg_title_c.setText(item.getSubject()+"");
        viewHolder.sugg_content.setText(item.getContent()+"");
        viewHolder.sugg_date.setText(item.getDate()+"");
        viewHolder.class_content.setText("Class: "+""+item.getClass_t()+"");
        viewHolder.division_content.setText("Division: "+""+item.getDivision_t()+"");
        Picasso.with(getContext()).load(Utils.SERVER_URL+item.getProfilePic_link()).into(viewHolder.title_image);
        Picasso.with(getContext()).load(Utils.SERVER_URL+item.getProfilePic_link()).into(viewHolder.content_image);


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

    public static class ViewHolder {
        TextView name_title;
        TextView name_content;
        TextView class_title;
        TextView division_title;
        TextView sug_title_t;
        TextView sugg_title_c;
        TextView sugg_content;
        TextView sugg_date;
        TextView class_content;
        TextView division_content;
        ImageView title_image;
        ImageView content_image;

    }
}
