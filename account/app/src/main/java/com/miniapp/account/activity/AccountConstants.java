package com.miniapp.account.activity;

import android.os.Environment;

/**
 * Created by zl on 20-11-29.
 */
public class AccountConstants {
    public static final String ACCOUNT_PACKAGE = "com.miniapp.account";
    public static final String ACTIVITY_ACCOUNT_DIALOG = "com.miniapp.account.activity.ui.AccountDialog";
    public static final String ACTIVITY_ACCOUNT_FILTRATE = "com.miniapp.account.activity.ui.AccountFiltrateActivity";

    /**
     * 增加还是更新, 增加 type 默认为 0
     * 更新的话 type 参数则是更新的 _id
     */
    public static final String ACTIVITY_ACCOUNT_ADD_OR_UPDATE = "com.miniapp.account.activity.ui.AccountAddOrUpdateActivity";
    public static final String ADD_OR_UPDATE_TYPE = "add_or_update";


    public static final String DIALOG_TYPE = "dialog_type";
    public static final int DIALOG_TYPE_LOGOUT = 0x01;
    public static final int DIALOG_TYPE_DELETE_ALL = 0x02;

    public static String ACCOUNT_DIR_PATH = Environment.getExternalStorageDirectory() + "/miniAccount";
    public static final String EXTERNAL_FILE_PATH = ACCOUNT_DIR_PATH + "/account.xml";
    public static int SDCARD_LOG_FILE_SAVE_DAYS = 0;
}
