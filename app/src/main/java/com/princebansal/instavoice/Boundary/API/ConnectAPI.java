
/*
 * Copyright (c) 2016. Created by Prince Bansal on 16-08-2016.
 */

package com.princebansal.instavoice.Boundary.API;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import com.prince.android.haptik.Boundary.Managers.DataHandler;
import com.prince.android.haptik.Control.ErrorDefinitions;
import com.prince.android.haptik.Entity.Actors.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ConnectAPI {

    //Constants
    public static final int COVERSATION_FETCH_CODE = 1;
    private static final String TAG = ConnectAPI.class.getSimpleName();


    //Declared URLs
    private final String fetchUrl = "http://haptik.co/android/test_data/";

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
    }


}