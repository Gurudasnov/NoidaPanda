package com.UdataToDaisy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.impl.common.IOUtil;

import com.includes.UserData;
import com.includes.JsonParser.ParsingService;
import com.includes.Model.Data;
import com.includes.Model.SchemaDataAttribute;
import com.includes.Model.SchemaMOs;
import com.includes.Model.Schemadata;

public class UdataToDaisy {

	public static void main(String args[]) throws IOException
	{
		XSSFWorkbook workbook = new XSSFWorkbook();
		UserData uDataObj = new UserData();

		String[] moListed = uDataObj.setDummyValuesToMO();
		List<String> sheetNames = new ArrayList<String>();

		XSSFSheet contentsSheet = workbook.createSheet("Contents");
		CreationHelper createHelper = workbook.getCreationHelper();

		sheetNames.add("Contents");
		//Just to make sure it is written in the 3rd column
		int colCount = 3;
		int moSerialIndex = 1;
		//FileOutputStream newOutPutStream = new FileOutputStream("d://Daisy_new.xlsx",true);
		Data userData=(Data) ParsingService.parseData("New-data.json", Data.class);
		Schemadata schemaData = (Schemadata) ParsingService.parseData("schema.json", Schemadata.class);

		//Create the contents page and Link each and every page.


		int sizeOfMos = userData.getMos().size();
		Row rowInMO = null;
		Cell attCell = null,valueCell=null;
		DataFormatter fmt = new DataFormatter();
		Object value;
		int rowIndexInc=0;
		int sheetIndex=0;
		boolean setAttribute = false;
		FileOutputStream outputStream = new FileOutputStream("d://Daisy_ppp.xlsx");

		for (String str: moListed) {
			rowIndexInc=0;
			setAttribute = false;
			Row row = contentsSheet.createRow(colCount);

			Cell indexCell = row.createCell(0);
			indexCell.setCellValue(moSerialIndex);
			Cell cell = row.createCell(1);
			cell.setCellValue((String) str);

			String link = str+"!A1";
			org.apache.poi.ss.usermodel.Hyperlink link2 = createHelper.createHyperlink(Hyperlink.LINK_DOCUMENT);
			link2.setAddress(link);

			cell.setHyperlink(link2);
			//cell.setCellStyle(hlink_style);
			Sheet moSheet = workbook.createSheet(str);
			sheetNames.add(str);
			colCount++;
			moSerialIndex++;

			if(str.contains("Contents"))	
				continue;
			sheetIndex = sheetNames.indexOf(str);
			System.out.println(str);
			///////////////////////
			///CODE TO ADD SCHEMA
			///////////////////////			
			for(int rowIndex=0;rowIndex<sizeOfMos;rowIndex++)
			{
				String s=schemaData.getMOs().get(rowIndex).getMO_name().toString();
				System.out.println(s);
				if(str.equals(schemaData.getMOs().get(rowIndex).getMO_name().toString()))
				{
					rowInMO = moSheet.createRow(rowIndexInc);
					int size = schemaData.getMOs().get(rowIndex).getAttributes().size();
					for(int cellIndex=0;cellIndex<size;cellIndex++)
					{	
						if(rowIndexInc==0)
						{
							attCell = rowInMO.createCell(cellIndex);
							String attName = schemaData.getMOs().get(rowIndex).getAttributes().get(cellIndex).getAttribute_name();
							attCell.setCellValue(attName);
							setAttribute = true;
						}
					}
					rowIndexInc++;
					rowInMO = moSheet.createRow(rowIndexInc);
					for(int cellIndex=0;cellIndex<size;cellIndex++)
					{	
						attCell = rowInMO.createCell(cellIndex);
						String dataType = schemaData.getMOs().get(rowIndex).getAttributes().get(cellIndex).getData_type();
						attCell.setCellValue(dataType);
					}
					rowIndexInc++;
					rowInMO = moSheet.createRow(rowIndexInc);
					for(int cellIndex=0;cellIndex<size;cellIndex++)
					{	
						attCell = rowInMO.createCell(cellIndex);
						String dataType = schemaData.getMOs().get(rowIndex).getAttributes().get(cellIndex).getDefault_value();
						attCell.setCellValue(dataType);
					}
					rowIndexInc++;
					rowInMO = moSheet.createRow(rowIndexInc);
					for(int cellIndex=0;cellIndex<size;cellIndex++)
					{	
						attCell = rowInMO.createCell(cellIndex);
						String dataType = schemaData.getMOs().get(rowIndex).getAttributes().get(cellIndex).getRange();
						attCell.setCellValue(dataType);
					}
					rowIndexInc++;
					rowInMO = moSheet.createRow(rowIndexInc);
					for(int cellIndex=0;cellIndex<size;cellIndex++)
					{	
						valueCell = rowInMO.createCell(cellIndex);
						value = userData.getMos().get(rowIndex).getAttributes().get(cellIndex).getValues();
						valueCell.setCellValue(value.toString());
					}
					if(setAttribute==true)
					{
						break;
					}
				}
				if(setAttribute==true)
					break;
			}			
		}
		workbook.write(outputStream);
		outputStream.flush();
		outputStream.close();
	}
}
