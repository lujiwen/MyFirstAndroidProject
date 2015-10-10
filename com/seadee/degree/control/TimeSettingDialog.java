package com.seadee.degree.control;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.seadee.degree.R;
import com.seadee.degree.view.DegreeTopView;
import com.seadee.library.utils.FloatDialog;

public class TimeSettingDialog extends FloatDialog {

	private DatePicker datePicker ;
	private TimePicker timePicker;
	private Context context ;
	private DegreeTopView degreeTopView ;
	private  TextView dateTextView;
	public TimeSettingDialog(Context context, boolean ischoke) {
		super(context, ischoke);
		init(context);
	}
	public TimeSettingDialog(Context context, boolean ischoke, TextView datetv) 
	{
		super(context, ischoke);
		init(context);
		this.dateTextView = datetv; 
	}
	
	private void init(Context context )
	{
		setContentView(R.layout.timesetting_layout);
		datePicker = (DatePicker)findViewById(R.id.datePicker) ;
		timePicker = (TimePicker)findViewById(R.id.timePicker) ;
		this.context = context ;

		timePicker.setOnTimeChangedListener(onTimeChangedListener);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH) , onDateChangedListener) ;
	}
	private OnTimeChangedListener onTimeChangedListener = new OnTimeChangedListener() {
		public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			Log.e(hourOfDay+"",minute+"");
			Calendar calendar = Calendar.getInstance();
			Date date = new Date() ;
			calendar.setTime(date);			
			calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(),
						hourOfDay, minute, calendar.get(Calendar.SECOND));
			AlarmManager amAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE) ;
			amAlarmManager.setTime(calendar.getTimeInMillis());	
		}
	};
	
	private OnDateChangedListener onDateChangedListener = new OnDateChangedListener() {	
		@Override
		public void onDateChanged(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) 
		{   
			
  			Calendar calendar = Calendar.getInstance();	
			calendar.set(year, monthOfYear, dayOfMonth);
			AlarmManager amAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE) ;
			amAlarmManager.setTime(calendar.getTimeInMillis()); 
			long systime = System.currentTimeMillis();
			CharSequence date = DateFormat.format("yyyy/MM/dd", systime);			 
			dateTextView.setText(date); 
		}
	};

}
