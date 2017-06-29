package com.npo.validatorService;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class EqualsValueValidator implements IRangeValidator{

	
	
	@Override
	public boolean isValid(String rangeSchema, Object value) {
		List list = new ArrayList();

System.out.println(rangeSchema);
		boolean flag = false;
StringTokenizer st = new StringTokenizer(rangeSchema," ");  
while (st.hasMoreTokens()) {  
    String str= st.nextToken();
   // System.out.println(str);
    StringTokenizer st1 = new StringTokenizer(str," = "); 
    	while (st1.hasMoreTokens()) { 
    		String str1= st1.nextToken();
    	//	System.out.println(str1);
    		list.add(str1);
    		
    	}
    	// System.out.println(list);
    	
    	if(list.contains(value)){
    		flag = true;
    	}else {
			flag = false;
		}
}
		
		
		
		
		return flag;
	}
	
	/*public static void main(String[] args) {
		EqualsValueValidator eqV = new EqualsValueValidator();
		boolean bool = eqV.isValid("FXEA=472084A FXEB=472501A FXEC=472601A", "472601d");
		System.out.println(bool);
	}*/

}
