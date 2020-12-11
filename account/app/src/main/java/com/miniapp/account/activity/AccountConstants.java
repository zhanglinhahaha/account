package com.miniapp.account.activity;

import android.os.Environment;

/**
 * Created by zl on 20-11-29.
 */
public class AccountConstants {
    public static final String ACCOUNT_PACKAGE = "com.miniapp.account";
    public static final String ACTIVITY_ACCOUNT_DIALOG = "com.miniapp.account.activity.ui.AccountDialog";
    public static final String ACTIVITY_ACCOUNT_CATEGORY = "com.miniapp.account.activity.ui.AccountCategoryActivity";

    /**
     * 增加还是更新, 增加 type 默认为 0
     * 更新的话 type 参数则是更新的 _id
     */
    public static final String ACTIVITY_ACCOUNT_ADD_OR_UPDATE = "com.miniapp.account.activity.ui.AccountAddOrUpdateActivity";
    public static final String ADD_OR_UPDATE_TYPE = "add_or_update";

    /**
     * 查询界面, 是自己选择, 还是带着用户类型的查询
     * 默认为 0
     */
    public static final String ACTIVITY_ACCOUNT_FILTRATE = "com.miniapp.account.activity.ui.AccountFiltrateActivity";
    public static final String QUERY_CATEGORY = "query_category";

    public static final String DIALOG_TYPE = "dialog_type";
    public static final int DIALOG_TYPE_LOGOUT = 0x01;
    public static final int DIALOG_TYPE_DELETE_ALL = 0x02;
    public static final int DIALOG_TYPE_ADD_CATEGORY = 0x03;
    public static final int DIALOG_TYPE_SET_LIMIT_MONEY = 0x04;

    public static String ACCOUNT_DIR_PATH = Environment.getExternalStorageDirectory() + "/miniAccount";
    public static final String EXTERNAL_FILE_PATH = ACCOUNT_DIR_PATH + "/account.xml";

    // define broadcast name
    public static final String FORCE_OFFLINE = "com.miniapp.account.broadcast.force_offline";

    public static final String ADD_CATEGORY_STRING = "add";
    public static final String IMAGE_IS_NULL = "0";
}
