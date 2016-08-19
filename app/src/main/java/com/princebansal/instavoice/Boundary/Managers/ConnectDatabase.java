/*
 * Copyright (c) 2016. Created by Prince Bansal on 16-08-2016.
 */

package com.princebansal.instavoice.Boundary.Managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConnectDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "haptikDatabase";

    // Contacts table name
    public static final String TABLE_MESSAGES = "messages";

    // Contacts Table Columns names
    public static final String KEY_BODY = "message_body";
    public static final String KEY_USERNAME = "message_username";
    public static final String KEY_NAME = "message_name";
    public static final String KEY_IMAGE_URL = "message_image_url";
    public static final String KEY_DATE = "message_date";
    public static final String KEY_FAVOURITE = "message_favourite";

    public static final String[] COLUMNS = {KEY_BODY, KEY_USERNAME, KEY_NAME, KEY_IMAGE_URL, KEY_DATE,KEY_FAVOURITE};

    public ConnectDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + KEY_BODY + " TEXT," + KEY_USERNAME + " TEXT,"
                + KEY_NAME + " TEXT," + KEY_IMAGE_URL + " TEXT," + KEY_DATE + " TEXT,"
                + KEY_FAVOURITE + " INTEGER DEFAULT 0,"
                + "PRIMARY KEY ("+KEY_USERNAME+","+KEY_DATE+")"+");";
        sqLiteDatabase.execSQL(CREATE_MESSAGES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
            onCreate(sqLiteDatabase);
    }

}
