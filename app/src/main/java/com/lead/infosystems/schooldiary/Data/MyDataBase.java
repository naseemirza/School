package com.lead.infosystems.schooldiary.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Faheem on 28-11-2016.
 */

public class MyDataBase extends SQLiteOpenHelper {

    private static final String DB_NAME = "schoolDiary.db";
    private static final String ID = "id";

    //chat contacts
    private static final String CHAT_CONTACT_TABLE = "chat_contact_table";

    private static final String CONTACT_USERID = "chat_userid";
    private static final String CONTACT_FIRST_NAME = "contact_first_name";
    private static final String CONTACT_LAST_NAME = "contact_last_name";

    String CREATE_CHAT_CONTACT = "create table "+CHAT_CONTACT_TABLE+" ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CONTACT_USERID+" TEXT, "+CONTACT_FIRST_NAME+" TEXT, "+CONTACT_LAST_NAME+" TEXT)";

    //active chats
    private static final String ACTIVE_CHAT_LIST_TABLE = "active_chat_table";
    private static final String CHAT_ID = "chat_id";
    private static final String USER1_NAME = "user1_name";
    private static final String USER1_ID = "user1_id";
    private static final String USER2_NAME = "user2_name";
    private static final String USER2_ID = "user2_id";
    private static final String LAST_MESSAGE = "message";
    private static final String DATE = "date";
    String CREATE_ACTIVE_CHATS = "create table "+ACTIVE_CHAT_LIST_TABLE+" ( "+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CHAT_ID+" TEXT, "+USER1_NAME+" TEXT, "+USER1_ID+" TEXT, "+USER2_NAME+" TEXT, "+USER2_ID+" TEXT, "+LAST_MESSAGE+" TEXT, "+DATE+" TEXT)";

    //chat messages
    private static final String CHAT_MESSAFGE_TABLE = "chat_message_table";
    private static final String USER_ID = "user_id";
    private static final String MESSAGE = "message";
    String CREATE_CHAT_MESSAGE = "create table "+CHAT_MESSAFGE_TABLE+" ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CHAT_ID+" TEXT, "+USER_ID+" TEXT, "+MESSAGE+" TEXT, "+DATE+" TEXT)";




    SQLiteDatabase db;

    public MyDataBase(Context context) {
        super(context, DB_NAME, null, 1);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CHAT_CONTACT);
        db.execSQL(CREATE_ACTIVE_CHATS);
        db.execSQL(CREATE_CHAT_MESSAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+CHAT_CONTACT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ACTIVE_CHAT_LIST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+CHAT_MESSAFGE_TABLE);
        onCreate(db);
    }

/////////// working with chat contacts
    public void insertIntoCOntact(String userId,String firstName,String lastName){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACT_USERID,userId);
        contentValues.put(CONTACT_FIRST_NAME,firstName);
        contentValues.put(CONTACT_LAST_NAME,lastName);
        db.insert(CHAT_CONTACT_TABLE,null,contentValues);
    }
    public void clearContacts(){
        db.execSQL("DROP TABLE IF EXISTS " +CHAT_CONTACT_TABLE);
        db.execSQL(CREATE_CHAT_CONTACT);
    }
    public Cursor getContacts(){
        return db.rawQuery("select * from "+CHAT_CONTACT_TABLE,null);
    }

    ///////working with active chats
    public void newChat(String chatId,String user1Name,String user1ID,String user2Name
            ,String user2ID,String date,String lastMessage){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHAT_ID,chatId);
        contentValues.put(USER1_NAME,user1Name);
        contentValues.put(USER1_ID,user1ID);
        contentValues.put(USER2_NAME,user2Name);
        contentValues.put(USER2_ID,user2ID);
        contentValues.put(LAST_MESSAGE,lastMessage);
        contentValues.put(DATE,date);
        int a = db.update(ACTIVE_CHAT_LIST_TABLE,contentValues,CHAT_ID+" = "+chatId,null);
        if(a==0){
           db.insert(ACTIVE_CHAT_LIST_TABLE,null,contentValues);
        }
    }
    public Cursor getActiveChats(){

        return db.rawQuery("select * from "+ACTIVE_CHAT_LIST_TABLE,null);
    }

    //working with messages
    public void chatMessage(String chatId,String from,String message,String time){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHAT_ID,chatId);
        contentValues.put(USER_ID,from);
        contentValues.put(MESSAGE,message);
        contentValues.put(DATE,time);
        db.insert(CHAT_MESSAFGE_TABLE,null,contentValues);
    }

    public Cursor getChatMessages(String chatId){
        return db.rawQuery("select * from "+CHAT_MESSAFGE_TABLE+" where "+CHAT_ID+" = "+chatId,null);
    }
    public String getChatID(String myId, String userID) {
        Cursor data = db.rawQuery("select "+CHAT_ID+" from "+ACTIVE_CHAT_LIST_TABLE+" where ( "+ USER1_ID+" = "+myId+
                " and "+USER2_ID+ " = "+userID+" ) or ( "+ USER1_ID+" = "+userID+ " and "+USER2_ID+ " = "+myId+" )",null);
        if(data.getCount()>0){
            data.moveToNext();
            return data.getString(0);
        }else{
            return null;
        }
    }
}
