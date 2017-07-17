/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.free.lib.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.StrictMode;


/**
 * Class containing some static utility methods.
 */
public class SDKUtil
{
    private SDKUtil()
    {
    }

    @TargetApi(VERSION_CODES.HONEYCOMB)
    public static void enableStrictMode()
    {
        if (SDKUtil.hasGingerbread())
        {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            if (SDKUtil.hasHoneycomb())
            {
                threadPolicyBuilder.penaltyFlashScreen();
                //设置指定activity的实例最大个数
//                vmPolicyBuilder
//                        .setClassInstanceLimit(ImageGridActivity.class, 1)
//                        .setClassInstanceLimit(ImageDetailActivity.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }

    /**
     * android sdk 2.2 or above
     *
     * @return
     */
    public static boolean hasFroyo()
    {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;
    }

    /**
     * android sdk 2.3 or above
     *
     * @return
     */
    public static boolean hasGingerbread()
    {
        return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }

    /**
     * android sdk 3.0 or above
     *
     * @return
     */
    public static boolean hasHoneycomb()
    {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    }

    /**
     * android sdk 3.1 or above
     *
     * @return
     */
    public static boolean hasHoneycombMR1()
    {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * android sdk 4.0 or above
     *
     * @return
     */
    public static boolean hasIcecreamSandwich()
    {
        return Build.VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * android sdk Android 4.0.3
     *
     * @return
     */
    public static boolean hasIcecreamSandwichMr1()
    {
        return Build.VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }


    /**
     * android sdk 4.2 or above
     *
     * @return
     */
    public static boolean hasJellyBeanMr1()
    {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * android sdk 4.1 or above
     *
     * @return
     */
    public static boolean hasJellyBean()
    {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

    /**
     * android sdk 4.3 or above
     *
     * @return
     */
    public static boolean hasJellyBeanMr2()
    {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2;
    }

    /**
     * android sdk 4.4 or above
     *
     * @return
     */
    public static boolean hasKitKat()
    {
        return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
    }


    /**
     * android sdk 5.0 or above
     *
     * @return
     */
    public static boolean hasLOLLIPOP()
    {
        return Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP;
    }


    /**
     * android sdk 6.0 or above
     *
     * @return
     */
    public static boolean hasM()
    {
        return Build.VERSION.SDK_INT >= VERSION_CODES.M;
    }

    /**
     * android sdk 7.0 or above
     *
     * @return
     */
    public static boolean hasN()
    {
        return Build.VERSION.SDK_INT >= VERSION_CODES.N;
    }

}
