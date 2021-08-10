package com.miniapp.account.activity.ui;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import com.miniapp.account.BaseActivity;
import com.miniapp.account.LogUtil;
import com.miniapp.account.R;
import com.miniapp.account.activity.AccountConstants;
import com.miniapp.account.activity.CategoryUtil;
import com.miniapp.account.activity.Util;
import com.miniapp.account.db.AccountDataDB;

public class AccountAddOrUpdateActivity extends BaseActivity {
    private static final String TAG = "AccountAddItemActivity";
    private Context mContext = null;

    private EditText mUsername = null;
    private EditText mPrice = null;
    private EditText mComment = null;
    private EditText mDate = null;
    private Button mAddButton = null;
    private int mAddOrUpdate = 0;
    private int mPrivyType = 0;
    private String mPrivyName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.v(TAG, "onCreate");
        setContentView(R.layout.activity_account_add_item);
        mContext = this;
        mUsername = (EditText) findViewById(R.id.db_username);
        mPrice = (EditText) findViewById(R.id.db_price);
        mComment = (EditText) findViewById(R.id.db_comment);
        mDate = (EditText) findViewById(R.id.db_date);
        mAddButton = (Button) findViewById(R.id.db_add);
        mAddOrUpdate = getIntent().getIntExtra(AccountConstants.ADD_OR_UPDATE_TYPE, 0);
        mPrivyType = getIntent().getIntExtra(AccountConstants.PRIVATE_TYPE, 0);
        mPrivyName = getIntent().getStringExtra(AccountConstants.PRIVATE_USERNAME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.v(TAG, "onResume");
        initOnResume();
    }

    @Override
    protected void onDestroy() {
        LogUtil.v(TAG, "onDestroy");
        super.onDestroy();
    }

    private void initOnResume() {
        LogUtil.v(TAG, "initOnResume, mPrivyType: " + mPrivyType + ", mAddOrUpdate: " + mAddOrUpdate);
        if(mAddOrUpdate != 0) makeContent();

        mAddButton.setOnClickListener(mClickListener);
        mDate.setOnTouchListener(mTouchListener);
        //don't show keyboard
        mDate.setInputType(InputType.TYPE_NULL);

        if(mPrivyName != null) {
            mUsername.setText(mPrivyName);
            mUsername.setEnabled(false);
        }else {
            mUsername.setOnTouchListener(mTouchListener);
            mUsername.setInputType(InputType.TYPE_NULL);
        }
    }

    private void makeContent() {
        LogUtil.v(TAG, "makeContent");
        mAddButton.setText(R.string.db_update);
        Cursor cursor = null;
        try {
            Uri uri = AccountDataDB.AccountGeneral.CONTENT_URI;
            if(mPrivyType == 1) uri = AccountDataDB.AccountPrivate.CONTENT_URI;
            cursor = getContentResolver().query(uri, null,
                    AccountDataDB.AccountGeneral.ID + "=?", new String[] {mAddOrUpdate+""}, null);
            if(cursor.getCount() != 0 ) {
                cursor.moveToNext();
                String name = cursor.getString(cursor.getColumnIndex(AccountDataDB.AccountGeneral.ACCOUNT_ITEM_USERNAME));
                String comment = cursor.getString(cursor.getColumnIndex(AccountDataDB.AccountGeneral.ACCOUNT_ITEM_COMMENT));
                String date = cursor.getString(cursor.getColumnIndex(AccountDataDB.AccountGeneral.ACCOUNT_ITEM_DATE));
                double price = cursor.getDouble(cursor.getColumnIndex(AccountDataDB.AccountGeneral.ACCOUNT_ITEM_PRICE));
                LogUtil.d(TAG, " date =" + date + " ,name  = " + name + ", comment = " + comment + ", price = " + price);
                mUsername.setText(name);
                mPrice.setText(""+price);
                mDate.setText(date);
                mComment.setText(comment);
            }
        }catch(Exception e){
            LogUtil.e(TAG, "cursor, " + e);
            e.printStackTrace();
        }finally{
            if(cursor != null){
                cursor.close();
            }
        }

    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction()==MotionEvent.ACTION_DOWN){
                if (v.getId() == R.id.db_date) {
                    showDatePickDialog(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            String timeTmp = year + "-"
                                    + (month+1 > 9 ? month+1 : ("0" + (month+1))) + "-"
                                    + (day > 9 ? day : "0" + day);
                            LogUtil.d(TAG, "timeTmp: " + timeTmp);
                            mDate.setText(timeTmp);
                            mDate.setSelection(mDate.getText().toString().length());
                        }
                    }, mDate.getText().toString());
                }else if(v.getId() == R.id.db_username) {
                    showListPopup();
                }
            }
            return false;
        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LogUtil.d(TAG, v.toString());
            if (v.getId() == R.id.db_add) {
                if(Util.isFastDoubleClick()) {
                    return;
                }
                checkItemInfo();
            }
        }
    };

    private void showDatePickDialog(DatePickerDialog.OnDateSetListener listener, String curDate) {
        Calendar calendar = Calendar.getInstance();
        int year = 0, month = 0, day = 0;
        try {
            year =Integer.parseInt(curDate.substring(0, curDate.indexOf("-"))) ;
            month =Integer.parseInt(curDate.substring(curDate.indexOf("-") + 1, curDate.lastIndexOf("-"))) - 1 ;
            day =Integer.parseInt(curDate.substring(curDate.lastIndexOf("-") + 1, curDate.length())) ;
        } catch (Exception e) {
            LogUtil.e(TAG, "curDate is empty or format error" + e);
            e.printStackTrace();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, listener, year, month, day);
        datePickerDialog.show();
    }

    private void showListPopup() {
        final ArrayList<String> list = CategoryUtil.getInstance(this).getCategoryUserNameList();
        if(list.size() > 0) {
            final ListPopupWindow listPopupWindow;
            listPopupWindow = new ListPopupWindow(this);
            listPopupWindow.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list));
            listPopupWindow.setAnchorView(mUsername);
            listPopupWindow.setModal(true);
            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mUsername.setText(list.get(i));
                    mUsername.setSelection(mUsername.getText().toString().length());
                    listPopupWindow.dismiss();
                }
            });
            listPopupWindow.show();
        }else {
            Toast.makeText(mContext, R.string.toast_add_category_first, Toast.LENGTH_SHORT).show();
        }

    }

    private void checkItemInfo() {
        String name = mUsername.getText().toString().trim();
        String priceStr = mPrice.getText().toString().trim();
        String comment = mComment.getText().toString().trim();
        String date = mDate.getText().toString().trim();

        if(name.length() == 0 || priceStr.length() == 0
                ||comment.length() == 0 || date.length() == 0) {
            Toast.makeText(mContext, R.string.toast_finish_all, Toast.LENGTH_SHORT).show();
            return;
        }

        double priceDouble = 0;
        try {
            priceDouble = Double.parseDouble(priceStr);
        }catch (Exception e) {
            e.printStackTrace();
        }
        ContentValues mContentValues = new ContentValues();
        mContentValues.put(AccountDataDB.AccountGeneral.ACCOUNT_ITEM_USERNAME, name);
        mContentValues.put(AccountDataDB.AccountGeneral.ACCOUNT_ITEM_PRICE, priceDouble);
        mContentValues.put(AccountDataDB.AccountGeneral.ACCOUNT_ITEM_COMMENT, comment);
        mContentValues.put(AccountDataDB.AccountGeneral.ACCOUNT_ITEM_DATE, date);
        LogUtil.d(TAG, mContentValues.toString());

        Uri uri = AccountDataDB.AccountGeneral.CONTENT_URI;
        if(mPrivyType == 1) uri = AccountDataDB.AccountPrivate.CONTENT_URI;

        if(mAddOrUpdate == 0) {
            getContentResolver().insert(uri, mContentValues);
        }else {
            getContentResolver().update(uri, mContentValues, AccountDataDB.AccountGeneral.ID + " = ?",
                    new String[]{""+mAddOrUpdate});
        }

        finish();
    }
}