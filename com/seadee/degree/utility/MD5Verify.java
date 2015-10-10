package com.seadee.degree.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Verify {
	static char hexDigits[]   = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c','d', 'e', 'f'}; 
	public static MessageDigest md = null; 
	
	static {
		try{
			md = MessageDigest.getInstance("MD5");
		}
		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
	}
	
	public static String getFileMD5String(File file) throws IOException { 
		if(!file.exists())
			throw new IOException();
        InputStream fis;  
        fis = new FileInputStream(file);  
        byte[] buffer = new byte[1024];  
        int numRead = 0;  
        while ((numRead = fis.read(buffer)) > 0) {  
            md.update(buffer, 0, numRead);  
         }  
        fis.close();  
        return bufferToHex(md.digest());  
    } 
	
	private static String bufferToHex(byte bytes[]) {  
        return bufferToHex(bytes, 0, bytes.length);  
    }  
	
	 private static String bufferToHex(byte bytes[], int m, int n) {  
	        StringBuffer stringbuffer = new StringBuffer(2 * n);  
	        int k = m + n;  
	        for (int l = m; l < k; l++) {  
	            appendHexPair(bytes[l], stringbuffer);  
	        }  
	        return stringbuffer.toString();  
	} 
	 
	 private static void appendHexPair(byte bt, StringBuffer stringbuffer) {  
	        char c0 = hexDigits[(bt & 0xf0) >> 4];
	        // 取字节中�?4 位的数字转换, >>> 为�?辑右移，将符号位�?��右移,此处未发现两种符号有何不�?  
	        char c1 = hexDigits[bt & 0xf];
	        // 取字节中�?4 位的数字转换   
	        stringbuffer.append(c0);  
	        stringbuffer.append(c1);  
	}  
	 
	 public static String getMD5String(byte[] bytes) {
		   md.update(bytes);
		   return bufferToHex(md.digest());
	}
	 
	 public static String getMD5String(String s) {
		   return getMD5String(s.getBytes());
	}
	 
	 public static boolean checkPassword(String password, String md5PwdStr) {
		   String s = getMD5String(password);
		   return s.equals(md5PwdStr);
	}
}
