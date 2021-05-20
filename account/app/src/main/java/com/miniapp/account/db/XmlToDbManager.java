package com.miniapp.account.db;

import android.content.ContentValues;
import android.content.Context;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import com.miniapp.account.LogUtil;
import com.miniapp.account.activity.AccountConstants;

/**
 * Created by zl on 20-12-3.
 */
public class XmlToDbManager {
    private static final String TAG = "AccountDbXmlToDbMgr";
    private Context mCtx = null;
    private ContentValues mCv = new ContentValues();
    private ArrayList<DbItem> mDbList;
    private String mRestoreFilePath;
    private AccountItemDb databaseHelper = null;

    public XmlToDbManager(Context context){
        mCtx = context;
        databaseHelper = AccountItemDb.getInstance(context);
        mDbList = new ArrayList<DbItem>();
    }

    public class DbItem {
        private String mColumn;
        private String mValue;

        public DbItem(String column, String value){
            mColumn = column;
            mValue = value;

        }
    }

    public int start(String path){
        mRestoreFilePath = path;
        int res = restore();
        deleteBackupFile();
        return res;
    }

    private int restore() {
        int importNum = 0;
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new FileInputStream(mRestoreFilePath), null);
            int parserEvent = parser.getEventType();
            String tag = null;

            boolean item_tag = false;

            String value = null;
            while(parserEvent != XmlPullParser.END_DOCUMENT){
                switch(parserEvent){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        tag = parser.getName();

                        if(tag.equals("item")) {
                            item_tag = true;
                            mDbList.clear();
                            mCv.clear();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if(item_tag) {
                            value = parser.getText();
                            if(!value.equals("\n")) {
                                if(!tag.equals("_id")) {
                                    DbItem item = new DbItem(tag, value);
                                    mDbList.add(item);
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tag = parser.getName();

                        if(tag.equals("item")) {
                            item_tag = false;
                            int size = mDbList.size();
                            for(int i=0; i<size; i++) {
                                DbItem item = mDbList.get(i);
                                if(item != null) {
                                    mCv.put(item.mColumn, item.mValue);
                                }
                            }
                            try {
                                if(mCv.size() > 0){
                                    databaseHelper.insert(mCv);
                                    ++importNum;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;

                }
                parserEvent = parser.next();

            }
        }catch(Exception e){
            LogUtil.e(TAG, "restore " + e);
            e.printStackTrace();
        }
        return importNum;
    }


    private void deleteBackupFile() {
        try {
            File file = new File(mRestoreFilePath);
            if(file.exists() && mRestoreFilePath.equals(AccountConstants.EXTERNAL_FILE_PATH)) {
                file.delete();
            }
        }catch(Exception e){
            LogUtil.e(TAG, "deleteBackupFile " + e);
            e.printStackTrace();
        }
    }
}
