package com.seadee.degree.control;

import com.seadee.degree.R;
import com.seadee.degree.service.LibDegree;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import  android.widget.ImageButton;

public class Buzzer extends ImageButton {

	public	enum  BuzzerState {normal ,alarm,disable};
	private BuzzerState state ;
	private boolean buzzEnabled ; // false: buzzer wouldn't buzz when alarm happend
	public Buzzer(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		init();
	}

	public Buzzer(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}
	public Buzzer(Context context) 
	{
		super(context);
		init();
	}
	private void init()
	{
		this.state = BuzzerState.normal ;
		setState(BuzzerState.normal);
		buzzEnabled = true ;
	}
	public void  setBackground(BuzzerState state )
	{
		switch(state)
		{
			case  normal:
				this.setBackgroundResource(R.drawable.green_buzzer);
				break ;
			case alarm:
				this.setBackgroundResource(R.drawable.red_buzzer);
				break;
			case disable:
				this.setBackgroundResource(R.drawable.disable_buzzer) ;
			default:
				break;
		}
	}
	public void  setState(BuzzerState state )
	{
		switch(state)
		{
			case  normal:
				this.state = BuzzerState.normal ;
				this.buzzEnabled = true ;
				this.setBackground(BuzzerState.normal);
				break ;
			case alarm:
				this.state = BuzzerState.alarm ;
				this.buzzEnabled = true ;
				this.setBackground(BuzzerState.alarm);		
				break;
			case disable:
				this.state = BuzzerState.disable ;
				this.buzzEnabled = false ;
				this.setBackground(BuzzerState.disable);
			default:
				break;
		}		
	}
	
	public BuzzerState getBuzzerState()
	{
		return state ;
	}
	public void startAlarm()
	{		
		Log.e("buzzeralarm", "start!") ;
		this.state = BuzzerState.alarm ;
				
		if(buzzEnabled) 
		{
			LibDegree.setSomeFlag(3);
		}
		//¿ªÏß³Ì
		new Thread()
		{
			@Override 
			public void run()
			{				
				int i=0;
				while(state == BuzzerState.alarm)
				{				
					if(i==0)
					{
						i =1;
					}
					else 
					{
						i= 0;
					}
					try {
						synchronized(this)
						{
							alarmHandler.sendEmptyMessage(i);							 
						}
						sleep(1000);
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				}	
			}					
		}.start();
	}
	
	private Handler  alarmHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what%2 == 0)
			{
				setBackgroundResource(R.drawable.red_buzzer);
			}
			else 
			{
				if(!buzzEnabled)
				{
					setBackgroundResource(R.drawable.disable_buzzer);
				}
				else 
				{
					setBackgroundResource(R.drawable.green_buzzer) ;
				}
			}
			super.handleMessage(msg);
		}
	};
	public void stopAlarm()
	{		
		Log.e("BuzzerAlarm", "stop!") ;
	
		if(buzzEnabled)
		{
			setState(BuzzerState.normal) ;
		}
		else 
		{
			setState(BuzzerState.disable);
		}
		LibDegree.setSomeFlag(0);
	}
}
