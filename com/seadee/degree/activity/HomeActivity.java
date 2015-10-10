package com.seadee.degree.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.seadee.degree.R;
import com.seadee.degree.core.ChartViewThread;
import com.seadee.degree.core.DegreeView;
import com.seadee.degree.service.HandleFile;
import com.seadee.degree.service.HomeService;
import com.seadee.degree.service.NetworkStateReceiver;
import com.seadee.degree.service.UdiskStateReceiver;
import com.seadee.degree.utility.UpdateManager;
import com.seadee.degree.view.DegreeBottomView.OnBottomViewListener;
import com.seadee.degree.view.DegreeLeftView.OnLeftViewListener;
import com.seadee.degree.view.DegreeTopView.OnTopViewListener;
 

public class HomeActivity extends Activity implements OnTopViewListener ,OnBottomViewListener,OnLeftViewListener {
    
	Context context;    
    LayoutInflater inflater;
	FrameLayout  graphLayout; //中间绘图区
	SurfaceHolder coordHolder;
	SurfaceHolder linesHolder;
	DegreeView degreeView;	
	static HomeActivity instance = null;
	boolean[] selectedButton;
	private static final String tag = "Home"; 
	public static  int  isfirstRun; 
    ConnectivityManager connectmanager;
    HomeService homeService  ;
    boolean isBound;
    private NetworkStateReceiver networkStateReceiver = new NetworkStateReceiver();
   // private UdiskStateReceiver udiskStateReceiver = new UdiskStateReceiver();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        requestWindowFeature(Window.FEATURE_NO_TITLE); //去掉标题栏      
        instance = this;
        context=HomeActivity.this;
        setContentView(R.layout.main);         
        init();               
    }
    
    @Override 
    protected void onDestroy()
    {
     //	udiskStateReceiver.unregUdiskReceiver(context);
     	networkStateReceiver.unregNetworkReciever(context); 
    	//this.context.unbindService(mConnection);
    	isBound = false ;
    	super.onDestroy();
    }
    public static HomeActivity getInstance()
    {
    	return instance;
    }   
     private void init()
     {
    
    	// this.inflater = LayoutInflater.from(this); 
         //inflater.inflate(R.layout.main,null);  
         degreeView=(DegreeView)findViewById(R.id.degreeview);   
      //  this.inflater = LayoutInflater.from(this); 
        //inflater.inflate(R.layout.main_layout,null);  
        
/*      isBound = this.getApplicationContext().bindService(new Intent(HomeActivity.this,HomeService.class),mConnection,
    		   Context.BIND_AUTO_CREATE);
        this.startService(new Intent(HomeActivity.this,HomeService.class));    
		Log.i("isBound",  String.valueOf(isBound));*/
        
       // udiskStateReceiver.regUdiskReciever(this);
		networkStateReceiver.regNetworkReciever(this);
        if(!NetworkStateReceiver.isEthernetConnected())
        {
        	Toast.makeText(this, "无网络连接！",Toast.LENGTH_SHORT).show();
        }
        else 
        {
	        UpdateManager updateManager = new UpdateManager(this);
	        updateManager.update(true);  
        } 
     } 
     
     @Override 
     protected void onActivityResult(int requestCode, int resultCode, Intent data)
     {    	 
/*    	if(requestCode == 0)
    	{
    		Log.e("result", resultCode+"");
    	}
    	Log.e("result", resultCode+"");
    	Log.e("result", requestCode+"");*/
     }
/*    private ServiceConnection  mConnection = new ServiceConnection() 
    {		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			homeService = null ;
		}	
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stuba
			Log.e("bind","conencted!");
			homeService = ((HomeService.HomeBinder)service).getService();
			Log.e("bind",homeService.toString());
			Log.e("bind",mConnection.toString());
		}
	};*/
 	@Override
 	protected void onResume() {
 		// TODO Auto-generated method stub
 		super.onResume();
 	}
 	
     @Override
     public void onWindowFocusChanged(boolean hasFocus) {
 
 		super.onWindowFocusChanged(hasFocus);
     }
     
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    
	@Override
	public void startDraw() {
		// TODO Auto-generated method stub
		 degreeView.startDrawLine();
	}

	@Override
	public void drawLastScreen() {
		// TODO Auto-generated method stub
		degreeView.stardrawLastscreen();
	}
	
	// 左边按钮事件 
	@Override
	public void pauseDrawLine() {
		// TODO Auto-generated method stub
		degreeView.pauseDrawLine();
	}

	@Override
	public void startDrawLine() {
		// TODO Auto-generated method stub
		degreeView.startDrawLine();
	}
	public  void initBtnState()
	{
		degreeView.initBtnState();
	}
	@Override
	public void continueDrawLine() {
		// TODO Auto-generated method stub
		degreeView.continueDrawLine();
	}
	@Override
	public void startDrawHistory()
	{
		degreeView.drawHistory();
	}
	@Override
	public void reDrawhistory() {
		startDrawHistory();
	}
	public void clearSelectedButton(int transBtnNum,int clubNum)
	{
		degreeView.clearSelectedButton(transBtnNum,clubNum);
	}
	public void clearTextView(int clubNum,int transbtn)
	{
		degreeView.clearTextView(clubNum,transbtn);
	}
	
	public void BuzzerAlarm( ) {
		// TODO Auto-generated method stub
		degreeView.BuzzerAlarm();
	}

	@Override
	public void stopAlarmBuzzer()
	{
		degreeView.clearAlarmTransBtn();
	}
	
	public void initTextView(int tranBtn,int club)
	{
		degreeView.initTextView(tranBtn,club);
	}

	@Override
	public void pairFlashing() {
		// TODO Auto-generated method stub
	/*	degreeView.pairFlashing();*/
	}

	public void stopPairflashing() {
		// TODO Auto-generated method stub
		degreeView.stopPairflashing();
	}

	@Override
	public void enableStartDraw() {
		degreeView.enableStartDraw();
	}

	@Override
	public void initPairState() {
		degreeView.initPairState(HandleFile.getRecord(context,"SD_config", "pair", 0));
	}

	@Override
	public void pairFalshing(int transNum) {
		degreeView.pairFlashing(transNum);
	}

	public void updateScaleDegree(int transBtn,int[] scaleData,int club) {
		degreeView.updateScaleDegree(transBtn, scaleData, club);
	}

	@Override
	public void removeSeries(int transmiter) 
	{
		degreeView.removeSeries(transmiter);
	}

	@Override
	public void drawNextScreen()
	{
		degreeView.drawNextScreen();
	}

	@Override
	public void resetHorLables(String[] labels) 
	{
		degreeView.resetHorLabels(labels);
	}
	
	public void stopDrawHistory()
	{
		degreeView.stopDrawHistory();
	}

	@Override
	public void initScale() {
		degreeView.initScale();		
	}

 
}
