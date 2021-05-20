package com.miniapp.account.extension;

import android.content.ContentValues;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.miniapp.account.LogUtil;
import com.miniapp.account.db.AccountItemDb;

public class ExtensionUtil {
    private static final String TAG = "ExtensionUtil";
    private static HashMap<String, Double> mUserCost = new HashMap<>();
    private static HashMap<String, Double> mDateCost = new HashMap<>();
    private static ArrayList<String> mDateList = new ArrayList<>();

    public static HashMap<String, Double> getUserCost() {
        return mUserCost;
    }
    public static ArrayList<String> getDateList() {
        return mDateList;
    }
    public static HashMap<String, Double> getDateCost() {
        return mDateCost;
    }

    public static boolean openFile(String filename) {
        boolean res = true;
        mUserCost = new HashMap<>();
        mDateCost = new HashMap<>();
        mDateList = new ArrayList<>();
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new FileInputStream(filename), null);
            int parserEvent = parser.getEventType();
            String tag = null;
            boolean item_tag = false;
            String value = null;
            HashMap<String, String> xmlKeyValue = new HashMap<>();
            LogUtil.e("start");
            while(parserEvent != XmlPullParser.END_DOCUMENT){
                switch(parserEvent){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        tag = parser.getName();

                        if(tag.equals("item")) {
                            item_tag = true;
                            xmlKeyValue.clear();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        if(item_tag) {
                            value = parser.getText();
                            if(!value.equals("\n")) {
                                if(!tag.equals("_id")) {
                                    xmlKeyValue.put(tag, value);
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tag = parser.getName();
                        if(tag.equals("item")) {
                            String username = xmlKeyValue.get(AccountItemDb.ACCOUNT_ITEM_USERNAME);
                            Double price = Double.valueOf(xmlKeyValue.get(AccountItemDb.ACCOUNT_ITEM_PRICE));
                            String date = xmlKeyValue.get(AccountItemDb.ACCOUNT_ITEM_DATE);
                            LogUtil.e(username + price + date);
                            mUserCost.put(username, price + (mUserCost.containsKey(username) ? mUserCost.get(username) : 0));
                            mDateCost.put(date, price + (mDateCost.containsKey(date) ? mDateCost.get(date) : 0));
                            if(!mDateList.contains(date)) mDateList.add(date);
                        }
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                }
                parserEvent = parser.next();

            }
            LogUtil.e("end");
        }catch(Exception e){
            res = false;
            LogUtil.e(TAG, "openFile " + e);
            e.printStackTrace();
        }
        return res;
    }
}
