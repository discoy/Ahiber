package com.dike.assistant.ahiber;

import android.content.Context;
import android.content.res.XmlResourceParser;

import org.free.lib.utils.Tracer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 解析sql文件的通用类
 *
 * @author 163
 */
public class TSQLParser
{

    private static boolean result;
    private static boolean namespaced;
    private static Map<String, TSqlMaper> sqlMapers;


    public static TSqlMaper enter(String id)
    {
        if (result)
        {
            return sqlMapers.get(id);
        }
        else
        {
            return null;
        }
    }

    public static void parse(Context context, String sqlFile) throws Exception
    {
        sqlMapers = new HashMap<String, TSqlMaper>();
        try
        {
            List<FileInclude> fileIncludes = enterInclude(context, sqlFile);
            for (int i = 0; i < fileIncludes.size(); i++)
            {
                FileInclude fileInclude = fileIncludes.get(i);
                int resId = context.getResources().getIdentifier(fileInclude.getFile(), fileInclude.getSource(),
                        context.getPackageName());
                if (0 == resId)
                {
                    continue;
                }
                execute(context, resId);
            }

            result = true;
        }
        catch (Exception e)
        {
            Tracer.printStackTrace(e);
            result = false;
            throw e;
        }
    }

    private static List<FileInclude> enterInclude(Context context, String sqlFile) throws Exception
    {
        List<FileInclude> fileIncludes = new ArrayList<FileInclude>();
        int resId = context.getResources().getIdentifier(sqlFile, TDatabaseConfig.RAW,
                context.getPackageName());

        if (0 == resId)
        {
            return fileIncludes;
        }
        //使用pull解析xml
        InputStream is = context.getResources().openRawResource(resId);
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xmlParser = factory.newPullParser();
        xmlParser.setInput(is, null);
        int eventType = xmlParser.getEventType();
        while (eventType != XmlResourceParser.END_DOCUMENT)
        {
            if (eventType == XmlResourceParser.START_TAG)
            {
                String tagName = xmlParser.getName();
                if (SQLTag.SQL_ROOT_TAG.equals(tagName))
                {
                    namespaced = "true".equals(xmlParser.getAttributeValue(null, SQLTag.NAMESPACE_TAG).toLowerCase(Locale.US)) ? true : false;
                }
                else if (SQLTag.INCLUDE_TAG.equals(tagName))
                {
                    FileInclude fileInclude = new FileInclude();
                    fileInclude.setFile(xmlParser.getAttributeValue(null, SQLTag.FILE_ATTRIBUTE_TAG));
                    fileInclude.setSource(xmlParser.getAttributeValue(null, SQLTag.SOURCE_ATTRIBUTE_TAG));
                    fileIncludes.add(fileInclude);
                }
            }
            eventType = xmlParser.next();
        }
        return fileIncludes;
    }


    private static void execute(Context context, int sourceId) throws Exception
    {
        InputStream is = context.getResources().openRawResource(sourceId);
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xmlParser = factory.newPullParser();
        xmlParser.setInput(is, null);
        String namespace = null;
        int eventType = xmlParser.getEventType();
        String currentId = "";
        while (eventType != XmlResourceParser.END_DOCUMENT)
        {
            if (eventType == XmlResourceParser.START_TAG)
            {
                String tagName = xmlParser.getName();
                if (SQLTag.MAPER_ROOT_TAG.equals(tagName))
                {
                    namespace = namespaced ? xmlParser.getAttributeValue(namespace, SQLTag.NAMESPACE_TAG) : null;
                }
                else if (SQLTag.INSERT_TAG.equals(tagName))
                {
                    currentId = addMaper(namespace, xmlParser, TSqlMaper.ST_INSERT);
                }
                else if (SQLTag.SELECT_TAG.equals(tagName))
                {
                    currentId = addMaper(namespace, xmlParser, TSqlMaper.ST_SELECT);
                }

                else if (SQLTag.DELETE_TAG.equals(tagName))
                {
                    currentId = addMaper(namespace, xmlParser, TSqlMaper.ST_DELETE);
                }

                else if (SQLTag.UPDATE_TAG.equals(tagName))
                {
                    currentId = addMaper(namespace, xmlParser, TSqlMaper.ST_UPDATE);
                }

                else if (SQLTag.SCRIPT_TAG.equals(tagName))
                {
                    currentId = addMaper(namespace, xmlParser, TSqlMaper.ST_SCRIPT);
                }
            }
            if (eventType == XmlResourceParser.TEXT)
            {
                TSqlMaper maper = sqlMapers.get(currentId);
                if (null != maper)
                {
                    maper.setScript(xmlParser.getText().trim());
                    currentId = "";

                }
            }
            eventType = xmlParser.next();
        }
    }

    private static String formatNamespace(String namespace)
    {
        if (null == namespace)
        {
            return "";
        }
        return namespace + ".";
    }

    private static String addMaper(String namespace, XmlPullParser xmlParser, int type)
    {
        TSqlMaper maper = new TSqlMaper();
        maper.setType(type);
        if (namespaced)
        {
            maper.setId(formatNamespace(namespace) + xmlParser.getAttributeValue(null, SQLTag.SQL_METHOD_TAG));
        }
        else
        {
            maper.setId(xmlParser.getAttributeValue(null, SQLTag.SQL_METHOD_TAG));
        }
        maper.setParameterClass(xmlParser.getAttributeValue(null, SQLTag.SQL_PARAMETER_TAG));
        maper.setReturnClass(xmlParser.getAttributeValue(null, SQLTag.SQL_RETURNCLASS_TAG));
        String cacheRefreshs = xmlParser.getAttributeValue(null, SQLTag.SQL_CACHEREFRESHS_TAG);
        maper.setSplit(xmlParser.getAttributeValue(null, SQLTag.SQL_SPLIT_TAG));
        if (null != cacheRefreshs)
        {
            maper.setCacheRefreshs(cacheRefreshs);
        }
        String cached = xmlParser.getAttributeValue(null, SQLTag.SQL_CACHED_TAG);
        if (null != cached)
        {
            maper.setCached(cached.toLowerCase(Locale.US).equals("true"));
        }
        else
        {
            maper.setCached(false);
        }

        sqlMapers.put(maper.getId(), maper);
        return maper.getId();
    }
}
