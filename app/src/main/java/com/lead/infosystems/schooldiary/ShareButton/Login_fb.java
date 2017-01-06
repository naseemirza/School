package com.lead.infosystems.schooldiary.ShareButton;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLoginListener;

import java.util.List;

/**
 * Created by Naseem on 06-01-2017.
 */

public class Login_fb {


    Context c;
    SimpleFacebook fb;
    Bitmap bm;
    String text;

    public Login_fb(Context c, SimpleFacebook fb, Bitmap bm ,String text) {
        this.c = c;
        this.fb = fb;
        this.bm = bm;
        this.text = text;
    }

    public void login(){
        fb.login(onLoginListener);
    }

    OnLoginListener onLoginListener=new OnLoginListener() {
        @Override
        public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
            Toast.makeText(c,"Login Successfull..",Toast.LENGTH_SHORT).show();
            new PhotoPublish(c,fb,bm,text).publishPhoto();
        }

        @Override
        public void onCancel() {
            Toast.makeText(c,"Cancelled..",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onException(Throwable throwable) {
            Log.e("Exception",throwable.getMessage());
        }

        @Override
        public void onFail(String reason) {
            Log.i("Fail",reason);
        }
    };
}
