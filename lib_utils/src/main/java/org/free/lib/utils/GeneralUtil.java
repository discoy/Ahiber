package org.free.lib.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.attr.format;


public class GeneralUtil
{
    public static ColorStateList createColorSelector(int aNormalColor, int aPressColor)
    {
        int stateDefault = -android.R.attr.state_pressed;
        int statePressed = android.R.attr.state_pressed;

        int[][] state = {{stateDefault}, {statePressed}};
        int[] color = {aNormalColor, aPressColor};
        return new ColorStateList(state, color);
    }

    public static boolean isEmail(String email)
    {
        String strPattern = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }


    public static boolean isPhone(String phone)
    {
        String phoneEx = "^1[358][0-9]{9}"; // 手机号
        Pattern p = Pattern.compile(phoneEx);
        p.matcher(phone);
        return phone.matches(phoneEx);
    }

    public static boolean isNumber(String phone)
    {
        String phoneEx = "^[0-9]*$"; // 为数字
        Pattern p = Pattern.compile(phoneEx);
        p.matcher(phone);
        return phone.matches(phoneEx);
    }


    /**
     * 根据文件大小自动对文件大小进行格式化后输出
     *
     * @param size 以byte为单位的文件大小
     * @return
     */
    public static String formatFileSizeAuto(long size)
    {
        String str = "";
        NumberFormat formater = NumberFormat.getInstance();
        formater.setMaximumFractionDigits(2);
        formater.setMinimumFractionDigits(2);
        size = size < 0 ? 0 : size;
        if (size < 1024)
        {
            str = size + "B";
        }
        else if (size / 1024f < 1024)
        {
            str = formater.format(size / 1024.0) + "KB";
        }
        else if (size / 1024f / 1024f < 1024)
        {
            str = formater.format(size / 1024.0 / 1024.0) + "MB";
        }
        else if (size / 1024f / 1024f / 1024f < 1024)
        {
            str = formater.format(size / 1024.0 / 1024.0 / 1024.0) + "GB";
        }
        else if (size / 1024f / 1024f / 1024f / 1024f < 1024)
        {
            str = formater.format(size / 1024.0 / 1024.0 / 1024.0 / 1024.0) + "TB";
        }
        return str;
    }

    /**
     * 通过2分方式插入数据到一个有序列表中，保证插入之后列表依然有序
     *
     * @param dataSource 数据源
     * @param data       需要插入的数据
     * @param comparator 比较器
     */
    public static <T> void binaryInsert(List<T> dataSource, T data, Comparator<T> comparator)
    {
        if (null == data || null == dataSource || null == comparator)
        {
            return;
        }
        int size = dataSource.size();
        if (size == 0)
        {
            dataSource.add(data);
        }
        else
        {
            T firstElement = dataSource.get(0);
            T lastElement = dataSource.get(size - 1);
            if (comparator.compare(data, firstElement) <= 0)
            {
                dataSource.add(0, data);
            }
            else if (comparator.compare(data, lastElement) >= 0)
            {
                dataSource.add(data);
            }
            else
            {
                int start = 1;
                int end = size - 2;
                int index;
                /**
                 * 比较结果。1--》data < dataSource[end]，2--》data > dataSource[start]，
                 */
                int compareType = 2;
                int compareValue;
                while (start < end)
                {
                    index = (start + end) / 2;
                    compareValue = comparator.compare(data, dataSource.get(index));
                    if (compareValue == 0)
                    {
                        dataSource.add(index + 1, data);
                        compareType = 0;
                        break;
                    }
                    else if (compareValue < 0)
                    {
                        end = index;
                        compareType = 1;
                    }
                    else
                    {
                        start = index + 1;
                        compareType = 2;
                    }
                }
                if (1 == compareType)
                {
                    dataSource.add(end, data);
                }
                else if (2 == compareType)
                {
                    dataSource.add(start, data);
                }

            }
        }
    }

    /**
     * 检查给定的文件名是否以给定的后缀名结尾
     *
     * @param fileName   需要检查的文件名
     * @param candidates 待匹配的扩展名，以.开头。如.txt,.doc
     * @return
     */
    public static boolean isMatchedExt(String fileName, String... candidates)
    {
        boolean isMatched = false;
        if (null != candidates)
        {
            int extStartIndex = fileName.lastIndexOf(".");
            if (-1 != extStartIndex)
            {
                String ext = fileName.substring(extStartIndex);
                for (String candidate : candidates)
                {
                    if (ext.equalsIgnoreCase(candidate))
                    {
                        isMatched = true;
                        break;
                    }
                }
            }
        }

        return isMatched;
    }

    /**
     * 计算两个日期型的时间相差多少时间
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 返回固定格式的字符串。
     */
    public static String twoTimeDistance(long startTime, long endTime)
    {

        long timeLong = endTime - startTime;
        if (timeLong < 60 * 1000)
        {
            return "刚刚";
        }
        else if (timeLong < 60 * 60 * 1000)
        {
            timeLong = timeLong / 1000 / 60;
            return timeLong + "分钟前";
        }
        else if (timeLong < 60 * 60 * 1000 * 24)
        {
            timeLong = timeLong / 60 / 60 / 1000;
            return timeLong + "小时前";
        }
        else if (timeLong < 60 * 60 * 1000 * 24 * 7)
        {
            timeLong = timeLong / 1000 / 60 / 60 / 24;
            return timeLong + "天前";
        }
        else if (timeLong < 60f * 60f * 1000f * 24f * 7f * 4f)
        {
            timeLong = timeLong / 1000 / 60 / 60 / 24 / 7;
            return timeLong + "星期前";
        }
        else if (timeLong < 60f * 60f * 1000f * 24f * 28f * 12f)
        {
            timeLong = timeLong / 60 / 60 / 1000 / 24 / 28;
            return timeLong + "个月前";
        }
        else
        {
            timeLong = timeLong / 1000 / 60 / 60 / 24 / 365;
            return timeLong + "年前";
        }
    }

    /**
     * format time with a format
     *
     * @param time   a current time
     * @param format see {@link SimpleDateFormat}
     * @return
     */
    public static String formatTime(long time, String format)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(time);
    }

    /**
     * format time with a format
     *
     * @param time   a current time
     * @param format see {@link SimpleDateFormat}
     * @return
     */
    public static long convertTime(String time,String format)
    {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = null;
        try
        {
            date = formatter.parse(time);
        }
        catch (ParseException e)
        {
            Tracer.printStackTrace(e);
        }
        return  date == null ? 0 : date.getTime();
    }

    /**
     * format time to hh:dd:mm.used fo player time
     *
     * @param time
     * @return
     */
    public static String formatTime(long time)
    {
        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
    }

    public static String getPath(final Context context, final Uri uri)
    {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri))
        {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri))
            {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type))
                {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
                Tracer.println("====getpath()type="+type);
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri))
            {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri))
            {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type))
                {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }
                else if ("video".equals(type))
                {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                }
                else if ("audio".equals(type))
                {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme()))
        {

            // Return the remote address
            if (isGooglePhotosUri(uri))
            {
                return uri.getLastPathSegment();
            }

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme()))
        {
            return uri.getPath();
        }

        return null;
    }

    /**
     * *
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs)
    {

        Cursor cursor = null;
        final String column = MediaStore.Images.Media.DATA;
        final String[] projection = {
                column
        };

        try
        {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst())
            {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri)
    {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri)
    {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri)
    {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri)
    {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
