package org.free.lib.utils;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;


public class ScreenUtil
{

	private static ScreenUtil mInstance;
	private Context mContext;
	/**
	 * 屏幕的总高度
	 */
	public  int mScreenWidth = 0;
	/**
	 * 屏幕的总高度[包括标题栏和下面的虚拟键]
	 */
	public  int mScreenHeight = 0;
	/**
	 * 屏幕的密度
	 */
	public  float mScreenDensity = 0;
	/**
	 * 屏幕的DIP
	 */
	public int mScreenDip = 0;
	/**
	 * 状态栏的高度
	 */
	public  int mScreenStatusBarHeight = 0;

	/**
	 * The navigation bar height which on the bottom
	 */
	public int mScreenNavigationBarHeight = 0;

	/**
	 * 屏幕的显示高度，[即内容区高度，不包括状态栏和下面的虚拟键]
	 */
	public int mDisplayHeight = 0;
	
	public static int dip2px(Context context,float dip)
	{
		return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context
				.getResources().getDisplayMetrics()));
	}
	
	private void init(Context context)
	{
		mContext = context.getApplicationContext();
		DisplayMetrics dm = mContext.getApplicationContext().getResources().getDisplayMetrics();
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
		mScreenDensity = dm.density;
		mScreenDip = (int) (mScreenDensity * DisplayMetrics.DENSITY_MEDIUM);
		
		mScreenStatusBarHeight = getStatusBarHeight();
		mScreenNavigationBarHeight = getNavigationBarHeight(context);

		mDisplayHeight = mScreenHeight - mScreenStatusBarHeight - mScreenNavigationBarHeight;
	}
	
	private ScreenUtil(Context context)
	{
		init(context);
	}
	
	public static ScreenUtil getInstance(Context context)
	{
		if(null == mInstance)
		{
			mInstance = new ScreenUtil(context);
		}
		return mInstance;
	}

	public static void reset()
	{
		mInstance = null;
	}
	
	private  int getStatusBarHeight()
	{
		Class<?> c = null;
		Object obj = null;
		java.lang.reflect.Field field = null;
		int x = 0, sbar = 0;
		try
		{
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = mContext.getResources().getDimensionPixelSize(x);
			return sbar;
		} catch (Exception e)
		{
			Tracer.printStackTrace(e);
			return sbar;
		}

	}

	/**
	 * 检查是否有底部导航栏
	 *
	 * @param context
	 * @return
	 */
	private static boolean checkDeviceHasNavigationBar(Context context)
	{
		if (!SDKUtil.hasIcecreamSandwich())
		{
			return false;
		}
		boolean hasNavigationBar = false;

		Point appUsableSize = getAppUsableScreenSize(context);
		Point realScreenSize = getRealScreenSize(context);
		// navigation bar at the bottom
		if (appUsableSize.y < realScreenSize.y)
		{
			hasNavigationBar = true;
		}
		else
		{
			hasNavigationBar = false;
		}
		return hasNavigationBar;

	}

	/**
	 * get the navigation bar height on the bottom.work above android sdk 3.0
	 *
	 * @param context
	 * @return
	 */
	private int getNavigationBarHeight(Context context)
	{
		if (checkDeviceHasNavigationBar(context))
		{
			Resources resources = context.getResources();
			int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
			if (resourceId > 0)
			{
				return resources.getDimensionPixelSize(resourceId);
			}
		}
		return 0;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private static Point getAppUsableScreenSize(Context context)
	{
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	public static Point getRealScreenSize(Context context)
	{
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		Point size = new Point();

		if (SDKUtil.hasJellyBeanMr1())
		{
			display.getRealSize(size);
		}
		else if (SDKUtil.hasIcecreamSandwich())
		{
			try
			{
				size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
				size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
			}
			catch (IllegalAccessException e)
			{
			}
			catch (InvocationTargetException e)
			{
			}
			catch (NoSuchMethodException e)
			{
			}
		}

		return size;
	}
	
}