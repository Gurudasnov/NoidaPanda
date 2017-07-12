package com.includes.Model;


import java.util.ArrayList;
import java.util.List;


public class SchemaMOs {
	private String MO_name;
	private List<SchemaDataAttribute> attributes;
	public String getMO_name() {
		return MO_name;
	}
	public void setMO_name(String mO_name) {
		this.MO_name = mO_name;
	}
	public List<SchemaDataAttribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<SchemaDataAttribute> attributes) {
		this.attributes = attributes;
	}



}
