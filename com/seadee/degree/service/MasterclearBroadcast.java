package com.seadee.degree.service;

import com.seadee.degree.activity.HomeActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
 
public class MasterclearBroadcast extends BroadcastReceiver {

	private Context context ;
	public MasterclearBroadcast(Context context )
	{
		this.context = context; 
	}
	public MasterclearBroadcast()
	{
		
	}	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String  action = intent.getAction();
		if(action.equals("android.intent.action.MASTER_CLEAR"))
		{
			PowerManager.materClear(context);
		}
	}

}
