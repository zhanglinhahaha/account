package com.miniapp.account.activity;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;

import com.miniapp.account.LogUtil;
import com.miniapp.account.activity.ui.AccountDepositActivity;

/**
 * Created by zl on 20-12-18.
 */
public class DepositUtil {
    private static final String TAG = "AccountDepositUtil";
    private static final String LOGIN_FILENAME = "deposit_data";
    private SharedPreferences mPreferences = null;
    private static DepositUtil mInstance = null;

    private ArrayList<AccountDepositActivity.DepositItem> mDepositList = new ArrayList<>();
    private ArrayList<String> mDepositItemList = new ArrayList<>();

    private DepositUtil(Context context){
        mPreferences = context.getSharedPreferences(LOGIN_FILENAME, Context.MODE_PRIVATE);
        iniDepositItemList();
        initDeposit();
    }

    public static DepositUtil getInstance(Context context){
        if (mInstance == null) {
            mInstance = new DepositUtil(context);
        }
        return mInstance;
    }

    private void setDepositItemList() {
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putInt("size", mDepositItemList.size());
        for (int i = 0; i < mDepositItemList.size(); ++i) {
            mEditor.putString("deposit" + i, mDepositItemList.get(i));
        }
        mEditor.apply();
    }

    private void iniDepositItemList() {
        int size = mPreferences.getInt("size",0);
        for(int i = 0; i < size; ++i) {
            mDepositItemList.add(mPreferences.getString("deposit" + i,null));
        }
    }

    private void initDeposit() {
        mDepositList.clear();
        for(String name : mDepositItemList) {
            mDepositList.add(new AccountDepositActivity.DepositItem(name, mPreferences.getFloat(name,0)));
        }
    }

    public ArrayList<AccountDepositActivity.DepositItem> getDepositList() {
        LogUtil.d(TAG, "mDepositList: " + mDepositList.size());
        return mDepositList;
    }

    public ArrayList<String> getCategoryUserNameList() {
        return mDepositItemList;
    }

    public void addDepositItem(String username, Float num) {
        LogUtil.d(TAG, "addDepositItem() called with: username = [" + username + "], num = [" + num + "]");
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putFloat(username, num);
        mEditor.apply();
        if(!mDepositItemList.contains(username)) {
            mDepositItemList.add(username);
            setDepositItemList();
        }
        initDeposit();
    }

    public void deleteDepositItem(String username) {
        LogUtil.d(TAG, "deleteDepositItem() called with: username = [" + username + "]");
        if(mDepositItemList.contains(username)) {
            mDepositItemList.remove(username);
            SharedPreferences.Editor mEditor = mPreferences.edit();
            mEditor.putFloat(username, 0);
            mEditor.apply();
            setDepositItemList();
        }
        initDeposit();
    }
}