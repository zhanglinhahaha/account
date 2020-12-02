package com.miniapp.account.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.miniapp.account.LogUtil;
import com.miniapp.account.service.AccountService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zl on 20-4-30.
 */
public class AccountItemDb extends SQLiteOpenHelper {
    private static final String TAG = "AccountItemDb";
    private static final String DATABASE_NAME = "AccountItemDb.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ACCOUNT = "account";

    public static final String ID = "_id";
    public static final String ACCOUNT_ITEM_USERNAME = "username";
    public static final String ACCOUNT_ITEM_PRICE = "price";
    public static final String ACCOUNT_ITEM_DATE = "date";
    public static final String ACCOUNT_ITEM_COMMENT = "comment";

    private Context mContext = null;
    private SQLiteDatabase database = this.getWritableDatabase();
    private Cursor cursor = null;

    public static final String CREATE_ACCOUNT = "create table " + TABLE_ACCOUNT + " ("
            + ID + " integer primary key autoincrement, "
            + ACCOUNT_ITEM_USERNAME + " text, "
            + ACCOUNT_ITEM_PRICE + " DOUBLE, "
            + ACCOUNT_ITEM_DATE + " text, "
            + ACCOUNT_ITEM_COMMENT + " text)";

    public AccountItemDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACCOUNT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_ACCOUNT + ";");
        onCreate(db);
    }

    public void insert(String user, double price, String comment) {
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        ContentValues values = new ContentValues();
        values.put(ACCOUNT_ITEM_USERNAME, user);
        values.put(ACCOUNT_ITEM_PRICE, price);
        values.put(ACCOUNT_ITEM_COMMENT, comment);
        values.put(ACCOUNT_ITEM_DATE, dateFormat.format(date));
        database.insert(TABLE_ACCOUNT, null, values);
        LogUtil.d(TAG, "insert: ");
    }

    public void insert(ContentValues values) {
        database.insert(TABLE_ACCOUNT, null, values);
        LogUtil.d(TAG, "insert values:");
    }

    public void update(){
        ContentValues values = new ContentValues();
        values.put(ACCOUNT_ITEM_PRICE, 25);
        database.update(TABLE_ACCOUNT, values, ACCOUNT_ITEM_USERNAME + " = ?",
                new String[]{"zhanglin"});
        LogUtil.d(TAG, "update: ");
    }

    public void delete(){
        database.delete(TABLE_ACCOUNT, ACCOUNT_ITEM_COMMENT + " = ?",
                new String[]{"eat lunch"});
        LogUtil.d(TAG, "delete: ");
    }

    public void delete(Integer id){
        database.delete(TABLE_ACCOUNT, ID + " = ?",
                new String[]{""+id});
        LogUtil.d(TAG, "delete: " + id);
    }

    public void queryForUser(String user, String month) {
    }

    public void queryForAll() {
        Cursor cursor = database.query(TABLE_ACCOUNT, null, null ,
                null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                String user = cursor.getString(cursor.getColumnIndex(ACCOUNT_ITEM_USERNAME));
                String price = cursor.getString(cursor.getColumnIndex(ACCOUNT_ITEM_PRICE));
                String comment = cursor.getString(cursor.getColumnIndex(ACCOUNT_ITEM_COMMENT));
                String date = cursor.getString(cursor.getColumnIndex(ACCOUNT_ITEM_DATE));
                LogUtil.d(TAG, user + " " + price +
                        " " + comment + " " + date);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    public Cursor getCursor() {
        Cursor cursor = database.query(TABLE_ACCOUNT, null, null ,
                null, null, null, null);
        return cursor;
    }
}
