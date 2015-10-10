package com.seadee.degree.control;

import android.content.Context;
import android.util.AttributeSet;

public class Transmitter extends SDButton {

	private Context context;
	private  boolean isPaired ;
	private  boolean isAlarming ;
	private  boolean isSelected;
	private  boolean  isStarted;
	private  boolean paired ;
	private  int position ;
	private int boardDegree;
	private int[] degrees = new int[4] ;
	private boolean isLowVolt ; 
	private int bateryPower ;
	private boolean isDisconnected ;
	
	
	public Transmitter(Context context ) {
		super(context );
		init(context);
	}

	public Transmitter(Context context, AttributeSet attrs ) {
		super(context, attrs );
		init(context);
	}
	public Transmitter(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	private void init(Context context)
	{
		this.context = context ;
		
	}
	
}
