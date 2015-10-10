package com.seadee.degree.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ClosedByInterruptException;

import com.seadee.library.R;
import com.seadee.library.control.SDProgressBar;
import com.seadee.library.utils.SDAlertDialog;
import com.seadee.library.utils.SDAlertDialog.ALERTSTYLE;
import com.seadee.library.utils.SDToast;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SDDownload {
	
	/*eg:
	   DownloadInfo info = new DownloadInfo(
				 "/cache/update.zip",
				 "update",
				 "http://www.seadeeplus.com/terminal/update-20140610.zip",
				 "seadee",
				 100);
		 SDDownload down = new SDDownload(this,info,downlistener);
		 down.show();
		 private DownloadListener downlistener = new DownloadListener(){};
	 */
	
	public interface DownloadListener{
		public void downloadFinished();
		public void downloadCanceled();
		public void downloadFailed(ERROR error);
		public void downloadExited();
	}
	
	public final String TAG = "SDDownload";
	DownloadListener listener;
	
	float speed = 0f;
	int time = 0;
	long starttime = 0;
	int progress = 0;
	
	TextView mTargetTV = null;
	TextView mFromTV = null;
	TextView mSizeTV = null;
	TextView mSpeedTV = null;
	TextView mTimeTV = null;

	SDProgressBar mProgressBar = null;
	
	SDAlertDialog alertDialog = null;
	Context context  = null;
	DownloadInfo downloadinfo;
	DownloadThread downthread = null;
	
	boolean SHOWTOASTFLAG = true;
	boolean CLOSEAFTERFINISHED = false;
	
	public enum ERROR{
		ERR_NONE,
		ERR_URL,
		ERR_INFO,
		ERR_IO,
		ERR_CONN
	}
	
	public static class DownloadInfo implements Cloneable{
		String filestring;
		String showfilestring;
		String url;
		String showurlstring;
		String size;
		
		public DownloadInfo(String filestring,String showfilestring,String url,String showurlstring,String size)
		{
			this.filestring = filestring;
			if(showfilestring==null || showfilestring.isEmpty())
				this.showfilestring = filestring;
			else
				this.showfilestring = showfilestring;
			this.url = url;
			 
			if(showurlstring==null || showurlstring.isEmpty())
				this.showurlstring = url;
			else
				this.showurlstring = showurlstring;
			
			this.size = size;
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}	
	}

	public SDDownload(Context context)
	{
		init(context,null,null);
	}
	
	public SDDownload(Context context,DownloadInfo info)
	{
		init(context,info,null);
	}
	
	public SDDownload(Context context,DownloadInfo info,DownloadListener l)
	{
		init(context,info,l);
	}
	
	
	private void init(Context context,DownloadInfo info,DownloadListener l)
	{
		this.context = context;
		alertDialog = new SDAlertDialog(context);
		View view = LayoutInflater.from(context).inflate(R.layout.sd_download_dialog, null);
		mTargetTV = (TextView)view.findViewById(R.id.target);
		mFromTV = (TextView)view.findViewById(R.id.from);
		mSizeTV = (TextView)view.findViewById(R.id.size);
		mSpeedTV = (TextView)view.findViewById(R.id.speed);
		mTimeTV = (TextView)view.findViewById(R.id.time);

		mProgressBar = (SDProgressBar)view.findViewById(R.id.progress);
		alertDialog.setStyle(ALERTSTYLE.CC|ALERTSTYLE.INFO);
		alertDialog.setView(view);
		alertDialog.setTitle(context.getString(R.string.download));
		
		alertDialog.getPositiveBtn().setText(context.getString(R.string.start_download));
		if(info != null)
			this.setDownloadInfo(info);
		if(l!=null)
			this.setOnDownloadListener(l);
		alertDialog.getPositiveBtn().setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SDDownload.this.start();
				setPosBtnEnable(false);
			}
			
		});
		alertDialog.getNegativeBtn().setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SDDownload.this.cancel();
			}
			
		});
	}
	
	private void setPosBtnEnable(boolean enabled)
	{
		alertDialog.getPositiveBtn().setEnabled(enabled);
	}
	
	public void show()
	{
		alertDialog.show();
	}
	
	public void setOnDownloadListener(DownloadListener l)
	{
		this.listener = l;
	}
	
	private static final int DOWNLOADDONE = 1000;
	private static final int DOWNLOADCANCELED = 1001;
	private static final int DOWNLOADFAILED = 1002;
	private static final int DOWNLOADEXITED = 1003;
	private static final int DOWNUPDATEUI = 1004;
	
	private Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what)
			{
			case DOWNLOADDONE:
				updateTitlePercent(-3);
				mSpeedTV.setText("0kb/s");
				mTimeTV.setText("0s");
				setPosBtnEnable(true);
				showToast(false);
				if(listener!=null)listener.downloadFinished();break;
			case DOWNLOADCANCELED:
				updateTitlePercent(-2);
				mSpeedTV.setText("0kb/s");
				mTimeTV.setText("0s");
				setPosBtnEnable(true);
				showToast(true);
				if(listener!=null)listener.downloadCanceled();break;
			case DOWNLOADFAILED:
				updateTitlePercent(-1);
				mSpeedTV.setText("0kb/s");
				mTimeTV.setText("0s");
				setPosBtnEnable(true);
				if(listener!=null)listener.downloadFailed((ERROR)msg.obj);break;
			case DOWNLOADEXITED:
				mSpeedTV.setText("0kb/s");
				mTimeTV.setText("0s");
				setPosBtnEnable(true);
				if(CLOSEAFTERFINISHED)
					alertDialog.cancel();
				if(listener!=null)listener.downloadExited();break;
			case DOWNUPDATEUI:
				try{
					mProgressBar.setProgress(progress);
					updateTitlePercent(progress);
					mSpeedTV.setText((int)speed+"kb/s");
					mTimeTV.setText(time+"s");
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	private void updateTitlePercent(int percent)
	{
		String title = context.getString(R.string.download);
		switch(percent)
		{
		case -1:
			title += context.getString(R.string.failed);break;
		case -2:
			title += context.getString(R.string.cancel);break;
		case -3:
			title += context.getString(R.string.done);break;
		default:
			title += "..."+percent+"%";
		}
		alertDialog.setTitle(title);
	}

	public SDAlertDialog getAlertDialog()
	{
		return alertDialog;
	}
	
	public void setCanceledOnTouchOutside(boolean cancelable)
	{
		alertDialog.setCanceledOnTouchOutside(cancelable);
	}
	
	public void setCloseAfterDownfinished()
	{
		CLOSEAFTERFINISHED = true;
	}
	
	public void setCancelToastShow()
	{
		SHOWTOASTFLAG = false;
	}
	
	private void showToast(boolean isdownloadcanceled)
	{
		if(!SHOWTOASTFLAG)
			return;
		SDToast.makeText(context, context.getString(
				isdownloadcanceled? R.string.download_canceled:R.string.download_successfully), 
							SDToast.LENGTH_SHORT).show();
	}
	
	public TextView getControl(String tag)
	{
		if(tag.equals("target"))	
			return mTargetTV;		
		else if(tag.equals("from"))		
			return mFromTV;		
		else if(tag.equals("size"))		
			return mSizeTV;		
		else if(tag.equals("speed"))		
			return mSpeedTV;		
		else if(tag.equals("time"))		
			return mTimeTV;		
		else 		
			return mTargetTV;
	}
	
	public SDDownload setDownloadInfo(DownloadInfo downloadinfo)
	{
		if(downloadinfo==null)
			return this;
		try {
			this.downloadinfo = (DownloadInfo) downloadinfo.clone();
			this.mTargetTV.setText(downloadinfo.showfilestring);
			this.mFromTV.setText(downloadinfo.showurlstring);
			String showsize = String.valueOf(downloadinfo.size);
			this.mSizeTV.setText(showsize);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}
	
	public void start()
	{
		if(this.downloadinfo == null)
		{
			if(listener!=null)listener.downloadFailed(ERROR.ERR_INFO);
			return;
		}
		downthread = new DownloadThread(downloadinfo);
		downthread.start();
	}
	
	public void cancel()
	{
		if(downthread!=null && downthread.isAlive())
		{
			downthread.interrupt();
			downthread.cancelDownload();
		}
		alertDialog.cancel();
	}
	
	private String fileLen2Str(int l)
	{
		if(l>1024*1024*1024)
			return String.format("{0:F2}", (float)(l/1024/1024)/1024)+"GB";
		if(l>1024*1024)
			return String.format("{0:F2}", (float)(l/1024)/1024)+"MB";
		if(l>1024)
			return String.format("{0:F2}", (float)(l)/1024)+"KB";
		return l+"B";
	}
	
	
	class DownloadThread extends Thread
	{
		HttpURLConnection conn = null;
		InputStream is = null;
		FileOutputStream fos = null;
		
		String urlstring;
		String filestring;

		byte[] buf = new byte[1024];
		boolean cancel = false;
		
		int length = 0;
		
		public DownloadThread(DownloadInfo di)
		{
			this.urlstring = di.url ;
			this.filestring = di.filestring;
		}
		
		public synchronized void cancelDownload()
		{
			cancel = true;
			Log.i(TAG,"cancel downloding");
		}
		
		private static final int MAXDISCARDNUM = 4;
		int CURDISCARDNUM = 0;
		private void sendUpdateMessageDelay()
		{
			try{
				CURDISCARDNUM++;
				if(handler.hasMessages(DOWNUPDATEUI))
				{
					handler.removeMessages(DOWNUPDATEUI);
					CURDISCARDNUM ++;
				}
				else
					CURDISCARDNUM = 0;
				
				if(CURDISCARDNUM > MAXDISCARDNUM)
				{
					handler.sendEmptyMessage(DOWNUPDATEUI);
				}
				else
					handler.sendEmptyMessageDelayed(DOWNUPDATEUI, 150);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();			
			int count = 0;
			long spend = 0;
			
			URL url;
			File file;
			try {
				url = new URL(urlstring);
				conn = (HttpURLConnection) url.openConnection();
				conn.connect();
				length = conn.getContentLength();
				mSizeTV.post(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(mSizeTV.getText().toString().isEmpty())
						{
							mSizeTV.setText(fileLen2Str(length));
						}
					}
					
				});
				is = conn.getInputStream();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				handler.obtainMessage(DOWNLOADFAILED, ERROR.ERR_URL).sendToTarget();
				e.printStackTrace();
				clear();
				return ;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				handler.obtainMessage(DOWNLOADFAILED, ERROR.ERR_CONN).sendToTarget();
				e.printStackTrace();
				clear();
				return;
			}
			
			file = new File(filestring);
			if(file.exists())
				file.delete();
			
			try {
				fos = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				handler.obtainMessage(DOWNLOADFAILED, ERROR.ERR_IO).sendToTarget();
				e.printStackTrace();
				clear();
				return;
			}
			
			int len = 0;
			starttime = System.currentTimeMillis();
			while(!cancel){
				try{
					try{
						len = is.read(buf);
					}
					catch(ClosedByInterruptException e)
					{
						handler.sendEmptyMessage(DOWNLOADCANCELED);
						break;
					}
					if(len == -1)
					{
						handler.sendEmptyMessage(DOWNLOADDONE);
						break;
					}
					count += len;
					if(length > 0)
						progress = (int)((float)count/(float)length*100);
					
					spend = (System.currentTimeMillis() - starttime);
					if(spend > 0)
						speed = (float)count/1024/(int)spend*1000;
					if(speed > 0)
						time = (int) ((length-count)/1024/speed);
					sendUpdateMessageDelay();
					try{
						fos.write(buf, 0, len);
					}
					catch(ClosedByInterruptException e)
					{
						handler.sendEmptyMessage(DOWNLOADCANCELED);
						break;
					}
				}
				catch(IOException e)
				{
					handler.obtainMessage(DOWNLOADFAILED, ERROR.ERR_IO).sendToTarget();
					e.printStackTrace();
					break;
				}
			}
			if(count<length)
			{
				handler.sendEmptyMessage(DOWNLOADCANCELED);
			}
			clear();
			handler.sendEmptyMessage(DOWNLOADEXITED);
		}
		
		private void clear()
		{
			try{
				if(fos !=null)
					fos.close();
				if(is != null)
					is.close();
				if(conn!=null)
					conn.disconnect();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}
}
