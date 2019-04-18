package com.elita.studydemo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.elita.studydemo.ContextUtil;
import com.elita.studydemo.ElitaLogUtils;
import com.elita.studydemo.ElitaUtils;
import com.elita.studydemo.service.inter.IRequestDataListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author nie yunlong
 * @description 消息处理 分发
 * @date 2018/6/20
 */
public class ElitaMessageController {

    private static ElitaMessageController instance;

    /**
     * 请求队列
     */
    private List<IRequestDataListener> mListRequestDataListener = new CopyOnWriteArrayList<>();

    public ElitaMessageController(Context context) {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ElitaMessageService.ACTION_RECEIVE_MESSAGE_ON_FAILED);
        mFilter.addAction(ElitaMessageService.ACTION_RECEIVE_MESSAGE_SUCCESS);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && intent.getAction() != null) {

                    switch (intent.getAction()) {
                        case ElitaMessageService.ACTION_RECEIVE_MESSAGE_SUCCESS:
                            String unzipStr = ElitaUtils.unzip(intent
                                    .getStringExtra(ElitaMessageService.INTENT_KEY_MESSAEGE));
                            ElitaLogUtils.e("解压数据--->" + unzipStr);
                            if (!TextUtils.isEmpty(unzipStr)) {
                                receivedMessage(unzipStr);
                            }

                            break;
                        case ElitaMessageService.ACTION_RECEIVE_MESSAGE_ON_FAILED:
                            iteratorOnFailedListener(intent
                                    .getStringExtra(ElitaMessageService.INTENT_KEY_ON_FAILED));
                            break;
                        default:
                            break;
                    }
                }

            }

        }, mFilter);

    }

    /**
     * 获取对象的实例
     *
     * @return
     */
    public static ElitaMessageController getInstance() {
        if (instance == null) {
            instance = new ElitaMessageController(ContextUtil.getContext());
        }
        return instance;
    }

    /**
     * 接收数据 ws
     *
     * @param content
     */
    private void receivedMessage(String content) {
        iteratorListener(content);
    }

    /**
     * 添加回调
     *
     * @param requestDataListener
     */
    public void addRequestDataListener(IRequestDataListener requestDataListener) {
        if (mListRequestDataListener != null && !mListRequestDataListener.contains(requestDataListener)) {
            mListRequestDataListener.add(requestDataListener);
        }
    }

    /**
     * 移除请求
     *
     * @param requestDataListener
     */
    public void removeRequestDataListener(IRequestDataListener requestDataListener) {
        if (mListRequestDataListener != null && mListRequestDataListener.contains(requestDataListener)) {
            mListRequestDataListener.remove(requestDataListener);
        }

    }

    /**
     * 数据分发
     *
     * @param json
     */
    private synchronized void iteratorListener(String json) {
        if (mListRequestDataListener.size() > 0) {
            for (IRequestDataListener requestDataListener : mListRequestDataListener) {

                requestDataListener.onSuccess(json);


            }
        }
    }

    /**
     * 数据分发 失败了
     *
     * @param
     */
    private void iteratorOnFailedListener(String reason) {
        if (mListRequestDataListener.size() > 0) {
            for (IRequestDataListener requestDataListener : mListRequestDataListener) {
                requestDataListener.onFailed(reason);
            }
        }

    }

    public void iteratorListenerLog() {
        if (mListRequestDataListener.size() > 0) {
            for (IRequestDataListener requestDataListener : mListRequestDataListener) {
                ElitaLogUtils.e("===>iteratorListenerLog" + requestDataListener.toString());

            }
        }
    }





}
