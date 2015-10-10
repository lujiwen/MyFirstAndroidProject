package com.seadee.degree.core;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageParser.NewPermissionInfo;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;
import com.seadee.degree.R;
import com.seadee.degree.activity.HomeActivity;
import com.seadee.degree.calendar.CalendarView;
import com.seadee.degree.service.HandleFile;
import com.seadee.degree.service.LibDegree;
import com.seadee.degree.service.LibDegree.DegreeAndTime;
import com.seadee.degree.service.SettingVarible;
import com.seadee.degree.utility.Utility;
import com.seadee.degree.view.DegreeBottomView;
import com.seadee.degree.view.DegreeTopView;


public class ChartGraphView extends LineGraphView   {
	
	private static 	final  String tag = "ChartGraphView"; 
	private Context context ;  
	public static ChartViewThread chartViewThread ;
	private String degree_unit ;
    HandleMessageListener handleMessageListener;   
	private static   GraphViewSeries series[];
	GraphViewData[] graphViewDatas; 
	public static  final int maxDataCount = 601;
	public static int  labelwidth= 40;
	public static int border = 32;
	public static int maxPointNum;
	public static int  startX ;
	public static int startY;
	private DegreeAndTime data ;

 	private  static String[]  horLabel ;
	private static GraphViewSeries[] boardSeries ;
	private static GraphViewSeries  timeSeries ;
	private static GraphViewSeries exceptionSeries ; //save the exception flag ;
	public static boolean darwHistory ;
	private static int [] select = new int[3];
	public static  int screen = 1;  // screenNum  
	private static Date chooseDate ;
	public static float chartGraphWidth  ;
	public static float chartGaphHeight  ;
	public static float chartGraphBorder ;
    public interface HandleMessageListener
    {
    	public abstract Handler getUIHandler();
    }    
        
    public void setHandleMessageListener(HandleMessageListener h)
    {
    	handleMessageListener = h;
    }    
    public Handler getUIHandler()
    {
    	return handleMessageListener.getUIHandler();
    }

	public ChartGraphView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    init(context);
	}
	public ChartGraphView(Context context, String title) {
	    super(context,title);
	    init(context);
	}
	private void initSeries()
	{
		series = new GraphViewSeries[12];				
		for(int i=0;i<12;i++)
		{
			series[i] = new GraphViewSeries(new GraphViewData[]{});
		} 
	
		series[0].getStyle().color = SettingVarible.oneDegree1Color;
 		series[1].getStyle().color = SettingVarible.oneDegree2Color;
		series[2].getStyle().color = SettingVarible.oneDegree3Color;
		series[3].getStyle().color = SettingVarible.oneDegree4Color;
		 
	 	series[4].getStyle().color = SettingVarible.twoDegree1color;
		series[5].getStyle().color = SettingVarible.twoDegree2color;
		series[6].getStyle().color = SettingVarible.twoDegree3color;
		series[7].getStyle().color = SettingVarible.twoDegree4color;
		
		series[8].getStyle().color = SettingVarible.threeDegree1color;
		series[9].getStyle().color = SettingVarible.threeDegree2color;
		series[10].getStyle().color = SettingVarible.threeDegree3color;
		series[11].getStyle().color = SettingVarible.threeDegree4color;  
		
		boardSeries = new GraphViewSeries[6];  //瀛樺偍6涓澘娓?
		for(int i=0;i<6;i++)
		{
			boardSeries[i] = new GraphViewSeries(new GraphViewData[]{});
		}
		timeSeries = new GraphViewSeries(new GraphViewData[]{});
		exceptionSeries = new GraphViewSeries(new GraphViewData[]{});
	}
	private void  resetSeries()
	{
		for(int i=0;i<12;i++)
		{
			series[i].resetData(new GraphViewData[]{});
		} 
		for(int i=0;i<6;i++)
		{
			boardSeries[i].resetData(new GraphViewData[]{});
		}
		timeSeries.resetData(new GraphViewData[]{});
		exceptionSeries.resetData(new GraphViewData[]{});
	}
	
	private void init(Context context)
	{		
		maxPointNum = 601;
		this.context = context; 
		degree_unit =  context.getResources().getString(R.string.degree_unit) ;
			
		this.setPadding(0,10, 10, 10);	
	
		horLabel = new String[]{"0min", "10min", "20min", "30min" } ;				
	    this.setHorizontalLabels(horLabel);	    
	 	this.setVerticalLabels(new String[]{
	 			"500"+degree_unit,"450"+degree_unit,"400"+degree_unit,
	 			"350"+degree_unit,"300"+degree_unit,"250"+degree_unit,
	 			"200"+degree_unit,"150"+degree_unit,"100"+degree_unit,
	 			"50  "+degree_unit,"0 "+degree_unit } );	    
	 	
	 	
		GraphViewStyle style = new GraphViewStyle();
		style.setNumHorizontalLabels(4);
		style.setVerticalLabelsWidth(labelwidth);
		style.setNumVerticalLabels(11);
	  	style.setTextSize(15);
		style.setGridColor(Color.WHITE);
		this.setGraphViewStyle(style);
		
		this.setManualYAxis(true);
		this.setManualYAxisBounds(500, 0);
	    this.setScalable(true);     
        this.setViewPort(0, maxPointNum-1);
        
		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED); 
		this.measure(w, h);
		startX = this.getMeasuredWidth();		
		startY=  this.getMeasuredHeight();
		/*dataBlock = new DegreeAndTime[2401] ;
		 for(int i=0;i<2401;i++)
		 {
			 dataBlock[i] = new DegreeAndTime() ; 
		 }*/
		initSeries();	
		chartGraphWidth = getGraphWidth() ;
		chartGaphHeight = getGraphHeight() ;
		chartGraphBorder =getGraphBorder() ;
		Log.e("init", this.getGraphBorder()+"") ;
		Log.e("init", this.getGraphHeight()+"") ;
		Log.e("init",this.getGraphWidth()+"");
  
	}
		
	public void resetHorLables(String[] lables)
	{
		horLabel =  lables ;
		resetHorizontalLabels(horLabel) ;
	}
 
	public static int getstartX()
	{
		return startX;
	}
	
	public static int getstartY()
	{
		return startY;
	}
	public static int getEndY() 
	{
		return 0;
	}
	public  float getGraphHeight()
	{
		return  super.getGraphHeight(); //549
	}
	public float getGraphWidth()
	{ 
		return super.getGraphWidth(); //790
	}
	public float getGraphBorder()
	{
		return super.getGraphBorder();
	}
	
	public static GraphViewSeries  getSeriesData(int index)
	{
		GraphViewSeries tempseries =  series[index] ;
		return tempseries ;
	}
	int[] getBoardDegree(int boardNum)
	{
		int[] data = new int[maxDataCount] ;		
		return data ;
	}
	public  void drawline()
	{
		 if(chartViewThread == null)
		 {
			  removeAllSeries();
			  chartViewThread = new ChartViewThread(this);
			  chartViewThread.start();
		 }
		 else 
		 {		
			  //removeAllSeries();
			 // chartViewThread.start();
			  ChartViewThread.isWait = false ;
		 }	
	 }
 
	private boolean[] checkIsDash(int exceptionFlag)
	{
		boolean[] istransmitterDash = new boolean[7] ; 
		String FlagString = Integer.toBinaryString(exceptionFlag);	
		int len = FlagString.length();
		if(len > 12)
		{
			String NoReceiveString = FlagString.substring(0, len-12);
			int len2 = NoReceiveString.length();
	    	for(int i=len2-1;i>=0;i--)
	    	{
	    		if(NoReceiveString.charAt(i) != '0')
	    		{
	    			istransmitterDash[(i+1)+(6-len2)] = true ;    //set the 4 series to dashline  of transmitter 
	    		}
	    	}
		}
		return istransmitterDash ;
	}
private ProgressDialog getProgressDialog(String title, String content) 
{
	ProgressDialog dialog = new ProgressDialog(context);	
	dialog.setCancelable(false);
	dialog.setTitle(title); 
	dialog.setIndeterminate(true);
	dialog.setMessage(content);
	dialog.setCancelable(true);
	return dialog;
}

private static int offset = 0;
public void  drawHistory()
{	
	if(DegreeBottomView.screenLengthchanged)
	{
		initSeries();
		DegreeBottomView.screenLengthchanged = false  ;
	}	 
	 chooseDate = CalendarView.downDate;
	 final ProgressDialog dialog = getProgressDialog("", getResources().getString(R.string.wait) );
	 dialog.show();
	 final long systm =System.currentTimeMillis() ;	 
	 new Thread(new Runnable() {	 
		@Override
		public void run() {
		
			if(chartViewThread != null)
			{
				if( chartViewThread.isAlive() )
				{
					  pauseDrawLine();
				}
			} 
			String datesString = new SimpleDateFormat ("yyyyMMdd").format(chooseDate);
		    select =  DegreeTopView.Selected ;
		    String storage_path = Environment.getExternalStorageDirectory().getPath()+"/sd_degree/"+datesString;
		    String  filename = storage_path+"/"+ datesString+".txt";	
		    int pointNum = Utility.min(maxPointNum,HandleFile.getFileSize(filename)/150); 		    
			int  transLength = countTransmiter(select);					
			Log.e("screenLength", maxPointNum+"") ;		
			try
			{		
				//dataBlock = HandleFile.getDataBlock(filename, offset, maxPointNum) ;
				for(int i=0;i<pointNum ;i++)
				{
				  //data = dataBlock[i] ;
				  //  time = new SimpleDateFormat("mmss").format(data.time);
					data = HandleFile.getStoredData(filename,offset+i*150);	
				//	exceptionSeries.appendData(new GraphViewData(i, data.degree[30]), false,maxPointNum);
					boolean isDashPoint[] =  checkIsDash(data.degree[30]) ;
				    for(int k=0;k<DegreeTopView.selectedCount;k++)
				    {
					      for(int p=0;p<5;p++)
					      {
					    	  if(p == 4)
					    	  {	
					    		  boardSeries[select[k]-1].appendData(new GraphViewData(i,data.degree[(select[k]-1)*5+p])  , false, maxPointNum);
					    	  }
					    	  else 
					    	  {							 
			    				  GraphViewData graphData ;
			    				  Log.e("isDashPoint",isDashPoint[select[k]]+"") ;
					    		  if(isDashPoint[select[k]])
					    		  {
					    			  graphData  = new GraphViewData(i, data.degree[(select[k]-1)*5+p],true);
					    		  }
					    		  else 
					    		  {
					    			  graphData  = new GraphViewData(i, data.degree[(select[k]-1)*5+p],false);
					    		  }
					    		  series[k*4+p].appendData(graphData, false, maxPointNum);				    		 					    
					    	  }
					    	}   
					  }	
				    timeSeries.appendData(new GraphViewData(i,data.time), false,maxPointNum);				  
				}			
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}  
			
			removeAllSeries();
			setViewPort(0, maxPointNum-1);
			// Log.e("getdata",(System.currentTimeMillis() - systm )+"");
			for(int i=0;i<transLength;i++)
			{
				 for(int j=0;j<4;j++)
				 {
					  addSeries(series[i*4+j]);
					
				 }
			}	
			dialog.dismiss();
		}	 
	}).start();

}
public void drawlastScreen( )  
{ 
	if(chartViewThread != null)
	{
		if( chartViewThread.isAlive() )
		{
			this.pauseDrawLine();
		}
	}
	//resetSeries();
	if((screen == 1))
	{
		Toast.makeText(context, R.string.firstScreen , Toast.LENGTH_SHORT).show();
	} 
	else if(screen>1)
	{	
		if(DegreeBottomView.screenLengthchanged)
		{
			initSeries();
			DegreeBottomView.screenLengthchanged = false ;
			Log.e("Screen","changed");
		}
		final ProgressDialog dialog = getProgressDialog("",  getResources().getString(R.string.wait));
		dialog.show();
		new  Thread(new Runnable() {			
			@Override
			public void run() {
				
				screen -= 1;
				offset -= (maxPointNum-1)*150;
				Log.e("offset", offset+"");
							
				Log.e("lastscreen", screen+"");
			 
				String datesString = new SimpleDateFormat ("yyyyMMdd").format(chooseDate);			 
			    select =  DegreeTopView.Selected ;
			    String storage_path=Environment.getExternalStorageDirectory().getPath()+"/sd_degree/"+datesString;
				String filename = storage_path+"/"+ datesString+".txt";		
			    int pointNum = Utility.min(maxPointNum,HandleFile.getFileSize(filename)/150); 
				int  transLength = countTransmiter(select);		
				Log.e("screenLength", maxPointNum+"") ;
				for(int i=0;i<pointNum;i++)
				{
					try
					{						
						data = HandleFile.getStoredData(filename,offset+i*150);	
					} 
					catch (IOException e)
					{
						e.printStackTrace();
					}  
				    for(int k=0;k<DegreeTopView.selectedCount;k++)
				    {
				      for(int p=0;p<5;p++)
				      {
				    	  if(p == 4)
				    	  {
				    		  boardSeries[select[k]-1].appendData(new GraphViewData(i,data.degree[(select[k]-1)*5+p]), false, maxPointNum);
				    	  }
				    	  else 
				    	  {
				    		  series[k*4+p].appendData(new GraphViewData(i, data.degree[(select[k]-1)*5+p]), false, maxPointNum);
				    	  }
				    	}   
					  }
				    timeSeries.appendData(new GraphViewData(i,data.time), false,maxPointNum);
				    exceptionSeries.appendData(new GraphViewData(i, data.degree[30]), false,maxPointNum);
				}			
				removeAllSeries();
				setViewPort(0, maxPointNum-1);
				for(int i=0;i<transLength;i++)
				{
					 for(int j=0;j<4;j++)
					 {
						 addSeries(series[i*4+j]);
					 }
				}	
				dialog.dismiss();
			}
		}).start(); 		
	}	 
	Log.e("lastscreen", screen+"") ;
}
public void drawNextScreen()
{
	if(chartViewThread != null)
	{
		if( chartViewThread.isAlive() )
		{
			this.pauseDrawLine();
		}
	}	
 	//resetSeries();
	String datesString = new SimpleDateFormat("yyyyMMdd").format(chooseDate);
    select  =  DegreeTopView.Selected ;   			

	String storage_path=Environment.getExternalStorageDirectory().getPath()+"/sd_degree/"+datesString;
	final String filename = storage_path+"/"+ datesString+".txt";
	offset += 150*(maxPointNum-1);
	final int pointNum = Utility.min(maxPointNum,((HandleFile.getFileSize(filename)- offset)/150)); 	

	Log.e("nextScreen",pointNum+"");
	Log.e("pointLeft", ((HandleFile.getFileSize(filename)- offset)/150)+"");
	Log.e("offset", offset+"");
	if((offset) > HandleFile.getFileSize(filename))  
	{
		offset -= 150*(maxPointNum-1);
		Toast.makeText(context, R.string.LatsScreen  , Toast.LENGTH_SHORT).show() ;
	}	
	else 
	{
		if(DegreeBottomView.screenLengthchanged)
		{
			initSeries();
			DegreeBottomView.screenLengthchanged = false ;
			Log.e("Screen","changed");
		}
		final ProgressDialog dialog = getProgressDialog("",getResources().getString(R.string.wait));
		dialog.show();
		
		new Thread(new Runnable() {			
			public void run() {
				screen ++ ;
				Log.e("nextScreen", screen+"");
				removeAllSeries();
			
				int  transLength = countTransmiter(select);
				Log.e("screenLength", pointNum+"") ;
				Log.e("offset", offset+"");
				for(int i=0;i<pointNum;i++)
				{
					try
					{						
						data = HandleFile.getStoredData(filename,offset+i*150);	
					} 
					catch (IOException e)
					{
						e.printStackTrace();
					}  
				    for(int k=0;k<3;k++)
				    {
				    	if(select[k] != 0)
				    	{
					      for(int p=0;p<5;p++)
					      {
					    	  if(p == 4)
					    	  {
					    		  boardSeries[select[k]-1].appendData(new GraphViewData(i,data.degree[(select[k]-1)*5+p]), false, maxPointNum);
					    	  }
					    	  else 
					    	  {
					    		  series[k*4+p].appendData(new GraphViewData(i, data.degree[(select[k]-1)*5+p]), false, maxPointNum);
					    	  }
					      }
				    	}   
					  }
				    timeSeries.appendData(new GraphViewData(i,data.time), false,maxPointNum);
				    exceptionSeries.appendData(new GraphViewData(i, data.degree[30]), false,maxPointNum);
				}				
				removeAllSeries();
				setViewPort(0, maxPointNum-1);
				for(int i=0;i<transLength;i++)
				{
					 for(int j=0;j<4;j++)
					 {
						   addSeries(series[i*4+j]);
					 }
				}
				dialog.dismiss();
			}
		}).start();		
	}
}

private int countTransmiter(int[] select )
{
	int c = 0;
	for(int i=0;i<3;i++)
	{
		if(select[i] != 0)
		{
			c += 1;
		}
	}
	return c;
}
	 
 static int getPos(int num)
 {
	 int x =0 ;
	 for(int i=0;i<select.length;i++)
	 {
		  if(select[i] == num)
		  {
			  x = i ;
		  }
	 }
	return x ;
 }
 	//获取一个发射机在某点的数据,供纵标尺拖动时候，动态显示历史数据的时候使用
	public  static int[]  getSeriesData(int transNum, int pos)
	{
		int[] data = new int[6];
		GraphViewDataInterface[][]  dataInterfaces = new GraphViewDataInterface[5][maxPointNum]; 
		int order = getPos(transNum);
		
		if(series[transNum-1] != null)
		{			
			for(int i=0;i<5;i++)
			{
				if(i == 4)  // board degree 
				{
					dataInterfaces[i] = boardSeries[transNum-1].getSeriesData() ;
				}
				else 
				{
					dataInterfaces[i] = series[order*4+i].getSeriesData() ;
				}
			}	
			//dataInterfaces[5] = timeSeries.getSeriesData();
			 
			if(pos < dataInterfaces[0].length )
			{
				for(int i=0;i<5;i++)
				{
					data[i] = (int) dataInterfaces[i][pos].getY();
				}
				data[5] = (int) timeSeries.getSeriesData()[pos].getY();
			} 			
		} 		
		return data;
	}
	
public void stopDrawLine()
{
	if(chartViewThread != null)
	{
		chartViewThread.isRun = false ;
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(! chartViewThread.isAlive())
		{
			chartViewThread = null;
			Log.i(tag,"chartViewThread is dead");
		}
	}
}

public  void pauseDrawLine()
{
	if(chartViewThread == null )
	{
		chartViewThread = new ChartViewThread(this) ;	
	}
	if(chartViewThread.isAlive()&&chartViewThread.isRun)
	{
		chartViewThread.isWait = true ;
	}
}

public void  continueDrawLine()
{
	if(chartViewThread == null)
	{
		chartViewThread = new ChartViewThread(this);
	}
	if(chartViewThread.isAlive()&&chartViewThread.isRun)
	{
		chartViewThread.isWait = false ;
	}
}

public void restartDrawLine()
{
    	stopDrawLine();
		drawline(); 
}

public void removeseries(int transmiter) 
{
	int index = DegreeTopView.selectedButtonPos[transmiter] ;
	for(int i=0;i<4;i++)
	{
		//this.removeSeries((index-1)*4+i) ;
		this.removeSeries(series[(index-1)*4+i]);
	}
}

}
