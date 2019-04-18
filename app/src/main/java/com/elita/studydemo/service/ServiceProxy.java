package com.elita.studydemo.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.elita.studydemo.ElitaLogUtils;


/**
 * @author nie yunlong
 * @description
 * @date 2018/6/19
 */
public class ServiceProxy {

    private static ServiceProxy instance;

    private RequestServiceConnection messageServiceConnection = new RequestServiceConnection();

    public static ServiceProxy getInstance(){
        if(instance==null){
            instance=new ServiceProxy();
        }
        return instance;
    }

    /**
     * 启动消息的服务
     */
    public void startMessageService(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), ElitaMessageService.class);
        context.getApplicationContext().bindService(intent, messageServiceConnection, Context.BIND_AUTO_CREATE);
        ElitaLogUtils.e("--->message_service---->消息服务--->start");
    }

    private class RequestServiceConnection implements ServiceConnection {
        private ElitaMessageService msgService = null;

        public final ElitaMessageService getMessageService() {
            return msgService;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ElitaLogUtils.w("--->message_service--->onServiceConnected--->服务已连接");

            try {
                ElitaMessageService.RequestBinder meesageBinder = (ElitaMessageService.RequestBinder) service;

                msgService = meesageBinder.getService();
                if (msgService != null) {
                    msgService.startWork();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ElitaLogUtils.w("--->message_service--->onServiceDisconnected");
            if (msgService != null) {
                msgService.stopWork();
            }
        }
    }

    public RequestServiceConnection getMessageServiceConnection() {
        if(messageServiceConnection!=null){
            return messageServiceConnection;

        }
        return new RequestServiceConnection();
    }

    /**
     * 获取消息服务器对象
     *
     * @return
     */
    public ElitaMessageService getMessageService() {
        return messageServiceConnection.getMessageService();
    }

    /*
    * 停止服务
    * */
    public void stopService(){
        if(messageServiceConnection!=null){
            if(messageServiceConnection.getMessageService()!=null) {
                messageServiceConnection.getMessageService().stopService();
            }
        }
    }

    /*
    * 暂停服务
    * */
    public void onPauseWs(){
        if(messageServiceConnection!=null){
            if(messageServiceConnection.getMessageService()!=null) {
                messageServiceConnection.getMessageService().onPause();
            }
        }
    }

    /*
    * 启动服务
    * */
    public void onResumeWs(){
        if(messageServiceConnection!=null){
            if(messageServiceConnection.getMessageService()!=null){
                messageServiceConnection.getMessageService().onResume();

            }
        }
    }

}
