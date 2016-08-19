package com.princebansal.instavoice.API;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.princebansal.instavoice.AppController;
import com.princebansal.instavoice.DashboardActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by VSRK on 8/19/2016.
 */
public class RegistrationAPI {

   public static String BASE_URL="http://devblogs.instavoice.com/vb";

    public static void registeruser(final Context context, String sim_serial_num, String phoneno, String countrycode, String networkOperator, final String deviceId, int PHONE_NO_EDIT_STATE, int COUNTRY_CODE_EDIT_STATE)
    {

        boolean phone_no_edited=false,opr_edited_state=false;
        if (PHONE_NO_EDIT_STATE==1)
        {
            phone_no_edited=true;
        }
        final ProgressDialog dialog=new ProgressDialog(context);
        dialog.setMessage("Please wait");
        dialog.show();

        final String data;
        HashMap<String,Object> registerparams=new HashMap<>();
        registerparams.put("cmd","join_user");
        registerparams.put("cmd","join_user");
        registerparams.put("client_os","A");
        registerparams.put("client_os_ver","6.0");
        registerparams.put("client_app_ver","vb.01.01.001");
        registerparams.put("app_secure_key","b2ff398f8db492c19ef89b548b04889c");
        registerparams.put("phone_num",countrycode+phoneno);
        registerparams.put("device_id",deviceId);
        registerparams.put("phone_num_edited",true);
        registerparams.put("opr_info_edited",false);
        registerparams.put("sim_serial_num",sim_serial_num);
        registerparams.put("sim_opr_mcc_mnc",networkOperator);
        registerparams.put("country_code",countrycode);
        data= new Gson().toJson(registerparams);
        Log.v("data",data);



        StringRequest postRequest = new StringRequest(Request.Method.POST,BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    Log.v("res called","res");
                        Log.v("response",response);
                        Toast.makeText(context,response,Toast.LENGTH_SHORT).show();

                        try {

                            JSONObject res = new JSONObject(response);
                            if (res.getString("status").equals("ok"))
                            {
                                String reg_secure_key=res.getString("reg_secure_key");
                                SharedPreferences preferences=context.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor=preferences.edit();
                                editor.putString("device_id",deviceId);
                                editor.commit();
                                verifyuser(reg_secure_key,context);
                            }


                        } catch (Throwable t) {
                        }


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

    public static void verifyuser(String reg_secure_key, final Context context)
    {
        final ProgressDialog dialog=new ProgressDialog(context);
        dialog.setMessage("Please wait verifying..");
        dialog.show();

        final String data;
        HashMap<String,Object> registerparams=new HashMap<>();
        registerparams.put("cmd","verify_user");
        registerparams.put("cmd","verify_user");
        registerparams.put("client_os","A");
        registerparams.put("client_os_ver","6.0");
        registerparams.put("client_app_ver","vb.01.01.001");
        registerparams.put("app_secure_key","b2ff398f8db492c19ef89b548b04889c");
        registerparams.put("reg_secure_key",reg_secure_key);
        registerparams.put("pin","1234");
        registerparams.put("self_verified",false);
        registerparams.put("cloud_secure_key","na");

        data= new Gson().toJson(registerparams);
        Log.v("data",data);



        StringRequest postRequest = new StringRequest(Request.Method.POST,BASE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v("res called","res");
                        Log.v("response",response);
                        Toast.makeText(context,response,Toast.LENGTH_SHORT).show();

                        try {

                            JSONObject res = new JSONObject(response);
                            if (res.getString("status").equals("ok"))
                            {
                                String login_id=res.getString("login_id");
                                String user_secure_key=res.getString("user_secure_key");
                                //
                                // Log.v("pass",res.getString("mqtt_password"));
                                SharedPreferences preferences=context.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor=preferences.edit();
                                editor.putString("login_id",login_id);
                                editor.putString("user_secure_key",user_secure_key);
                                //editor.putString("mqtt_password",res.getString("mqtt_password"));
                                editor.commit();

                                context.startActivity(new Intent(context, DashboardActivity.class));


                            }


                        } catch (Exception t) {
                            t.printStackTrace();
                        }


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
