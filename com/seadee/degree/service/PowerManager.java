package com.seadee.degree.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageParser.Component;

public class PowerManager {
	public static void halt(Context context)
	{
		 Intent haltIntent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
	     haltIntent.putExtra("android.intent.extra.KEY_CONFIRM",false);  
         haltIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
         context.startActivity(haltIntent);	    	
	}
	
	public static void reboot(Context context)
	{		
		Intent rebootIntent = new Intent(Intent.ACTION_REBOOT);
	    rebootIntent.putExtra("android.intent.extra.KEY_CONFIRM",false);  
        rebootIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        context.startActivity(rebootIntent);	
	}
	
	public static void materClear(Context context)
	{
		Intent clearIntent = new Intent();
		ComponentName cn = new ComponentName("com.android.settings" ,"com.android.settings.MasterClear");
		clearIntent.setComponent(cn);
		context.startService(clearIntent);
	}
}
