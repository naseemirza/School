package com.lead.infosystems.schooldiary.Data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Faheem on 29-09-2016.
 */

public class Post_Data {
    private String first_name,last_name,id,text_message,src_link,commentsNum;
    private long timeInmilisec;
    boolean isLiked;
    ArrayList<String> likedStudents = new ArrayList<>();

    public Post_Data(String first_name, String last_name, String id, String text_message, String src_link,
                     long timeInmilisec, boolean isLiked, ArrayList<String> likedStudents,String commentNum) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.id = id;
        this.text_message = text_message;
        this.src_link = src_link;
        this.timeInmilisec = timeInmilisec;
        this.isLiked = isLiked;
        this.likedStudents = likedStudents;
        this.commentsNum = commentNum;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getId() {
        return id;
    }

    public String getText_message() {
        return text_message;
    }

    public String getSrc_link() {
        return src_link;
    }

    public long getTimeInmilisec() {
        return timeInmilisec;
    }

    public boolean isLiked() {
        if(isLiked){
            Log.e("isliked", "true");
        }else{
            Log.e("isliked", "false");
        }
        return isLiked;
    }

    public ArrayList<String> getLikedStudents() {
        return likedStudents;
    }

    public void setLiked(boolean liked,String studentNum) {
        if(liked){
            likedStudents.add(studentNum);
        }else {
            likedStudents.remove(studentNum);
        }
        isLiked = liked;
    }
    public String numLikes(){
        Log.e("Like_no",likedStudents.toString());
        return likedStudents.size()+"";
    }

    public String getCommentsNum() {
        return commentsNum;
    }
}
