package com.miniapp.account.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class AccountDataDB {
    public static final String AUTHORITY_ACCOUNT = "com.miniapp.account.db.AccountDataDB";
    //desc表示降序排列，asc是升序排列
    public static final String ACCOUNT_ITEM_DATE_ASC = AccountDataDB.AccountGeneral.ACCOUNT_ITEM_DATE + " asc";

    public static final class AccountGeneral implements BaseColumns {
        public static final String TABLE_NAME = "account";
        public static final String ID = "_id";
        public static final String ACCOUNT_ITEM_USERNAME = "username";
        public static final String ACCOUNT_ITEM_PRICE = "price";
        public static final String ACCOUNT_ITEM_DATE = "date";
        public static final String ACCOUNT_ITEM_COMMENT = "comment";

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY_ACCOUNT + "/account");

        public static final String CREATE_ACCOUNT_GENERAL = "create table " + TABLE_NAME + " ("
                + ID + " integer primary key autoincrement, "
                + ACCOUNT_ITEM_USERNAME + " text, "
                + ACCOUNT_ITEM_PRICE + " DOUBLE, "
                + ACCOUNT_ITEM_DATE + " text, "
                + ACCOUNT_ITEM_COMMENT + " text)";
    }

    public static final class AccountPrivate implements BaseColumns {
        public static final String TABLE_NAME = "account_privy";
        public static final String ID = "_id";
        public static final String ACCOUNT_ITEM_USERNAME = "username";
        public static final String ACCOUNT_ITEM_PRICE = "price";
        public static final String ACCOUNT_ITEM_DATE = "date";
        public static final String ACCOUNT_ITEM_COMMENT = "comment";

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY_ACCOUNT + "/account_privy");

        public static final String CREATE_ACCOUNT_PRIVATE = "create table " + TABLE_NAME + " ("
                + ID + " integer primary key autoincrement, "
                + ACCOUNT_ITEM_USERNAME + " text, "
                + ACCOUNT_ITEM_PRICE + " DOUBLE, "
                + ACCOUNT_ITEM_DATE + " text, "
                + ACCOUNT_ITEM_COMMENT + " text)";
    }

}
