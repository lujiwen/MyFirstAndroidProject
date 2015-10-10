package com.seadee.degree.core;

import java.util.Date;

import android.R.fraction;
import android.R.integer;
import android.content.Context;
import android.graphics.Color;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.seadee.degree.R;
import com.seadee.degree.activity.HomeActivity;
import com.seadee.degree.comunication.CommunicationSetting;
import com.seadee.degree.control.HorizontalScale;
import com.seadee.degree.control.SDButton.STATE;
import com.seadee.degree.control.VerticalScale;
import com.seadee.degree.core.ChartGraphView.HandleMessageListener;
import com.seadee.degree.service.LibDegree;
import com.seadee.degree.view.DegreeBottomView;
import com.seadee.degree.view.DegreeLeftView;
import com.seadee.degree.view.DegreeRightView;
import com.seadee.degree.view.DegreeTopView;

public class DegreeView extends LinearLayout implements  HandleMessageListener 
	{
	Context context;
	LayoutInflater inflater;
	SurfaceHolder surfaceholder,linesurfaceholder;
	FrameLayout framelayout;
	LinearLayout infoview;

	public DegreeTopView degreeTopView;
	public DegreeLeftView degreeLeftView;
	public DegreeRightView degreeRightView;
	public DegreeBottomView degreeBottomView;	
	ChartGraphView  chartGraphView;
	ChartViewThread chartViewThread;
	VerticalScale verScale;  
	HorizontalScale horScale;  
	private String alarmString;	 
	private static DegreeView  degreeViewInstance = null;
	public final static int[]  UPDATEDETEXTVIEW ={ 	
													0x1001, 0x1002, 0x1003, 0x1004, 
													0x1005, 0x1006, 0x1007, 0x1008,
													0x1009, 0x10010, 0x10011, 0x10012
												 };
 
	public final static String[]  UPDATEDETEXTVIEWKEY = {
															"updatetext1","updatetext2","updatetext3","updatetext4",
															"updatetext5","updatetext6","updatetext7","updatetext8",
															"updatetext9","updatetext10","updatetext11","updatetext12"
														};		
	public final static int  updateAlramKey =  0x1000;  //锟斤拷探头锟斤拷锟斤拷
	public final static int  boardDegreeAlarmKey = 0x000;  //锟斤拷锟铰憋拷锟斤拷 
	public final static int  powerExceptionAlarmKey = 0x10013 ; //锟届常锟斤拷锟斤拷 
	public final static int  connectExceptionAlarmKey = 0x10014 ; //锟届常锟斤拷锟斤拷
	public final static int  NoReceiveDataAlarmKey = 0x10019 ; // do not receive data for 2 times 
	public final static int  ClearTextColorKey = 0x10020;
	public final static int  scaleUpdateKey = 0x10015 ; 
	public final static int  boardUpdateKey = 0x10016 ; 
    public static final int StopKey = 0x10017;  
    public static final int NextKey = 0x10018;
    public static final int batteryUpdateKey = 0x10021;
    
	public static String  horizLine ;
	public String degree_unit;
	private String warn;
	private String warn1;
	private String warnTransmiter;
	private String warnLine;
	private String warnBoard;
	private String tail1;
	private String tail2;
	public static boolean enableScale;
	
	public DegreeView(Context context) {
		super(context);
		init(context);
	}
	
	public DegreeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public DegreeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	public void init(Context context)
	{
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.main_layout,this); 
		
		degreeTopView = (DegreeTopView) findViewById(R.id.degreeTop);
        degreeLeftView = (DegreeLeftView) findViewById(R.id.degreeLeft);
        degreeRightView = (DegreeRightView) findViewById(R.id.degreeRight);
        degreeBottomView = (DegreeBottomView) findViewById(R.id.degreeBottom);
	    framelayout = (FrameLayout)findViewById(R.id.graphs_layout); 
 
		int width = framelayout.getMeasuredWidth();
		enableScale =false ;
	    chartGraphView = new ChartGraphView(context, "");
	    framelayout.addView(chartGraphView); 
	     
		verScale = new VerticalScale(context);	    
	    framelayout.addView(verScale);
	    horScale = new HorizontalScale(context);
	    framelayout.addView(horScale);
	   
	    chartGraphView.setHandleMessageListener(this); 	     
	    initString();   
	    degreeViewInstance = this ;
   }
	public static DegreeView getInstance()
	{
		return degreeViewInstance ;
	}
	void initString()
	{
		 alarmString = "";	 
		 horizLine = context.getResources().getString(R.string.line);
		 degree_unit= context.getResources().getString(R.string.degree_unit);
		 warn = context.getResources().getString(R.string.warn);
		 warnTransmiter = context.getResources().getString(R.string.wannTransmiter);
		 warnLine = context.getResources().getString(R.string.warnLine);
		 warnBoard = context.getResources().getString(R.string.warnBoard);
		 tail1 =  context.getResources().getString(R.string.tail1);
		 tail2 =  context.getResources().getString(R.string.tail2);		    
	}
	
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {		
			if(msg.what == UPDATEDETEXTVIEW[0])
			{
					if(degreeRightView!=null)
					 degreeRightView.degreeTextView[1].setText(msg.getData().getString(UPDATEDETEXTVIEWKEY[0]));
			}
			else if(msg.what == UPDATEDETEXTVIEW[1])
			{
				if(degreeRightView!=null)
					 degreeRightView.degreeTextView[2].setText(msg.getData().getString(UPDATEDETEXTVIEWKEY[1]));
			}
			else if(msg.what == UPDATEDETEXTVIEW[2])
			{
				if(degreeRightView!=null)
					 degreeRightView.degreeTextView[3].setText(msg.getData().getString(UPDATEDETEXTVIEWKEY[2]));
			}
			else if(msg.what == UPDATEDETEXTVIEW[3])
			{
				if(degreeRightView!=null)
					 degreeRightView.degreeTextView[4].setText(msg.getData().getString(UPDATEDETEXTVIEWKEY[3]));
			}
			else if(msg.what == UPDATEDETEXTVIEW[4])
			{
				if(degreeRightView!=null)
					 degreeRightView.degreeTextView[5].setText(msg.getData().getString(UPDATEDETEXTVIEWKEY[4]));
			}
			else if(msg.what == UPDATEDETEXTVIEW[5])
			{
				if(degreeRightView!=null)
				degreeRightView.degreeTextView[6].setText(msg.getData().getString(UPDATEDETEXTVIEWKEY[5]));
			}
			else if(msg.what == UPDATEDETEXTVIEW[6])
			{
				if(degreeRightView!=null)
					 degreeRightView.degreeTextView[7].setText(msg.getData().getString(UPDATEDETEXTVIEWKEY[6]));
			}
			else if(msg.what == UPDATEDETEXTVIEW[7])
			{
				if(degreeRightView!=null)
					 degreeRightView.degreeTextView[8].setText(msg.getData().getString(UPDATEDETEXTVIEWKEY[7]));
			}
			else if(msg.what == UPDATEDETEXTVIEW[8])
			{
				if(degreeRightView!=null)
					 degreeRightView.degreeTextView[9].setText(msg.getData().getString(UPDATEDETEXTVIEWKEY[8]));
			}
			else if(msg.what == UPDATEDETEXTVIEW[9])
			{
				if(degreeRightView!=null)
					 degreeRightView.degreeTextView[10].setText(msg.getData().getString(UPDATEDETEXTVIEWKEY[9]));
			}
			else if(msg.what == UPDATEDETEXTVIEW[10])
			{
				if(degreeRightView!=null)
					 degreeRightView.degreeTextView[11].setText(msg.getData().getString(UPDATEDETEXTVIEWKEY[10]));
			}
			else if(msg.what == UPDATEDETEXTVIEW[11])
			{
				if(degreeRightView!=null)
					 degreeRightView.degreeTextView[12].setText(msg.getData().getString(UPDATEDETEXTVIEWKEY[11]));
			} 				
			else if(msg.what == updateAlramKey )	//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟教酵凤拷露缺锟斤拷锟絋extView
			{
				alarmString = warn+msg.getData().getString("transmitter")+warnTransmiter+
				msg.getData().getString("linenum")+warnLine+msg.getData().getString("degree")+degree_unit;
				//degreeBottomView.warnInfoTv.append(alarmString);
				degreeBottomView.updateAlrmInfo(alarmString);
			}
			else if(msg.what == boardDegreeAlarmKey )  //锟斤拷锟斤拷锟铰凤拷锟斤拷 锟斤拷锟铰憋拷锟斤拷TextView 
			{
				alarmString = warn+msg.arg1+warnBoard+msg.arg2+degree_unit;
				degreeBottomView.updateAlrmInfo(alarmString);
			}
			else if(msg.what == powerExceptionAlarmKey)
			{				
				String string =  msg.getData().getString("LowVolt");	
				Log.e("powerExceptionAlarmKey", "LowVolt"+string);
				alarmString =  getAlarmStrings(string,string.length(),false);
				if(!alarmString.equals(""))
				{
					degreeBottomView.updateAlrmInfo(alarmString);
				}
			}
			else if(msg.what == connectExceptionAlarmKey)
			{				 
				String string = msg.getData().getString("Disconnect");	
				Log.e("disconnect1", string);
				alarmString = getAlarmStrings(string, string.length(),true);
				Log.e("disconnect2", alarmString);
				if(!alarmString.equals(""))
				{
					degreeBottomView.updateAlrmInfo(alarmString);
				}
			}	
			else if(msg.what == NoReceiveDataAlarmKey)
			{
				String string = msg.getData().getString("NoReceiveData");
				setTextViewColor(string);
			}
			else if(msg.what == ClearTextColorKey)
			{
				Log.e("cleanColor", "cleanColor") ;
				boolean[] isdash = msg.getData().getBooleanArray("clearTextColor") ;
				for(int i=1;i<=6;i++)
				{
					if((isdash[i])&&degreeTopView.isStarted[i])
					{
						Log.e("isdash", i+"");
						for(int j=1;j<=4;j++)
						{
							degreeRightView.degreeTextView[(degreeTopView.selectedButtonPos[i]-1)*4+j].setTextColor(Color.BLACK) ;							 
							Log.e("textViewColorClean", ((degreeTopView.selectedButtonPos[i]-1)*4+j)+":done") ;
						}
					}
				}
			}
			else if(msg.what == boardUpdateKey)
			{
				Bundle bundle  = msg.getData();
				degreeBottomView.updateBoardDegree(bundle.getIntArray("boardUpdate"));
				
			}
			else if(msg.what == batteryUpdateKey)
			{
				 degreeTopView.UpdateBaterry(msg.getData().getIntArray("batteryPower"));
			}

			super.handleMessage(msg);
		}
	 };
	 
	 /**
	  * when disconnect exception occur set the color of  the textviews on rightView to red
	  *  @param str  like 0101001 to judge set which textView to red 
	  * */
	 private void setTextViewColor(String str)
	 {
		 int len = str.length();
		 Log.e("settextRed", "start!") ;
		 for(int i=len-1;i>=0;i--)
		 {
			 if(str.charAt(i) == '1')
			 {
				 int selecbtn = i+1+(6-len) ;
				 for(int j=1;j<=4;j++)
				 {
					 if(degreeTopView.isStarted[selecbtn])
					 {
						 degreeRightView.degreeTextView[(DegreeTopView.selectedButtonPos[selecbtn]-1)*4+j].setTextColor(Color.RED);						 
					 }					
				 }				 
			 }
		 }	
		 Log.e("settextRed", "done!") ;
	 }
	 
	 /**
	  * return the alarm String judged by the exceptionStr like 0101010
	  * @param len  the length of the  exceptionStr
	  * @param  isDisconnect  the flag to judge the exceptionStr is disconnect exception or power volt too low exception
	  * */
	 String getAlarmStrings(String exceptionStr, int len,boolean isDisconnect)
	 {
		   String alarm =""; 
		   String tail = "";
		   String exceptionTransNumString = "";
		   if(isDisconnect)
		   {
			    tail = tail1 ;
		   }
		   else 
		   {
			   tail = tail2 ;
		   }
		 for(int i=len-1;i>=0;i--)
		 {
			 if(isDisconnect) // handle disconnect exception 
			 {
				 if((exceptionStr.charAt(i) == '1')&&(degreeTopView.selectedButtonPos[6-(len-i)+1]!= 0))
				 {
					 exceptionTransNumString = (6-(len-1-i))+","+exceptionTransNumString;
					 chartViewThread.isTransBtnAlarm[6-(len-i)] = true;
					 if(!chartViewThread.startAlarm)
					 {
						 chartViewThread.startAlarm = true; 
						 BuzzerAlarm();
					 }
				 }
			 }
			 else  //handle low volt exeception 
			 {
				 if((exceptionStr.charAt(i) == '1')&&(degreeTopView.paired[6-(len-i)+1] == true))
				 {
					 exceptionTransNumString = (6-(len-1-i))+","+exceptionTransNumString;
					 chartViewThread.isTransBtnAlarm[6-(len-i)] = true;
					 if(!chartViewThread.startAlarm)
					 {
						 chartViewThread.startAlarm = true; 
						 BuzzerAlarm();
					 }
				 }
			 }

		 }
		 Log.e(tail, exceptionTransNumString);
		 if(exceptionTransNumString.length()>1)//backdelete the last ","
		 {
			 exceptionTransNumString = exceptionTransNumString.substring(0, exceptionTransNumString.length()-1);
		 }		
		 if(exceptionTransNumString.equals(""))
		 {
			 return "";
		 }
		 else 
		 {
			 return  warn+exceptionTransNumString+tail;
		 }
	 }
	 
	public void startDrawLine()
	{
	    if(chartGraphView == null)
	    {
	    	chartGraphView  = new ChartGraphView(context, "") ;
	    	chartGraphView.drawline();	    	 
	    }
	    else if(!ChartViewThread.isWait)
	    {
	    	chartGraphView.drawline();
/*	    	 new Thread()
	    	 {
		       	 public void run()
		       	 {
		       		LibDegree.dataWhilehandle();
		       	 }
	        }.start();*/
/*	        new Thread()
	    	 {
		       	 public void run()
		       	 {
		       		LibDegree.send485Settings(CommunicationSetting.getCommunicateSettings(context), 5);
		       	 }
	        }.start();*/
	    }
	}	
	
	public void stardrawLastscreen()
	{
		if(chartGraphView != null)
		{
			chartGraphView.drawlastScreen();
		}	
	}
 	public void drawHistory()
	{		
		chartGraphView.drawHistory();
	} 
	public void stopDrawLine()
	{
		chartGraphView.stopDrawLine();
	}
	public void restartDrawLine()
	{
		chartGraphView.restartDrawLine();
	}
	public void pauseDrawLine()
	{
		chartGraphView.pauseDrawLine();
	//	degreeBottomView.buzzerBtn.stopAlarm();
	}
	public void continueDrawLine()
	{
		chartGraphView.continueDrawLine();	
	}

	@Override
	public Handler getUIHandler() {
		return mHandler;
	}
	
	public void clearSelectedButton(int btnNum,int clubNum)
	{
		degreeTopView.initTransBtn(btnNum, clubNum);
		ChartViewThread.isTransBtnAlarm[btnNum-1] = false;
		//degreeBottomView.scaleTextViews[btnNum-1].setText("");
		Log.e("alarmstart", ChartViewThread.checkAlarmStarted()+"");
		if(!ChartViewThread.checkAlarmStarted())    
		{
			ChartViewThread.startAlarm = false ;
			degreeBottomView.buzzerBtn.stopAlarm();
		}
	}
	
	public void clearTextView(int club,int transmiter)
	{
		for(int i=1;i<=4;i++)
		{
			degreeRightView.degreeTextView[club*4+i].setText("");
		}	
		//清除下方板温
		//degreeBottomView.scaleTextViews[transmiter-1].setText("");
		
		if(!ChartViewThread.checkAlarmStarted())   //选中的发射机中没有报警的  停止报警
		{
			ChartViewThread.startAlarm = false ;
			degreeBottomView.buzzerBtn.stopAlarm();
		}
	}

	public void BuzzerAlarm( ) 
	{
		Log.e("buzzerAlarm", "start!");
		degreeBottomView.buzzerBtn.startAlarm();	
		new Thread()
		{		
			public void run()
			{		
				synchronized(this)
				{
					int i =0;					
					while(degreeLeftView.isStart&&ChartViewThread.startAlarm&&ChartViewThread.checkAlarmStarted())  
					{
						Message msg = new Message();
						if(i== 0)
						{
							msg.arg2 = 1;
							i= 1;
						}
						else 
						{
							msg.arg2 = 0;
							i = 0;
						}
						buzzerHandler.sendMessage(msg);
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}	
					Log.e("buzzerAlarm", "end!");
				}								
			}					
		}.start();	
	}
	
	public Handler buzzerHandler= new Handler()
	{
		public void handleMessage(Message msg)
		{	
				if(msg.arg2%2 == 0)
				{				
					for(int i=0;i<6;i++ )
					{
						if(chartViewThread.isTransBtnAlarm[i])
						{
							degreeTopView.tranButton[i].setState(STATE.alarm);
						}
					}
				}
				else 
				{
					for(int i =0;i<6;i++ )
					{
						if(chartViewThread.isTransBtnAlarm[i])
						{					 
							degreeTopView.tranButton[i].setState(STATE.selected);
						}
					}							
				}			
			super.handleMessage(msg);
		}	
	};

	public void clearAlarmTransBtn() 
	{
		for(int i=0;i<6;i++ )
		{			
			if(chartViewThread.isTransBtnAlarm[i])
			{
				chartViewThread.isTransBtnAlarm[i] = false;
				if(DegreeTopView.selectedButtonPos[i+1] > 0 )
				{			
					degreeTopView.tranButton[i].setState(STATE.selected);
				}
				else 
				{				
					degreeTopView.tranButton[i].setState(STATE.normal);
				}
			}					
		}			
	}
	

	public void initTextView(int transBtn,int club ) 
	{
/*		for(int i=1;i<=4;i++)
		{
			degreeRightView.degreeTextView[club*4+i].setText(transBtn+"-0");
			degreeRightView.degreeTextView[club*4+i].setTextColor(Color.BLACK);
		}*/
		degreeRightView.initTextView(transBtn, club);
	}

	public void stopPairflashing()
	{
		for(int i=0;i<6;i++)
		{
			if(degreeTopView.paired[i+1])
			{						
				degreeTopView.tranButton[i].setState(STATE.normal);
			}
			else 
			{			
				degreeTopView.tranButton[i].setState(STATE.disable);
			}
		}				
	}
	public void stopDrawHistory() 
	{		 
		for(int i=0;i<DegreeTopView.selectedCount;i++)
		{
			int clubNum = degreeTopView.selectedButtonPos[degreeTopView.Selected[i]] ;
			degreeBottomView.scaleTextViews[degreeTopView.Selected[i]-1].setText("") ;
			for(int j=1;j<5;j++)
			{
				degreeRightView.degreeTextView[(clubNum-1)*4+j].setText("") ;
			}
		}
		degreeTopView.initSelectStatus();
		chartGraphView.removeAllSeries();
		stopPairflashing() ;
	}
	public void enableStartDraw() {
		degreeLeftView.startEndBtn.setClickable(true);
		degreeLeftView.startEndBtn.setState(STATE.normal);
		degreeLeftView.pairBtn.setState(STATE.normal);
	}

	/**
	 * Vertical scale move to update rightView degreetText and bottomView boardDegree
	 * @param transBtn  which transmiter
	 * @param degreeData degree and BoardDegree 
	 * @param club rightView which 4 textViews to update  
	 */	
	public void updateScaleDegree(int transBtn,int[] degreeData,int club) {
		for(int i=1;i<6;i++)
		{
			if(i == 5)
			{				
				degreeBottomView.scaleTextViews[transBtn-1].setText( degreeData[i-1]+"") ;
			}
			else 
			{
				degreeRightView.degreeTextView[club*4+i].setText(transBtn+horizLine+degreeData[i-1]+"");
				degreeRightView.degreeTextView[club*4+i].setTextColor(Color.BLACK);
			}			
		}		
	}

	public void startPairFlashing(final int transNum) {
		 new Thread()
		 {
			 @Override
			 public void run()
			 {
				 Log.e("fashing", "start");
				 int i =0 ;
				 while(degreeTopView.StartFalshing&&!DegreeTopView.pairDone)
				 {
					 Message  msg = new Message();
					 msg.arg1 = transNum ;
					 msg.arg2 = i++ ;
					pairFlashingHandler.sendMessage(msg) ;						
					try {
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				 }
				 Log.e("fashing", "end!"); 
			 }
		 }.start();
	}
	Handler pairFlashingHandler = new Handler()
	{
		public void handleMessage(Message msg )
		{
			if(msg.arg2%2 == 0)
			{		
				degreeTopView.tranButton[msg.arg1-1].setState(STATE.disable);
				
			}
			else 
			{
				degreeTopView.tranButton[msg.arg1-1].setState(STATE.normal);
			}
			super.handleMessage(msg);
		}
	};
	public void setPairStatus(int record)
	{		
		for(int i=1;i<7;i++)
		{
			if(record%10*i == 1 )
			{
				//degreeTopView.tranButton[i-1].setBackgroundResource(R.drawable.sd_ic_btn_normal);
				degreeTopView.tranButton[i-1].setState(STATE.normal);
			}
			else 
			{
				//degreeTopView.tranButton[i-1].setBackgroundResource(R.drawable.sd_ic_btn_break);	
				degreeTopView.tranButton[i-1].setState(STATE.disable);
			}
			record -= 10*i ;
		}
	}

	public void initPairState(int record) {
		// TODO Auto-generated method stub

		for(int i=0;i<6;i++)
		{
			if(degreeTopView.paired[i+1])
			{					
				degreeTopView.tranButton[i].setState(STATE.normal);
			}
			else 
			{		
				degreeTopView.tranButton[i].setState(STATE.disable);
			}

			degreeTopView.isStarted[i+1] = false ;
			if(ChartViewThread.isTransBtnAlarm != null)
			{
				ChartViewThread.isTransBtnAlarm[i] = false ;
			}
			if(ChartViewThread.startAlarm)
			{
				degreeBottomView.buzzerBtn.stopAlarm();
				ChartViewThread.startAlarm = false ;
			}
		}	
		
		//reset 
		degreeTopView.selectedButtonPos = new int[7] ; 
		degreeTopView.Selected = new int[3];	
		degreeBottomView.clearBoardDegreeTv();
		degreeRightView.clearDegreeTvs();
		degreeTopView.isRun =  false ; 
		degreeTopView.selectedCount = 0;
	}

	public void pairFlashing(int transNum) {
		startPairFlashing(transNum);
	}

	public void drawNextScreen() {
		if(chartGraphView != null )
		{
			chartGraphView.drawNextScreen();
		}
	}
	
	/**
	 * remove the NO.tranmiter's 4 lines on the chartGrap
	 * @param transmiter NO.transmiter  
	 */	
	public void removeSeries(int transmiter) 
	{		
		chartGraphView.removeseries(transmiter) ;
		int club = degreeTopView.selectedButtonPos[transmiter] ;
		for(int i=0;i<4;i++)
		{
			degreeRightView.degreeTextView[(club-1)*4+i+1].setText("");
		}
		degreeBottomView.scaleTextViews[transmiter-1].setText("") ;
	}

	public void resetHorLabels(String[] labels) 
	{
		chartGraphView.resetHorLables(labels);
	}

	public void initBtnState() 
	{
		for(int i=0;i<DegreeTopView.selectedCount;i++)
		{
			int btnNum = degreeTopView.Selected[i] ;
			int clubNum = degreeTopView.selectedButtonPos[btnNum] ;
			
			degreeBottomView.scaleTextViews[btnNum-1].setText("") ;
			for(int j=1;j<5;j++)
			{
				degreeRightView.degreeTextView[(clubNum-1)*4+j].setText("") ;
				//degreeRightView.degreeTextView[(clubNum-1)*4+j].setTextColor(Color.BLACK);
			}
			degreeTopView.isStarted[btnNum] = false ;
		}
		for(int i=0;i<6;i++ )
		{
			//degreeTopView.tranButton[i].setBackgroundResource(R.drawable.sd_ic_btn_normal) ;
			degreeTopView.tranButton[i].setState(STATE.normal);
		}	
		degreeTopView.initSelectStatus();
		chartViewThread.isWait = false ;		
	}

	public void initScale() 
	{
		chartGraphView.chartGaphHeight = chartGraphView.getGraphHeight();
		chartGraphView.chartGraphWidth = chartGraphView.getGraphWidth() ;
		chartGraphView.chartGraphBorder = chartGraphView.getGraphBorder();
		verScale.chartGraphWidth = chartGraphView.chartGraphWidth ;
		horScale.chartGraphHeight = chartGraphView.chartGaphHeight;
		verScale.endX = (int) (verScale.startX +verScale.chartGraphWidth) ;
		horScale.coordTop = (int) (horScale.topBorder + chartGraphView.border + 5); 
		horScale.coordBottom = (int) (horScale.coordTop + chartGraphView.chartGaphHeight) ; 
		horScale.invalidate();
		verScale.invalidate();
		verScale.hintString = "" ;
	}
	
	/**
	 * lock/unlock the  top,right left,bootom 4 views 
	 * @param isLock true:lock screen ,false:unlock screen
	 * */
	public void lockScreen(boolean isLock)
	{
		degreeTopView.lockTopScreen(!isLock);
		degreeLeftView.lockLeftScreen(!isLock);
		degreeRightView.lockRightScreen(!isLock);
		degreeBottomView.lockBottomScreen(!isLock);
	}
}
