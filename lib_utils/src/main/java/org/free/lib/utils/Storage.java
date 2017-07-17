package org.free.lib.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author disco
 */
public class Storage
{

    public static final int PHONE_STORAGE = 0;
    public static final int INNER_SDCARD = 1;
    public static final int OUTER_SDCARD = 2;

    /**
     * sdcard根目录
     */
    private String sdRootPath;

    /**
     * 手机内存根目录
     */
    private String sysRootPath;

    /**
     * sdcard的总大小
     */
    public long sdTotalSpace;

    /**
     * sdcard可用大小
     */
    public long sdAvailabelSpace;

    /**
     * 手机内存的总大小
     */
    public long sysTotalSpace;

    /**
     * 手机内存可用大小
     */
    public long sysAvailabelSpace;


    /**
     * sdcard 是否可用
     */
    public boolean isSdCardAvailble;

    /**
     * sdcard 是否可写
     */
    public boolean isWrite;

    /**
     * sdcard是否可读
     */
    public boolean isRead;

    private static Storage mInstance;

    private Map<Integer,String> mExternalDirMap;
    private BroadcastReceiver iExternalStorageReceiver;
    private Context mContext;

    private Storage(Context context)
    {
        mContext = context;
        updateExternalStorageState();
//        startWatchingExternalStorage();
    }

    public static Storage getInstance(Context context)
    {
        if (null == mInstance)
        {
            mInstance = new Storage(context);
        }
        return mInstance;
    }

    private void startWatchingExternalStorage()
    {
        if(null == iExternalStorageReceiver)
        {
            iExternalStorageReceiver = new BroadcastReceiver()
            {
                @Override
                public void onReceive(Context arg0, Intent arg1)
                {
                    updateExternalStorageState();
                }
            };
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addDataScheme("file");
        mContext.registerReceiver(iExternalStorageReceiver, filter);
        updateExternalStorageState();
    }

    private Map<Integer,String> getExternalRootDir()
    {
        Map<Integer,String> externalRootDirMap = new HashMap<>();
        int i = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
        {
            File[] files = mContext.getExternalFilesDirs(null);
            if(null != files)
            {
                String fullPath;
                for(File file : files)
                {
                    if(null == file)
                    {
                        continue;
                    }
                    fullPath = file.getAbsolutePath();
                    externalRootDirMap.put(i,fullPath.substring(0,fullPath.indexOf("/Android/data")));
                    i++;
                }
            }

        }
        else
        {
            File file = mContext.getExternalFilesDir(null);
            if(null != file)
            {
                String fullPath = file.getAbsolutePath();
                externalRootDirMap.put(i,fullPath.substring(0,fullPath.indexOf("/Android/data")));
            }

        }
        return externalRootDirMap;
    }

    private void stopWatchingExternalStorage()
    {
        mContext.unregisterReceiver(iExternalStorageReceiver);
    }

    private void updateExternalStorageState()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
        {
            isSdCardAvailble = isWrite = isRead = true;
        }
        else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
        {
            isSdCardAvailble = true;
            isRead = true;
            isWrite = false;
        }
        else
        {
            isSdCardAvailble = isRead = isWrite = false;
        }
        mExternalDirMap = getExternalRootDir();
        sysTotalSpace = readSystemTotalSize();
        sysAvailabelSpace = readSystemAvailaleSize();
        sysRootPath = Environment.getDataDirectory().getAbsolutePath();
        if (isSdCardAvailble)
        {
            sdTotalSpace = readSDCardTotalSize();
            sdAvailabelSpace = readSDCardAvailaleSize();
            sdRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        else
        {
            sdRootPath = null;
            sdTotalSpace = sdAvailabelSpace = 0;
        }

    }

    public String getExternalPath(int index)
    {
        return mExternalDirMap.get(index);
    }


    public String getSafeRootPath()
    {
        if (isSdCardAvailble && !TextUtils.isEmpty(sdRootPath))
        {
            return sdRootPath;
        }
        return sysRootPath;
    }

    /**
     * 获取sdcard剩余可用空间
     *
     * @return
     */
    private long readSDCardAvailaleSize()
    {
        File path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
        StatFs stat = null;
        try
        {
            stat = new StatFs(path.getPath());
            long blockSize = SDKUtil.hasJellyBeanMr2() ? stat.getBlockSizeLong() :
                    stat.getBlockSize();
            long availableBlocks = SDKUtil.hasJellyBeanMr2() ? stat.getAvailableBlocksLong() :
                    stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        }
        catch (Exception e)
        {
            Tracer.printStackTrace(e);
            return -1;
        }

    }

    /**
     * 获取sdcard总空间
     *
     * @return
     */
    private long readSDCardTotalSize()
    {
        File sdcardDir = Environment.getExternalStorageDirectory();
        StatFs stat = null;
        try
        {
            stat = new StatFs(sdcardDir.getPath());
            long blockSize = SDKUtil.hasJellyBeanMr2() ? stat.getBlockSizeLong() :
                    stat.getBlockSize();
            long blockCount = SDKUtil.hasJellyBeanMr2() ? stat.getBlockCountLong() :
                    stat.getBlockCount();
            return blockCount * blockSize;
        }
        catch (Exception e)
        {
            Tracer.printStackTrace(e);
            return -1;
        }
    }

    /**
     * 获取手机内存的剩余可用空间
     *
     * @return
     */
    private long readSystemAvailaleSize()
    {

        File root = Environment.getRootDirectory();
        StatFs stat = null;
        try
        {
            stat = new StatFs(root.getPath());
            long blockSize = SDKUtil.hasJellyBeanMr2() ? stat.getBlockSizeLong() :
                    stat.getBlockSize();
            long availCount = SDKUtil.hasJellyBeanMr2() ? stat.getAvailableBlocksLong() :
                    stat.getAvailableBlocks();
            return availCount * blockSize;
        }
        catch (Exception e)
        {
            Tracer.printStackTrace(e);
            return -1;
        }

    }

    /**
     * 获取手机内存的总大小
     *
     * @return
     */
    private long readSystemTotalSize()
    {
        File root = Environment.getRootDirectory();
        StatFs stat = null;
        try
        {
            stat = new StatFs(root.getPath());
            long blockSize = SDKUtil.hasJellyBeanMr2() ? stat.getBlockSizeLong() :
                    stat.getBlockSize();
            long blockCount = SDKUtil.hasJellyBeanMr2() ? stat.getBlockCountLong() :
                    stat.getBlockCount();
            return blockCount * blockSize;
        }
        catch (Exception e)
        {
            Tracer.printStackTrace(e);
            return -1;
        }
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append("[************storage**********\n");
        builder.append("       sdRootPath=" + sdRootPath + "\n");
        builder.append("     sysdRootPath=" + sysRootPath + "\n");
        builder.append("     sdTotalSpace=" + sdTotalSpace + "\n");
        builder.append(" sdAvailabelSpace=" + sdAvailabelSpace + "\n");
        builder.append("    sysTotalSpace=" + sdTotalSpace + "\n");
        builder.append("sysAvailabelSpace=" + sdAvailabelSpace + "\n");
        builder.append("          isWrite=" + isWrite + "\n");
        builder.append("           isRead=" + isRead + "\n");
        builder.append("*****************************]");

        return builder.toString();
    }

}
