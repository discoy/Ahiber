package org.free.lib.utils;

import android.util.Log;

/**
 * <b>Created by Disco for AppCenter.</b>
 * <br><b>Version:</b>
 * <br><b>Profile:</b>
 * <br><b>Date:</b> 2016/5/18.
 * <br><b>Email:</b>dike_doit@163.com.
 */
public class Tracer
{
    public static boolean IS_DEBUG = true;
    public static String TAG = "lib_Utils:";


    public static void println(String msg)
    {
        if(IS_DEBUG)
        {
            System.out.println(msg);
        }
    }

    public static void println(String tag,String msg)
    {
        if(IS_DEBUG)
        {
            System.out.println(tag+msg);
        }
    }

    public static void printStackTrace(Throwable error)
    {
        if(IS_DEBUG)
        {
            error.printStackTrace();
        }
    }

    public static void e(String msg)
    {
        e(TAG,msg);
    }

    public static void e(String tag,String error)
    {
        if(IS_DEBUG)
        {
            Log.e(TAG,error);
        }
    }

    public static void i(String tag,String info)
    {
        if(IS_DEBUG)
        {
            Log.i(tag,info);
        }
    }

    public static void i(String info)
    {
        i(TAG,info);
    }

    public static void w(String warn)
    {
        w(TAG,warn);
    }

    public static void w(String tag,String warn)
    {
        if(IS_DEBUG)
        {
            Log.w(tag,warn);
        }
    }

    public static void w(String tag, String msg, Throwable tr)
    {
        if(IS_DEBUG)
        {
            Log.w(tag,msg,tr);
        }
    }
}
