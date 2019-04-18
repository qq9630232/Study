package com.elita.studydemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.elita.studydemo.ContextUtil;
import com.elita.studydemo.ElitaLogUtils;
import com.elita.studydemo.ElitaUtils;
import com.elita.studydemo.config.ApiEndPoint;
import com.elita.studydemo.network.NetWorkReceiverUtils;
import com.elita.studydemo.network.NetWorkUtils;
import com.elita.studydemo.service.inter.IWsManage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * @author nie yunlong
 * @description 网络请求service
 * @date 2018/6/19
 */
public class ElitaMessageService extends Service implements IWsManage, NetWorkReceiverUtils.ObserverNetWork {
    private static final String TAG = "ElitaMessageService";
    private RequestBinder requestBinder = new RequestBinder();
    /**
     * 发送请求句柄
     */
    private WebSocket mWebSocket;
    /**
     * webSocket
     */
    private EchoWebSocketListener listener;
    /**
     *
     */
    private OkHttpClient.Builder okBuilder;

    private OkHttpClient client;

    public static final String ACTION_RECEIVE_MESSAGE_SUCCESS = "com.yiche.elita_lib.MESSAGE";
    /**
     * 广播action
     */
    public static final String ACTION_RECEIVE_MESSAGE_ON_FAILED = "com.yiche.elita_lib.MESSAGE.Failed";
    /**
     * socket 连接成功
     */
    public static final String ACTION_CONNECT_SOCKET_SUCCESS = "com.yiche.elita_lib.MESSAGE.Socket_SUCCESS";


    public static final String INTENT_KEY_MESSAEGE = "message";
    /**
     * 获取数据 失败
     */
    public static final String INTENT_KEY_ON_FAILED = "elita_get_data_failed";

//    ElitaMessageController messageController = ElitaMessageController.getInstance();

    private final static int RECONNECT_INTERVAL = 10 * 1000;    //重连自增步长
    private final static long RECONNECT_MAX_TIME = 120 * 1000;   //最大重连间隔

    private int reconnectCount = 0;   //重连次数

    private boolean isManualClose = false;         //是否为手动关闭websocket连接

    private Handler wsMainHandler = new Handler(Looper.getMainLooper());

    private boolean isConnecting = false;

    private List<String> mJsonList = new CopyOnWriteArrayList<>();

    private Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            ElitaLogUtils.e("===>尝试重新连接");
            startConnectWs();
        }
    };
    private String mJson ;
    @Override
    public void onCreate() {
        super.onCreate();
        NetWorkReceiverUtils.getInstance().registerNetWorkReceiver(this);
        NetWorkReceiverUtils.getInstance().addNetWorkObserver(this);
        //网络连接上就会 开始连接websocket
        ElitaLogUtils.w(TAG,"服务onCreate");
        startConnectWs();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return requestBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDisconnect() {
        ElitaLogUtils.e("是否在前台："+ ContextUtil.isFront());
        if(ContextUtil.isFront()){

//            ToastUtils.showLongToast(getApplicationContext(), R.string.elita_net_bad);

        }
        ElitaLogUtils.e("--->网络断开连接");
        Intent intent = new Intent();
        intent.setAction(ACTION_RECEIVE_MESSAGE_ON_FAILED);
        intent.putExtra(INTENT_KEY_ON_FAILED, "网络连接失败");
        sendBroadcast(intent);
    }

    @Override
    public void onConnect(NetWorkUtils.NetWorkStatus type) {
        ElitaLogUtils.e("--->网络已经连接");

        startConnectWs();
    }

    public class RequestBinder extends Binder {

        public RequestBinder() {

        }

        public ElitaMessageService getService() {
            return ElitaMessageService.this;
        }
    }


    private class EchoWebSocketListener extends WebSocketListener {


        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            ElitaLogUtils.e("--->websocket response connect success");
            mWebSocket = webSocket;
            isConnecting = false;

            cancelReconnect();
            if(mJsonList.size()>0){
                for (String json:mJsonList){
                    write(json);
                    mJsonList.remove(json);

                }
            }
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            ElitaLogUtils.e("--->websocket response" + text + ",sessionId==>请求文本");
            String compareWsResponseData = ElitaUtils.zip(text);
            if (TextUtils.isEmpty(compareWsResponseData)) {
                return;
            }
            mWebSocket = webSocket;

            ElitaLogUtils.e("===>compareWsResponseData****\n" + compareWsResponseData);
            Intent intent = new Intent();
            intent.setAction(ACTION_RECEIVE_MESSAGE_SUCCESS);
            intent.putExtra(INTENT_KEY_MESSAEGE, compareWsResponseData);
            sendBroadcast(intent);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
            mWebSocket = null;
            isConnecting = false;

            ElitaLogUtils.e("--->websocket onClosing==》onClosing" + reason);
            startConnectWs();
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            mWebSocket = null;
            isConnecting = false;

            ElitaLogUtils.e("--->websocket onFailure==》onClosed" + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
//            if(mWebSocket!=null){
//                boolean isCloseSuccess=mWebSocket.close(111,"");
//                ElitaLogUtils.e("--->websocket onFailure isCloseSuccess" + isCloseSuccess);
//            }
            mWebSocket = null;
            isConnecting = false;
//            onReconnect();

            if(!NetWorkUtils.isNetWorkConnect(ElitaMessageService.this)){
                return;
            }
            ElitaLogUtils.e("--->websocket onFailure" + t.getMessage());
//            NetWorkUtils.NetWorkStatus net_type = NetWorkUtils.getNetWorkStatus(ElitaMessageService.this);
//            if (net_type != NetWorkUtils.NetWorkStatus.NETWORK_ERROR) {
//                return;
//            }
            Intent intent = new Intent();
            intent.setAction(ACTION_RECEIVE_MESSAGE_ON_FAILED);
            intent.putExtra(INTENT_KEY_ON_FAILED, t.getMessage());
            sendBroadcast(intent);
        }


    }


    @Override
    public WebSocket getWebSocket() {
        return mWebSocket;
    }

    @Override
    public synchronized void startConnectWs() {
        isManualClose = false;

        if (!NetWorkUtils.isNetWorkConnect(this)) {
            ElitaLogUtils.w("--->网络没有连接-->不进行尝试");
            cancelReconnect();
            return;
        }
        isConnecting = true;
        ElitaLogUtils.e("===>webSocket->add"+ApiEndPoint.BASE_HOST_ADDRESS);
        listener = new EchoWebSocketListener();
        Request request = new Request.Builder()
                .url(ApiEndPoint.BASE_HOST_ADDRESS)
                .removeHeader("User-Agent")
                .addHeader("User-Agent",getUserAgent())
                .build();
        okBuilder = new OkHttpClient.Builder();
        okBuilder.readTimeout(100, TimeUnit.SECONDS);
        okBuilder.connectTimeout(100, TimeUnit.SECONDS);
        okBuilder.retryOnConnectionFailure(true);
        client = okBuilder.build();

        client.dispatcher().cancelAll();
        client.newWebSocket(request, listener);
    }
    /**
     * 返回正确的UserAgent
     * @return
     */
    private   String getUserAgent(){
        String userAgent = "";
        StringBuffer sb = new StringBuffer();
        userAgent = System.getProperty("http.agent");//Dalvik/2.1.0 (Linux; U; Android 6.0.1; vivo X9L Build/MMB29M)

        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }

        ElitaLogUtils.w("User-Agent","User-Agent: "+ sb.toString());
        return sb.toString();
    }

    @Override
    public void disconnectWs() {
        isManualClose = true;
        if (mWebSocket != null) {
            mWebSocket.cancel();
        }
        cancelReconnect();
        ElitaLogUtils.w("销毁了服务");

    }


    @Override
    public void onReconnect() {
        if (isManualClose) { //是手动断开 不会重新尝试重连
            return;
        }
        if (!NetWorkUtils.isNetWorkConnect(this)||!ContextUtil.isFront()) {
            ElitaLogUtils.w("--->网络没有连接-->不进行尝试");
            return;
        }
        ElitaLogUtils.w("--->"+!NetWorkUtils.isNetWorkConnect(this));
        ElitaLogUtils.w("--->"+!ContextUtil.isFront());

        //TODO 判断一下网络 没有网络不进行尝试连接
        long delay = reconnectCount * RECONNECT_INTERVAL;

        ElitaLogUtils.w("--->网络没有连接-->不进行尝试"+(delay > RECONNECT_MAX_TIME ? RECONNECT_MAX_TIME : delay));

        wsMainHandler
                .postDelayed(reconnectRunnable, delay > RECONNECT_MAX_TIME ? RECONNECT_MAX_TIME : delay);
        reconnectCount++;
    }


    /**
     * 取消尝试
     */
    private void cancelReconnect() {
        wsMainHandler.removeCallbacks(reconnectRunnable);
        reconnectCount = 0;
    }


    @Override
    public void write(String json) {
        ElitaLogUtils.w("==>:Request"+json);

        if(json==null){
            return;
        }
        if(mWebSocket!=null){
            mWebSocket.send(json);
        }else{
            if (!NetWorkUtils.isNetWorkConnect(this)) {
                ElitaLogUtils.w("--->网络没有连接-->不进行添加Json");
                Intent intent = new Intent();
                intent.setAction(ACTION_RECEIVE_MESSAGE_ON_FAILED);
                intent.putExtra(INTENT_KEY_ON_FAILED, "当前无网络");
                sendBroadcast(intent);
                return;
            }

//            Intent intent = new Intent();
//            intent.setAction(ACTION_RECEIVE_MESSAGE_ON_FAILED);
//            intent.putExtra(INTENT_KEY_ON_FAILED, "网络状态不好");
//            sendBroadcast(intent);
            onReconnect();
            mJsonList.add(json);
        }


    }


    public void startWork() {

    }

    public void stopWork() {

    }

    public void stopService() {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ElitaLogUtils.e("---->service 停止");
        ElitaLogUtils.w("停止了服务");

    }

    public void onPause(){
        disconnectWs();
    }

    public void onResume(){
        if(getWebSocket()==null&&!isConnecting){
            startConnectWs();
            ElitaLogUtils.w("ElitaBaseActivity","webSocket不存在---->");

        }else {
            ElitaLogUtils.w("ElitaBaseActivity","webSocket存在---->");
        }
    }


}
