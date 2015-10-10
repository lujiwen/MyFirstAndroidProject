package com.seadee.degree.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seadee.degree.R;
import com.seadee.degree.activity.HomeActivity;
import com.seadee.degree.core.ChartGraphView;
import com.seadee.degree.core.ChartViewThread;
import com.seadee.degree.core.DegreeView;
import com.seadee.degree.service.SettingVarible;
public class DegreeRightView extends LinearLayout {
	
	Context context;  
	LayoutInflater layoutInflater;  
 
	public  TextView oneDegree1Tv, oneDegree2Tv,oneDegree3Tv,oneDegree4Tv;
	public  TextView twoDegree1Tv,twoDegree2Tv,twoDegree3Tv,twoDegree4Tv;
	public  TextView threeDegree1Tv,threeDegree2Tv,threeDegree3Tv,threeDegree4Tv;
	public  TextView[] degreeTextView;
	public ImageView oneDegree1ColorIv,oneDegree2ColorIv,oneDegree3ColorIv,oneDegree4ColorIv;
	public ImageView twoDegree1ColorIv,twoDegree2ColorIv,twoDegree3ColorIv,twoDegree4ColorIv;
	public ImageView threeDegree1ColorIv,threeDegree2ColorIv,threeDegree3ColorIv,threeDegree4ColorIv;
	
//	OnRightViewListener onRightViewListener;
    public  static int[] LineColors ; 
	public ChartGraphView chartGraphView;
	private static boolean[]  isLineStarted ;
	public static int color[]; 
	private String horLine ;
 	public DegreeRightView(Context context) {
		super(context);
		init(context);
	}

	public DegreeRightView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DegreeRightView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(Context context) {
		
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
		layoutInflater.inflate(R.layout.right_layout, this);
		
		initDegreeTextView();	
		oneDegree1ColorIv = (ImageView) findViewById(R.id.oneDegree1colorIv);
		oneDegree2ColorIv = (ImageView) findViewById(R.id.oneDegree2colorIv);
		oneDegree3ColorIv = (ImageView) findViewById(R.id.oneDegree3colorIv);
		oneDegree4ColorIv = (ImageView) findViewById(R.id.oneDegree4colorIv);
		
		twoDegree1ColorIv = (ImageView) findViewById(R.id.twoDegree1colorIv);
		twoDegree2ColorIv = (ImageView) findViewById(R.id.twoDegree2colorIv);
		twoDegree3ColorIv = (ImageView) findViewById(R.id.twoDegree3colorIv);
		twoDegree4ColorIv = (ImageView) findViewById(R.id.twoDegree4colorIv);
		
		threeDegree1ColorIv = (ImageView) findViewById(R.id.threeDegree1colorIv);
		threeDegree2ColorIv = (ImageView) findViewById(R.id.threeDegree2colorIv);
		threeDegree3ColorIv = (ImageView) findViewById(R.id.threeDegree3colorIv);
		threeDegree4ColorIv = (ImageView) findViewById(R.id.threeDegree4colorIv);
		
		LineColors = new int[13];
	    isLineStarted = new boolean[13];  
	    color = new int[13]; 	
	    convertColor();  
	    horLine = context.getResources().getString(R.string.line) ; 
		initEventListener(); 		
	}
	void initDegreeTextView()
	{
		degreeTextView = new TextView[13];
		for(int i=1;i<13;i++ )
		{
			degreeTextView[i] = new TextView(context);
		}
		degreeTextView[1] =(TextView) findViewById(R.id.oneDegree1ShowTv);
		degreeTextView[2] =(TextView) findViewById(R.id.oneDegree2ShowTv);
		degreeTextView[3] =(TextView) findViewById(R.id.oneDegree3ShowTv);
		degreeTextView[4] =(TextView) findViewById(R.id.oneDegree4ShowTv);
		degreeTextView[5] =(TextView) findViewById(R.id.twoDegree1ShowTv);
		degreeTextView[6] =(TextView) findViewById(R.id.twoDegree2ShowTv);
		degreeTextView[7] =(TextView) findViewById(R.id.twoDegree3ShowTv);
		degreeTextView[8] =(TextView) findViewById(R.id.twoDegree4ShowTv);
		degreeTextView[9] =(TextView) findViewById(R.id.threeDegree1ShowTv);
		degreeTextView[10] =(TextView) findViewById(R.id.threeDegree2ShowTv);
		degreeTextView[11] =(TextView) findViewById(R.id.threeDegree3ShowTv);
		degreeTextView[12] =(TextView) findViewById(R.id.threeDegree4ShowTv);
		
	}
	
	void convertColor()  
	{
		color[1] = SettingVarible.oneDegree1Color;
		color[2] = SettingVarible.oneDegree2Color;
		color[3] = SettingVarible.oneDegree3Color;
		color[4] = SettingVarible.oneDegree4Color; 
		
		color[5] = SettingVarible.twoDegree1color;
		color[6] = SettingVarible.twoDegree2color;
		color[7] = SettingVarible.twoDegree3color;
		color[8] = SettingVarible.twoDegree4color;
		
		color[9] = SettingVarible.threeDegree1color;
		color[10] = SettingVarible.threeDegree2color;
		color[11] = SettingVarible.threeDegree3color;
		color[12] = SettingVarible.threeDegree4color;
	}

	private void initEventListener()
	{		
		for(int i=1;i<13;i++ )
		{
			degreeTextView[i].setOnClickListener(listener);
		}
	}

	private boolean isRunning()  //检查所有的直线 是否存在正在绘制的直线
	{
		for(int i=1;i<13;i++)
		{
			if(isLineStarted[i])
			{
				return true;
			}
		}
		 return false;		
	}

  	private  void clearLine(int linenum)  //清除linenum对应的直线和textView值
	{	 
  		degreeTextView[linenum].setText("");   
  		Message msg = new Message();
  		msg.arg2 = 1; //只删除一条曲线
  		msg.arg1 = linenum;
  	 	ChartViewThread.clearSeriesHandler.sendMessage(msg);  
  		
  		//判断改组是够已经全部清除 全部清除则将对应的发射机按钮置为未选中状态
  		int club = (linenum-1)/4; 
  		int transButtonNum = DegreeTopView.Selected[club];
  		
  		if((!isLineStarted[club*4+1])&&(!isLineStarted[club*4+2])&&(!isLineStarted[club*4+3])&&(!isLineStarted[club*4+4]))
  		{
  			 HomeActivity.getInstance().clearSelectedButton(transButtonNum,club); 
  			 ChartViewThread.isTransBtnAlarm[transButtonNum-1] = false ;
  			 ChartViewThread.resetSeries(club);
  			 
  		}
	}  
	//开始或停止画第transmiter 第linenum条线
	private void  DrawLine(int transmiter ,int linenum) 
	{	
		Log.e("linenum",linenum+"") ;
		if((transmiter>0))  //发射机按钮被点击才可以开始划线
		{				
			 if( DegreeTopView.isRun&&(!isLineStarted[linenum])) // 重新开始绘制linum曲线
			 {	
				 LineColors[linenum] =color[linenum];				     
				 isLineStarted[linenum] = true ;
				 degreeTextView[linenum].setText(horLine+horLine);
			 }
			 else if((DegreeTopView.isRun)&&isLineStarted[linenum]&&(LineColors[linenum] < 0)) //已经开始划linenum线 ，再次点击取消
			 {
				 LineColors[linenum] = 0;	
				 isLineStarted[linenum] = false ;
				 //清除对应的直线和textView值
				 clearLine(linenum); 					 
			  }
		}	
	}
	
	private OnClickListener listener = new OnClickListener() {		
		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {			
			//one 	
			case R.id.oneDegree1ShowTv:
				DrawLine(DegreeTopView.Selected[0],1);
				break;		
				
			case R.id.oneDegree2ShowTv:		
				DrawLine(DegreeTopView.Selected[0],2);
				break;
				
			case R.id.oneDegree3ShowTv:	
				DrawLine(DegreeTopView.Selected[0],3);
				break;	
				
			case R.id.oneDegree4ShowTv:  	
				DrawLine(DegreeTopView.Selected[0],4);
				break;				
			//two
			case R.id.twoDegree1ShowTv:		
				DrawLine(DegreeTopView.Selected[1],5);							
				break;
				
			case R.id.twoDegree2ShowTv:	
				DrawLine(DegreeTopView.Selected[1],6);
				break;
				
			case R.id.twoDegree3ShowTv:
				DrawLine(DegreeTopView.Selected[1],7);
				break;
				
			case R.id.twoDegree4ShowTv:
				DrawLine(DegreeTopView.Selected[1],8);
				break;
				
			//three
			case R.id.threeDegree1ShowTv:		
				DrawLine(DegreeTopView.Selected[2],9);
				break;
				
			case R.id.threeDegree2ShowTv:
				DrawLine(DegreeTopView.Selected[2],10);
				break;
				
			case R.id.threeDegree3ShowTv:
				DrawLine(DegreeTopView.Selected[2],11);	
				break;
				
			case R.id.threeDegree4ShowTv:		
				DrawLine(DegreeTopView.Selected[2],12);
				break; 
				
			default:
				break;
			}
		}
	};
	
	
	public static void setLineColor(int clubNum,boolean Isclear) 
	{
		if(!Isclear)
		{
			for(int i=1;i<=4;i++ )
			{
				 LineColors[clubNum*4+i] = color[clubNum*4+i];
				 isLineStarted[clubNum*4+i] = true;
			}
		}
		else 
		{
			for(int i=1;i<=4;i++ )
			{
				 LineColors[clubNum*4+i] = 0;
				 isLineStarted[clubNum*4+i] = false ;
			}
		}		
	}
	public void lockRightScreen(boolean islock)
	{
		for(int i=1;i<13;i++)
		{
			degreeTextView[i].setEnabled(islock);
		}
	}
	public void initTextView(int transBtn,int club ) 
	{
		for(int i=1;i<=4;i++)
		{
			 degreeTextView[club*4+i].setText(transBtn+"-0");
			 degreeTextView[club*4+i].setTextColor(Color.BLACK);
		}
	}
	public void clearDegreeTvs()
	{
		for(int i=1;i<13;i++)
		{
			degreeTextView[i].setText("");
		}
	}
}
