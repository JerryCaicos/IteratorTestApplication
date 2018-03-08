package com.yg.mobile.iteratort.app.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by JerryCaicos on 2017/1/18.
 */

public class LogUtils
{
    private static final String TAG = "LogUtils";

    private static final String LINE = "LogUtils----";


    public static final boolean enableLog = true;

    public static void i(String msg)
    {
        if(enableLog && !TextUtils.isEmpty(msg))
        {
            Log.i(TAG, getMsg(msg));
        }
    }

    /**
     * @Description:
     * @see Log#i(String, String)
     */
    public static void i(Object object,String msg)
    {
        if(enableLog && msg != null)
        {
            Log.i(getTag(object),msg);
        }
    }

    public static void v(String msg)
    {
        if(enableLog && !TextUtils.isEmpty(msg))
        {
            Log.v(TAG, getMsg(msg));
        }
    }

    public static void d(String msg)
    {
        if(enableLog && !TextUtils.isEmpty(msg))
        {
            Log.d(TAG, getMsg(msg));
        }
    }

    public static void d(String tag, String msg)
    {
        if(enableLog && !TextUtils.isEmpty(msg))
        {
            Log.d(tag, getMsg(msg));
        }
    }

    public static void w(String msg)
    {
        if(enableLog && !TextUtils.isEmpty(msg))
        {
            Log.w(TAG, getMsg(msg));
        }
    }

    public static void e(Object object, Throwable error)
    {
        Log.e(getTag(object), getMsg(""), filterThrowable(error));
    }

    private static Throwable filterThrowable(Throwable error)
    {
        StackTraceElement[] stackTraceElements = error.getStackTrace();
        error.setStackTrace(new StackTraceElement[]{stackTraceElements[0]});
        return error;
    }

    private static String getTag(Object object)
    {
        if(object == null)
        {
            return TAG;
        }
        String className = object.getClass().getSimpleName();
        if("String".equals(className))
        {
            return object.toString();
        }
        return className;
    }

    private static String getMsg(String msg)
    {
        return LINE + msg;
    }
}
