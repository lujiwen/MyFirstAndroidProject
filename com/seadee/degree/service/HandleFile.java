package com.seadee.degree.service;

import gov.nist.javax.sip.header.RecordRoute;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.apache.http.util.EncodingUtils;

import com.seadee.degree.R;
import android.R.integer;
import android.R.string;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.net.ParseException;
import android.os.Environment;
import android.util.Log;
import android.view.ViewDebug.ExportedProperty;
import android.widget.Checkable;
import android.widget.Toast;

import com.seadee.degree.activity.HomeActivity;
import com.seadee.degree.service.LibDegree.DegreeAndTime;



public class HandleFile {
	private static final String tag = "HandleFile" ;
	private static FileOutputStream fout;
		
	public static File getRootDirectoryFile()
	{
		String root=Environment.getExternalStorageDirectory().getPath()+"/sd_degree/";
		return new File(root);
	}
	
	/**
	 * called when create a new file everyday ,if the idle space is less than 1024*1024 ,then delete 
	 * the flie from now longest 
	 * */
	public static void  checkFreeSpace()
	{
		String root=Environment.getExternalStorageDirectory().getPath()+"/sd_degree/";
		File file = new File(root);
		Log.e("path", file.getAbsolutePath().toString());
		Log.e("/sd_degree/：freespace", file.getFreeSpace()+"");
		File[] childFiles =  file.listFiles();
		while(file.getFreeSpace()<1024*1024)
		{
			childFiles[0].delete();
			childFiles = file.listFiles();
		}
	}
	public static DegreeAndTime[] getDataBlock(String storage,long offset,int screenPointNum) throws IOException
	{
		 DegreeAndTime dgrAndTm[]  = new DegreeAndTime[screenPointNum];
		 RandomAccessFile rafFile = new RandomAccessFile(storage,"r");
	/*	 FileInputStream fin = new FileInputStream(storage);*/
		 rafFile.seek(offset);

		 String buf = "";
		 for(int i=0;i<screenPointNum;i++)
		 {			
			 dgrAndTm[i] = new DegreeAndTime();	
			 if((buf = rafFile.readLine()) != null)
			 {
				 if((buf)!= null)   
				 {
					 dgrAndTm[i] = parseData(buf); 
				 }
			 }	  		 			 
		 }		 
		 
		 for(int i=0;i<screenPointNum;i++)
		 {			
			 dgrAndTm[i] = new DegreeAndTime();	
			 if((buf = rafFile.readLine()) != null)
			 {
				 if((buf)!= null)   
				 {
					 dgrAndTm[i] = parseData(buf); 
				 }
			 }	  		 
		 }
		 rafFile.close();
		 return dgrAndTm;
	}
	/**
	 * read a line of the datas from file 
	 * @param storage the path of the file 
	 * @param offset the r/w  postion of the file 
	 * */
	 public static  DegreeAndTime  getStoredData(String storage,long offset) throws IOException
	 {
		 DegreeAndTime dgrAndTm  = new DegreeAndTime();		 
		 RandomAccessFile rafFile = new RandomAccessFile(storage,"r");
		 FileInputStream fin = new FileInputStream(storage);
		 String buf = "";
		 rafFile.seek(offset);
 
		 if((buf = rafFile.readLine()) != null)
		 {
			 if((buf)!= null)   
			 {
				 dgrAndTm = parseData(buf); 
			 }
		 }	  		 
		 rafFile.close();
		 return dgrAndTm;		 
	 }

	 /**Parse the line of data read from dataFile  into 30 Degrees and time  
	  *@param storedData a line of data read form file  
	  **/ 
	 private static DegreeAndTime parseData(String storedData)
	 {
		 DegreeAndTime  data =new DegreeAndTime();
		 String[] temp =  new String[32];
		 temp  = storedData.split(","); 
		 try {
			 data.time = Long.parseLong(temp[0]);
			 for(int i=1;i<32;i++)
			 {
				data.degree[i-1] = Integer.parseInt(temp[i]);
			 } 
		} catch (Exception e) {
			 e.printStackTrace();
		}		 
		 return data ;
	 }
	 
	 public static int  getFileSize(String fileName)
	 {
		File  file = new  File(fileName);		  	  
		if(file.exists())
	  	 {
			return (int)file.length(); 
	  	 }	
		else 
		 {
			return 0;
		 }		
	 } 
	 
		/**
		 * export file to external device 
		 * @param downDate the date of the historyDataFile be choosed
		 * @param externalPath exprot to destination
		 * @throws IOException 
		 **/		
		public static boolean ExportFile(java.util.Date downDate,String externalPath ,Context context) throws IOException
		{	     
			 File Dir = getFileNameByDate(downDate); 
			 String date = new SimpleDateFormat ("yyyyMMdd").format(downDate);
		  	 if(!Dir.exists())
		      {
		          return false ;
		      }			  	 
		  	 File file = new File(Dir.toString()+"/"+date+".txt");
		  	 File newFile = new  File(externalPath+"/"+date+".txt") ;
		  	 Log.e("NewFile",newFile.getAbsolutePath().toString()) ;		  	 
		  	 
		  	 if(!newFile.exists())
		  	 { 		  		 
		  		 Log.e("externalCanWrite", newFile.canWrite()+"") ;
		  		 newFile.createNewFile(); 
		  		 if(copyFile(file, newFile) >0)
		  			 return true ;		  
		  	 }
		  	 else 
		  	 {
		  		 Toast.makeText(context,R.string.fileExisted,Toast.LENGTH_SHORT).show() ;		  		  
		  		 return false ;
		  	 }
			 return false ;			 
		}
		
		
	private static long copyFile(File src,File dest) 
	{
		 long copySize = 0 ;
		 try {
			FileChannel channelIn  = new FileInputStream(src).getChannel();
			FileChannel channelout = new FileOutputStream(dest).getChannel();
			try {
				long size = channelIn.size() ;
				channelIn.transferTo(0, size, channelout);
				copySize =  channelout.size();
				channelIn.close();
				channelout.close();
				return copySize ;
			} catch (IOException e) {
				e.printStackTrace();
			}	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		 
		 return copySize ;
	}
	
	
 	public static  void wirteIntTofile(String count,int cnt,boolean isNewLine)
 	{
 		
 		String  path = Environment.getExternalStorageDirectory().getPath()+"/logCnt.txt";
 		File file = new File(path);
 		if(!file.exists())
 		{
 			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 		}
 		try {
			FileOutputStream fOutputStream = new FileOutputStream(file,true);
	 		String cntString = count+":"+new String().valueOf(cnt)+",";
	 		
	 		if(isNewLine)
	 		{
	 			cntString += "\r\n";
	 		}
	 		byte[] tempBytes = new byte[20];
			tempBytes = cntString.getBytes();
			   			
			try {
				fOutputStream.write(tempBytes);
				fOutputStream.close(); 	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  //windows 锟斤拷锟斤拷
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

 	}
 	
	 /**
	  * store a line of data include time and 30 degrees into file
	  * @param tm current system time 
	  * @param degree 30 degrees  
	  * */ 
	private static String dateString ;
	 public static  void  StoreData(long tm,int[] degree) throws IOException
	{ 	 		 
		 String date="";
		 String time="";
		 if(tm!=0)
		 {
		 	 date = new SimpleDateFormat ("yyyyMMdd").format(new Date(tm));		 	
		 	 time  = new SimpleDateFormat ("HHmmss").format(new  Date(tm));
			 if(dateString != date)
			 {
				 dateString = date;
			 }			 
		 }
		 else  //the pause flag 
		 {
			 date = dateString ;
			 time = new String("000000");
		 }

			 String tempString = "" ;
			 byte[]  bytes = new byte[150]; 
			 byte[] tempBytes = new byte[10];
			 int countBytes =0;			  
		     String storage_path=Environment.getExternalStorageDirectory().getPath()+"/sd_degree/"+date;
		     File dir=new File(storage_path);
		     
		  	 if(!dir.exists())  
		      {
		          dir.mkdirs();
		      }
		  	  File file = new  File(dir.toString()+"/"+date+".txt");		  
		  	  if(!file.exists())
		  	  {
			  		try 
			  		{
			  			checkFreeSpace(); //检测外存剩余控件， 空间小于1024则删除距现在最久的历史文件
			  			file.createNewFile();					
					} catch (IOException e) 
					{
						e.printStackTrace();
					} 	  					  	
		  	  }		  
			   fout = new FileOutputStream(file.getAbsolutePath(),true);  // append = true	
			  
			   tempBytes = time.getBytes();
			   for(int j=0;j<tempBytes.length;j++)
			   {
				   bytes[countBytes++] = tempBytes[j] ;   
			   }
			   
			   tempBytes = tempString.valueOf(",").getBytes();			   
			   for(int j=0;j<tempBytes.length;j++)
			   {
				   bytes[countBytes++] = tempBytes[j] ;   
			   }
			   for(int i=0 ;i<=30;i++ )
			   {
				  /* tempBytes  = tempString.valueOf(getRandom()).getBytes();*/
				   tempBytes  = tempString.valueOf(degree[i]).getBytes();
				   for(int j=0;j<tempBytes.length;j++)
				   {
					   bytes[countBytes++] = tempBytes[j] ;   
				   }
			  	   if(i!=30)
			  	   {
			  		   tempBytes  =  tempString.valueOf(",").getBytes();	
					   for(int j=0;j<tempBytes.length;j++)
					   {
						   bytes[countBytes++] = tempBytes[j];   
					   }
			  		}	    	    
				} // end for 
			   
	  		   tempBytes  =  tempString.valueOf(",").getBytes();	
			   for(int j=0;j<tempBytes.length;j++)
			   {
				   bytes[countBytes++] = tempBytes[j];   
			   }
			     tempBytes = tempString.valueOf("\r\n").getBytes();
			      
				 for(int i=0;i<tempBytes.length;i++)
				 {
				    bytes[148+i] = tempBytes[i] ;   
				 } 				 
			    fout.write(bytes);  //windows 锟斤拷锟斤拷
			    fout.close(); 	
			}

		public static boolean deleteFile(java.util.Date downDate)
		{			    
			 File dir =getFileNameByDate(downDate);
		  	 if(!dir.exists())
		      {
		          return false ;
		      }
		  	 if(dir.isDirectory())  // 锟侥硷拷锟斤拷锟斤拷锟斤拷锟侥硷拷锟斤拷锟斤拷直锟斤拷删锟斤拷
		  	 {
		  		File[] childfFiles = dir.listFiles();
		  		if(childfFiles.length>0)	
		  		{
		  			for(int i=0 ;i<childfFiles.length;i++)
		  			{
		  				childfFiles[i].delete();
		  			}
		  		}
		  	 }
			 return dir.delete();
		}
	
		/**
		 * Check if the file of the downDate exists
		 * @param downDate the date user clicked;  
		 * @return
		 * */
		public static  boolean	checkFile(java.util.Date date) 
		{	     
		  	 if(!getFileNameByDate(date).exists())
		  	 {
		  		 return false;
		  	 }	
		  	 else 
		  	 {
		  		 return true;
			 }		  	
		}	
 		private static File getFileNameByDate(java.util.Date downDate)
		{
			 String date = new SimpleDateFormat ("yyyyMMdd").format(downDate);		
			 return  new File( Environment.getExternalStorageDirectory().getPath()+"/sd_degree/"+date);		     		 
		}
		public static boolean isFileExist(int dateNum)
		{
			File dir = new File( Environment.getExternalStorageDirectory().getPath()+"/sd_degree/"+Integer.valueOf(dateNum).toString()); 
			return  dir.exists();			
		}
 			
		public static String readFile(String fileName,Context context)  
		{
			String resString ="";
			try {	
				 InputStream inputStream = HandleFile.getAssertStream(fileName, context)  ;
				byte b[] = new byte[inputStream.available()] ;
				inputStream.read(b);
				resString = EncodingUtils.getString(b, "GBK");	 
				inputStream.close();
 
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 	resString ;
		}		
		
		/** 
		 * store int data into xml file 
		 * @param context 
		 * @param filename the name of the XML file 
		 * @param keyStr the key of the data
		 * @param value the int value 
		 */
		public static void record(Context context,String filename,String keyStr ,int value)
		{
			 SharedPreferences sharedPreferences = context.getSharedPreferences(filename,context.MODE_PRIVATE);
		     Editor editor = sharedPreferences.edit();   
		     editor.putInt(keyStr, value);
		     editor.commit();	    
		}	
		
		/** 
		 * store string data into xml file 
		 * @param context 
		 * @param filename the name of the XML file 
		 * @param keyStr the key of the data
		 * @param value the string value 
		 */
		public static void record(Context context,String filename,String keyStr ,String value)
		{
			 SharedPreferences sharedPreferences = context.getSharedPreferences(filename,context.MODE_PRIVATE);
		     Editor editor = sharedPreferences.edit();   
		     editor.putString(keyStr, value);
		     editor.commit();	    
		}	
		
	 	 /**
	 	  * get int data from XML 
	 	  * @param context
	 	  * @param keyStr
	 	  * @param defValue if  the data is never stored before, then return it   
	 	  */
	 	public static int getRecord(Context context,String filename,String keyStr ,int defValue  )
		{
			 SharedPreferences sharedPreferences = context.getSharedPreferences(filename, context.MODE_PRIVATE);
			 return  sharedPreferences.getInt(keyStr , defValue);	     
		}	
	 	 /**
	 	  * get string data from XML 
	 	  * @param context
	 	  * @param keyStr
	 	  * @param defValue if  the data is never stored before, then return it   
	 	  */
	 	public static String getRecord(Context context,String filename,String keyStr ,String defValue  )
		{
			 SharedPreferences sharedPreferences = context.getSharedPreferences(filename, context.MODE_PRIVATE);
			 String s =  sharedPreferences.getString(keyStr , defValue);
			 Log.e("getRecord",  s);
			 return  sharedPreferences.getString(keyStr , defValue);	     
		}
	 	
		/**
		 * read file from assert folder
		 * 
		 * */
		public static InputStream getAssertStream(String Filename,Context context)
		{
			AssetManager assetManager =  context.getAssets();
			 InputStream stream = null ;
			try {
				stream  = assetManager.open(Filename);
				/*return   stream  ;*/
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
				return stream;	
		}
		
	 
}
