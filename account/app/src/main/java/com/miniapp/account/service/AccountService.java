package com.miniapp.account.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import java.util.ArrayList;

import com.miniapp.account.LogUtil;
import com.miniapp.account.activity.AccountConstants;
import com.miniapp.account.activity.CategoryUtil;
import com.miniapp.account.broadcast.AccountBroadcastReceiver;
import com.miniapp.account.db.AccountDataDB;

/**
 * Created by zl on 20-11-27.
 */
public class AccountService extends Service {
    private static final String TAG = "AccountService";
    private AccountBroadcastReceiver mReceiver = null;
    private volatile static AccountService mAccountService = null;

    private ArrayList<String> mUserNameList = null;
    private ArrayList<String> mDateList = null;
    private double mTotalMoney = 0;

    public static AccountService getService(Context ctx) {
        LogUtil.d(TAG,"getService() called, (AccountService == null)? " + (mAccountService == null));
        if (mAccountService == null) {
            ctx.startService(new Intent(ctx, AccountService.class));
        }
        return mAccountService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate: ");
        mAccountService = this;
        mReceiver = new AccountBroadcastReceiver();
        initIntentFilter();
        syncUserNameList();
    }

    private void syncUserNameList() {
        updateDbData();
        getContentResolver().registerContentObserver(AccountDataDB.AccountGeneral.CONTENT_URI,true, changeObserver);
        CategoryUtil categoryUtil = CategoryUtil.getInstance(mAccountService);
        ArrayList<String> categoryList = categoryUtil.getCategoryUserNameList();
        for(String name : mUserNameList) {
            if(!categoryList.contains(name)) {
                categoryUtil.addUserCate(name, AccountConstants.IMAGE_IS_NULL);
            }
        }
    }

    private final ContentObserver changeObserver =
            new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange) {
                    LogUtil.d(TAG, "onChange: ");
                    updateDbData();
                }
            };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand: ");
        mAccountService = this;
        return super.onStartCommand(intent, flags, startId);
    }

    private void initIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AccountConstants.FORCE_OFFLINE);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy: ");
        getContentResolver().unregisterContentObserver(changeObserver);
        if(mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.v(TAG, "onBind()");
        return null;
    }

    private void updateDbData() {
        LogUtil.v(TAG, "updateDbData()");
        mUserNameList = new ArrayList<>();
        mDateList = new ArrayList<>();
        mTotalMoney = 0;
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(AccountDataDB.AccountGeneral.CONTENT_URI, null, null,
                    null, AccountDataDB.ACCOUNT_ITEM_DATE_ASC);
            int num = 0;
            if(cursor.moveToFirst()) {
                do {
                    mTotalMoney +=  cursor.getDouble(cursor.getColumnIndex(AccountDataDB.AccountGeneral.ACCOUNT_ITEM_PRICE));
                    String username = cursor.getString(cursor.getColumnIndex(AccountDataDB.AccountGeneral.ACCOUNT_ITEM_USERNAME));
                    String date = cursor.getString(cursor.getColumnIndex(AccountDataDB.AccountGeneral.ACCOUNT_ITEM_DATE));

                    String month = date.substring(0, date.lastIndexOf("-"));
                    num++;
                    if(!mUserNameList.contains(username)) {
                        mUserNameList.add(username);
                    }
                    if(!mDateList.contains(month)) {
                        mDateList.add(month);
                    }
                }while (cursor.moveToNext());
            }
            LogUtil.i(TAG, mTotalMoney + " " + mUserNameList.size() + " " + mDateList.size() + " " + num);
        } catch (Exception e) {
            LogUtil.e(TAG, "cursor " + e);
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    public ArrayList<String> getUserNameList() {
        return mUserNameList;
    }

    public ArrayList<String> getDateList() {
        return mDateList;
    }

    public double getTotalMoney() {
        return mTotalMoney;
    }
}
