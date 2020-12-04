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
import com.miniapp.account.db.AccountItemDb;

public class AccountDialog extends BaseActivity {
    private static final String TAG = "AccountDialog";
    private AlertDialog mDialog = null;
    private Context mContext = null;
    private int mDialogType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_dialog);
        LogUtil.v(TAG, "onCreate");
        mContext = this;
        mDialogType = getIntent().getIntExtra(AccountConstants.DIALOG_TYPE, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.v(TAG, "onResume");
        initOnResume();
    }

    private void initOnResume() {
        switch (mDialogType) {
            case AccountConstants.DIALOG_TYPE_LOGOUT:
                showLogOutDialog();
                break;
            case AccountConstants.DIALOG_TYPE_DELETE_ALL:
                showDeleteAllDialog();
            default:
                break;
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

    private void showLogOutDialog() {
        LogUtil.v(TAG, "showLogOutDiag");
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.dialog_warning_title);
        builder.setMessage(R.string.dialog_logout_msg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.dialog_button_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LoginUtil.getInstance(mContext).setLoginSettings(null, null, false);;
                LogUtil.d(TAG, "1 " + LoginUtil.getInstance(mContext).getLoginRememberPassword());
                Intent intent1 = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(intent1);
                ActivityCollector.finishAll();
            }
        });
        builder.setNegativeButton(R.string.dialog_button_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        mDialog = builder.create();
        mDialog.show();
    }

    private void showDeleteAllDialog() {
        LogUtil.v(TAG, "showDeleteAllDialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.dialog_warning_title);
        builder.setMessage(R.string.dialog_delete_all_msg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.dialog_button_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AccountItemDb databaseHelper = new AccountItemDb(mContext);
                databaseHelper.deleteAll();
                finish();
            }
        });
        builder.setNegativeButton(R.string.dialog_button_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        mDialog = builder.create();
        mDialog.show();
    }

}