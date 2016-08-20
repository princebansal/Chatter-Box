/*
 * Copyright (c) 2016. Created by Prince Bansal on 16-08-2016.
 */

package com.princebansal.instavoice.Boundary.Managers;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.princebansal.instavoice.Control.DatabaseContract;
import com.princebansal.instavoice.Entity.Actors.Insight;
import com.princebansal.instavoice.Entity.Actors.Message;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DataHandler {

    private static final String TAG = DataHandler.class.getSimpleName();

    private Context context;
    private SharedPreferences preferences;
    private static DataHandler mInstance;

    private DataHandler(Context c) {
        this.context = c;

        try {
            preferences = context.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

    }

    public static DataHandler getInstance(Context context) {
        if (mInstance == null)
            mInstance = new DataHandler(context.getApplicationContext());
        return mInstance;
    }


    private void saveString(String key, String string) {
        preferences.edit().putString(key, string).commit();
    }

    public String getString(String key, String def) {

        return preferences.getString(key, def);
    }


    public void saveFirstTimeUser(String s) {
        saveString("firstTimeUser", s);
    }

    public Boolean getFirstTimeUser() {
        return Boolean.parseBoolean(getString("firstTimeUser", "true"));

    }

    public void saveConversation(ArrayList<Message> messageArrayList) {

        try {
            if (messageArrayList == null) {
                Log.i(TAG, "performRequest: messageList is null");
                messageArrayList = new ArrayList<>();
            }

            if (messageArrayList.size() == 0) {
                Log.d("messagesList", TAG + "> No server changes to update local database");
            } else {
                Log.d("messageList", TAG + "> Updating local database with remote changes");

                ArrayList<Message> localMessages = new ArrayList<>();
                Cursor curMessages = context.getContentResolver().query(DatabaseContract.CONTENT_URI_MESSAGE, null, null, null, null);
                if (curMessages != null) {
                    while (curMessages.moveToNext()) {
                        localMessages.add(Message.fromCursor(curMessages));
                    }
                    curMessages.close();
                }

                // See what Remote messages are missing on Local
                ArrayList<Message> messagesToLocal = new ArrayList<>();
                for (Message remMessage : messageArrayList) {
                    if (!localMessages.contains(remMessage))
                        messagesToLocal.add(remMessage);
                }


                // Updating local messages
                int i = 0;
                ContentValues messagesToLocalValues[] = new ContentValues[messageArrayList.size()];
                for (Message remoteMessage : messageArrayList) {
                    //Log.d("messagesList", TAG + "> Remote -> Local [" + remoteMessage.getBody() + "]");
                    messagesToLocalValues[i++] = remoteMessage.getContentValues();
                }
                context.getContentResolver().bulkInsert(DatabaseContract.CONTENT_URI_MESSAGE, messagesToLocalValues);
            }

            Log.d("messagesList", TAG + "> Finished.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Message> getMessagesList() {
        try {
            Cursor cursor = context.getContentResolver().query(DatabaseContract.CONTENT_URI_MESSAGE, null, null, null, ConnectDatabase.KEY_DATE + " ASC");
            List<Message> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                list.add(Message.fromCursor(cursor));
            }
            cursor.close();
            return list != null ? list : new ArrayList<Message>();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /*public List<Insight> getInsightsList() {
        try {
            List<Insight> insights=new ArrayList<>();
            Cursor cursor = context.getContentResolver().query(DatabaseContract.CONTENT_URI_MESSAGE, null, null, null, ConnectDatabase.KEY_DATE + " ASC");
            List<Message> list = new ArrayList<>();
            while (cursor.moveToNext()) {
                list.add(Message.fromCursor(cursor));
            }
            cursor.close();

            if (list != null) {
                HashMap<String, ArrayList<Message>> map = new HashMap<>();
                for (Message m : list) {
                    if (map.containsKey(m.getUsername()))
                        map.get(m.getUsername()).add(m);
                    else {
                        ArrayList<Message> temp=new ArrayList<>();
                        temp.add(m);
                        map.put(m.getUsername(),temp);
                    }
                }

                Iterator<String> keys = map.keySet().iterator();
                while (keys.hasNext()) {
                    String key=keys.next();
                    Insight insight=new Insight();
                    insight.setName(map.get(key).get(0).getName());
                    insight.setUserName(map.get(key).get(0).getUsername());
                    insight.setImageUrl(map.get(key).get(0).getImageUrl());
                    insight.setTotal(map.get(key).size());
                    int fav=0;
                    for(int i=0;i<map.get(key).size();i++){
                        if(map.get(key).get(i).isFavourite()){
                            fav++;
                        }
                    }
                    insight.setFavourites(fav);
                    insights.add(insight);
                }
                return insights;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    public Message getMessage(String username, String time) {
        try {

            Message message;
            Cursor cursor = context.getContentResolver().query(DatabaseContract.CONTENT_URI_MESSAGE, null,
                    ConnectDatabase.KEY_USERNAME + "=?" + " AND " + ConnectDatabase.KEY_DATE + "=?"
                    , new String[]{username, time}, null);

            if (cursor != null && cursor.moveToFirst()) {

                message = Message.fromCursor(cursor);
                cursor.close();
                return message;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
*/
    public boolean isDatabaseBuild() {

        Cursor cursor = context.getContentResolver().query(DatabaseContract.CONTENT_URI_MESSAGE, null, null, null, null);
        boolean bool = cursor != null && cursor.getCount() > 0;
        return bool;
    }

   /* public boolean markAsFavourite(Object o,boolean isFavourite) {
        Message message=(Message)o;
        ContentValues contentValues=new ContentValues();
        contentValues.put(ConnectDatabase.KEY_FAVOURITE,isFavourite?1:0);
        Log.i(TAG, "markAsFavourite: "+message.getUsername()+":"+message.getTime());
        int result=context.getContentResolver().update(DatabaseContract.CONTENT_URI_MESSAGE,contentValues,
                ConnectDatabase.KEY_USERNAME+"=?"+" AND "+ConnectDatabase.KEY_DATE+"=?",
                new String[]{message.getUsername(),message.getRawTime()});
        Log.i(TAG, "markAsFavourite: result:"+result);
        if(result!=-1){
            return true;
        }else {
            return false;
        }
    }
*/
    public String getUserSecureKey() {
        return getString("user_secure_key","");
    }

    public void saveSimSerailNumber(String sim_serial_num) {
        saveString("sim_serial_num",sim_serial_num);
    }

    public String getSimSerialNumber(){
        return getString("sim_serial_num","");
    }

    public String getIvUserId() {
        return getString("iv_user_id","0");
    }
}
