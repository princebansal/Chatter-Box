package com.princebansal.instavoice.Entity.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.princebansal.instavoice.API.RegistrationAPI;
import com.princebansal.instavoice.R;

public class RegisterActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE =1 ;
    EditText phone_no,country_code;
    Button submit_btn;
    int PHONE_NO_EDIT_STATE=0;
    int COUNTRY_CODE_EDIT_STATE=0;
    String networkOperator,deviceId,sim_serial_num,sim_opr_nm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();
        getphonestatepermission();

    }

    private void init()
    {
        phone_no=(EditText)findViewById(R.id.phone_no);
        country_code=(EditText)findViewById(R.id.country_code);
        submit_btn=(Button)findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(phone_no.getText().toString())&&!TextUtils.isEmpty(country_code.getText().toString()))
                {
                    RegistrationAPI.registeruser(RegisterActivity.this,sim_serial_num,phone_no.getText().toString(),country_code.getText().toString(),networkOperator,deviceId,PHONE_NO_EDIT_STATE,COUNTRY_CODE_EDIT_STATE);
                }
                else
                {
                    Toast.makeText(RegisterActivity.this,"Please provide all the details",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void getphonestatepermission() {

        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            getinbuiltdetails();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               getinbuiltdetails();
        }

    }




    private void getinbuiltdetails()
    {
        TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();


        Log.v("phoneno",mPhoneNumber);
        if (!TextUtils.isEmpty(mPhoneNumber))
        {
            country_code.setText(mPhoneNumber.substring(1,3));
            phone_no.setText(mPhoneNumber.substring(3,mPhoneNumber.length()));
            phone_no.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b==true)
                    {
                        PHONE_NO_EDIT_STATE=1;
                        COUNTRY_CODE_EDIT_STATE=1;
                    }
                }
            });


        }

        networkOperator = tMgr.getNetworkOperator();
        sim_serial_num=tMgr.getSimSerialNumber();

        if (TextUtils.isEmpty(networkOperator) == false) {
           Log.v("MCC_MNC",networkOperator);
            SharedPreferences prefs=getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
            SharedPreferences.Editor edit=prefs.edit();
            edit.putString("sim_opr_mcc_mnc",networkOperator);
            edit.commit();
        }
        Log.v("dev id",tMgr.getDeviceId());
        deviceId=tMgr.getDeviceId();
    }
}
