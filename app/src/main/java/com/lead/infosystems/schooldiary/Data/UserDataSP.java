package com.lead.infosystems.schooldiary.Data;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserDataSP {

    public static final String LOGGEDIN = "logged_in";
    public static final String NUMBER_USER = "number_user";
    public static final String STUDENT_NUMBER = "student_number";
    public static final String PROPIC_URL = "profilePic_link";
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
    public static final String CLOUD_ID = "reg_id";
    public static final String POST_DATA = "POST_DATA";
    public static final String QA_DATA = "qa_data";
    public static final String SCHOOL_FEES = "school_fees";
    public static final String SCHOOL_WEBSITE_LINK = "school_websitelink";
    public static final String IDENTIFICATION = "identification";
    public static final String SUBJECTS = "subjects";
    public static final String PRINCIPAL_FIRST_NAME = "principal_first_name";
    public static final String PRINCIPAL_LAST_NAME = "principal_last_name";
    public static final String PRINCIPAL_MOBILE = "principal_mobile";
    public static final String PRINCIPAL_GMAIL = "principal_gmail_id";
    public static final String PRINCIPAL_PIC = "principal_profilePic_link";
    public static final String PRINCIPAL_DESIGNATION = "principal_designation";
    public static final String PRINCIPAL_QUALIFICATION = "principal_qualifiaction";
    public static final String PRINCIPAL_INTERESTS_FIELD = "principal_interests_field";
    public static final String PRINCIPAL_CONTACT_DETAIL= "principal_contact_detail";

    public static final String DIRECTOR_FIRST_NAME = "director_first_name";
    public static final String DIRECTOR_LAST_NAME = "director_last_name";
    public static final String DIRECTOR_MOBILE = "director_mobile";
    public static final String DIRECTOR_GMAIL = "director_gmail_id";
    public static final String DIRECTOR_PIC = "director_profilePic_link";
    public static final String DIRECTOR_DESIGNATION = "director_designation";
    public static final String DIRECTOR_QUALIFICATION = "director_qualifiaction";
    public static final String DIRECTOR_INTERESTS_FIELD = "director_interests_field";
    public static final String DIRECTOR_CONTACT_DETAIL= "director_contact_detail";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public UserDataSP(Context context) {
        sharedPreferences = context.getSharedPreferences("USER_SP",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }


    public void storeLoggedInUser(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray(json);
        JSONObject jsonObject = jsonArray.getJSONObject(0);


        editor.putString(IDENTIFICATION,jsonObject.getString(IDENTIFICATION));
        editor.putString(SCHOOL_NUMBER,jsonObject.getString(SCHOOL_NUMBER));
        editor.putString(NUMBER_USER,jsonObject.getString(NUMBER_USER));
        editor.putString(PROPIC_URL,jsonObject.getString(PROPIC_URL));
        editor.putString(IDENTIFICATION,jsonObject.getString(IDENTIFICATION));
        editor.putString(FIRST_NAME,jsonObject.getString(FIRST_NAME));
        editor.putString(LAST_NAME,jsonObject.getString(LAST_NAME));
        editor.putString(FATHERS_NAME,jsonObject.getString(FATHERS_NAME));
        editor.putString(MOBILE_NUMBER,jsonObject.getString(MOBILE_NUMBER));
        editor.putString(ADDRESS,jsonObject.getString(ADDRESS));
        editor.putString(EMAIL_ID,jsonObject.getString(EMAIL_ID));
        editor.putString(BLOOD_GROUP,jsonObject.getString(BLOOD_GROUP));
        editor.putString(SCHOOL_FEES,jsonObject.getString(SCHOOL_FEES));
        editor.putString(SCHOOL_WEBSITE_LINK,jsonObject.getString(SCHOOL_WEBSITE_LINK));

        editor.putBoolean(LOGGEDIN,true);
        editor.commit();
    }

    public boolean isStudent(){
       return sharedPreferences.getString(IDENTIFICATION,"").contentEquals("student")? true:false;
    }

    public void storeStudentData(String json)throws JSONException{
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
        return sharedPreferences.getBoolean(LOGGEDIN,false);
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

    public  void storeData(String data){
        String[] res = data.split("@@@");
        editor.putString(SUBJECTS,res[0]);
        editor.commit();
    }


    public void storePrincipalData(String firstName, String lastName, String mobile, String gmail, String pic, String designation, String qualifiaction, String interests_field, String contact_detail)
    {
        editor.putString(PRINCIPAL_FIRST_NAME, firstName);
        editor.putString(PRINCIPAL_LAST_NAME, lastName);
        editor.putString(PRINCIPAL_MOBILE, mobile);
        editor.putString(PRINCIPAL_GMAIL, gmail);
        editor.putString(PRINCIPAL_PIC, pic);
        editor.putString(PRINCIPAL_DESIGNATION, designation);
        editor.putString(PRINCIPAL_QUALIFICATION, qualifiaction);
        editor.putString(PRINCIPAL_INTERESTS_FIELD, interests_field);
        editor.putString(PRINCIPAL_CONTACT_DETAIL, contact_detail);
        editor.commit();
    }
    public String getPrincipalData(String key)
    {
        return sharedPreferences.getString(key, "");
    }

    public void storeDirectorData(String firstName, String lastName, String mobile, String gmail, String pic, String designation, String qualifiaction, String interests_field, String contact_detail)
    {
        editor.putString(DIRECTOR_FIRST_NAME, firstName);
        editor.putString(DIRECTOR_LAST_NAME, lastName);
        editor.putString(DIRECTOR_MOBILE, mobile);
        editor.putString(DIRECTOR_GMAIL, gmail);
        editor.putString(DIRECTOR_PIC, pic);
        editor.putString(DIRECTOR_DESIGNATION, designation);
        editor.putString(DIRECTOR_QUALIFICATION, qualifiaction);
        editor.putString(DIRECTOR_INTERESTS_FIELD, interests_field);
        editor.putString(DIRECTOR_CONTACT_DETAIL, contact_detail);
        editor.commit();
    }
    public String getDirectorData(String key)
    {
        return sharedPreferences.getString(key, "");
    }

}