package com.elita.studydemo;

import com.elita.studydemo.service.ElitaMessageService;
import com.elita.studydemo.service.ServiceProxy;

/**
 * Created by SDC on 2019/4/18.
 */

public class RequestManager {
    /**
     * 获取webSocket服务
     */
    public ElitaMessageService getWsService() {
        return ServiceProxy.getInstance().getMessageService();

    }

    /**
     * 写网络请求参数
     *
     * @param json
     */
    public void write(String json) {
        try {
            if(getWsService()!=null){

            getWsService().write(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
