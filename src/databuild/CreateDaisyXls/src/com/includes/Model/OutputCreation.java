package com.includes.Model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.includes.JsonParser.ParsingService;

//import com.Service.DefaultAssignment.AssignDefultToUserData;
//import com.Service.JsonParser.ParsingService;
//import com.Service.MergeWithSnapShot.MergeData;
//import com.Service.RangeValidation.ValidateRange;
//import com.Service.ValidationBuilder.RangeChooser;

public class OutputCreation {

	public static void main(String[] args) throws JAXBException, FileNotFoundException {
		Data userData=(Data) ParsingService.parseData("schema.json", Data.class);
		
System.out.println(userData.getMos().get(0).getAttributes().get(0).getValues());

	}
	
	
	
	
}
