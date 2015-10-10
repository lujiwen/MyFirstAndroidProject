package com.seadee.degree.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class HomeService extends Service 
{	
	private String tag = "HomeService";
	private static HomeService instance ;
	private UdiskStateReceiver udiskStateReceiver = new UdiskStateReceiver( );	
 	private NetworkStateReceiver networkStateReceiver = new NetworkStateReceiver( );
	private BroadcastReceiver usbBroadcastReceiver;
	private final static String TAG = "HomeService" ;
	private final IBinder mBinder = new HomeBinder();
	public class HomeBinder extends Binder
	{
		 public HomeService getService()
		 {
			 return HomeService.this;
		 }
	}

	@Override 
	public void onCreate()
	{
		Log.e(tag, "oncreat!");
		super.onCreate();
		instance = this;
		Log.e(tag, "startReg");
		udiskStateReceiver.regUdiskReciever(this);
		networkStateReceiver.regNetworkReciever(this);
	}
	
	@Override 
	public void onStart(Intent intent, int startId)
	{
		usbBroadcastReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				 if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED))
				 {
	                  Log.d(TAG, "Received SDCard Mount Event!");
	              } 
				 else if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED))
               	 {
	                 Log.d(TAG, "Received SDCard UnMount Event!");
	              }
	            }
		};
		
		IntentFilter usbfFilter = new IntentFilter();
		usbfFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		usbfFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		usbfFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		usbfFilter.addDataScheme("usb");
		registerReceiver(usbBroadcastReceiver, usbfFilter);
	}
	
	@Override 
	public void  onDestroy()
	{
		udiskStateReceiver.unregUdiskReceiver(this);
		networkStateReceiver.unregNetworkReciever(this);
		super.onDestroy();
	}
	@Override 
	public IBinder onBind(Intent intent)
	{
		Log.e(tag, "onbind!");
		return mBinder;		
	}
}
