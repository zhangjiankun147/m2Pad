package dev.romdev.com.m2pad.net;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author WJQ
 */
public class RetrofitManager {

//    //================单例实现：方式1(begin)=======================饿汉
//    private static RetrofitManager instance = new RetrofitManager();
//
//    private RetrofitManager() {
//    }
//
//    public static RetrofitManager getInstance() {
//        return instance;
//    }
//    //================单例实现：方式1(end)=========================


//    //================单例实现：方式2(begin)=======================懒汉
//    private static RetrofitManager instance = null;
//
//    private RetrofitManager() {
//    }
//
//    public static RetrofitManager getInstance() {
//        if (instance == null) {
//            synchronized (RetrofitManager.class) {
//                if (instance == null) {
//                    instance = new RetrofitManager();
//                }
//            }
//        }
//        return instance;
//    }
//    //================单例实现：方式2(end)=========================


    //================单例实现：方式3(begin)=======================懒汉
    private RetrofitManager() {
        initRetrofit();
    }

    public static RetrofitManager getInstance() {
        return SingleHolder.instance;
    }

    private static class SingleHolder {
        public static RetrofitManager instance = new RetrofitManager();
    }
    //================单例实现：方式3(end)=========================

    private Retrofit retrofit;
    private IHttpService httpService;

    public IHttpService getService() {
        return httpService;
    }

    private void initRetrofit() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(10, TimeUnit.SECONDS);    // 连接超时时间:10秒

//        if (LogUtil.mDebug) {       // 开发阶段，才在后台打印日志
//            // 设置打印日志的拦截器
//            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            client.addInterceptor(interceptor);
//        }

        retrofit = new Retrofit.Builder()
                .baseUrl(IHttpService.HOST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();

        httpService = retrofit.create(IHttpService.class);
    }
}
