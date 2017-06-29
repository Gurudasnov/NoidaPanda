package com.Validator.Models;


import java.util.ArrayList;
import java.util.List;


public class SMOs {
	private String MO_name;
	private List<SDataAttribute> attributes;
	public String getMO_name() {
		return MO_name;
	}
	public void setMO_name(String mO_name) {
		this.MO_name = mO_name;
	}
	public List<SDataAttribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<SDataAttribute> attributes) {
		this.attributes = attributes;
	}



}
