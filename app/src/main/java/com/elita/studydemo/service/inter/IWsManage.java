package com.elita.studydemo.service.inter;


import okhttp3.WebSocket;

/**
 * @author nie yunlong
 * @description webSocket
 * @date 2018/6/20
 */
public interface IWsManage {
    /**
     * 获取webSocket
     *
     * @return
     */
    WebSocket getWebSocket();

    /**
     * 开始连接socket
     */
    void startConnectWs();

    /**
     * 断开连接
     */
    void disconnectWs();

    /**
     * 尝试重连
     */
    void onReconnect();

    /**
     * 写数据
     *
     * @param json
     */
    void write(String json);


}
