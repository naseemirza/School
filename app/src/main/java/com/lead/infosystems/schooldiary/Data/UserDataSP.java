package com.lead.infosystems.schooldiary.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserDataSP {



    public static final String NUMBER_USER = "number_user";
    public static final String STUDENT_NUMBER = "student_number";
    public static final String SCHOOL_NUMBER = "school_number";
    public static final String DIVISION = "division";
    public static final String ROLL_NO = "roll_number";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String FATHERS_NAME = "father_name";
    public static final String MOBILE_NUMBER = "mobile";
    public static final String ADDRESS = "address";
    public static final String EMAIL_ID = "gmail_id";
    public static final String BLOOD_GROUP = "blood_group";
    public static final String CLASS = "class";
    public static final String CLOUD_ID = "cloud_id";
    public static final String POST_DATA = "POST_DATA";
    public static final String QA_DATA = "qa_data";
    public static final String SCHOOL_FEES = "school_fees";


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public UserDataSP(Context context) {
        sharedPreferences = context.getSharedPreferences("USER_SP",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    public void storeLoggedInUser(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        JSONObject jsonObject = jsonArray.getJSONObject(0);



        editor.putString(SCHOOL_NUMBER,jsonObject.getString(SCHOOL_NUMBER));
        editor.putString(NUMBER_USER,jsonObject.getString(NUMBER_USER));
        //editor.putString(DIVISION,jsonObject.getString(DIVISION));
        //editor.putString(ROLL_NO,jsonObject.getString(ROLL_NO));
        editor.putString(FIRST_NAME,jsonObject.getString(FIRST_NAME));
        editor.putString(LAST_NAME,jsonObject.getString(LAST_NAME));
        editor.putString(FATHERS_NAME,jsonObject.getString(FATHERS_NAME));
        editor.putString(MOBILE_NUMBER,jsonObject.getString(MOBILE_NUMBER));
        editor.putString(ADDRESS,jsonObject.getString(ADDRESS));
        editor.putString(EMAIL_ID,jsonObject.getString(EMAIL_ID));
        editor.putString(BLOOD_GROUP,jsonObject.getString(BLOOD_GROUP));
        //editor.putString(CLASS,jsonObject.getString(CLASS));
        editor.putString(SCHOOL_FEES,jsonObject.getString(SCHOOL_FEES));

        editor.putBoolean("LoggedIn",true);
        editor.commit();
    }

    public void storeafterLoging(String json)throws JSONException{
        JSONArray jsonArray = new JSONArray(json);
        JSONObject jsonObject = jsonArray.getJSONObject(0);


        editor.putString(STUDENT_NUMBER,jsonObject.getString(STUDENT_NUMBER));
        editor.putString(DIVISION,jsonObject.getString(DIVISION));
        editor.putString(ROLL_NO,jsonObject.getString(ROLL_NO));
        editor.putString(CLASS,jsonObject.getString(CLASS));
        editor.commit();
    }


    public void storeCloudId(String token){
        editor.putString(CLOUD_ID,token);
        editor.commit();
    }

    public void storePostData(String postData){
        editor.putString(POST_DATA,postData);
        editor.commit();
    }
    public void appendToPostData(String addendString){
        String data = getPostData() + addendString;
        data = data.replace("][",",");
        editor.putString(POST_DATA,"");
        editor.putString(POST_DATA,data);
        editor.commit();
    }

    public void prefixPostData(String prefixData){
        String d = prefixData + getPostData();
        d = d.replace("][",",");
        editor.putString(POST_DATA,"");
        editor.putString(POST_DATA,d);
        editor.commit();
    }

    public String getPostData(){
      return   sharedPreferences.getString(POST_DATA,"");
    }
    public String getUserData(String key){
        return sharedPreferences.getString(key,"");
    }

    public boolean isUserLoggedIn(){
        return sharedPreferences.getBoolean("LoggedIn",false);
    }

    public void clearUserData(){
        editor.clear();
        editor.commit();
    }
    public void storeQaData(String data){
        editor.putString(QA_DATA,data);
        editor.commit();
    }
    public String getQaData(){
        return sharedPreferences.getString(QA_DATA,"");
    }
    public void appendQaData(String appendData){
        String d = getQaData() + appendData;
        d = d.replace("][",",");
        editor.putString(QA_DATA,"");
        editor.putString(QA_DATA,d);
        editor.commit();
    }
    public void prefixQaData(String prefixData){
        String d = prefixData + getQaData();
        d = d.replace("][",",");
        editor.putString(QA_DATA,"");
        editor.putString(QA_DATA,d);
        editor.commit();
    }

}