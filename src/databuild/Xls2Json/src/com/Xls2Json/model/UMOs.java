package com.Xls2Json.model;


import java.util.ArrayList;
import java.util.List;


public class UMOs {
	private String MO_name;
	private List<DataAttribute> attributes;
	//private List<DataAttribute> umoListAttAndVal;
	
	
	//private List<RowsValues> attributes_val;
	private String attName;
	private Object attValue;

	/*public List<DataAttribute> getUmoListAttAndVal() {
		return umoListAttAndVal;
	}
	public void setUmoListAttAndVal(List<DataAttribute> umoListAttAndVal) {
		this.umoListAttAndVal = umoListAttAndVal;
	}*/
	@Override
	public String toString() {
		return "UMOs [MO_name=" + MO_name + ", attributes=" + attributes + ", attName=" + attName + ", attValue="
				+ attValue + "]";
	}

	public String getAttName() {
		return attName;
	}
	public void setAttName(String attName) {
		this.attName = attName;
	}
	public Object getAttValue() {
		return attValue;
	}
	public void setAttValue(Object attValue) {
		this.attValue = attValue;
	}
	public String getMO_name() {
		return MO_name;
	}
	/*public List<RowsValues> getAttributes_val() {
		return attributes_val;
	}
	public void setAttributes_val(ArrayList<RowsValues> moAttVal) {
		this.attributes_val = moAttVal;
	}*/
	public void setMO_name(String mO_name) {
		this.MO_name = mO_name;
	}
	public List<DataAttribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<DataAttribute> moAttVal) {
		this.attributes = moAttVal;
	}
	/*public void setAttributes(List<DataAttribute> moNames) {
		// TODO Auto-generated method stub
		*/
	//}



}
