package com.lead.infosystems.schooldiary.Management;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;
import com.ramotion.foldingcell.FoldingCell;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;

public class ExpandableCellListAdapter extends ArrayAdapter<ItemDetail> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private View.OnClickListener defaultRequestBtnClickListener;


    public ExpandableCellListAdapter(Context context, List<ItemDetail> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ItemDetail item = getItem(position);
        FoldingCell cell = (FoldingCell) convertView;
        ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.management_layout_item, parent, false);
            viewHolder.nameTitle = (TextView) cell.findViewById(R.id.teacher_name_title);
            viewHolder.nameContent = (TextView) cell.findViewById(R.id.teacher_name_content);
            viewHolder.mobileN = (TextView) cell.findViewById(R.id.mobile_no);
            viewHolder.gmailId = (TextView) cell.findViewById(R.id.gmail);
            viewHolder.designation= (TextView) cell.findViewById(R.id.designation_t);
            viewHolder.profilImageTitle= (ImageView)cell.findViewById(R.id.profile_title);
            viewHolder.qualifications = (TextView)cell.findViewById(R.id.qualification_t);
            viewHolder.interests_field= (TextView)cell.findViewById(R.id.field);
            viewHolder.profileImageContent = (ImageView)cell.findViewById(R.id.profile_content);
            viewHolder.contact_detail = (TextView)cell.findViewById(R.id.contactDetail);
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

        viewHolder.nameTitle.setText(item.getFirstName()+item.getLastName()+"");
        viewHolder.nameContent.setText(item.getFirstName()+item.getLastName()+"");
        viewHolder.mobileN.setText(item.getMobile_no()+"");
        viewHolder.designation.setText(item.getDesignation()+"");
        viewHolder.gmailId.setText(item.getGmail_id()+"");
        viewHolder.qualifications.setText(item.getQualifications()+"");
        viewHolder.interests_field.setText(item.getInterests_field()+"");
        viewHolder.contact_detail.setText(item.getContact_detail()+"");
        Picasso.with(getContext()).load(Utils.SERVER_URL+item.getProfil_pic()).into(viewHolder.profilImageTitle);
        Picasso.with(getContext()).load(Utils.SERVER_URL+item.getProfil_pic()).into(viewHolder.profileImageContent);
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
        TextView nameTitle;
        TextView mobileN;
        TextView gmailId;
        TextView designation;
        TextView qualifications;
        ImageView profilImageTitle;
        TextView interests_field;
        TextView contact_detail;
        ImageView profileImageContent;
        TextView nameContent;

    }
}
