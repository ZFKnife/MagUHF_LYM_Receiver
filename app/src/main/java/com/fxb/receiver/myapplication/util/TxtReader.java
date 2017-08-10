package com.fxb.receiver.myapplication.util;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class TxtReader {
	/**
	 *
	 * @param inputStream
	 * @return
	 */
	public static String getString(InputStream inputStream) {
		InputStreamReader inputStreamReader = null;
		try {
			inputStreamReader = new InputStreamReader(inputStream, "GBK");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		StringBuffer sb = new StringBuffer("");
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	public static String getString(String filepath) {
		File file = new File(filepath);
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return getString(fileInputStream);
	}

	static void writeFileToSD(String  s) {
		    String sdStatus = Environment.getExternalStorageState();
		   if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
		        Log.d("TestFile", "SD card is not avaiable/writeable right now.");
		        return;
		    }
		    try {
		        String pathName="/sdcard/UHFLog/";
		        String fileName="initlog.txt";
		        File path = new File(pathName);
		       File file = new File(pathName + fileName);
		        if( !path.exists()) {
		            Log.d("TestFile", "Create the path:" + pathName);
		           path.mkdir();
		       }
		        if(!file.exists()) {
		           Log.d("TestFile", "Create the file:" + fileName);
		           file.createNewFile();
		       }
		        FileOutputStream stream = new FileOutputStream(file,true);
		        byte[] buf = s.getBytes();
		        stream.write(buf);
		        stream.close();

		   } catch(Exception e) {
		        Log.e("TestFile", "Error on writeFilToSD.");
		        e.printStackTrace();
		    }
		}

}

