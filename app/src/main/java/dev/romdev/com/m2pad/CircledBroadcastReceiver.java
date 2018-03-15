package dev.romdev.com.m2pad;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import dev.romdev.com.m2pad.net.EventBusMsg;
import dev.romdev.com.m2pad.net.InstallApkQuietly;
import dev.romdev.com.m2pad.utils.LogUtils;
import static dev.romdev.com.m2pad.MainActivity.ACTION_CHECK_TIME;
import static dev.romdev.com.m2pad.MainActivity.ACTION_FIRST_TIME;
import static dev.romdev.com.m2pad.MainActivity.API_POST_VALUE;
import static dev.romdev.com.m2pad.MainActivity.appInstallWhiteList;
import static dev.romdev.com.m2pad.MainActivity.componentName;
import static dev.romdev.com.m2pad.MainActivity.dpm;


//import static dev.romdev.com.m2pad.MainActivity.ACTION_ALIVE;

/**
 * Created by cong on 10/30/17.
 */

public class CircledBroadcastReceiver extends BroadcastReceiver {

    //    private static ArrayList<String> allowedTime = new ArrayList<>();
    private ArrayList<String> allowedTime = new ArrayList<>();

    private ArrayList<String> allowedIP = new ArrayList<>();

    private ArrayList<String> allowedApp = new ArrayList<>();

    private ArrayList<String> uninstallApp = new ArrayList<>();

    private static boolean isControlTime = true;

    @Override
    public void onReceive(final Context context, Intent intent) {

        final Context context1 = context;
        if (API_POST_VALUE == null) {
            return;
        }

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {//开机完成
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            circledAction(context1);
            return;
        }
        if (intent.getAction().equals(ACTION_FIRST_TIME)) {
            MainActivity.close();
            circledAction(context1);
            return;
        }


        if (intent.getAction().equals(ACTION_CHECK_TIME)
                || intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    noPasswordInstall(context1);//免密安装包
//                    checkBrowserAPITime(context1); //获取APP上网时间

//                    if(isControlTime){
//                        checkBrowserAPIWebIP(context1);//指定 ip 上网
//                        checkAccessNetApp(context1);//指定app上网
//                    }
                    checkBrowserAPIWebIP(context1);//指定 ip 上网
                    checkAccessNetApp(context1);//指定app上网
                    checkUSBInstall(context1);//获取USB状态
                    checkHomeButton(context1);//获取HOME键状态
                    updatePassword(context1);//获取后台设置的密码
                    checkAppUpdata(context1);//检测app更新
                    checkSafeModeAuthority(context1);//检查安全模式权限
                    checkBludtoolAuthority(context1);//蓝牙
                    checkSDCardAuthority(context1);//SD 卡
                    checkUninstallApp(context1);//禁止卸载某一款app
                }
            }).start();
            circledAction(context1);
        }
    }

    /**
     * 系统闹钟，15分钟一次
     *
     * @param context
     */
    private void circledAction(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.get(Calendar.HOUR_OF_DAY);
        calendar.add(Calendar.MINUTE, +15);
        calendar.get(Calendar.SECOND);
        Intent intent = new Intent(ACTION_CHECK_TIME);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0x16, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);//系统闹钟，15分钟一次


    }


    /**
     * 检测app更新
     *
     * @param context
     */
    private void checkAppUpdata(Context context) {
        Toast.makeText(context, "检查版本号", Toast.LENGTH_LONG).show();
        //检查版本号
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        String oldVersion = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            oldVersion = pi.versionName.replace(".", "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        if (StringUtil.isEmpty(oldVersion) || !StringUtil.strIsNum(oldVersion)) {
//            return;
//        }
        JSONObject object = new JSONObject();
        try {
            object.put("interUser", "control_app");
            object.put("interPwd", "E10ADC3949BA59ABBE56E057F20F883E");
            object.put("moduleType", "T03");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String API_POST_VALUE = object.toString();
        APICallUtil apiCallUtil = new APICallUtil(ConfigParam.BASE_API_Updata, ConfigParam.WEB_UPDATA_APP, API_POST_VALUE);
        try {
            String reVaule = apiCallUtil.call();
            UpdateAPPResponse updataFromJson = new Gson().fromJson(reVaule, UpdateAPPResponse.class);

            EventBusMsg msg = new EventBusMsg(ConfigParam.DOWNLOADAPK, updataFromJson);//发送的消息内容

            final String finalOldVersion = oldVersion.replace(".", "");
            Toast.makeText(context, finalOldVersion, Toast.LENGTH_LONG).show();
            UpdateAPPResponse updata = (UpdateAPPResponse) msg.object;

            if (StringUtil.isNotEmpty(updata.data.get(0).versionRecord.versionCode)) {//版本号
                String forcedUpgrade = updata.data.get(0).versionRecord.downloadUrl;
                String currentVersion = updata.data.get(0).versionRecord.versionCode.replace(".", "");
                if (StringUtil.isNotEmpty(currentVersion) && StringUtil.strIsNum(currentVersion)) {
                    //需要升级
                    if (Integer.parseInt(currentVersion) > Integer.parseInt(finalOldVersion)) {
                        //不强制升级
                        if (StringUtil.isNotEmpty(updata.data.get(0).versionRecord.forcedUpgrade)
                                && "I02".equals(updata.data.get(0).versionRecord.forcedUpgrade)) {

                        }
                        //强制升级
                        if (StringUtil.isNotEmpty(updata.data.get(0).versionRecord.forcedUpgrade)
                                && "I01".equals(updata.data.get(0).versionRecord.forcedUpgrade)) {
                            InstallApkQuietly.packageAppName = "dev.romdev.com.m2pad";
                            InstallApkQuietly.url = forcedUpgrade;
                            InstallApkQuietly.mContext = context;
                            InstallApkQuietly.init();
                            Toast.makeText(context,"下载",Toast.LENGTH_LONG).show();

                        }
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 免密安装包
     *
     * @param context
     */
    private void noPasswordInstall(Context context) {

        Looper.prepare();
        APICallUtil apiCallUtil = new APICallUtil(ConfigParam.BASE_API, ConfigParam.NO_PASSWORD_INSTLAL, API_POST_VALUE);
//        ArrayList<String> lists = (ArrayList<String>)dpm.getIPWhiteList(componentName);
        JSONObject object1 = null;
        try {
            String retVaule = apiCallUtil.call();

            object1 = new JSONObject(retVaule);
            boolean isSuccess = object1.getBoolean("flag");

            if (!isSuccess) {
                Log.e("cong", "no password install wrong, return now");
                Toast.makeText(context, "免密安装包访问失败", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONArray jsonArray = object1.getJSONArray("policyValueList");

            if ((jsonArray != null) && (jsonArray.length() > 0)) {//解析 json 数组，把免密码安装的app包名遍历然后放到appInstallWhiteList集合中
                for (int i = 0; i < jsonArray.length(); i++) {
                    String local = (String) jsonArray.get(i);

                    String apps[] = local.split("white:");
                    //{"policyValueList":["white:17:30-19:30","white:12:30-14:00"],"flag":true,"code":null,"msg":null}
                    // "policyValueList":["white:com.app.gaofenyun","white:com.app.quanlang"]

                    String val = apps[1];

                    if (!appInstallWhiteList.contains(val)) {
                        appInstallWhiteList.add(val);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "免密安装app初始化失败", Toast.LENGTH_SHORT).show();
        }

        dpm.addInstallPackages(componentName, appInstallWhiteList);
        LogUtils.file("免密安装白名单后台接口"+appInstallWhiteList);


    }

    /**
     * 获取后台设置的密码
     *
     * @param context
     */
    private void updatePassword(Context context) {

        try {
            APICallUtil apiCallUtil = new APICallUtil(ConfigParam.BASE_API, ConfigParam.PASSWORD_ACCESS, API_POST_VALUE);

            String retVaule = apiCallUtil.call();

            JSONObject object1 = new JSONObject(retVaule);
            boolean isSuccess = object1.getBoolean("flag");

            if (isSuccess) {
                String val = object1.getString("controlPassword");

                if (val != null) {
                    SharedPreferences sp =
                            context.getSharedPreferences(ConfigParam.SP_NAME, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(ConfigParam.SP_NAME_KEY_PASSWORD, val);//获取后台设置的密码
                    editor.commit();
                }
                LogUtils.file("后台更新密码"+val);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "更新密码失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取HOME键状态
     *
     * @param content
     */
    private void checkHomeButton(Context content) {
        APICallUtil apiCallUtil = new APICallUtil(ConfigParam.BASE_API, ConfigParam.HOME_BUTTON_CHECK, API_POST_VALUE);

        JSONObject object1 = null;
        try {

            String retVaule = apiCallUtil.call();

            object1 = new JSONObject(retVaule);
            boolean isSuccess = object1.getBoolean("flag");

            if (!isSuccess) {
                Log.e("cong", "usb installation api access, return now");
                Toast.makeText(content, "获取Home键状态失败", Toast.LENGTH_SHORT).show();
                return;
            }

            String ret = object1.getString("disableFlag");
            if (ret.equals("disable")) {
                dpm.setHomeButtonDisabled(componentName, true);
                boolean home= dpm.isHomeButtonDisabled(componentName);
                LogUtils.file("查询home禁用状态后台接口"+home);
                return;
            }
            if (ret.equals("enable")) {
                dpm.setHomeButtonDisabled(componentName, false);
                boolean home= dpm.isHomeButtonDisabled(componentName);
                LogUtils.file("查询home禁用状态后台接口"+home);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(content, "获取Home键状态失败", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 获取USB状态
     *
     * @param content
     */
    private void checkUSBInstall(Context content) {
        APICallUtil apiCallUtil = new APICallUtil(ConfigParam.BASE_API, ConfigParam.ACTION_USB_INSTALL_APP, API_POST_VALUE);

        JSONObject object1 = null;
        try {

            String retVaule = apiCallUtil.call();

            object1 = new JSONObject(retVaule);
            boolean isSuccess = object1.getBoolean("flag");

            if (!isSuccess) {
                Log.e("cong", "usb installation api access, return now");
                Toast.makeText(content, "获取USB状态失败", Toast.LENGTH_SHORT).show();

                return;
            }

            String ret = object1.getString("disableFlag");
            if (ret.equals("disable")) {
                dpm.setUSBDataDisabled(componentName, true);
                boolean usb = dpm.isUSBDataDisabled(componentName);
                LogUtils.file("获取USB状态后台接口"+usb);
                return;
            }
            if (ret.equals("enable")) {
                dpm.setUSBDataDisabled(componentName, false);
                boolean usb = dpm.isUSBDataDisabled(componentName);
                LogUtils.file("获取USB状态后台接口"+usb);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(content, "获取USB状态失败", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 获取IP白名单
     *
     * @param content
     */
    private void checkBrowserAPIWebIP(Context content) {

        APICallUtil apiCallUtil = new APICallUtil(ConfigParam.BASE_API, ConfigParam.WEB_ACCESS_IP_CONTROL, API_POST_VALUE);

        JSONObject object1 = null;
        try {
            String retVaule = apiCallUtil.call();

            object1 = new JSONObject(retVaule);
            boolean isSuccess = object1.getBoolean("flag");

            if (!isSuccess) {
                Log.e("cong", "web acces time IP wrong, return now");
                Toast.makeText(content, "获取IP白名单失败", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONArray jsonArray = object1.getJSONArray("policyValueList");
//            if (jsonArray != null && jsonArray.length() > 0){
//                allowedIP.clear();
//            }

            if ((jsonArray != null) && (jsonArray.length() > 0)) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String local = (String) jsonArray.get(i);

                    String apps[] = local.split("white:");
                    //{"policyValueList":["white:17:30-19:30","white:12:30-14:00"],"flag":true,"code":null,"msg":null}

//                    if (apps.length < 2) {
//                        continue;
//                    }

                    String val = apps[1].trim();
                    if (!allowedIP.contains(val))//加入判断，过滤重复2018.1.2
                        allowedIP.add(val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(content, "网络白名单获取失败", Toast.LENGTH_SHORT).show();
            LogUtils.file("错误日志" + e.toString());
        }

        //hard code api debug ip
        if (!allowedIP.contains("119.23.60.223"))
            allowedIP.add("119.23.60.223");

        if (!allowedIP.contains("119.23.113.71"))//gaofenyun.com
            allowedIP.add("119.23.113.71");

        //内网IP标示：
        // 172.16.x.x至172.31.x.x 　　
        // 192.168.x.x
        // 10.x.x.x
        if (!allowedIP.contains("192.0.0.0/8"))
            allowedIP.add("192.0.0.0/8");

        if (!allowedIP.contains("10.0.0.0/8"))
            allowedIP.add("10.0.0.0/8");

        if (!allowedIP.contains("42.156.141.13"))
            allowedIP.add("42.156.141.13");//www.gaofenyun.com

        if (!allowedIP.contains("120.77.167.192"))
            allowedIP.add("120.77.167.192");//pubquanlang.oss-cn-shenzhen.aliyuncs.com

        if (!allowedIP.contains("120.77.166.123"))
            allowedIP.add("120.77.166.123");//quanlang.oss-cn-shenzhen.aliyuncs.com


        dpm.setIPWhiteList(componentName, allowedIP);
//        ArrayList<String> lists = (ArrayList<String>)dpm.getIPWhiteList(componentName);

        List<String> ipWhiteList = dpm.getIPWhiteList(componentName);
        String s = String.valueOf(ipWhiteList);
        LogUtils.file("广播白名单平板接口：", s + "\r\n");
        LogUtils.file("白名单后台策略：", String.valueOf(allowedIP) + "\r\n");
    }

    /**
     * 获取APP上网时间
     *
     * @param content
     */
    private void checkBrowserAPITime(Context content) {

        APICallUtil apiCallUtil = new APICallUtil(ConfigParam.BASE_API, ConfigParam.WEB_ACCESS_TIME, API_POST_VALUE);

        JSONObject object1 = null;
        try {
            String retVaule = apiCallUtil.call();

            object1 = new JSONObject(retVaule);
            boolean isSuccess = object1.getBoolean("flag");

            if (!isSuccess) {
                Log.e("cong", "web acces time API wrong, return now");
                Toast.makeText(content, "获取上网时间失败", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONArray jsonArray = object1.getJSONArray("policyValueList");

            if ((jsonArray != null) && (jsonArray.length() > 0)) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String local = (String) jsonArray.get(i);
                    String apps[] = local.split("white:");
                    //{"policyValueList":["white:17:30-19:30","white:12:30-14:00"],"flag":true,"code":null,"msg":null}
                    String val = apps[1];
                    allowedTime.add(val);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(content, "访问网络超时", Toast.LENGTH_SHORT).show();
        }

        int length = allowedTime.size();
        for (int i = 0; i < length; i++) {
            String time = allowedTime.get(i);

            String[] allowd = time.split("-");

            String start = allowd[0];
            String end = allowd[1];

            String s[] = start.split(":");
            String e[] = end.split(":");

            Log.e("browserTextS", Integer.valueOf(s[0]).toString() + ":" + Integer.valueOf(s[1]).toString());
            Log.e("browserTextE", Integer.valueOf(e[0]).toString() + ":" + Integer.valueOf(e[1]).toString());


            Date dateStart = new Date();
            dateStart.setHours(Integer.valueOf(s[0]));
            dateStart.setMinutes(Integer.valueOf(s[1]));

            Date dateEnd = new Date();
            dateEnd.setHours(Integer.valueOf(e[0]));
            dateEnd.setMinutes(Integer.valueOf(e[1]));

            Date now = new Date();
            LogUtils.file("browserText起始", dateStart.getHours() + ":" + dateStart.getMinutes());
            LogUtils.file("browserText当前", now.getHours() + ":" + now.getMinutes());
            LogUtils.file("browserText结束", dateEnd.getHours() + ":" + dateEnd.getMinutes());


            if (now.getHours() < dateStart.getHours() || now.getHours() > dateEnd.getHours()) {
                isControlTime = true;
                Log.e("cong", "disable browserText");
                return;
            }
            if (now.getHours() == dateStart.getHours()) {
                if (now.getMinutes() < dateStart.getMinutes()) {
                    isControlTime = true;
                    Log.e("cong", "disable browserText");
                    return;
                }
            }
            if (now.getHours() == dateEnd.getHours()) {
                if (now.getMinutes() > dateEnd.getMinutes()) {
                    isControlTime = true;
                    Log.e("cong", "disable browserText");
                    return;
                }
            }
            isControlTime = false;
            dpm.setIPWhiteList(componentName, null);
            dpm.cleanNetworkAccessAppWhiteList(componentName);
            Log.e("cong", "enable browserText");

        }
    }

    /**
     * 检查SD卡权限
     *
     * @param context
     */
    private void checkSDCardAuthority(Context context) {
        APICallUtil apiCallUtil = new APICallUtil(ConfigParam.BASE_API, ConfigParam.SDCARD_CHECK, API_POST_VALUE);

        JSONObject object1 = null;
        try {

            String retVaule = apiCallUtil.call();

            object1 = new JSONObject(retVaule);
            boolean isSuccess = object1.getBoolean("flag");

            if (!isSuccess) {
                Log.e("cong", "SD installation api access, return now");
                Toast.makeText(context, "获取SD状态失败", Toast.LENGTH_SHORT).show();

                return;
            }

            String ret = object1.getString("disableFlag");
            Log.e("SDCardText", ret);
            if (ret.equals("disable")) {     //禁用 SD 卡
                dpm.setExternalStorageDisabled(componentName, true);
                boolean sd = dpm.isExternalStorageDisabled(componentName);
                LogUtils.file("查询sd卡禁用状态后台接口"+sd);
                return;
            }
            if (ret.equals("enable")) {      //启用 SD 卡
                dpm.setExternalStorageDisabled(componentName, false);
                boolean sd = dpm.isExternalStorageDisabled(componentName);
                LogUtils.file("查询sd卡禁用状态后台接口"+sd);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "获取SD状态失败", Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * 检查蓝牙权限
     *
     * @param context
     */
    private void checkBludtoolAuthority(Context context) {
        APICallUtil apiCallUtil = new APICallUtil(ConfigParam.BASE_API, ConfigParam.BLUETOOTH_CHECK, API_POST_VALUE);
        String string = API_POST_VALUE;
        JSONObject object1 = null;
        try {

            String retVaule = apiCallUtil.call();

            object1 = new JSONObject(retVaule);
            boolean isSuccess = object1.getBoolean("flag");

            if (!isSuccess) {
                Log.e("cong", "bluetooth installation api access, return now");


                return;
            }

            String ret = object1.getString("disableFlag");
            Log.e("bluetoothText", ret);
            if (ret.equals("disable")) {     //禁用蓝牙
                dpm.setBluetoothDisabled(componentName, true);
                boolean blue = dpm.isBluetoothDisabled(componentName);
                LogUtils.file("查询是否禁用蓝牙功能后台接口"+blue);
                return;
            }
            if (ret.equals("enable")) {      //启用蓝牙
                dpm.setBluetoothDisabled(componentName, false);
                boolean blue = dpm.isBluetoothDisabled(componentName);
                LogUtils.file("查询是否禁用蓝牙功能后台接口"+blue);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "获取蓝牙状态失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检查安全模式权限
     *
     * @param context
     */
    private void checkSafeModeAuthority(Context context) {
        APICallUtil apiCallUtil = new APICallUtil(ConfigParam.BASE_API, ConfigParam.SAFEMODE_CHECK, API_POST_VALUE);

        JSONObject object1 = null;
        try {

            String retVaule = apiCallUtil.call();

            object1 = new JSONObject(retVaule);
            boolean isSuccess = object1.getBoolean("flag");

            if (!isSuccess) {
                Log.e("cong", "safe mode installation api access, return now");
                Toast.makeText(context, "获取安全模式状态失败", Toast.LENGTH_SHORT).show();

                return;
            }

            String ret = object1.getString("disableFlag");
            Log.e("SafeModeText", ret);
            if (ret.equals("disable")) {     //禁用安全模式
                dpm.setSafeModeDisabled(componentName, true);
                boolean safe = dpm.isSafeModeDisabled(componentName);
                LogUtils.file("查询安全模式禁用状态后台接口"+safe);
                return;
            }
            if (ret.equals("enable")) {      //启用安全模式
                dpm.setSafeModeDisabled(componentName, false);
                boolean safe = dpm.isSafeModeDisabled(componentName);
                LogUtils.file("查询安全模式禁用状态后台接口"+safe);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "获取安全模式状态失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 指定 app 上网
     *
     * @param content
     */
    private void checkAccessNetApp(Context content) {

        APICallUtil apiCallUtil = new APICallUtil(ConfigParam.BASE_API, ConfigParam.NETWORK_ACCESS_APP, API_POST_VALUE);

        JSONObject object1 = null;
        try {
            String retVaule = apiCallUtil.call();

            object1 = new JSONObject(retVaule);
            boolean isSuccess = object1.getBoolean("flag");

            if (!isSuccess) {
                Log.e("cong", "web acces network App wrong, return now");
                Toast.makeText(content, "获取指定 app 上网失败", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONArray jsonArray = object1.getJSONArray("policyValueList");

            if ((jsonArray != null) && (jsonArray.length() > 0)) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String local = (String) jsonArray.get(i);

                    String apps[] = local.split("white:");
                    //{"policyValueList":["white:17:30-19:30","white:12:30-14:00"],"flag":true,"code":null,"msg":null}
                    String val = apps[1];

                    Log.e("AccessNetAppText", val);
                    if (!allowedApp.contains(val))
                        allowedApp.add(val);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(content, "指定 app 上网获取失败", Toast.LENGTH_SHORT).show();
        }

        dpm.addNetworkAccessAppWhiteList(componentName, allowedApp);
        List<String> networkAccessAppWhiteList = dpm.getNetworkAccessAppWhiteList(componentName);
        String appWhiteList = String.valueOf(networkAccessAppWhiteList);
        LogUtils.file("指定app上网后台接口"+appWhiteList);
        LogUtils.file("指定app上网后台策略"+String.valueOf(allowedApp));
    }

    /**
     * 禁止卸载某一款 app
     *
     * @param content
     */
    private void checkUninstallApp(Context content) {

        APICallUtil apiCallUtil = new APICallUtil(ConfigParam.BASE_API, ConfigParam.UNINSTALL_APP, API_POST_VALUE);

        JSONObject object1 = null;
        try {
            String retVaule = apiCallUtil.call();

            object1 = new JSONObject(retVaule);
            boolean isSuccess = object1.getBoolean("flag");

            if (!isSuccess) {
                Log.e("cong", "web acces uninstall App wrong, return now");
                Toast.makeText(content, "获取禁止卸载的 app 失败", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONArray jsonArray = object1.getJSONArray("policyValueList");

            if ((jsonArray != null) && (jsonArray.length() > 0)) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String local = (String) jsonArray.get(i);

                    String apps[] = local.split("white:");
                    //{"policyValueList":["white:17:30-19:30","white:12:30-14:00"],"flag":true,"code":null,"msg":null}
                    String val = apps[1];
                    Log.e("UninstallAppText", val);
                    if (!uninstallApp.contains(val))
                        uninstallApp.add(val);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(content, "获取禁止卸载的 app 失败", Toast.LENGTH_SHORT).show();
        }
        dpm.setDisallowedUninstallPackages(componentName, uninstallApp);
        LogUtils.file("查询禁止卸载app名单后台接口"+uninstallApp);
    }

}
