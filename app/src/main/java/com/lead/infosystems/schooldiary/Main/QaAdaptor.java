package com.lead.infosystems.schooldiary.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lead.infosystems.schooldiary.Data.QaData;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;
import com.lead.infosystems.schooldiary.ServerConnection.ServerConnect;
import com.lead.infosystems.schooldiary.ServerConnection.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QaAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private ArrayList<QaData> itemList;

    private OnLoadMoreListener onLoadMoreListener;
    private LinearLayoutManager mLinearLayoutManager;
    private Activity activity;
    private UserDataSP userDataSP;

    private boolean isMoreLoading = false;
    private int visibleThreshold = 1;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    public interface OnLoadMoreListener{
        void onLoadMore();
        void onAnswerClick(QaAnimData qaAnimData);
    }

    public QaAdaptor(OnLoadMoreListener onLoadMoreListener,Activity activity) {
        this.onLoadMoreListener=onLoadMoreListener;
        itemList = new ArrayList<>();
        this.activity = activity;
        userDataSP = new UserDataSP(activity.getApplicationContext());
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof StudentViewHolder) {
            final QaData singleItem = itemList.get(position);
            ((StudentViewHolder) holder).name.setText(singleItem.getName());
            ((StudentViewHolder) holder).time.setText(singleItem.getTime());
            ((StudentViewHolder) holder).question_text.setText(singleItem.getQuestionText());
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

            if(Integer.parseInt(itemList.get(position).getStudent_number()) ==
                    Integer.parseInt(userDataSP.getUserData(UserDataSP.NUMBER_USER))){
                ((StudentViewHolder) holder).answerDelete.setVisibility(View.VISIBLE);
            }else{
                ((StudentViewHolder) holder).answerDelete.setVisibility(View.GONE);
            }

            ((StudentViewHolder) holder).answerDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CharSequence[] item = { "Delete"};
                    AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
                    dialog.setItems(item, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            connect(Utils.QA_DELETE,itemList.get(position).getStudent_number()
                                    ,itemList.get(position).getqNum(),position);
                        }
                    });
                    dialog.show();
                }
            });
        }
    }

    private void connect(String url, final String studentNum, final String questionNum, final int pos){
        RequestQueue requestQueue = Volley.newRequestQueue(activity.getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response != null && response.contains("DONE")){
                    deleteItem(pos);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               Toast.makeText(activity.getApplicationContext(),ServerConnect.connectionError(error),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("question_number",studentNum);
                map.put("number_user",questionNum);
                return map;
            }
        };
        RetryPolicy retry = new DefaultRetryPolicy(2000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retry);
        requestQueue.add(request);
    }

    private void deleteItem(int pos) {
        itemList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(0,itemList.size());
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
        TextView name, time, question_text;
        Button answerBtn;
        ImageView propic;
        ImageButton answerDelete;

        public StudentViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.name);
            time = (TextView) v.findViewById(R.id.time_rcv);
            question_text = (TextView) v.findViewById(R.id.question_text);
            propic = (ImageView) v.findViewById(R.id.profile_image);
            answerBtn = (Button) v.findViewById(R.id.answer_btn);
            answerDelete = (ImageButton) v.findViewById(R.id.delete_answer);
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