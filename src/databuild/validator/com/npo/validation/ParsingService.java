package com.npo.validation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ParsingService {

	
	public static Object parseData(String fileName,Class cls){
		  
		 Object obj  = null;
		
	       try {
	    	   BufferedReader bufferedReader = new BufferedReader(
	    		         new FileReader(fileName)); 
	           GsonBuilder builder = new GsonBuilder(); 
	           Gson gson = builder.create();
	           //Gson gson = new GsonBuilder().create();
	   obj  = gson.fromJson(bufferedReader, cls);
	           
	       }catch (IOException e) {
	           e.printStackTrace();
	       }
		   
		   return obj;
	}
	
	
	/*public static void main(String[] args) {
		
		Udata ud =  (Udata) parseData("schema.json",Udata.class);
		System.out.println(ud.getMos().get(0).getMO_name());
		
	}*/
	
}
