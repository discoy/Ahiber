package com.dike.assistant.ahiber;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import org.free.lib.utils.Tracer;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 通用的数据库操作类，封装了一些通用的方法供子类使用
 *
 * @author ld
 */
public abstract class TDatabaseOperate implements IDatabaseOperate
{

    protected static Map<String, List<String>> clzMethodMap = new HashMap<String, List<String>>();

    protected static String VALUE = "value";
    protected static String SIMPLE_TYPE_MODEL = "#" + VALUE + "#";
    protected static String VALUE_SPLITE_CHAR = "#";

    /**
     * 生成单条的sql语句
     *
     * @param script
     * @param parameter
     * @return
     */
    public String reflect(final String script, String valueKey, Object parameter)
    {
        if (parameter == null)
        {
            return script;
        }
        String _script = new String(script);
        // 处理简单类型
        if (parameter instanceof String || parameter instanceof Integer
                || parameter instanceof Double || parameter instanceof Long
                || parameter instanceof Charset || parameter instanceof Float)
        {

            String value = String.valueOf(parameter);
            value = sqliteEscape(value);
            _script = _script.replaceAll(valueKey == null ? SIMPLE_TYPE_MODEL : valueKey, "'" + value + "'");
            return _script;
        }
        else if (parameter instanceof Map<?, ?>)
        {
            //增加对Map数据类型的处理 【ld 2014年5月5日19:53:07】
            @SuppressWarnings("unchecked")
            Map<String, ?> maps = (Map<String, ?>) parameter;
            Iterator<String> keyItr = maps.keySet().iterator();
            String key = null;
            Object value = null;
            while (keyItr.hasNext())
            {
                key = keyItr.next();
                value = maps.get(key);
                _script = reflect(_script, VALUE_SPLITE_CHAR + key + VALUE_SPLITE_CHAR, value);
            }
            return _script;
        }
        List<String> gets = reflectMethods(parameter.getClass());
        String propertyName = "";//the field name of class
        String recordName = ""; // the record name of db.
        String methodName = "";// the method name of class
        String temp = "";
        String[] split = null;
        for (int i = 0; i < gets.size(); i++)
        {
            try
            {
                temp = gets.get(i);
                split = temp.split(";");
                propertyName = split[0];
                methodName = split[1];
                recordName = split.length == 3 ? split[2] : propertyName;

                java.lang.reflect.Method m = parameter.getClass().getMethod(methodName);
                Object valueObject = m.invoke(parameter);
                String value = "";
                if (valueObject != null)
                {
                    if (valueObject.getClass().equals(byte[].class))
                    {
                        value = new String((byte[]) valueObject);
                    }
                    else
                    {
                        value = String.valueOf(valueObject);
                    }
                }

                value = sqliteEscape(value);

                _script = _script.replaceAll(
                        VALUE_SPLITE_CHAR + recordName
                                + VALUE_SPLITE_CHAR, "'" + value + "'");

            }
            catch (Exception e)
            {
                Tracer.printStackTrace(e);
                break;
            }
        }

        return _script;

    }

    /**
     * 生成批量sql语句
     *
     * @param script
     * @param parameters
     * @return
     */
    public List<BatchSql> reflect(String script, String split, List<?> parameters)
    {
        List<BatchSql> sqlList = new ArrayList<BatchSql>();
        if (parameters == null || TextUtils.isEmpty(script))
        {
            return sqlList;
        }
        int count = 0;

        if (TextUtils.isEmpty(split))
        {
            for (Object parameter : parameters)
            {
                script = reflect(script, VALUE_SPLITE_CHAR + VALUE + count + VALUE_SPLITE_CHAR, parameter);
            }
            BatchSql bs = new BatchSql();
            bs.sql = script;
            sqlList.add(bs);
        }
        else
        {
            StringBuilder sqlBuidler = new StringBuilder();
            String[] splits = script.split(split);
            if (splits == null || splits.length != 4)
            {
                Tracer.e(AhiberConfig.TAG,"sql format not right-->" + script);
                return sqlList;
            }
            String head = new String(splits[0].trim());
            String repeat = new String(splits[1].trim());
            String tail = new String(splits[2].trim());
            int size = Integer.valueOf(splits[3].trim());

            String sql = head;
            // 处理简单类型
            for (Object parameter : parameters)
            {
                if (parameter instanceof String || parameter instanceof Integer
                        || parameter instanceof Double || parameter instanceof Long
                        || parameter instanceof Charset || parameter instanceof Float)
                {

                    String value = String.valueOf(parameter);
                    value = sqliteEscape(value);

                    sql = sql.replaceAll(VALUE_SPLITE_CHAR + VALUE + count + VALUE_SPLITE_CHAR, "'" + value
                            + "'");
                    sqlBuidler.append(sql);

                }
                else if (parameter instanceof Map<?, ?>)
                {
                    //增加对Map数据类型的处理 【ld 2014年5月5日19:53:07】
                    @SuppressWarnings("unchecked")
                    Map<String, ?> maps = (Map<String, ?>) parameter;
                    Iterator<String> keyItr = maps.keySet().iterator();
                    String key = null;
                    Object value = null;
                    while (keyItr.hasNext())
                    {
                        key = keyItr.next();
                        value = maps.get(key);
                        sql = reflect(sql, VALUE_SPLITE_CHAR + key + VALUE_SPLITE_CHAR, value);

                    }
                    sqlBuidler.append(sql);
                }
                else
                {
                    List<String> gets = reflectMethods(parameter.getClass());
                    String propertyName = "";//the field name of class
                    String recordName = ""; // the record name of db.
                    String methodName = "";// the method name of class
                    String temp = "";
                    splits = null;
                    for (int i = 0; i < gets.size(); i++)
                    {
                        try
                        {
                            temp = gets.get(i);
                            splits = temp.split(";");
                            propertyName = splits[0];
                            methodName = splits[1];
                            recordName = splits.length == 3 ? splits[2] : propertyName;

                            java.lang.reflect.Method m = parameter.getClass().getMethod(methodName);
                            Object valueObject = m.invoke(parameter);
                            String value = "";
                            if (valueObject != null)
                            {
                                if (valueObject.getClass() == byte.class
                                        || valueObject.getClass().equals(byte[].class))
                                {
                                    value = new String((byte[]) valueObject);
                                }
                                else
                                {
                                    value = String.valueOf(valueObject);
                                }
                            }

                            value = sqliteEscape(value);

                            sql = sql.replaceAll(
                                    VALUE_SPLITE_CHAR + recordName
                                            + VALUE_SPLITE_CHAR, "'" + value + "'");

                        }
                        catch (Exception e)
                        {
                            Tracer.printStackTrace(e);;
                            break;
                        }
                    }
                    sqlBuidler.append(sql);
                }
                count++;
                sql = repeat;
                if (count >= size)
                {
                    BatchSql bs = new BatchSql();
                    bs.count = count;
                    bs.sql = sqlBuidler.toString() + tail;
                    sqlList.add(bs);
                    sqlBuidler.delete(0, sqlBuidler.length());
                    count = 0;
                    sql = head;
                }
            }
            //收尾工作
            if (count < size && 0 < count)
            {
                BatchSql bs = new BatchSql();
                bs.count = count;
                bs.sql = sqlBuidler.toString() + tail;
                sqlList.add(bs);
                sqlBuidler.delete(0, sqlBuidler.length());
                count = 0;
                sql = head;
            }
        }

        return sqlList;

    }

    public static String sqliteEscape(String keyWord)
    {
        if (keyWord == null)
        {
            return "";
        }
        keyWord = keyWord.replace("'", "''");
//		keyWord = keyWord.replace("%", "/%");
//		keyWord = keyWord.replace("&", "/&");
//		keyWord = keyWord.replace("(", "/(");
//		keyWord = keyWord.replace(")", "/)");
        return keyWord;
    }

    protected static Field getAllDeclaredField(Class<?> AClass, String fieldName)
    {
        Field field = null;
        if (null == AClass)
        {
            return field;
        }
        try
        {
            field = AClass.getDeclaredField(fieldName);

        }
        catch (Exception e)
        {
			Tracer.printStackTrace(e);
            field = getAllDeclaredField(AClass.getSuperclass(), fieldName);
        }
        return field;
    }


    public static List<String> reflectMethods(Class<?> AClass)
    {
        if (clzMethodMap.containsKey(AClass.getName()))
        {
            return clzMethodMap.get(AClass.getName());
        }
        List<String> methods = new ArrayList<String>();
        java.lang.reflect.Method[] ms = AClass.getMethods();
        Field field = null;
        String fieldName = "";
        String methodName = "";
        String flagName = "";
        for (int i = 0; i < ms.length; i++)
        {
            java.lang.reflect.Method m = ms[i];

            methodName = m.getName();
            if (methodName.length() <= 3)
            {
                continue;
            }
            if (!methodName.subSequence(0, 3).equals("get"))
            {
                continue;
            }
            if (methodName.equals("getClass"))
            {
                continue;
            }
            fieldName = methodName.substring(3, 4).toLowerCase(Locale.getDefault())
                    + methodName.substring(4);
            field = getAllDeclaredField(AClass, fieldName);
            if (null == field)
            {
                continue;
            }
            field.setAccessible(true);
            Expose exposeAnno = field.getAnnotation(Expose.class);
            if (exposeAnno == null || !exposeAnno.persistence())
            {
                continue;
            }
            PersistenceName persistAnno = field
                    .getAnnotation(PersistenceName.class);
            if (persistAnno != null)
            {
                flagName = fieldName + ";" + methodName + ";" + persistAnno.value();

            }
            else
            {
                flagName = fieldName + ";" + methodName;
            }
            methods.add(flagName);

        }

        clzMethodMap.put(AClass.getName(), methods);
        return methods;
    }

    protected boolean isSimpleClass(Class<?> myClass)
    {
        if (myClass == Integer.class)
        {
            return true;
        }
        else if (myClass == String.class)
        {
            return true;
        }
        else if (myClass == Double.class)
        {
            return true;

        }
        else if (myClass == Long.class)
        {
            return true;

        }
        else if (myClass == Float.class)
        {
            return true;
        }
        return false;
    }

    protected List<Object> excuteQuerySimple(Class<?> returnClass, Cursor mCursor,
                                             int[] iColumns)
    {
        List<Object> returnList = new ArrayList<Object>();
        if (!isSimpleClass(returnClass))
        {
            return returnList;
        }

        while (mCursor.moveToNext())
        {
            for (int i = 0; i < iColumns.length; i++)
            {

                if (returnClass == Integer.class)
                {
                    returnList
                            .add(Integer.valueOf(mCursor.getInt(iColumns[i])));
                }
                else if (returnClass == String.class)
                {

                    returnList.add(String.valueOf(mCursor
                            .getString(iColumns[i])));
                }
                else if (returnClass == Double.class)
                {

                    returnList.add(Double.valueOf(mCursor
                            .getDouble(iColumns[i])));

                }
                else if (returnClass == Long.class)
                {
                    returnList.add(Long.valueOf(mCursor.getLong(iColumns[i])));

                }
                else if (returnClass == Float.class)
                {
                    returnList
                            .add(Float.valueOf(mCursor.getFloat(iColumns[i])));
                }

            }
        }
        if (mCursor != null && (!mCursor.isClosed()))
        {
            mCursor.close();
        }
        return returnList;
    }

    protected List<Object> excuteQueryMap(Class<?> returnClass, Cursor mCursor,
                                          int[] iColumns)
    {
        List<Object> returnList = new ArrayList<Object>();
        if ((returnClass != Map.class))
        {
            return returnList;
        }
        Map<String, String> maps = null;
        while (mCursor.moveToNext())
        {
            maps = new HashMap<String, String>();
            for (int i = 0; i < iColumns.length; i++)
            {
                maps.put(mCursor.getColumnName(iColumns[i]), mCursor.getString(iColumns[i]));
            }
            returnList.add(maps);
        }
        if (mCursor != null && (!mCursor.isClosed()))
        {
            mCursor.close();
        }
        return returnList;
    }

    @Override
    public boolean createData()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean updateData(SQLiteDatabase oldDb, SQLiteDatabase newDb)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean open()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean close()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDatabaseAvailable()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object queryForObject(String method, Object parameter)
            throws TAHiberException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object queryForObject(String method) throws TAHiberException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<?> queryForList(String method, Object parameter)
            throws TAHiberException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<?> queryForList(String method, Object parameter, int limit)
            throws TAHiberException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<?> queryForList(String method) throws TAHiberException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int deleteForObject(String method, Object parameter) throws TAHiberException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int delete(String method) throws TAHiberException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int updateForObject(String method, Object parameter) throws TAHiberException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int update(String method) throws TAHiberException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getAutoIncSeq(String tablename)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int insertList(String method, List<?> parameter) throws TAHiberException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int insertObject(String method, Object parameter) throws TAHiberException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int insert(String method) throws TAHiberException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void scriptRunner(String method) throws TAHiberException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean exeSQL(String sql)
    {
        // TODO Auto-generated method stub
        return false;
    }

}
