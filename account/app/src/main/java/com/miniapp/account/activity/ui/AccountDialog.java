package com.miniapp.account.activity.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.miniapp.account.ActivityCollector;
import com.miniapp.account.LogUtil;
import com.miniapp.account.R;
import com.miniapp.account.activity.AccountConstants;
import com.miniapp.account.activity.LoginUtil;

public class AccountDialog extends BaseActivity {
    private static final String TAG = "AccountDialog";
    private AlertDialog mDialog = null;
    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_dialog);
        mContext = this;
        int buttonNum = getIntent().getIntExtra(AccountConstants.DIALOG_BUTTON_NUMBER, 0);
        switch (buttonNum) {
            case 1:
                showOneButtonDialog();
                break;
            default:
                LogUtil.e("error num");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.v(TAG, "onStop");
        if(mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private void showOneButtonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogCustom);
        builder.setTitle("Warning");
        builder.setMessage("You are force to be offline. Please try to login again.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCollector.finishAll();
                LoginUtil.getInstance(mContext).setLoginSettings(null, null, false);;
                LogUtil.d(TAG, "1 " + LoginUtil.getInstance(mContext).getLoginRememberPassword());
                Intent intent1 = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(intent1);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        mDialog = builder.create();
        mDialog.show();
    }

}