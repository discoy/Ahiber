package org.free.lib.utils;

import android.text.TextUtils;

public class NumberParserUtil
{

	public static int parseStringToInt(String str,int defaultValue)
	{
		if(TextUtils.isEmpty(str))
		{
			return defaultValue;
		}
		int aInt = defaultValue;
		try
		{
			aInt = Integer.parseInt(str);
		}
		catch(Exception e)
		{
			aInt = defaultValue;
		}
		return aInt;
	}
	
	public static int parseObjectToInt(Object obj,int defaultValue)
	{
		if(null == obj)
		{
			return defaultValue;
		}
		else if(obj instanceof String)
		{
			return parseStringToNumber(obj.toString(),defaultValue).intValue();
		}
		else if(obj instanceof Number)
		{
			return ((Number)obj).intValue();
		}
		return defaultValue;
	}
	
	public static Number parseStringToNumber(String str,Number defaultValue)
	{
		Number value = defaultValue;
		try
		{
			if(str == null)
			{
				value =  defaultValue;
			}
			else if(defaultValue instanceof Integer)
			{
				value =  Integer.valueOf(str);
			}
			else if(defaultValue instanceof Long)
			{
				value =  Long.valueOf(str);
			}
			else if(defaultValue instanceof Float)
			{
				value =  Float.valueOf(str);
			}
			else if(defaultValue instanceof Double)
			{
				value =  Double.valueOf(str);
			}
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
		return value;
	}
	
}
