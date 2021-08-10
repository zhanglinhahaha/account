package com.miniapp.account.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.miniapp.account.LogUtil;

public class AccountDataDBProvider extends ContentProvider {
    private static final String TAG = "AccountItemDb";
    private static final String DATABASE_NAME = "AccountItemDb.db";
    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase mSQLiteDatabase;
    private DatabaseHelper mDBHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(AccountDataDB.AccountGeneral.CREATE_ACCOUNT_GENERAL);
            db.execSQL(AccountDataDB.AccountPrivate.CREATE_ACCOUNT_PRIVATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + AccountDataDB.AccountGeneral.TABLE_NAME + ";");
            db.execSQL("drop table if exists " + AccountDataDB.AccountPrivate.TABLE_NAME + ";");
            onCreate(db);
        }
    }

    @Override
    public boolean onCreate() {
        mDBHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        mSQLiteDatabase = mDBHelper.getWritableDatabase();
        return mSQLiteDatabase != null;
    }

    private static final int ACCOUNT_GEN = 0;
    private static final int ACCOUNT_GEN_ID = 1;
    private static final int ACCOUNT_PRIVY = 2;
    private static final int ACCOUNT_PRIVY_ID = 3;

    private static UriMatcher matcher;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AccountDataDB.AUTHORITY_ACCOUNT, "account", ACCOUNT_GEN);
        matcher.addURI(AccountDataDB.AUTHORITY_ACCOUNT, "account/#", ACCOUNT_GEN_ID);
        matcher.addURI(AccountDataDB.AUTHORITY_ACCOUNT, "account_privy", ACCOUNT_PRIVY);
        matcher.addURI(AccountDataDB.AUTHORITY_ACCOUNT, "account_privy/#", ACCOUNT_PRIVY_ID);
    }

    @Override
    public String getType(Uri uri) {
        switch (matcher.match(uri)) {
            case ACCOUNT_GEN:
            case ACCOUNT_GEN_ID:
                return "*/*";
        }
        return null;
    }

    public String getTableName(Uri uri) {
        switch (matcher.match(uri)) {
            case ACCOUNT_GEN:
                return AccountDataDB.AccountGeneral.TABLE_NAME;
            case ACCOUNT_PRIVY:
                return AccountDataDB.AccountPrivate.TABLE_NAME;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String tableName = getTableName(uri);
        Uri contentUri;
        long id;

        switch (matcher.match(uri)) {
            case ACCOUNT_GEN:
                contentUri = AccountDataDB.AccountGeneral.CONTENT_URI;
                break;
            case ACCOUNT_PRIVY:
                contentUri = AccountDataDB.AccountPrivate.CONTENT_URI;
                break;
            default:
                throw new IllegalArgumentException();
        }

        id = mSQLiteDatabase.insert(tableName, null, values);
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(contentUri, id);
            getContext().getContentResolver().notifyChange(itemUri, null);
            return itemUri;
        } else {
            LogUtil.e(TAG, "insert failed");
            throw new SQLException();
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String tableName = getTableName(uri);
        String id;
        String whereSegment;
        int count;

        switch (matcher.match(uri)) {
            case ACCOUNT_GEN:
            case ACCOUNT_PRIVY:
                whereSegment = selection;
                break;
            case ACCOUNT_GEN_ID:
            case ACCOUNT_PRIVY_ID:
                id = uri.getPathSegments().get(1);
                whereSegment = "_id = " + id
                        + (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : "");
                break;
            default:
                throw new IllegalArgumentException();
        }

        count = mSQLiteDatabase.update(tableName, values, whereSegment, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName = getTableName(uri);
        String id;
        String whereSegment;
        int count;

        switch (matcher.match(uri)) {
            case ACCOUNT_GEN:
            case ACCOUNT_PRIVY:
                whereSegment = selection;
                break;
            case ACCOUNT_GEN_ID:
            case ACCOUNT_PRIVY_ID:
                id = uri.getPathSegments().get(1);
                whereSegment = "_id = " + id
                        + (!TextUtils.isEmpty(selection) ? "AND (" + selection + ')' : "");
                break;
            default:
                throw new IllegalArgumentException();
        }
        count = mSQLiteDatabase.delete(tableName, whereSegment, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String tableName = getTableName(uri);
        switch (matcher.match(uri)) {
            case ACCOUNT_GEN:
            case ACCOUNT_PRIVY:
                qb.setTables(tableName);
                break;
            case ACCOUNT_GEN_ID:
            case ACCOUNT_PRIVY_ID:
                qb.setTables(tableName);
                qb.appendWhere("_id=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException();
        }

        Cursor c = null;
        try {
            c = qb.query(mSQLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);
            c.setNotificationUri(getContext().getContentResolver(), uri);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return c;
    }
}
