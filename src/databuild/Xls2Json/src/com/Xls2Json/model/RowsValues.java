package com.Xls2Json.model;

import java.util.ArrayList;
import java.util.List;

public class RowsValues {
	private ArrayList<AttributeValues> attributes_val;

	@Override
	public String toString() {
		return "RowsValues [attributes_val=" + attributes_val + "]";
	}

	public ArrayList<AttributeValues> getAttributes_val() {
		return attributes_val;
	}

	public void setAttributes_val(ArrayList<AttributeValues> attributes_val) {
		this.attributes_val = attributes_val;
	}
}
