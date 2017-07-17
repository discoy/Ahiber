package org.free.lib.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 创建时间：2014年12月8日 上午10:23:37 项目名称：Shoyel
 * 
 * @author disco
 * @email disco.liu@gmail.com
 * @version 1.0
 * @since JDK 1.6.0_21 文件名称：UUIDMaker.java 类说明：
 */
public class UUIDMaker
{
	private static final String HASH_ALGORITHM = "MD5";
	private static final int RADIX = 10 + 26; // 10 digits + 26 letters

	/**
	 * 将给定的string通过MD5运算生成一个唯一与之对应的ID
	 * 
	 * @param str
	 *            需要生成的string
	 * @return 一个唯一与之对应的ID
	 */
	public static String getUUID_MD5(String str)
	{
		byte[] md5 = getMD5(str.getBytes());
		BigInteger bi = new BigInteger(md5).abs();
		return bi.toString(RADIX);
	}
	
	private static byte[] getMD5(byte[] data)
	{
		byte[] hash = null;
		try
		{
			MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
			digest.update(data);
			hash = digest.digest();
		}
		catch (NoSuchAlgorithmException e)
		{
			Tracer.printStackTrace(e);
		}
		return hash;
	}
}
