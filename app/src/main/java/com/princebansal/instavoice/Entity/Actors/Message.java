/*
 * Copyright (c) 2016. Created by Prince Bansal on 16-08-2016.
 */

package com.princebansal.instavoice.Entity.Actors;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;


public class Message {

    private static final String TAG = Message.class.getSimpleName();

    @SerializedName("from_blogger_id")
    private long fromBloggerId;
    @SerializedName("msg_id")
    private long messageId;
    @SerializedName("msg_content_type")
    private String messageContentType;
    @SerializedName("msg_content")
    private Chatt chattContent;
    private long duration;
    @SerializedName("msg_dt")
    private long messageDate;
    private String annotation;
    @SerializedName("blogger_display_name")
    private String bloggerDisplayName;
    private String profileFolderName;
    private String blogFolderName;
    @SerializedName("is_msg_base64")
    private boolean isMessageBase64;
    @SerializedName("media_format")
    private String mediaFormat;
    @SerializedName("msg_flow")
    private String messageFlow;
    @SerializedName("isReceivedMsg")
    private boolean isReceivedMessage;
    private String type;
    @SerializedName("like_cnt")
    private int likeCount;
    @SerializedName("comment_cnt")
    private int commentCount;
    @SerializedName("shares_cnt")
    private int sharesCount;
    @SerializedName("is_self_liked")
    private boolean isSelfLiked;
    private String outputFile;


    public long getFromBloggerId() {
        return fromBloggerId;
    }

    public void setFromBloggerId(long fromBloggerId) {
        this.fromBloggerId = fromBloggerId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getMessageContentType() {
        return messageContentType;
    }

    public void setMessageContentType(String messageContentType) {
        this.messageContentType = messageContentType;
    }

    public Chatt getChattContent() {
        return chattContent;
    }

    public void setChattContent(Chatt chattContent) {
        this.chattContent = chattContent;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(long messageDate) {
        this.messageDate = messageDate;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getBloggerDisplayName() {
        return bloggerDisplayName;
    }

    public void setBloggerDisplayName(String bloggerDisplayName) {
        this.bloggerDisplayName = bloggerDisplayName;
    }

    public String getProfileFolderName() {
        return profileFolderName;
    }

    public void setProfileFolderName(String profileFolderName) {
        this.profileFolderName = profileFolderName;
    }

    public String getBlogFolderName() {
        return blogFolderName;
    }

    public void setBlogFolderName(String blogFolderName) {
        this.blogFolderName = blogFolderName;
    }

    public boolean isMessageBase64() {
        return isMessageBase64;
    }

    public void setMessageBase64(boolean messageBase64) {
        isMessageBase64 = messageBase64;
    }

    public String getMediaFormat() {
        return mediaFormat;
    }

    public void setMediaFormat(String mediaFormat) {
        this.mediaFormat = mediaFormat;
    }

    public String getMessageFlow() {
        return messageFlow;
    }

    public void setMessageFlow(String messageFlow) {
        this.messageFlow = messageFlow;
    }

    public boolean isReceivedMessage() {
        return isReceivedMessage;
    }

    public void setReceivedMessage(boolean receivedMessage) {
        isReceivedMessage = receivedMessage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getSharesCount() {
        return sharesCount;
    }

    public void setSharesCount(int sharesCount) {
        this.sharesCount = sharesCount;
    }

    public boolean isSelfLiked() {
        return isSelfLiked;
    }

    public void setSelfLiked(boolean selfLiked) {
        isSelfLiked = selfLiked;
    }

    public ContentValues getContentValues() throws JSONException {
        ContentValues values = new ContentValues();
        return values;
    }

    public static Message fromCursor(Cursor cursor) throws JSONException {
        Message message = new Message();
        return message;
    }

    public static String formatTimeForView(Context context, long time) {

        return DateUtils.formatDateTime(context, time, DateUtils.FORMAT_SHOW_TIME);

    }

    public static String formatDateForView(Context context, long date) {

        return DateUtils.formatDateTime(context, date, DateUtils.FORMAT_ABBREV_RELATIVE);

    }


    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }
}
