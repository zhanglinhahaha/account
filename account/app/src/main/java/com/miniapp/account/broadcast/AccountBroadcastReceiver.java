package com.miniapp.account.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.miniapp.account.ActivityCollector;
import com.miniapp.account.LogUtil;
import com.miniapp.account.R;
import com.miniapp.account.activity.LoginUtil;
import com.miniapp.account.activity.ui.LoginActivity;

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
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
            builder.setTitle("Warning");
            builder.setMessage("You are force to be offline. Please try to login again.");
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCollector.finishAll();
                    LoginUtil.getInstance(context).setLoginRememberPassword(false);
                    Intent intent1 = new Intent(context, LoginActivity.class);
                    context.startActivity(intent1);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.show();
        }
    }
}
