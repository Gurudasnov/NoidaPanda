package com.includes.Model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement(namespace = "com.Model.Data")
public class DataMOs implements Serializable{

	private String MO_name;
	
	private List<DataAttribute> attributes;
	@XmlAttribute
	public String getMO_name() {
		return MO_name;
	}
	public void setMO_name(String mO_name) {
		this.MO_name = mO_name;
	}
	@XmlElement
	public List<DataAttribute> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(List<DataAttribute> attributes) {
		this.attributes = attributes;
	}



}
