package org.free.lib.utils;

import android.content.Context;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil
{

    public static String getValidFileName(String fileName)
    {
        if (TextUtils.isEmpty(fileName))
        {
            return fileName;
        }
        String regEx = "[///:*?\"<>|]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(fileName);
        return m.replaceAll("").trim();
    }

    public static long getAvalibaleSpace(String path)
    {
        if (TextUtils.isEmpty(path))
        {
            return -1;
        }
        StatFs stat = null;
        try
        {
            stat = new StatFs(path);
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



    public static long getPathSize(String path)
    {
        if (TextUtils.isEmpty(path))
        {
            return 0;
        }
        File file = new File(path);
        if (!file.exists())
        {
            return 0;
        }
        if (file.isFile())
        {
            return file.length();
        }
        if (file.isDirectory())
        {
            long size = 0;
            for (File subFile : file.listFiles())
            {
                size += getPathSize(subFile.getPath());
            }
            return size;
        }
        return 0;
    }

    public static long getTotalSpace(String path)
    {
        if (TextUtils.isEmpty(path))
        {
            return -1;
        }
        StatFs stat = new StatFs(path);
        long blockSize = SDKUtil.hasJellyBeanMr2() ? stat.getBlockSizeLong() :
                stat.getBlockSize();
        long totalBlocks = SDKUtil.hasJellyBeanMr2() ? stat.getBlockCountLong() :
                stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public static String[] importImage(String path)
    {
        if (TextUtils.isEmpty(path))
        {
            return null;
        }
        File folder = new File(path);
        String[] files = folder.list();
        int length = files == null ? 0 : files.length;
        for (int i = 0; i < length; i++)
        {
            files[i] = path + files[i];
        }
        return files;
    }

    /**
     * 复制整个文件夹内容
     *
     * @param src 需要复制的原始文件
     * @param des 需要复制到的目标文件
     * @param checkDesExists 是否需要检查目标文件是否存在，如果为true，当目标文件不存在时，不会进行复制
     * @return boolean
     */
    public static boolean copyFile(File src, File des, boolean checkDesExists)
    {

        if (null == src || null == des)
        {
            return false;
        }
        if (!src.exists())
        {
            return false;
        }
        if (!src.isFile())
        {
            return false;
        }
        if (!src.canRead())
        {
            return false;
        }
        if (checkDesExists)
        {
            if (!des.exists())
            {
                return false;
            }
        }
        if (!des.getParentFile().exists())
        {
            des.getParentFile().mkdirs();
        }
        if (des.exists())
        {
            des.delete();
        }
        try
        {
            des.createNewFile();
            FileInputStream fosfrom = new FileInputStream(src);
            BufferedInputStream bufIn = new BufferedInputStream(fosfrom);
            FileOutputStream fosto = new FileOutputStream(des, false);
            BufferedOutputStream bufOut = new BufferedOutputStream(fosto);

            byte bt[] = new byte[8 * 1024];
            int c;
            while ((c = bufIn.read(bt)) != -1)
            {
                bufOut.write(bt, 0, c);
            }
            bufIn.close();
            bufOut.close();
            fosfrom.close();
            fosto.close();
        }
        catch (Exception ex)
        {
            Tracer.printStackTrace(ex);
            return false;
        }
        return true;

    }

    /**
     * 复制整个文件夹内容
     *
     * @param srcFile 需要复制的原始文件
     * @param desFile 需要复制到的目标文件
     * @return boolean
     */
    public boolean copyFileNio(File srcFile, File desFile)
    {

        if (null == srcFile || !srcFile.exists())
        {
            return false;
        }
        if (null == desFile || !desFile.exists())
        {
            return false;
        }
        try
        {
            FileInputStream fin = new FileInputStream(srcFile);
            FileOutputStream fout = new FileOutputStream(desFile);
            FileChannel fcin = fin.getChannel();
            FileChannel fcout = fout.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 8);

            while (true)
            {
                buffer.clear();
                int r = fcin.read(buffer);
                if (r == -1)
                {
                    break;
                }
                buffer.flip();
                fcout.write(buffer);
            }

            fcin.close();
            fcout.close();
            fin.close();
            fout.close();
            return true;
        }
        catch (FileNotFoundException e)
        {
            Tracer.printStackTrace(e);
            return false;
        }
        catch (IOException e)
        {
            Tracer.printStackTrace(e);
            return false;
        }


    }

    /**
     * Delete a folder include folder
     *
     * @param afolderDir
     * @return
     */
    public static boolean deleteFolder(String afolderDir)
    {
        if (TextUtils.isEmpty(afolderDir))
        {
            return false;
        }
        File vFolder = new File(afolderDir);
        if (vFolder.isDirectory())
        {
            return deleteFolder(vFolder, true);
        }

        return false;
    }

    /**
     * Delete a file no mater it's a file or folder
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath)
    {
        if (TextUtils.isEmpty(filePath))
        {
            return false;
        }

        return deleteFile(new File(filePath));
    }

    /**
     * Delete a file no mater it's a file or folder
     *
     * @param file
     * @return
     */
    public static boolean deleteFile(File file)
    {
        if (null == file || !file.exists())
        {
            return false;
        }
        if (file.isFile())
        {
            return file.delete();
        }
        else if (file.isDirectory())
        {
            return deleteFolder(file,true);
        }
        return false;
    }

    /**
     * Delete a folder
     *
     * @param aFolder
     * @param isDeleteSelf whether delete self
     * @return
     */
    public static boolean deleteFolder(File aFolder, boolean isDeleteSelf)
    {
        File[] Files = aFolder.listFiles();
        if (null == Files)
        {
            return true;
        }
        int vCount = Files.length;
        int vDelCount = 0;
        boolean vRet = false;
        for (File vFile : Files)
        {
            if (vFile.isFile())
            {
                vRet = vFile.delete();
            }
            else if (vFile.isDirectory())
            {
                vRet = deleteFolder(vFile, isDeleteSelf);
            }

            if (vRet)
            {
                vDelCount++;
            }
        }

        vRet = vDelCount == vCount;
        if (vRet && isDeleteSelf)
        {
            vRet = aFolder.delete(); //delete folder self when empty
        }
        return vRet;
    }

    public static boolean isFileExist(String path)
    {
        if(TextUtils.isEmpty(path))
        {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    public static void copyAssertJarToFile(Context context, String filename,
                                           String des)
    {
        try
        {

            File file = new File(des);
            if (file.exists())
            {
                return;
            }

            InputStream inputStream = context.getAssets().open(filename);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte buffer[] = new byte[1024*8];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1)
            {
                fileOutputStream.write(buffer, 0, len);
            }
            fileOutputStream.close();
            fileOutputStream.close();
        }
        catch (Exception e)
        {
            Tracer.printStackTrace(e);
        }
    }

}
