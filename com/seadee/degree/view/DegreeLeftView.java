package com.seadee.degree.view;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.seadee.degree.R;
import com.seadee.degree.activity.CommunicationSetActivity;
import com.seadee.degree.activity.EthernetSettingActivity;
import com.seadee.degree.activity.HelpActivity;
import com.seadee.degree.activity.HomeActivity;
import com.seadee.degree.calendar.CalendarDialog;
import com.seadee.degree.comunication.CommunicationSetting;
import com.seadee.degree.control.CommonButton;
import com.seadee.degree.control.SDButton;
import com.seadee.degree.control.SDButton.STATE;
import com.seadee.degree.control.UnlockScreenDialog;
import com.seadee.degree.core.ChartGraphView;
import com.seadee.degree.core.ChartViewThread;
import com.seadee.degree.core.DegreeView;
import com.seadee.degree.service.HandleFile;
import com.seadee.degree.service.LibDegree;
import com.seadee.degree.service.PowerManager;
import com.seadee.degree.utility.Utility;

public class DegreeLeftView extends LinearLayout {
 
	public interface OnLeftViewListener 
	{
		public abstract void pauseDrawLine();
		public abstract void startDrawLine();
		public abstract void continueDrawLine();
		public abstract void pairFlashing();
		public abstract void initPairState();
		public abstract void initScale();
	}
	
	OnLeftViewListener onLeftViewListener;
	public void SetOnLeftViewListener(OnLeftViewListener listener)
	{
		onLeftViewListener = listener;
	}
	
	Context context;
	LayoutInflater layoutInflater;
	public SDButton startEndBtn,scaleBtn,pairBtn,lockscreenButton;
	public CommonButton fileBtn,shutdownBtn,correctBtn,helpBtn,settingButton;
	public boolean isScale  ;
	public static boolean isStart ;
	public static boolean PairStarted;
	public static boolean settingstarted;
	private String banString ; 
	private String promoteString;
	public static  boolean screenLocked;
	private boolean isfirstRun ;
	public DegreeLeftView(Context context) {
		super(context);
		init(context);
	}

	public DegreeLeftView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		init(context);
	}

	public DegreeLeftView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		layoutInflater.inflate(R.layout.left_layout, this);
		
		startEndBtn = (SDButton) findViewById(R.id.start_end_Btn);
		fileBtn = (CommonButton) findViewById(R.id.fileBtn);
		scaleBtn = (SDButton) findViewById(R.id.scaleBtn);
		pairBtn = (SDButton) findViewById(R.id.pairBtn);
		correctBtn = (CommonButton)findViewById(R.id.correctBtn);
		helpBtn =(CommonButton)findViewById(R.id.helpBtn);
		settingButton= (CommonButton)findViewById(R.id.settingBtn);
		shutdownBtn = (CommonButton) findViewById(R.id.shutdownBtn);
		lockscreenButton = (SDButton)findViewById(R.id.lockscreen);
		isStart = false; 
		PairStarted =false;
		settingstarted = false ;
		startEndBtn.setText(R.string.start);
		isScale = false ;

		startEndBtn.setOnClickListener(listener);
		fileBtn.setOnClickListener(listener);
		scaleBtn.setOnClickListener(listener);
		pairBtn.setOnClickListener(listener);
		shutdownBtn.setOnClickListener(listener);
		helpBtn.setOnClickListener(listener);
		correctBtn.setOnClickListener(listener);
		settingButton.setOnClickListener(listener);
		lockscreenButton.setOnClickListener(listener);
		onLeftViewListener = HomeActivity.getInstance();
		
		banString = getResources().getString(R.string.ban);
		promoteString = getResources().getString(R.string.promote);
		isfirstRun = true ;
	}

	private OnClickListener listener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.start_end_Btn:	
					isStart = !isStart;
					if (isStart) 
					{	 			
						start();
					} 
					else //stop 
					{
						stop();
					}			
				break;
			case R.id.fileBtn:	
				if(!isStart&&!PairStarted)
				{
					CalendarDialog calendarDialog = new CalendarDialog(context);
				 	calendarDialog.show();    
				}
				else
				{
					Toast.makeText(context, R.string.checkFilePromte , Toast.LENGTH_LONG).show() ;
				}				
				break;
			case R.id.scaleBtn:
				DegreeView.enableScale = !DegreeView.enableScale ;
				if(DegreeView.enableScale)
				{				
					scaleBtn.setState(STATE.selected);
					onLeftViewListener.initScale();
				}
				else 
				{					
					scaleBtn.setState(STATE.normal) ;
					onLeftViewListener.initScale();
				}				
				break;
			case R.id.pairBtn:	 	
				if((!isStart)&&(!PairStarted))
				{
					DegreeTopView.StartFalshing = true ;
					PairStarted = true;		
					DegreeTopView.pairDone = false ;
					startEndBtn.setClickable(false);
					ChartGraphView.darwHistory = false ;
					
					pairBtn.setState(STATE.selected);
					startEndBtn.setState(STATE.disable) ;
					onLeftViewListener.initPairState();	
					Toast.makeText(context, promoteString, Toast.LENGTH_LONG).show();
				}
				else if(PairStarted&&!DegreeTopView.pairDone&&(DegreeTopView.choosePairBtn==0 ))
				{
					DegreeTopView.StartFalshing = false ;
					PairStarted = false;		
					DegreeTopView.pairDone = false ;
					startEndBtn.setClickable(true);					
					pairBtn.setState(STATE.normal);
					startEndBtn.setState(STATE.normal) ;
					ChartGraphView.darwHistory = false ;
				}
				else 
				{					
					Toast.makeText(context, banString, Toast.LENGTH_SHORT).show();
				}								
				break;						
			case R.id.shutdownBtn:
				AlertDialog.Builder dlg = new AlertDialog.Builder(context);
				dlg.setTitle(getResources().getString(R.string.selectOperation));
				dlg.setSingleChoiceItems(new String[]{getResources().getString(R.string.shutdown), getResources().getString(R.string.reboot)}, 0,
						new DialogInterface.OnClickListener() {							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch(which)
								{
									case 0:
										showShutdownDlg();
										break;
									case 1:
										showRebootDlg();
										break ;
								}								
							}
						}						
					);
				dlg.setNegativeButton(R.string.cancel, null);
				dlg.show();	 
				break;
			case R.id.settingBtn:
				showSettingdlg();
			 	break;
			case R.id.correctBtn:  //校准
				Utility.startPackage(context,"com.pm.calib","com.pm.calib.PmCalibActivity");
				break;
			case R.id.helpBtn:
				showHelpWebView();
/*				new AlertDialog.Builder(context) 
				.setTitle(context.getResources().getString(R.string.help_info))			
				.setMessage(HandleFile.readFile("help_info.txt",context))
				.setPositiveButton(getResources().getString(R.string.confirm),  null)
				.show();
			*/
				break ;
			case R.id.lockscreen:			
 				if(!screenLocked) //点击锁屏
				{ 
 					 DegreeView.getInstance().lockScreen(true);
 				}
				else  //点击解锁
				{
					 UnlockScreenDialog lockDlg = new UnlockScreenDialog(context, true);
					 lockDlg.show();					 
				}						 
			break;
			default:
				break;
			}
		}
	};
	
	private void showShutdownDlg()
	{
		AlertDialog.Builder dlg = new AlertDialog.Builder(context)
		.setTitle(getResources().getString(R.string.shutdown))
		.setMessage(R.string.shutdownConfirm) 
		.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {					
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PowerManager.halt(context);					 
			}
		} ) 
		.setNegativeButton(R.string.cancel, null);
		dlg.show();	
	}
	
	/**
	 *  显示重启对话框
	 *  */
	private void showRebootDlg()
	{
		AlertDialog.Builder dlg = new AlertDialog.Builder(context)
		.setTitle(getResources().getString(R.string.reboot))
		.setMessage(R.string.rebootConfirm) 
		.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {					
			@Override
			public void onClick(DialogInterface dialog, int which) {
				PowerManager.reboot(context);					 
			}
		} ) 
		.setNegativeButton(R.string.cancel, null);
		dlg.show();	
	}
	
	/**
	 *  显示设置对话框
	 *  */
	private void showSettingdlg()
	{
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setTitle("设置");
		dlg.setSingleChoiceItems(new String[]{getResources().getString(R.string.eth_settings),getResources().getString(R.string.communication_setting)}, 0, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int which)
			{
				 switch (which) 
				 {
					case 0:
						Intent intent = new Intent();
						intent.setClass(context,EthernetSettingActivity.class);
					 	HomeActivity.getInstance().startActivityForResult(intent,0); 
						break;
					case 1:
						Intent intent2 = new Intent();
						intent2.setClass(context, CommunicationSetActivity.class);
						context.startActivity(intent2);
						break;
					default:
						break;
				}				
			}
		}  
		);		
		dlg.show();		
	}
	/**
	 *  锁住/解锁左边的控件
	 **/
	public void lockLeftScreen(boolean islock)
	{
		    startEndBtn.setEnabled(islock);
		    scaleBtn.setEnabled(islock);
		    pairBtn.setEnabled(islock);		    
		    fileBtn.setEnabled(islock);
		    shutdownBtn.setEnabled(islock);
		    correctBtn.setEnabled(islock);
		    helpBtn.setEnabled(islock);
		    settingButton.setEnabled(islock);
		    if(!islock)
		    {
			     lockscreenButton.setState(STATE.selected);
			   lockscreenButton.setText(getResources().getString(R.string.unlockScreen));		
				 screenLocked = !screenLocked; 
		    }
		    else 
		    {
			     lockscreenButton.setState(STATE.normal);
			     lockscreenButton.setText(getResources().getString(R.string.lockscreen));		
				 screenLocked = !screenLocked; 
		    }
	}
	
	private void start()
	{
		 Log.e("start",isStart+"");
		 if(isfirstRun)
		 {		
			 isfirstRun = !isfirstRun;
			 new Thread()
	    	 {
		       	 public void run()
		       	 {
		       		LibDegree.dataWhilehandle(1);
		       	 }
	        }.start();
 			new Thread()
			{
				@Override
				public void run() 
				{
					 LibDegree.send485Settings(CommunicationSetting.getCommunicateSettings(context), 5);
				}
			}.start(); 
			startEndBtn.setText(R.string.pause);
			startEndBtn.setState(STATE.selected);
		 	 if(! ChartViewThread.isWait)
			 {
				HomeActivity.getInstance().stopPairflashing(); 
			 }
		 	if(ChartGraphView.darwHistory)
		 	{
		 		HomeActivity.getInstance().stopDrawHistory(); 
		 	}
			if(ChartGraphView.chartViewThread != null )
			{
				onLeftViewListener.continueDrawLine();
			}			
			ChartGraphView.darwHistory = false;
		 }
		 else 
		 {
			 if(LibDegree.FlagStatusStartEnd(0) == 1)
			 {
				 new Thread()
		    	 {
			       	 public void run()
			       	 {
			       		LibDegree.dataWhilehandle(1);
			       	 }
		        }.start();
				startEndBtn.setText(R.string.pause);
				startEndBtn.setState(STATE.selected);
			 	 if(! ChartViewThread.isWait)
				 {
					HomeActivity.getInstance().stopPairflashing(); 
				 }
			 	if(ChartGraphView.darwHistory)
			 	{
			 		HomeActivity.getInstance().stopDrawHistory(); 
			 	}
				if(ChartGraphView.chartViewThread != null )
				{
					onLeftViewListener.continueDrawLine();
				}			
				ChartGraphView.darwHistory = false;
			 }
			 else 
			 {
				 Toast.makeText(context,R.string.tryAgain,Toast.LENGTH_LONG).show();
			 } 
		 }
	}
	private void stop()
	{
		if(LibDegree.FlagStatusStartEnd(1) == 1)
		{
	    	 new Thread()
	    	 {
		       	 public void run()
		       	 {
		       		LibDegree.dataWhilehandle(0);
		       	 }
	        }.start();
			Log.e("start",isStart+"");
			startEndBtn.setText(R.string.start);
			startEndBtn.setState(STATE.normal);
			//存入一组全0 数据 显示历史数据的时候作为标志 
			int[] degree = new int[31]; 
			try {
				HandleFile.StoreData(0, degree);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
			if(ChartGraphView.chartViewThread != null)
			{
				 onLeftViewListener.pauseDrawLine();
			}
		} 		
		else 
		{
			Toast.makeText(context,R.string.tryAgain , Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 *  显示帮助网页
	 *  */
	private void showHelpWebView()
	{
		Intent intent = new Intent();
		intent.setClass(context, HelpActivity.class);
		context.startActivity(intent);
	}
}
