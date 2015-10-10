/*
   Network State Receiver

   Copyright 2013 Thinstuff Technologies GmbH, Author: Martin Fleisz

   This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. 
   If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
*/

package com.seadee.degree.service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;


public class NetworkStateReceiver extends BroadcastReceiver {

	@SuppressWarnings("unused")
	private final static String TAG="NetworkStateReceiver";
	static ConnectivityManager connectmanager;
	boolean wifistate=false;
	boolean ethernetstate=false;
	boolean FirstConnect=true;
	private static Context context;
	public static boolean isConnect;
	private static NetworkStateReceiver instance;
/*	public NetworkStateReceiver(Context context)
	{
		this.context = context ;
	}*/
	public static String getLocalIpAddress(Context context) {
		final String IPTAG="getLocalIpAddress";
		String nullAddress="0.0.0.0";
	     try { 
	    	 if(SettingVarible.networkstate!=SettingVarible.NETWORKSTATE.NONETWORK)
	    	 {
	         for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	             NetworkInterface intf = en.nextElement();
	             for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                 InetAddress inetAddress = enumIpAddr.nextElement();
	                 String ip_address=inetAddress.getHostAddress();
	                 if (!inetAddress.isLoopbackAddress()&&InetAddressUtils.isIPv4Address(ip_address))	 
	                	 return ip_address;
	             }
	           }  
	    	 }
	     } catch (SocketException ex) {
	         Log.e(IPTAG, ex.toString());
	     }
		return nullAddress;
	} 
	
	public static NetworkStateReceiver getInstance()
	{
		return instance;
	}
	
	public void regNetworkReciever(Context context)
	{
		this.context=context;
		instance=this;
		IntentFilter mFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(this,mFilter);  
	}
	
	public void unregNetworkReciever(Context context)
	{
		context.unregisterReceiver(this);
	}
	
	public boolean isWifiConnected()
	{
		if(connectmanager==null)
			connectmanager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectmanager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
	}
	
	public static boolean isEthernetConnected()
	{
		if(connectmanager==null)
			connectmanager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectmanager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).isConnected();
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(connectmanager==null)
			connectmanager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		wifistate=connectmanager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
		ethernetstate=connectmanager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).isConnected();
		
		if(wifistate)
		{
			SettingVarible.networkstate=SettingVarible.NETWORKSTATE.WIFI;
			isConnect = false ;
		}
		else if (ethernetstate)
		{
			SettingVarible.networkstate=SettingVarible.NETWORKSTATE.ETHERNET;
			isConnect = false ;
		}
		
		/*if(wifistate||ethernetstate)
		{
			if(FirstConnect)
			{
				HomeActivity.getInstance().handler.sendEmptyMessage(HomeActivity.FIRSTNETWORK);
				FirstConnect=false;
			}	
		}
		else
		{
			SettingVarible.networkstate=SettingVarible.NETWORKSTATE.NONETWORK;
			if(SessionActivity.getInstance()!=null)
			{
				SessionActivity.getInstance().uiHandler.sendEmptyMessage(SessionActivity.UIHandler.NETWORK_DISCONNECT);
			}
		}
		HomeActivity.getInstance().handler.sendEmptyMessage(HomeActivity.SWITCHNETWORKICON);*/
		SettingVarible.ipAddress=getLocalIpAddress(context);
	}
}
