package com.lead.infosystems.schooldiary.Main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.R;

/**
 * Created by Faheem on 23-01-2017.
 */

public class Badges extends RelativeLayout {

    private UserDataSP user;
    Context con;
    public Badges(Context context) {
        super(context);
        this.con = context;
        user = new UserDataSP(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.tab_icon,this,true);
    }

    public View getBadgeIcon(int icon){
        ((ImageView)findViewById(R.id.badge_icon)).setImageResource(icon);
        int number = getNumberFromSP(icon);
        if(number > 0){
             String num = String.valueOf(number);
             TextDrawable drawable = TextDrawable.builder().buildRound(num,Color.RED);
            ((ImageView)findViewById(R.id.badge_num)).setImageDrawable(drawable);
        }
        return getRootView();
    }


    public int getNumberFromSP(int icon) {
        return user.getNotificationNumber((icon == R.drawable.ic_chat)? user.CHAT_NOTIFICATION_NUM:user.NOTIFICATION_NUM);
    }
}
