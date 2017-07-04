package com.Xls2Json.model;

import java.util.ArrayList;
import java.util.List;

public class AttributeValues {
	private String attribute_name;
	private Object value;
	@Override
	public String toString() {
		return "AttributeValues [attribute_name=" + attribute_name + ", value=" + value + "]";
	}
		public String getAttribute_name() {
		return attribute_name;
	}
	public void setAttribute_name(String attribute_name) {
		this.attribute_name = attribute_name;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
}
