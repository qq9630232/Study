package com.elita.studydemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.elita.studydemo.service.ElitaMessageController;
import com.elita.studydemo.service.ServiceProxy;
import com.elita.studydemo.service.inter.IRequestDataListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Hello World!
     */
    private TextView mSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        ElitaMessageController.getInstance().addRequestDataListener(listener);

        try {
            Class a = Class.forName("com.elita.studydemo.School");
            Method[] methods = a.getMethods();
            Field[] fields = a.getFields();
            for (Field field : fields) {
                Log.e("zxz", "field is :" + field.getName());

            }
//            try {
//                Method run = a.getMethod("run", a);
//                Log.e("zxz","method is :"+run.getName());
//
//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            }
//            for (Method method : methods) {
//                Log.e("zxz","method is :"+method.getName());
//
//                Class<?>[] parameterTypes = method.getParameterTypes();
//                for (Class<?> parameterType : parameterTypes) {
//                    Log.e("zxz","parameterType is :"+parameterType.getName());
//
//                }
//
//            }
//            School school = (School) a.newInstance();
            Log.e("zxz", a.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    IRequestDataListener listener = new IRequestDataListener()

    {

        @Override
        public void onSuccess(String data) {

        }

        @Override
        public void onFailed(String reason) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ElitaMessageController.getInstance().removeRequestDataListener(listener);
    }

    public void sendRequestWs() {
        new RequestManager().write("{\n" +
                "    \"logId\":\"123\",\n" +
                "    \"caller\":{\n" +
                "        \"appId\":\"10037\",\n" +
                "        \"token\":\"cookie\"\n" +
                "    },\n" +
                "    \"user\":{\n" +
                "        \"uuid\":\"9986645666393488\",\n" +
                "        \"content\":\"智能选车\",\n" +
                "        \"channel\":\"3\",\n" +
                "    },\n" +
                "    \"parameters\":{\n" +
                "        \"cityId\":\"201\",\n" +
                "        \"sence\":\"0\",\n" +
                "        \"version\":\"8.3.1\",\n" +
                "        \"device\":\"iphone 6s\",\n" +
                "        \"system\":\"ios\",\n" +
                "        \"isRooted\":\"no\",\n" +
                "        \"senceDataType\":\"0\",\n" +
                "        \"abstractParam\":\"\",\n" +
                "        \"inputMode\":\"1001\",\n" +
                "        \"tagClassification\":\"100\"\n" +
                "    }\n" +
                "}");
    }

    private void initView() {
        mSend = (TextView) findViewById(R.id.send);
        mSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.send:
                sendRequestWs();

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ServiceProxy.getInstance()!=null){
            ServiceProxy.getInstance().onResumeWs();

        }    }
}
