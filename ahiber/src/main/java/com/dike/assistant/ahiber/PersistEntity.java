package com.dike.assistant.ahiber;

class PersistEntity
{

    public String mPropertyName;
    public String mPersistName;
    public String mMethodName;

    public Class<?>[] mPersistClassArr;

    public PersistEntity()
    {
        this(null, null);
    }

    public PersistEntity(String propertyName, String persistName)
    {
        mPropertyName = propertyName;
        mPersistName = persistName;
    }

}
