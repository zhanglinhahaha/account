package com.miniapp.account.activity;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;

import com.miniapp.account.R;
import com.miniapp.account.LogUtil;

/**
 * Created by zl on 20-12-8.
 */
public class CategoryUtil {
    private static final String TAG = "AccountCategoryUtil";
    private static final String LOGIN_FILENAME = "category_data";
    private SharedPreferences mPreferences = null;
    private static CategoryUtil mInstance = null;

    private ArrayList<AccountCategoryAdapter.Category> mUserCateList = new ArrayList<>();
    private ArrayList<String> mUserNameList = new ArrayList<>();

    private CategoryUtil(Context context){
        mPreferences = context.getSharedPreferences(LOGIN_FILENAME, Context.MODE_PRIVATE);
        initUserNameList();
        initCategory();
    }

    public static CategoryUtil getInstance(Context context){
        if (mInstance == null) {
            mInstance = new CategoryUtil(context);
        }
        return mInstance;
    }

    private void setUserNameList() {
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putInt("size", mUserNameList.size());
        for (int i = 0; i < mUserNameList.size(); ++i) {
            mEditor.putString("name" + i, mUserNameList.get(i));
        }
        mEditor.apply();
    }

    private void initUserNameList() {
        int size = mPreferences.getInt("size",0);
        for(int i = 0; i < size; ++i) {
            mUserNameList.add(mPreferences.getString("name" + i,null));
        }
    }

    private void initCategory() {
        mUserCateList.clear();
        for(String name : mUserNameList) {
            mUserCateList.add(new AccountCategoryAdapter.Category(name, mPreferences.getString(name,"0")));
        }
        mUserCateList.add(new AccountCategoryAdapter.Category("add", String.valueOf(R.drawable.add)));
    }

    public ArrayList<AccountCategoryAdapter.Category> getUserCateList() {
        LogUtil.d(TAG, "mUserCateList: " + mUserCateList.size());
        return mUserCateList;
    }

    public ArrayList<String> getCategoryUserNameList() {
        return mUserNameList;
    }

    public void addUserCate(String username, String imageId) {
        LogUtil.d(TAG, "addUserCate() called with: username = [" + username + "], imageId = [" + imageId + "]");
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putString(username, imageId);
        mEditor.apply();
        if(!mUserNameList.contains(username)) {
            mUserNameList.add(username);
            setUserNameList();
        }
        initCategory();
    }

    public void deleteUserCate(String username) {
        LogUtil.d(TAG, "deleteUserCate() called with: username = [" + username + "]");
        if(mUserNameList.contains(username)) {
            mUserNameList.remove(username);
            SharedPreferences.Editor mEditor = mPreferences.edit();
            mEditor.putString(username, "0");
            mEditor.apply();
            setUserNameList();
        }
        initCategory();
    }
}
