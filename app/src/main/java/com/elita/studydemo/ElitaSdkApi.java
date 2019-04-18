package com.elita.studydemo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.elita.studydemo.service.ElitaRouter;


/**
 * @author nie yunlong
 * @description
 * @date 2018/6/20
 */
public class ElitaSdkApi {

    private static ElitaSdkApi sdkApi;
    /**
     * 初始化
     */
    private ElitaRouter router;

    public ElitaSdkApi(Context context) {
        this.router = new ElitaRouter(context.getApplicationContext());
    }

    public static ElitaSdkApi get(@NonNull Context context) {
        return createElitaSdkApi(context);
    }

    /**
     * 创建sdk api
     *
     * @param context
     * @return
     */
    private static ElitaSdkApi createElitaSdkApi(final Context context) {
        if (sdkApi == null) {
            sdkApi = new ElitaSdkApi(context);
        }
        return sdkApi;
    }

    /**
     * log 开关
     *
     * @param isClose
     */
    public static void setLogOpen(boolean isClose) {
        ElitaLogUtils.setEnableLog(isClose);
    }

//    /**
//     * 适当的时机传入
//     *
//     * @param cityId   城市ID 默认是北京
//     * @param deviceId
//     * @param cityName
//     */
//    public ElitaSdkApi initParams(final String cityId, final String deviceId, String cityName, String appId, String token) {
//        router.initParam(cityId, deviceId, cityName,appId,token);
//        return this;
//    }


//    /**
//     * 启动sdk主页面
//     *
//     * @param activity
//     */
//
//    public static void launch(Activity activity) {
//        MainActivity.launch(activity);
//    }
    /*
    * 停止Api
    * */
    public static void clearApi(){

        if(sdkApi!=null){
            sdkApi = null;
        }
    }
}
