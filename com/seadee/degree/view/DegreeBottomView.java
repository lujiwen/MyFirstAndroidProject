package com.seadee.degree.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.seadee.degree.R;
import com.seadee.degree.activity.HomeActivity;
import com.seadee.degree.control.Buzzer;
import com.seadee.degree.control.Buzzer.BuzzerState;
import com.seadee.degree.control.CommonButton;
import com.seadee.degree.control.SDButton;
import com.seadee.degree.core.ChartGraphView;
import com.seadee.degree.core.ChartViewThread;
import com.seadee.degree.service.HandleFile;
import com.seadee.degree.utility.Utility;
public class DegreeBottomView extends LinearLayout {

	public interface OnBottomViewListener
	{
		public abstract void  drawLastScreen();
		public abstract void  drawNextScreen();		
		public abstract void  stopAlarmBuzzer();
		public abstract void  reDrawhistory();
		public abstract void  resetHorLables(String[] labels);
	}
	
	Context context;
	LayoutInflater layoutInflater;
	public TextView warnInfoTv  ;
	public TextView[] scaleTextViews; //下方的6个板温 
	public SDButton lastBtn,nextBtn;
	public CommonButton timeSettingBtn;
	private static final String tag = "DegreeBottomView";
	private ChartGraphView chartGraphView;
    public ScrollView  alarmScrollView ;
    public LinearLayout alarmLayout;
    private String minute ;
    private String hour ;
    private String selectScreeTime ;
    public static boolean screenLengthchanged ;
    public Buzzer buzzerBtn;
	OnBottomViewListener   onBottomViewListener ;
	static DegreeBottomView   degreeBottomViewInstance = null;
	public void SetOnBottomViewListener(OnBottomViewListener listener)
	{
		onBottomViewListener = listener;
	}
	
	public DegreeBottomView(Context context) {
		super(context);
		init(context);
	}

	public DegreeBottomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DegreeBottomView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		layoutInflater.inflate(R.layout.bottom_layout, this);		 
		chartGraphView = new ChartGraphView(context,"");
		
		warnInfoTv = (TextView) findViewById(R.id.warnInfoTv);
		warnInfoTv.setMaxLines(1000); // 最多显示100条告警信息
		warnInfoTv.setOnClickListener(listener);
		
		alarmLayout = (LinearLayout)findViewById(R.id.alarmMessage);
		alarmLayout.setOnClickListener(listener);
		
		alarmScrollView = (ScrollView)findViewById(R.id.alarmScroll);
		alarmScrollView.setOnClickListener(listener);
		lastBtn = (SDButton) findViewById(R.id.lastBtn);
		nextBtn = (SDButton) findViewById(R.id.nextBtn);
		timeSettingBtn = (CommonButton) findViewById(R.id.timeSettingBtn);
		alarmScrollView = (ScrollView) findViewById(R.id.alarmScroll);
		buzzerBtn = (Buzzer) findViewById(R.id.buzzingBtn);				
	 	buzzerBtn.setOnClickListener(listener);
		lastBtn.setOnClickListener(listener);	
		nextBtn.setOnClickListener(listener);
		onBottomViewListener = HomeActivity.getInstance();  
		timeSettingBtn.setOnClickListener(listener);
		
		//下方的6个板温 
		scaleTextViews = new TextView[6];
		scaleTextViews[0] = (TextView) findViewById(R.id.scaleDegree1Tv);
		scaleTextViews[1] = (TextView) findViewById(R.id.scaleDegree2Tv);
		scaleTextViews[2] = (TextView) findViewById(R.id.scaleDegree3Tv);
		scaleTextViews[3] = (TextView) findViewById(R.id.scaleDegree4Tv);
		scaleTextViews[4] = (TextView) findViewById(R.id.scaleDegree5Tv);
		scaleTextViews[5] = (TextView) findViewById(R.id.scaleDegree6Tv);
	 
		minute = getResources().getString(R.string.minute) ;
		hour = getResources().getString(R.string.hour) ;
		selectScreeTime = getResources().getString(R.string.selectScreeTime) ;
		HandleFile.record(context,"SD_config", "screenLengthItem", 1); 
		degreeBottomViewInstance = this;
	}
	public static DegreeBottomView getInstance()
	{
		return degreeBottomViewInstance ;
	}
	void setScaleDegrees(int x)
	{
		scaleTextViews[0].setText(x+"");
	}
	
	//将警告栏中的报警信息显示到对话框上
	private void showAlarmMessageDlg()
	{
		final	AlertDialog.Builder  dlg = new AlertDialog.Builder(context);
		dlg.setMessage(warnInfoTv.getText());
		dlg.setTitle(getResources().getString(R.string.AlarmMsg));
		dlg.setNegativeButton(getResources().getString(R.string.clearAlarmMsg), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dlg.setMessage("");
				warnInfoTv.setText("");
			}
		});
		dlg.show();
	}
	
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.warnInfoTv:
				showAlarmMessageDlg();
				break;
			case R.id.lastBtn:	
				//绘制上一屏线条		
				if(ChartGraphView.darwHistory)
				{
					if(DegreeTopView.selectedCount != 0)
					{
						onBottomViewListener.drawLastScreen() ;
					}		
					else 
					{
						Toast.makeText(context, R.string.selectPromote , Toast.LENGTH_SHORT).show();
					}
				}
				else 
				{
					 Toast.makeText(context, R.string.selectDate,Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.nextBtn:
				if(ChartGraphView.darwHistory)
				{
					if(DegreeTopView.selectedCount != 0)
					{
						onBottomViewListener.drawNextScreen();
					}		
					else 
					{
						Toast.makeText(context, R.string.selectPromote , Toast.LENGTH_SHORT).show();
					}
				}
				else 
				{
					 Toast.makeText(context, R.string.selectDate,Toast.LENGTH_SHORT).show();
				}
				
				break;
			case R.id.timeSettingBtn:
				if(!DegreeTopView.isRun)
				{
					showTimeSettingDlg();	
				}						
				break;
			case R.id.buzzingBtn:								
			 	if(ChartViewThread.startAlarm ) //点击蜂鸣器按钮 停止报警 
				 {			
			 		buzzerBtn.stopAlarm();
				 	Log.i("BottomView","buzzer stop! ");			 	
					ChartViewThread.startAlarm = false;
				 	onBottomViewListener.stopAlarmBuzzer();				 	
				 }	
			 	else //正常，未报警状态下，切换蜂鸣器状态
			 	{
				 	switchBuzzerState();
			 	}
				break;
			default:
				break;
			}
		}
	};
	private void  switchBuzzerState()
	{
		if(!ChartViewThread.startAlarm)
		{
		 	if(buzzerBtn.getBuzzerState()==BuzzerState.disable)
		 	{
		 		buzzerBtn.setState(BuzzerState.normal);
		 	}			 	
		 	else  
		 	{
		 		buzzerBtn.setState(BuzzerState.disable);
		 	}
		}
	}
	public static  String[] labels ; 
	private int  chooseItem ;
	
	//屏幕时间长度设置对话框显示
	private void showTimeSettingDlg() 
	{		 
	 	//弹出文件操作对话框 
		AlertDialog.Builder dialBuilder =	new AlertDialog.Builder(context);
		dialBuilder.setTitle(selectScreeTime);	
		chooseItem = HandleFile.getRecord(context, "SD_config", "screenLengthItem", 1) ;
		dialBuilder.setSingleChoiceItems(new String[] {"15"+minute,"30"+minute,"1"+hour,"2"+hour}, chooseItem, 		
				new DialogInterface.OnClickListener() {				
					@Override
					public void onClick(DialogInterface dialog, int which) {
					  switch(which)
					  {
						  case 0 :	
							  labels = new String[]{"0min","5min","10min","15min"};
							  ChartGraphView.maxPointNum = 301 ;							
							  break;
						  case 1:
							  labels = new String[]{"0min","10min","20min","30min"};
							  ChartGraphView.maxPointNum = 601 ;
							  break;
						  case 2:							 
							  labels = new String[]{"0min","20min","40min","60min"};
							  ChartGraphView.maxPointNum = 1201 ;
							  break;
						  case 3:							 
							  labels = new String[]{"0min","40min","80min","120min"};
							  ChartGraphView.maxPointNum = 2401 ;
							  break;
						  default:
							  break;
						}					  	
					  screenLengthchanged = true ;
					  onBottomViewListener.resetHorLables(labels);
					  if((DegreeTopView.selectedCount>0)&&(ChartGraphView.darwHistory == true))
					  {
						  onBottomViewListener.reDrawhistory();
					  }					  
					  	dialog.dismiss() ;	
					  	HandleFile.record(context,"SD_config", "screenLengthItem", which) ;
					  }							  				
					}						 
				 );
		dialBuilder.setNegativeButton(getResources().getString(R.string.cancel), null); 
		dialBuilder.show();	
	}
	
	//锁住或者解锁 BottomView,禁用或者启用控件
	public void lockBottomScreen(boolean islock)
	{
		warnInfoTv.setEnabled(islock);
		lastBtn.setEnabled(islock);
		nextBtn.setEnabled(islock);
		buzzerBtn.setEnabled(islock);
		timeSettingBtn.setEnabled(islock);
	}
	
	//更新下方6个板温
	public void updateBoardDegree(int[] boardDegrees)
	{
	//	Log.e("updateBoardDegree",boardDegrees[0]+":"+boardDegrees[1]+":"+boardDegrees[2]+":"+boardDegrees[3]+":"+boardDegrees[4]+":"+boardDegrees[5]);
		for(int i=0;i<6;i++)
		{
			if(DegreeTopView.paired[i+1])
			{
				scaleTextViews[i].setText(String.valueOf(boardDegrees[i]));	
			}
			else 
			{
				scaleTextViews[i].setText("");
			}
		}
	}
	//更新报警栏的报警信息
	public void updateAlrmInfo(String alrmString)
	{		
		 warnInfoTv.append(alrmString+"("+Utility.getSystemTime()+")"+"\n");
		 //显示定位到最后一条警告信息 
		 alarmScrollView.fullScroll(ScrollView.FOCUS_DOWN);
	}
	
	public void clearBoardDegreeTv()
	{
		for(int i=0;i<6;i++)
		{
			scaleTextViews[i].setText("");
		}
	}
}
