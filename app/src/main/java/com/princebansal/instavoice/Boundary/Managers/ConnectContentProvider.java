/*
 * Copyright (c) 2016. Created by Prince Bansal on 16-08-2016.
 */

package com.princebansal.instavoice.Boundary.Managers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import static com.princebansal.instavoice.Control.DatabaseContract.AUTHORITY;
import static com.princebansal.instavoice.Control.DatabaseContract.CONTENT_ITEM_TYPE_MESSAGE;
import static com.princebansal.instavoice.Control.DatabaseContract.CONTENT_TYPE_DIR_MESSAGE;
import static com.princebansal.instavoice.Control.DatabaseContract.CONTENT_URI_MESSAGE;


public class ConnectContentProvider extends ContentProvider {

    public static final UriMatcher URI_MATCHER = buildUriMatcher();
    public static final String PATH_MESSAGE = "messages";
    public static final int PATH_TOKEN_MESSAGE = 100;
    public static final String PATH_FOR_ID_MESSAGE = "messages/*";
    public static final int PATH_FOR_ID_TOKEN_MESSAGE = 200;

    // Uri Matcher for the content provider
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AUTHORITY;
        matcher.addURI(authority, PATH_MESSAGE, PATH_TOKEN_MESSAGE);
        matcher.addURI(authority, PATH_FOR_ID_MESSAGE, PATH_FOR_ID_TOKEN_MESSAGE);
        return matcher;
    }

    // Content Provider stuff

    private ConnectDatabase dbHelper;

    @Override
    public boolean onCreate() {
        Context ctx = getContext();
        dbHelper = new ConnectDatabase(ctx);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case PATH_TOKEN_MESSAGE:
                return CONTENT_TYPE_DIR_MESSAGE;
            case PATH_FOR_ID_TOKEN_MESSAGE:
                return CONTENT_ITEM_TYPE_MESSAGE;
            default:
                throw new UnsupportedOperationException("URI " + uri + " is not supported.");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            // retrieve messages list
            case PATH_TOKEN_MESSAGE: {
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ConnectDatabase.TABLE_MESSAGES);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            case PATH_FOR_ID_TOKEN_MESSAGE: {
                String un=uri.getPathSegments().get(uri.getPathSegments().size()-2);
                String date=uri.getLastPathSegment();
                SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
                builder.setTables(ConnectDatabase.TABLE_MESSAGES);
                builder.appendWhere(ConnectDatabase.KEY_USERNAME + "=" + un+" and "+ConnectDatabase.KEY_DATE+"="+date);
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
            }
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int token = URI_MATCHER.match(uri);
        switch (token) {
            case PATH_TOKEN_MESSAGE: {
                long id = db.insertWithOnConflict(ConnectDatabase.TABLE_MESSAGES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return CONTENT_URI_MESSAGE.buildUpon().appendPath(String.valueOf(id)).build();
            }
            default: {
                throw new UnsupportedOperationException("URI: " + uri + " not supported.");
            }
        }
    }



    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int token = URI_MATCHER.match(uri);
        int rowsDeleted = -1;
        switch (token) {
            case (PATH_TOKEN_MESSAGE):
                rowsDeleted = db.delete(ConnectDatabase.TABLE_MESSAGES, selection, selectionArgs);
                break;
            case (PATH_FOR_ID_TOKEN_MESSAGE):
                String messageIdWhereClause = ConnectDatabase.KEY_DATE + "=" + uri.getLastPathSegment()
                        +" and "+ConnectDatabase.KEY_USERNAME+"="+uri.getPathSegments().get(uri.getPathSegments().size()-2);
                if (!TextUtils.isEmpty(selection))
                    messageIdWhereClause += " AND " + selection;
                rowsDeleted = db.delete(ConnectDatabase.TABLE_MESSAGES, messageIdWhereClause, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        // Notifying the changes, if there are any
        if (rowsDeleted != -1)
            getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    /**
     * Man..I'm tired..
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int token = URI_MATCHER.match(uri);
        switch (token) {
            case PATH_TOKEN_MESSAGE: {
                long id = db.update(ConnectDatabase.TABLE_MESSAGES, values, selection, selectionArgs);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return (int) id;
            }
            case PATH_FOR_ID_TOKEN_MESSAGE:
                String messageIdWhereClause = ConnectDatabase.KEY_DATE + "=" + uri.getLastPathSegment()
                        +" and "+ConnectDatabase.KEY_USERNAME+"="+uri.getPathSegments().get(uri.getPathSegments().size()-2);
                if (!TextUtils.isEmpty(selection))
                    messageIdWhereClause += " AND " + selection;
                long id = db.update(ConnectDatabase.TABLE_MESSAGES, values, messageIdWhereClause, selectionArgs);
                if (id != -1)
                    getContext().getContentResolver().notifyChange(uri, null);
                return (int) id;
            default: {
                throw new UnsupportedOperationException("URI: " + uri + " not supported.");
            }
        }
    }

}