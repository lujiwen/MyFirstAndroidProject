package com.seadee.degree.control;

import java.text.DecimalFormat;

import com.seadee.degree.core.ChartGraphView;
import com.seadee.degree.core.DegreeView;
import com.seadee.degree.view.DegreeLeftView;

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

public class HorizontalScale extends View  {
	ViewGroup.LayoutParams lp;
	
	public final static String TAG = "HorizontalScale";
	private boolean scrollingStarted ;
	private  float  lastTouchEventY = 70;
	public static float topBorder;
	private static float offsety = 0;
	private  String hintString = "" ; 
	private int startY ;
	public int coordTop ;
	public int coordBottom ;
	public float chartGraphHeight ;
	
	public HorizontalScale(Context context) 
	{
		super(context);
	 	init();	
	}

	void init()
	{		
		startY = ChartGraphView.getstartY(); 
		lp = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,40);
		this.setLayoutParams(lp);
		this.layout(0, (int)lastTouchEventY, this.getWidth(), 10);
		lastTouchEventY = topBorder = getTop();
/*		Log.e("topboder", topBorder+"") ;*/
		//coordTop = (int) (startY+topBorder+20); 
		//coordBottom 
/*		coordTop = 107 ;  // topborder:70  border:32 +5
	 	
*/		//chartGraphHeight = (int)ChartGraphView.chartGaphHeight ;
/*		 chartGraphHeight = 549 ;*/
/*		Log.e("hor_chartGraphHeight", chartGraphHeight+"") ;*/
		coordBottom = 656 ;  //coordtop：107 height: 549
		this.setY(coordBottom-15) ;  		 
	}

	protected void onDraw(Canvas canvas)
	{
		if(DegreeView.enableScale)
		{
			super.onDraw(canvas);	
			canvas.save();
			Log.i(TAG,"onDraw");
			Paint paint = new Paint();
			paint.setColor(Color.RED);
			Rect rect = new Rect();
			rect.left =  30;
			rect.top  = 15;
			rect.right = 40;
			rect.bottom = 25;
			canvas.drawRect(rect, paint);	
			canvas.drawLine(40,rect.top+(rect.bottom-rect.top)/2, this.getWidth()-10, rect.top+(rect.bottom-rect.top)/2, paint);
		
			paint.setColor(Color.WHITE);
			Rect textRect = new Rect() ;
			textRect.left = 0;
			textRect.top = 0;
			textRect.right = 30;
			textRect.bottom = 15;
			canvas.drawRect(textRect, paint);
			paint.setColor(Color.BLACK);
			canvas.drawText(hintString, 0, 10, paint);
		}

		canvas.restore();					
	}
 
	private void onMoveGesture(float  offy ,float eventY) 
	{		
		 float coordY = lastTouchEventY + offy ; 
		 double percent ;
		 double coord ; //在坐标系中的坐标
		 
  		 if(coordY < coordTop-15)
		 {
			 this.setY(coordTop-topBorder-15);
			 coord = chartGraphHeight ;		 
		 } 
  		 else if(coordY > coordBottom-15 )
  		 {
  			 this.setY(coordBottom - topBorder-15) ;
  			// Log.e("this.setY", (coordBottom-topBorder)+"");
  			 coord = 0;  			
  		 }
  		 else 
  		 {
  			 this.setY(coordY-topBorder-15);
  			//Log.e("this.setY", (coordY-topBorder)+"");
  			 coord = (coordBottom-coordTop)-(coordY- coordTop);  
  		//	 Log.e("cood", coord+"");
  		 }  	
  		 percent = coord/(chartGraphHeight);
  		// Log.e("percent!",(percent)+"");
  		 hintString = new DecimalFormat("00.00").format(percent*500);
 
	    invalidate();
	}
	
 	@Override
	public boolean onTouchEvent(MotionEvent event) 
 	{
 		int action = event.getAction();
 		int x =(int)event.getRawY();
 		boolean handled = false ;
 		if((!handled)&&(DegreeView.enableScale))
 		{
 			switch(action)
 	 		{
 	 			case MotionEvent.ACTION_DOWN: 		
 	 				lastTouchEventY =event.getRawY();
 	 		//		Log.e("lastTouchEventY", lastTouchEventY+"");
 	 				scrollingStarted = true ;
 	 				handled = true ;
 	 				break;
 	 			case MotionEvent.ACTION_MOVE:
 	 				if(scrollingStarted)
 	 				{
 	 					if(lastTouchEventY != 0)
 	 					{ 	 		
 	 			//			Log.e(String.valueOf(event.getRawY()),String.valueOf(lastTouchEventY));
 	 						offsety = event.getRawY()  - lastTouchEventY;
 	 						onMoveGesture(offsety,event.getRawY());
 	 					} 	 					
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
 			 lastTouchEventY = 0 ;
 		} 
 		return handled;
 		 
 	}
}
