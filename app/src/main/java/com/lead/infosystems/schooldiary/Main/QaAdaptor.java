package com.lead.infosystems.schooldiary.Main;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.Data.QaData;
import com.lead.infosystems.schooldiary.R;

import java.util.ArrayList;
import java.util.List;

public class QaAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private ArrayList<QaData> itemList;

    private OnLoadMoreListener onLoadMoreListener;
    private LinearLayoutManager mLinearLayoutManager;

    private boolean isMoreLoading = false;
    private int visibleThreshold = 1;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    public interface OnLoadMoreListener{
        void onLoadMore();
        void onAnswerClick(QaAnimData qaAnimData);
    }

    public QaAdaptor(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener=onLoadMoreListener;
        itemList = new ArrayList<>();
    }

    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager){
        this.mLinearLayoutManager=linearLayoutManager;
    }

    public void setRecyclerView(RecyclerView mView){
        mView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (!isMoreLoading && (totalItemCount - visibleItemCount)<= (firstVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isMoreLoading = true;
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            return new StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.question_ans_item, parent, false));
        } else {
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false));
        }

    }

    public void addAll(List<QaData> lst){
        itemList.clear();
        itemList.addAll(lst);
        notifyDataSetChanged();
    }

    public void addItemMore(List<QaData> lst){
        itemList.addAll(lst);
        notifyItemRangeChanged(0,itemList.size());
    }
    public void addItemAtTop(List<QaData> lst){
        itemList.addAll(0,lst);
        notifyItemRangeChanged(0,itemList.size());
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StudentViewHolder) {
            final QaData singleItem = itemList.get(position);
            ((StudentViewHolder) holder).name.setText(singleItem.getName());
            ((StudentViewHolder) holder).time.setText(singleItem.getTime());
            ((StudentViewHolder) holder).question_text.setText(singleItem.getQuestionText());
            ((StudentViewHolder) holder).answerNum.setText((singleItem.getNumAnswers() == "0")?
                    "No Answers":singleItem.getNumAnswers()+" Answer");
            ((StudentViewHolder) holder).answerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLoadMoreListener.onAnswerClick(new QaAnimData(((StudentViewHolder) holder).name
                            ,((StudentViewHolder) holder).time
                            ,((StudentViewHolder) holder).question_text
                            ,((StudentViewHolder) holder).propic
                            ,singleItem.getqNum()));
                }
            });
        }
    }

    public void setMoreLoading(boolean isMoreLoading) {
        this.isMoreLoading=isMoreLoading;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setProgressMore(final boolean isProgress) {
        if (isProgress) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    itemList.add(null);
                    notifyItemInserted(itemList.size() - 1);
                }
            });
        } else {
            itemList.remove(itemList.size() - 1);
            notifyItemRemoved(itemList.size());
        }
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, question_text,answerNum;
        Button answerBtn;
        ImageView propic;

        public StudentViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            time = (TextView) v.findViewById(R.id.time);
            question_text = (TextView) v.findViewById(R.id.question_text);
            propic = (ImageView) v.findViewById(R.id.profile_image);
            answerBtn = (Button) v.findViewById(R.id.answer_btn);
            answerNum = (TextView) v.findViewById(R.id.answer_num);
        }
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar pBar;
        public ProgressViewHolder(View v) {
            super(v);
            pBar = (ProgressBar) v.findViewById(R.id.pBar);
        }
    }
}