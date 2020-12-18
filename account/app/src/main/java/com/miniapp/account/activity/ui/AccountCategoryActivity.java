package com.miniapp.account.activity.ui;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import com.miniapp.account.LogUtil;
import com.miniapp.account.R;
import com.miniapp.account.activity.AccountCategoryAdapter;
import com.miniapp.account.activity.AccountConstants;
import com.miniapp.account.activity.CategoryUtil;
import com.miniapp.account.activity.Util;
import com.miniapp.account.service.AccountService;

public class AccountCategoryActivity extends BaseActivity {
    private static final String TAG = "AccountCategoryActivity";
    private RecyclerView mRecyclerView = null;
    private CategoryUtil categoryUtil = null;
    private AccountCategoryAdapter mAdapter = null;
    private GridLayoutManager mGridLayoutManager = null;
    private Context mContext = null;
    private String  categoryName = null;
    private CircleImageView circleImageView = null;
    private Toolbar mToolbar = null;

    private static boolean isDeleteStatus = false;
    private static final int CHOOSE_PHOTO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.v(TAG, "onCreate");
        setContentView(R.layout.activity_account_category);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        categoryUtil = CategoryUtil.getInstance(this);
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.v(TAG, "onResume");
        initOnResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.v(TAG, "onDestroy");
    }

    private void initOnResume() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) actionBar.setTitle(R.string.nav_menu_category);
        makeContent();
    }

    private void makeContent() {
        int spanCount = (isDeleteStatus) ? 1 : 2;
        mGridLayoutManager = new GridLayoutManager(mContext, spanCount);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new AccountCategoryAdapter(categoryUtil.getUserCateList(), this, mOnClickListener, isDeleteStatus);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.other_menu, menu);
        MenuItem manage_item = menu.findItem(R.id.manage);
        manage_item.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LogUtil.d(TAG, "onOptionsItemSelected called: " + item.toString());
        if(item.getItemId() == R.id.manage) {
            isDeleteStatus = !isDeleteStatus;
            makeContent();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(isDeleteStatus) {
            isDeleteStatus = false;
            makeContent();
        }else{
            super.onBackPressed();
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(Util.isFastDoubleClick()) return;
            int mChooseId = Integer.parseInt(v.getTag().toString());
            categoryName = categoryUtil.getUserCateList().get(mChooseId).getName();
            LogUtil.d(TAG, "onClick: " + mChooseId + ", categoryName: " + categoryName + ", isDeleteStatus" + isDeleteStatus);
            if(isDeleteStatus) {
                if(v.getId() == R.id.category_delete) {
                    if(AccountService.getService(mContext).getUserNameList().contains(categoryName)) {
                        Toast.makeText(mContext, R.string.toast_delete_db_first, Toast.LENGTH_SHORT).show();
                    }else {
                        categoryUtil.deleteUserCate(categoryName);
                        makeContent();
                    }
                }else if(v.getId() == R.id.category_image) {
                    openAlbum();
                    circleImageView = (CircleImageView) v.findViewById(R.id.category_image);
                }
            }else {
                Intent intent1 = new Intent();
                if(categoryName.equals(AccountConstants.ADD_CATEGORY_STRING)) {
                    intent1.setClassName(AccountConstants.ACCOUNT_PACKAGE, AccountConstants.ACTIVITY_ACCOUNT_DIALOG);
                    intent1.putExtra(AccountConstants.DIALOG_TYPE, AccountConstants.DIALOG_TYPE_ADD_CATEGORY);
                }else {
                    intent1.setClassName(AccountConstants.ACCOUNT_PACKAGE, AccountConstants.ACTIVITY_ACCOUNT_FILTRATE);
                    intent1.putExtra(AccountConstants.QUERY_CATEGORY, categoryName);
                }
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent1);
            }
        }
    };


    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null ) {
            if(requestCode == CHOOSE_PHOTO) {
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
            }
        }else {
            LogUtil.e(TAG, "openAlbum data is null");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleImageOnKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = null;
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                if (docId.startsWith("raw:")) {
                    imagePath = docId.replaceFirst("raw:", "");
                }else {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                            "//downloads/public_downloads"), Long.parseLong(docId));
                    imagePath = getImagePath(contentUri, null);
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        }
        displayImage(imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri externalContentUri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(externalContentUri,
                null, selection, null, null);
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(
                        MediaStore.Images.Media.DATA));
                LogUtil.d(TAG, "getImagePath: "+path);
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath){
        LogUtil.d(TAG, imagePath);
        if(imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            circleImageView.setImageBitmap(bitmap);
            categoryUtil.addUserCate(categoryName, imagePath);
        }else{
            Toast.makeText(this,R.string.toast_failed_get_image,Toast.LENGTH_SHORT).show();
        }
    }
}