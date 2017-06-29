package com.Validator.Models;


import java.util.ArrayList;
import java.util.List;


public class UMOs {
	private String MO_name;
	private List<DataAttribute> attributes;
	public String getMO_name() {
		return MO_name;
	}
	public void setMO_name(String mO_name) {
		this.MO_name = mO_name;
	}
	public List<DataAttribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<DataAttribute> attributes) {
		this.attributes = attributes;
	}



}
