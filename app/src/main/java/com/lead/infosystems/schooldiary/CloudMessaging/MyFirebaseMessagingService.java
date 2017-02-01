package com.lead.infosystems.schooldiary.CloudMessaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lead.infosystems.schooldiary.Data.MyDataBase;
import com.lead.infosystems.schooldiary.Data.NotificationData;
import com.lead.infosystems.schooldiary.Data.UserDataSP;
import com.lead.infosystems.schooldiary.Main.Chat;
import com.lead.infosystems.schooldiary.Main.MainActivity;
import com.lead.infosystems.schooldiary.Main.MainTabAdapter;
import com.lead.infosystems.schooldiary.R;
import com.sromku.simple.fb.entities.User;

import java.util.Map;

/**
 * Created by Faheem on 14-10-2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private int NOTIFICATION_ID = 2;

    public static final String INTENT_FILTER_CHAT = "chat_intent_filter";
    public static final String CHAT_ID = "chat_id";
    public static final String SENDER_FULL_NAME = "user_name";
    public static final String SENDER_USER_ID = "from_user_id";
    public static final String MESSAGE = "message";
    public static final String TIME = "time";


    public int CHAT_NOTIFICATION_ID = 1;

    public static final String NOTIFICATION_TEXT = "notification_text";
    public static final String NOTIFICATION_TYPE = "type";

    private MyDataBase myDataBase;
    private UserDataSP userDataSP;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        myDataBase = new MyDataBase(getApplicationContext());
        userDataSP = new UserDataSP(getApplicationContext());
        Log.e("noti",remoteMessage.getData().toString());
        Map<String ,String> data = remoteMessage.getData();

        if(data.get(CHAT_ID) != null){
            chatMessage(data);
        }else if(data.get(NOTIFICATION_TYPE) != null){
            forNotificationTab(data);
        }
    }

    private void forNotificationTab(Map<String, String> data){
        userDataSP.setNotificationNumber(userDataSP.getNotificationNumber(UserDataSP.NOTIFICATION_NUM)+1,UserDataSP.NOTIFICATION_NUM);
        sendNotificationRecevedBrodcast();
        Intent intent = new Intent(this, MainActivity.class);
        String notificationTitle = "";
        if(data.get(NOTIFICATION_TYPE).contentEquals(NotificationData.EVENT)){
            intent.setAction(NotificationData.EVENT);
            notificationTitle = "New Event";
            NOTIFICATION_ID = 2;
        }else if(data.get(NOTIFICATION_TYPE).contentEquals(NotificationData.APPLICATION_FORM)){
            intent.setAction(NotificationData.APPLICATION_FORM);
            notificationTitle = "New Application Form";
            NOTIFICATION_ID = 3;
        }else if(data.get(NOTIFICATION_TYPE).contentEquals(NotificationData.HOME_WORK)){
            intent.setAction(NotificationData.HOME_WORK);
            notificationTitle = "New Home Work";
            NOTIFICATION_ID = 4;
        }else if(data.get(NOTIFICATION_TYPE).contentEquals(NotificationData.MARKS)){
            intent.setAction(NotificationData.MARKS);
            notificationTitle = "New Marks Uploaded ";
            NOTIFICATION_ID = 5;
        }else if(data.get(NOTIFICATION_TYPE).contentEquals(NotificationData.MODEL_QP)){
            intent.setAction(NotificationData.MODEL_QP);
            notificationTitle = "New Model Question Paper Uploaded";
            NOTIFICATION_ID = 6;
        }else if(data.get(NOTIFICATION_TYPE).contentEquals(NotificationData.TEST_EXAM)){
            intent.setAction(NotificationData.TEST_EXAM);
            notificationTitle = "Marks Uploaded";
            NOTIFICATION_ID = 7;
        }

        sendNotification(notificationTitle,data.get(NOTIFICATION_TEXT).replace("+"," "),intent,NOTIFICATION_ID);
    }


    private void chatMessage(Map<String, String> data){
        userDataSP.setNotificationNumber(userDataSP.getNotificationNumber(UserDataSP.CHAT_NOTIFICATION_NUM)+1,UserDataSP.CHAT_NOTIFICATION_NUM);
        sendNotificationRecevedBrodcast();
        myDataBase.newChat(data.get(CHAT_ID)
                ,data.get(SENDER_FULL_NAME)
                ,data.get(SENDER_USER_ID)
                ,userDataSP.getUserData(UserDataSP.FIRST_NAME)+" "+userDataSP.getUserData(UserDataSP.FIRST_NAME)
                ,userDataSP.getUserData(UserDataSP.NUMBER_USER)
                ,data.get(TIME)
                ,data.get(MESSAGE), userDataSP.getUserData(UserDataSP.PROPIC_URL));

        myDataBase.chatMessage(data.get(CHAT_ID),data.get(SENDER_USER_ID),data.get(MESSAGE),data.get(TIME));

        Intent intent = new Intent(INTENT_FILTER_CHAT);
        intent.putExtra(CHAT_ID,data.get(CHAT_ID));
        sendBroadcast(intent);

        intent = new Intent(this, Chat.class);
        intent.putExtra(Chat.FIRST_NAME,data.get(SENDER_FULL_NAME));
        intent.putExtra(Chat.CHAT_ID,data.get(CHAT_ID));
        intent.putExtra(Chat.PROPIC_LINK,data.get(Chat.PROPIC_LINK));
        intent.putExtra(Chat.USER_ID,data.get(SENDER_USER_ID));
        if(!Chat.ACTIVITY_ACTIVE){
            sendNotification(data.get(SENDER_FULL_NAME),data.get(MESSAGE),intent,CHAT_NOTIFICATION_ID);
        }
    }

    private void sendNotificationRecevedBrodcast(){
        sendBroadcast(new Intent(MainTabAdapter.NOTIFICATION_BC_FILTER));
    }

    private void sendNotification(String title, String msg, Intent intent, int notification_id) {

        NotificationManager mNotificationManager;
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(notification_id, mBuilder.build());
    }
}
