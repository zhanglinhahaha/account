package com.miniapp.account.service;

import android.app.Service;
import android.content.Context;
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
    private static AccountService mAccountService = null;

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
    }

    private void initIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastUtil.FORCE_OFFLINE);
        intentFilter.addAction("zltext");
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
