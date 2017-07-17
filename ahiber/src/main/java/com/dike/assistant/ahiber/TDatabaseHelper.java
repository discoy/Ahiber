package com.dike.assistant.ahiber;

public abstract class TDatabaseHelper
{

    protected IDatabaseOperate databaseOperator;

    public IDatabaseOperate getDatabaseOperator()
    {
        return databaseOperator;
    }

    public void setDatabaseOperator(IDatabaseOperate databaseOperator)
    {
        this.databaseOperator = databaseOperator;
    }
}
