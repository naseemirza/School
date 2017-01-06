package com.lead.infosystems.schooldiary.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
    private static final String PROPIC_LINK = "profilePic_link";

    String CREATE_CHAT_CONTACT = "create table "+CHAT_CONTACT_TABLE+" ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CONTACT_USERID+" TEXT, "+CONTACT_FIRST_NAME+" TEXT, "+CONTACT_LAST_NAME+" TEXT, "+PROPIC_LINK+" TEXT)";

    // Home work data
    private static final String HOMEWORK_TABLE = "homework_table";
    private static final String HOMEWORK_TITLE = "homework_title";
    private static final String HOMEWORK_CONTENTS = "homework_contents";
    private static final String LASTDATE_SUBMISSION = "lastDate_submission";
    private static final String HOMEWORK_SUBJECT= "subject";
    private static final String HOMEWORK_DATE = "homeworkDate";
    private static final String HOMEWORK_NUMMBERUSER = "number_user";
    private static final String HOMEWORK_NUMBER = "homework_number";

    String CREATE_HOMEWORK_TABLE = "create table "+ HOMEWORK_TABLE + "("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+HOMEWORK_TITLE+" TEXT, "+HOMEWORK_CONTENTS+" TEXT, "+LASTDATE_SUBMISSION+" TEXT, "+HOMEWORK_SUBJECT+" TEXT, "+HOMEWORK_DATE+" TEXT, "+HOMEWORK_NUMMBERUSER+" TEXT, "+HOMEWORK_NUMBER+" TEXT  )";

    // Attendance table data
    private static final String ATTENDANCE_TABLE = "attendance_table";
    private static final String ATTENDANCE_YEAR = "year";
    private static final String ATTENDANCE_DAY = "day";
    private static final String ATTENDANCE_MONTH = "month";
    private static final String ATTENDANCE_VALUE = "attendance";

    String CREATE_ATTENDANCE_TABLE = "create table "+ ATTENDANCE_TABLE + "("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+ATTENDANCE_YEAR+" TEXT, "+ATTENDANCE_DAY+" TEXT, "+ATTENDANCE_MONTH+" TEXT, "+ATTENDANCE_VALUE+" TEXT )";


    // event table
    private static final String EVENT_TABLE = "events_table";
    private static final String EVENT_NAME = "event_name";
    private static final String EVENT_DETAIL = "event_details";
    private static final String EVENT_DATE = "event_date";
    private static final String EVENTSUBMIT_DATE = "submit_date";
    private static final String EVENTSCHOOL_NUMBER = "school_number";

    String CREATE_EVENT_TABLE = "create table "+ EVENT_TABLE + "("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+EVENT_NAME+" TEXT, "+EVENT_DETAIL+" TEXT, "+EVENT_DATE+" TEXT, "+EVENTSUBMIT_DATE+" TEXT, "+EVENTSCHOOL_NUMBER+" TEXT )";


    // progress report table
    // subject table
    private static final String SUBJECT_TABLE = "subject_table";
    private static final String SUBJECT_NAME = "sub_name";

    String CREATE_SUBJECT_TABLE = "create table "+ SUBJECT_TABLE + "("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+SUBJECT_NAME+" TEXT )";

    // marks table
    private static final String MARKS_TABLE = "marks_table";
    private static final String EXAM_DATE_MARKS = "date";
    private static final String EXAM_NAME_MARKS = "exam_name";
    private static final String TOTAL_MARKS = "total";
    private static final String OBTAIN_MARKS = "marks";
    private static final String MARKS_PERCENTAGE = "percentage";
    String CREATE_MARKS_TABLE = "create table "+ MARKS_TABLE + "("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+EXAM_DATE_MARKS+" TEXT, "+EXAM_NAME_MARKS+" TEXT, "+TOTAL_MARKS+" TEXT, "+OBTAIN_MARKS+" TEXT, "+MARKS_PERCENTAGE+" TEXT)";



    //management table

    private static final String MANAGEMENT_TABLE = "management_table";
    private static final String FIRST_NAME = "event_name";
    private static final String LAST_NAME = "event_details";
    private static final String MOBILE = "event_date";
    private static final String GMAIL = "submit_date";
    private static final String PIC = "school_number";
    private static final String DESIGNATION = "designation";
    private static final String QUALIFICATION = "qualifications";
    private static final String INTERESTS_FIELD = "interests_field";
    private static final String CONTACT_DETAIL= "contact_detail";

    String CREATE_MANAGEMENT_TABLE = "create table "+ MANAGEMENT_TABLE + "("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+FIRST_NAME+" TEXT, "+LAST_NAME+" TEXT, "+MOBILE+" TEXT, "+GMAIL+" TEXT, "+PIC+" TEXT, "+DESIGNATION+" TEXT, "+QUALIFICATION+" TEXT, "+INTERESTS_FIELD+" TEXT, "+CONTACT_DETAIL+" )";



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
            CHAT_ID+" TEXT, "+USER1_NAME+" TEXT, "+USER1_ID+" TEXT, "+USER2_NAME+" TEXT, "+USER2_ID+" TEXT, "
            +LAST_MESSAGE+" TEXT, "+DATE+" TEXT, "+PROPIC_LINK+" TEXT)";

    //chat messages
    private static final String CHAT_MESSAFGE_TABLE = "chat_message_table";
    private static final String USER_ID = "user_id";
    private static final String MESSAGE = "message";
    String CREATE_CHAT_MESSAGE = "create table "+CHAT_MESSAFGE_TABLE+" ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CHAT_ID+" TEXT, "+USER_ID+" TEXT, "+MESSAGE+" TEXT, "+DATE+" TEXT)";

    // notification table
    private static final String NOTIFICATION_TABLE = "notification_table";
    private static final String NOTIFICATION_NUMBER = "notification_number";
    private static final String CLASS = "class";
    private static final String DIV = "div";
    private static final String NOTIFICATION_TEXT = "text";
    private static final String NOTIFICATION_TYPE = "type";
    String CREATE_NOTIFICATION_TABLE = "create table "+NOTIFICATION_TABLE+" ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NOTIFICATION_NUMBER+" TEXT, "+DATE+" TEXT, "+CLASS+" TEXT, "+DIV+" TEXT, "
            +NOTIFICATION_TEXT+" TEXT, "+NOTIFICATION_TYPE+" TEXT)";

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
        db.execSQL(CREATE_NOTIFICATION_TABLE);
        db.execSQL(CREATE_HOMEWORK_TABLE);
        db.execSQL(CREATE_ATTENDANCE_TABLE);
        db.execSQL(CREATE_EVENT_TABLE);
        db.execSQL(CREATE_SUBJECT_TABLE);
        db.execSQL(CREATE_MARKS_TABLE);
        db.execSQL(CREATE_MANAGEMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+CHAT_CONTACT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ACTIVE_CHAT_LIST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+CHAT_MESSAFGE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+NOTIFICATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+HOMEWORK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+ATTENDANCE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+EVENT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+SUBJECT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+MARKS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+MANAGEMENT_TABLE);
        onCreate(db);
    }
    public void clearDb(){
        onUpgrade(db,0,0);
    }
/////////// working with chat contacts
    public void insertIntoCOntact(String userId,String firstName,String lastName,String profilePic_link){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACT_USERID,userId);
        contentValues.put(CONTACT_FIRST_NAME,firstName);
        contentValues.put(CONTACT_LAST_NAME,lastName);
        contentValues.put(PROPIC_LINK,profilePic_link);
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
    public void newChat(String chatId, String user1Name, String user1ID, String user2Name
            , String user2ID, String date, String lastMessage, String profilePic_link){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHAT_ID,chatId);
        contentValues.put(USER1_NAME,user1Name);
        contentValues.put(USER1_ID,user1ID);
        contentValues.put(USER2_NAME,user2Name);
        contentValues.put(USER2_ID,user2ID);
        contentValues.put(LAST_MESSAGE,lastMessage);
        contentValues.put(DATE,date);
        contentValues.put(PROPIC_LINK,profilePic_link);
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

    //working with notification table
    public void incertNotification(String notificationNum,String date,String mClass,String div,String text,String type){
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIFICATION_NUMBER,notificationNum);
        contentValues.put(DATE,date);
        contentValues.put(CLASS,mClass);
        contentValues.put(DIV,div);
        contentValues.put(NOTIFICATION_TEXT,text);
        contentValues.put(NOTIFICATION_TYPE,type);
        db.insert(NOTIFICATION_TABLE,null,contentValues);
    }
    public void clearNotifications(){
        db.execSQL("DROP TABLE IF EXISTS " +NOTIFICATION_TABLE);
        db.execSQL(CREATE_NOTIFICATION_TABLE);
    }
    public Cursor getNotifications(){
        return db.rawQuery("select * from "+NOTIFICATION_TABLE,null);
    }


    //working with homework table
    public void insertHomeWorkData(String homework_title,String homework_contents,String lastDate_submission, String subject, String homeworkDate, String number_user, String homework_number){
        ContentValues contentValues = new ContentValues();
        contentValues.put(HOMEWORK_TITLE,homework_title);
        contentValues.put(HOMEWORK_CONTENTS,homework_contents);
        contentValues.put(LASTDATE_SUBMISSION,lastDate_submission);
        contentValues.put(HOMEWORK_SUBJECT,subject);
        contentValues.put(HOMEWORK_DATE,homeworkDate);
        contentValues.put(HOMEWORK_NUMMBERUSER, number_user);
        contentValues.put(HOMEWORK_NUMBER,homework_number);
       int a= (int) db.insert(HOMEWORK_TABLE,null,contentValues);
        Log.e("a value ", String.valueOf(a));
    }
    public void clearHomeWorkData(){
        db.execSQL("DROP TABLE IF EXISTS " +HOMEWORK_TABLE);
        db.execSQL(CREATE_HOMEWORK_TABLE);
    }
    public Cursor getHomeWorkData(){
        return db.rawQuery("select * from "+HOMEWORK_TABLE,null);
    }


    //working with attendance table
    public void insertAttendanceData(String year, String day, String month, String attendance){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ATTENDANCE_YEAR,year);
        contentValues.put(ATTENDANCE_DAY,day);
        contentValues.put(ATTENDANCE_MONTH,month);
        contentValues.put(ATTENDANCE_VALUE,attendance);
       int a = (int) db.insert(ATTENDANCE_TABLE,null,contentValues);
        Log.e("a.....", String.valueOf(a));
    }
    public void clearAttendanceData(){
        db.execSQL("DROP TABLE IF EXISTS " +ATTENDANCE_TABLE);
        db.execSQL(CREATE_ATTENDANCE_TABLE);
    }
    public Cursor getAttendanceData(){
        return db.rawQuery("select * from "+ATTENDANCE_TABLE,null);
    }
    //working with event table
    public void insertEventData(String event_name, String event_details, String event_date, String submit_date, String school_number){
        ContentValues contentValues = new ContentValues();
        contentValues.put(EVENT_NAME, event_name);
        contentValues.put(EVENT_DETAIL, event_details);
        contentValues.put(EVENT_DATE, event_date);
        contentValues.put(EVENTSUBMIT_DATE,submit_date);
        contentValues.put(EVENTSCHOOL_NUMBER,school_number);
        int a = (int) db.insert(EVENT_TABLE,null,contentValues);
        Log.e("a.....", String.valueOf(a));
    }
    public void clearEventData(){
        db.execSQL("DROP TABLE IF EXISTS " +EVENT_TABLE);
        db.execSQL(CREATE_EVENT_TABLE);
    }
    public Cursor getEventData(){
        return db.rawQuery("select * from "+EVENT_TABLE,null);
    }


    //working with subject table
    public void insertSubjectData(String sub_name){
        ContentValues contentValues = new ContentValues();
        contentValues.put(SUBJECT_NAME, sub_name);
        int a = (int) db.insert(SUBJECT_TABLE,null,contentValues);
        Log.e("a.....", String.valueOf(a));
    }
    public void clearSubjectData(){
        db.execSQL("DROP TABLE IF EXISTS " +SUBJECT_TABLE);
        db.execSQL(CREATE_SUBJECT_TABLE);
    }
    public Cursor getSubjectData(){
        return db.rawQuery("select * from "+SUBJECT_TABLE,null);
    }



    //working with Marks table
    public void insertMarksData(String date, String exam_name, String total, String marks, String percentage){
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXAM_DATE_MARKS, date);
        contentValues.put(EXAM_NAME_MARKS, exam_name);
        contentValues.put(TOTAL_MARKS, total);
        contentValues.put(OBTAIN_MARKS,marks);
        contentValues.put(MARKS_PERCENTAGE, percentage);
        int a = (int) db.insert(MARKS_TABLE,null,contentValues);
        Log.e("a.....", String.valueOf(a));
    }
    public void clearMarksData(){
        db.execSQL("DROP TABLE IF EXISTS " +MARKS_TABLE);
        db.execSQL(CREATE_MARKS_TABLE);
    }
    public Cursor getMarksData(){
        return db.rawQuery("select * from "+MARKS_TABLE,null);
    }

    //working with Management table
    public void insertManagementData(String first_name, String last_name, String mobile, String gmail, String pic, String designation, String qualification, String interests_field, String contact_detail){
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIRST_NAME, first_name);
        contentValues.put(LAST_NAME, last_name);
        contentValues.put(MOBILE, mobile);
        contentValues.put(GMAIL,gmail);
        contentValues.put(PIC, pic);
        contentValues.put(DESIGNATION, designation);
        contentValues.put(QUALIFICATION, qualification);
        contentValues.put(INTERESTS_FIELD, interests_field);
        contentValues.put(CONTACT_DETAIL, contact_detail);
        int a = (int) db.insert(MANAGEMENT_TABLE,null,contentValues);
        Log.e("a.....", String.valueOf(a));
    }
    public void clearManagementData(){
        db.execSQL("DROP TABLE IF EXISTS " +MANAGEMENT_TABLE);
        db.execSQL(CREATE_MANAGEMENT_TABLE);
    }
    public Cursor getManagementData(){
        return db.rawQuery("select * from "+MANAGEMENT_TABLE,null);
    }



}
