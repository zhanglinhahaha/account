package com.miniapp.account.activity.ui;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import java.util.Calendar;

import com.miniapp.account.LogUtil;
import com.miniapp.account.R;
import com.miniapp.account.activity.AccountConstants;
import com.miniapp.account.db.AccountItemDb;

public class AccountAddOrUpdateActivity extends BaseActivity {
    private static final String TAG = "AccountAddItemActivity";
    private Context mContext = null;

    private EditText mUsername = null;
    private EditText mPrice = null;
    private EditText mComment = null;
    private EditText mDate = null;
    private Button mAddButton = null;
    private ContentValues mContentValues = null;
    private int mAddOrUpdate = 0;

    AccountItemDb databaseHelper = null;

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
        databaseHelper = new AccountItemDb(mContext);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.v(TAG, "onResume");
        initOnResume();
    }

    private void initOnResume() {

        if(mAddOrUpdate != 0) makeContent();

        mAddButton.setOnClickListener(mClickListener);
        mDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    hideInput();
                    switch (v.getId()) {
                        case R.id.db_date:
                            showDatePickDialog(new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                    //选择日期过后执行的事件
                                    mDate.setText(year + "-" + (month + 1) + "-" + day);
                                    mDate.setSelection(mDate.getText().toString().length());
                                }
                            }, mDate.getText().toString());
                            break;
                    }
                }
                return false;
            }
        });
    }

    private void makeContent() {
        LogUtil.v(TAG, "makeContent");
        mAddButton.setText(R.string.db_update);
        Cursor cursor = null;
        try {
            cursor = databaseHelper.query(mAddOrUpdate);
            if(cursor.getCount() != 0 ) {
                String name = cursor.getString(cursor.getColumnIndex(AccountItemDb.ACCOUNT_ITEM_USERNAME));
                String comment = cursor.getString(cursor.getColumnIndex(AccountItemDb.ACCOUNT_ITEM_COMMENT));
                String date = cursor.getString(cursor.getColumnIndex(AccountItemDb.ACCOUNT_ITEM_DATE));
                double price = cursor.getDouble(cursor.getColumnIndex(AccountItemDb.ACCOUNT_ITEM_PRICE));
                LogUtil.d(TAG, " date =" + date + " ,name  = " + name + ", comment = " + comment + ", price = " + price);
                mUsername.setText(name);
                mPrice.setText(""+price);
                mDate.setText(date);
                mComment.setText(comment);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(cursor != null){
                cursor.close();
            }
        }

    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LogUtil.d(TAG, v.toString());
            switch (v.getId()) {
                case R.id.db_add:
                    checkItemInfo();
                    break;
                default:
                    break;
            }
        }
    };

    private void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public void showDatePickDialog(DatePickerDialog.OnDateSetListener listener, String curDate) {
        Calendar calendar = Calendar.getInstance();
        int year = 0,month = 0,day = 0;
        try {
            year =Integer.parseInt(curDate.substring(0,curDate.indexOf("-"))) ;
            month =Integer.parseInt(curDate.substring(curDate.indexOf("-")+1,curDate.lastIndexOf("-")))-1 ;
            day =Integer.parseInt(curDate.substring(curDate.lastIndexOf("-")+1,curDate.length())) ;
        } catch (Exception e) {
            e.printStackTrace();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day=calendar.get(Calendar.DAY_OF_MONTH);
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,DatePickerDialog.THEME_HOLO_LIGHT,listener, year,month , day);
        datePickerDialog.show();
    }

    private boolean checkItemInfo() {
        String name = mUsername.getText().toString().trim();
        String priceStr = mPrice.getText().toString().trim();
        String comment = mComment.getText().toString().trim();
        String date = mDate.getText().toString().trim();

        if(name.length() == 0 || priceStr.length() == 0
                ||comment.length() == 0 || date.length() == 0) {
            return false;
        }

        double priceDouble = 0;
        try {
            priceDouble = Double.valueOf(priceStr);
        }catch (Exception e) {
            e.printStackTrace();
        }
        mContentValues = new ContentValues();
        mContentValues.put(AccountItemDb.ACCOUNT_ITEM_USERNAME, name);
        mContentValues.put(AccountItemDb.ACCOUNT_ITEM_PRICE, priceDouble);
        mContentValues.put(AccountItemDb.ACCOUNT_ITEM_COMMENT, comment);
        mContentValues.put(AccountItemDb.ACCOUNT_ITEM_DATE, date);
        LogUtil.d(TAG, mContentValues.toString());

        if(mAddOrUpdate == 0) {
            databaseHelper.insert(mContentValues);
        }else {
            databaseHelper.update(mContentValues, mAddOrUpdate);
        }

        Intent intent = new Intent(mContext, AccountMainActivity.class);
        startActivity(intent);
        finish();
        return true;
    }
}