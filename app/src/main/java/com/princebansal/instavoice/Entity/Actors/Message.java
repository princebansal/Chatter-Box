/*
 * Copyright (c) 2016. Created by Prince Bansal on 16-08-2016.
 */

package com.princebansal.instavoice.Entity.Actors;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.prince.android.haptik.Boundary.Managers.ConnectDatabase.COLUMNS;

public class Message {

    private static final String TAG = Message.class.getSimpleName();
    private String body;
    private String username;
    @SerializedName("Name")
    private String name;
    @SerializedName("image-url")
    private String imageUrl;
    @SerializedName("message-time")
    private String time;
    private String rawTime;
    private boolean favourite=false;
    private boolean selected=false;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        setRawTime(time);
        this.time = getTimeInMillis(time);;
    }

    public String getRawTime() {
        return rawTime;
    }

    public void setRawTime(String rawTime) {
        this.rawTime = rawTime;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private String getTimeInMillis(String time) {
        Log.i(TAG, "getTimeInMillis: "+time);
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        long timeInMillis;
        try {
            timeInMillis=simpleDateFormat.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return String.valueOf(timeInMillis);
    }

    public ContentValues getContentValues() throws JSONException {
        ContentValues values = new ContentValues();
        values.put(COLUMNS[0], getBody());
        values.put(COLUMNS[1], getUsername());
        values.put(COLUMNS[2], getName());
        values.put(COLUMNS[3], getImageUrl());
        values.put(COLUMNS[4], getTime());
        values.put(COLUMNS[5], isFavourite()?1:0);
        return values;
    }

    public static Message fromCursor(Cursor cursor) throws JSONException {
        Message message=new Message();
        message.setBody(cursor.getString(0));
        message.setUsername(cursor.getString(1));
        message.setName(cursor.getString(2));
        message.setImageUrl(cursor.getString(3));
        message.setTime(cursor.getString(4));
        message.setFavourite(cursor.getInt(5)>0);
        return message;
    }

    public static String formatTimeForView(Context context, String time) {

        return DateUtils.formatDateTime(context,Long.parseLong(time),DateUtils.FORMAT_SHOW_TIME);

    }

    public static String formatDateForView(Context context, String time) {

        return DateUtils.formatDateTime(context,Long.parseLong(time),DateUtils.FORMAT_ABBREV_RELATIVE);

    }
}
