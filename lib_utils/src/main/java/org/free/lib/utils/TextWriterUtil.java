package org.free.lib.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Disco on 2015/4/6.
 */
public class TextWriterUtil
{

    private static void makeDir(String path)
    {
        File file = new File(path);

        if(file.exists())
        {
            file.delete();
        }
        else
        {
            File dir = file.getParentFile();
            if(null != dir && !dir.exists())
            {
                dir.mkdirs();
            }
        }

    }
    /**
     * 通过FileWriter写入
     * @param filePath
     * @param data
     * @param isAppend
     * @return
     */
    public static boolean writeString1(String filePath,String data,boolean isAppend)
    {
        makeDir(filePath);
        FileWriter fileWriter = null;
        boolean isOk = false;
        try
        {
            fileWriter = new FileWriter(filePath,isAppend);
            fileWriter.write(data);
            fileWriter.flush();
            fileWriter.close();
            isOk = true;
        }
        catch (FileNotFoundException e)
        {
            Tracer.printStackTrace(e);
        }
        catch (IOException e)
        {
            Tracer.printStackTrace(e);
        }
        return isOk;
    }

    /**
     *通过BufferedWriter写入
     * @param filePath
     * @param data
     * @param isAppend
     * @return
     */
    public static boolean writeString2(String filePath,String data,boolean isAppend)
    {
        makeDir(filePath);
        BufferedWriter bufferedWriter = null;
        boolean isOk = false;
        try
        {
            bufferedWriter = new BufferedWriter(new FileWriter(filePath,isAppend));
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            isOk = true;
        }
        catch (FileNotFoundException e)
        {
            Tracer.printStackTrace(e);
        }
        catch (IOException e)
        {
            Tracer.printStackTrace(e);
        }
        return isOk;
    }

    /**
     *通过BufferedOutputStream写入
     * @param filePath
     * @param data
     * @param isAppend
     * @return
     */
    public static boolean writeString3(String filePath,String data,boolean isAppend)
    {
        makeDir(filePath);
        BufferedOutputStream bo = null;

        boolean isOk = false;
        try
        {
            bo = new BufferedOutputStream(new FileOutputStream(filePath,isAppend));
            bo.write(data.getBytes());
            bo.flush();
            bo.close();
            isOk = true;
        }
        catch (FileNotFoundException e)
        {
            Tracer.printStackTrace(e);
        }
        catch (IOException e)
        {
            Tracer.printStackTrace(e);
        }
        return isOk;
    }

    /**
     * 通过MappedByteBuffer写入
     * @param filePath
     * @param data
     * @param isAppend
     * @return
     */
    public static boolean writeString4(String filePath,String data,boolean isAppend)
    {
        makeDir(filePath);
        FileChannel fileChannel = null;
        MappedByteBuffer byteBuffer = null;
        RandomAccessFile randomAccessFile = null;
        boolean isOk = false;
        try
        {
            randomAccessFile = new RandomAccessFile(filePath,"rw");
            byte[] bytes = data.getBytes();
            fileChannel = randomAccessFile.getChannel();
            byteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE,0,bytes.length);
            byteBuffer.put(bytes);
            fileChannel.close();
            randomAccessFile.close();
            isOk = true;
        }
        catch (FileNotFoundException e)
        {
            Tracer.printStackTrace(e);
        }
        catch (IOException e)
        {
            Tracer.printStackTrace(e);
        }
        return isOk;
    }
}
