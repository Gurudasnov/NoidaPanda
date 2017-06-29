package com.npo.validatorService;

public class StepsDecimalValidator implements IRangeValidator{

	@Override
	public boolean isValid(String rangeSchema, Object value) {
		System.out.println("StepsDecimalValidator --- isValid()");
		 return false;
	}

}
