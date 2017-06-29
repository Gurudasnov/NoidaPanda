package com.npo.validation;

import com.Validator.Models.Sdata;
import com.Validator.Models.Udata;
import com.npo.validation.ParsingService;

public class ValidationService {

//	/* static */ Udata userData = (Udata) ParsingService.parseData("New-data.json", Udata.class);
//	/* static */ Sdata schemaData = (Sdata) ParsingService.parseData("schema.json", Sdata.class);

	/**
	 * Validation Login: 1. No. of MOs in schema should be same as that of user
	 * data 2. Data type of the attribute should match with user data
	 * 
	 * @return b
	 */
	public static boolean validateType(Udata userData, Sdata schemaData) {
		Boolean flag = true;
		String v_type = null;
		int length_UMos = userData.getMos().size();
		int length_UAttributes = 3;
		int length_SMos =schemaData.getMOs().size();
		int length_SAttributes = 3;
		//
		// No. of MOs in schema should be same as that of user data
		if (!(length_UMos == length_SMos && length_UAttributes == length_SAttributes)) {
			flag =  false;
			// return the reason for validation error
		}
		for (int i = 0, j = 0; i < length_UMos && j < length_SMos && flag == true; i++, j++) {
			for (int k = 0, l = 0; k < length_UAttributes && l < length_SAttributes; k++, l++) {
				String o2 = schemaData.getMOs().get(j).getAttributes().get(l).getData_type();
				String o1 = userData.getMos().get(i).getAttributes().get(k).getValues().getClass().getSimpleName();
				
						flag= validate1(o1,o2);
						if(flag == false){
							break;
						}
				}
			}
		
		return flag;
	}
	public static boolean validate1(String str1,String str2) {
		
		System.out.println(str1 +str2);
		if(str1.equals("Double")){
			str1="Number";
			if(str1.equals(str2)){
				return true;
			}
		}else{
			if(str1.equals(str2)){
				return true;
			}else {
				return false;
			}
		}
		return false;
		}
	
	
	
	

	public static void main(String[] args) {

//		ValidationService v = new ValidationService();
//		boolean b = v.validateType();
		Udata userData = (Udata) ParsingService.parseData("New-data.json", Udata.class);
		Sdata schemaData = (Sdata) ParsingService.parseData("schema.json", Sdata.class);
		Boolean b = ValidationService.validateType(userData, schemaData);
		 System.out.println(b);

	}
}