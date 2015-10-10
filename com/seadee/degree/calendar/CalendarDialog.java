package com.seadee.degree.calendar;

/*import android.R;*/
import com.seadee.degree.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import com.seadee.degree.calendar.*;
import com.seadee.degree.calendar.CalendarView.calendarViewMessageListener;
import com.seadee.library.utils.FloatDialog;

import android.widget.TextView;

public class CalendarDialog extends Dialog implements calendarViewMessageListener{

	private Context context; 
	private  CalendarView  calendarView ;
	public static int  dismissDlalogKey =  0x1;
	public CalendarDialog(Context context) {
		super(context);
		this.context = context;
		init();
	}
    public CalendarDialog(Context context, int theme){
	        super(context, theme);
	        this.context = context;
	        init();
	    }

    /*public CalendarDialog(Context context, boolean ischoke)
	{
		super(context, ischoke);
        this.context = context;
        init();
	}
	*/
    private void init()
    {
    /*	this.setTitle(R.string.);*/
    	this.setTitle(R.string.selectFile) ;
    //	this.setSize(100, 300);
    	initCalendarView();
    }

    private void initCalendarView()
    {
    	
    	calendarView = new CalendarView(context);    	
    //	calendarView = (CalendarView)findViewById(R.id.calendarView1); 
    	calendarView.setCalendarMessageListener(this) ;
    }
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.calender_layout);  
    }
	private Handler calendarHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what == dismissDlalogKey)
			{
				dismiss();
			}
			super.handleMessage(msg);
		}
	};
	@Override
	public Handler getCalendarHandler() {
		return calendarHandler;
	}
 
	
}
