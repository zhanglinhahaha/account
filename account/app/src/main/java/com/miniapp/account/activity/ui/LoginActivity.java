package com.miniapp.account.activity.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.miniapp.account.MainActivity;
import com.miniapp.account.R;

import com.miniapp.account.LogUtil;
import com.miniapp.account.activity.LoginUtil;

/**
 * Created by zl on 20-11-27.
 */
public class LoginActivity extends BaseActivity {

    private static final String TAG = "AccountLoginActivity";

    private EditText accountEdit;
    private EditText passwordEdit;
    private Button login;
    private CheckBox rememberPass;

    private LoginUtil mLoginUtil = null;
    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        accountEdit = (EditText) findViewById(R.id.account);
        passwordEdit = (EditText) findViewById(R.id.password);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);
        login = (Button) findViewById(R.id.login);
        mLoginUtil = LoginUtil.getInstance(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initOnResume();
    }

    private void initOnResume() {
        boolean isRemember = mLoginUtil.getLoginRememberPassword();
        if(isRemember) {
            LogUtil.d(TAG,  "isRemember is true, gotoMainActivity");
            gotoMainActivity();
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                if(account.equals("admin") && password.equals("123456")) {

                    if(rememberPass.isChecked()) {
                        mLoginUtil.setLoginPassword(password);
                        mLoginUtil.setLoginUsername(account);
                        mLoginUtil.setLoginRememberPassword(true);
                    }else {
                        mLoginUtil.setLoginRememberPassword(false);
                        //mLoginUtil.getEditor().clear();
                    }
                    mLoginUtil.getEditor().apply();
                    gotoMainActivity();
                }else {
                    Toast.makeText(mContext,"account or password is error",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
        finish();
    }
}