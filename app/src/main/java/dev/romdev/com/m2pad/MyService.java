package dev.romdev.com.m2pad;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.content.ComponentName;
import com.huawei.android.app.admin.HwDevicePolicyManager;

/**
 * Created by cong on 10/23/17.
 */

public class MyService extends Service {

    HwDevicePolicyManager dpm;
    ComponentName componentName;
    @Override
    public void onCreate(){
        super.onCreate();

        componentName = new ComponentName(this, MyService.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        process(intent);
        return Service.START_REDELIVER_INTENT;
    }

    private void process(Intent intent){
        //pull USB status
        return;
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

}
