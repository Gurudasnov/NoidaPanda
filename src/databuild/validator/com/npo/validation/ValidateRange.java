package com.npo.validation;

import com.Validator.Models.Sdata;
import com.Validator.Models.Udata;
import com.npo.validatorService.RangeChooser;

public class ValidateRange {
	


public static boolean validateUserDataByRange(Udata userData, Sdata schemaData) {
	
	Boolean flag = true;
	String v_type = null;
	int length_UMos = userData.getMos().size();
	int length_UAttributes = 3;
	int length_SMos =schemaData.getMOs().size();
	int length_SAttributes = 3;
	
	if (!(length_UMos == length_SMos && length_UAttributes == length_SAttributes)) {
		flag =  false;
	}
	for (int i = 0, j = 0; i < length_UMos && j < length_SMos && flag == true; i++, j++) {
		for (int k = 0, l = 0; k < length_UAttributes && l < length_SAttributes; k++, l++) {
			String o2 = schemaData.getMOs().get(j).getAttributes().get(l).getRange();
			Object o1 = userData.getMos().get(i).getAttributes().get(k).getValues();
		
		flag = RangeChooser.getResult(o2, o1);
			if(flag == false){
				break;
			}
		}}
	
	return flag;
}

public static void main(String[] args) {
	    Udata userData = (Udata) ParsingService.parseData("New-data.json", Udata.class);
		Sdata schemaData = (Sdata) ParsingService.parseData("schema.json", Sdata.class);
		Boolean b = ValidateRange.validateUserDataByRange(userData, schemaData);
		System.out.println(b);
}

}
