package com.lead.infosystems.schooldiary.Generic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.lead.infosystems.schooldiary.ICompressedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by Faheem on 26-12-2016.
 */

public class CompressImage extends AsyncTask<Void,Void,Boolean>{
    private Activity activity;
    private ProgressDialog progressDialog;
    private Bitmap img;
    private final int MAX_IMAGE_SIZE = 150;
    private Bitmap encoded_image;
    private File imagefile = null;
    private ICompressedImage iCompressedImage;

   public CompressImage(Activity activity,ICompressedImage iCompressedImage){
        this.activity = activity;
        progressDialog = new ProgressDialog(activity);
        this.iCompressedImage = iCompressedImage;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Compressing Image please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if(img != null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            img.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            int size = byteArrayOutputStream.toByteArray().length/1024;
            if(size>MAX_IMAGE_SIZE) {
                Bitmap b = img;
                img = null;
                int wr = 16;
                int hr = 9;
                int res = 55;
                int quality = 100;
                img = resize(b, wr*res, hr*res);
                do {
                    b = img;
                    img = null;
                    byteArrayOutputStream = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
                    img = BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(),0,
                            byteArrayOutputStream.toByteArray().length);
                    size = byteArrayOutputStream.toByteArray().length / 1024;
                    quality = quality - 10;
                    if(quality < 70){
                        res = res -10;
                        img = resize(img,wr *res, hr*res );
                        quality = 100;}
                }while (size > MAX_IMAGE_SIZE);
            }
            try {
                imagefile = new File(Environment.getExternalStorageDirectory(),Utils.TEMP_IMG);
                FileOutputStream fileOutputStream = new FileOutputStream(imagefile.getPath());
                fileOutputStream.write(byteArrayOutputStream.toByteArray());
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }else{
            return false;
        }
    }
    @Override
    protected void onPostExecute(Boolean send) {
        iCompressedImage.compressedImageFile(imagefile);
        progressDialog.dismiss();

    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
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
}
