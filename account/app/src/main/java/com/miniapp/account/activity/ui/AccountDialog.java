package com.miniapp.account.activity.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.miniapp.account.ActivityCollector;
import com.miniapp.account.LogUtil;
import com.miniapp.account.R;
import com.miniapp.account.activity.AccountConstants;
import com.miniapp.account.activity.CategoryUtil;
import com.miniapp.account.activity.LoginUtil;
import com.miniapp.account.db.AccountItemDb;

public class AccountDialog extends BaseActivity {
    private static final String TAG = "AccountDialog";
    private AlertDialog mDialog = null;
    private Context mContext = null;
    private int mDialogType = 0;
    private int titleMsg = 0;
    private int posBtn = 0;

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
        showPopup();
    }

    private void showPopup() {
        switch (mDialogType) {
            case AccountConstants.DIALOG_TYPE_LOGOUT:
                titleMsg = R.string.dialog_logout_msg;
                showAlertDialog();
                break;
            case AccountConstants.DIALOG_TYPE_DELETE_ALL:
                titleMsg = R.string.dialog_delete_all_msg;
                showAlertDialog();
                break;
            case AccountConstants.DIALOG_TYPE_ADD_CATEGORY:
                titleMsg = R.string.dialog_add_category;
                posBtn = R.string.dialog_button_add;
                showOneEditTextDialog();
                break;
            case AccountConstants.DIALOG_TYPE_SET_LIMIT_MONEY:
                titleMsg = R.string.dialog_set_limit_money;
                posBtn = R.string.dialog_button_set;
                showOneEditTextDialog();
                break;
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

    private void showAlertDialog() {
        LogUtil.v(TAG, "showAlertDialog, " + getResources().getString(titleMsg));
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.dialog_warning_title);
        builder.setMessage(titleMsg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.dialog_button_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (mDialogType) {
                    case AccountConstants.DIALOG_TYPE_LOGOUT:
                        LoginUtil.getInstance(mContext).setLoginSettings(null, null, false);;
                        Intent intent1 = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(intent1);
                        ActivityCollector.finishAll();
                        break;
                    case AccountConstants.DIALOG_TYPE_DELETE_ALL:
                        AccountItemDb databaseHelper = AccountItemDb.getInstance(mContext);
                        databaseHelper.deleteAll();
                        finish();
                        break;
                    default:
                        break;
                }

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

    private void showOneEditTextDialog() {
        LogUtil.v(TAG, "showOneEditTextDialog, " + getResources().getString(titleMsg));
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View viewCategory = inflater.inflate(R.layout.add_category, null);
        final EditText etCategoryName = viewCategory.findViewById(R.id.input_category_name);
        if (mDialogType == AccountConstants.DIALOG_TYPE_SET_LIMIT_MONEY) {
            etCategoryName.setHint(R.string.dialog_button_set);
            etCategoryName.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setTitle(titleMsg).setView(viewCategory);
        builder.setPositiveButton(posBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (mDialogType) {
                    case AccountConstants.DIALOG_TYPE_ADD_CATEGORY:
                        if (etCategoryName.getText().toString().trim().length() != 0) {
                            CategoryUtil.getInstance(mContext).addUserCate(etCategoryName.getText().toString().trim(), "0");
                        }
                        break;
                    case AccountConstants.DIALOG_TYPE_SET_LIMIT_MONEY:
                        if (etCategoryName.getText().toString().trim().length() != 0) {
                            LoginUtil.getInstance(mContext).setLimitMoney(Integer.valueOf(etCategoryName.getText().toString().trim()));
                        }
                        break;
                    default:
                        break;
                }
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