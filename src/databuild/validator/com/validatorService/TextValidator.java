package com.npo.validatorService;

public class TextValidator implements IRangeValidator{

	/*public static void main(String[] args) {
	
		TextValidator tV = new TextValidator();
		tV.isValid("Text, 1 characters","112");
		
		
	}*/

	@Override
	public boolean isValid(String rangeSchema, Object value) {
		
		boolean flag = false;
//System.out.println(rangeSchema);

		int rangeValue = Integer.parseInt(rangeSchema.substring(rangeSchema.indexOf(", ")+2,rangeSchema.lastIndexOf(" ")));

		String strValue = (String) value;

		if(strValue.length()>=0 && strValue.length() <=rangeValue){
			flag = true;
		}else{
			flag = false;
		}

		return flag;
	}

}
