package com.dike.assistant.ahiber;

import org.free.lib.utils.Tracer;

public class TSqlMaper
{

    public static final int ST_INSERT = 3;
    public static final int ST_DELETE = 4;
    public static final int ST_UPDATE = 5;
    public static final int ST_SELECT = 6;
    public static final int ST_SCRIPT = 9;

    private String id;
    private String script;
    private String cacheRefreshs;
    private int type;
    /**
     * 参数类型
     *
     * @deprecated
     */
    private String parameterClass;
    private String split;
    private String returnClass;
    private boolean cached;

    public TSqlMaper()
    {

    }

    public String getId()
    {
        return id;
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public String getScript()
    {
        return script;
    }


    public void setScript(String script)
    {
        this.script = script;
    }


    public String getCacheRefreshs()
    {
        return cacheRefreshs;
    }


    public void setCacheRefreshs(String cacheRefreshs)
    {
        this.cacheRefreshs = cacheRefreshs;
    }


    public int getType()
    {
        return type;
    }


    public void setType(int type)
    {
        this.type = type;
    }


    public String getParameterClass()
    {
        return parameterClass;
    }


    public void setParameterClass(String parameterClass)
    {
        this.parameterClass = parameterClass;
    }


    public String getSplit()
    {
        return split;
    }


    public void setSplit(String split)
    {
        this.split = split;
    }


    public String getReturnClass()
    {
        return returnClass;
    }


    public void setReturnClass(String returnClass)
    {
        this.returnClass = returnClass;
    }


    public boolean isCached()
    {
        return cached;
    }


    public void setCached(boolean cached)
    {
        this.cached = cached;
    }

    public void println()
    {
        Tracer.println(AhiberConfig.TAG,"[id=" + id + "|script=" + script + "|cacheRefreshs=" + cacheRefreshs + "|type=" + type + "]");
    }
}
