package com.elita.studydemo.service.inter;

/**
 * @author nie yunlong
 * @description 获取请求数据
 * @date 2018/6/19
 */
public interface IRequestDataListener {
    /**
     * 获取webSocket数据
     * @param data
     */
    void onSuccess(String data);

    /**
     * 失败了
     * @param reason
     */
    void onFailed(String reason);

}
