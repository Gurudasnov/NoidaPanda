package com.npo.validatorService;

import java.util.StringTokenizer;

public  class ANTValidator implements IRangeValidator{
	static int min;
	static int max;
	static int step;
	public boolean isValid(String rangeSchema, Object obj) {
		
		boolean flag = false;
		
		max = Integer.parseInt(rangeSchema.substring(rangeSchema.lastIndexOf("(")+1,rangeSchema.lastIndexOf(")")));
		min = Integer.parseInt(rangeSchema.substring(rangeSchema.indexOf("(")+1,rangeSchema.indexOf(")")));
		//System.out.println(min);
		//System.out.println(max);
		
		 Number number = (Number) obj;
		   long value = (long) number.longValue();
		
		if (value>=min && value <=max) {
			flag = true;
		}else{
			flag = false;
		}
		 return flag;
	}
	
	/*public static void main(String[] args) {
		ANTValidator antV = new ANTValidator();
		boolean bool = antV.isValid("ANT1 (0), ANT2 (1), ANT3 (2), ANT4 (3), ANT5 (4), ANT6 (5)", -1);
		//System.out.println(bool);
	}*/

}
