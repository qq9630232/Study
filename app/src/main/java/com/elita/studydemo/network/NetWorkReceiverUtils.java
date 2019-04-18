package com.elita.studydemo.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.elita.studydemo.ContextUtil;
import com.elita.studydemo.ElitaLogUtils;


/**
 * Created by NYL for RoundTable
 * 监听网络变化
 * Date: 2015/12/21
 */
public class NetWorkReceiverUtils {
    /**
     * 网络
     */
    private boolean isNetwork = false;
    private Context context;
    private static NetWorkReceiverUtils networkUtils;
    private MyNetWorkReceiver myNetWorkReceiver;

    private ObserverNetWork observerNetWork;

    public static NetWorkReceiverUtils getInstance() {
        if (networkUtils == null) {
            networkUtils = new NetWorkReceiverUtils();
        }
        return networkUtils;
    }

    public boolean getNetwork() {
        if (context != null) {
            isNetwork = NetWorkUtils.isNetWorkConnect(context);
        }
        return isNetwork;
    }

    public NetWorkUtils.NetWorkStatus getNetWorkStatus() {
        if (context != null) {
            return NetWorkUtils.getNetWorkStatus(context);
        }
        return NetWorkUtils.NetWorkStatus.NETWORK_ERROR;
    }

    public void addNetWorkObserver(ObserverNetWork observerNetWork) {
        this.observerNetWork = observerNetWork;
    }

    public void registerNetWorkReceiver(Context context) {
        ElitaLogUtils.w("--------register network");
        this.context = context;
        IntentFilter mFilter = new IntentFilter();
        myNetWorkReceiver = new MyNetWorkReceiver();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(myNetWorkReceiver, mFilter);
    }

    class MyNetWorkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)&&action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                NetWorkUtils.NetWorkStatus net_type = NetWorkUtils.getNetWorkStatus(context);
                if (net_type != NetWorkUtils.NetWorkStatus.NETWORK_ERROR) { //有网络
                    if (observerNetWork != null&& ContextUtil.isFront()) {
                        observerNetWork.onConnect(net_type);
                    }
                } else {
                    if (observerNetWork != null) {
                        ElitaLogUtils.w("断开连接了!!!!!!");
                        observerNetWork.onDisconnect();
                    }

                }
            }
        }
    }


    /**
     * 注销网络广播
     */
    public void ungisterNetWork() {
        if (myNetWorkReceiver != null) {
            context.unregisterReceiver(myNetWorkReceiver);
        }
        if (observerNetWork != null) {
            observerNetWork = null;
        }
    }

    public interface ObserverNetWork {
        public void onDisconnect();

        public void onConnect(NetWorkUtils.NetWorkStatus type);
    }
}
