package com.seadee.degree.utility;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.http.util.EncodingUtils;

import com.seadee.library.utils.Log;
import com.seadee.degree.utility.MD5Verify;
import com.seadee.library.utils.ParseXml;
import com.seadee.library.utils.SDAlertDialog;
import com.seadee.library.utils.SDAlertDialog.ALERTSTYLE;
import com.seadee.degree.utility.SDDownload;
import com.seadee.degree.utility.SDDownload.DownloadInfo;
import com.seadee.degree.utility.SDDownload.DownloadListener;
import com.seadee.degree.utility.SDDownload.ERROR;

import android.content.Context;
import android.os.Environment;
import android.os.RecoverySystem;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.seadee.degree.R;

public class UpdateManager {
	
	public static final String TAG = "UpdateManager";
	public static String a10xmlurl = "http://www.seadeeplus.com/terminal/A10/a10update.xml";
	public static String a10debugxmlurl = "http://www.seadeeplus.com/terminal/A10/a10update-debug.xml";
	public static String a20xmlurl = "http://www.seadeeplus.com/terminal/a20update.xml";
	public static String a20debugxmlurl = "http://www.seadeeplus.com/terminal/a20update-debug.xml";
	
	HashMap<String, String> mHashMap ;
	String mVersionTag = "version";
	String mTimeTag = "time";
	String mMd5Tag = "md5";
	String mUrlTag = "url";
	String mContentTag = "content";
	String mSizeTag = "size";
	String mForceTag = "force";
	
	Context context;
	SDAlertDialog alertDialog;
	
	public UpdateInfo updateinfo = new UpdateInfo();

	public class UpdateInfo{
		public String xml = a20xmlurl;
		public int localversion;
		public int remoteversion;
		public long time;
		public String size;
		public String md5;
		public String url;
		public String content;
		public boolean isupdateexist; 
		public boolean isupdateforce = false;
		
		public UpdateInfo()
		{
 		//	if(SettingVarible.DEBUG)
		//	{
				xml = a10debugxmlurl;
		//	} 
 
		}
		
		public void print()
		{
			Log.i(TAG,"xml is "+xml);
			Log.i(TAG,"localversion is "+localversion);
			Log.i(TAG,"remote version is "+remoteversion);
			Log.i(TAG,"time is "+time);
			Log.i(TAG,"size is "+size);
			Log.i(TAG,"md5 is "+md5);
			Log.i(TAG,"url is "+url);
			Log.i(TAG,"content "+content);
			Log.i(TAG,"isupdateexist is "+isupdateexist);
			Log.i(TAG,"isupdateforce is "+isupdateforce);
		}
	}
	
	public UpdateManager(Context context)
	{
		this.context = context;
		if(renewUpdateInfo())
			updateinfo.print();
		else
			Log.i(TAG,"renew update info failed");
		initUpdateMessageDialog();
	}
	
	
	private void initUpdateMessageDialog()
	{
		alertDialog = new SDAlertDialog(context);
		alertDialog.setStyle(ALERTSTYLE.CC|ALERTSTYLE.INFO);
		alertDialog.setTitle(context.getString(R.string.update));
		alertDialog.getMessageTextView().setLines(10);
		alertDialog.getMessageTextView().setMovementMethod(ScrollingMovementMethod.getInstance());
	}
	
	public SDAlertDialog getAlertDialog()
	{
		return alertDialog;
	}
	
	public UpdateInfo getUpdateInfo()
	{
		return updateinfo;
	}

	private boolean getInfoFromHash(String tag)
	{
		String temp = "";
		temp = mHashMap.get(tag);
		if(temp == null || temp.isEmpty())
		{
			return false;
		}
		
		try{
			if(tag.equals(mVersionTag))
				updateinfo.remoteversion = Integer.valueOf(temp);
			else if(tag.equals(mTimeTag))
				updateinfo.time = Long.valueOf(temp);
			else if(tag.equals(mMd5Tag))
				updateinfo.md5 = temp;
			else if(tag.equals(mUrlTag))
				updateinfo.url = temp;
			else if(tag.equals(mContentTag))
				updateinfo.content = temp;
			else if(tag.equals(mSizeTag))
				updateinfo.size = temp;
			else if(tag.equals(mForceTag))
				updateinfo.isupdateforce = temp.equals("yes")?true:false;
			else 
				return false;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
//		Log.i(TAG,"==>  " + tag + " is "+ temp + "  <==");
		return true;
	}
	
	private int getLocalVersion()
	{
		return Integer.valueOf(android.os.Build.VERSION.INCREMENTAL);
	}
	
	public boolean renewUpdateInfo()
	{
		mHashMap = new ParseXml().getHashfromRemoteXML(a10xmlurl);
		if(mHashMap==null)
			return false;
		if(mHashMap.isEmpty())
			return false;
		if(!getInfoFromHash(mVersionTag))
			return false;
		updateinfo.localversion = getLocalVersion();
		updateinfo.isupdateexist = updateinfo.remoteversion > updateinfo.localversion;
		if(!getInfoFromHash(mTimeTag))
			return false;
		if(!getInfoFromHash(mMd5Tag))
			return false;
		if(!getInfoFromHash(mUrlTag))
			return false;
		if(!getInfoFromHash(mContentTag))
			return false;
		if(!getInfoFromHash(mSizeTag))
			return false;
		if(!getInfoFromHash(mForceTag))
			return false;
		if(updateinfo.content != null)
			updateinfo.content = EncodingUtils.getString(updateinfo.content.getBytes(), "utf-8");
		return true;
	}
	
	private void showDownloadDialog()
	{
		if(updateinfo == null)
			return;
		Log.i(TAG,"updateinfo's url is "+updateinfo.url);
		 DownloadInfo info = new DownloadInfo(
				Environment.getDownloadCacheDirectory().getAbsolutePath()+"/update.zip",			
				 context.getString(updateinfo.isupdateforce?R.string.update_target_force_hint:R.string.update_target_hint),
				 updateinfo.url,
				 ""/*context.getString(R.string.update_from_seadee)*/,
				 updateinfo.size);
		 SDDownload down = new SDDownload(context,info,downlistener);
		 down.show();
		 down.setCanceledOnTouchOutside(false);
		 down.setCloseAfterDownfinished();
		 down.getAlertDialog().getPositiveBtn().performClick();
	}

	public void update(boolean ask)
	{
		if(!updateinfo.isupdateexist)
		{
			if(ask)
			{
				alertDialog.setMessage( context.getString(R.string.update_current_newest) );
				alertDialog.getPositiveBtn().setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						alertDialog.cancel();
					}
					
				});
				alertDialog.show();
			}
		}
		else
		{
			if(ask)
			{
				alertDialog.setMessage(context.getString(R.string.update_show_message)/*.replace("%d", updateinfo.content)*/);
				alertDialog.getPositiveBtn().setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						showDownloadDialog();
						alertDialog.hide();
					}
					
				});
				alertDialog.show();
			}
			else
				showDownloadDialog();
		}
	}
	
	public boolean verifyFile(File file,String md5)
	{
		if(!file.exists())
			return false;
		try {
			if(MD5Verify.getFileMD5String(file).equals(md5))
				return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return false;
	}
	
	private DownloadListener downlistener = new DownloadListener(){

		@Override
		public void downloadFinished() {
			// TODO Auto-generated method stub 
			Log.i(TAG,"down_fisished");
			
			File file = new File(Environment.getDownloadCacheDirectory(),"update.zip");
			Log.e("verrify", "start~!");
			if(!verifyFile(file,updateinfo.md5))
			{
			//	alertDialog.getMessageTextView().append(/*"\n"+context.getString(R.string.update_file_verify_failed)*/"升级文件验证失败");
				Log.e("verrify", "failed!");
				Toast.makeText(context, context.getResources().getString(R.string.update_file_verify_failed)  ,Toast.LENGTH_SHORT).show();
				return;
			}
			alertDialog.setMessage( context.getString(R.string.update_download_success)/*+"\n"+
					context.getString(R.string.update_file_verify)*/);
			alertDialog.show();
			alertDialog.getPositiveBtn().setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					alertDialog.cancel();
					Log.e("recovery!", "start3");
				 	recoveryUpdate(context);
				}				
			});
				Log.e("verrify ok !", "recovery_update!");
				//alertDialog.getPositiveBtn().performClick();
				recoveryUpdate(context);
		 

			//alertDialog.getMessageTextView().append("升级文件验证成功"/*"\n"+context.getString(R.string.update_file_verify_success)*/);
			
			/*new Thread()
			{
				int i = 5;
				public void run()
				{
					//alertDialog.getPositiveBtn().setEnabled(false);
					//alertDialog.getNegativeBtn().setEnabled(false);
					final String msg = alertDialog.getMessageTextView().getText().toString()+"\n";
					final String extra = context.getString(R.string.update_reboot_hit) ;
					while(i>0)
					{
						alertDialog.getMessageTextView().post(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								alertDialog.setMessage(msg+extra.replace("%d", String.valueOf(i)));
							}							
						});
						i--;
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					alertDialog.cancel();
					Log.e("recovery", "start!");
					recoveryUpdate(context);
				}
			}.start();	*/		
		}

		@Override
		public void downloadCanceled() {
			// TODO Auto-generated method stub
			Log.i(TAG,"down_canceled");
		}

		@Override
		public void downloadExited() {
			// TODO Auto-generated method stub
			Log.i(TAG,"down_exist");
		}

		@Override
		public void downloadFailed(ERROR error) {
			// TODO Auto-generated method stub
			alertDialog.setMessage(context.getString(R.string.update_download_failed));
			alertDialog.show();
			alertDialog.getPositiveBtn().setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					alertDialog.cancel();
				}				
			});
		}		
	};
	
	public static void recoveryUpdate(Context context){
		File packageFile = new File(Environment.getDownloadCacheDirectory(),"update.zip");
		Log.e("recovery !", "start!2");
		try {			
		     RecoverySystem.installPackage(context, packageFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
  
}
