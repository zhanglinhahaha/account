package com.miniapp.account.activity;

import android.os.SystemClock;

/**
 * Created by zl on 20-12-11.
 */
public class Util {
    private static final int HALF_OF_ONE_S = 500;
    private static long lastClickTime = 0;
    /**
     * deny click button quickly
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < HALF_OF_ONE_S) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    private static final int HIT_COUNT = 5;
    private static final long DURATION = 1000;
    private static long[] mHits = new long[HIT_COUNT];
    public static boolean onClickButton5Times() {
        boolean res = false;
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (SystemClock.uptimeMillis() - mHits[0] <= DURATION) {
            mHits = new long[HIT_COUNT];
            res = true;
        }
        return  res;
    }
}
