package com.miniapp.account;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.miniapp.account.activity.ui.BaseActivity;
import com.miniapp.account.activity.ui.LoginActivity;
import com.miniapp.account.broadcast.BroadcastUtil;
import com.miniapp.account.db.AccountItemDb;
import com.miniapp.account.service.AccountService;

public class MainActivity extends BaseActivity {
    private static final String TAG = "AccountMainActivity";
    private AccountItemDb databaseHelper = null;
    private Context mContext = null;
    private AccountService mAccountService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        sqlTest();
        mAccountService = AccountService.getService(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mContext.sendBroadcast(new Intent(BroadcastUtil.FORCE_OFFLINE));
                break;
            default:
        }
        return true;
    }

    private void sqlTest() {
        databaseHelper = new AccountItemDb(this);
        //insert data
        Button addData = (Button) findViewById(R.id.add_data);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseHelper.insert("zl", 50.11, "eat lunch");
                databaseHelper.insert("ljm", 150, "buy cloth");
            }
        });
        //update data
        Button updateData = (Button) findViewById(R.id.update_data);
        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                databaseHelper.update();
                Intent intent = new Intent(mContext, LoginActivity.class);
                mContext.startActivity(intent);
                finish();
            }
        });
        //delete data
        Button deleteData = (Button) findViewById(R.id.delete_data);
        deleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseHelper.delete();
            }
        });
        //query data
        Button queryData = (Button) findViewById(R.id.query_data);
        queryData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseHelper.queryForAll();
            }
        });
    }
}
