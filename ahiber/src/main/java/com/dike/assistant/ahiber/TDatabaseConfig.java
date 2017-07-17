package com.dike.assistant.ahiber;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;

import org.free.lib.utils.Tracer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * 封装了对数据库进行配置的通用类。
 *
 * @author ld
 */
public class TDatabaseConfig
{

    private static TDatabaseConfig databaseConfig;

    private static String CONFIG_FILE = "/assets/ahiber_config.properties";

    private static String DATABASE_NAME_DEF = "test";
    private static int DATABASE_VERSION_DEF = 1;

    private static String DB_DIR = "ahiber_dbdir";
    private static String DB_NAME_PRO = "ahiber_dbname";
    private static String DB_DIR_OLD = "ahiber_old_dbdir";
    private static String DB_VERSION_PRO = "ahiber_dbversion";
    private static String DB_NAME_SPACE_PRO = "ahiber_dbnamespace";
    private static String DB_DATA_SRC_FILE_PRO = "ahiber_dbdatasrcfile";
    private static String DB_SQL_FILE_PRO = "ahiber_dbsqlfile";
    private static String DB_TYPE_PRO = "ahiber_dbtype";

    public static String RAW = "raw";

    public String databaseDir;
    public String databaseOldDir;
    public String databaseName;
    public String newDatabaseName;
    public int databaseVersion;
    public int databaseOldVersion;
    public String dataSrcFile;
    public String databaseType;
    public String sqlFile;
    public String nameSpace;
    /**
     * 数据库文件是否已经导入
     */
    public boolean isImportDB;
    public boolean hasOldDb;

    private TDatabaseConfig(Context context)
    {
        try
        {
            initConfig(context);
        }
        catch (TAHiberException e)
        {
            Tracer.printStackTrace(e);
        }
    }

    public static TDatabaseConfig getInstance(Context context)
    {
        if (null == databaseConfig)
        {
            databaseConfig = new TDatabaseConfig(context);
        }
        return databaseConfig;
    }

    public String getNewDatabasePath(Context context)
    {
        return getDatabasePathFromContext(context) + newDatabaseName;
    }

    private void initConfig(Context context) throws TAHiberException
    {
        // 读取配置文件
        BufferedInputStream br = null;
        try
        {
            Properties pro = new Properties();
            //使用BufferedInputStream提高IO效率
            br = new BufferedInputStream(TDatabaseConfig.class.getResourceAsStream(CONFIG_FILE));
            pro.load(br);
            databaseDir = pro.getProperty(DB_DIR, "");
            databaseOldDir = pro.getProperty(DB_DIR_OLD, "");
            databaseName = pro.getProperty(DB_NAME_PRO, DATABASE_NAME_DEF);
            databaseVersion = Integer.parseInt(pro.getProperty(DB_VERSION_PRO,
                    String.valueOf(DATABASE_VERSION_DEF)));
            dataSrcFile = pro.getProperty(DB_DATA_SRC_FILE_PRO);
            databaseType = pro.getProperty(DB_TYPE_PRO);
            sqlFile = pro.getProperty(DB_SQL_FILE_PRO);
            nameSpace = pro.getProperty(DB_NAME_SPACE_PRO);
            newDatabaseName = databaseName;
            hasOldDb = false;

            databaseDir = changeDatabasesDir(context, databaseDir);
            //数据迁移
            moveData(context);
            //compare version
            SharedPreferences sp = context.getSharedPreferences("AHiber", Context.MODE_PRIVATE);
            databaseOldVersion = sp.getInt(DB_VERSION_PRO, 0);
            boolean isForceImport = databaseVersion > databaseOldVersion ? true : false;
            Tracer.println(AhiberConfig.TAG,"databaseOldVersion="+databaseOldVersion+"||databaseVersion="+databaseVersion);
            sp.edit().putInt(DB_VERSION_PRO, databaseVersion).commit();
            //导入数据库文件（如果有的话）
            isImportDB = importDataSrc(context, isForceImport);
            //解析sql文件
            parseSQL(context);

        }
        catch (Exception e)
        {
            Tracer.printStackTrace(e);
            throw new TAHiberException(e);
        }
        finally
        {
            try
            {
                br.close();
            }
            catch (Exception e)
            {
                Tracer.printStackTrace(e);
                throw new TAHiberException(e);
            }
        }
    }

    /**
     * @param @param mContext
     * @return void
     * @throws
     * @Title: changeDatabasesDir
     * @Description:  Change default location of sqlite databases.
     */
    private static String changeDatabasesDir(Context mContext, String path)
    {
        try
        {
            if (TextUtils.isEmpty(path))
            {
                return path;
            }
            File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/" + path);
            file.mkdirs();
            Field field = null;
            field = ContextWrapper.class.getDeclaredField("mBase");
            field.setAccessible(true);
            // // 获取mBase变量  
            Object obj = field.get(mContext);
            field = obj.getClass().getDeclaredField("mDatabasesDir");
            field.setAccessible(true);
            // 创建自定义路径  
            field.set(obj, file);
            return file.getAbsolutePath() + "/";
        }
        catch (NoSuchFieldException e)
        {
            Tracer.printStackTrace(e);
            return path;
        }
        catch (IllegalArgumentException e)
        {
            Tracer.printStackTrace(e);
            return path;
        }
        catch (IllegalAccessException e)
        {
            Tracer.printStackTrace(e);
            return path;
        }

    }

    private void moveData(Context context)
    {
        if (TextUtils.isEmpty(databaseOldDir))
        {
            return;
        }

        String dbFilePathOld = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator
                +databaseOldDir + databaseName;
        File dbFileOld = new File(dbFilePathOld);
        String dbFileDirNew = getDatabasePathFromContext(context);
        String dbFilePathNew = dbFileDirNew + databaseName;
        File dbFileNew = new File(dbFilePathNew);
        if(dbFileOld.exists() && dbFileOld.isFile() && dbFileOld.canRead() && !dbFileNew.exists())
        {
            File dir = new File(dbFileDirNew);
            dir.mkdirs();
            InputStream is = null;
            OutputStream os = null;
            try
            {
                is = new BufferedInputStream(new FileInputStream(dbFileOld));
                os = new BufferedOutputStream(new FileOutputStream(dbFileNew));
                byte[] buffer = new byte[1024 * 4];
                int count = 0;
                while ((count = is.read(buffer, 0, buffer.length)) > 0)
                {
                    os.write(buffer, 0, count);
                }
                os.flush();
            }
            catch (IOException e)
            {
                dbFileNew.delete();
                Tracer.printStackTrace(e);
            }
            finally
            {
                if(null != is)
                {
                    try
                    {
                        is.close();
                    }
                    catch (IOException e)
                    {
                        dbFileNew.delete();
                        Tracer.printStackTrace(e);
                    }
                }
                if(null != os)
                {
                    try
                    {
                        os.close();
                    }
                    catch (IOException e)
                    {
                        Tracer.printStackTrace(e);
                    }
                }
            }

            dbFileOld.delete();
        }
    }

    private boolean importDataSrc(Context context, boolean isForceImport)
    {
        if (TextUtils.isEmpty(dataSrcFile))
        {
            return false;
        }
        try
        {

            String fileNameDir = getDatabasePathFromContext(context);
            String fileNamePath = fileNameDir + databaseName;
            File file = new File(fileNamePath);
            if (!isForceImport)
            {
                if ((file.exists() && file.isFile()))
                {
                    return true;
                }
            }
            //if there has a db file then change the new database file to "new_*"
            if (file.exists())
            {
                hasOldDb = true;
                newDatabaseName = "new_" + databaseName;
                fileNamePath = fileNameDir + newDatabaseName;
                //if the app is first install then set databaseOldVersion = 0,if the app is uninstalled and install again
                //set databaseOldVersion = -1;
                databaseOldVersion = databaseOldVersion == 0 ? -1 : databaseOldVersion;
//				file.renameTo(new File(fileNameDir  + oldDatabaseName));
            }
            int id = getSourceId(context);
            if (0 == id)
            {
                return false;
            }
            file = new File(fileNameDir);
            file.mkdirs();
            InputStream is = context.getResources().openRawResource(id);
            OutputStream os = new BufferedOutputStream(new FileOutputStream(fileNamePath));
            byte[] buffer = new byte[1024 * 4];
            int count = 0;
            while ((count = is.read(buffer, 0, buffer.length)) > 0)
            {
                os.write(buffer, 0, count);
            }
            os.flush();
            os.close();
            is.close();
        }
        catch (Exception e)
        {
            Tracer.printStackTrace(e);
            return false;
        }

        return true;
    }

    private int getSourceId(Context context)
    {
        String packageName = context.getPackageName();
        String fileName = getNameExceptExtendName(dataSrcFile);

        return context.getResources().getIdentifier(fileName, RAW, packageName);
    }

    private String getDatabasePathFromContext(Context context)
    {

        if (!TextUtils.isEmpty(databaseDir))
        {
            return databaseDir;
        }
        else
        {
            String packageName = context.getPackageName();
            String filePath = "/databases/";
//			String fileName = databaseName;
            String path = "/data"
                    + Environment.getDataDirectory().getAbsolutePath() + "/";

//			String path =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
            return path + packageName + filePath;
        }

    }


    private boolean parseSQL(Context context) throws TAHiberException
    {
        try
        {
            TSQLParser.parse(context, getNameExceptExtendName(sqlFile));
        }
        catch (Exception e)
        {
            Tracer.printStackTrace(e);
            throw new TAHiberException(e);
        }
        return true;
    }

    private String getNameExceptExtendName(String str)
    {
        int start = 0;
        int end = str.indexOf(".");
        if (start >= end)
        {
            return str;
        }
        return str.substring(start, end);
    }


}
