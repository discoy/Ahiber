package org.free.lib.utils;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TextReaderUtil
{

    public static String FILE_HEADER_ASSETS                 = "assets://";
    public static String FILE_HEADER_FILE                   = "file://";

    private static InputStream getInputStream(Context context,String fileName) throws IOException
    {
        if(TextUtils.isEmpty(fileName))
        {
            return null;
        }
        InputStream inputStream = null;
        if(fileName.startsWith(FILE_HEADER_ASSETS))
        {
            fileName = fileName.substring(FILE_HEADER_ASSETS.length());
            inputStream = context.getResources().getAssets().open(fileName);
        }
        else if(fileName.startsWith(FILE_HEADER_FILE))
        {
            fileName = fileName.substring(FILE_HEADER_FILE.length());
            if(FileUtil.isFileExist(fileName))
            {
                inputStream = new FileInputStream(fileName);
            }
        }
        else
        {
            inputStream = new FileInputStream(fileName);
        }
        return inputStream;
    }

    /**
     * 读取指定目录的文件内容。文件名格式：[header|path]，如[file://mnt/sdcard/test]
     * @param fileName
     * @return 文件内容
     * todo 字符编码问题
     */
	public static String readAllTextByFileName_v1(Context context,String fileName)
	{

        BufferedInputStream br = null;
		String content = null;
		try
		{
            InputStream inputStream = getInputStream(context,fileName);
            if(null != inputStream)
            {
                br = new BufferedInputStream(inputStream);
                byte[] buffer = new byte[1024 * 8];
                StringBuilder builder = new StringBuilder();
                int readLen = br.read(buffer, 0, buffer.length);
                while(readLen != -1)
                {
                    builder.append(new String(buffer,0,readLen, "utf-8"));
                    readLen = br.read(buffer, 0, buffer.length);
                }
                content = builder.toString();
                br.close();
            }
		}
		catch (IOException e)
		{
            Tracer.printStackTrace(e);
		}
		return content;
	}

    /**
     * 读取指定目录的文件内容。文件名格式：[header|path]，如[file://mnt/sdcard/test]
     * @param filePath
     * @return 文件内容，解决了中文编码问题
     */
    public static String readAllTextByFileName_v2(Context context,String filePath)
    {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        String content = null;
        try
        {
            InputStream inputStream = getInputStream(context,filePath);
            if(null != inputStream)
            {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                content = reader.readLine();
                while(null != content )
                {
                    builder.append(content);
                    content = reader.readLine();
                }
            }

        }
        catch (IOException e)
        {
            Tracer.printStackTrace(e);
        }
        finally
        {
            try
            {
                if(null != reader)
                {
                    reader.close();
                }
            }
            catch (IOException e)
            {
                Tracer.printStackTrace(e);
            }
        }
        return builder.toString();

    }

    public static String readConfigByFileName(Context context,String filePath)
    {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        String content = null;
        try
        {
            InputStream inputStream = getInputStream(context,filePath);
            if(null != inputStream)
            {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                content = reader.readLine();
                while(null != content )
                {
                    if(!content.startsWith("#"))
                    {
                        builder.append(content);
                    }
                    content = reader.readLine();
                }
            }

        }
        catch (IOException e)
        {
            Tracer.printStackTrace(e);
        }
        finally
        {
            try
            {
                if(null != reader)
                {
                    reader.close();
                }
            }
            catch (IOException e)
            {
                Tracer.printStackTrace(e);
            }
        }
        return builder.toString();

    }

}
