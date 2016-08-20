package com.princebansal.instavoice.API;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Config;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by VSRK on 8/20/2016.
 */
public class UploadAudio extends AsyncTask<Void,Void,Void> {


    Context context;
    Activity activity;
    String filepath;

    public UploadAudio(Context context,String filepath,Activity activity)
    {
        this.context=context;
        this.filepath=filepath;
        this.activity=activity;
    }

    public void uploadaudio()
    {
        HttpsURLConnection connection=null;
        DataOutputStream outputStream=null;
        String boundary="*****";
        String lineEnd="\r\n";
        int bytesAvaialable,bytesRead,bufferSize;
        int maxBuffersize=1*1024*1024;
        String twoHyphens="--";
        byte[] buffer;
        String data;

        SharedPreferences preferences=context.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);

        HashMap<String,Object> registerparams=new HashMap<>();
        registerparams.put("cmd","send_voice_blog");
        registerparams.put("cmd","send_voice_blog");
        registerparams.put("client_os","A");
        registerparams.put("client_os_ver","6.0");
        registerparams.put("client_app_ver","vb.01.01.001");
        registerparams.put("app_secure_key","b2ff398f8db492c19ef89b548b04889c");
        registerparams.put("user_secure_key",preferences.getString("user_secure_key",null));
        registerparams.put("iv_user_id",preferences.getString("iv_user_id",null));
        registerparams.put("msg_format","pcm");

        data= new Gson().toJson(registerparams);



        File file=new File(filepath);
        try {
            FileInputStream fileInputStream=new FileInputStream(file);
            URL url=new URL("https://devblogs.instavoice.com/vb");
            connection=(HttpsURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection","Keep-Alive");
            connection.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);
            connection.setRequestProperty("data",data);

            outputStream=new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens+boundary+lineEnd);
            outputStream.writeBytes("Content-Disposition:form-data;name=\"content\";filename=\""+filepath+"\""+lineEnd);
            outputStream.writeBytes(lineEnd);
            bytesAvaialable=fileInputStream.available();
            bufferSize=Math.min(bytesAvaialable,maxBuffersize);
            buffer=new byte[bufferSize];
            bytesRead=fileInputStream.read(buffer,0,bufferSize);
            while (bytesRead>0)
            {
                outputStream.write(buffer,0,bufferSize);
                bytesAvaialable=fileInputStream.available();
                bufferSize=Math.min(bytesAvaialable,maxBuffersize);
                bytesRead=fileInputStream.read(buffer,0,bufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens+boundary+twoHyphens+lineEnd);

            int serverResponsecode=connection.getResponseCode();
            final String serverResponseMessage=connection.getResponseMessage();
            Log.v("serresponse",serverResponseMessage);



             activity.runOnUiThread(new Runnable() {
                 @Override
                 public void run() {
                     try {

                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 }
             });

//

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected Void doInBackground(Void... s) {
        uploadaudio();
        return null;
    }
}
