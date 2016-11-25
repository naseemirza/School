package com.lead.infosystems.schooldiary.Attendance;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Naseem on 04-11-2016.
 */


public class SPData {
    SharedPreferences.Editor editor;
    SharedPreferences sp;
    public static final String STU = "stulist";


    SPData(Context context){
        sp = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public  void storeData(String data){
        String[] res = data.split("@@@");
        editor.putString(STU,res[0]);


        editor.commit();
    }
    public String getData(String key){
        return sp.getString(key,"");
    }
}
