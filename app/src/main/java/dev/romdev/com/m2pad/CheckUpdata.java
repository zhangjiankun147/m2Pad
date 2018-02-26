package dev.romdev.com.m2pad;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LCL on 2017/11/15.
 */

public class CheckUpdata {
    public void checkVersion(final Context context) {

        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;
        String oldVersion = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            oldVersion = pi.versionName.replace(".", "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (StringUtil.isEmpty(oldVersion) || !StringUtil.strIsNum(oldVersion)) {
            return;
        }

        final String finalOldVersion = oldVersion;

//----------------------------------------------------------------------------------------------------
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("interUser","value");
            jsonObject.put("interPwd","value");
            jsonObject.put("operateAccountNo",123);
            jsonObject.put("belongSchoolId",123);
            jsonObject.put("moduleType","T03");

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        RetrofitManager
//                .getInstance()
//                .getService()
//                .updateApp(jsonObject.toString())
//                .enqueue(new Callback<UpdateAPPResponse>() {
//                    @Override
//                    public void onResponse(Call<UpdateAPPResponse> call, Response<UpdateAPPResponse> response) {
////                        response.body().data
//                        if (StringUtil.isNotEmpty(response.body().data.get(0).versionRecord.versionCode)) {//版本号
//                            String currentVersion = response.body().data.get(0).versionRecord.versionCode.replace(".", "");
//                            if (StringUtil.isNotEmpty(currentVersion) && StringUtil.strIsNum(currentVersion)) {
//                                //需要升级
//                                if (Integer.parseInt(currentVersion) > Integer.parseInt(finalOldVersion)) {
//                                    //不强制升级
//                                    if (StringUtil.isNotEmpty(response.body().data.get(0).versionRecord.forcedUpgrade)
//                                            && "I02".equals(response.body().data.get(0).versionRecord.forcedUpgrade)) {
//                                        UpdateAPPDialog updateAPPDialog = new UpdateAPPDialog();
//                                        updateAPPDialog.setDownloadUrl(response.body().data.get(0).versionRecord.downloadUrl);
//                                        updateAPPDialog.setIsForce(false);
//                                        updateAPPDialog.setUploadTitle(response.body().data.get(0).versionRecord.uploadTitle);//更新标题
//                                        updateAPPDialog.setUploadDetail(response.body().data.get(0).versionRecord.uploadDetail);//更新内容
//                                        updateAPPDialog.setCurrentVersion(response.body().data.get(0).versionRecord.versionCode);//版本号
//                                        updateAPPDialog.initUpdateAPPDialog(context).show();
//                                    }
//                                    //强制升级
//                                    if (StringUtil.isNotEmpty(response.body().data.get(0).versionRecord.forcedUpgrade)
//                                            && "I03".equals(response.body().data.get(0).versionRecord.forcedUpgrade)) {
//                                        UpdateAPPDialog updateAPPDialog = new UpdateAPPDialog();
//                                        updateAPPDialog.setDownloadUrl(response.body().data.get(0).versionRecord.downloadUrl);
//                                        updateAPPDialog.setIsForce(true);
//                                        updateAPPDialog.setUploadTitle(response.body().data.get(0).versionRecord.uploadTitle);
//                                        updateAPPDialog.setUploadDetail(response.body().data.get(0).versionRecord.uploadDetail);
//                                        updateAPPDialog.setCurrentVersion(response.body().data.get(0).versionRecord.versionCode);
//                                        updateAPPDialog.initUpdateAPPDialog(context).show();
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<UpdateAPPResponse> call, Throwable t) {
//                        Log.e(TAG, "onFailure: ",t);
//
//                        String message = t.getMessage();
//                        Log.e("请求失败","网络异常");
//
//                    }
//                });





//----------------------------------------------------------------------------------------




//        -----------------------------------------------------------------------------------
//        OkHttpUtils
//                .postString()
//                .url("http://119.23.113.71:8080/service-soa/getLatestModuleVerion")
//                .content(new Gson().toJson(jsonObject))
////                .mediaType(MediaType.parse("application/json; charset=utf-8"))
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        Log.e("checkUpdata","checkUpdata error");
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        UpdateAPPResponse updata = new Gson().fromJson(response, UpdateAPPResponse.class);
//
//                        if (StringUtil.isNotEmpty(updata.data.get(0).versionRecord.versionCode)) {//版本号
//                            String currentVersion = updata.data.get(0).versionRecord.versionCode.replace(".", "");
//                            if (StringUtil.isNotEmpty(currentVersion) && StringUtil.strIsNum(currentVersion)) {
//                                //需要升级
//                                if (Integer.parseInt(currentVersion) > Integer.parseInt(finalOldVersion)) {
//                                    //不强制升级
//                                    if (StringUtil.isNotEmpty(updata.data.get(0).versionRecord.forcedUpgrade)
//                                            && "I02".equals(updata.data.get(0).versionRecord.forcedUpgrade)) {
//                                        UpdateAPPDialog updateAPPDialog = new UpdateAPPDialog();
//                                        updateAPPDialog.setDownloadUrl(updata.data.get(0).versionRecord.downloadUrl);
//                                        updateAPPDialog.setIsForce(false);
//                                        updateAPPDialog.setUploadTitle(updata.data.get(0).versionRecord.uploadTitle);//更新标题
//                                        updateAPPDialog.setUploadDetail(updata.data.get(0).versionRecord.uploadDetail);//更新内容
//                                        updateAPPDialog.setCurrentVersion(updata.data.get(0).versionRecord.versionCode);//版本号
//                                        updateAPPDialog.initUpdateAPPDialog(context).show();
//                                    }
//                                    //强制升级
//                                    if (StringUtil.isNotEmpty(updata.data.get(0).versionRecord.forcedUpgrade)
//                                            && "I03".equals(updata.data.get(0).versionRecord.forcedUpgrade)) {
//                                        UpdateAPPDialog updateAPPDialog = new UpdateAPPDialog();
//                                        updateAPPDialog.setDownloadUrl(updata.data.get(0).versionRecord.downloadUrl);
//                                        updateAPPDialog.setIsForce(true);
//                                        updateAPPDialog.setUploadTitle(updata.data.get(0).versionRecord.uploadTitle);
//                                        updateAPPDialog.setUploadDetail(updata.data.get(0).versionRecord.uploadDetail);
//                                        updateAPPDialog.setCurrentVersion(updata.data.get(0).versionRecord.versionCode);
//                                        updateAPPDialog.initUpdateAPPDialog(context).show();
//                                    }
//                                }
//                            }
//                        }
//                    }
//                });

    }

}
