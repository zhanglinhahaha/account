package com.miniapp.account.activity;

import android.content.Context;
import android.content.SharedPreferences;

import com.miniapp.account.LogUtil;

/**
 * Created by zl on 20-11-27.
 */
public class LoginUtil {
    private static final String TAG = "AccountLoginUtil";
    private static final String LOGIN_USERNAME = "username";
    private static final String LOGIN_PASSWORD = "password";
    private static final String LOGIN_REMEMBER_PASSWORD = "remember_password";
    private static final String LOGIN_ACCOUNT_LIMIT = "account_limit_money_every_month";
    private static final String LOGIN_FILENAME = "login_data";

    private SharedPreferences mPreferences = null;
    private static LoginUtil mInstance = null;

    private LoginUtil(Context context){
        mPreferences = context.getSharedPreferences(LOGIN_FILENAME, Context.MODE_PRIVATE);
    }

    public static LoginUtil getInstance(Context context){
        if (mInstance == null) {
            mInstance = new LoginUtil(context);
        }
        return mInstance;
    }

    public void setLoginSettings(String username, String password, Boolean isRemember) {
        LogUtil.d(TAG, "setLoginSettings() called with: username = [" + username + "], password = [" + password + "], isRemember = [" + isRemember + "]");
        SharedPreferences.Editor mEditor = mPreferences.edit();
        if(username != null) mEditor.putString(LOGIN_USERNAME, username);
        if(password != null) mEditor.putString(LOGIN_PASSWORD, password);
        if(isRemember != null) mEditor.putBoolean(LOGIN_REMEMBER_PASSWORD, isRemember);
        mEditor.apply();
    }

    public String getLoginUsername() {
        return mPreferences.getString(LOGIN_USERNAME, "");
    }

    public String getLoginPassword() {
        return mPreferences.getString(LOGIN_PASSWORD, "");
    }

    public Boolean getLoginRememberPassword() {
        return mPreferences.getBoolean(LOGIN_REMEMBER_PASSWORD, false);
    }

    public int getLimitMoney() {
        return mPreferences.getInt(LOGIN_ACCOUNT_LIMIT, 0);
    }

    public void setLimitMoney(int limitMoney) {
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putInt(LOGIN_ACCOUNT_LIMIT, limitMoney);
        mEditor.apply();
    }
}
