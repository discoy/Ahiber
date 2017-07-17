package com.dike.assistant.ahiber;

/**
 * 定义sql文件中标签的通用类
 * @author 163
 *
 */
public class SQLTag {
	

	public	static final String SQL_ROOT_TAG    = "sqlfile";
	public	static final String NAMESPACE_TAG = "namespace";
	public	static final String INCLUDE_TAG = "include";
	public	static final String FILE_ATTRIBUTE_TAG   = "file";
	public	static final String SOURCE_ATTRIBUTE_TAG = "source";
	
	public	static final String INSERT_TAG    = "insert";
	public	static final String DELETE_TAG    = "delete";
	public	static final String UPDATE_TAG    = "update";
	public	static final String SELECT_TAG    = "select";
	public	static final String SCRIPT_TAG    = "script";
	public	static final String[] mapTags = new String[]{INSERT_TAG,DELETE_TAG,UPDATE_TAG,SELECT_TAG,SCRIPT_TAG};
		
	public	static final String MAPER_ROOT_TAG           = "mapers";
	public  static final String SQL_METHOD_TAG           = "method";
	@Deprecated
	/**
	 * 暂时弃用，统一用反射处理
	 */
	public  static final String SQL_PARAMETER_TAG        = "parameterClass";
	public  static final String SQL_CACHEREFRESHS_TAG    = "cacheRefreshs";   // 表示需要刷新的缓存id（id==method）
	public  static final String SQL_RETURNCLASS_TAG      = "returnClass";
	public  static final String SQL_CACHED_TAG           = "cached";
	public  static final String SQL_SPLIT_TAG            = "split";   //表示string.split()的分隔标志ָ���־
}
