package com.seadee.degree.core;

import gov.nist.javax.sip.header.InReplyTo;

import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.seadee.degree.activity.HomeActivity;
import com.seadee.degree.service.HandleFile;
import com.seadee.degree.service.LibDegree;
import com.seadee.degree.service.LibDegree.DegreeAndTime;
import com.seadee.degree.utility.Utility;
import com.seadee.degree.view.DegreeRightView;
import com.seadee.degree.view.DegreeTopView;


public class ChartViewThread extends Thread 
{
	private final String tag = "chartViewThread" ; 
	private static GraphViewSeries[] series;
	private static GraphViewSeries[] boardSeries ;
	private static ChartGraphView chartGraphView;
	public boolean isRun; 	
	private int[]  degreeData;
	
	private  static int MaxPointNum ;
	public  static double lastX = 0d;
	private DegreeAndTime data;
 
	public static boolean isWait; 
	public static boolean[] isTransBtnAlarm ; 
	private int[] BoardDegree;
	private int alarmBoardDegree ;
	
	private int Board;
	public  static boolean startAlarm;
	private String horizLine ;
	private int[] lineColors;
	private int[] selected;

 	public ChartViewThread(ChartGraphView chartGraphView)
 	 {
 		 this.chartGraphView = chartGraphView; 		 	  
 		 init();	 
 	 }
   
 	void init()
 	{
 		 isWait = false ;
 		 isRun = false ;
 		 series = new GraphViewSeries[13]; 	 
 		 boardSeries = new GraphViewSeries[6];  //存储6个板温
		 degreeData = new int[31];
		 
 		 for(int i=0;i<13;i++)
 		 {
 			 series[i] = new GraphViewSeries(new GraphViewData[]{});
 			 series[i].getStyle().color =DegreeRightView.color[i] ; 
 		 } 
 		 for(int i=0;i<6;i++)
 		 {
 			 boardSeries[i] = new GraphViewSeries(new GraphViewData[]{});
 		 }
 		 data = new DegreeAndTime();
 		 MaxPointNum =chartGraphView.maxPointNum;
 		 startAlarm = false ;
 		 isTransBtnAlarm = new boolean[6];
 		 BoardDegree = new int[7];
 		 alarmBoardDegree  = 0;
 		 Board = 0;
 	     horizLine= DegreeView.horizLine;
 		 lineColors= new int[13];
 	}

 
 	 @Override  
    public void run()  
     { 
 	//	 HandleFile.wirteIntTofile("cnt1",cnt1, false);		 
 		 isRun = true;
 		 while(isRun)
 		 {
 			// HandleFile.wirteIntTofile("cnt2",cnt2, false);
 			 try  
	         {  
                synchronized (this)  
                {               
                //	HandleFile.wirteIntTofile("cnt3",cnt3, true);
                	if((!isWait)&&(DegreeTopView.selectedCount>0))
                	{       	                		
                		drawLine();	                		
                	}   
                	Thread.sleep(3000);
                }  				 
	           }
 			 catch (Exception e)
 			 { 				 
	        	  e.printStackTrace();  
	         } 
 		 }	 
 	 }   
 	 
 	 //得到一组第transNum个发射机的pos位置的数据
 	public  static int[]  getSeriesData(int transNum, int pos)
 	{
 		int[] data = new int[5];
 		GraphViewDataInterface[][]  dataInterfaces = new GraphViewDataInterface[ChartGraphView.maxPointNum][5]; 
 		if(series[transNum] != null)
 		{
 			dataInterfaces[0] = series[(transNum-1)*4+1].getSeriesData() ;
 			dataInterfaces[1] = series[(transNum-1)*4+2].getSeriesData() ;
 			dataInterfaces[2] = series[(transNum-1)*4+3].getSeriesData() ;
 			dataInterfaces[3] = series[(transNum-1)*4+4].getSeriesData() ;
 			dataInterfaces[4] = boardSeries[transNum].getSeriesData() ;
 			
 			if(pos < dataInterfaces[0].length )
 			{
 				data[0] = (int) dataInterfaces[0][pos].getY();
 				data[1] = (int) dataInterfaces[1][pos].getY();
 				data[2] = (int) dataInterfaces[2][pos].getY();
 				data[3] = (int) dataInterfaces[3][pos].getY();
 				data[4] = (int) dataInterfaces[4][pos].getY();
 			} 			
 		} 		
 		return data;
 	} 	
 	private int[] getBoardDegrees(int[] data)
 	{
 		int[] boardDegrees  = new int[6] ;
 		for(int i=0;i<6;i++)
 		{
 			boardDegrees[i] = data[i*5+4];
 		}
 		//(selectedButton-1)*5 + ((i-1)%4)
 		//Log.e("boardDegree",boardDegrees[0]+":"+boardDegrees[1]+":"+boardDegrees[2]+":"+boardDegrees[3]+":"+boardDegrees[4]+":"+boardDegrees[5]);
 		return boardDegrees;
 	}
 	
 	private static int  flag = 0;
 	private static int cnt =0;  // count time to 1 min then update battery  20*3s = 1min
 	private void drawLine() 
 	{	  
 		Log.e("startDrawLine", Utility.getSystemTime());
	 	data.getDegreeAndTime();
 	 	degreeData = data.degree;
 		flag = degreeData[30];  //获取异常标志位
 		Log.e("exceptionflag", flag+"") ;	
		if(flag != 0) // 不等于0  存在异常
		{
			String FlagString = Integer.toBinaryString(flag);						
			checkExceptions(FlagString,FlagString.length());   //检测异常 并报警				
		} 			 
  		else if(flag == 0)  //
		{
 			 if(!isAllfalse(istransmitterDash))
 			 {
 	 			Log.e("comebackNormal!",flag+"");
 				Message msg  = new Message();
 				Bundle bundle  =  new Bundle();
 				msg.what =  DegreeView.ClearTextColorKey;
 				bundle.putBooleanArray("clearTextColor", istransmitterDash);
 				msg.setData(bundle);
 				chartGraphView.getUIHandler().sendMessage(msg); 
 				msg = null ;
 				bundle = null ;
 				istransmitterDash = new boolean[7] ;  //reset all istransmitterDash back to false 
 			 }
		}  
	
  	  	//画12条曲线	  
 		int valueY = 0 ;	
 		lineColors = DegreeRightView.LineColors ;
 		selected =  DegreeTopView.Selected;
 		int highestDegree = DegreeTopView.highestDegree ;
 		int boardAlarmDegree = DegreeTopView.BoardAlarmDegree ; 
 		int selectedButton = 0;

 		for(int i=1;i<DegreeRightView.LineColors.length;i++)
 		{ 		
 				//线
	 			if( (lineColors[i] != 0)&&(selected[(i-1)/4] != 0))
	 			{		
	 				 if(series[i] != null)
	 	 			{
	 	 				chartGraphView.removeSeries(series[i]);
	 	 			} 	 			
	 				//根据选择的按钮 提取相应曲线的温度值 ,并添加到曲线
	 				selectedButton = DegreeTopView.Selected[(i-1)/4];
		 			valueY = degreeData[(selectedButton-1)*5 + ((i-1)%4)]; 	
		 			GraphViewData data ;
		 			if(istransmitterDash[selectedButton])
		 			{
			 			data = new GraphViewData(lastX, valueY,true);
		 			}
		 			else 
		 			{
		 				data = new GraphViewData(lastX, valueY, false);
		 			}
		 			series[i].appendData(data, true, MaxPointNum);
		 			data = null ;
		 		//	series[i].getStyle().color = DegreeRightView.LineColors[i];	
		 			chartGraphView.addSeries(series[i]) ;
		  	
					 //用value更新第i条线条所对应的TextView的数值 		
					 updateTextView(selectedButton,valueY,i);
					//曲线温度报警    大于三级温度 将一直报警  
					if((valueY>highestDegree)&&(highestDegree!= 0))	
					 {							
						alarm(selectedButton, (i-1)%4+1, valueY,false);
					 }									 		
	 			} // end if 	
	 			//板温
		 		if((i%4 == 0)&&(selectedButton!=0))			 			
		 		{ 			 
					//提取相应的板温 
				  	BoardDegree[selectedButton] = degreeData[(selectedButton-1)*5+4] ;
				 	boardSeries[selectedButton-1].appendData(new GraphViewData(lastX, BoardDegree[selectedButton]),true,MaxPointNum);
				 	
				 	int[] boardDegrees =  getBoardDegrees(degreeData);
				 	//更新第selectButton的下方板温
				 	updateBoardDegree(boardDegrees);
					
		 			//板温报警  					
					if((BoardDegree[selectedButton] > boardAlarmDegree)&&(boardAlarmDegree!=0))
					{
						if((Board != selectedButton)||(alarmBoardDegree!=BoardDegree[selectedButton])) //一组曲线只显示一次报警温度 
						{
							alarmBoardDegree = BoardDegree[selectedButton] ; // 和下一次比对 
							Board = selectedButton ;// 和下一次比对 
							alarm(selectedButton, 0, BoardDegree[selectedButton],true);			
						}						
					}
		 		}	
 		} //end for  
  		  lastX++;
 	     //更新可见点的视口		 
		 if(lastX>MaxPointNum) 
		 {
		 	chartGraphView.setViewPort(lastX-MaxPointNum, MaxPointNum);	 
	 	 }  
		// 10*3s = 30s	  
		if((cnt)%10==0)
		{
			//Log.i("drawLine","updateBatPower");
			cnt = 0;
			updateBatPower();
		}
		cnt++;
		Log.e("endDrawLine", Utility.getSystemTime());
	}

 	/**
 	 *  alarm
 	 * @param transmiter which tranmiter has exception 
 	 * @param lineNum which line of the tranmiter has exception 
 	 * @param degree the unexcepted degree over the alram degree
 	 * @param isBoardDegree whether the unexcepted degree is boardDegeee or probe temperature    
 	 * */
	final private void alarm(int transmiter,int lineNum,int degree ,boolean isBoardDegree)
	{
		if(isBoardDegree == false )   //热探头12条曲线报警
		{
			//下方显示热探头报警信息
			 Bundle  bundle= new Bundle();
			 bundle.putString("transmitter", Integer.valueOf(transmiter).toString());
			 bundle.putString("linenum", Integer.valueOf(lineNum).toString());
			 bundle.putString("degree", Integer.valueOf(degree).toString());
			 Message msg  = new Message();
			 msg.what = DegreeView.updateAlramKey;
			 msg.setData(bundle);
			 chartGraphView.getUIHandler().sendMessage(msg);
			 msg = null ;
			 bundle = null;
		}
		else  //板温报警
		{
			//下方显示板温的报警信息
			Message msg = new Message() ;
			msg.what =  DegreeView.boardDegreeAlarmKey;
			msg.arg1 = transmiter ; //板子编号 
			msg.arg2 = degree ;	// 板子温度 			
			chartGraphView.getUIHandler().sendMessage(msg);	
			msg = null ;
		}
			//蜂鸣器
		 if(!isTransBtnAlarm[transmiter-1])
		 {			 
			  isTransBtnAlarm[transmiter-1] = true;	 	
 			  if(!startAlarm)
 			  {
 				  Log.i("buzzer", (transmiter)+" buzzer start!");	
 				  startAlarm = true;
 	 			  HomeActivity.getInstance().BuzzerAlarm(); //蜂鸣器按钮闪烁	
 			  }			  
		 }				 			
	}
/* 	private final void updateBoardDegree(int transNum,int boardDegree)
 	{
 		//Log.e("boardDegree", transNum+":"+boardDegree);
 		Message msg = new Message() ;
 		msg.what =DegreeView.boardUpdateKey ;
 		msg.arg1 = transNum ;
 		msg.arg2 = boardDegree;
 		chartGraphView.getUIHandler().sendMessage(msg);
 		msg = null;
 	}*/
 	private final void updateBoardDegree(int[] boardDegree)
 	{
 		//Log.e("boardDegree", transNum+":"+boardDegree);
 		Message msg = new Message() ;
 		Bundle bundle = new Bundle();
 		bundle.putIntArray("boardUpdate",boardDegree);
 		msg.what =DegreeView.boardUpdateKey ;
 		msg.setData(bundle);
 		chartGraphView.getUIHandler().sendMessage(msg);
 		msg = null;
 		bundle = null ;
 	}
 	/**
 	 * Update the degree on the rightView 
 	 * */
    private final void updateTextView(int transBtnNum,int value,int lineNum)
    {
    	String string = "";
    	if(value >0)
    	{
    		string = transBtnNum+horizLine+value ; 
    	}
    	else if(value == 0 )
    	{
    		string =  horizLine + horizLine;
    	}   	
		Bundle bundle = new Bundle();
	    Message messge = new Message();
		bundle.putString(DegreeView.UPDATEDETEXTVIEWKEY[lineNum-1], string);
        messge.what = DegreeView.UPDATEDETEXTVIEW[lineNum-1];
        messge.setData(bundle);                    
        chartGraphView.getUIHandler().sendMessage(messge); 	 
        messge = null ;
        bundle = null ;
    } 
    
    public static Handler clearSeriesHandler =new Handler()
    {
    	@Override 
    	public void handleMessage(Message msg)
    	{
    		if(msg.arg2 == 4 )  //点击发射机 按钮同时取消4条 曲线的绘制
    		{
    			for(int i=1;i<=4;i++)
        		{
        		 	series[msg.arg1*4+i].resetData(new GraphViewData[]{});	
    				//series[msg.arg1*4+i]
        		}
    		}
    		if(msg.arg2 == 1 ) //点击textView 取消一条曲线的绘制
    		{
    			//series[msg.arg1].resetData(new GraphViewData[]{});
    			//series[msg.arg1].resetData(new GraphViewData[]{});
    			 chartGraphView.removeSeries(series[msg.arg1]);
    		}
    	}    	
    };
    private void  judgeNoDataReceive(String  string ,int len)
    {
    	for(int i=len-1;i>=0;i--)
    	{
    		if(string.charAt(i) != '0')
    		{
    			istransmitterDash[(i+1)+(6-len)] = true ;    //set the 4 series to dashline  of transmitter 
    		}
    	}
    }    
    
    private boolean isAllfalse(boolean[] isdash)
    {
    	for(int i=1;i<7;i++)
    	{
    		if(isdash[i])
    		{
    			return false ;
    		}
    	}
    	return true ;
    }
    
    private  boolean[]   istransmitterDash = new boolean[7] ;      
    public static  void resetSeries(int clubNum)
    {
    	for(int i=1;i<=4;i++)
    	{
    		series[(clubNum)*4+i].resetData(new GraphViewData[]{}) ;
    	}
    }
    
	private  void checkExceptions(String string,int len)
	{		 			
		Log.e("exceptionStrintg",string+":"+len) ;
		Message msg1= new Message();
		Bundle  bundle1 = new Bundle();
		Message msg2= new Message();
		Bundle  bundle2 = new Bundle();
		Message msg3= new Message();
		Bundle  bundle3 = new Bundle();
		String alarmString ="";
		
		if(len > 12)  //12位以上的：2次没收到数据的发射机编号
		{
			alarmString = string.substring(len-12,len );
			String NoReceiveString = string.substring(0, len-12);
			Log.e("NoReceiveString",NoReceiveString) ;
			judgeNoDataReceive(NoReceiveString,NoReceiveString.length());	
			if(NoReceiveString.contains("1"))
			{
				msg3.what = DegreeView.NoReceiveDataAlarmKey;
				bundle3.putString("NoReceiveData", NoReceiveString) ;
				msg3.setData(bundle3) ;
				chartGraphView.getUIHandler().sendMessage(msg3);	
			}		
		}
		else   //如果之前将颜色变为红色，曲线变为虚线，现在变回正常
		{
			Log.e("datareceiced", "ok") ;
			if(!isAllfalse(istransmitterDash))  // set true to false , dashline no more  
			{				
				msg3.what =  DegreeView.ClearTextColorKey;
				bundle3.putBooleanArray("clearTextColor", istransmitterDash) ;
				msg3.setData(bundle3);
				chartGraphView.getUIHandler().sendMessage(msg3);
				istransmitterDash = new boolean[7] ;
			}			
			alarmString = string ;			
		}
		Log.e("AlarmStrintg",alarmString) ;
		int alarmlength = alarmString.length() ; 
		if((alarmlength!=0)&&(alarmString.contains("1")))
		{
			if(alarmlength>0&&alarmlength<=6)  // 1 alarm 
			{
					msg1.what = DegreeView.connectExceptionAlarmKey ;
					bundle1.putString("Disconnect" , alarmString);
					msg1.setData(bundle1);
					chartGraphView.getUIHandler().sendMessage(msg1); 	
			}
			else  if(alarmlength>6&&alarmlength<=12)   // 2 alarm 
			{
				String  isLink = alarmString.substring(alarmlength-6, alarmlength);
				if(isLink.contains("1")/*!isAll0(isLink,isLink.length() )*/)
				{
					msg2.what = DegreeView.connectExceptionAlarmKey; 			
					bundle2.putString("Disconnect", isLink);
					msg2.setData(bundle2);
					chartGraphView.getUIHandler().sendMessage(msg2); 	
				}	
				String  isPower =alarmString.substring(0, alarmlength-6);
				if(isPower.contains("1"))
				{
					msg2 = new Message() ;
					bundle2 = new Bundle();
					msg2.what = DegreeView.powerExceptionAlarmKey;
					bundle2.putString("LowVolt", isPower);
					msg2.setData(bundle2);
					chartGraphView.getUIHandler().sendMessage(msg2); 	
				}
			}
		}	
		
		bundle1 = null ;
		bundle2 = null ;
		bundle3 = null ;
		msg1 = null ;
		msg2 = null ;
		msg3 = null ;
 	}
	 
	/**
	 * Check if the  every char of the  string is 0
	 **/   
	private boolean isAll0(String string,int len) 
	{
		if(!string.isEmpty())
		{
			for(int i=0;i<len;i++)
			{
				if(string.charAt(i) != '0' )
				{
					return false ;
				}
			}
			return true;
		}
		else  return true ; 	
	}
	
	/**
	 * update power state of the 6 battery on the topView
	 * */
	private void updateBatPower()
	{
		Message  msg = new Message();
		Bundle bundle = new Bundle();
		msg.what = DegreeView.batteryUpdateKey;
		int[] batArr = LibDegree.GetVoltage();
		bundle.putIntArray("batteryPower",batArr);
		Log.e("battArr",batArr[0]+":"+batArr[1]+":"+batArr[2]+":"+batArr[3]+":"+batArr[4]+":"+batArr[5]);
		msg.setData(bundle);
		chartGraphView.getUIHandler().sendMessage(msg);
	}
	
	public static boolean checkAlarmStarted()
	{
		for(int i=0 ;i<6;i++)
		{
			if(ChartViewThread.isTransBtnAlarm[i])
			{
				 return true ;
			}
		}		
		return false ;
	}
}
