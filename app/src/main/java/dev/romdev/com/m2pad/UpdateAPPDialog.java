package dev.romdev.com.m2pad;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.io.File;

/**
 * @className: UpdateAPPDialog
 * @classDescription: APP更新
 * @author: miao
 * @createTime: 2017年09月12日
 */
public class UpdateAPPDialog implements View.OnClickListener{

    private TextView title;
    private TextView tvUploadTitle;
    private TextView tvUploadDetail;
    private TextView forceUpdate;
    private TextView cancelUpdate;
    private TextView baseLine2;
    private TextView startUpdate;
    private LinearLayout llAction;
    private TextView baseLine;
    private FlikerProgressBar roundProgressbar;
    private LinearLayout llDownload;

    private Dialog dialog;
    private Context context;
    private String downloadUrl;
    private boolean isForce;
    private String uploadTitle;
    private String uploadDetail;
    private String currentVersion;

    public Dialog initUpdateAPPDialog(Context context) {
        if (context == null) {
            return null;
        }
        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.update_app_dialog, null);

        title = (TextView) view.findViewById(R.id.title);
        tvUploadTitle = (TextView) view.findViewById(R.id.uploadTitle);
        tvUploadDetail = (TextView) view.findViewById(R.id.uploadDetail);
        forceUpdate = (TextView) view.findViewById(R.id.force_update);
        forceUpdate.setOnClickListener(this);
        cancelUpdate = (TextView) view.findViewById(R.id.cancel_update);
        cancelUpdate.setOnClickListener(this);
        baseLine2 = (TextView) view.findViewById(R.id.baseLine2);
        baseLine = (TextView) view.findViewById(R.id.baseLine);
        startUpdate = (TextView) view.findViewById(R.id.start_update);
        startUpdate.setOnClickListener(this);
        llAction = (LinearLayout) view.findViewById(R.id.ll_action);
        roundProgressbar = (FlikerProgressBar) view.findViewById(R.id.round_progressbar);
        llDownload = (LinearLayout) view.findViewById(R.id.ll_download);

        dialog = new Dialog(context, R.style.loadingDialog);
        dialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        dialog.setCancelable(false);

        if(StringUtil.isNotEmpty(uploadTitle)){
            tvUploadTitle.setText(uploadTitle);
        }
        if(StringUtil.isNotEmpty(uploadDetail)){
            tvUploadDetail.setText(uploadDetail);
        }
        //执行强制更新
        if(isForce){
            forceUpdate.setVisibility(View.VISIBLE);
            cancelUpdate.setVisibility(View.GONE);
            baseLine2.setVisibility(View.GONE);
            startUpdate.setVisibility(View.GONE);
        }
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.force_update:
                llAction.setVisibility(View.GONE);
                baseLine.setVisibility(View.GONE);
                llDownload.setVisibility(View.VISIBLE);
                downloadApk(downloadUrl);
                break;
            case R.id.cancel_update:
//                dialog.dismiss();
                downloadApk(downloadUrl);
                break;
            case R.id.start_update:
                llAction.setVisibility(View.GONE);
                baseLine.setVisibility(View.GONE);
                llDownload.setVisibility(View.VISIBLE);
                downloadApk(downloadUrl);
                break;
        }
    }


//    private void startDownload() {
//        if (context == null) {
//            return ;
//        }
//
//        OkGo.<String>post("http://192.168.1.255/" + "download")
//                .params("fileName", "app-release-"+currentVersion+".apk")
//                .params("operateAccountNo", CommonDatas.getAccountId(context))
//                .params("belongSchoolId", CommonDatas.getBelongSchoolId())
//                .params("downloadUrl", downloadUrl)
//                .params("courseName", "apk")
//                .execute(new StringCallback() {
//
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        String newUrl = "";
////                        LogUtils.d(response.body());
//                        try {
//                            JSONObject jsonObject = new JSONObject(response.body());
//                            if (jsonObject.getInt("code") == -1) {
//                                //外网下载,主控端未准备好
//                                newUrl = downloadUrl;
//                            } else if(jsonObject.getInt("code") == 0){
////                                LogUtils.d("内网下载!");
//                                newUrl = CommonDatas.getLocalHost() + jsonObject.getString("downloadUrl");
//                            }else {
//                                //异常情况，外网下载。
//                                newUrl = downloadUrl;
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        downloadApk(newUrl);
//                    }
//
//                    @Override
//                    public void onError(Response<String> response) {
//                        super.onError(response);
//                        //查询主控机失败 直接下载
//                        downloadApk(downloadUrl);
//                    }
//                });
//
//    }
    //下载
    public void downloadApk(String Url){
        if (context == null) {
            return ;
        }
        title.setText("下载中...");
        OkGo.<File>get(Url)
        .execute(new FileCallback() {
            @Override
            public void onStart(Request<File, ? extends Request> request) {
                super.onStart(request);
            }

            @Override
            public void downloadProgress(Progress progress) { //(int) (progress.fraction * 100)
                super.downloadProgress(progress);
                roundProgressbar.setProgress((int) (progress.fraction * 100));
            }

            @Override
            public void onSuccess(Response<File> response) {
                if (context == null) {
                    return ;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Uri contentUri = FileProvider.getUriForFile(context,
                            context.getApplicationContext().getPackageName() + ".provider",
                            response.body());
                    intent.setDataAndType(contentUri,"application/vnd.android.package-archive");
                } else {
                    uri = Uri.fromFile(response.body());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(uri,"application/vnd.android.package-archive");
                }
                context.startActivity(intent);

//                //智能安装
//                String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
////                app-control-1.0.2.apk
////                String apkPath = sdPath + "/app"+"/dev.romdev.com.m2pad-2/base.apk";
//                String apkPath = sdPath +"/Download/app-control-1.0.2.apk";
////                String apkPath = sdPath +"dev.romdev.com.m2pad-2/base.apk";
//                if (TextUtils.isEmpty(apkPath)){
//                    Toast.makeText(context, "请选择安装包", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                File file = new File(apkPath);
//                if (!file.exists()){
//                    Toast.makeText(context, "安装包找到不到", Toast.LENGTH_SHORT).show();
//                }else {
//                    Toast.makeText(context, "成功", Toast.LENGTH_SHORT).show();
//                }
//                Uri uri = Uri.fromFile(file);
//                Intent localIntent = new Intent(Intent.ACTION_VIEW);
//                localIntent.setDataAndType(uri,"application/vnd.android.package-archive");
//                context.startActivity(localIntent);
            }

            @Override
            public void onFinish() {
//                EventBusMsg msg = new EventBusMsg(ConfigParam.INSTALLAPK,"");
//                EventBus.getDefault().post(msg);
                super.onFinish();
            }
        });
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public void setIsForce(boolean isForce) {
        this.isForce = isForce;
    }

    public void setUploadTitle(String uploadTitle) {
        this.uploadTitle = uploadTitle;
    }

    public void setUploadDetail(String uploadDetail) {
        this.uploadDetail = uploadDetail;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

}
