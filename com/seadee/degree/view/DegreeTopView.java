package com.seadee.degree.view;

import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DigitalClock;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seadee.degree.R;
import com.seadee.degree.activity.HomeActivity;
import com.seadee.degree.control.Battery;
import com.seadee.degree.control.KeyBoardDialog;
import com.seadee.degree.control.KeyBoardDialog.KeyboardHandleListener;
import com.seadee.degree.control.SDButton;
import com.seadee.degree.control.SDButton.STATE;
import com.seadee.degree.control.SDEditText;
import com.seadee.degree.control.TimeSettingDialog;
import com.seadee.degree.core.ChartGraphView;
import com.seadee.degree.core.ChartViewThread;
import com.seadee.degree.core.DegreeView;
import com.seadee.degree.service.HandleFile;
import com.seadee.degree.service.LibDegree;
import com.seadee.degree.service.PowerManager;

public class DegreeTopView extends LinearLayout  implements KeyboardHandleListener 
{	
	public interface OnTopViewListener
	{
	 	public abstract void startDraw();
		public abstract void enableStartDraw();
		public abstract void pairFalshing(int transNum);
		public abstract void startDrawHistory();
		public abstract void removeSeries(int transmiter);
	}
		
	private OnTopViewListener onTopViewListener ;
	private Context context;
	LayoutInflater layoutInflater;
	public LinearLayout transmitter;
	public SDButton[] tranButton = new SDButton[6];  
	
	private TextView chineseTv, englishTv;
	private static  LinearLayout dateSettingLayout;
	public static  boolean isRun = false ;  
	public SDEditText alarmEditText[] =new SDEditText[2] ; //2个报警温度设置文本编辑框
	public static int[] selectedButtonPos; //选中的发射机在右侧的显示位置 取值1-3,0：未分配到位置
	public static int[]  Selected ; //存储已经选中的发射机
	public static boolean pairDone ;  
	public final  int duration = 500;
	public static boolean[]  isStarted ;  //标记1-6号发射机是否已经开始绘制线条
	public static int[] alarmDegree;
	public static  int alrmboardDegree;
	public static int selectedCount  ;  
	public static boolean[] paired; //标记1-6号发射机是否已经配对
	public static  boolean StartFalshing;
	public static int  highestDegree;
	public static int BoardAlarmDegree ;
	public LinearLayout alarmDegreeLayout ;
	public static boolean switchLangEnable ; 
	private CharSequence  date ; 
	private DigitalClock digitalClock ;	 //数字时钟 显示“时分秒”
	public TextView dateTextView; //日期显示 
	public Battery[] battery  ;
	private KeyBoardDialog[] keyBoardDialog = new KeyBoardDialog[2]; //2个报警温度设置框各自对应1个数字键盘
	public DegreeTopView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle); 
		init( context);	
	}

	public DegreeTopView(Context context ) {
		super(context );
		init(context);
	}
	public DegreeTopView(Context context, AttributeSet attrs ) {
		super(context, attrs );
		init(context);		
	}
	private void init( Context context)
	{			 
		this.context = context;
		this.layoutInflater = layoutInflater.from(context);
        layoutInflater.inflate(R.layout.top_layout, this);
		transmitter = (LinearLayout)findViewById(R.id.transmitter_layout);
		LinearLayout.LayoutParams layoutParams =new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT,
				1);
		layoutParams.setMargins(2, 0, 2, 0); 
		pairDone = true ;
		selectedButtonPos = new int[7] ; 
		Selected = new int[3];		
		paired = new boolean[7];		
		int pairNum  = HandleFile.getRecord(context,"SD_config", "pair", 0);
		Log.e("paired", pairNum+"") ;
		
		int temp =0 ;
		selectedCount = 0;
		for(int i=0;i<tranButton.length;i++)
		{
			  tranButton[i] = new SDButton(context);			  
			  tranButton[i].setText(""+(i+1));
			  tranButton[i].setState(STATE.normal);
			  tranButton[i].setOnClickListener(clickListener);
			  tranButton[i].setOnLongClickListener(longClickListener);
			  transmitter.addView(tranButton[i],layoutParams);				  
		}	
		for(int i=1;i<7;i++)
		{
			temp = pairNum%10; 			
			if(temp ==1 )
			{
				paired[7-i] = true;
				tranButton[(6-i)].setState(STATE.normal);
			}
			else 
			{
				tranButton[(6-i)].setState(STATE.disable); 
			}
			pairNum = pairNum/10;
		}
		isStarted = new boolean[7];       

		chineseTv = (TextView) findViewById(R.id.chineseTv);
		englishTv = (TextView) findViewById(R.id.englishTv);
		chineseTv.setOnClickListener(languageListener);
		englishTv.setOnClickListener(languageListener); 
		
 		if(HandleFile.getRecord(context,"SD_config", "isChinese",1) == 1)
		{
			chineseTv.setBackgroundResource(R.drawable.sd_ic_btn_choose);
			englishTv.setBackgroundResource(R.drawable.sd_ic_btn_normal) ;
		}
		else 
		{
			englishTv.setBackgroundResource(R.drawable.sd_ic_btn_choose);
			chineseTv.setBackgroundResource(R.drawable.sd_ic_btn_normal);
		} 
		dateSettingLayout = (LinearLayout) findViewById(R.id.dateSetting);	
		dateSettingLayout.setOnClickListener(clickListener);
		
		//dateTextView = new TextView(context);
		dateTextView = (TextView)findViewById(R.id.dateTv);
		dateTextView.setOnTouchListener(touchListener);
		long systime = System.currentTimeMillis();
		date = DateFormat.format("yyyy/MM/dd", systime);
		dateTextView.setText(date);
		
	 	digitalClock = new DigitalClock(context);
	 	digitalClock = (DigitalClock)findViewById(R.id.digitalClockShow) ;
		digitalClock.setOnTouchListener(touchListener);
		
		onTopViewListener = HomeActivity.getInstance();
		alarmDegreeLayout = (LinearLayout)findViewById(R.id.alarmEditText) ;
		
		alarmEditText = new SDEditText[2];
		alarmEditText[0] = new SDEditText(context);
		alarmEditText[1] = new SDEditText(context);
		alarmEditText[0] = (SDEditText)findViewById(R.id.highestAlarm) ;
		alarmEditText[1] = (SDEditText)findViewById(R.id.boardAlarm);
 		for(int i=0;i<2;i++)
		{	
		    alarmEditText[i].setOnTouchListener(touchListener) ;
		    alarmEditText[i].setCursorVisible(false);		
		 
		} 
 		alarmEditText[0].setMaxValue(999);
 		alarmEditText[0].setMaxLength(3);
		alarmEditText[1].setMaxValue(80);
		alarmEditText[1].setMaxLength(2);
		highestDegree = HandleFile.getRecord(context,"SD_config","highestDegree", 0);
		BoardAlarmDegree = HandleFile.getRecord(context,"SD_config","BoardAlarmDegree", 0);

		alarmEditText[0].setText(highestDegree+"");
	 	alarmEditText[1].setText(BoardAlarmDegree+""); 
	 		 	
	 	switchLangEnable = true ;
	 	initBattery();
	 	for(int i=0;i<2;i++)
	 	{
	 		keyBoardDialog[i] = new KeyBoardDialog(context, false,alarmEditText[i]);
	 		keyBoardDialog[i].setKeyboardEventListener(this);
	 	}
	}
	private void initBattery()
	{
		battery = new Battery[6];
	 	battery[0] = (Battery)findViewById(R.id.battery1);
	 	battery[1] = (Battery)findViewById(R.id.battery2);
	 	battery[2] = (Battery)findViewById(R.id.battery3);
	 	battery[3] = (Battery)findViewById(R.id.battery4);
	 	battery[4] = (Battery)findViewById(R.id.battery5);
	 	battery[5] = (Battery)findViewById(R.id.battery6);
	}
	
private OnLongClickListener longClickListener = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			if(!DegreeLeftView.isStart)
			{
				if(v == tranButton[0])
				{
					deletePaird(1);
				}
				else if(v == tranButton[1])
				{
					deletePaird(2);
				}
				else if(v == tranButton[2])
				{
					deletePaird(3);				
				}
				else if(v == tranButton[3])
				{
					deletePaird(4);
				}
				else if(v == tranButton[4])
				{
					deletePaird(5);
				}
				else if(v == tranButton[5])
				{
					deletePaird(6);
				}
			}
			return false;
		}
	};
	
	/**
	 * long Pess the button to delete the pair history to re-pair 
	 *@param transNum  the tranmitter button slected to remove pair history
	 * */
	private void deletePaird(final int transNum)
	{
		if((!paired[transNum])||isStarted[transNum])
		{
			Log.e("pair","can't delete pair record!");
			return ;
		}
		AlertDialog.Builder dlg = new AlertDialog.Builder(context);
		dlg.setTitle(R.string.deletePair);
		dlg.setMessage(R.string.deletePairConfirm);
		dlg.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				Log.e("longpress2Delete", transNum+"");
				paired[transNum] = false ;
				int pairnum = pairConvertToNum();
				HandleFile.record(context, "SD_config", "pair", pairnum);
				tranButton[transNum-1].setState(STATE.disable) ;
			}
		});
		dlg.setNegativeButton(R.string.cancel, null) ;
		dlg.show();
	}
	
	private OnTouchListener touchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
	
			switch (event.getAction()) {
			case   MotionEvent.ACTION_DOWN:
				if(v == alarmEditText[0])
				{
					keyBoardDialog[0].show();
					keyBoardDialog[1].dismiss();
				}
				else if(v == alarmEditText[1])
				{
					 keyBoardDialog[1].show();
					 keyBoardDialog[0].dismiss();
				}
				else if(v == digitalClock)
				{
					//TimeSettingDialog dlg = new TimeSettingDialog(context, true);
					TimeSettingDialog dlg = new TimeSettingDialog(context, true,dateTextView);
					dlg.show();
				}
				else if (v == dateTextView)
				{
					TimeSettingDialog dlg = new TimeSettingDialog(context, true,dateTextView);
					dlg.show();
				}
				break;

			default:
				break;
			}
			return false;
		}
	}; 

     /**
      * Count how many transBtn has been selected
      * @return the number of the transBtn which has been selected   
      **/
	private int countSelected()
	{
		int cnt =0;
		for(int i=0;i<3;i++)
		{
			if(Selected[i] !=0 )
			{
				cnt += 1;
			}
		}
		return cnt;
	}
	
	/**
	 * check if one club of 4 textViews in rightView left to show transNum's 4 degrees 
	 * @param trasNum  you know that  
	 * @return  if one club left on the rightView , return the num of the club else return 0
	 * */
	private int  checkPosition(int transNum) 
	{
		for(int i=0;i<3;i++ )
		{
			if(Selected[i] == 0)
			{
				Selected[i] = transNum;
				selectedCount = countSelected();
				HomeActivity.getInstance().initTextView(transNum,i);  //给选中的发射机分配的右侧4个TextView初始化“--”
				return i+1 ;
			}
		}		
		return 0 ;
	}

	/**
	 * covert paired[] to num like 010110	
	 * 0 means hasn't been  paired  1 mean has
	 **/ 
	private  int pairConvertToNum()
	{
		int tmp=0;
		for(int i=1;i<7;i++)
		{
			if(paired[i])
			{
				tmp += Math.pow(10, 6-i);
			}
		}
			return tmp;
	}
	
	private  int selectedConvertToNum()
	{
		int tmp=0;
		for(int i=1;i<7;i++)
		{
			if(selectedButtonPos[i] != 0)
			{
				tmp += Math.pow(10, 6-i);
			}
		}
			return tmp;
	}
	
	public Handler  pairHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{						
			boolean result = msg.getData().getBoolean("isPairSuccess") ;
			int btnNum = msg.what;
			if(result)  //配对成功
			{
				HandleFile.record(context,"SD_config","isFirsrtRun",0);
				paired[btnNum] = true;
				int pairedNum  = pairConvertToNum();
				HandleFile.record(context,"SD_config", "pair",pairedNum);				
				Toast.makeText(context, btnNum+getResources().getString(R.string.pairSuccess) , duration).show();
			}
			else  //配对失败
			{
				Bundle b = msg.getData();
				int lastpairNum = b.getInt("lastPairdNum");
				if(lastpairNum ==0) //匹配失败
				{
					Toast.makeText(context, getResources().getString(R.string.pairFail1), duration).show();
				}
				else if(lastpairNum > 0) //发射机已经配过对，配对失败
				{
					Toast.makeText(context, getResources().getString(R.string.pairFail2)+lastpairNum+getResources().getString(R.string.pairFail2Tail), lastpairNum).show(); 
				}
				
				HandleFile.record(context,"SD_config","isFirsrtRun",0);
				paired[btnNum] = false;
				int pairedNum  = pairConvertToNum();
				HandleFile.record(context,"SD_config", "pair",pairedNum);
				Toast.makeText(context, getResources().getString(R.string.pairFail), duration).show();
			}
			isStarted[btnNum] = false ;
			Log.e("pairResult",  result+"");
			pairDone = true ; 
			choosePairBtn = 0;
			onTopViewListener.enableStartDraw();
			DegreeLeftView.PairStarted = false;
	
				for(int i=1;i<7;i++)
				{
					if(paired[i])
					{
						tranButton[i-1].setState(STATE.normal);
					}
					else 
					{
						tranButton[i-1].setState(STATE.disable);
					}						
				}
				for(int j=0;j<tranButton.length;j++ )
				{
					tranButton[j].setEnabled(true) ;					
				}
		}				
	};
	
	public static int choosePairBtn = 0 ;
	
	private synchronized void pair(final  int transmiterNum) 
	{		
		choosePairBtn = transmiterNum ;
		StartFalshing = true ;
		for(int i=0;i<tranButton.length;i++ ) //每次只允许一个发射机点击配对，其余发射机禁止
		{
			if(transmiterNum != (i+1))
			{
				tranButton[i].setEnabled(false) ;
			}
		}		
		
		//配对发射机闪烁
		onTopViewListener.pairFalshing(transmiterNum);		
		new Thread() 
		{
				public void run()				
				{
					synchronized(this)
					{
						Log.e("pair",transmiterNum+"");					
						Message msg = new Message();
						Bundle bundle = new Bundle() ;
						int result = LibDegree.MatchAddress(transmiterNum) ;
					 
						if(result == 0)
						{
							bundle.putBoolean("isPairSuccess", false) ;
							bundle.putInt("lastPairdNum", 0);
							msg.setData(bundle);
							msg.what = transmiterNum ;
						}
						else if(result > 0) 
						{
							if(result == transmiterNum)
							{
								bundle.putBoolean("isPairSuccess", true) ;
								msg.setData(bundle);
								msg.what = transmiterNum ;
							}
							else
							{
								bundle.putBoolean("isPairSuccess", false) ;
								bundle.putInt("lastPairdNum", result);
								msg.setData(bundle);
								msg.what = transmiterNum ;
							}
						}
						StartFalshing = false;
						Log.e("StarFlashing", StartFalshing+"") ;
						pairHandler.sendMessage(msg);
						msg = null;
						bundle = null ;
					}
				}		
		}.start();	
	}
	
	/**
	 * draw transmiter's lines or delete the transmiter's 4 lines 
	 * @param transmiter  the number of the Transmiter 
	 */	
	private void  drawline(int transmiter)
	{  
		if(!isRun&&(selectedCount < 3)&&paired[transmiter])   // draw the first slected tranmister's 4  lines 
		{
			Log.e("drawLine", "1");
			tranButton[transmiter-1].setState(STATE.selected) ;
			selectedButtonPos[transmiter] = checkPosition(transmiter);//给选中的发射机在右侧分配4个textView(=1个club)的位置来显示 
			DegreeRightView.setLineColor(selectedButtonPos[transmiter]-1,false);//给选中的4条曲线分配颜色
			onTopViewListener.startDraw();
			isRun= true ;
			isStarted[transmiter] = true; 	
		 	UpdateBaterry(LibDegree.GetVoltage());
		}
		else if(isStarted[transmiter]&&isRun)  // click selected transmiter button clear 4 lines of the transmiter    
		{
			Log.e("drawLine", "2");
			tranButton[transmiter-1].setState(STATE.normal);
			DegreeRightView.setLineColor(selectedButtonPos[transmiter]-1,true);//清除该发射机绘制的线条
			isStarted[transmiter] = false;
			ChartViewThread.isTransBtnAlarm[transmiter-1] = false;   //无论该发射机报警与否，都停止报警
			clearTransmiter(transmiter);	
		    UpdateBaterry(LibDegree.GetVoltage());
		}			 
		else if((isRun)&&(!isStarted[transmiter])&&(selectedCount < 3)&&(paired[transmiter])) //a other transmiter has been selected ,select a new transmiter to run  			
		{			
			Log.e("drawLine", "3");
			 if(DegreeTopView.selectedButtonPos[transmiter]>0)
			 {
					tranButton[transmiter-1].setState(STATE.selected);
					DegreeRightView.setLineColor(selectedButtonPos[transmiter]-1,false); //閿熸枻鎷烽敓鎻鎷烽敓绲猠lectedCount-1閿熶粙璧嬮敓鏂ゆ嫹鑹�閿熸枻鎷烽敓绔尅鎷峰閿熶茎鎲嬫嫹蹇�
					isStarted[transmiter] = true;	
			 }
			 else 
			 {
					tranButton[transmiter-1].setState(STATE.selected);
					selectedButtonPos[transmiter] = checkPosition(transmiter);//閿熸枻鎷烽敓鎻紮鎷烽敓鏂ゆ嫹閿熻娇锟介敓鏂ゆ嫹绀洪敓鏂ゆ嫹涓�敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
					DegreeRightView.setLineColor(selectedButtonPos[transmiter]-1,false); //閿熸枻鎷烽敓鎻鎷烽敓绲猠lectedCount-1閿熶粙璧嬮敓鏂ゆ嫹鑹�閿熸枻鎷烽敓绔尅鎷峰閿熶茎鎲嬫嫹蹇�
					isStarted[transmiter] = true;	
			 }
		}	
		LibDegree.setSendStatus(selectedConvertToNum());
		Log.e("sendSlectedStatus",selectedConvertToNum()+"");
	}
			
	/**
	 * clear all the infomation of the transmiter include lines,degree on the right textView
	 * and boardDegree on the bottom textView and ...   
	 * @param transmiter   
	 * */
	private  void clearTransmiter(int transmiter) 
	{	   
	     for(int i=0;i<3;i++ )
	     {
	    	 if(Selected[i] ==  transmiter ) 
	    	 {
	    		 Selected[i] = 0;
	    	 }
	     }	     
  		Message msg = new Message();
  		msg.arg1 = selectedButtonPos[transmiter] -1 ;
  		msg.arg2 = 4; //同时取消一个发射机的4条曲线的显示
  		ChartViewThread.clearSeriesHandler.sendMessage(msg);  
		HomeActivity.getInstance().clearTextView(msg.arg1,transmiter);
		selectedButtonPos[transmiter] = 0;
		selectedCount = countSelected();
	}  	
	
	private OnClickListener languageListener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			if(v == chineseTv)
			{
				
				if(HandleFile.getRecord(context,"SD_config", "isChinese", 1)==1)
				{
					Toast.makeText(context,"当前正在使用中文!", Toast.LENGTH_SHORT).show() ;
				}
				else 
				{
					AlertDialog.Builder dlg = new AlertDialog.Builder(context)
					.setTitle(getResources().getString(R.string.shutdown))
					.setMessage(R.string.langSwitchConfirm) 
					.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							HandleFile.record(context,"SD_config", "isChinese", 1);
							com.android.internal.app.LocalePicker.updateLocale(Locale.PRC );
							chineseTv.setBackgroundResource(R.drawable.sd_ic_btn_choose);
							englishTv.setBackgroundResource(R.drawable.sd_ic_btn_normal);
							PowerManager.reboot(context);		
						}
					} ) 
					.setNegativeButton(R.string.cancel, null);
					dlg.show();										
				}			
			}
			else if(v == englishTv)
			{
				
				if(HandleFile.getRecord(context,"SD_config", "isChinese", 1)==0)
				{
					Toast.makeText(context, "English is currently in use!", Toast.LENGTH_SHORT).show() ;
				}
				else 
				{
					AlertDialog.Builder dlg = new AlertDialog.Builder(context)
					.setTitle(getResources().getString(R.string.shutdown))
					.setMessage(R.string.langSwitchConfirm) 
					.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							HandleFile.record(context,"SD_config", "isChinese", 0);
							com.android.internal.app.LocalePicker.updateLocale(Locale.ENGLISH );
							chineseTv.setBackgroundResource(R.drawable.sd_ic_btn_normal);
							englishTv.setBackgroundResource(R.drawable.sd_ic_btn_choose);	
							PowerManager.reboot(context);		
						}
					} )  
					.setNegativeButton(R.string.cancel, null);
					dlg.show();
					Log.e("switch", "English");
				}				
			}
		}	
	};
	private OnClickListener  clickListener  = new OnClickListener() {	
		@Override
		public void onClick(View v) {
			switchLangEnable = false ;
			if((DegreeLeftView.isStart)&&(pairDone)&&(!DegreeLeftView.PairStarted)) 
			{
				Log.e("click", "drawLine!");
				if((v == tranButton[0]))
				{																				
					drawline(1);
				}
				else if(v == tranButton[1]) 
				{
					drawline(2);
				}
				else if(v == tranButton[2]) 
				{
					drawline(3);
				}
				else if(v == tranButton[3]) 
				{
					drawline(4);
				}
				else if(v == tranButton[4]) 
				{						
					drawline(5);
				}
				else if(v == tranButton[5]) 
				{
					drawline(6);
				}
			}//end if
			if(DegreeLeftView.PairStarted &&(!pairDone)) // 閿熺獤鎾呮嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閽�閿熸枻鎷峰閿熸枻鎷烽敓锟� 
			{
				Log.e("click","pair!");
				if(v == tranButton[0])
				{ 
					pair(1); 
				}
				else if (v == tranButton[1]) 
				{										 
					pair(2);
				}
				else if (v == tranButton[2]) 
				{ 
					pair(3);
				}			
				else if (v == tranButton[3]) 
				{		 
					pair(4);
				}
				else if (v == tranButton[4]) 
				{
					pair(5);
				}
				else if (v == tranButton[5]) 
				{
					pair(6);
				}				
			}	
			if(ChartGraphView.darwHistory&&!DegreeLeftView.PairStarted)
			{
				Log.e("click", "drawLine!");
				if(v == tranButton[0])
				{
					drawHistory(1);
				}
				else if(v == tranButton[1])
				{
					drawHistory(2);
				}
				else if(v == tranButton[2])
				{
					drawHistory(3);
				}
				else if(v == tranButton[3])
				{
					drawHistory(4);
				}
				else if(v == tranButton[4])
				{
					drawHistory(5);
				}
				else if(v == tranButton[5])
				{
					drawHistory(6);
				}				
			}
		}
	};
 
	public  void initSelectStatus()
	{
		selectedButtonPos = new int[7] ;
		selectedCount = 0 ;
		Selected = new int[3];
	}
	private void drawHistory(int transmiter)
	{		
		if(selectedButtonPos[transmiter] != 0)  //remove transmiter's series 
		{
			 onTopViewListener.removeSeries(transmiter) ;
			 tranButton[transmiter-1].setBackgroundResource(R.drawable.sd_ic_btn_normal);
			 Selected[selectedButtonPos[transmiter]-1] = 0;
			 selectedButtonPos[transmiter] = 0; 
			 selectedCount-- ;
		}
		else  if((selectedCount<3)&&(selectedButtonPos[transmiter] == 0))
		{
			tranButton[transmiter-1].setBackgroundResource(R.drawable.sd_ic_btn_choose);	 	
		 	selectedButtonPos[transmiter] = checkPosition(transmiter);//閿熸枻鎷烽敓鎻紮鎷烽敓鏂ゆ嫹閿熻娇锟介敓鏂ゆ嫹绀洪敓鏂ゆ嫹涓�敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷
			onTopViewListener.startDrawHistory();
		} 
	}
 	@Override
	public Handler getKeyboardEventHandler() {
		return KeyBoardEventhandler ;
	} 
 	
 	private  Handler KeyBoardEventhandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{	
			int editID = 0 ;
			if(!msg.getData().isEmpty())
			{
				Bundle bundle = msg.getData();
				
				 //保存第一个editText传回来的报警值
				if( bundle.containsKey(String.valueOf(R.id.highestAlarm)))
				{ 
					int highestDegree = bundle.getInt(String.valueOf(R.id.highestAlarm));
					Log.e("key",highestDegree+"");
				    DegreeTopView.this.highestDegree = highestDegree;
					HandleFile.record(context,"SD_config", "highestDegree",highestDegree);	
				}
				//保存第二个editText回传的设置报警值
				else if(bundle.containsKey(String.valueOf(R.id.boardAlarm)))
				{
					int BoardAlarmDegree = bundle.getInt(String.valueOf(R.id.boardAlarm));
					Log.e("key",BoardAlarmDegree+"");
					DegreeTopView.this.BoardAlarmDegree = BoardAlarmDegree;
					HandleFile.record(context,"SD_config", "BoardAlarmDegree",BoardAlarmDegree);
				}
			}
			 super.handleMessage(msg);
		}		
	}; 
	
	//锁住或者解锁TopView中的控件,不允许/允许点击
	public void lockTopScreen(boolean islock)
	{
		for(int i=0;i<6;i++)
		{
			tranButton[i].setEnabled(islock);			
		}
		chineseTv.setEnabled(islock);
		englishTv.setEnabled(islock);
		alarmEditText[0].setEnabled(islock);
		alarmEditText[1].setEnabled(islock);
		dateTextView.setEnabled(islock);
		digitalClock.setEnabled(islock);
	}
	
	
	//更新配过对的发射机的电池电量
	public void UpdateBaterry(int[] batsPwoer)
	{		
		for(int i=1;i<=6;i++)
		{
			if (paired[i]) 
			{
				battery[i-1].setPower(batsPwoer[i-1]);
			}
			else 
			{
				battery[i-1].setBateryDisable();
			}
		}		
	}
	
	public void initTransBtn(int transBtnNum,int clubNum)
	{
		 tranButton[transBtnNum-1].setState(STATE.normal); 
		 Selected[clubNum] = 0;
		 selectedCount -= 1;
		 selectedButtonPos[transBtnNum] = 0;
		 isStarted[transBtnNum] = false ;
	}
}  
