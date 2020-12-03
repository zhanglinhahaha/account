package com.miniapp.account;

import android.content.Context;
import android.os.Bundle;

import com.miniapp.account.activity.ui.BaseActivity;

public class MainActivity extends BaseActivity {
    private static final String TAG = "AccountMain";
    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
    }
}
