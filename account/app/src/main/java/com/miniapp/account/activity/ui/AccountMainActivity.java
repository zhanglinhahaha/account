package com.miniapp.account.activity.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import com.miniapp.account.BuildConfig;
import com.miniapp.account.LogUtil;
import com.miniapp.account.R;
import com.miniapp.account.activity.AccountConstants;
import com.miniapp.account.activity.AccountCursorAdapter;
import com.miniapp.account.broadcast.BroadcastUtil;
import com.miniapp.account.db.AccountItemDb;
import com.miniapp.account.db.DbToXmlManager;
import com.miniapp.account.db.XmlToDbManager;
import com.miniapp.account.service.AccountService;

import java.io.File;

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

    private ListView mContentsList = null;
    private AccountCursorAdapter mAdapter = null;
    private AccountItemDb databaseHelper = null;
    private SwipeRefreshLayout swipeRefresh = null;
    private Cursor cursor = null;

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
        mContentsList = (ListView) findViewById(R.id.itemList);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        LogUtil.delFile();//delete logFile once a week
        applyForPermission();
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

        mfab.setOnClickListener(mButtonClickListener);

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        makeContents();
    }

    private void makeContents() {
        LogUtil.v(TAG,"makeContents()");
        databaseHelper = AccountItemDb.getInstance(this);
        try {
            cursor = databaseHelper.getCursor();
            if (cursor.getCount() == 0) {
                mContentsList.setVisibility(View.INVISIBLE);
            } else {
                mContentsList.setVisibility(View.VISIBLE);
                String[] from = new String[] { AccountItemDb.ACCOUNT_ITEM_USERNAME };
                int[] to = new int[] { R.id.row_name };
                mAdapter = new AccountCursorAdapter(this, R.layout.row_account, cursor, from,
                        to, mListVewItemClickListener);
                mContentsList.setAdapter(mAdapter);
                TextView textView = (TextView) findViewById(R.id.querySum);
                textView.setText(String.format("%.2f", databaseHelper.getTotalMoneyForCursor(null)));
            }
        }catch (Exception e) {
            LogUtil.e(TAG, "cursor == null" + (cursor == null));
            e.printStackTrace();
        }
    }

    private void refresh() {
        LogUtil.v(TAG,"refresh()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        makeContents();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private View.OnClickListener mListVewItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_delete:
                    Integer mDeleteDbId = Integer.valueOf(v.getTag().toString());
                    LogUtil.d(TAG,"onClick cancel button , mDeleteDbId = " + mDeleteDbId);
                    databaseHelper.delete(mDeleteDbId);
                    refresh();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        LogUtil.d(TAG, "onDestroy");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String path = AccountConstants.EXTERNAL_FILE_PATH;
        LogUtil.d(TAG, "onOptionsItemSelected called: " + item.toString());
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.exportDb:
                DbToXmlManager exportFile = new DbToXmlManager(mContext);
                if(exportFile.start(path) > 0) {
                    Toast.makeText(mContext, path, Toast.LENGTH_SHORT).show();
                }else {
                    LogUtil.e(TAG, "exportFile failed.");
                }
                break;
            case R.id.importDb:
                XmlToDbManager importFile = new XmlToDbManager(mContext);
                int num = importFile.start(path);
                Toast.makeText(mContext, "import Num = " + num, Toast.LENGTH_SHORT).show();
                refresh();
                break;
            case R.id.share:
                shareFiles(path);
                break;
            default:break;
        }
        return true;
    }

    public void shareFiles(String filepath) {
        File file = new File(filepath);
        LogUtil.d(TAG, filepath);
        if (file.exists()) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileprovider",file);
                LogUtil.d(TAG, ">= N, " + contentUri.toString());
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                LogUtil.d(TAG, Uri.fromFile(file).toString());
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            }
            shareIntent.setType("text/plain");
            Intent chooser = Intent.createChooser(shareIntent, "Share file...");
            mContext.startActivity(chooser);
        }else {
            Toast.makeText(mContext, "The file don't exist !!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyForPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }
    }

    private View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LogUtil.d(TAG, "mButtonClickListener called: " + v.toString());
            switch (v.getId()) {
                case R.id.fab:
                    Intent intent = new Intent(mContext, AccountAddOrUpdateActivity.class);
                    startActivity(intent);
                    break;
                case R.id.icon_image:
                    Toast.makeText(mContext, "The total consumption: " + databaseHelper.getTotalMoneyForCursor(null), Toast.LENGTH_SHORT).show();
                    LogUtil.d(TAG, "call icon_image test. " + "The total consumption: " + databaseHelper.getTotalMoneyForCursor(null));
                    break;
                default:break;
            }
        }
    };

    private NavigationView.OnNavigationItemSelectedListener mNavigationClickListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            LogUtil.d(TAG, "onNavigationItemSelected called: " + menuItem.toString());
            Intent intent1 = new Intent();
            switch (menuItem.getItemId()) {
                case R.id.nav_logout:
                    mContext.sendBroadcast(new Intent(BroadcastUtil.FORCE_OFFLINE));
                    break;
                case R.id.nav_task:
                    intent1.setClassName(AccountConstants.ACCOUNT_PACKAGE, AccountConstants.ACTIVITY_ACCOUNT_DIALOG);
                    intent1.putExtra(AccountConstants.DIALOG_TYPE, AccountConstants.DIALOG_TYPE_DELETE_ALL);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent1);
                    break;
                case R.id.nav_mail:
                    intent1.setClassName(AccountConstants.ACCOUNT_PACKAGE, AccountConstants.ACTIVITY_ACCOUNT_FILTRATE);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent1);
                    break;
                default:break;
            }
            mDrawerLayout.closeDrawers();
            return true;
        }
    };
}