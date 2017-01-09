package com.lead.infosystems.schooldiary.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.Data.Post_Data;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.IPostInterface;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.Generic.ServerConnect;
import com.lead.infosystems.schooldiary.Generic.Utils;
import com.lead.infosystems.schooldiary.ShareButton.Login_fb;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PostAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private static ArrayList<Post_Data> itemList;

    private IPostInterface iPostInterface;
    private LinearLayoutManager mLinearLayoutManager;
    private Context context;
    Activity activity;
    private UserDataSP userDataSP;
    private boolean canClickLike = true;
    private boolean isMoreLoading = false;
    private int visibleThreshold = 1;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    public static PostAnimData postAnimData;


    public PostAdaptor(IPostInterface iPostInterface, Activity activity) {
        this.iPostInterface = iPostInterface;
        this.activity = activity;
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
                    if (iPostInterface != null) {
                        iPostInterface.onLoadMore();
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

    public void addItemAtTop(List<Post_Data> lst){
        itemList.addAll(0,lst);
        notifyItemRangeChanged(0,itemList.size());
    }

    public void deleteItem(int position){
        itemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0,itemList.size());
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof StudentViewHolder) {
            final Bitmap[] postImageBitmap = new Bitmap[1];
            final boolean[] isImageAvailable = new boolean[1];
            final Post_Data singleItem = (Post_Data) itemList.get(position);
            final ImageView propic = (ImageView) ((StudentViewHolder) holder).v.findViewById(R.id.propic);
            if(singleItem.getPropicLink() != null && singleItem.getPropicLink().contains("jpeg")){
                Picasso.with(context).load(Utils.SERVER_URL+singleItem.getPropicLink().replace("profilepic","propic_thumb"))
                        .networkPolicy(ServerConnect.checkInternetConenction(activity) ?
                                NetworkPolicy.NO_CACHE : NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.defaultpropic)
                        .into(propic);
            }else{
                propic.setImageDrawable(activity.getResources().getDrawable(R.drawable.defaultpropic));
            }
            ((StudentViewHolder) holder).name.setText(singleItem.getFirst_name() + " " + singleItem.getLast_name());
            ((StudentViewHolder) holder).time.setText(Utils.getTimeString(singleItem.gettimeString()));
            ((StudentViewHolder) holder).text.setText(singleItem.getText_message());
            if (singleItem.getSrc_link().length()>10) {
                ((StudentViewHolder) holder).postImage.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(singleItem.getSrc_link())
                        .into(((StudentViewHolder) holder).postImage);
                        isImageAvailable[0] = true;
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        try {
                            postImageBitmap[0] = Picasso.with(context)
                                    .load(singleItem.getSrc_link()).get();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            } else {
                ((StudentViewHolder) holder).postImage.setVisibility(View.GONE);
                isImageAvailable[0] = false;
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
                            builder.appendQueryParameter("number_user",userDataSP.getUserData(UserDataSP.NUMBER_USER));
                            new ActionConnect(Utils.DELETE,singleItem,((StudentViewHolder) holder),position)
                                    .execute(builder.build().getQuery());
                        }else{
                            Uri.Builder builder = new Uri.Builder();
                            builder.appendQueryParameter("post_id",singleItem.getId());
                            builder.appendQueryParameter("number_user",userDataSP.getUserData(UserDataSP.NUMBER_USER));
                            new ActionConnect(Utils.LIKE,singleItem,((StudentViewHolder) holder),position)
                                    .execute(builder.build().getQuery());
                        }
                }
                }
            });
            ((StudentViewHolder) holder).share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(postImageBitmap[0] != null){
                        new Login_fb(context,MainActivity.fb,postImageBitmap[0],singleItem.getSrc_link()
                                ,((StudentViewHolder) holder).text.getText().toString()).login();
                    }else{
                        Toast.makeText(context,"No Image to Share",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            ((StudentViewHolder) holder).comment_num.setText(singleItem.getCommentsNum()+"");
            ((StudentViewHolder) holder).comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postAnimData = new PostAnimData(((StudentViewHolder) holder).propic,
                            ((StudentViewHolder) holder).name
                            ,((StudentViewHolder) holder).time
                            , ((StudentViewHolder) holder).text
                            ,((StudentViewHolder) holder).postImage
                            , ((StudentViewHolder) holder).postCardView
                            ,((StudentViewHolder) holder).comment_num
                            ,postImageBitmap[0]
                            , isImageAvailable[0]
                            ,singleItem.getId()
                            ,singleItem
                            ,position);
                    iPostInterface.onCommentClick(postAnimData);

                }
            });

            if(singleItem.getStudentNum() == Integer.parseInt(userDataSP.getUserData(UserDataSP.NUMBER_USER))){
                ((StudentViewHolder) holder).deleteBtn.setVisibility(View.VISIBLE);
                ((StudentViewHolder) holder).deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //delete post
                        final CharSequence[] item = { "Delete"};
                        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                        dialog.setItems(item, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri.Builder builder = new Uri.Builder();
                                builder.appendQueryParameter("post_id",singleItem.getId());
                                new ActionConnect(Utils.DELETE, null, null,position).execute(builder.build().getQuery());
                            }
                        });
                        dialog.show();

                    }
                });
            }else{
                ((StudentViewHolder) holder).deleteBtn.setVisibility(View.GONE);
            }

        }
    }

//    public static void updateCommentNum(TextView comment_num, int position, int a){
//        itemList.get(position).setCommentsNum(itemList.get(position).getCommentsNum() + a);
//        commentNum.setText(itemList.get(position).getCommentsNum()+"");
//    }
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
        public ImageView postImage, like_icon , propic;
        public LinearLayout like,comment,share;
        public CardView postCardView;
        public ImageButton deleteBtn;
        public View v;

        public StudentViewHolder(View v) {
            super(v);
            this.v = v;
            propic = (ImageView) v.findViewById(R.id.propic);
            postCardView = (CardView) v.findViewById(R.id.post_card_view);
            name = (TextView) v.findViewById(R.id.title);
            time = (TextView) v.findViewById(R.id.time_rcv);
            text = (TextView) v.findViewById(R.id.text);
            postImage = (ImageView) v.findViewById(R.id.postimage);

            like = (LinearLayout) v.findViewById(R.id.like);
            like_text = (TextView) v.findViewById(R.id.like_text);
            like_num = (TextView) v.findViewById(R.id.like_num);
            like_icon = (ImageView) v.findViewById(R.id.like_icon);

            comment = (LinearLayout) v.findViewById(R.id.comment);
            comment_num = (TextView) v.findViewById(R.id.comment_num);

            share = (LinearLayout) v.findViewById(R.id.share);

            postCardView = (CardView) v.findViewById(R.id.post_card_view);

            deleteBtn = (ImageButton) v.findViewById(R.id.delete);

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
        int position;
        Post_Data item;
        StudentViewHolder holder;
        public ActionConnect(String urlFile, Post_Data item, StudentViewHolder holder,int position) {
            this.urlFile = urlFile;
            this.item = item;
            this.holder = holder;
            this.position = position;
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
            if(s != null){
                s = s.trim();
                if(urlFile == Utils.LIKE) {
                    canClickLike =true;
                    if(s.contains("DONE")){
                        Toast.makeText(context.getApplicationContext(),"Liked",Toast.LENGTH_SHORT).show();
                        item.setLiked(true,userDataSP.getUserData(UserDataSP.NUMBER_USER));
                        holder.like_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_liked));
                        holder.like_num.setText(item.numLikes());
                    }else {
                        Toast.makeText(context.getApplicationContext(),"Connection Error..",Toast.LENGTH_SHORT).show();
                    }
                }else if(urlFile == Utils.DELETE && holder != null){
                    //remove like
                    canClickLike = true;
                    if(s.contains("DONE")){
                        Toast.makeText(context.getApplicationContext(),"Unliked",Toast.LENGTH_SHORT).show();
                        item.setLiked(false,userDataSP.getUserData(UserDataSP.NUMBER_USER));
                        holder.like_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_not_liked));
                        holder.like_num.setText(item.numLikes());
                    }else {
                        Toast.makeText(context.getApplicationContext(),"Connection Error..",Toast.LENGTH_SHORT).show();
                    }
                }else if(urlFile == Utils.DELETE && holder == null){
                    //delete post
                    deleteItem(position);
                }
            }
        }
    }

}