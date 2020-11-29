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
    private static final String LOGIN_FILENAME = "login_data";


    private SharedPreferences mPreferences = null;
    private SharedPreferences.Editor mEditor = null;
    private Context mContext = null;
    private static LoginUtil mInstance = null;

    private LoginUtil(Context context){
        mContext = context;
        mPreferences = mContext.getSharedPreferences(LOGIN_FILENAME, mContext.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static LoginUtil getInstance(Context context){
        if (mInstance == null) {
            mInstance = new LoginUtil(context);
        }
        return mInstance;
    }

    public void setLoginSettings(String username, String password, Boolean isRemember) {
        LogUtil.d(TAG, "setLoginSettings() called with: username = [" + username + "], password = [" + password + "], isRemember = [" + isRemember + "]");
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
}
