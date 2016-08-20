
/*
 * Copyright (c) 2016. Created by Prince Bansal on 16-08-2016.
 */

package com.princebansal.instavoice.Boundary.API;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.princebansal.instavoice.Boundary.Managers.DataHandler;
import com.princebansal.instavoice.Control.ErrorDefinitions;
import com.princebansal.instavoice.Entity.Actors.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConnectAPI {

    //Constants
    public static final int COVERSATION_FETCH_CODE = 1;
    private static final String TAG = ConnectAPI.class.getSimpleName();


    //Declared URLs
    private final String fetchUrl = "https://devblogs.instavoice.com/vb";

    private AppController appController;
    private ServerAuthenticateListener mServerAuthenticateListener;
    private DataHandler dataHandler;
    private long REQUEST_TIMEOUT = 30;

    public ConnectAPI() {
        appController = AppController.getInstance();
        dataHandler = DataHandler.getInstance(appController.getApplicationContext());
    }

    public void refresh() {
        if (mServerAuthenticateListener != null) {
            mServerAuthenticateListener.onRequestInitiated(COVERSATION_FETCH_CODE);
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    fetchUrl.trim(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {

                        Log.d("refresh", "response");

                        if (validateResponse(response)) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                Gson gson = new Gson();
                                ArrayList<Message> messageList = gson.fromJson(jsonObject.getJSONArray("messages").toString(), new TypeToken<ArrayList<Message>>() {}.getType());
                                dataHandler.saveConversation(messageList);
                                mServerAuthenticateListener.onRequestCompleted(COVERSATION_FETCH_CODE);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        } else {
                            mServerAuthenticateListener.onRequestError(COVERSATION_FETCH_CODE, ErrorDefinitions.ERROR_RESPONSE_INVALID);
                        }

                    } else {
                        mServerAuthenticateListener.onRequestError(COVERSATION_FETCH_CODE, ErrorDefinitions.ERROR_RESPONSE_NULL);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    mServerAuthenticateListener.onRequestError(COVERSATION_FETCH_CODE, error.getMessage());
                }
            }) {

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    Log.i(TAG, "parseNetworkResponse: " + response.toString());
                    return super.parseNetworkResponse(response);
                }

                @Override
                protected VolleyError parseNetworkError(VolleyError volleyError) {
                    Log.i(TAG, "parseNetworkError: " + volleyError.getCause());
                    ;
                    return super.parseNetworkError(volleyError);
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(16000,
                    2,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            appController.addToRequestQueue(stringRequest, "refreshrequest");
        } else {
            return;
        }
    }


    public void fetchMessages()
    {

        mServerAuthenticateListener.onRequestInitiated(COVERSATION_FETCH_CODE);
        final String data;
        HashMap<String,Object> registerparams=new HashMap<>();
        registerparams.put("cmd","fetch_vobolos");
        registerparams.put("client_os","A");
        registerparams.put("client_os_ver","6.0");
        registerparams.put("client_app_ver","vb.01.01.001");
        registerparams.put("app_secure_key","b2ff398f8db492c19ef89b548b04889c");
        registerparams.put("user_secure_key",dataHandler.getUserSecureKey());
        registerparams.put("iv_user_id",Integer.parseInt(dataHandler.getIvUserId()));
        registerparams.put("sim_serial_num",dataHandler.getSimSerialNumber());
        data= new Gson().toJson(registerparams);
        Log.v("data",data);



        StringRequest postRequest = new StringRequest(Request.Method.POST,fetchUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("res called","res");
                        Log.v("response",response);

                        try {

                            JSONObject res = new JSONObject(response);
                            if (res.getString("status").equals("ok"))
                            {
                                Gson gson=new Gson();
                                List<Message> list=gson.fromJson(res.getJSONArray("blog_msgs").toString(),new TypeToken<List<Message>>() {}.getType());
                                mServerAuthenticateListener.onRequestCompleted(COVERSATION_FETCH_CODE,list);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.v("err","error");
                mServerAuthenticateListener.onRequestError(COVERSATION_FETCH_CODE,error.getMessage());
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


    private boolean validateResponse(String response) {
        if (TextUtils.isEmpty(response)) {
            return false;
        }
        try {

            if(new JSONObject(response).has("messages"))
            return true;
            else
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }

    public void setServerAuthenticateListener(ServerAuthenticateListener listener) {
        mServerAuthenticateListener = listener;
    }


    public interface ServerAuthenticateListener {
        void onRequestInitiated(int code);

        void onRequestCompleted(int code);

        void onRequestError(int code, String message);

        void onRequestCompleted(int coversationFetchCode, List<Message> list);

    }

    public void follow(final Context context, String blogger_id)
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
        registerparams.put("blogger_id",blogger_id);
        registerparams.put("action","F");
        registerparams.put("user_secure_key",preferences.getString("user_secure_key",null));
        registerparams.put("iv_user_id",preferences.getString("iv_user_id",null));

        data= new Gson().toJson(registerparams);
        Log.v("data",data);



        StringRequest postRequest = new StringRequest(Request.Method.POST,fetchUrl,
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
    public void unfollow(final Context context, String blogger_id)
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



        StringRequest postRequest = new StringRequest(Request.Method.POST,fetchUrl,
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