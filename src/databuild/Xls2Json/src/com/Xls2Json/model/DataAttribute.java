package com.Xls2Json.model;


public class DataAttribute {
@Override
public String toString() {
	return "DataAttribute [attribute_name=" + attribute_name + ", range=" + range + ", default_val=" + default_val
			+ ", mandatory=" + mandatory + ", attType=" + attType + ", attValue=" + attValue + "]";
}
private String attribute_name;
private Object range;
private Object default_val;
private Object mandatory;
private String attType;
private Object attValue;

public Object getAttValue() {
	return attValue;
}
public void setAttValue(Object attValue) {
	this.attValue = attValue;
}
public String getAttType() {
	return attType;
}
public void setAttType(String attType) {
	this.attType = attType;
}
public Object getMandatory() {
	return mandatory;
}
public Object getDefault_val() {
	return default_val;
}
public void setDefault_val(Object default_val) {
	this.default_val = default_val;
}
public void setMandatory(Object mandatory) {
	this.mandatory = mandatory;
}
public String getAttribute_name() {
	return attribute_name;
}
public void setAttribute_name(String attribute_name) {
	this.attribute_name = attribute_name;
}
public Object getValues() {
	return range;
}
public void setValues(Object values) {
	this.range = values;
}

}
