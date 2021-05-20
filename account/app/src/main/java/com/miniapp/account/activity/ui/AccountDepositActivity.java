package com.miniapp.account.activity.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;

import com.miniapp.account.BaseActivity;
import com.miniapp.account.LogUtil;
import com.miniapp.account.R;
import com.miniapp.account.activity.AccountConstants;
import com.miniapp.account.activity.DepositUtil;
import com.miniapp.account.activity.Util;


public class AccountDepositActivity extends BaseActivity {
    private static final String TAG = "AccountDepositActivity";

    private boolean isEditStatus = false;
    private boolean isVisibleStatus = false;
    private Toolbar mToolbar = null;
    private ListView mListView = null;
    private Context mContext = null;
    private Button mBtnAdd = null;
    private DepositUtil depositUtil = null;
    private ArrayList<DepositItem> depositItemArrayList = new ArrayList<>();
    private float mDepositSum = 0;
    private TextView mShowDepositSum = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.v(TAG, "onCreate");
        setContentView(R.layout.activity_account_deposit);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mListView = (ListView) findViewById(R.id.itemList);
        mBtnAdd = (Button) findViewById(R.id.item_add);
        mShowDepositSum = (TextView) findViewById(R.id.storageSum);
        depositUtil = DepositUtil.getInstance(this);
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.v(TAG, "onResume");
        isEditStatus = false;
        isVisibleStatus = false;
        initOnResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.v(TAG, "onDestroy");
    }
    private void initOnResume() {
        setSupportActionBar(mToolbar);
        mToolbar.setOnClickListener(mClickListener);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) actionBar.setTitle(R.string.nav_menu_storage);
        makeContent();
    }

    private void makeContent() {
        LogUtil.v(TAG, "makeContent, isEditStatus: " + isEditStatus + ", isVisibleStatus: " + isVisibleStatus);
        depositItemArrayList = depositUtil.getDepositList();

        DepositAdapter adapter = new DepositAdapter(mContext, R.layout.deposit_item, depositItemArrayList);
        mListView.setAdapter(adapter);
        if(isEditStatus) {
            mBtnAdd.setVisibility(View.VISIBLE);
            mBtnAdd.setOnClickListener(mClickListener);
        }else {
            mBtnAdd.setVisibility(View.GONE);
        }
        if(isVisibleStatus) {
            mShowDepositSum.setVisibility(View.VISIBLE);
            mDepositSum = 0;
            for(DepositItem item : depositItemArrayList) {
                mDepositSum += item.getDeposit();
            }
            mShowDepositSum.setText("Total: " + String.format("%.2f", mDepositSum));
        }else {
            mShowDepositSum.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if(isEditStatus) {
            isEditStatus = false;
            makeContent();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.other_menu, menu);
        MenuItem manage_item = menu.findItem(R.id.visibleItem);
        manage_item.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LogUtil.v(TAG, "onOptionsItemSelected called: " + item.toString());
        if(item.getItemId() == R.id.visibleItem) {
            if(Util.onClickButton5Times()) {
                isVisibleStatus = true;
            }else {
                isVisibleStatus = false;
            }
            makeContent();
        }
        return true;
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LogUtil.d(TAG, "v: " + v.toString());
            if(v.getId() == R.id.toolbar) {
                if(Util.onClickButton5Times()) {
                    isEditStatus = true;
                    makeContent();
                }
            }else if(v.getId() == R.id.item_add) {
                if(Util.isFastDoubleClick()) return;
                Intent intent = new Intent();
                intent.setClassName(AccountConstants.ACCOUNT_PACKAGE, AccountConstants.ACTIVITY_ACCOUNT_DIALOG);
                intent.putExtra(AccountConstants.DIALOG_TYPE, AccountConstants.DIALOG_TYPE_ADD_DEPOSIT_CATEGORY);
                mContext.startActivity(intent);
            }else if(v.getId() == R.id.deposit_delete) {
                if(Util.isFastDoubleClick()) return;
                int pos = (int) v.getTag();
                DepositItem item = depositItemArrayList.get(pos);
                LogUtil.d(TAG, "delete, name:, " + item.getName() + ", deposit: " + item.getDeposit());
                depositUtil.deleteDepositItem(item.getName());
                makeContent();
            }
        }
    };

    public static class DepositItem {
        private String name;
        private Float deposit;

        public DepositItem(String name, Float deposit) {
            this.name = name;
            this.deposit = deposit;
        }
        public String getName() {
            return name;
        }
        public double getDeposit() {
            return deposit;
        }
    }

    class DepositAdapter extends ArrayAdapter<DepositItem> {

        private int mLayoutId;

        public DepositAdapter(Context context, int resource, ArrayList<DepositItem> objects) {
            super(context, resource, objects);
            mLayoutId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final DepositItem item = getItem(position);
            ViewHolder viewholder;
            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(mLayoutId, parent,false);
                viewholder = new ViewHolder(convertView);
                convertView.setTag(viewholder);
            } else {
                viewholder = (ViewHolder) convertView.getTag();
            }

            if(item != null) {
                viewholder.depositName.setText(item.getName());
                viewholder.depositShow.setText(String.valueOf(item.getDeposit()));
                if(isEditStatus) {
                    viewholder.depositShow.setVisibility(View.GONE);
                    viewholder.depositBtnDe.setVisibility(View.VISIBLE);
                    viewholder.depositBtnDe.setTag(position);
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(Util.isFastDoubleClick()) return;
                            Intent intent = new Intent();
                            intent.setClassName(AccountConstants.ACCOUNT_PACKAGE, AccountConstants.ACTIVITY_ACCOUNT_DIALOG);
                            intent.putExtra(AccountConstants.DIALOG_TYPE, AccountConstants.DIALOG_TYPE_UPDATE_DEPOSIT_CATEGORY);
                            intent.putExtra(AccountConstants.DIALOG_HINT_ONE, item.getName());
                            intent.putExtra(AccountConstants.DIALOG_HINT_TWO, String.valueOf(item.getDeposit()));
                            mContext.startActivity(intent);
                        }
                    });
                }else {
                    viewholder.depositShow.setVisibility(View.VISIBLE);
                    viewholder.depositBtnDe.setVisibility(View.GONE);
                }
                if(!isVisibleStatus) {
                    viewholder.depositShow.setText("****");
                }
            }
            return (convertView);
        }

        class ViewHolder {
            TextView depositName;
            TextView depositShow;
            Button depositBtnDe;

            public ViewHolder(View view) {
                depositName = (TextView) view.findViewById(R.id.deposit_name);
                depositShow = (TextView) view.findViewById(R.id.deposit_show);
                depositBtnDe = (Button) view.findViewById(R.id.deposit_delete);
                depositBtnDe.setOnClickListener(mClickListener);
            }
        }
    }
}