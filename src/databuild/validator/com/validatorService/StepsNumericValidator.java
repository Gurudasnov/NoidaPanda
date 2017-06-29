package com.npo.validatorService;

import java.util.StringTokenizer;

public class StepsNumericValidator implements IRangeValidator{
	
	static int min;
	static int max;
	static int step;
	
	@Override
   public boolean isValid(String rangeSchema, Object obj) {
	   boolean flag=false;
	   Number number = (Number) obj;
	   long value = (long) number.longValue();
	 StringTokenizer st = new StringTokenizer(rangeSchema,", ");  
     while (st.hasMoreTokens()) {  
         String str= st.nextToken();
        if(str.contains(".")){
        	max = Integer.parseInt(str.substring(str.lastIndexOf(".")+1));
        	min = Integer.parseInt(str.substring(0,str.indexOf(".")));
        }
        else{
        	if(!str.contains("step")){
        		step=Integer.parseInt(str);
        }
     }  
     }
	
	if(value >= min && value <= max){
		long x=value%step;
		if(x == 0){
			flag=true;
		}else{
			flag=false;
		}
		
	}
		return flag;
	}
	
public static void main(String[] args) {
	StepsNumericValidator snv = new StepsNumericValidator();
	snv.isValid("",1);
}

}
