package com.app.util;

import android.app.Activity;
import android.app.Application;
import java.util.LinkedList;
import java.util.List;

public class MyApplication extends Application {
    private static MyApplication instance;
    public List<Activity> activityList = new LinkedList();

    private MyApplication() {
    }

    public static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        this.activityList.add(activity);
    }

    public void exit() {
        for (Activity activity : this.activityList) {
            activity.finish();
        }
        System.exit(0);
    }
}
