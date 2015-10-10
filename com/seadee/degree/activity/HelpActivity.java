package com.seadee.degree.activity;

import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.seadee.degree.R;
import com.seadee.library.activity.SDActivity;
import com.seadee.library.activity.SDMenuBar;
import com.seadee.library.activity.SDStatusBar;
import com.seadee.library.activity.SDToolBar;

public class HelpActivity extends  SDActivity   
{
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_info_layout);
		setTitle(R.string.help_info);
		setSize(1000, LayoutParams.MATCH_PARENT);
		loadHtml();
	}

	private void loadHtml()
	{
		Log.e("webview","load1");
		 try {
			 WebView heipIndfoWebView = (WebView)findViewById(R.id.help_info);
			 heipIndfoWebView.getSettings().setBuiltInZoomControls(true);
			 heipIndfoWebView.loadUrl("file:///android_asset/help_info.html");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
 

 	@Override
	public void onCreateMenuBar(SDMenuBar menubar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parseMenu(int menuindex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCreateToolBar(SDToolBar toolbar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCreateStatusBar(SDStatusBar statusbar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ActivityParams specialActivity() {
		// TODO Auto-generated method stub
		return null;
	}
 
	
}
