package com.seadee.degree.service;

 

import com.seadee.degree.R;

import android.R.color;
import android.graphics.Color;

public class SettingVarible {
	//the value of the degree or time coordinate been divided into 
	public static int degreeLevel=10,timeLevel=3; 
	
	//the min or max value of the show degree
	public static int min_degree=0,max_degree=500; 
	
	//persist
	//public static int warn_low_degree=100,warn_high_degree=400; 
	
	//the coordinate offset from the surfaceview.
	public static int offsetx=50,offsety=70;  
	
	//the width of the drawing lines. 
	public static int linewidth=4;
	
	//the interval time of each drawing action.
	public static int interval=1000;
	
	//the color of the drawing lines
	public static int line0Color=Color.GRAY, line1Color=Color.RED,line2Color=Color.YELLOW,line3Color=Color.MAGENTA,line4Color=Color.BLUE;
	
	//the color of the samplepoint;
	public static int sampleColor=Color.WHITE;
	
	//the color of the coordinate and the corlor of background in the surfaceview 
	public static int coordinateColor=Color.WHITE,backgroundColor=Color.BLACK;  
	
	public static int oneDegree1Color = Color.rgb(255,0,0);
	public static int oneDegree2Color = Color.rgb(0, 0, 255);
	public static int oneDegree3Color = Color.rgb(255, 255,0);
	public static int oneDegree4Color = Color.rgb(255, 127, 0);
	
	public static int twoDegree1color = Color.rgb(255,0,255);
	public static int twoDegree2color = Color.rgb(255,127,127);
	public static int twoDegree3color = Color.rgb(0, 255, 64);
	public static int twoDegree4color = Color.rgb(106, 58, 106);
	
	public static int threeDegree1color = Color.rgb(0, 127, 0);
	public static int threeDegree2color = Color.rgb(64, 0, 0);
	public static int threeDegree3color = Color.rgb(127, 127,127);
	public static int threeDegree4color = Color.rgb(0, 255, 255);
	

	public enum NETWORKSTATE{NONETWORK,WIFI,ETHERNET};
/*	public static Common.DISPLAY display;*/
	public static NETWORKSTATE networkstate;
	public static String ipAddress;
	public void refresh()
	{
		//...
	}
	
	
}
