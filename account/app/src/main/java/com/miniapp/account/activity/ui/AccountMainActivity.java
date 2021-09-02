package com.miniapp.account.activity.ui;

import android.Manifest;
import android.app.Activity;
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
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import java.io.File;
import de.hdodenhof.circleimageview.CircleImageView;

import com.miniapp.account.BaseActivity;
import com.miniapp.account.BuildConfig;
import com.miniapp.account.FileUtil;
import com.miniapp.account.LogUtil;
import com.miniapp.account.R;
import com.miniapp.account.activity.AccountConstants;
import com.miniapp.account.activity.AccountCursorAdapter;
import com.miniapp.account.activity.LoginUtil;
import com.miniapp.account.activity.Util;
import com.miniapp.account.db.AccountDataDB;
import com.miniapp.account.db.DbToXmlManager;
import com.miniapp.account.db.XmlToDbManager;
import com.miniapp.account.extension.ExtensionUtil;
import com.miniapp.account.service.AccountService;



public class AccountMainActivity extends BaseActivity {
    private static final String TAG = "AccountMainActivity";

    private Context mContext = null;
    private AccountService mAccountService = null;

    private DrawerLayout mDrawerLayout = null;
    private Toolbar mToolbar = null;
    private NavigationView mNavigationView = null;
    private FloatingActionButton mFab = null;
    private View mHeaderView = null;
    private CircleImageView mUserImage = null;
    private TextView mUserName = null;
    private TextView mUserMail = null;

    private ListView mContentsList = null;
    private AccountCursorAdapter mAdapter = null;
    private SwipeRefreshLayout swipeRefresh = null;
    private Cursor cursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_main);
        LogUtil.v(TAG, "onCreate");
        mContext = this;
        mAccountService = AccountService.getService(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mFab = (FloatingActionButton)findViewById(R.id.fab);
        mContentsList = (ListView) findViewById(R.id.itemList);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        applyForPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.v(TAG, "onResume");
        initOnResume();
    }

    private void initOnResume() {
        //set home pic
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.account_menu);
        }

        mNavigationView.setItemIconTintList(null);
        mNavigationView.setCheckedItem(R.id.nav_home);//set selected status
        mNavigationView.setNavigationItemSelectedListener(mNavigationClickListener);

        //set onClickListener with user pic
        mHeaderView = mNavigationView.getHeaderView(0);
        mUserImage = (CircleImageView) mHeaderView.findViewById(R.id.icon_image);
        mUserImage.setOnClickListener(mButtonClickListener);

        mFab.setOnClickListener(mButtonClickListener);

        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        //solve the issue of "refresh when the listview doesn't reach the top".
        mContentsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                swipeRefresh.setEnabled(firstVisibleItem == 0);
            }
        });

        mAccountService.setDataCallback(new AccountService.DataChangedCallback() {
            @Override
            public void totalMoneyChanged(double money) {
                showRes(money);
            }
        });

        makeContents();
    }

    private void makeContents() {
        LogUtil.v(TAG,"makeContents()");
        try {
            cursor = getContentResolver().query(AccountDataDB.AccountGeneral.CONTENT_URI, null, null,
                    null, AccountDataDB.ACCOUNT_ITEM_DATE_ASC);
            if (cursor.getCount() == 0) {
                mContentsList.setVisibility(View.INVISIBLE);
            } else {
                mContentsList.setVisibility(View.VISIBLE);
                String[] from = new String[] { AccountDataDB.AccountGeneral.ACCOUNT_ITEM_USERNAME };
                int[] to = new int[] { R.id.row_name };
                mAdapter = new AccountCursorAdapter(this, R.layout.row_account, cursor, from,
                        to, mListVewItemClickListener, false);
                mContentsList.setAdapter(mAdapter);
            }
            showRes(mAccountService.getTotalMoney());
        }catch (Exception e) {
            LogUtil.e(TAG, "cursor == null" + (cursor == null));
            e.printStackTrace();
        }
    }

    private void showRes(double usedMoney) {
        TextView textView = (TextView) findViewById(R.id.querySum);
        int totalMoney = LoginUtil.getInstance(mContext).getLimitMoney();
        String showRes = "Used: " + String.format("%.2f", usedMoney)
                + "\nRemain: " + String.format("%.2f", (totalMoney- usedMoney));
        textView.setText(showRes);
    }

    private void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mAccountService.manualUpdate();
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.v(TAG,"refresh()");
                        swipeRefresh.setRefreshing(false);
                        makeContents();
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
                    getContentResolver().delete(AccountDataDB.AccountGeneral.CONTENT_URI,
                            AccountDataDB.AccountGeneral.ID + " = ?",new String[]{""+mDeleteDbId});
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = null;
        Uri uri = null;
        if (resultCode == Activity.RESULT_OK) {
            uri = data.getData();
            path = FileUtil.getPath(mContext, uri);
        }
        if(path != null && ExtensionUtil.openFile(path)) {
            XmlToDbManager importFile = new XmlToDbManager(mContext);
            int num = importFile.start(path, AccountDataDB.AccountGeneral.CONTENT_URI);
            Toast.makeText(mContext, String.format(getString(R.string.toast_import_num), String.valueOf(num)), Toast.LENGTH_SHORT).show();
            refresh();
        }else {
            Toast.makeText(mContext, "file is wrong", Toast.LENGTH_SHORT).show();
        }
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
                if(exportFile.start(path, getContentResolver().query(AccountDataDB.AccountGeneral.CONTENT_URI, null, null,
                    null, AccountDataDB.ACCOUNT_ITEM_DATE_ASC)) > 0) {
                    Toast.makeText(mContext, path, Toast.LENGTH_SHORT).show();
                }else {
                    LogUtil.e(TAG, "exportFile failed.");
                }
                break;
            case R.id.importDb:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
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
            Toast.makeText(mContext, R.string.toast_file_no_exist, Toast.LENGTH_SHORT).show();
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
            if(Util.isFastDoubleClick()) return;
            LogUtil.d(TAG, "mButtonClickListener called: " + v.toString());
            switch (v.getId()) {
                case R.id.fab:
                    Intent intent = new Intent(mContext, AccountAddOrUpdateActivity.class);
                    startActivity(intent);
                    break;
                case R.id.icon_image:
                    Intent intent1 = new Intent();
                    intent1.setClassName(AccountConstants.ACCOUNT_PACKAGE, AccountConstants.ACTIVITY_ACCOUNT_DIALOG);
                    intent1.putExtra(AccountConstants.DIALOG_TYPE, AccountConstants.DIALOG_TYPE_SET_LIMIT_MONEY);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent1);
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
                    mContext.sendBroadcast(new Intent(AccountConstants.FORCE_OFFLINE));
                    break;
                case R.id.nav_delete:
                    intent1.setClassName(AccountConstants.ACCOUNT_PACKAGE, AccountConstants.ACTIVITY_ACCOUNT_DIALOG);
                    intent1.putExtra(AccountConstants.DIALOG_TYPE, AccountConstants.DIALOG_TYPE_DELETE_ALL);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent1);
                    break;
                case R.id.nav_query:
                    intent1.setClassName(AccountConstants.ACCOUNT_PACKAGE, AccountConstants.ACTIVITY_ACCOUNT_FILTRATE);
                    intent1.putExtra(AccountConstants.QUERY_CATEGORY, "#NULL#");
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent1);
                    break;
                case R.id.nav_category:
                    intent1.setClassName(AccountConstants.ACCOUNT_PACKAGE, AccountConstants.ACTIVITY_ACCOUNT_CATEGORY);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent1);
                    break;
                case R.id.nav_analysis:
                    intent1.setClassName(AccountConstants.ACCOUNT_PACKAGE, AccountConstants.ACTIVITY_ACCOUNT_ANALYSIS);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent1);
                    break;
                case R.id.nav_storage:
                    intent1.setClassName(AccountConstants.ACCOUNT_PACKAGE, AccountConstants.ACTIVITY_ACCOUNT_DEPOSIT);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent1);
                default:break;
            }
            mDrawerLayout.closeDrawers();
            return true;
        }
    };
}