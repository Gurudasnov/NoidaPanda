package com.includes.Model;

import org.apache.poi.ss.usermodel.RichTextString;

public class SchemaDataAttribute {
		
	
	    private String attribute_name;
		private String default_value;
		private String range;
		private String data_type;
		public String getAttribute_name() {
			return attribute_name;
		}
		public void setAttribute_name(String attribute_name) {
			this.attribute_name = attribute_name;
		}
		public String getDefault_value() {
			return default_value;
		}
		public void setDefault_value(String default_value) {
			this.default_value = default_value;
		}
		public String getRange() {
			return range;
		}
		public void setRange(String range) {
			this.range = range;
		}
		public String getData_type() {
			return data_type;
		}
		public void setData_type(String data_type) {
			this.data_type = data_type;
		}
		
		

}
