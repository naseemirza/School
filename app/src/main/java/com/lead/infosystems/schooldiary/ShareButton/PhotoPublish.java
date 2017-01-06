package com.lead.infosystems.schooldiary.ShareButton;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.lead.infosystems.schooldiary.R;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.entities.Photo;
import com.sromku.simple.fb.listeners.OnPublishListener;

/**
 * Created by Naseem on 06-01-2017.
 */

public class PhotoPublish {

    Context c;
    SimpleFacebook fb;
    Bitmap bm;
    String text;
    static final String ALBUM_ID="567810640042973";

    public PhotoPublish(Context c, SimpleFacebook fb, Bitmap bm, String text) {
        this.c = c;
        this.fb = fb;
        this.text = text;
        this.bm = bm;
    }

    public void publishPhoto(){
           Photo photo;
        if(bm != null){
            photo=new Photo.Builder()
                    .setImage(resize(bm,720,720))
                    .setName(text)
                    .build();
            fb.publish(photo,ALBUM_ID,onPublishListener);
        }

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
