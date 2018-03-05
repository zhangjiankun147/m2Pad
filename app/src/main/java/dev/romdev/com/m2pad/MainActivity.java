package dev.romdev.com.m2pad;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.huawei.android.app.admin.HwDevicePolicyManager;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import dev.romdev.com.m2pad.net.EventBusMsg;
import dev.romdev.com.m2pad.net.InstallApkQuietly;
import dev.romdev.com.m2pad.utils.LogUtils;
import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private EditText passWord;
    private Context context;
    public static HwDevicePolicyManager dpm;
    public static ComponentName componentName;
    private EditText userName;
    private LinearLayout mPassCom;
    private Button mOkButton;
    private LinearLayout mControlPanel;
    private RelativeLayout mRlControl;


    public final String packageName = "dev.romdev.com.m2pad";//dev.romdev.com.m2pad

    public final static String ACTION_CHECK_TIME = "com.romdev.ACTION_CHECK_TIME";

    public final static String ACTION_FIRST_TIME = "com.romdev.ACTION_FIRST_TIME";

    public final String BROWER_PACKAGE_NAME = "com.android.chrome";

    public final static String HW_BROWSER_PACKAGE = "com.android.browser";


    public ArrayList<String> timeSlot = new ArrayList();

    public static ArrayList<String> appInstallWhiteList = new ArrayList();

    public static String API_POST_VALUE = null;



    public static Switch usbSwitch;
    public static Switch resetSysSwitch;
    public static Switch installAppSwitch;
    public static LinearLayout controlPart;
//    private Button mClean;
//    private Button mUp;

    private final int OPEN_OPT_WINDOW_SLOT = 1000 * 60 * 5; // minutes operation time开放时间
    private TextView mTvVersion;
    private SpotsDialog dialog;
    private AlarmManager alarmManager;
    private PendingIntent pi;
    public static Switch browserUse;
    public static Switch sdka;
    public static Switch blue;
    private ArrayList<String> ar;
    //    private CheckUpdata mCheckUpdata;

    private void timeControl() {
//        Timer t = new Timer();
//        t.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Log.e("cong---", "timer task closed-------");
//                handler.sendEmptyMessage(0x7);
//            }
//        }, OPEN_OPT_WINDOW_SLOT);
//        Intent intent = new Intent(ACTION_FIRST_TIME);
//        sendBroadcast(intent);
//        Intent intent = new Intent(ACTION_CHECK_TIME);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.get(Calendar.HOUR_OF_DAY);
        calendar.add(Calendar.MINUTE,+5);
        calendar.get(Calendar.SECOND);
        Intent intent = new Intent(this,CircledBroadcastReceiver.class);
        intent.setAction(ACTION_FIRST_TIME);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        pi = PendingIntent.getBroadcast(context, 110, intent,PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() ,pi);//系统闹钟，5分钟执行一次

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            int what = message.what;

            switch (what) {
                case 0x1:     //fail verification
                    dialog.dismiss();
                    Toast.makeText(context, "验证失败", Toast.LENGTH_LONG).show();
                    onClickClose(null);
                    break;

                case 0x7:
                    onClickClose(null);
                    break;
                case 0x8:   //pass verification
                    dialog.dismiss();
                    Toast.makeText(context, "验证成功,系统管控功能激活", Toast.LENGTH_LONG).show();
                    enableControl();
                    timeControl();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //文本日志路径设置
        String logDir = Environment.getExternalStorageDirectory().
                getAbsolutePath() + "/" + "全朗高分云/" + "/" + "Log" + "/";
        LogUtils.getConfig().setDir(logDir);

//        EventBus.getDefault().register(this);//注册eventbus
        checkMyPermission();//检查更新权限


        initView();

        dpm = new HwDevicePolicyManager(this);
        componentName = new ComponentName(this, MainActivity.class);

        initData();


    }

    private void initData() {

         ar = new ArrayList<>();
        ArrayList<String> forbidkill = new ArrayList<>();
        ArrayList<String> accessibility = new ArrayList<>();
        forbidkill.add(packageName);

        try {
            ar.add(packageName);
            ar.add("com.yqh.education");//高分云app
            ar.add("com.android.application");
            ar.add(BROWER_PACKAGE_NAME);
            accessibility.add(packageName);

            dpm.setDisallowedUninstallPackages(componentName, ar);//不能卸载管控app

            dpm.setFactoryResetDisabled(componentName, true);//不能恢复出厂设置

            dpm.setTimeModifyDisabled(componentName, true);//不允许修改系统时间

            dpm.setHomeButtonDisabled(componentName, true); //禁止HOME键

            dpm.addPersistentApp(componentName, ar);//设置在后台运行不退出

//            dpm.setApplicationDisabled(componentName, HW_BROWSER_PACKAGE, false);//让华为浏览器显示

            dpm.setApplicationDisabled(componentName,BROWER_PACKAGE_NAME,false);//让谷歌浏览器显示

            dpm.setForbidKillAppWhitelist(componentName, forbidkill); // 禁止指定的 app 被杀死

            dpm.setSafeModeDisabled(componentName,true);    //禁止安全模式

            dpm.setBluetoothDisabled(componentName, true);   //禁止蓝牙

            dpm.setExternalStorageDisabled(componentName, true);     //禁止 SD 卡

            dpm.setAccessibilityWhiteList(componentName, accessibility);//设置默认开启辅助功能应用

            dpm.setIPWhiteList(componentName, null);//浏览器上网
            dpm.cleanNetworkAccessAppWhiteList(componentName);

            setAppWhiteList();//设置app的白名单

            JSONObject object = new JSONObject();
            object.put("interUser", ConfigParam.API_USER_NAME);
            object.put("interPwd", ConfigParam.API_PASSWORD_ENCRYPT);

            API_POST_VALUE = object.toString();


            SharedPreferences sp =
                    this.getSharedPreferences(ConfigParam.SP_NAME, Context.MODE_PRIVATE);

            String pwd = sp.getString(ConfigParam.SP_NAME_KEY_PASSWORD, "0");

            if (pwd.equals("0")) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("password", ConfigParam.LOGIN_PASSWORD);
                editor.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "初始化失败", Toast.LENGTH_SHORT).show();
        }
        String version = checkVersion();
        mTvVersion.setText("V " + version);


//        mClean.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dpm.setIPWhiteList(componentName, null);
//            }
//        });
//        mUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                doServerUpdate();
//            }
//        });

    }

    private void initView() {
        passWord = (EditText) findViewById(R.id.passWord);

        userName = (EditText) findViewById(R.id.userName);

        resetSysSwitch = (Switch) findViewById(R.id.systemReset);

        usbSwitch = (Switch) findViewById(R.id.usb);

        installAppSwitch = (Switch) findViewById(R.id.installAPP);


        controlPart = (LinearLayout) findViewById(R.id.controlPanel);

        mTvVersion = (TextView) findViewById(R.id.version);//版本号

        mPassCom = (LinearLayout) findViewById(R.id.passCom);//输入框

        mOkButton = (Button) findViewById(R.id.okButton);//ok按钮

        mControlPanel = (LinearLayout) findViewById(R.id.controlPanel);

        mRlControl = (RelativeLayout) findViewById(R.id.rl_control);

        browserUse = (Switch)findViewById(R.id.browserUse);
        sdka = (Switch)findViewById(R.id.sdka);
        blue = (Switch)findViewById(R.id.blue);
//        mClean = (Button) findViewById(R.id.clean);
//        mUp = (Button) findViewById(R.id.up);
        controlPart.setVisibility(View.INVISIBLE);

        context = this;

    }

    private void doServerUpdate() {
        //do more api request
        Intent intent = new Intent(ACTION_CHECK_TIME);
        sendBroadcast(intent);
    }


    @Override
    protected void onResume() {
        passWord.setText("");
        SharedPreferences sharedPreferences = getSharedPreferences("share", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFirstRun)
        {
            ArrayList<String> netAdressList = new ArrayList<>();
            netAdressList.add("119.23.60.223");
            netAdressList.add("119.23.113.71");
            netAdressList.add("192.0.0.0/8");
            netAdressList.add("10.0.0.0/8");
            netAdressList.add("42.156.141.13");
            netAdressList.add("120.77.167.192");
            netAdressList.add("120.77.166.123");
            dpm.removeIPWhiteList(componentName,netAdressList);
            Log.d("debug", "第一次运行");
            editor.putBoolean("isFirstRun", false);
            editor.commit();

        } else
        {
            Log.d("debug", "不是第一次运行");

        }
        doServerUpdate();
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
        String version = checkVersion();
        mTvVersion.setText("V " + version);

        Intent callByEdu = getIntent();//被高分云程序调起
//        if (callByEdu.getFlags() == 101) {
//            mRlControl.setVisibility(View.GONE);
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    finish();
//                }
//            }, 1000);
//
//        } else {
//            mRlControl.setVisibility(View.VISIBLE);
//        }
        if (callByEdu.getFlags() == 101){
            finish();
        }

//----------------------------------
        //获取ip上网白名单
        List<String> ipWhiteList = dpm.getIPWhiteList(componentName);
        String s = String.valueOf(ipWhiteList);
        LogUtils.file("白名单：", s + "\r\n");

        //获取App上网白名单
        List<String> networkAccessAppWhiteList = dpm.getNetworkAccessAppWhiteList(componentName);
        String appWhiteList = String.valueOf(networkAccessAppWhiteList);
        LogUtils.file("App上网白名单：", appWhiteList + "\r\n");

        //--------------------------

        super.onResume();
    }

    public void onClickOK(View view) {
        dialog = new SpotsDialog(context,"正在请求数据，请稍后......");
        dialog.show();
        final String password = passWord.getText().toString();
        if (password.length() == 0) {
            Toast.makeText(this, "无效的密码,请重新输入", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            return;
        }

        SharedPreferences sharedPreferences =
                this.getSharedPreferences(ConfigParam.SP_NAME, Context.MODE_PRIVATE);

        String stored = sharedPreferences.getString(ConfigParam.SP_NAME_KEY_PASSWORD, "0");
        if (stored.equals(password)) {
            handler.sendEmptyMessage(0x8);//enableControl();验证密码成功，会把权限打开
        } else {
            handler.sendEmptyMessage(0x1);//onClickClose(null);验证密码失败，会执行“马上关闭”
        }
    }

    public void onClickClose(View view) {
        Log.e("cong", "going close port");
        disableControl();
    }

    public static void  close(){
        usbSwitch.setChecked(false);

        installAppSwitch.setChecked(false);

        resetSysSwitch.setChecked(false);

        browserUse.setChecked(false);
        sdka.setChecked(false);
        blue.setChecked(false);

        //set app installation enabled
//        dpm.setAppInstallDisabled(componentName, true);

        dpm.setFactoryResetDisabled(componentName, true);


        dpm.setMtpDisabled(componentName, true);

        dpm.setHomeButtonDisabled(componentName, true);

        dpm.setInstallPackages(componentName, appInstallWhiteList);

        dpm.setUSBDataDisabled(componentName, true);

        dpm.setSafeModeDisabled(componentName,true);    //禁止安全模式

        dpm.setBluetoothDisabled(componentName, true);   //禁止蓝牙

        dpm.setExternalStorageDisabled(componentName, true);     //禁止 SD 卡

        controlPart.setVisibility(View.GONE);
    }

    public  void enableControl() {
//        loopUpdata(0);

        controlPart.setVisibility(View.VISIBLE);

        usbSwitch.setChecked(true);

        installAppSwitch.setChecked(true);

        resetSysSwitch.setChecked(true);

        browserUse.setChecked(true);
        sdka.setChecked(true);
        blue.setChecked(true);

        dpm.setFactoryResetDisabled(componentName, false);

        dpm.setAdbDisabled(componentName, false);

        dpm.setMtpDisabled(componentName, false);

        dpm.setHomeButtonDisabled(componentName, false);

//        dpm.setApplicationDisabled(componentName, HW_BROWSER_PACKAGE, false);

        dpm.setApplicationDisabled(componentName,BROWER_PACKAGE_NAME,false);//让谷歌浏览器显示

        dpm.setTimeModifyDisabled(componentName, false);

        dpm.setUSBDataDisabled(componentName, false);

        dpm.setBluetoothDisabled(componentName, false);   //启用蓝牙

        dpm.setExternalStorageDisabled(componentName, false);     //启用 SD 卡

        dpm.setSafeModeDisabled(componentName,false);

        //open install
        ArrayList<String> arrayList = (ArrayList<String>) dpm.getInstallPackageWhiteList(componentName);
        dpm.removeInstallPackages(componentName, arrayList);//输入密码免密安装app

        dpm.setIPWhiteList(componentName, null);//浏览器上网
        dpm.cleanNetworkAccessAppWhiteList(componentName);
        dpm.setDisallowedUninstallPackages(componentName, null);

    }

    private void disableControl() {

//        loopUpdata(1);

        usbSwitch.setChecked(false);

        installAppSwitch.setChecked(false);

        resetSysSwitch.setChecked(false);

        browserUse.setChecked(false);
        sdka.setChecked(false);
        blue.setChecked(false);

        //set app installation enabled
//      dpm.setAppInstallDisabled(componentName, true);

        ar.add(packageName);
        ar.add("com.yqh.education");//高分云app
        ar.add("com.android.application");
        ar.add(BROWER_PACKAGE_NAME);

        dpm.setDisallowedUninstallPackages(componentName, ar);//不能卸载管控app

        dpm.setFactoryResetDisabled(componentName, true);


        dpm.setMtpDisabled(componentName, true);

        dpm.setHomeButtonDisabled(componentName, true);

        dpm.setInstallPackages(componentName, appInstallWhiteList);

        dpm.setUSBDataDisabled(componentName, true);

        dpm.setSafeModeDisabled(componentName,true);    //禁止安全模式

        dpm.setBluetoothDisabled(componentName, true);   //禁止蓝牙

        dpm.setExternalStorageDisabled(componentName, true);     //禁止 SD 卡

        controlPart.setVisibility(View.GONE);
        alarmManager.cancel(pi);
    }

    private void setAppWhiteList() {

        //setInstallPackageBlackList

        appInstallWhiteList.add(packageName);

        appInstallWhiteList.add("dev.romdev.com.zhangan");

        appInstallWhiteList.add("com.romdev.app1");

        appInstallWhiteList.add("com.yqh.education");//高分云app包名

        appInstallWhiteList.add("com.yangcong345.android.phone");

        appInstallWhiteList.add("com.A17zuoye.mobile.homework");

        appInstallWhiteList.add("com.knowbox.wb.student");

        appInstallWhiteList.add(BROWER_PACKAGE_NAME);

        appInstallWhiteList.add(HW_BROWSER_PACKAGE);



//        dpm.setAppWhite(componentName, true, appInstallWhiteList);

        dpm.addInstallPackages(componentName, appInstallWhiteList);
    }

    /**
     * 检查版本更新前，先检查存储权限。否则无法下载apk
     *
     * @author miao
     * @createTime 2017年10月19日
     */
    private void checkMyPermission() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.CAMERA}, 1101);
        } else {
//
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1101) {
            if (grantResults.length != 0 && (grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
//                showToast("请设置必须的应用权限，否则将会导致运行异常！");
                Toast.makeText(context, "请设置必须的应用权限，否则将会导致运行异常！", Toast.LENGTH_SHORT).show();
            } else if (grantResults.length != 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

            }
        }
    }

    //--------------------------检擦app更新end--------------------------------------------------
    //EventBus注册和销毁
    @Override
    protected void onDestroy() {
//        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

    //检查版本号
    public String checkVersion() {
        PackageManager pm = getPackageManager();
        PackageInfo pi = null;
        String version = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            version = pi.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

//    @Subscribe(threadMode = ThreadMode.MainThread,sticky = true)
//    public void onEvent(EventBusMsg msg) {//接收从CircledBroadcastReceiver发过来的消息
//        //检查版本号
//        PackageManager pm = context.getPackageManager();
//        PackageInfo pi = null;
//        String oldVersion = null;
//        try {
//            pi = pm.getPackageInfo(context.getPackageName(), 0);
//            oldVersion = pi.versionName.replace(".", "");
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
////        String oldVersion = checkVersion();
//        if (StringUtil.isEmpty(oldVersion) || !StringUtil.strIsNum(oldVersion)) {
//            return;
//        }
//
//        final String finalOldVersion = oldVersion.replace(".", "");
//
//
//        if (msg.what == ConfigParam.DOWNLOADAPK) {
//            UpdateAPPResponse updata = (UpdateAPPResponse) msg.object;
//
//            if (StringUtil.isNotEmpty(updata.data.get(0).versionRecord.versionCode)) {//版本号
//                String forcedUpgrade = updata.data.get(0).versionRecord.downloadUrl;
//                String currentVersion = updata.data.get(0).versionRecord.versionCode.replace(".", "");
//                if (StringUtil.isNotEmpty(currentVersion) && StringUtil.strIsNum(currentVersion)) {
//                    //需要升级
//                    if (Integer.parseInt(currentVersion) > Integer.parseInt(finalOldVersion)) {
//                        //不强制升级
//                        if (StringUtil.isNotEmpty(updata.data.get(0).versionRecord.forcedUpgrade)
//                                && "I02".equals(updata.data.get(0).versionRecord.forcedUpgrade)) {
//
//                        }
//                        //强制升级
//                        if (StringUtil.isNotEmpty(updata.data.get(0).versionRecord.forcedUpgrade)
//                                && "I01".equals(updata.data.get(0).versionRecord.forcedUpgrade)) {
//
////                                        downloadApk(updata.data.get(0).versionRecord.downloadUrl);
//
////                                        UpdateAPPDialog updateAPPDialog = new UpdateAPPDialog();
////                                        updateAPPDialog.setIsForce(true);
////
////                                        updateAPPDialog.setDownloadUrl(updata.data.get(0).versionRecord.downloadUrl);
////                                        updateAPPDialog.setUploadTitle(updata.data.get(0).versionRecord.uploadTitle);
////                                        updateAPPDialog.setUploadDetail(updata.data.get(0).versionRecord.uploadDetail);
////                                        updateAPPDialog.setCurrentVersion(updata.data.get(0).versionRecord.versionCode);
////                                        updateAPPDialog.initUpdateAPPDialog(context).show();
//
////--------------------------------------------------------------------------------------------
//                            InstallApkQuietly.packageAppName = "dev.romdev.com.m2pad";
//                            InstallApkQuietly.url = updata.data.get(0).versionRecord.downloadUrl;
//                            InstallApkQuietly.mContext = this;
//                            InstallApkQuietly.init();
//
//                        }
//                    }
//                }
//            }
//        }
//
//    }

}
