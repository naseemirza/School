package com.lead.infosystems.schooldiary.ShareButton;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.lead.infosystems.schooldiary.Main.FragTabHome;
import com.lead.infosystems.schooldiary.R;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Album;
import com.sromku.simple.fb.entities.Feed;
import com.sromku.simple.fb.entities.Photo;
import com.sromku.simple.fb.listeners.OnAlbumsListener;
import com.sromku.simple.fb.listeners.OnPublishListener;

import java.util.List;

/**
 * Created by Naseem on 06-01-2017.
 */

public class PhotoPublish {

    Context c;
    SimpleFacebook fb;
    Bitmap bm;
    String text,img;
    static final String ALBUM_ID="567810640042973";

    public PhotoPublish(Context c, SimpleFacebook fb, Bitmap bm,String img, String text) {
        this.c = c;
        this.fb = fb;
        this.text = text;
        this.bm = bm;
        this.img = img;
    }

    public void publishPhoto(){


        final Feed feed = new Feed.Builder()
                .setMessage("Clone it out...")
                .setName("Simple Facebook for Android")
                .setCaption("Code less, do the same.")
                .setDescription(
                        "The Simple Facebook library project makes the life much easier by coding less code for being able to login, publish feeds and open graph stories, invite friends and more.")
                .setPicture(
                        "https://raw.github.com/sromku/android-simple-facebook/master/Refs/android_facebook_sdk_logo.png")
                .setLink(
                        "https://github.com/sromku/android-simple-facebook")
                .build();
            fb.publish(feed,onPublishListener);

    }
    public static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }
    OnPublishListener onPublishListener=new OnPublishListener() {
        @Override
        public void onComplete(String response) {
            super.onComplete(response);
            Toast.makeText(c,"Publish Successfully..",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onException(Throwable throwable) {
            super.onException(throwable);
            Log.e("Exception",throwable.getMessage());
        }

        @Override
        public void onFail(String reason) {
            super.onFail(reason);
            Log.i("Fail",reason);
        }
    };
}
