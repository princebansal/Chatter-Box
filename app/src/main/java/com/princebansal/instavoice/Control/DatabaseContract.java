/*
 * Copyright (c) 2016. Created by Prince Bansal on 16-08-2016.
 */

package com.princebansal.instavoice.Control;

import android.media.MediaRecorder;
import android.net.Uri;

import java.io.FileDescriptor;


public class DatabaseContract {


    //Content Types
    public static final String CONTENT_TYPE_DIR_MESSAGE = "com.prince.android.cursor.dir/haptik.message";
    public static final String CONTENT_ITEM_TYPE_MESSAGE = "com.prince.android.cursor.item/haptik.message";

    public static final String AUTHORITY = "com.prince.android.provider";
    // content://<authority>/<path to type>
    public static final Uri CONTENT_URI_MESSAGE = Uri.parse("content://" + AUTHORITY + "/messages");

    MediaRecorder mediaRecorder=new MediaRecorder();

    void f(){
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(new FileDescriptor());
    }


}
