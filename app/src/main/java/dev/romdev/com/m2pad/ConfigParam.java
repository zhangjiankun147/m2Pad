package dev.romdev.com.m2pad;

/**
 * Created by kevin on 10/23/17.
 */

public class ConfigParam {
    //http://119.23.113.71:8080/service-soa/sysModuleVersion/
    //测试：http://119.23.60.223:2001/service-soa/sysControlPolicy/
    public final static String TEST_API_BASE = "http://119.23.60.223:2001/service-soa/sysControlPolicy/";

//        public final static String BASE_API = "http://119.23.113.71:8080/service-soa/sysControlPolicy/";    //正式
    public final static String BASE_API = "http://119.23.60.223:2001/service-soa/sysControlPolicy/";  //测试

//    public final static String BASE_API_Updata = "http://119.23.113.71:8080/service-soa/sysModuleVersion/";     //升级管控app
    //升级测试
    public final static String BASE_API_Updata = "http://119.23.113.71:9001/service-soa/sysModuleVersion/";     //升级管控app

    public final static String WEB_UPDATA_APP = "getLatestModuleVerion";    //升级管控app

    public final static String SDCARD_CHECK = "sdCardDisable";        //    SD 卡

    public final static String BLUETOOTH_CHECK = "bluetoothDisable";    //  蓝牙

    public final static String SAFEMODE_CHECK = "safeModeDisable";     //  安全模式

    public final static String NETWORK_ACCESS_APP = "webAccessAppList";   //  指定 app 上网

    public final static String UNINSTALL_APP = "controlUninstallAppList";  //不能卸载指定的 app

    public final static String WEB_ACCESS_IP_CONTROL = "controlWebAccessList";

    public final static String WEB_ACCESS_TIME  = "webAccessTime";

    public final static String ACTION_USB_INSTALL_APP = "usbInstallDisable";

    public final static String NO_PASSWORD_INSTLAL = "noPasswrodInstall";

    public final static String HOME_BUTTON_CHECK = "homeButtonDisable";

    public final static String PASSWORD_ACCESS = "controlPassword";

    public final static String API_USER_NAME = "control_app";

    public final static String API_PASSWORD = "123456";

    public final static String API_PASSWORD_ENCRYPT = MD5Utils.md5(API_PASSWORD).toUpperCase();

    public final static String LOGIN_PASSWORD = "123456";

    public final static String SP_NAME = "sp_record";

    public final static String SP_NAME_KEY_PASSWORD = "sp_password";

    public final static int DOWNLOADAPK = 1;

    public final static int INSTALLAPK = 2;
    public final static int ALIVEBYEDU = 101;

}
