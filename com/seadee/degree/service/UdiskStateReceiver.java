package com.seadee.degree.service;

import java.util.ArrayList;

import com.seadee.degree.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.os.storage.StorageManager;
import android.util.Log;
import android.widget.Toast;

public class UdiskStateReceiver extends BroadcastReceiver {

    private StorageManager mStorageManager = null;
    public Context context;
    
    public  UdiskStateReceiver(Context context )
    {
    	this.context = context ;
    }
    public  UdiskStateReceiver( )
    {
       
    }
    
	@Override
	public void onReceive(Context context, Intent intent) 
	{
			String action = intent.getAction();
	      if (action.equals(Intent.ACTION_MEDIA_EJECT))
	      {
	    	  Toast.makeText(context,context.getResources().getString(R.string.udiskEject) , Toast.LENGTH_SHORT).show() ;
	    	  Log.e("broadcast", "ACTION_MEDIA_EJECT");
	    	  Log.e("broadcast", "eject~!");
	      } 
	      else if (action.equals(Intent.ACTION_MEDIA_MOUNTED))
	      {
	    	  Toast.makeText(context,context.getResources().getString(R.string.udiskMounted), Toast.LENGTH_SHORT).show() ;
	    	  Log.e("broadcast", "ACTION_MEDIA_MOUNTED") ;
	      }	       
	}
	 public  boolean isUDiskMounted()
	 {
		 if(mStorageManager==null)
			mStorageManager = (StorageManager)context.getSystemService(Context.STORAGE_SERVICE);
		String state = mStorageManager.getVolumeState(getUsbStoragePath(context));
		return Environment.MEDIA_MOUNTED.equals(state); 		 
	 }	
	 public boolean unMountUdisk(Context context)
	 {
		 if(!isUDiskMounted())
		 {
			 return true ;
		 }
		 else 
		 {
			 try {
				IBinder service = ServiceManager.getService("mount") ;
				if(service != null)
				{
					IMountService mountService = IMountService.Stub.asInterface(service) ;
					if(mountService!=null)
						mountService.unmountVolume(getUsbStoragePath(context), true, false);	
					for(int i=0;i<30;i++)
					{
						if(!isUDiskMounted())
						{
							return true;
						}
						try {
							Thread.sleep(150);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				 
			} catch (RemoteException e) 
			{
				e.printStackTrace();
				return false ;
			}
		 }
		 return false ;
	 }
	 
	public static  String getUsbStoragePath(Context context)
	{
		String path ="" ;
		ArrayList<String> totalDevicesList ;
		totalDevicesList = new ArrayList<String>() ;
		StorageManager storageManager = (StorageManager)context.getSystemService(context.STORAGE_SERVICE);
		String[] list = storageManager.getVolumePaths();
		
		for(int i=0;i<list.length;i++)
		{
			totalDevicesList.add(list[i]) ;
		}
		for(int i=0;i<totalDevicesList.size();i++)
		{
			if(!totalDevicesList.get(i).equals(Environment.getExternalStorageDirectory().getPath()))
			{
				if(totalDevicesList.get(i).contains("usb"))
				{
					path = totalDevicesList.get(i);
					Log.e("USBPath",path);
					return path ;
				}
			}
		}
		return path;
	}
	public void unregUdiskReceiver(Context context)
	{
		context.unregisterReceiver(this);
	}
 	
	public void regUdiskReciever(Context context)
	{
		this.context=context;
		IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);

        iFilter.addDataScheme("file");
        context.registerReceiver(this, iFilter); 
	}

}
