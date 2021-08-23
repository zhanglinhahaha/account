package com.miniapp.account.activity.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

import com.miniapp.account.BaseActivity;
import com.miniapp.account.LogUtil;
import com.miniapp.account.R;
import com.miniapp.account.activity.AccountConstants;
import com.miniapp.account.activity.AccountCursorAdapter;
import com.miniapp.account.activity.Util;
import com.miniapp.account.db.AccountDataDB;
import com.miniapp.account.service.AccountService;

public class AccountFiltrateActivity extends BaseActivity {
    private static final String TAG = "AccountFiltrateActivity";
    private Spinner mDateSpinner = null;
    private ArrayAdapter<String> mDateAdapter;
    private Spinner mNameSpinner = null;
    private ArrayAdapter<String> mNameAdapter;
    private AccountService mAccountService = null;
    private Button mBtnQuery = null;
    private TextView mSumView = null;
    private Context mContext = null;

    private Cursor mCursor = null;
    private ListView mContentsList = null;
    private LinearLayout mQuerySpinnerLayout = null;
    private String mPrivyUserName = null;
    private Boolean mNotPrivyType = true;
    private Toolbar mToolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_filtrate);
        LogUtil.v(TAG, "onCreate");
        mContext = this;
        mDateSpinner = (Spinner) findViewById(R.id.date_spinner);
        mNameSpinner = (Spinner) findViewById(R.id.username_spinner);
        mBtnQuery = (Button) findViewById(R.id.btn_query);
        mContentsList = (ListView) findViewById(R.id.itemList);
        mSumView = (TextView) findViewById(R.id.querySum);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) actionBar.setTitle(R.string.nav_menu_private);

        mQuerySpinnerLayout = (LinearLayout) findViewById(R.id.queryLayout);
        mPrivyUserName = getIntent().getStringExtra(AccountConstants.QUERY_CATEGORY);
        mNotPrivyType = mPrivyUserName != null && mPrivyUserName.equals(AccountConstants.NULL_CATEGORY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.v(TAG, "onResume, mPrivyUserName: " + mPrivyUserName + ", mNotPrivyType: " + mNotPrivyType);
        initOnResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.v(TAG, "onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.other_menu, menu);
        MenuItem addItem = menu.findItem(R.id.importFile);
        addItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.importFile) {
            Intent intent1 = new Intent();
            intent1.setClassName(AccountConstants.ACCOUNT_PACKAGE, AccountConstants.ACTIVITY_ACCOUNT_ADD_OR_UPDATE);
            intent1.putExtra(AccountConstants.PRIVATE_TYPE, 1);
            intent1.putExtra(AccountConstants.PRIVATE_USERNAME, mPrivyUserName);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent1);
        }
        return true;
    }

    private void initOnResume() {
        mAccountService = AccountService.getService(mContext);
        if(mAccountService != null) {
            if(mNotPrivyType) {
                mToolbar.setVisibility(View.GONE);
                ArrayList<String> mDateList = new ArrayList<>(mAccountService.getDateList());
                mDateList.add(0, "all date");
                mDateAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, mDateList);
                mDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mDateSpinner.setAdapter(mDateAdapter);

                ArrayList<String> mNameList = new ArrayList<>(mAccountService.getUserNameList());
                for(int i = mNameList.size(), j = 0; i > 0; --i, ++j) {
                    String tmp = getResources().getString(R.string.not_contain) + " " + mNameList.get(j);
                    mNameList.add(tmp);
                }
                mNameList.add(0, "all username");
                mNameAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, mNameList);
                mNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mNameSpinner.setAdapter(mNameAdapter);

                mBtnQuery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Util.isFastDoubleClick()) return;
                        boolean isNotContain = false;
                        String username = mNameSpinner.getSelectedItem().toString();
                        String date = mDateSpinner.getSelectedItem().toString();
                        if (username.equals("all username")) username = null;
                        else if(username.contains(" ")) {
                            username = username.substring(username.lastIndexOf(" ") + 1);
                            isNotContain = true;
                        }
                        if (date.equals("all date")) date = null;

                        LogUtil.d(TAG, "Query username: " + username + ", date: " + date + ", isNotContain: " + isNotContain);

                        mCursor = queryDateAndName(date, username, isNotContain, false);

                        makeContent();
                    }
                });
            }else {
                mCursor = queryDateAndName(null, mPrivyUserName, false, true);
                mQuerySpinnerLayout.setVisibility(View.GONE);
                mToolbar.setVisibility(View.VISIBLE);
            }
        }else {
            Toast.makeText(mContext, R.string.toast_date_error, Toast.LENGTH_SHORT).show();
        }
        makeContent();
    }

    private void makeContent() {
        LogUtil.d(TAG, "makeContent");
        try {
            if (mCursor.getCount() == 0) {
                mContentsList.setVisibility(View.INVISIBLE);
                mSumView.setVisibility(View.INVISIBLE);
            } else {
                mContentsList.setVisibility(View.VISIBLE);
                String[] from = new String[] { AccountDataDB.AccountGeneral.ACCOUNT_ITEM_USERNAME };
                int[] to = new int[] { R.id.row_name };
                AccountCursorAdapter mAdapter;
                if(mNotPrivyType) {
                    mAdapter = new AccountCursorAdapter(this, R.layout.row_account, mCursor, from,
                            to, null, false);
                }else {
                    mAdapter = new AccountCursorAdapter(this, R.layout.row_account, mCursor, from,
                            to, mListVewItemClickListener, true);
                }
                mContentsList.setAdapter(mAdapter);

                double usedMoney = getTotalMoneyForCursor(mCursor);
                String showRes = "Used: " + String.format("%.2f", usedMoney);
                mSumView.setText(showRes);
            }
        }catch (Exception e) {
            LogUtil.e(TAG, "cursor == null" + (mCursor == null));
            e.printStackTrace();
        }
    }

    private View.OnClickListener mListVewItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_delete:
                    Integer mDeleteDbId = Integer.valueOf(v.getTag().toString());
                    LogUtil.d(TAG,"onClick cancel button , mDeleteDbId = " + mDeleteDbId);
                    getContentResolver().delete(AccountDataDB.AccountPrivate.CONTENT_URI,
                            AccountDataDB.AccountGeneral.ID + " = ?",new String[]{""+mDeleteDbId});
                    break;
                default:
                    break;
            }
        }
    };

    private Cursor queryDateAndName(String date, String username, boolean isNotContain, boolean isPrivy) {
        Cursor cursor = null;
        String selection = null;
        String[] selectionArg = null;
        Uri uri = AccountDataDB.AccountGeneral.CONTENT_URI;
        if(isPrivy) uri = AccountDataDB.AccountPrivate.CONTENT_URI;
        if(date == null && username == null) {
            LogUtil.e(TAG, "Error, date and username both are null");
            return getContentResolver().query(uri, null, null,
                    null, AccountDataDB.ACCOUNT_ITEM_DATE_ASC);
        }else {
            if(date != null) {
                if(username != null) {
                    selection = AccountDataDB.AccountGeneral.ACCOUNT_ITEM_DATE + " LIKE ? " + " AND "
                            + AccountDataDB.AccountGeneral.ACCOUNT_ITEM_USERNAME + (isNotContain ? " != ? ": " = ? ");
                    selectionArg = new String[]{date + "%", username};
                }else {
                    selection = AccountDataDB.AccountGeneral.ACCOUNT_ITEM_DATE + " LIKE ? ";
                    selectionArg = new String[]{date + "%"};
                }
            }else {
                selection = AccountDataDB.AccountGeneral.ACCOUNT_ITEM_USERNAME + (isNotContain ? " != ? ": " = ? ");
                selectionArg = new String[]{username};
            }
        }
        cursor = getContentResolver().query(uri,
                null, selection, selectionArg, AccountDataDB.ACCOUNT_ITEM_DATE_ASC);

        return cursor;
    }

    private double getTotalMoneyForCursor(Cursor cursor) {
        double res = 0;
        boolean flag = false;
        if(cursor == null) {
            flag = true;
            cursor = getContentResolver().query(AccountDataDB.AccountGeneral.CONTENT_URI, null, null,
                    null, null);
        }
        try {
            if(cursor.moveToFirst()) {
                do {
                    res += cursor.getDouble(cursor.getColumnIndex(AccountDataDB.AccountGeneral.ACCOUNT_ITEM_PRICE));
                }while (cursor.moveToNext());
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "cursor " + e);
            e.printStackTrace();
        }finally {
            if(flag && cursor != null) {
                cursor.close();
            }
        }
        return res;
    }
}