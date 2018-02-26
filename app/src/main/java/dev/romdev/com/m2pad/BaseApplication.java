package dev.romdev.com.m2pad;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

import dev.romdev.com.m2pad.utils.Utils;

/**
 * Created by LCL on 2018/1/24.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        CrashReport.initCrashReport(getApplicationContext(), "da48344edf", false);
    }
}
