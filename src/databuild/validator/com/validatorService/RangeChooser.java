package com.npo.validatorService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RangeChooser {
	
	
	public static String getRangeValidator(String range) {
		
		String value=null;
	
	try {
		File file = new File("range.properties");
		FileInputStream fileInput = new FileInputStream(file);
		Properties properties = new Properties();
		properties.load(fileInput);
		fileInput.close();

		Enumeration<Object> enuKeys = properties.keys();
		while (enuKeys.hasMoreElements()) {
			String key = (String) enuKeys.nextElement();
			boolean bool=Pattern.compile(key).matcher(range).matches();
			if(bool){
				
				value =properties.getProperty(key);
				break;
				//System.out.println(value);
			}else {
				value=null;
			}
			//System.out.println(value);
		}
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	
	return value;
}
	
	
	
	


	public static boolean getResult(String range,Object value) {
		boolean flag=false;
		try {
			String obj = getRangeValidator(range);
			//System.out.println(obj);
			Class<?> cl  = Class.forName(obj);
			//System.out.println(range);
			//System.out.println(value);
			//System.out.println(cl.getName());
				
			
			Boolean bool = ((IRangeValidator) cl.newInstance()).isValid(range, value);
			//System.out.println(bool);
			if(bool){
				flag=true;
			}
			else {
				flag=false;
			}
		} catch (ClassNotFoundException e) {
			
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return flag;

	}
	
	/*public static void main(String[] args) {
		//boolean bool = getResult("Optimized Cooling (0), Linear (1), Low Noise (2)",1);
		//boolean bool = getResult("1...18, step 2",5);
		//boolean bool = getResult("0.5...2.5 step 0.5",122);
		//  boolean bool = getResult("0...5 chars","hsahq");
		//boolean bool = getResult("FXEA=472084A FXEB=472501A FXEB=472501A","472501A");
		//String bool = getRangeValidator("Text, 1 characters");
		boolean bool = getResult("Text, 3 characters","112");
		System.out.println(bool);
	}*/

}
