package com.seadee.degree.control;

import java.text.Format;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.seadee.degree.activity.HomeActivity;
import com.seadee.degree.core.ChartGraphView;
import com.seadee.degree.core.ChartViewThread;
import com.seadee.degree.core.DegreeView;
import com.seadee.degree.view.DegreeTopView;

public class VerticalScale extends View  {
	
	ViewGroup.LayoutParams lp;
	public final static String TAG = "VerticalScale";
	private boolean scrollingStarted ;
	private float lastTouchEventX = 75;  //È¥ÆÁÄ»µÄ¾ø¶Ô×ø±ê
 	private static float leftBorder ; // ×ó±ßµÄÆÁÄ»±ßÑØ×ø±ê 
 	public int startX;
 	public int endX;
 	private  static float  offsetx =0;
 	private  int[] scaleData ;
 	int[] datas ;
	int[][] d =new int[3][6] ;   
	int pos = 0 ;
	private int maxPointNum ;
	private TextView hint;
	public String hintString ;
	private Context context ;
	public  float chartGraphWidth;
	public VerticalScale(Context context) 
	{
		super(context);
		this.context = context ;
	 	init();	
	}
	void init()
	{		
		hint = new TextView(this.context);
		lp = new ViewGroup.LayoutParams(50,LayoutParams.MATCH_PARENT);	
		this.setLayoutParams(lp);
	
		this.layout((int)lastTouchEventX, 100, (int)(lastTouchEventX+10), this.getHeight());	 	 
		startX = ChartGraphView.getstartX(); 
		chartGraphWidth =  ChartGraphView.chartGraphWidth; // no success 
		//chartGraphWidth = 790;
		//endX = startX +chartGraphWidth; // width: 790    		
		scaleData  = new int[6]; 
		leftBorder = lastTouchEventX;
		datas = new int[ChartGraphView.maxDataCount];
		this.setX(startX);  
		maxPointNum = ChartGraphView.maxPointNum ;	
	}
	 
	protected void onDraw(Canvas canvas)
	{
		if(DegreeView.enableScale) //draw text when scale enabled
		{	
			if(ChartGraphView.darwHistory)
			{
				super.onDraw(canvas);	
				canvas.save();
				Paint paint = new Paint();
				paint.setColor(Color.RED);
				Rect rect = new Rect();
				rect.left =  0;
				rect.top  = 30;
				rect.right = 10;
				rect.bottom = 40;
				canvas.drawRect(rect, paint);	
				canvas.drawLine(5,40, 5, this.getHeight()-40, paint);
			
				paint.setColor(Color.WHITE);
				Rect textRect = new Rect() ;
				textRect.left = 0 ;
				textRect.right = 50;
				textRect.top = 10;
				textRect.bottom = 25;
				canvas.drawRect(textRect, paint) ;
				paint.setColor(Color.BLACK);
				canvas.drawText(hintString, 0, 20, paint);
				canvas.restore();	
			}			
		}
						
	}
	
	int[] calculateDegree()
	{
		GraphViewSeries series  =  ChartGraphView.getSeriesData(3);
		GraphViewDataInterface[]  SeriesDatas=  series.getSeriesData();
		int length = ChartGraphView.maxDataCount;
		int[] tempDatas = new int[length];
		
		for(int i=length;i>0;i--)
		{
			int  x = (int) SeriesDatas[length-i].getX();
			tempDatas[x] = (int) SeriesDatas[length-i].getY();
		}		
		return tempDatas;
	}
 
	private void onMoveGesture(float f,float evnetX) 
	{
		float coordx =  lastTouchEventX+f ;
		
		if(coordx <startX)
		{
			this.setX(startX-leftBorder);
		}
 		else if(coordx > endX)
		{
			this.setX(endX-leftBorder);
		} 
		else 
		{
			this.setX(coordx-leftBorder);	
		}
		double coord = (double)(coordx -startX) ;				
		if(coord < 0 )
		{
			coord = 0.0 ;
		}
		else if(coord > 790)
		{
			coord = 790.0 ;			
		}
		if(ChartGraphView.darwHistory)
		{
			double percent = (double)(coord/chartGraphWidth);
			pos =  (int) ((ChartGraphView.maxPointNum-1)*percent) ;
			int len = DegreeTopView.selectedCount ;
			int[] select = DegreeTopView.Selected;
			
			// show 3 transmiter's history data at most 
			for(int i=0;i<3;i++)
			{
				 if(select[i] != 0)
				 {
					d[i] = ChartGraphView.getSeriesData(select[i], pos);	
					if(d[i][5]!=0)
					{
						scaleData[0] =  d[i][0];		
						scaleData[1] =  d[i][1];
						scaleData[2] =  d[i][2];
						scaleData[3] =  d[i][3];
						scaleData[4] =  d[i][4]; 
						hintString =  converTimeString(d[i][5]);
					 	HomeActivity.getInstance().updateScaleDegree(select[i],scaleData,i);
					}
					else 
					{
						hintString = "ÔÝÍ£";
					}
				}
			 }						 
		}
		lastTouchEventX = evnetX;		
	    invalidate(); 
	}
	private String converTimeString(int time)
	{
		String timeString = "" ;
		String hour ,minute,second;
		second = String.format("%02d",time%100) ;
		minute = String.format("%02d", time/100%100) ;
		hour = String.format("%02d", time/10000%100);
		
		timeString = hour+":"+minute+":"+second;
		return timeString ;
 	}
 	@Override
	public boolean onTouchEvent(MotionEvent event) 
 	{
 		int action = event.getAction();
 		boolean handled = false ;
 		if((!handled)&&(DegreeView.enableScale))
 		{
 			switch(action)
 	 		{
 	 			case MotionEvent.ACTION_DOWN: 
 	 				lastTouchEventX = event.getRawX();
 	 				scrollingStarted = true ;
 	 				handled = true ;
 	 				break;
 	 			case MotionEvent.ACTION_MOVE:
 	 				if(scrollingStarted)
 	 				{	 	
 	 					offsetx = event.getRawX() - lastTouchEventX;
 	 					onMoveGesture(offsetx,event.getRawX());
 	 					handled = true ;
 	 				}
 	 				break ;
 	 			case MotionEvent.ACTION_UP:
					scrollingStarted = false;
					handled = true; 	 				
 	 				break;
 	 		}
 		}
 		else 
 		{
 			 scrollingStarted = false ;
 			 lastTouchEventX = 0 ;
 		}
 		return handled;
 	}
 

	
}
