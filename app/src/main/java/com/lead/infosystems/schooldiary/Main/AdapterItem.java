package com.lead.infosystems.schooldiary.Main;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.CursorAnchorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.Post_Data;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.ServerConnect;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterItem extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private ArrayList<Post_Data> itemList;

    private OnLoadMoreListener onLoadMoreListener;
    private LinearLayoutManager mLinearLayoutManager;
    private Context context;
    private UserDataSP userDataSP;
    private boolean canClickLike = true;
    private boolean isMoreLoading = false;
    private int visibleThreshold = 1;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    public interface OnLoadMoreListener {
        void onLoadMore();
        void onCommentClick(String post_id);
    }

    public AdapterItem(OnLoadMoreListener onLoadMoreListener, Activity activity) {
        this.onLoadMoreListener = onLoadMoreListener;
        this.context = activity.getApplicationContext();
        userDataSP = new UserDataSP(context);
        itemList = new ArrayList<>();
    }

    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    public void setRecyclerView(RecyclerView mView) {
        mView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (!isMoreLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
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
            return new StudentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false));
        } else {
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false));
        }

    }

    public void addAll(List<Post_Data> lst) {
        itemList.clear();
        itemList.addAll(lst);
        notifyDataSetChanged();
    }

    public void addItemMore(List<Post_Data> lst) {
        itemList.addAll(lst);
        notifyItemRangeChanged(0, itemList.size());
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof StudentViewHolder) {
          final Post_Data singleItem = (Post_Data) itemList.get(position);
            ((StudentViewHolder) holder).name.setText(singleItem.getFirst_name() + " " + singleItem.getLast_name());
            ((StudentViewHolder) holder).time.setText(getTimeString(singleItem.getTimeInmilisec()));
            ((StudentViewHolder) holder).text.setText(singleItem.getText_message());
            if (singleItem.getSrc_link().length()>5) {
                ((StudentViewHolder) holder).postImage.setVisibility(View.VISIBLE);
                Picasso.with(context).load(singleItem.getSrc_link())
                        .into(((StudentViewHolder) holder).postImage);
            } else {
                ((StudentViewHolder) holder).postImage.setVisibility(View.GONE);
            }
            ((StudentViewHolder) holder).like_num.setText(singleItem.numLikes());
            if(singleItem.isLiked()){
                ((StudentViewHolder) holder).like_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_liked));
            }else{
                ((StudentViewHolder) holder).like_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_not_liked));
            }
            ((StudentViewHolder) holder).like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(canClickLike){
                        canClickLike = false;
                        if(singleItem.isLiked()){
                            canClickLike = false;
                            Uri.Builder builder = new Uri.Builder();
                            builder.appendQueryParameter("post_id",singleItem.getId());
                            builder.appendQueryParameter("student_number",userDataSP.getUserData(UserDataSP.STUDENT_NUMBER));
                            new ActionConnect(Utils.DELETE_PL,singleItem,((StudentViewHolder) holder)).execute(builder.build().getQuery());
                        }else{
                            Uri.Builder builder = new Uri.Builder();
                            builder.appendQueryParameter("post_id",singleItem.getId());
                            builder.appendQueryParameter("student_number",userDataSP.getUserData(UserDataSP.STUDENT_NUMBER));
                            new ActionConnect(Utils.POST_LIKE,singleItem,((StudentViewHolder) holder)).execute(builder.build().getQuery());
                        }
                    }
                }
            });
            ((StudentViewHolder) holder).comment_num.setText(singleItem.getCommentsNum());
            ((StudentViewHolder) holder).comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLoadMoreListener.onCommentClick(singleItem.getId());
                }
            });

        }
    }

    private String getTimeString(long postTime){
        String time = "";
        long seconds = (System.currentTimeMillis() - postTime)/1000;

            if(seconds < 60 ){
                time  = seconds + " seconds ago";
            }else if(seconds <  60 * 60){
                time = (int)(seconds /60)+" min ago";
            }else if(seconds < 86400){
                time = (int) (seconds / (60*60))+"hr ago";
            }else if(seconds < 604800){
                time = (int) (seconds / (60*60*24)) + " days ago";
            }else if(seconds > 518400) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-dd,  hh:mm a");
                time = simpleDateFormat.format(new Date(postTime)).replace("  ", " at ");
            } else if((seconds/(60*60*24*30))>365){
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MMM-dd,  hh:mm a");
                time = simpleDateFormat.format(new Date(postTime)).replace("  ", " at ");
            }
        return time;
    }
    public void setMoreLoading(boolean isMoreLoading) {
        this.isMoreLoading = isMoreLoading;
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
            if(itemList.size() != 0){
            itemList.remove(itemList.size() - 1);
            }
            notifyItemRemoved(itemList.size());
        }
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView name, time, text, like_text, like_num, comment_num;
        public ImageView postImage, like_icon;
        public LinearLayout like,comment;

        public StudentViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            time = (TextView) v.findViewById(R.id.time);
            text = (TextView) v.findViewById(R.id.text);
            postImage = (ImageView) v.findViewById(R.id.postimage);

            like = (LinearLayout) v.findViewById(R.id.like);
            like_text = (TextView) v.findViewById(R.id.like_text);
            like_num = (TextView) v.findViewById(R.id.like_num);
            like_icon = (ImageView) v.findViewById(R.id.like_icon);

            comment = (LinearLayout) v.findViewById(R.id.comment);
            comment_num = (TextView) v.findViewById(R.id.comment_num);

        }
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar pBar;

        public ProgressViewHolder(View v) {
            super(v);
            pBar = (ProgressBar) v.findViewById(R.id.pBar);
        }
    }

    class ActionConnect extends AsyncTask<String,Void,String>{


        String urlFile;
        Post_Data item;
        StudentViewHolder holder;
        public ActionConnect(String urlFile, Post_Data item, StudentViewHolder holder) {
            this.urlFile = urlFile;
            this.item = item;
            this.holder = holder;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                return ServerConnect.downloadUrl(urlFile,params[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return  null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("result",s);
            if(s != null){
                s = s.trim();
                if(urlFile == Utils.POST_LIKE) {
                    canClickLike =true;
                    if(s.contains("DONE")){
                        Toast.makeText(context.getApplicationContext(),"Liked",Toast.LENGTH_SHORT).show();
                        item.setLiked(true,userDataSP.getUserData(UserDataSP.STUDENT_NUMBER));
                        holder.like_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_liked));
                        holder.like_num.setText(item.numLikes());
                    }else {
                        Toast.makeText(context.getApplicationContext(),"Connection Error..",Toast.LENGTH_SHORT).show();
                    }
                }else if(urlFile == Utils.DELETE_PL){
                    canClickLike = true;
                    if(s.contains("DONE")){
                        Toast.makeText(context.getApplicationContext(),"Unliked",Toast.LENGTH_SHORT).show();
                        item.setLiked(false,userDataSP.getUserData(UserDataSP.STUDENT_NUMBER));
                        holder.like_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_not_liked));
                        holder.like_num.setText(item.numLikes());
                    }else {
                        Toast.makeText(context.getApplicationContext(),"Connection Error..",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

}