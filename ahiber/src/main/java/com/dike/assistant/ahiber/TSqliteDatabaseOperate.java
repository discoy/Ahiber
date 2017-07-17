package com.dike.assistant.ahiber;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import org.free.lib.utils.Tracer;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Administrator
 */

public class TSqliteDatabaseOperate extends TDatabaseOperate
{

    class MySqliteOpenHelper extends SQLiteOpenHelper
    {


        public MySqliteOpenHelper(Context context)
        {
            super(context, TDatabaseConfig.getInstance(context).databaseName, null, TDatabaseConfig.getInstance(context).databaseVersion);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            Tracer.println("create");
            if (!dbConfig.isImportDB)
            {
                //do create data operate
                createData();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            //no use
//			TAHiberLog.println("onUpgrade-->oldVersion="+oldVersion+" newVersion="+newVersion);
//			if(oldVersion < newVersion){
//				clearData();
//				updateData();
//			}
        }
    }

    protected MySqliteOpenHelper sqliteOpenHelper;
    protected SQLiteDatabase db;
    protected TDatabaseConfig dbConfig;
    private int mDbConnectionCount = 0;
    /**
     * whether close db connection after one times use complete.<br>
     * If this property is set false {@link #close()} should be called explicit
     */
    protected boolean isCloseDbEveryTimeUse = false;

    public TSqliteDatabaseOperate(Context context)
    {
        dbConfig = TDatabaseConfig.getInstance(context);
        sqliteOpenHelper = new MySqliteOpenHelper(context);
        if (dbConfig.hasOldDb)
        {
            //open temp new db
            SQLiteDatabase newDb = context.openOrCreateDatabase(dbConfig.newDatabaseName, Context.MODE_PRIVATE, null);
            //open old db
            open();
            //copy data from temp new db to old db
            //now do this in main thread,if the time too long should put in sub thread execute
            updateData(db, newDb);
            //close temp new db
//			close();
            //close and delete temp new db
            newDb.close();
            File dbFile = new File(dbConfig.getNewDatabasePath(context));
            dbFile.delete();
        }
    }

    /**
     * Execute sql cmd
     *
     * @param sql sql
     * @return -1 execute failed otherwise is success.
     */
    private int exeSQLPrivate(String sql)
    {

        if (null == sql || sql.trim().length() == 0)
        {
            return -1;
        }
        try
        {
            String[] sqls = sql.split(";");
            boolean ignore = false;
            for (int i = 0; i < sqls.length; i++)
            {

                if (sqls[i].indexOf("#db_version=") != -1)
                {
                    //数据库升级检查
                    int version = Integer.valueOf(sqls[i].substring(sqls[i].indexOf("=") + 1).trim());
                    if (version >= 0 && (version <= dbConfig.databaseOldVersion || -1 == dbConfig.databaseOldVersion))
                    {
                        ignore = false;
                        continue;
                    }
                    else
                    {
                        ignore = true;
                        continue;
                    }
                }
                if (!ignore)
                {
                    Tracer.println(AhiberConfig.TAG,"update sql=" + sqls[i]);
                    db.execSQL(sqls[i]);
                }
            }
            return 1;
        }
        catch (Exception e)
        {
            Tracer.printStackTrace(e);
            return -1;
        }
    }

    private boolean shouldCloseDB()
    {
        synchronized (this)
        {
            return 0 == mDbConnectionCount && null != db && db.isOpen() && !db.isDbLockedByCurrentThread();
        }

    }

    private void increaseDbConnectionCount()
    {
        synchronized (this)
        {
            mDbConnectionCount++;
        }
//		System.out.println("<<"+mCursorConnectionCount);
    }

    private void decreaseDbConnectionCount()
    {
        synchronized (this)
        {
            mDbConnectionCount--;
        }
//		System.out.println("<<"+mCursorConnectionCount);
    }

    private void closeInner()
    {
        decreaseDbConnectionCount();
        if (isCloseDbEveryTimeUse)
        {
            close();
        }
    }

    private Cursor rawQuery(String sql, String[] selectionArgs)
    {
        Cursor mCursor = null;
        try
        {
            mCursor = db.rawQuery(sql, selectionArgs);
        }
        catch (Exception e)
        {
            Tracer.printStackTrace(e);
            return null;
        }

        return mCursor;
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
    public Object queryForObject(String method, Object parameter)
            throws TAHiberException
    {
        List<?> mList = queryForList(method, parameter);
        if (mList.size() > 0)
        {
            return mList.get(0);
        }
        else
        {
            return null;
        }
    }

    @Override
    public Object queryForObject(String method) throws TAHiberException
    {
        return queryForObject(method, null);
    }

    @Override
    public List<?> queryForList(String method, Object parameter)
            throws TAHiberException
    {
        return queryForList(method, parameter, Integer.MAX_VALUE);
    }

    @Override
    public List<?> queryForList(String method, Object parameter, int limit)
            throws TAHiberException
    {
        long start = System.currentTimeMillis();
        List<Object> returnList = new ArrayList<Object>();
        TSqlMaper maper = TSQLParser.enter(method);
        if (maper == null)
        {
            throw new TAHiberException("method " + method + " not find");
        }
        if (TSqlMaper.ST_SELECT != maper.getType())
        {
            throw new TAHiberException("method " + method + " not correct maper ");
        }

        String sql = reflect(maper.getScript(), null, parameter);
        if (maper.isCached())
        {
            List<?> cacherList = TCacher.newInstance().store.get("_" + method + "_"
                    + sql);
            if (cacherList != null)
            {
                return cacherList;
            }
        }

        if (!isDatabaseAvailable())
        {
            if (!open())
            {
                throw new TAHiberException("db can't open ");
            }
        }
        // 防止多线程同时操作mCursor导致数据错乱
        {

            Cursor mCursor = rawQuery(sql, null);
            if (null == mCursor)
            {
                decreaseDbConnectionCount();
                return returnList;
            }

            long end = System.currentTimeMillis();
            Tracer.println(AhiberConfig.TAG,"select time=" + (end - start));
            String[] cColumns = mCursor.getColumnNames();
            int[] iColumns = new int[cColumns.length];
            for (int i = 0; i < cColumns.length; i++)
            {
                iColumns[i] = mCursor.getColumnIndex(cColumns[i]);
            }
            a:
            {
                Class<?> returnClass = null;
                try
                {
                    returnClass = Class.forName(maper.getReturnClass());
                }
                catch (ClassNotFoundException e)
                {
                    Tracer.printStackTrace(e);
                    break a;
                }
                // for special for simple
                returnList = excuteQuerySimple(returnClass, mCursor, iColumns);
                if (returnList.size() > 0)
                {
                    break a;
                }
                // for special for map
                returnList = excuteQueryMap(returnClass, mCursor, iColumns);
                if (returnList.size() > 0)
                {
                    break a;
                }

                Map<String, PersistEntity> mapClass = new HashMap<String, PersistEntity>();
                java.lang.reflect.Method[] ms = returnClass.getMethods();
                String fieldName = "";
                Expose exposeAnno;
                PersistenceName persistAnno;
                PersistEntity persistEntity = null;
                String methodName = null;
                Field field = null;

                for (int i = 0; i < ms.length; i++)
                {
                    java.lang.reflect.Method m = ms[i];
                    methodName = m.getName();
                    Class<?>[] parameterClasses = m.getParameterTypes();
                    if (parameterClasses.length == 1
                            && methodName.startsWith("set"))
                    {
                        fieldName = methodName.substring(3, 4).toLowerCase(Locale.getDefault()) + methodName.substring(4);
                        field = getAllDeclaredField(returnClass, fieldName);
                        if (null == field)
                        {
                            continue;
                        }
                        exposeAnno = field.getAnnotation(Expose.class);
                        if (null == exposeAnno || !exposeAnno.depersistence())
                        {
                            continue;
                        }
                        persistAnno = field.getAnnotation(PersistenceName.class);
                        persistEntity = new PersistEntity();
                        if (null != persistAnno)
                        {
                            persistEntity.mPersistClassArr = parameterClasses;
                            persistEntity.mPersistName = persistAnno.value();
                            persistEntity.mPropertyName = fieldName;
                        }
                        else
                        {
                            persistEntity.mPersistClassArr = parameterClasses;
                            persistEntity.mPersistName = persistEntity.mPropertyName = fieldName;
                        }
                        persistEntity.mMethodName = methodName;
                        mapClass.put(persistEntity.mPersistName, persistEntity);

                    }
                }
                int iIndex = 0;
                String persistName = null;
                methodName = null;
                while (mCursor.moveToNext())
                {

                    if (limit <= iIndex)
                    {
                        break;
                    }
                    Object returnObject = null;
                    try
                    {
                        returnObject = returnClass.newInstance();
                    }
                    catch (Exception e)
                    {
                        Tracer.printStackTrace(e);
                        continue;
                    }

                    for (int i = 0; i < iColumns.length; i++)
                    {
                        persistName = cColumns[i];
                        if (!mapClass.containsKey(persistName))
                        {
                            continue;
                        }
                        Class<?>[] parameterTypes = mapClass.get(persistName).mPersistClassArr;
                        methodName = mapClass.get(persistName).mMethodName;
                        if (parameterTypes != null)
                        {

                            try
                            {
                                java.lang.reflect.Method mMethod = returnObject
                                        .getClass().getMethod(methodName,
                                                parameterTypes);
                                if (mMethod != null)
                                {
                                    if (parameterTypes[0] == int.class)
                                    {
                                        mMethod.invoke(returnObject,
                                                mCursor.getInt(iColumns[i]));
                                    }
                                    else if (parameterTypes[0] == String.class)
                                    {
                                        mMethod.invoke(returnObject,
                                                mCursor.getString(iColumns[i]));
                                    }
                                    else if (parameterTypes[0] == double.class)
                                    {
                                        mMethod.invoke(returnObject,
                                                mCursor.getDouble(iColumns[i]));
                                    }
                                    else if (parameterTypes[0] == float.class)
                                    {
                                        mMethod.invoke(returnObject,
                                                mCursor.getFloat(iColumns[i]));
                                    }
                                    else if (parameterTypes[0] == long.class)
                                    {
                                        mMethod.invoke(returnObject,
                                                mCursor.getLong(iColumns[i]));
                                    }
                                    else if (parameterTypes[0] == byte[].class)
                                    {
                                        mMethod.invoke(returnObject, mCursor.getBlob(i));
                                    }
                                    else if (parameterTypes[0] == byte.class)
                                    {
                                        mMethod.invoke(returnObject, mCursor.getBlob(i));
                                    }
                                }
                            }
                            catch (Exception e)
                            {
                                Tracer.printStackTrace(e);
                                continue;
                            }
                        }

                    }
                    returnList.add(returnObject);
                    iIndex++;

                }
            }

            if (mCursor != null && (!mCursor.isClosed()))
            {
                mCursor.close();
            }

        }

        closeInner();
        if (maper.isCached() && returnList.size() > 0)
        {
            TCacher.newInstance().store.put("_" + method + "_" + sql, returnList);
        }
        return returnList;
    }

    @Override
    public List<?> queryForList(String method) throws TAHiberException
    {
        return queryForList(method, null);
    }

    @Override
    public int deleteForList(String method, List<?> parameters)
            throws TAHiberException
    {
        int returnValue = -1;
        if (TextUtils.isEmpty(method) || null == parameters)
        {
            return returnValue;
        }
        if (null != parameters && parameters.size() == 1)
        {
            return deleteForObject(method, parameters.get(0));
        }
        if (!isDatabaseAvailable())
        {
            if (!open())
            {
                throw new TAHiberException("db can't open ");
            }
        }
        TSqlMaper maper = TSQLParser.enter(method);
        if (maper == null)
        {
            throw new TAHiberException("method " + method + " not find");
        }
        if (maper.getType() != TSqlMaper.ST_DELETE)
        {
            throw new TAHiberException("method " + method + " not correct maper ");
        }
        List<BatchSql> bsList = reflect(maper.getScript(), maper.getSplit(), parameters);

        for (BatchSql bs : bsList)
        {
            Tracer.println(AhiberConfig.TAG,"delete single sql=" + bs.sql);
            returnValue = exeSQLPrivate(bs.sql);
        }
        closeInner();
        return returnValue;
    }

    @Override
    public int deleteForObject(String method, Object parameter) throws TAHiberException
    {
        int returnValue = 0;
        TSqlMaper maper = TSQLParser.enter(method);
        if (maper == null)
        {
            throw new TAHiberException("method " + method + " not find");
        }
        if (TSqlMaper.ST_DELETE != maper.getType())
        {
            throw new TAHiberException("method " + method + " not correct maper ");
        }

        if (!isDatabaseAvailable())
        {
            if (!open())
            {
                throw new TAHiberException("db can't open ");
            }
        }
        String sql = reflect(maper.getScript(), null, parameter);

        Tracer.println(AhiberConfig.TAG,"delete sql=" + sql);
        returnValue = exeSQLPrivate(sql);
        if (maper.getCacheRefreshs() != null)
        {
            TCacher.newInstance().store.removeForKeys(maper
                    .getCacheRefreshs().split(";"));
        }


        closeInner();
        return returnValue;
    }

    @Override
    public int delete(String method) throws TAHiberException
    {
        return deleteForObject(method, null);
    }


    @Deprecated
    @Override
    public int updateForList(String method, List<?> parameters)
            throws TAHiberException
    {
        // TODO Auto-generated method stub
        return -1;
    }

    @Override
    public int updateForObject(String method, Object parameter) throws TAHiberException
    {

        int returnValue = 0;
        TSqlMaper maper = TSQLParser.enter(method);
        if (maper == null)
        {
            throw new TAHiberException("method " + method + " not find");
        }
        if (TSqlMaper.ST_UPDATE != maper.getType())
        {
            throw new TAHiberException("method " + method + " not correct maper ");
        }

        if (!isDatabaseAvailable())
        {
            if (!open())
            {
                throw new TAHiberException("db can't open ");
            }
        }
        String sql = reflect(maper.getScript(), null, parameter);
        Tracer.println(AhiberConfig.TAG,"update sql=" + sql);
        returnValue = exeSQLPrivate(sql);
        if (maper.getCacheRefreshs() != null)
        {
            TCacher.newInstance().store.removeForKeys(maper
                    .getCacheRefreshs().split(";"));
        }


        closeInner();
        return returnValue;
    }

    @Override
    public int update(String method) throws TAHiberException
    {
        return updateForObject(method, null);
    }

    @Override
    public long getAutoIncSeq(String tablename)
    {
        if (!isDatabaseAvailable())
        {
            if (!open())
            {
                return 0;
            }
        }
        Cursor mCursor = rawQuery("select seq from sqlite_sequence where name='" + tablename + "'", null);
        if (null == mCursor)
        {
            decreaseDbConnectionCount();
            return 0;
        }
        long seq = -1;

        while (mCursor != null && mCursor.moveToNext())
        {
            seq = mCursor.getLong(0);
        }

        if (mCursor != null && (!mCursor.isClosed()))
        {
            mCursor.close();
        }

        // if the row does not exist, create it
        if (seq == -1)
        {
            seq = 1;
            String insertMSgSeqSql = "insert or replace into sqlite_sequence (name, seq) values('"
                    + tablename + "', '1')";
            exeSQLPrivate(insertMSgSeqSql);
        }

        String updateSql = "UPDATE sqlite_sequence set seq='" + (seq + 1)
                + "' where name='" + tablename + "'";

        exeSQLPrivate(updateSql);

        closeInner();
        return seq;
    }

    @Override
    public int insertList(String method, List<?> parameter) throws TAHiberException
    {
        int returnValue = -1;
        if (TextUtils.isEmpty(method) || null == parameter || parameter.size() < 1)
        {
            return returnValue;
        }
        if (null != parameter && parameter.size() == 1)
        {
            return insertObject(method, parameter.get(0));
        }
        int count = 0;
        if (!isDatabaseAvailable())
        {
            if (!open())
            {
                throw new TAHiberException("db can't open ");
            }
        }
        TSqlMaper maper = TSQLParser.enter(method);
        if (maper == null)
        {
            throw new TAHiberException("method " + method + " not find");
        }
        if (!(maper.getType() == TSqlMaper.ST_INSERT))
        {
            throw new TAHiberException("method " + method + " not correct maper ");
        }
        List<BatchSql> bsList = reflect(maper.getScript(), maper.getSplit(), parameter);

        for (BatchSql bs : bsList)
        {
            Tracer.println(AhiberConfig.TAG,"insert single sql=" + bs.sql);
            if (exeSQLPrivate(bs.sql) == 1)
            {
                count += bs.count;
            }
        }
        closeInner();
        return count;
    }

    @Override
    public int insertObject(String method, Object parameter) throws TAHiberException
    {
        int returnValue = -1;
        if (TextUtils.isEmpty(method))
        {
            return returnValue;
        }
        if (!isDatabaseAvailable())
        {
            if (!open())
            {
                throw new TAHiberException("db can't open ");
            }
        }
        TSqlMaper maper = TSQLParser.enter(method);
        if (maper == null)
        {
            throw new TAHiberException("method " + method + " not find");
        }
        if (!(maper.getType() == TSqlMaper.ST_INSERT))
        {
            throw new TAHiberException("method " + method + " not correct maper ");
        }


        String sql = reflect(maper.getScript(), null, parameter);
        Tracer.println(AhiberConfig.TAG,"insert single sql=" + sql);
        returnValue = exeSQLPrivate(sql);
        if (maper.getCacheRefreshs() != null)
        {
            TCacher.newInstance().store.removeForKeys(maper
                    .getCacheRefreshs().split(";"));
        }

        closeInner();
        return returnValue;
    }

    @Override
    public int insert(String method) throws TAHiberException
    {
        return insertObject(method, null);
    }

    @Override
    public void scriptRunner(String method) throws TAHiberException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean exeSQL(String sql)
    {
        if (!isDatabaseAvailable())
        {
            if (!open())
            {
                return false;
            }
        }
        try
        {
            String[] sqls = sql.split(";");
            for (int i = 0; i < sqls.length; i++)
            {
                db.execSQL(sqliteEscape(sqls[i]));
            }
        }
        catch (Exception e)
        {
            Tracer.printStackTrace(e);
            return false;
        }
        return true;
    }

    @Override
    public boolean open()
    {
        try
        {
            db = sqliteOpenHelper.getWritableDatabase();
            increaseDbConnectionCount();
            Tracer.println(AhiberConfig.TAG,"db.version=" + db.getVersion());
        }
        catch (Exception e)
        {
            Tracer.printStackTrace(e);
            return false;
        }
        return true;
    }

    @Override
    public boolean close()
    {
        if (shouldCloseDB())
        {
            try
            {
                Tracer.println(AhiberConfig.TAG,"<<<==db close");
                db.close();
                sqliteOpenHelper.close();

            }
            catch (Exception e)
            {
                Tracer.printStackTrace(e);
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isDatabaseAvailable()
    {
        if (null != db && db.isOpen())
        {
            increaseDbConnectionCount();
            Tracer.println(AhiberConfig.TAG,"db.name=" + db.getPath());
            return true;
        }
        return false;
    }

    /**
     * detect whether close db connection after one times use complete
     *
     * @return true or false
     */
    public boolean isCloseDbEveryTimeUse()
    {
        return isCloseDbEveryTimeUse;
    }

    /**
     * Set whether close db connection after one times use complete.<br>
     * If this 'isCloseDbEveryTimeUse' is set false {@link #close()} should be called explicit.
     *
     * @param isCloseDbEveryTimeUse Whether close db connection after one times use complete
     */
    public void setCloseDbEveryTimeUse(boolean isCloseDbEveryTimeUse)
    {
        this.isCloseDbEveryTimeUse = isCloseDbEveryTimeUse;
    }


}
