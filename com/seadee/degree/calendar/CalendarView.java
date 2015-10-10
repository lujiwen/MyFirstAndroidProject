package com.seadee.degree.calendar;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.seadee.degree.R;
import com.seadee.degree.activity.HomeActivity;
import com.seadee.degree.core.ChartGraphView;
import com.seadee.degree.service.HandleFile;
import com.seadee.degree.service.UdiskStateReceiver;
import com.seadee.degree.view.DegreeTopView;


public class CalendarView extends View implements View.OnTouchListener{

	public  interface calendarViewMessageListener
	{
		abstract Handler getCalendarHandler(); 
	}
	 private static calendarViewMessageListener calendarListener;
	 public void  setCalendarMessageListener(calendarViewMessageListener listenner)
	 {
		 calendarListener = listenner; 
	 }
	 
	 public Handler getCalendarHandler()
	 {
		 return  calendarListener.getCalendarHandler(); 
	 }
	 
private Context context; 
private final static String TAG = "MyCalendar";
private Date selectedStartDate;
private Date selectedEndDate;
private Date curDate; // 当前日历显示的月
private Date today; // 今天的日期文字显示红色
public static  Date downDate; // 手指按下状态时临时日期
private Date showFirstDate, showLastDate; // 日历显示的第一个日期和最后一个日期
private int downIndex; // 按下的格子索引
private Calendar calendar;
private Surface surface;
private int[] date = new int[42]; // 日历显示数字
private int curStartIndex, curEndIndex; // 当前显示的日历起始的索引
private boolean completed = false; // 为false表示只选择了开始日期，true表示结束日期也选择了
private String fileNotExist ;
public CalendarView(Context context, AttributeSet attrs, int defStyle) 
{
	super(context, attrs, defStyle);
	this.context = context;
	init();
}
public CalendarView(Context context, AttributeSet attrs )
{
	super(context, attrs );
	this.context = context;
	init();
}
public CalendarView(Context context) 
{
	super(context);
	this.context = context;
	init();
}
private void init()
{
	curDate = selectedStartDate = selectedEndDate = today = new Date();
	calendar = Calendar.getInstance();
	calendar.setTime(curDate);
	
	surface = new Surface();
	surface.density = getResources().getDisplayMetrics().density;
	setBackgroundColor(surface.bgColor);
	setOnTouchListener(this);
	
	fileNotExist = getResources().getString(R.string.fileNotexist) ;
}
@Override
public boolean onTouch(View v, MotionEvent event) 
{
	switch (event.getAction()) 
	{
		case MotionEvent.ACTION_DOWN:
			if(setSelectedDateByCoor(event.getX(), event.getY()) != null) 
			{ 
			 	//弹出文件操作对话框 
				AlertDialog.Builder dialBuilder =	new AlertDialog.Builder(context);
				dialBuilder.setTitle(getResources().getString(R.string.selectOperation));
				dialBuilder.setSingleChoiceItems(new String[] {getResources().getString(R.string.checkFile),getResources().getString(R.string.deleteFile),getResources().getString(R.string.exportFile)}, 0, 
				new DialogInterface.OnClickListener() 
				{				
					@Override
					public void onClick(DialogInterface dialog, int which) {
					  switch(which)
					  {
						  case 0 :																  
							  if(HandleFile.checkFile(downDate))
							  { 
								//  DegreeTopView.initSelectStatus();
								  ChartGraphView.darwHistory = true ;
								  DegreeTopView.isRun = false ;						
								  HomeActivity.getInstance().initBtnState();							
								  Toast.makeText(context, getResources().getString(R.string.historySelect), 1000).show();
								  calendarListener.getCalendarHandler().sendEmptyMessage(CalendarDialog.dismissDlalogKey) ;
							  
							  }
							  else 
							  {
								   Toast.makeText(context, fileNotExist, Toast.LENGTH_SHORT).show();
							  }		
							  dialog.dismiss();
							
							  break;
						  case 1:
							  if( HandleFile.checkFile(downDate))
							  {
								  if(HandleFile.deleteFile(downDate))
									 {
										 Toast.makeText(context, getResources().getString(R.string.deleteSuccess) , 1000).show();
										 dialog.dismiss() ;
										 calendarListener.getCalendarHandler().sendEmptyMessage(CalendarDialog.dismissDlalogKey);
									 }
									 else
									 {
										 Toast.makeText(context, getResources().getString(R.string.deleteFailed), 1000).show();
										 dialog.dismiss() ;
									 }								
							  }
							  else 
							  {
								  Toast.makeText(context,fileNotExist, Toast.LENGTH_SHORT).show() ;
								  dialog.dismiss() ;
							  }						
							 break;
						  case 2:	
							  if(HandleFile.checkFile(downDate))
							  {
								  UdiskStateReceiver receiver = new UdiskStateReceiver(context) ;
								  if(!receiver.isUDiskMounted())
								  {
									  Toast.makeText(context,getResources().getString(R.string.noUdiskDetected),Toast.LENGTH_SHORT).show() ;								
								  }
								  else 
								  {
										String externalPath =  UdiskStateReceiver.getUsbStoragePath(context);							
										try {
											//Log.e("export", "start~!");
											if(HandleFile.ExportFile(downDate,externalPath,context))
											{
												Toast.makeText(context, getResources().getString(R.string.exportSuccess), Toast.LENGTH_SHORT).show();							    	
												//Log.e("USB","成功导出文件到外部设备！" );
												dialog.dismiss();
												calendarListener.getCalendarHandler().sendEmptyMessage(CalendarDialog.dismissDlalogKey);
											}
											else
											{
												Toast.makeText(context, getResources().getString(R.string.exportFailed), Toast.LENGTH_SHORT).show();
												//Log.e("USB","失败导出文件到外部设备！" );
												dialog.dismiss();												 
											}
										//	Log.e("export", "end~!");
										} catch (IOException e) {
											e.printStackTrace();
										}
								  }																					    									 
							  }
							  else 
							  {
								  Toast.makeText(context,fileNotExist, Toast.LENGTH_SHORT).show() ;
								  dialog.dismiss() ;
							  }							   
							  break;
						  default:						
							  break;
							  }
					  }							  				
					}						 
				 );
				dialBuilder.setNegativeButton(getResources().getString(R.string.cancel), null); 
				dialBuilder.show();
			}	
	 		break;	
	}
	return true;
}
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
{
	surface.width = (int) (200 * surface.density);
	surface.height = (int) (215 * surface.density);
	if (View.MeasureSpec.getMode(widthMeasureSpec) == View.MeasureSpec.EXACTLY)
	{
		surface.width = View.MeasureSpec.getSize(widthMeasureSpec);
	}
	if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.EXACTLY) 
	{
		surface.height = View.MeasureSpec.getSize(heightMeasureSpec);
	}
	
	widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.width, View.MeasureSpec.EXACTLY);
	heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(surface.height, View.MeasureSpec.EXACTLY);
	setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
}
@Override
protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
	Log.d(TAG, "[onLayout] changed:" + (changed ? "new size" : "not change") + " left:" + left + 
			" top:" + top + " right:" + right + " bottom:" + bottom);
	
	if (changed) 
	{
		surface.init();
	}
	super.onLayout(changed, left, top, right, bottom);
}
@Override
protected void onDraw(Canvas canvas) 
	{
		Log.d(TAG, "onDraw");
		// 画框
		canvas.drawPath(surface.boxPath, surface.borderPaint);
		// 年月
		String monthText = getYearAndmonth();
		float textWidth = surface.monthPaint.measureText(monthText);
		canvas.drawText(monthText, (surface.width - textWidth)/2f, surface.monthHeight*3/4f, surface.monthPaint);
		// 上一月/下一月
		canvas.drawPath(surface.preMonthBtnPath, surface.monthChangeBtnPaint);
		canvas.drawPath(surface.nextMonthBtnPath, surface.monthChangeBtnPaint);
		// 星期
		float weekTextY = surface.monthHeight + surface.weekHeight*3/4f;
		for (int i=0; i<surface.weekText.length; i++)
		{
			float weekTextX = i * surface.cellWidth + (surface.cellWidth - surface.weekPaint.measureText(surface.weekText[i]))/2f;
			canvas.drawText(surface.weekText[i], weekTextX, weekTextY, surface.weekPaint);
		}
		// 计算日期
		calculateDate();
		// 按下状态，选择状态背景色
		drawDownOrSelectedBg(canvas);
		// write date number
		int todayIndex = -1;
		calendar.setTime(curDate);
		String curYearAndMonth = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH);
		 
		calendar.setTime(today);
		String todayYearAndMonth = calendar.get(Calendar.YEAR) + "" + calendar.get(Calendar.MONTH);
		if (curYearAndMonth.equals(todayYearAndMonth))
		{
			int todayNumber = calendar.get(Calendar.DAY_OF_MONTH);
			todayIndex = curStartIndex + todayNumber - 1;
		}
 
		//绘制一个月日期 
		for (int i=0; i<42; i++) 
		{
			int color = surface.textColor;
			if ((isLastMonth(i))||(isNextMonth(i)))  //不属于本月的日期用浅色
			{
				color = surface.borderColor;
			} 
			else 
			{
				if(HandleFile.isFileExist(getYearAndMonth(curDate)*100+date[i]))//该日文件是否存在存在 *100 便于将整形转化为字符串
				{
					color = surface.fileExistColor;   //文件存在就用红色突出显示
				}								 			
			}
			drawCellText(canvas, i, date[i] + "", color);
		}		
		super.onDraw(canvas);
}

private int getYearAndMonth(Date curdate)
 { 
	   calendar.setTime(curdate );
	   int year = calendar.get(calendar.YEAR);
	   int month = calendar.get(calendar.MONTH)+1;  // 月份显示的是一月开头的第一天的月份 需要+1
	 
	   return year*100+month;
 }

private void calculateDate()
{
	calendar.setTime(curDate);
	calendar.set(Calendar.DAY_OF_MONTH, 1);
	
	int dayInWeek = calendar.get(Calendar.DAY_OF_WEEK);

	int monthStart = dayInWeek;
	if (monthStart == 1) 
	{
		monthStart = 8;
	}
	monthStart -= 2;
	curStartIndex = monthStart;
	date[monthStart] = 1;
	// last month
	if (monthStart > 0) 
	{
		calendar.set(Calendar.DAY_OF_MONTH, 0);
		int dayInmonth = calendar.get(Calendar.DAY_OF_MONTH);
		for (int i=monthStart-1; i>=0; i--) 
		{
			date[i] = dayInmonth;
			dayInmonth--;
		}
		calendar.set(Calendar.DAY_OF_MONTH, date[0]);
	}
	showFirstDate = calendar.getTime();
	// this month
	calendar.setTime(curDate);
	calendar.add(Calendar.MONTH, 1);
	calendar.set(Calendar.DAY_OF_MONTH, 0);
	 
	int monthDay = calendar.get(Calendar.DAY_OF_MONTH);
	for (int i=1; i<monthDay; i++)
	{
		date[monthStart + i] = i + 1;
	}
	curEndIndex = monthStart + monthDay;
	// next month
	for (int i=monthStart + monthDay; i<42; i++)
	{
		date[i] = i - (monthStart + monthDay) + 1;
	}
	if (curEndIndex < 42) {
	// 显示了下一月的
	calendar.add(Calendar.DAY_OF_MONTH, 1);
	}
	calendar.set(Calendar.DAY_OF_MONTH, date[41]);
	showLastDate = calendar.getTime();
}
/**
* 
* @param canvas
* @param index
* @param text
*/
private void drawCellText(Canvas canvas, int index, String text, int color) 
{
	int x = getXByIndex(index);
	int y = getYByIndex(index);
	surface.datePaint.setColor(color);
	surface.datePaint.setTextSize(30);
	float cellY = surface.monthHeight + surface.weekHeight + (y - 1) * surface.cellHeight + surface.cellHeight * 3/4f;
	float cellX = (surface.cellWidth * (x-1)) + (surface.cellWidth - surface.datePaint.measureText(text))/2f;
	canvas.drawText(text, cellX, cellY, surface.datePaint);
}
/**
* 
* @param canvas
* @param index
* @param color 
*/
private void drawCellBg(Canvas canvas, int index, int color)
{
	int x = getXByIndex(index);
	int y = getYByIndex(index);
	surface.cellBgPaint.setColor(color);
	float left = surface.cellWidth * (x - 1) + surface.borderWidth;
	float top = surface.monthHeight + surface.weekHeight + (y - 1) * surface.cellHeight + surface.borderWidth;
	float right = left + surface.cellWidth - surface.borderWidth;
	float bottom = 	top + surface.cellHeight - surface.borderWidth;
	canvas.drawRect(left, top,right,bottom, surface.cellBgPaint);
}

private void drawDownOrSelectedBg(Canvas canvas) 
{
	// down and not up
	if (downDate != null) 
	{
		drawCellBg(canvas, downIndex, surface.cellDownColor);
	}
// selected bg color
	if (!selectedEndDate.before(showFirstDate) && !selectedStartDate.after(showLastDate))
	{
		int[] section = new int[]{-1, -1};
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, -1);
		findSelectedIndex(0, curStartIndex, calendar, section);
		if (section[1] == -1) {
		calendar.setTime(curDate);
		findSelectedIndex(curStartIndex, curEndIndex, calendar, section);
	}
	if (section[1] == -1)
	{
		calendar.setTime(curDate);
		calendar.add(Calendar.MONTH, 1);
		findSelectedIndex(curEndIndex, 42, calendar, section);
	}
	if (section[0] == -1)  section[0] = 0; 
	if (section[1] == -1)  section[1] = 41; 
	for (int i=section[0]; i<=section[1]; i++) 
	{
		drawCellBg(canvas, i, surface.cellSelectedColor);
	}
}
}
 
private void findSelectedIndex(int startIndex, int endIndex, Calendar calendar, int[] section) 
{
	for (int i=startIndex; i<endIndex; i++) 
	{
		calendar.set(Calendar.DAY_OF_MONTH, date[i]);
		Date temp = calendar.getTime();
	 
		if (temp.compareTo(selectedStartDate) == 0)
		{
			section[0] = i;
		}
		if (temp.compareTo(selectedEndDate) == 0) 
		{
			section[1] = i;
			return;
		}
	}
}
/*public Date getSelectedStartDate() 
{
	return selectedStartDate;
}
public Date getSelectedEndDate()
{
	return selectedEndDate;
}*/
private boolean isLastMonth(int i) 
{
	if (i < curStartIndex) 
	{
		return true;
	}
	return false;
}
private boolean isNextMonth(int i)
{
	if (i >= curEndIndex)
	{
		return true;
	}
	return false;
}
private int getXByIndex(int i) 
{
	return i%7 + 1; // 1 2 3 4 5 6 7
} 
private int getYByIndex(int i)
{
	return i/7 + 1; // 1 2 3 4 5 6
}
// 获得当前应该显示的年月
private String getYearAndmonth()
{
	calendar.setTime(curDate);
	int year = calendar.get(Calendar.YEAR);
	int month = calendar.get(Calendar.MONTH) + 1;
	return year+ getResources().getString(R.string.year)/*"年" */+ month + getResources().getString(R.string.month)/*"月"*/;
}


private Date  setSelectedDateByCoor(float x, float y)
{
	 Date  m_date = null ;  
	// change month
	if (y < surface.monthHeight) 
	{		
		// pre month
		if (x < surface.monthChangeWidth)
		{
			calendar.setTime(curDate);
			calendar.add(Calendar.MONTH, -1);
			curDate = calendar.getTime();
		} 
		// next month
		else if (x > surface.width - surface.monthChangeWidth)
		{
			calendar.setTime(curDate);
			calendar.add(Calendar.MONTH, 1);
			curDate = calendar.getTime();
		}
	 
	}
	// cell click down
	if (y > surface.monthHeight + surface.weekHeight) 
	{
		int m = (int) (Math.floor(x/surface.cellWidth) + 1);
		int n = (int) (Math.floor((y - (surface.monthHeight + surface.weekHeight))/new Float(surface.cellHeight)) + 1);
		downIndex = (n-1) * 7 + m - 1;
 
		calendar.setTime(curDate);
		if (isLastMonth(downIndex))
		{
			calendar.add(Calendar.MONTH, -1);
		} 
		else if (isNextMonth(downIndex))
		{
			calendar.add(Calendar.MONTH, 1);
		}
		calendar.set(Calendar.DAY_OF_MONTH, date[downIndex]);
		downDate = calendar.getTime();
	    m_date =  downDate ;
	}
		invalidate();
		return m_date ;
}


/**
* 
* 1. 布局尺寸
* 2. 文字颜色，大小
* 3. 当前日期的颜色，选择的日期颜色 
*/
private class Surface {
public float density;
public int width; // 整个控件的宽度
public int height; // 整个控件的高度
public float monthHeight; // 显示月的高度 
public float monthChangeWidth; // 上一月、下一月按钮宽度
public float weekHeight; // 显示星期的高度
public float cellWidth; // 日期方框宽度
public float cellHeight; // 日期方框高度
public float borderWidth;
public int bgColor = Color.parseColor("#FFFFFF");
private int textColor = Color.BLACK;
private int textColorUnimportant = Color.parseColor("#666666");
private int btnColor = Color.parseColor("#666666");
private int borderColor = Color.parseColor("#CCCCCC");
public int todayNumberColor = Color.RED;
public int  fileExistColor = Color.RED;
public int cellDownColor = Color.parseColor("#CCFFFF");
public int cellSelectedColor = Color.parseColor("#99CCFF");

public Paint borderPaint;
public Paint monthPaint;
public Paint weekPaint;
public Paint datePaint;
public Paint monthChangeBtnPaint;
public Paint cellBgPaint;
public Path boxPath; // 边框路径
public Path preMonthBtnPath; // 上一月按钮三角形
public Path nextMonthBtnPath; // 下一月按钮三角形
public String[] weekText    ;

	public void init()
	{
		float temp = height / 7f;
		monthHeight = (float) ((temp + temp*0.3f) * 0.6);
		monthChangeWidth = monthHeight * 1.5f;
		weekHeight = (float) ((temp + temp*0.3f) * 0.4);
		cellHeight = (height - monthHeight - weekHeight) / 6f;
		cellWidth = width / 7f;
		borderPaint = new Paint();
		borderPaint.setColor(borderColor);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderWidth = (float) (0.5 * density);
	 
		borderWidth = borderWidth < 1 ? 1 : borderWidth;
		borderPaint.setStrokeWidth(borderWidth);
		monthPaint = new Paint();
		monthPaint.setColor(textColor);
		monthPaint.setAntiAlias(true);
		float textSize = cellHeight * 0.4f;
 
		monthPaint.setTextSize(textSize);
		monthPaint.setTypeface(Typeface.DEFAULT_BOLD);
		weekPaint = new Paint();
		weekPaint.setColor(textColorUnimportant);
		weekPaint.setAntiAlias(true);
		float weekTextSize = weekHeight * 0.6f;		
		weekPaint.setTextSize(weekTextSize);
		weekPaint.setTypeface(Typeface.DEFAULT_BOLD);
		
		datePaint = new Paint();
		datePaint.setColor(textColor);
		datePaint.setAntiAlias(true);
		float cellTextSize = cellHeight * 0.5f;
		datePaint.setTextSize(cellTextSize);
 
		boxPath = new Path();
		boxPath.addRect(0, 0, width, height, Direction.CW);
		boxPath.moveTo(0, monthHeight);
		boxPath.rLineTo(width, 0);
		boxPath.moveTo(0, monthHeight + weekHeight);
		boxPath.rLineTo(width, 0);
		weekText = new String[7] ;
		weekText[0] = getResources().getString(R.string.monday) ;
		weekText[1] = getResources().getString(R.string.Tuesday) ;
		weekText[2] =getResources().getString(R.string.wednesday);
		weekText[3] =getResources().getString(R.string.thursday);
		weekText[4] =getResources().getString(R.string.Friday) ;
		weekText[5] =getResources().getString(R.string.saturday) ;
		weekText[6] =getResources().getString(R.string.sunday) ;
 
		for (int i=1; i<6; i++)
		{
			boxPath.moveTo(0, monthHeight + weekHeight + i*cellHeight);
			boxPath.rLineTo(width, 0);
			boxPath.moveTo(i*cellWidth, monthHeight);
			boxPath.rLineTo(0, height - monthHeight);
		}
		boxPath.moveTo(6*cellWidth, monthHeight);
		boxPath.rLineTo(0, height - monthHeight);
		preMonthBtnPath = new Path();
		int btnHeight = (int) (monthHeight * 0.6f);
		
		preMonthBtnPath.moveTo(monthChangeWidth/2f, monthHeight/2f);
		preMonthBtnPath.rLineTo(btnHeight/2f, -btnHeight/2f);
		preMonthBtnPath.rLineTo(0, btnHeight);
		preMonthBtnPath.close();
		
		nextMonthBtnPath = new Path();
		nextMonthBtnPath.moveTo(width-monthChangeWidth/2f, monthHeight/2f);
		nextMonthBtnPath.rLineTo(-btnHeight/2f, -btnHeight/2f);
		nextMonthBtnPath.rLineTo(0, btnHeight);
		nextMonthBtnPath.close();
		
		monthChangeBtnPaint = new Paint();
		monthChangeBtnPaint.setAntiAlias(true);
		monthChangeBtnPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		monthChangeBtnPaint.setColor(btnColor);
		
		cellBgPaint = new Paint();
		cellBgPaint.setAntiAlias(true);
		cellBgPaint.setStyle(Paint.Style.FILL);
		cellBgPaint.setColor(cellSelectedColor);
	}
  }
}