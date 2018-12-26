package com.clubank.device.op;

import com.clubank.common.BuildConfig;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by long on 17-9-13.
 */

public class OkHttpManager {

    private static OkHttpClient okHttpClient;
    private static OkHttpManager okHttpManager;

    public synchronized static OkHttpManager getInstance(){
        if (okHttpClient == null || okHttpManager == null){
            okHttpManager = new OkHttpManager();
        }
        return okHttpManager;
    }

    private OkHttpManager(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (BuildConfig.DEBUG){
            builder.addInterceptor(logging);
//            builder.addInterceptor(new HttpStatusInterceptor());
        }else {
            builder.proxy(Proxy.NO_PROXY);
        }
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        okHttpClient = builder.build();
    }

    public OkHttpClient getClient(){
        if (okHttpClient != null){
            return okHttpClient;
        }else {
            getInstance();
            return okHttpClient;
        }
    }
}
