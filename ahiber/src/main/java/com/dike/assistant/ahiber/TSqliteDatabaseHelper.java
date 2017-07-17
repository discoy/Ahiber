package com.dike.assistant.ahiber;

import android.content.Context;

public class TSqliteDatabaseHelper extends TDatabaseHelper
{

    private static TSqliteDatabaseHelper sqliteDatabaseHelper;


    private TSqliteDatabaseHelper(Context context)
    {
        databaseOperator = new TSqliteDatabaseOperate(context);
    }

    private TSqliteDatabaseHelper(IDatabaseOperate databaseOperator)
    {
        this.databaseOperator = databaseOperator;
    }

    public static TSqliteDatabaseHelper getInstance(Context context)
    {
        if (null == sqliteDatabaseHelper)
        {
            sqliteDatabaseHelper = new TSqliteDatabaseHelper(context);
        }
        return sqliteDatabaseHelper;
    }

    public static TSqliteDatabaseHelper getInstance(IDatabaseOperate databaseOperator)
    {
        if (null == sqliteDatabaseHelper)
        {
            sqliteDatabaseHelper = new TSqliteDatabaseHelper(databaseOperator);
        }
        return sqliteDatabaseHelper;
    }

    @Override
    public IDatabaseOperate getDatabaseOperator()
    {
        return super.getDatabaseOperator();
    }
}
