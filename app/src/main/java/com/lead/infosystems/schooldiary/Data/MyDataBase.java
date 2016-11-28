package com.lead.infosystems.schooldiary.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Faheem on 28-11-2016.
 */

public class MyDataBase extends SQLiteOpenHelper {
    private static final String DB_NAME = "schoolDiary.db";
    private static final String CHAT_TABLE = "chat_table";

    private static final String CHAT_CONTACT = "chat_contact_table";
    private static final String CONTACT_USERID = "chat_userid";
    private static final String CONTACT_FIRST_NAME = "contact_first_name";
    private static final String CONTACT_LAST_NAME = "contact_flast_name";
    private static final String ID = "id";


    public MyDataBase(Context context) {
        super(context, DB_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+CHAT_CONTACT+" ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CONTACT_USERID+" TEXT, "+CONTACT_FIRST_NAME+" TEXT, "+CONTACT_LAST_NAME+" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE "+CHAT_CONTACT);
        onCreate(db);
    }
}
