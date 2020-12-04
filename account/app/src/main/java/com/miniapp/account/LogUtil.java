package com.miniapp.account;

import android.os.Environment;
import android.util.Log;

import com.miniapp.account.activity.AccountConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zl on 19-10-25.
 * 打印日志工具
 * 当level=VERBOSE，日志全部打印
 * 当level=NOTHING，日志全部不打印
 */
public class LogUtil {
    private static final String TAG = "zl_Account_LogUtil";

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;

    private static SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");
    private static String MYLOGFILEName = "log.txt";

    private static int mLevel = VERBOSE;

    public static void setLogLevel(int level) {
        mLevel = level;
    }

    public static void v(String msg) {
        v(TAG, msg);
    }
    public static void v(String tag, String msg) {
        if (mLevel <= VERBOSE) {
            writeLogToFile("VERBOSE", tag, msg);
            Log.v(tag, msg);
        }
    }

    public static void d(String msg) {
        d(TAG, msg);
    }
    public static void d(String tag, String msg) {
        if(mLevel <= DEBUG) {
            writeLogToFile("DEBUG", tag, msg);
            Log.d(tag, msg);
        }
    }

    public static void i(String msg) {
        i(TAG, msg);
    }
    public static void i(String tag, String msg) {
        if(mLevel <= INFO) {
            writeLogToFile("INFO", tag, msg);
            Log.i(tag, msg);
        }
    }

    public static void w(String msg) {
        w(TAG, msg);
    }
    public static void w(String tag, String msg) {
        writeLogToFile("WARNING", tag, msg);
        if(mLevel <= WARN) {
            Log.w(tag, msg);
        }
    }

    public static void e(String msg) {
        e(TAG, msg);
    }
    public static void e(String tag, String msg) {
        writeLogToFile("ERROR", tag, msg);
        if(mLevel <= ERROR) {
            Log.e(tag, msg);
        }
    }

    private static void writeLogToFile(String mylogtype, String tag, String text) {
        Date nowTime = new Date();
        String needWriteFile = logfile.format(nowTime);
        String needWriteMessage = myLogSdf.format(nowTime) + "    " + mylogtype + "    " + tag + "    " + text;
        File dirsFile = new File(AccountConstants.ACCOUNT_DIR_PATH);
        if (!dirsFile.exists()){
            dirsFile.mkdirs();
        }
        File file = new File(dirsFile.toString(), needWriteFile + MYLOGFILEName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
            }
        }

        try {
            FileWriter filerWriter = new FileWriter(file, true);
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delFile() {
        String needDelFile= logfile.format(getDateBefore());
        File file = new File(AccountConstants.ACCOUNT_DIR_PATH, needDelFile + MYLOGFILEName);
        if (file.exists()) {
            file.delete();
        }
    }

    private static Date getDateBefore() {
        Date nowtTime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowtTime);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - AccountConstants.SDCARD_LOG_FILE_SAVE_DAYS);
        return now.getTime();
    }
}