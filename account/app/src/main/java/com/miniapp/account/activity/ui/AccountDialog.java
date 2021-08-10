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
import android.widget.Toast;

import com.miniapp.account.ActivityCollector;
import com.miniapp.account.BaseActivity;
import com.miniapp.account.LogUtil;
import com.miniapp.account.R;
import com.miniapp.account.activity.AccountConstants;
import com.miniapp.account.activity.CategoryUtil;
import com.miniapp.account.activity.DepositUtil;
import com.miniapp.account.activity.LoginUtil;
import com.miniapp.account.db.AccountDataDB;

public class AccountDialog extends BaseActivity {
    private static final String TAG = "AccountDialog";
    private AlertDialog mDialog = null;
    private Context mContext = null;
    private int mDialogType = 0;
    private int titleMsg = 0;
    private int posBtn = 0;
    private String hintOne = null;
    private String hintTwo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_dialog);
        LogUtil.v(TAG, "onCreate");
        mContext = this;
        mDialogType = getIntent().getIntExtra(AccountConstants.DIALOG_TYPE, 0);
        hintOne = getIntent().getStringExtra(AccountConstants.DIALOG_HINT_ONE);
        hintTwo = getIntent().getStringExtra(AccountConstants.DIALOG_HINT_TWO);
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
            case AccountConstants.DIALOG_TYPE_ADD_DEPOSIT_CATEGORY:
                titleMsg = R.string.dialog_add_category;
                posBtn = R.string.dialog_button_add;
                showEditTextDialog();
                break;
            case AccountConstants.DIALOG_TYPE_SET_LIMIT_MONEY:
                titleMsg = R.string.dialog_set_limit_money;
                posBtn = R.string.dialog_button_set;
                showEditTextDialog();
                break;
            case AccountConstants.DIALOG_TYPE_UPDATE_DEPOSIT_CATEGORY:
                titleMsg = R.string.dialog_update_category;
                posBtn = R.string.dialog_button_update;
                showEditTextDialog();
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
        LogUtil.v(TAG, "showAlertDialog, " + mDialogType);
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
                        getContentResolver().delete(AccountDataDB.AccountGeneral.CONTENT_URI, null, null);
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

    private void showEditTextDialog() {
        LogUtil.v(TAG, "showOneEditTextDialog, " + mDialogType);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View viewCategory = inflater.inflate(R.layout.add_category, null);
        final EditText etCategoryName = (EditText) viewCategory.findViewById(R.id.input_category_name);
        final EditText etCategoryNum = (EditText) viewCategory.findViewById(R.id.input_category_num);
        if (mDialogType == AccountConstants.DIALOG_TYPE_SET_LIMIT_MONEY) {
            etCategoryNum.setVisibility(View.GONE);
            etCategoryName.setHint(R.string.dialog_button_set);
            etCategoryName.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if(mDialogType == AccountConstants.DIALOG_TYPE_ADD_DEPOSIT_CATEGORY || mDialogType == AccountConstants.DIALOG_TYPE_UPDATE_DEPOSIT_CATEGORY) {
            etCategoryNum.setVisibility(View.VISIBLE);
            etCategoryName.setHint(R.string.db_username);
            etCategoryNum.setHint(R.string.nav_menu_storage);
            if(hintOne != null)  {
                etCategoryName.setText(hintOne);
            }
            if(hintTwo != null) {
                etCategoryNum.setText(hintTwo);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setTitle(titleMsg).setView(viewCategory);
        builder.setPositiveButton(posBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (etCategoryName.getText().toString().trim().length() == 0) {
                    Toast.makeText(mContext, R.string.toast_input_is_null, Toast.LENGTH_SHORT).show();
                }else {
                    switch (mDialogType) {
                        case AccountConstants.DIALOG_TYPE_ADD_CATEGORY:
                            CategoryUtil.getInstance(mContext).addUserCate(etCategoryName.getText().toString().trim(), "0");
                            break;
                        case AccountConstants.DIALOG_TYPE_SET_LIMIT_MONEY:
                            LoginUtil.getInstance(mContext).setLimitMoney(Integer.valueOf(etCategoryName.getText().toString().trim()));
                            break;
                        case AccountConstants.DIALOG_TYPE_ADD_DEPOSIT_CATEGORY:
                        case AccountConstants.DIALOG_TYPE_UPDATE_DEPOSIT_CATEGORY:
                            float deposit = 0f;
                            if(etCategoryNum.getText().toString().trim().length() != 0) deposit = Float.parseFloat(etCategoryNum.getText().toString());
                            DepositUtil.getInstance(mContext).addDepositItem(etCategoryName.getText().toString().trim(), deposit);
                            break;
                        default:
                            break;
                    }
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