package com.miniapp.account.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.miniapp.account.LogUtil;
import com.miniapp.account.activity.AccountConstants;

/**
 * Created by zl on 20-11-27.
 */
public class AccountBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "AccountBroadcastReceive";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        LogUtil.d(TAG,"onReceiver() : " + action);
        if(action.equals(BroadcastUtil.FORCE_OFFLINE)) {
            Intent intent1 = new Intent();
            intent1.setClassName(AccountConstants.ACCOUNT_PACKAGE, AccountConstants.ACTIVITY_ACCOUNT_DIALOG);
            intent1.putExtra(AccountConstants.DIALOG_TYPE, AccountConstants.DIALOG_TYPE_LOGOUT);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}
