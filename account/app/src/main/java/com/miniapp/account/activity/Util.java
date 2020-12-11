package com.miniapp.account.activity;

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
}
