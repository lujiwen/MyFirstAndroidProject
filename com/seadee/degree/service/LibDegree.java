package com.seadee.degree.service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import android.util.Log;
 

public class LibDegree {	
	public static native void setSomeFlag(int mode);
	public static native int[] getDegree();
	public native static void dataWhilehandle(int opendev); 
	public native static int MatchAddress(int pairNum);
	public native static void setSendStatus(int status);
	public native static int[] GetVoltage();//获取一组电池的电量
	public native static void send485Settings(int[] setttings,int len); //传送485设置 
	public native static int FlagStatusStartEnd(int select);  
	
	static int i=0;
	static double angle;
	private static int degree[]=new int[5];
	public static long time;
	private final static  String tag = "libDegree";
	private  HandleFile handleFile;
	
	static{
		System.loadLibrary("Degree");
	}

	public static class DegreeAndTime
	{
		public long  time;
		public  int[] degree;
		public DegreeAndTime()
		{
			this.time = 0;
			degree = new int[31];
		}
		public DegreeAndTime(int[] dgr, long time)
		{
			 degree = dgr;
			 this.time = time;
		}
		
		public void getDegreeAndTime()
		{
			 degree = getDegree();	
			 
		     long tm = System.currentTimeMillis();		
			 try {
			 	HandleFile.StoreData(tm,degree);//取得数据存入文件
			} catch (IOException e) {
				e.printStackTrace();
			}  
		}	
	}
}
