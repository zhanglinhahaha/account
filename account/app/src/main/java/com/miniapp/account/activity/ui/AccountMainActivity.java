package com.miniapp.account.activity.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import com.miniapp.account.LogUtil;
import com.miniapp.account.R;
import com.miniapp.account.activity.LoginUtil;
import com.miniapp.account.broadcast.BroadcastUtil;
import com.miniapp.account.service.AccountService;

import de.hdodenhof.circleimageview.CircleImageView;


public class AccountMainActivity extends BaseActivity {
    private static final String TAG = "AccountMainActivity";

    private Context mContext = null;
    private AccountService mAccountService = null;

    private DrawerLayout mDrawerLayout = null;
    private Toolbar mToolbar = null;
    private NavigationView mNavigationView = null;
    private FloatingActionButton mfab = null;
    private View mHeaderView = null;
    private CircleImageView mUserImage = null;
    private TextView mUserName = null;
    private TextView mUserMail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_main);
        LogUtil.d(TAG, "onCreate");
        mContext = this;
        mAccountService = AccountService.getService(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mfab = (FloatingActionButton)findViewById(R.id.fab);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume");
        initOnResume();
    }

    private void initOnResume() {
        //set home pic
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu_32px);
        }

        mNavigationView.setItemIconTintList(null);
        mNavigationView.setCheckedItem(R.id.nav_call);//set selected status
        mNavigationView.setNavigationItemSelectedListener(mNavigationClickListener);

        //set onClickListener with user pic
        mHeaderView = mNavigationView.getHeaderView(0);
        mUserImage = (CircleImageView) mHeaderView.findViewById(R.id.icon_image);
        mUserImage.setOnClickListener(mButtonClickListener);
//        mUserName = (TextView) mHeaderView.findViewById(R.id.username);
//        mUserName.setText(LoginUtil.getInstance(mContext).getLoginUsername());
//        mUserMail = (TextView) mHeaderView.findViewById(R.id.mail);
//        mUserMail.setText(LoginUtil.getInstance(mContext).getLoginPassword());

        mfab.setOnClickListener(mButtonClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LogUtil.d(TAG, "onOptionsItemSelected called: " + item.toString());
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.back:
            case R.id.delete:
            case R.id.settings:
            default:break;
        }
        return true;
    }

    private View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LogUtil.d(TAG, "mButtonClickListener called: " + v.toString());
            switch (v.getId()) {
                case R.id.fab:
//                    Snackbar.make(v, "Data Deleted", Snackbar.LENGTH_SHORT)
//                            .setAction("Undo", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    LogUtil.d(TAG, "Undo called");
//                                }
//                            }).show();
                    break;
                case R.id.icon_image:
                    LogUtil.d(TAG, "call icon_image test");
                    break;
                default:break;
            }
        }
    };

    private NavigationView.OnNavigationItemSelectedListener mNavigationClickListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            LogUtil.d(TAG, "onNavigationItemSelected called: " + menuItem.toString());
            switch (menuItem.getItemId()) {
                case R.id.nav_logout:
                    mContext.sendBroadcast(new Intent(BroadcastUtil.FORCE_OFFLINE));
                    break;
                case R.id.nav_call:
                default:break;
            }
            mDrawerLayout.closeDrawers();
            return true;
        }
    };
}