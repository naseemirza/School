package com.lead.infosystems.schooldiary.CloudMessaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Main.Chat;
import com.lead.infosystems.schooldiary.Main.MainActivity;
import com.lead.infosystems.schooldiary.R;

import java.util.Map;

/**
 * Created by Faheem on 14-10-2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final int NOTIFICATION_ID = 1;

    public static String INTENT_FILTER_CHAT = "chat_intent_filter";
    public static String CHAT_ID = "chat_id";
    public static String SENDER_FULL_NAME = "user_name";
    public static String SENDER_USER_ID = "from_user_id";
    public static String MESSAGE = "message";
    public static String TIME = "time";


    private MyDataBase myDataBase;
    private UserDataSP userDataSP;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        myDataBase = new MyDataBase(getApplicationContext());
        userDataSP = new UserDataSP(getApplicationContext());
        if(remoteMessage.getData().get(CHAT_ID) != null){
            chatMessage(remoteMessage.getData());
        }
    }

    private void chatMessage(Map<String, String> data){
        myDataBase.newChat(data.get(CHAT_ID)
                ,data.get(SENDER_FULL_NAME)
                ,data.get(SENDER_USER_ID)
                ,userDataSP.getUserData(UserDataSP.FIRST_NAME)+" "+userDataSP.getUserData(UserDataSP.FIRST_NAME)
                ,userDataSP.getUserData(UserDataSP.NUMBER_USER)
                ,data.get(TIME)
                ,data.get(MESSAGE));

        myDataBase.chatMessage(data.get(CHAT_ID),data.get(SENDER_USER_ID),data.get(MESSAGE),data.get(TIME));

        Intent intent = new Intent(INTENT_FILTER_CHAT);
        intent.putExtra(CHAT_ID,data.get(CHAT_ID));
        sendBroadcast(intent);
        sendNotification(data.get(SENDER_FULL_NAME),data.get(MESSAGE),data.get(CHAT_ID));
    }


    private void sendNotification(String name, String msg, String chatID) {

        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, Chat.class);
        intent.putExtra(Chat.FIRST_NAME,name);
        intent.putExtra(Chat.CHAT_ID,chatID);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(name)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
