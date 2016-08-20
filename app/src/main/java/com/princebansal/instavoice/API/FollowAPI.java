package com.princebansal.instavoice.API;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.LongSerializationPolicy;
import com.princebansal.instavoice.Boundary.API.AppController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by VSRK on 8/20/2016.
 */
public class FollowAPI {

    public static String BASE_URL="http://devblogs.instavoice.com/vb";


    public static void follow(final Context context, String blogger_id)
    {
        final ProgressDialog dialog=new ProgressDialog(context);
        dialog.setMessage("Please wait ..");
        dialog.show();

        SharedPreferences preferences=context.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);


        final String data;

        HashMap<String,Object> registerparams=new HashMap<>();
        registerparams.put("cmd","follow_request_blog");
        registerparams.put("cmd","follow_request_blog");
        registerparams.put("client_os","A");
        registerparams.put("client_os_ver","6.0");
        registerparams.put("client_app_ver","vb.01.01.001");
        registerparams.put("app_secure_key","b2ff398f8db492c19ef89b548b04889c");
        registerparams.put("blogger_id","17198561");
        registerparams.put("action","F");
        registerparams.put("user_secure_key",preferences.getString("user_secure_key",null));
        registerparams.put("iv_user_id",preferences.getString("iv_user_id",null));

        data= new Gson().toJson(registerparams);
        Log.v("data",data);



        StringRequest postRequest = new StringRequest(Request.Method.POST,BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("res called","res");
                        Log.v("response",response);
                        Toast.makeText(context,response,Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.v("err","error");
                dialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {


                Map<String, String> params = new HashMap<String, String>();
                params.put("data",data);
                return params;
            }
        };

        // Adding request to request queue
        RetryPolicy policy = new DefaultRetryPolicy(30000000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(postRequest);
    }
    public static void unfollow(final Context context, String blogger_id)
    {
        final ProgressDialog dialog=new ProgressDialog(context);
        dialog.setMessage("Please wait ..");
        dialog.show();

        SharedPreferences preferences=context.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);


        final String data;

        HashMap<String,Object> registerparams=new HashMap<>();
        registerparams.put("cmd","follow_request_blog");
        registerparams.put("cmd","follow_request_blog");
        registerparams.put("client_os","A");
        registerparams.put("client_os_ver","6.0");
        registerparams.put("client_app_ver","vb.01.01.001");
        registerparams.put("app_secure_key","b2ff398f8db492c19ef89b548b04889c");
        registerparams.put("blogger_id","17198561");
        registerparams.put("action","G");
        registerparams.put("user_secure_key",preferences.getString("user_secure_key",null));
        registerparams.put("iv_user_id",preferences.getString("iv_user_id",null));

        data= new Gson().toJson(registerparams);
        Log.v("data",data);



        StringRequest postRequest = new StringRequest(Request.Method.POST,BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("res called","res");
                        Log.v("response",response);
                        Toast.makeText(context,response,Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.v("err","error");
                dialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {


                Map<String, String> params = new HashMap<String, String>();
                params.put("data",data);
                return params;
            }
        };

        // Adding request to request queue
        RetryPolicy policy = new DefaultRetryPolicy(30000000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(postRequest);
    }
}
