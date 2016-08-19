package com.princebansal.instavoice.Entity.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.princebansal.instavoice.*;
import com.princebansal.instavoice.API.SigninAPI;

/**
 * Created by VSRK on 8/19/2016.
 */
public class SigninActivity extends AppCompatActivity {

    EditText phone,password;
    TextView signup_text;
    Button submit_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        setinit();
    }

    private void init()
    {
        phone=(EditText)findViewById(R.id.phone_no);
        password=(EditText)findViewById(R.id.password);
        signup_text=(TextView)findViewById(R.id.signup_text);
        submit_btn=(Button)findViewById(R.id.submit_btn);

    }

    private void setinit()
    {
        signup_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SigninActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(phone.getText().toString())&&!TextUtils.isEmpty(password.getText().toString()))
                {
                    SigninAPI.singin(SigninActivity.this,phone.getText().toString(),password.getText().toString());
                }
                else
                {
                    Toast.makeText(SigninActivity.this,"Please enter all the details",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
