package com.seadee.degree.control;

import com.seadee.degree.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class CommonButton extends Button {

	public CommonButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}
	public CommonButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	public CommonButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private void init(Context context) { 
		setBackgroundResource(R.drawable.sd_ic_btn_normal);		 
	}
	@Override 
	public boolean onTouchEvent(MotionEvent event) 
	{		 
		switch(event.getAction()) 
		{
		case MotionEvent.ACTION_DOWN: 	
			setBackgroundResource(R.drawable.sd_ic_btn_hover);
			break;
		case MotionEvent.ACTION_UP:
			setBackgroundResource(R.drawable.sd_ic_btn_normal);
			break;
		}		
	    return super.onTouchEvent(event);
	}
	
	 
	
}
