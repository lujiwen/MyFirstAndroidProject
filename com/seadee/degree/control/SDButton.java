package com.seadee.degree.control;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import com.seadee.degree.R;


public class SDButton extends Button {
	
	public enum STATE{disable,normal,selected,alarm};
	STATE state=STATE.normal;
	
	public SDButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}
	public SDButton(Context context, AttributeSet attrs ) {
		super(context, attrs );
		// TODO Auto-generated constructor stub
		init(context);
	}
	public SDButton(Context context ) {
		super(context,null);
		// TODO Auto-generated constructor stub
		init(context);
	}
	private void init(Context context)
	{
		setBackgroundResource(R.drawable.sd_ic_btn_normal);
		setState(state);		 
	}
	
	 public STATE setState(STATE state)
	 { 
		 switch(state)
			{
			case selected:
			     setBackgroundResource(R.drawable.sd_ic_btn_choose);
				 break;
			case normal:
				 setBackgroundResource(R.drawable.sd_ic_btn_normal);
				 break;
			case disable: 
				 setBackgroundResource(R.drawable.sd_ic_btn_break);
				break;
			case alarm: 
				setBackgroundResource(R.drawable.sd_ic_btn_stop) ;
			default: 
				break; 
			}
		 
		   this.state = state;
		   return state;   
	 }
	 public STATE getState()
	 {
		 return this.state;	 
	 }
	 
	 
}
