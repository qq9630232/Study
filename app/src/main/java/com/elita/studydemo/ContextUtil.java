package com.elita.studydemo;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ContextUtil {
    private static final String TAG = ContextUtil.class.getSimpleName();
    private static Context mContext;
    private static int activityVisiableCount ;
    public static boolean ignoreMobile;

    public ContextUtil() {

    }

    public static Context getContext() {
        if (mContext == null) {
            ElitaLogUtils.e(TAG, "context is not null");
            throw new NullPointerException("please init set ElitaSdkApi.get(application)");
        }

        return mContext;
    }

    public static void setContext(Context context) {
        mContext = context.getApplicationContext();
    }

    public static final String getPackageName() {
        return null == mContext ? "" : mContext.getPackageName();
    }

    public static void hideKeyboard(Activity context) {
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity context, EditText editText) {
//        View view = context.getCurrentFocus();
//        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
            imm.showSoftInput(editText, 0);

//        }

    }

    /*
    * 是否在前台
    * */
    public static boolean isFront(){
        ElitaLogUtils.w("是否在前台："+(activityVisiableCount>0));
        return activityVisiableCount>0;
    }
    /*
    * 设置App是否在前台
    * */
    public static void setAppFront(boolean isFront){
        if(isFront){
            activityVisiableCount ++;

        }else if(activityVisiableCount>0){
            activityVisiableCount--;

        }
    }
}
