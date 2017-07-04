package com.Xls2Json;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.Xls2Json.model.DataAttribute;
import com.Xls2Json.model.UMOs;
import com.Xls2Json.model.Udata;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser; 

public class ConvertXlsToJson {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {

			//parseXLSX2Schema("D:\\MY_DATA\\Documents\\NPO\\work\\xls2Json\\RMOD.xlsx");
			parseXLSX2Schema("RMOD.xlsx");
			//parseXLSX2DataSchema("RMOD.xlsx");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("null")
	private static void parseXLSX2DataSchema(String excelFilePath) {
		try {
			//Getting the XLS File
			FileInputStream inputStream = new FileInputStream(new File(excelFilePath));

			//Creating SheetNames 
			Workbook workbook = new XSSFWorkbook(inputStream);
			List<String> sheetNames = new ArrayList<String>();
			for (int i=0; i<workbook.getNumberOfSheets(); i++) {
				sheetNames.add( workbook.getSheetName(i) );
			}

			//Still to get Sheet Name
			for(String str: sheetNames)
			{
				///DataFormatter formatter = new DataFormatter();
				//System.out.println("str:: "+str);

				//Get the index based on the index provided in the for loop and fill the MO name.
				int index = sheetNames.indexOf(str);

				//To check whether repeating MO names in a row are present 
				boolean found = false;
				//boolean foundAttributeIndex = false;

				// Get the Sheet required, where index value will be updated above
				Sheet moSheet = workbook.getSheetAt(index);

				//Required to read the MOs column wise and store it in Array List so that it can be populated to main MO object
				//List<DataAttribute> moNames = new ArrayList<DataAttribute>();

				//Indicate the number of row to be traversed to get the MO Header attributes
				int rowNumHeader=0;
				//int startIndex = 0;


				int lastRowToBeScanned = moSheet.getLastRowNum(); 

				//System.out.println("numOfRows:: "+lastRowToBeScanned);

				java.util.Iterator<Row> iterator = moSheet.iterator();
				ArrayList<String> rowContents = new ArrayList<String>();
				int indexOfAttribute=0;

				while (iterator.hasNext()) {
					Row nextRow = iterator.next();
					java.util.Iterator<Cell> cellIterator = nextRow.cellIterator();

					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							//System.out.print("Cell Values:: "+cell.getStringCellValue());
							String str2 = cell.getStringCellValue();
							rowContents.add(str2);
							break;
						}

					}
					for (int i = 0; i < rowContents.size(); i++) {
						if(rowContents.get(i).toString().equals("(Mandatory Field)") || rowContents.get(i).toString().equals("(Mandatory)"))
						{
							found = true;
							break;
						}						
					}
					rowNumHeader++;
					if(found==true)
						break;
				}

				found=false;
				DataFormatter fmt = new DataFormatter();
				java.util.Iterator<Row> iteratornew = moSheet.iterator();
				ArrayList<String> rowContentsnew = new ArrayList<String>();
				while (iteratornew.hasNext()) {
					Row nextRow = iteratornew.next();
					java.util.Iterator<Cell> cellIterator = nextRow.cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							String str2 = cell.getStringCellValue();
							rowContentsnew.add(str2);
							break;
						}

					}
					for (int i = 0; i < rowContentsnew.size(); i++) {
						for (int j = i+1; j < rowContentsnew.size(); j++) {

							if(rowContentsnew.get(i).toString().equals(rowContentsnew.get(j).toString()))
							{
								found = true;
								indexOfAttribute++;
								break;
							}
						}
						if(found == true)
							break;
					}	
					indexOfAttribute++;
					if(found==true)
						break;
				}
				//System.out.println(indexOfAttribute);
				//Udata allMOsDSchema = new Udata();
				//ArrayList<AttributeValues> moAttVal = null;
				//ArrayList<RowsValues> rowVal = null;
				//System.out.println("SUCCESS "+rowNumHeader);

				Udata uData = null;
				//uData = new Udata();

				//UMOs umos=null;
				List<UMOs> umoList=null;

				DataAttribute attVal = null;
				//List<DataAttribute> attValList = null;

				//uData = new Udata();
				uData = new Udata();
				
				umoList = new ArrayList<UMOs>();
				Row attributeRow = moSheet.getRow(indexOfAttribute);
				
				
				for(int currentRow = rowNumHeader;currentRow<=lastRowToBeScanned;currentRow++)
				{
					List<DataAttribute> attValList = new ArrayList<DataAttribute>();
					UMOs umos = new UMOs();
					umos.setMO_name(str);
					//attValList.clear();
					Row currentRowValues = moSheet.getRow(currentRow);
					if (currentRowValues == null)
						throw new IllegalArgumentException("Empty values for the current row "+currentRow);
					
					Cell attValue=null;
					Cell cellValue=null;
					Object value = null;
					String att = null;

					int lastCellNum = currentRowValues.getLastCellNum();

					for(int col = 1;col<lastCellNum;col++)
					{
						attVal = new DataAttribute();
						attValue = attributeRow.getCell(col);
						cellValue = currentRowValues.getCell(col);
						att = attValue.getStringCellValue();
						value = fmt.formatCellValue(cellValue);
						attVal.setAttValue(value);
						attVal.setAttribute_name(att);
						attValList.add(attVal);
					}
					umos.setAttributes(attValList);
					umoList.add(umos);
				}
				
				uData.setMos(umoList);
				//System.out.println(uData.toString());
				Gson gson = new Gson();
				String json = gson.toJson(uData);
				JsonParser jsonParser = new JsonParser();
				JsonElement jsonElement = jsonParser.parse(json);
				System.out.println(jsonElement);
				//System.out.println("Names of sheets: "+str);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	@SuppressWarnings({ "null", "static-access" })
	public static void parseXLSX2Schema(String fileName) {

		String excelFilePath = fileName;
		//DataFormatter formatter = new DataFormatter();

		try {
			//Getting the XLS File
			FileInputStream inputStream = new FileInputStream(new File(excelFilePath));

			//Creating SheetNames 
			Workbook workbook = new XSSFWorkbook(inputStream);
			List<String> sheetNames = new ArrayList<String>();
			for (int i=0; i<workbook.getNumberOfSheets(); i++) {
				sheetNames.add( workbook.getSheetName(i) );
			}
			//Still to get Sheet Name
			for(String str: sheetNames)
			{
				//System.out.println("str:: "+str);

				//Get the index based on the index provided in the for loop and fill the MO name.
				int index = sheetNames.indexOf(str);

				//To check whether repeating MO names in a row are present 
				boolean found = false;

				// Get the Sheet required, where index value will be updated above
				Sheet moSheet = workbook.getSheetAt(index);

				//Required to read the MOs column wise and store it in Array List so that it can be populated to main MO object
				List<DataAttribute> moNames = new ArrayList<DataAttribute>();

				//Indicate the number of row to be traversed to get the MO Header attributes
				int rowNumHeader=0;
				int startIndex = 0;

				//Start Index will be given to indicate the start of reading the row
				Row rows = moSheet.getRow(startIndex);
				int lastRowNum = moSheet.getLastRowNum(); 
				for (int rowIndex=0; rowIndex<=lastRowNum; rowIndex++) { 
					rows = moSheet.getRow(rowIndex); 
				} 
				int lastCellNum = rows.getLastCellNum();

				java.util.Iterator<Row> iterator = moSheet.iterator();
				ArrayList<String> rowContents = new ArrayList<String>();
				while (iterator.hasNext()) {
					Row nextRow = iterator.next();
					java.util.Iterator<Cell> cellIterator = nextRow.cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();

						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							String str2 = cell.getStringCellValue();
							rowContents.add(str2);
							break;
						}

					}
					for (int i = 0; i < rowContents.size(); i++) {
						for (int j = i+1; j < rowContents.size(); j++) {

							if(rowContents.get(i).toString().equals(rowContents.get(j).toString()))
							{
								found = true;
								rowNumHeader++;
								break;
							}

						}
						if(found == true)
							break;
					}	
					rowNumHeader++;
					if(found==true)
						break;
				}
				//System.out.println(rowNumHeader);
				UMOs obj = new UMOs();
				Udata allMOs = new Udata();
				obj.setMO_name(str);

				int colIndex = 1;
				int colReadIndex = 1;
				//1. Convert object to JSON string
				Gson gson = new Gson();

				for(int j = colReadIndex; j < lastCellNum;j++)
				{
					DataAttribute da= new DataAttribute();
					for(int i = rowNumHeader; i < rowNumHeader+3;i++)
					{
						Row headings = moSheet.getRow(i);
						Cell c=null;
						int cellType;
						if (headings == null)
							throw new IllegalArgumentException("Empty headings row of row "+i);
						String val = null;
						//Based on the column number fill the corresponding Data Attribute
						if (i==rowNumHeader)
						{
							c = headings.getCell(colIndex);
							val = c.getStringCellValue();
							da.setAttribute_name(val);			//Filling Attribute name by reading index 0
							headings = moSheet.getRow(rowNumHeader+4);
							c=headings.getCell(colIndex);
							cellType = c.getCellType();
							if(cellType==c.CELL_TYPE_NUMERIC)
							{
								da.setAttType("number");
							}
							if(cellType==c.CELL_TYPE_STRING)
							{
								da.setAttType("String");
							}
						}
						if (i==rowNumHeader+1)
						{
							c = headings.getCell(colIndex);
							val = c.getStringCellValue();
							da.setValues(val);
							if(val.contains("efault"))
							{
								int default_val = getDefaultValue(val);
								da.setDefault_val(default_val);
							}
							else
								da.setDefault_val("n/a");
							headings = moSheet.getRow(rowNumHeader+4);
							c=headings.getCell(colIndex);
							cellType = c.getCellType();
							if(cellType==c.CELL_TYPE_NUMERIC)
							{
								da.setAttType("number");
							}
							if(cellType==c.CELL_TYPE_STRING)
							{
								da.setAttType("String");
							}
						}
						if (i==rowNumHeader+2)
						{
							c = headings.getCell(colIndex);
							val = c.getStringCellValue();
							boolean val2=false;
							if(val.contains("(Mandatory Field)") || val.contains("(Mandatory)"))
							{
								val2 = true;
							}
							da.setMandatory(val2);//Filling Attribute Mandatory by reading index 2
							headings = moSheet.getRow(rowNumHeader+4);
							c=headings.getCell(colIndex);
							cellType = c.getCellType();
							if(cellType==c.CELL_TYPE_NUMERIC)
							{
								da.setAttType("number");
							}
							if(cellType==c.CELL_TYPE_STRING)
							{
								da.setAttType("String");
							}
						}
						
					}
					moNames.add(da);
					colIndex++;
				}

				
				List<UMOs> umoList = new ArrayList<UMOs>();
				obj.setAttributes(moNames);
				umoList.add(obj);
				allMOs.setMos(umoList);
				String json = gson.toJson(allMOs);
				JsonParser jsonParser = new JsonParser();
				JsonElement jsonElement = jsonParser.parse(json);
				System.out.println(jsonElement);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static int getDefaultValue(String s){
		int defaultValue = 0;
		String defaultStrPart = s.substring(s.indexOf("efault"));
		StringTokenizer st;

		if(defaultStrPart.contains(":")){
			st = new StringTokenizer(defaultStrPart, ":");
			String part1 = st.nextToken();
			String part2 = st.nextToken();
			StringTokenizer st_space = new StringTokenizer(part2); 
			part2 = st_space.nextToken();
			defaultValue = Integer.parseInt(part2.trim());
		} else if(defaultStrPart.contains("=")){
			st = new StringTokenizer(defaultStrPart, "=");
			String part1 = st.nextToken();
			String part2 = st.nextToken();
			defaultValue = Integer.parseInt(part2.trim());
		} else if(defaultStrPart.contains("(")){
			st = new StringTokenizer(defaultStrPart, "(");
			String part1 = st.nextToken();
			String part2 = st.nextToken();
			StringTokenizer st_closePara = new StringTokenizer(part2, ")"); 
			String numPart = st_closePara.nextToken();
			defaultValue = Integer.parseInt(numPart);
		}
		return defaultValue;
	}
}
