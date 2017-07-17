package org.free.lib.utils;

import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * <b>Created by Disco for Video.</b>
 * <br><b>Version:</b>
 * <br><b>Profile:</b>
 * <br><b>Date:</b> 2016/11/30.
 * <br><b>Email:</b>dike_doit@163.com.
 */

public class Zip
{

    public interface ZipObserver
    {
         void onUpdate(float percent);
    }

    /**
     * 解压zip文件到指定的目录
     * @param zipFilePath 需要解压的文件路径+文件名
     * @param outFileDir 需要解压到的目录
     * @param outFolderName 需要解压到的文件夹名称.如果为null将使用zip文件的文件名作为解压后的文件夹
     * @param observer          进度观察者
     * @throws Exception
     */
    public static String unZip(String zipFilePath, String outFileDir,String outFolderName,ZipObserver observer) throws Exception
    {
        File zipFile = new File(zipFilePath);
        long totalSize = zipFile.length();
        long unzipSize = 0;
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFilePath));
        BufferedInputStream bufIn = new BufferedInputStream(inZip);
        ZipEntry zipEntry;
        String szName = "";
        int lastSepIndex = zipFilePath.lastIndexOf(File.separator);
        int extIndex = zipFilePath.lastIndexOf(".");
        String folderName = TextUtils.isEmpty(outFolderName) ? zipFilePath.substring(lastSepIndex+1,extIndex) : outFolderName;
        while ((zipEntry = inZip.getNextEntry()) != null)
        {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory())
            {
                File folder = new File(outFileDir + File.separator + folderName + File.separator + szName);
                folder.mkdirs();
            }
            else
            {

                File file = new File(outFileDir + File.separator + folderName + File.separator + szName);
                // get the output stream of the file
                file.getParentFile().mkdirs();
                FileOutputStream fout = new FileOutputStream(file);
                BufferedOutputStream bufout = new BufferedOutputStream(fout);
                int len;
                byte[] buffer = new byte[4 * 1024];
                // read (len) bytes into buffer
                while ((len = bufIn.read(buffer)) != -1)
                {
                    // write (len) byte from buffer at the position 0
                    bufout.write(buffer, 0, len);
                }

                inZip.closeEntry();
                bufout.close();
                fout.close();

                //add progress observer
                unzipSize += zipEntry.getCompressedSize();
                if(null != observer)
                {
                    observer.onUpdate(unzipSize * 1.0f / totalSize);
                }
            }

        }
        bufIn.close();
        inZip.close();
        //add progress observer
        if(null != observer)
        {
            observer.onUpdate(1.0f);
        }

        return outFileDir + File.separator + folderName + File.separator;
    }
}
