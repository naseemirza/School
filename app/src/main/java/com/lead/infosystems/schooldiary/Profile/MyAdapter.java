package com.lead.infosystems.schooldiary.Profile;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.Data.QuestionData;
import com.lead.infosystems.schooldiary.R;

import java.util.ArrayList;

public class MyAdapter  extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private ArrayList<QuestionData> data;

    public MyAdapter(ArrayList<QuestionData> mydata){
        data = mydata;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        QuestionData currentitem = data.get(position);
        holder.name.setText(currentitem.getname());
        holder.date.setText(currentitem.getDate());
        holder.question.setText(currentitem.getQuestion());
        holder.img.setImageDrawable(currentitem.getDrawable());
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView date;
        TextView question;
        ImageView img;
        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            date = (TextView) itemView.findViewById(R.id.time);
            question = (TextView) itemView.findViewById(R.id.question_text);
            img  = (ImageView) itemView.findViewById(R.id.postimage);


        }
    }
}
