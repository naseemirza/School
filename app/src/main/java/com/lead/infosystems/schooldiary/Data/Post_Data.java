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
    private String first_name,last_name,id,text_message,src_link,timeString;
    boolean isLiked;
    private int commentsNum,studentNum;
    ArrayList<String> likedStudents = new ArrayList<>();

    public Post_Data(String studentNum, String first_name, String last_name, String id, String text_message, String src_link,
                     String timeString, boolean isLiked, ArrayList<String> likedStudents,String commentNum) {
        this.studentNum = Integer.parseInt(studentNum);
        this.first_name = first_name;
        this.last_name = last_name;
        this.id = id;
        this.text_message = text_message;
        this.src_link = src_link;
        this.timeString = timeString;
        this.isLiked = isLiked;
        this.likedStudents = likedStudents;
        this.commentsNum = Integer.parseInt(commentNum);
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

    public String gettimeString() {
        return timeString;
    }

    public boolean isLiked() {
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
        return likedStudents.size()+"";
    }

    public int getCommentsNum() {
        return commentsNum;
    }
    public void setCommentsNum(int num){
        this.commentsNum = num;
    }

    public int getStudentNum() {
        return studentNum;
    }
}
