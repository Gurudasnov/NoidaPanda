package com.npo.validatorService;

import java.util.StringTokenizer;

public  class CharsValidator implements IRangeValidator {
	
	
	@Override
	public boolean isValid(String rangeSchema, Object obj) {
		//  System.out.println("CharsValidator - --- isValid()");
		  boolean flag = false;
		  int min;
		  int max;
		  String value = (String) obj;
		  int lengthValue = value.length();
		  min= Integer.parseInt(rangeSchema.substring(0,rangeSchema.indexOf(".")));
		  String str3 = rangeSchema.substring(rangeSchema.lastIndexOf(".")+1);
		  String[] str4 = str3.split(" ");
		  max = Integer.parseInt(str4[0]);
		           if(lengthValue>=min && lengthValue <=max){
			              flag = true;
		                  }else{
			              flag = false;
		                  }
                 return flag;		  
	}
	
	
	
	/*public static void main(String[] args) {
		CharsValidator cv = new CharsValidator();
		
		cv.isValid("0...255 chars", "c");
	}*/

}
