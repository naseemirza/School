package com.lead.infosystems.schooldiary.Data;

/**
 * Created by Faheem on 16-11-2016.
 */

public class PostCommentData {
    private String name, time, text, comment_id;
    int likes;
    private boolean user_liked;

    public PostCommentData(String name, String time, String text, String likes, String comment_id, String user_liked) {
        this.name = name;
        this.time = time;
        this.text = text;
        this.likes = Integer.parseInt((likes.contains("null"))? "0":likes);
        this.comment_id = comment_id;
        if(user_liked.contains("true")){
            this.user_liked = true;
        }else{
            this.user_liked = false;
        }
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setUser_liked(boolean user_liked) {
        this.user_liked = user_liked;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public int getLikes() {
        return likes;
    }

    public String getComment_id() {
        return comment_id;
    }

    public boolean isUser_liked() {
        return user_liked;
    }
}