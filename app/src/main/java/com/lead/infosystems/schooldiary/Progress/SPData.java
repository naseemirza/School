package com.lead.infosystems.schooldiary.Progress;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Naseem on 12-11-2016.
 */

public class SPData {

    SharedPreferences.Editor editor;
    SharedPreferences sp;
    public static final String SUB = "subj";
    // public static final String MAK = "marks";


    SPData(Context context){
        sp = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public  void storeData(String data){
        String[] res = data.split("@@@");
        editor.putString(SUB,res[0]);
        // editor.putString(MAK,res[0]);

        editor.commit();
    }
    public String getData(String key){
        return sp.getString(key,"");
    }
}
