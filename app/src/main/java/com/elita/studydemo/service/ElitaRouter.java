package com.elita.studydemo.service;

import android.content.Context;

import com.elita.studydemo.ContextUtil;


/**
 * @author nie yunlong
 * @description 初始化 服务 上下文
 * @date 2018/6/20
 */
public class ElitaRouter {
    /**
     * 请求参数
     */
//    private AppPreferenceHelper appPreferenceHelper;

    public ElitaRouter(Context context) {
        ContextUtil.setContext(context.getApplicationContext());
        ServiceProxy.getInstance().startMessageService(context.getApplicationContext());
//        appPreferenceHelper = AppPreferenceHelper.getInstance(context.getApplicationContext());
    }

    /**
     * 初始化必要参数
     *
     * @param cityId   城市ID
     * @param deviceId 手机唯一标示
     * @param cityName 城市名称
     */
//    public void initParam(final String cityId, final String deviceId, String cityName,String appId,String token) {
//        appPreferenceHelper.initParams(cityId, deviceId, cityName,appId,token);
//    }
//    /**
//     * 初始化百度SDK参数
//     *
//     * @param appId   百度AppId
//     * @param apiKey API KEY
//     * @param secretKey
//     */
//    public void initBaiduKey( String appId,String apiKey,String secretKey) {
//        appPreferenceHelper.initBaiduKey(appId, apiKey, secretKey);
//    }
}
