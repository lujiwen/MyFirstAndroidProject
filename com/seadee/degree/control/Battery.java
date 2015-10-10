package com.seadee.degree.control;

import com.seadee.degree.R;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Battery extends LinearLayout  {
	
	private static final String  tag = "Battery";
	
	private  Context context ;
	LayoutInflater layoutInflater;
	ViewGroup.LayoutParams lp;
	public   ImageView batteryImg ;
	private   TextView powerText ;
	
	private final static float  MaxPowerVolt = 4.2f;
	private final static float  MinPowerVolt = 3.2f;
	private  float powerValue  ;
	
	public Battery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	public Battery(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public Battery(Context context) {
		super(context);
		init(context);
	}
	public Battery(Context context,float power) {
		super(context);
		this.powerValue = power;
		init(context);	
	}
 
	private void init(Context context)
	{	
		this.context = context ;
		this.layoutInflater = layoutInflater.from(context);
        layoutInflater.inflate(R.layout.battery_layout, this);
        batteryImg = (ImageView)findViewById(R.id.battery);
	    powerText = (TextView)findViewById(R.id.power);
	    powerText.setTextSize(17);	
	    //setPower(this.powerValue);
	    setBateryDisable();
	}
	
	public float getPower()
	{
		return this.powerValue;
	}
	
	public void setPower(int power)
	{
		this.powerValue = power ;
		setBatteryState(power);
	}
	
	public void setBateryDisable()
	{
		batteryImg.setBackgroundResource(R.drawable.not_receice_power);
		powerText.setText("0%");
	}
	
	/**
	 * set the state of the battery to show how much batttery power left 
	 * @param  powerPercent  the letf percentage of power of the battery 
	 * */
	private void setBatteryState(int powerPercent)
	{
		// £”‡µÁ¡ø		
		if(powerPercent<=10)
		{
			this.batteryImg.setBackgroundResource(R.drawable.power1);
			this.powerText.setText(powerPercent+"%");
		}
		else if((powerPercent>=0)&&(powerPercent<20)) 
		{
			this.batteryImg.setBackgroundResource(R.drawable.power2);			
			this.powerText.setText(powerPercent+"%");	
		}
		else if((powerPercent>=20)&&(powerPercent<30)) 
		{
			this.batteryImg.setBackgroundResource(R.drawable.power3);
			this.powerText.setText(powerPercent+"%");	
		}
		else if((powerPercent>=30)&&(powerPercent<40)) 
		{
			this.batteryImg.setBackgroundResource(R.drawable.power4);
			this.powerText.setText(powerPercent+"%");	
		}
		else if((powerPercent>=40)&&(powerPercent<50)) 
		{
			this.batteryImg.setBackgroundResource(R.drawable.power5);
			this.powerText.setText(powerPercent+"%");	
		}
		else if((powerPercent>=50)&&(powerPercent<60)) 
		{			
			this.batteryImg.setBackgroundResource(R.drawable.power6);
			this.powerText.setText(powerPercent+"%");			
		}
		else if((powerPercent>=60)&&(powerPercent<70)) 
		{
			this.batteryImg.setBackgroundResource(R.drawable.power7);
			this.powerText.setText(powerPercent+"%");	
		}
		else if((powerPercent>=70)&&(powerPercent<80)) 
		{
			this.batteryImg.setBackgroundResource(R.drawable.power8);
			this.powerText.setText(powerPercent+"%");	
		}
		else if((powerPercent>=80)&&(powerPercent<90)) 
		{
			this.batteryImg.setBackgroundResource(R.drawable.power9);
			this.powerText.setText(powerPercent+"%");	
		}
		else if((powerPercent>=90)&&(powerPercent<100)) 
		{
			 this.batteryImg.setBackgroundResource(R.drawable.power9);
			 this.powerText.setText(powerPercent+"%");	
		}
		else if(powerPercent >= 100) 
		{
			this.batteryImg.setBackgroundResource(R.drawable.power10);			
			this.powerText.setText(powerPercent+"%");	
		}		
	} 
 
 
 
}
