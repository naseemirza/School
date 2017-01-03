package com.lead.infosystems.schooldiary.ServerConnection;

import android.content.Context;
import android.util.Log;
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
import com.lead.infosystems.schooldiary.IVolleyResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Faheem on 16-12-2016.
 */

public class MyVolley {
    private String url;
    private HashMap<String,String> params;
    private Context context;
    private int RETRY_NUM = DefaultRetryPolicy.DEFAULT_MAX_RETRIES;
    private IVolleyResponse iVolleyResponse;
    public static final String RESPONSE_ERROR="response_error";



    public MyVolley(Context context, IVolleyResponse iVolleyResponse) {
        this.context = context;
        this.iVolleyResponse = iVolleyResponse;
        params = new HashMap<>();
    }

    public void setParams(String KEY, String VALUE) {
        params.put(KEY,VALUE);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRETRY_NUM(int RETRY_NUM) {
        this.RETRY_NUM = RETRY_NUM;
    }

    public void connect(){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                clear();
                Log.e("res",response);
                if(response != null && !response.contentEquals("ERROR")){
                    iVolleyResponse.volleyResponce(response);
                }else{
                    iVolleyResponse.volleyResponce(RESPONSE_ERROR);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
             clear();
//                Toast toast = new Toast(context);
//                toast.cancel();
//                toast.setText(ServerConnect.connectionError(error));
//                toast.setDuration(Toast.LENGTH_SHORT);
//                toast.show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        RetryPolicy retryPolicy = new DefaultRetryPolicy(2000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(retryPolicy);
        requestQueue.add(request);
    }
    public void clear(){
        params = new HashMap<String,String>();
        url = null;
    }
}
