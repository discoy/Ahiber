package com.dike.assistant.ahiber;

import android.content.Context;

import java.util.Locale;

public class TDatabaseManager
{

    private static String SQLITE = "sqlite";

    /**
     * Get default database helper
     *
     * @param context The context
     * @return TDatabaseHelper
     */
    public static TDatabaseHelper getDefaultDatabaseHelper(Context context)
    {
        String type = TDatabaseConfig.getInstance(context).databaseType;
        if (null == type || type.trim().length() < 1)
        {
            return null;
        }
        if (SQLITE.equals(type.toLowerCase(Locale.US)))
        {
            return TSqliteDatabaseHelper.getInstance(context);
        }
        return null;
    }

    public static TDatabaseHelper getDatabaseHelperByOperator(IDatabaseOperate databaseOperator)
    {
        return TSqliteDatabaseHelper.getInstance(databaseOperator);
    }

}
