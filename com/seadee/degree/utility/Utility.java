package com.seadee.degree.utility;

import java.lang.reflect.Method;
import java.util.Locale;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.format.DateFormat;

public class Utility 
{
	public static void startPackage(Context context,String packageName,String className)
	{
		Intent intent = new Intent();
		intent.setComponent(new ComponentName(packageName, className));
		intent.setAction(Intent.ACTION_VIEW);
		context.startActivity(intent);
	}
	
	//«–ªª”Ô—‘
	public  void updateLanguage(Locale locale)
	{
		try 
		{
			Object objIActMag;
			Class<?> clzIActMag = Class.forName("android.app.IActivityManager");
			Class<?> clzActMagNative = Class.forName("android.app.ActivityManagerNative");
			Method mtdActMagNative$getDefault = clzActMagNative.getDeclaredMethod("getDefault");
			objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);
			Method mtdIActMag$getConfiguration = clzIActMag.getDeclaredMethod("getConfiguration");
			Configuration config = (Configuration) mtdIActMag$getConfiguration.invoke(objIActMag);
			config.locale = locale;
			Class[] clzParams = { Configuration.class };
			Method mtdIActMag$updateConfiguration = clzIActMag.getDeclaredMethod("updateConfiguration", clzParams);
			mtdIActMag$updateConfiguration.invoke(objIActMag, config);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static int min(int a,int b)
	{
		return a>b?b:a;
	}
	
	
	public static String getSystemTime()
	{
		long systime = System.currentTimeMillis();
		CharSequence date = DateFormat.format("hh:mm:ss", systime);	
		return date.toString();
	}
	
	public static String getSystemDate()
	{
		long systime = System.currentTimeMillis();
		CharSequence date = DateFormat.format("yyyy/MM/dd", systime);	
		return date.toString();
	}
	
}
