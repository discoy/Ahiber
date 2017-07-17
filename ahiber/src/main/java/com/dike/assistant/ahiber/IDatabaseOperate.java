package com.dike.assistant.ahiber;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;


/**
 * 定义数据库操作接口，包括了ahiber提供的所有数据操作方法
 * @author ld
 *
 *@version 1.2 
 *<br>1.insert 增加对Map<String,?>类型的支持，注意Map中的内容只能是基本数据类型
 *<br>2.query 返回增加对Map<String,?>类型的支持，注意Map中的内容只能是基本数据类型
 *<br>3.update 增加对Map<String,?>类型的支持，注意Map中的内容只能是基本数据类型
 *<br>4.delete 增加对Map<String,?>类型的支持，注意Map中的内容只能是基本数据类型
 */
public interface IDatabaseOperate {

	public final static String splitSQL = "@;@";

	
	/**
	 * 创建数据库数据结构，用于第一次或者数据库重建后的数据库结构构建
	 * @return 数据库数据结构构建成功返回true;否则返回false
	 */
	public boolean createData();


	/**
	 * 升级数据库数据结构，升级思路：将新增的数据库表从新数据库中拷贝到原有数据库中
	 * @param oldDb 原来的数据库
	 * @param newDb 新的数据库
	 * @return 升级成功返回true;否则返回false.
	 */
	public boolean updateData(SQLiteDatabase oldDb, SQLiteDatabase newDb);
	
	/**
	 * 打开数据库相关操作
	 * @return
	 */
	public boolean open();
	
	/**
	 * 关闭数据库相关操作
	 * @return
	 */
	public boolean close();
	
	
	public boolean isDatabaseAvailable();
	
	/**
	 * 通过指定的sql方法和参数来检索符合条件的对象。
	 * @param method 对应的sql查询方法名
	 * @param parameter 查询参数，以对象方式呈现。
	 * @return 返回指定类型的对象实例。
	 * @throws TAHiberException
	 */
	public Object queryForObject(String method, Object parameter) throws TAHiberException;

	/**
	 * 通过指定的sql方法来检索符合条件的对象。
	 * @param method 对应的sql查询方法名
	 * @return 返回指定类型的对象实例。
	 * @throws TAHiberException
	 */
	public Object queryForObject(String method) throws TAHiberException;

	/**
	 * 通过指定的sql方法和参数来检索符合条件的对象列表。
	 * @param method 对应的sql查询方法名
	 * @param parameter 查询参数，以对象方式呈现
	 * @return 返回指定类型的对象实例列表。
	 * @throws TAHiberException
	 */
	public List<?> queryForList(String method, Object parameter) throws TAHiberException;

	/**
	 * 通过指定的sql方法,参数和查询返回的最大个数来检索符合条件的对象列表。
	 * @param method 对应的sql查询方法名 
	 * @param parameter 查询参数，以对象方式呈现
	 * @param limit 返回结果列表的最大个数
	 * @return 返回指定类型和最大个数的对象实例列表。
	 * @throws TAHiberException
	 */
	public List<?> queryForList(String method, Object parameter, int limit)
			throws TAHiberException;

	/**
	 * 通过指定的sql方法来检索符合条件的对象列表。
	 * @param method 对应的sql查询方法名 
	 * @return 返回指定类型的对象实例列表。
	 * @throws TAHiberException
	 */
	public List<?> queryForList(String method) throws TAHiberException;
	
	/**
	 * 通过指定的sql方法和删除列表来删除符合条件的对象。
	 * @param method 对应的sql删除方法名
	 * @param parameters 删除条件参数，以对象列表方式呈现
	 * @return 返回1删除成功，-1时删除失败。
	 * @throws TAHiberException
	 */
	public int deleteForList(String method, List<?> parameters) throws TAHiberException;
	/**
	 * 通过指定的sql方法和参数来删除符合条件的对象。
	 * @param method 对应的sql删除方法名
	 * @param parameter 查询参数，以对象方式呈现
	 * @return 返回1删除成功，-1时删除失败。
	 * @throws TAHiberException
	 */
	public int deleteForObject(String method, Object parameter) throws TAHiberException;

	/**
	 * 通过指定的sql方法来删除符合条件的对象。
	 * @param method 对应的sql删除方法名
	 * @return 返回1删除成功，-1时删除失败。
	 * @throws TAHiberException
	 */
	public int delete(String method) throws TAHiberException;

	
	/**
	 * @deprecated
	 *  通过指定的sql方法来批量更新符合条件的对象 <由于逻辑问题，目前未实现>
	 * @param method 对应的sql更新方法名
	 * @param parameters 查询参数，以对象列表方式呈现
	 * @return 返回1更新成功，-1时更新失败。
	 * @throws TAHiberException
	 */
	public int updateForList(String method, List<?> parameters) throws TAHiberException;
	/**
	 * 通过指定的sql方法和参数来更新符合条件的对象。
	 * @param method 对应的sql更新方法名
	 * @param parameter 查询参数，以对象方式呈现
	 * @return 返回1更新成功，-1时更新失败。
	 * @throws TAHiberException
	 */
	public int updateForObject(String method, Object parameter) throws TAHiberException;

	/**
	 * 通过指定的sql方法来更新符合条件的对象。
	 * @param method 对应的sql更新方法名
	 * @return 返回1更新成功，-1时更新失败。
	 * @throws TAHiberException
	 */
	public int update(String method) throws TAHiberException;

	/**
	 * 获取指定表名的的自增seq的当前大小
	 * @param tablename 表名
	 * @return 自增seq当前大小
	 */
	public long getAutoIncSeq(String tablename);
	
	/**
	 * 通过指定的sql方法插入对象列表到数据库中
	 * @param method 对应的sql插入方法名
	 * @param parameter 需要插入的对象列表
	 * @return 插入的条目个数，-1插入失败
	 * @throws TAHiberException
	 */
	public int insertList(String method, List<?> parameter) throws TAHiberException;
	
	/**
	 * 通过指定的sql方法插入对象到数据库中
	 * <br>【2014年5月5日20:06:00  ld -->增加对Map<String,?>类型的支持,注意Map里面只支持基本数据类型】
	 * @param method 对应的sql插入方法名
	 * @param parameter 需要插入的对象
	 * @return 插入的条目个数，-1插入失败
	 * @throws TAHiberException
	 */
	public int insertObject(String method, Object parameter) throws TAHiberException;

	/**
	 * 通过指定的sql方法插入对象到数据库中
	 * @param method 对应的sql插入方法名
	 * @return 插入的条目个数，-1插入失败
	 * @throws TAHiberException
	 */
	public int insert(String method) throws TAHiberException;

	public void scriptRunner(String method) throws TAHiberException;

	/**
	 * 执行sql语句,不建议使用，有sql注入风险
	 * @param sql
	 * @return 此语句是否执行成功.
	 */
	public boolean exeSQL(String sql);
}
