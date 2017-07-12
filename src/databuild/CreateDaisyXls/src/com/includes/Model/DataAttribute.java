package com.includes.Model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = { "attribute_name", "values" })
public class DataAttribute implements Serializable {
private String attribute_name;
private Object value;

@XmlAttribute

public String getAttribute_name() {
	return attribute_name;
}
public void setAttribute_name(String attribute_name) {
	this.attribute_name = attribute_name;
}
@XmlElement
public Object getValues() {
	return value;
}
public void setValues(Object values) {
	this.value = values;
}

}
