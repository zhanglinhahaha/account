package com.miniapp.account.activity.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import com.miniapp.account.LogUtil;
import com.miniapp.account.R;
import com.miniapp.account.activity.AccountCursorAdapter;
import com.miniapp.account.db.AccountItemDb;
import com.miniapp.account.service.AccountService;

public class AccountFiltrateActivity extends BaseActivity {
    private static final String TAG = "AccountFiltrateActivity";
    private Spinner mDateSpinner = null;
    private ArrayAdapter<String> mDateAdapter;
    private ArrayList<String> mDateList = null;
    private Spinner mNameSpinner = null;
    private ArrayAdapter<String> mNameAdapter;
    private ArrayList<String> mNameList = null;
    private AccountService mAccountService = null;
    private Button mBtnQuery = null;
    private TextView mSumView = null;
    private Context mContext = null;

    private Cursor mCursor = null;
    private ListView mContentsList = null;
    private AccountItemDb databaseHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_filtrate);
        mContext = this;
        mDateSpinner = (Spinner) findViewById(R.id.date_spinner);
        mNameSpinner = (Spinner) findViewById(R.id.username_spinner);
        mBtnQuery = (Button) findViewById(R.id.btn_query);
        mContentsList = (ListView) findViewById(R.id.itemList);
        mSumView = (TextView) findViewById(R.id.querySum);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initOnResume();
    }

    private void initOnResume() {
        mAccountService = AccountService.getService(mContext);
        if(mAccountService != null) {
            mAccountService.updateDbData();
            mDateList = mAccountService.getDateList();
            mDateList.add(0, "all date");
            mDateAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, mDateList);
            mDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mDateSpinner.setAdapter(mDateAdapter);
//            mDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });

            mNameList = mAccountService.getUserNameList();
            mNameList.add(0, "all username");
            mNameAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, mNameList);
            mNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mNameSpinner.setAdapter(mNameAdapter);

            mBtnQuery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String username = mNameSpinner.getSelectedItem().toString();
                    String date = mDateSpinner.getSelectedItem().toString();
                    LogUtil.d(TAG, "Query username: " + username + ", date: " + date);
                    if(username.equals("all username")) username = null;
                    if(date.equals("all date")) date = null;
                    databaseHelper = AccountItemDb.getInstance(mContext);
                    mCursor = databaseHelper.queryDateAndName(date, username);
                    makeContent();
                }
            });
        }else {
            Toast.makeText(mContext, "Data Error", Toast.LENGTH_SHORT).show();
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
                String[] from = new String[] { AccountItemDb.ACCOUNT_ITEM_USERNAME };
                int[] to = new int[] { R.id.row_name };
                AccountCursorAdapter mAdapter = new AccountCursorAdapter(this, R.layout.row_account, mCursor, from,
                        to, null);
                mContentsList.setAdapter(mAdapter);
                mSumView.setText(String.format("%.2f", databaseHelper.getTotalMoneyForCursor(mCursor)));
            }
        }catch (Exception e) {
            LogUtil.e(TAG, "cursor == null" + (mCursor == null));
            e.printStackTrace();
        }
    }
}