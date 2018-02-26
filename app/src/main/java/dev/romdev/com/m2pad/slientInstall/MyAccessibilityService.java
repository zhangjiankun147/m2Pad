package dev.romdev.com.m2pad.slientInstall;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LCL on 2017/11/17.
 */

public class MyAccessibilityService extends AccessibilityService {
    Map<Integer,Boolean> handledMap = new HashMap<>();
    public MyAccessibilityService(){

    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityNodeInfo nodeInfo = event.getSource();
        if (nodeInfo != null) {
            int eventType = event.getEventType();
            if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
                    eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                if (handledMap.get(event.getWindowId()) == null) {
                    boolean handled = iterateNodesAndHandle(nodeInfo);
                    if (handled){
                        handledMap.put(event.getWindowId(),true);
                    }

                }

            }
        }
    }
    private boolean iterateNodesAndHandle(AccessibilityNodeInfo nodeInfo){
        if (nodeInfo != null){
            int childCount = nodeInfo.getChildCount();
            if("android.widget.Button".equals(nodeInfo.getClassName())){
                String nodeContent = nodeInfo.getText().toString();
                Log.e("服务",nodeContent);
                if("安装".equals(nodeContent)
                        || "打开".equals(nodeContent)
                        ||"确定".equals(nodeContent)){
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return true;
                }
            }else if ("android.widget.ScrollView".equals(nodeInfo.getClassName())){
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
            }
            for (int i= 0; i < childCount; i++) {
                AccessibilityNodeInfo childNodeInfo = nodeInfo.getChild(i);
                if (iterateNodesAndHandle(childNodeInfo)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onInterrupt(){

    }

    //----------------------------------------------------------------------------------------------------

//    @Override
//    public void onAccessibilityEvent(AccessibilityEvent event) {
//        Log.d("InstallService", event.toString());
//        checkInstall(event);
//    }
//
//
//    private void checkInstall(AccessibilityEvent event) {
//        AccessibilityNodeInfo source = event.getSource();
//        if (source != null) {
//            boolean installPage = event.getPackageName().equals("com.android.packageinstaller");
//            if (installPage) {
//                installAPK(event);
//            }
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    private void installAPK(AccessibilityEvent event) {
//        AccessibilityNodeInfo source = getRootInActiveWindow();
//        List<AccessibilityNodeInfo> nextInfos = source.findAccessibilityNodeInfosByText("下一步");
//        nextClick(nextInfos);
//        List<AccessibilityNodeInfo> installInfos = source.findAccessibilityNodeInfosByText("安装");
//        nextClick(installInfos);
//        List<AccessibilityNodeInfo> openInfos = source.findAccessibilityNodeInfosByText("打开");
//        nextClick(openInfos);
////        List<AccessibilityNodeInfo> downLoad = source.findAccessibilityNodeInfosByText("立即更新");
////        nextClick(downLoad);
//
//        runInBack(event);
//
//    }
//
//    private void runInBack(AccessibilityEvent event) {
//        event.getSource().performAction(AccessibilityService.GLOBAL_ACTION_BACK);
//    }
//
//    private void nextClick(List<AccessibilityNodeInfo> infos) {
//        if (infos != null)
//            for (AccessibilityNodeInfo info : infos) {
//                if (info.isEnabled() && info.isClickable())
//                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            }
//    }
//
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//    private boolean checkTilte(AccessibilityNodeInfo source) {
//        List<AccessibilityNodeInfo> infos = getRootInActiveWindow().findAccessibilityNodeInfosByViewId("@id/app_name");
//        for (AccessibilityNodeInfo nodeInfo : infos) {
//            if (nodeInfo.getClassName().equals("android.widget.TextView")) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public void onInterrupt() {
//
//    }
//
//    @Override
//    protected void onServiceConnected() {
//        Log.d("InstallService", "auto install apk");
//    }
    //------------------------------------------------------------------------------------



}



