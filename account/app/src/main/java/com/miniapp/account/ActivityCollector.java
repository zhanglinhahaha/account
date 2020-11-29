package com.miniapp.account;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zl on 20-11-27.
 */
public class ActivityCollector {

    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public static void finishAll() {
        for(Activity activity : activities) {
            LogUtil.d(activity.getLocalClassName());
            if(activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
