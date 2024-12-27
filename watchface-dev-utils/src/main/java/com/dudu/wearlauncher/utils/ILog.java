package com.lazytong.launcher.utils;
import android.util.Log;

public class ILog {
    /*
    *规范log从我做起
    **/
    
    public static void v(String msg) {
    	Log.v(getCallerName(),msg);
    }
    
    public static void d(String msg) {
    	Log.d(getCallerName(),msg);
    }

    public static void i(String msg) {
    	Log.i(getCallerName(),msg);
    }
    
    public static void w(String msg) {
    	Log.w(getCallerName(),msg);
    }
    
    public static void e(String msg) {
    	Log.e(getCallerName(),msg);
    }
    
    private static String getCallerName() {
        //0 VMStack.getThreadStackTrace
        //1 Thread.getStackTrace
        //2 LogUtil.getCallerInfo
        //3 LogUtil.e
        //4 Caller
    	StackTraceElement st = Thread.currentThread().getStackTrace()[4];
        return st.getClassName() + "." +/*st.getMethodName()+*/"[Line:" + st.getLineNumber() + "]";
    }
    
}
