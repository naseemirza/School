package com.lead.infosystems.schooldiary.Main;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import com.lead.infosystems.schooldiary.Data.Post_Data;

/**
 * Created by Faheem on 20-11-2016.
 */

public class PostAnimData {
    private View propic, name, time, text, post_img, card;
    private Bitmap postImageBitmap;
    private boolean isImage;
    private String postID;
    private Post_Data item;
    private TextView comment_num;
    private int position;

    public PostAnimData(View propic, View name, View time, View text, View post_img, View card
            , TextView comment_num, Bitmap postImageBitmap, boolean isImage, String postID, Post_Data item, int position) {
        this.propic = propic;
        this.name = name;
        this.time = time;
        this.text = text;
        this.post_img = post_img;
        this.card = card;
        this.isImage = isImage;
        this.postID = postID;
        this.item = item;
        this.comment_num = comment_num;
        this.postImageBitmap = postImageBitmap;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public Bitmap getPostImageBitmap() {
        return postImageBitmap;
    }

    public View getPropic() {
        return propic;
    }

    public View getName() {
        return name;
    }

    public View getTime() {
        return time;
    }

    public View getText() {
        return text;
    }

    public View getPost_img() {
        return this.post_img;
    }

    public View getCard() {
        return card;
    }

    public boolean isImageAvailable() {
        return isImage;
    }

    public String getPostID() {
        return postID;
    }

    public Post_Data getItem() {
        return item;
    }

    public TextView getComment_num() {
        return comment_num;
    }
}
