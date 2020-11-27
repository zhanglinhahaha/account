package com.miniapp.account.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.miniapp.account.LogUtil;
import com.miniapp.account.broadcast.AccountBroadcastReceiver;
import com.miniapp.account.broadcast.BroadcastUtil;

/**
 * Created by zl on 20-11-27.
 */
public class AccountService extends Service {
    private static final String TAG = "AccountService";

    private AccountBroadcastReceiver mReceiver = null;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate: ");
        mReceiver = new AccountBroadcastReceiver();
        initIntentFilter();
    }

    private void initIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastUtil.FORCE_OFFLINE);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy: ");
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
}
