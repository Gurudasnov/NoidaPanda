import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

public class CSVHelper
{
//	private static String firstLine = null;
	private static SparkConf spConfig = null;
	private static SparkSession spSession = null;
	private static SparkContext spContext = null;
	private static final String STRINGTYPE= "String";
	private static final String INTTYPE= "Int";
	private static final String DOUBLETYPE= "Double";
	private static HashMap<String, String> fieldsMap = null;
	public static Dataset<Row> parseCSVUsingRDD(String csvFile)
	{
		JavaRDD<String> testRDD = spContext.textFile(csvFile, 1).toJavaRDD();
		String firstLineTemp = testRDD.first();
		if(firstLineTemp == null || firstLineTemp.isEmpty())
		{
			List<String> linesInCSV = testRDD.collect();
			if(linesInCSV != null && linesInCSV.size() > 0)
			{
				for(int i = 0; i < linesInCSV.size(); i++)
				{
					firstLineTemp = linesInCSV.get(i);
					if(firstLineTemp != null && firstLineTemp.length() > 0)
						break;
				}
			}
		}
		final String firstLine = firstLineTemp;
		List<StructField> fields = new ArrayList<>();
		String[] colNames = firstLine.split(",");
		String[] colTypes = new String[colNames.length];
		int i = 0;
		for (String fieldName : colNames)
		{
			String tempFieldName = fieldName.trim();
			StructField field = DataTypes.createStructField(tempFieldName, DataTypes.StringType, true);
			String val = fieldsMap.get(tempFieldName);
			switch(val)
			{
				case STRINGTYPE:
					field = DataTypes.createStructField(tempFieldName, DataTypes.StringType, true);
					colTypes[i] = STRINGTYPE;
					break;
				case INTTYPE:
					field = DataTypes.createStructField(tempFieldName, DataTypes.IntegerType, true);
					colTypes[i] = INTTYPE;
					break;
				case DOUBLETYPE:
					field = DataTypes.createStructField(tempFieldName, DataTypes.DoubleType, true);
					colTypes[i] = DOUBLETYPE;
					break;
				default:
					field = DataTypes.createStructField(tempFieldName, DataTypes.StringType, true);
					colTypes[i] = STRINGTYPE;
					break;
			}
			i = i + 1;
			fields.add(field);
		}
		StructType schema = DataTypes.createStructType(fields);

		JavaRDD<Row> rowRDD = testRDD.map(new Function<String, Row>()
		{
			private static final long serialVersionUID = 1L;
			@Override
			public Row call(String record) throws Exception
			{
				if(record == null || record.isEmpty() || record.equals(firstLine))
				{
					Object[] nullArr = new Object[colNames.length];
					for(int i = 0; i < nullArr.length; i++)
					{
						switch(colTypes[i])
						{
							case STRINGTYPE:
								nullArr[i] = new String("");
								break;
							case INTTYPE:
								nullArr[i] = new Integer(0);
								break;
							case DOUBLETYPE:
								nullArr[i] = new Double(0.0);
								break;
							default:
								nullArr[i] = new String("");
								break;
						}
					}
					return RowFactory.create(nullArr);
				}
				String[] attributes = record.split(",");
				Object[] vals = new Object[attributes.length];
				for(int i = 0; i < attributes.length; i++)
				{
					try
					{
						switch(colTypes[i])
						{
							case STRINGTYPE:
								vals[i] = attributes[i].trim();
								break;
							case INTTYPE:
								vals[i] = Integer.parseInt(attributes[i].trim());
								break;
							case DOUBLETYPE:
								vals[i] = Double.parseDouble(attributes[i].trim());
								break;
							default:
								vals[i] = attributes[i].trim();
								break;
						}
					}
					catch(NumberFormatException nfe)
					{
						vals[i] = attributes[i].trim();
					}
				}
				return RowFactory.create(vals);
			}
		});

		Dataset<Row> testFrame = spSession.createDataFrame(rowRDD, schema);
		testFrame.createOrReplaceTempView("temp_people");
		StringBuffer strBuf = new StringBuffer("select * from temp_people");
		if(colTypes != null && colTypes.length > 0)
		{
			strBuf.append(" where");
			for(int j = 0; j < colTypes.length; j++)
			{
				switch(colTypes[j])
				{
					case STRINGTYPE:
						strBuf.append(" " + colNames[j] + " <> '' and");
						break;
					case INTTYPE:
						strBuf.append(" " + colNames[j] + " != 0 and");
						break;
					case DOUBLETYPE:
						strBuf.append(" " + colNames[j] + " != 0.0 and");
						break;
					default:
						strBuf.append(" " + colNames[j] + " <> '' and");
						break;
				}
			}
		}
		String strTemp = strBuf.toString();
		String strQuery = strTemp.substring(0, strTemp.lastIndexOf(" and"));
		testFrame = spSession.sql(strQuery);
		return testFrame;
	}
	public static void parseCSVUsingDataFrame(String csvFile)
	{
		Dataset<Row> testFrame = spSession.read().csv(csvFile);
		testFrame.createOrReplaceTempView("temp_people");
		testFrame.show();
		String firstLine = testFrame.first().toString().replace("[", "").replace("]", "");
		StringBuffer strBuf = new StringBuffer("select * from temp_people");
		String[] colNames = firstLine.split(",");
		if(colNames != null && colNames.length > 0)
		{
			int i = 0;
			strBuf.append(" where");
			for(i = 0; i < colNames.length; i++)
				strBuf.append(" _c" + i + " <> '" + colNames[i].trim() + "' and");
		}
		String strTemp = strBuf.toString();
		String strQuery = strTemp.substring(0, strTemp.lastIndexOf(" and"));
		testFrame = spSession.sql(strQuery);
		testFrame.createOrReplaceTempView("people");
		testFrame.show();
		strQuery = "desc people";
		testFrame = spSession.sql(strQuery);
		testFrame.show();
	}
	public static void parseMultipleCSVUsingRDD(String csvFile1, String csvFile2)
	{
		Dataset<Row> df1 = parseCSVUsingRDD(csvFile1);
		if(df1 != null)
		{
			df1.show();
			df1.createOrReplaceTempView("csvFile1");
		}
		Dataset<Row> df2 = parseCSVUsingRDD(csvFile2);
		if(df2 != null)
		{
			df2.show();
			df2.createOrReplaceTempView("csvFile2");
		}
		Dataset<Row> df3 = spSession.sql("select * from csvFile1 FULL JOIN csvFile2 on csvFile1.EMPID = csvFile2.EMPID");
		df3.show();
	}
	public static void main(String[] args) throws Exception
	{
		Properties config = new Properties();
		File f = new File("src/main/resources/CSVHelper.properties");
		FileInputStream in = new FileInputStream(f);
		config.load(in);
		String hadoopHomeDir = config.getProperty("hadoop.home.dir");
		System.setProperty("hadoop.home.dir", hadoopHomeDir);
		fieldsMap = new HashMap<String, String>();
		int i = 1;
		boolean isException = false;
		while(true)
		{
			String fieldMapping = config.getProperty("FieldMapping" + i);
			if(fieldMapping == null)
				break;
			if(fieldMapping.isEmpty())
			{
				isException = true;
				StringBuffer strEx = new StringBuffer("\nSyntax for FieldMapping is:");
				strEx.append("\nFieldMappingN=FieldName:FieldType");
				strEx.append("\nwhere N is greater than 1");
				throw new Exception(strEx.toString());
			}
			String[] fields = fieldMapping.split(":");
			if(fields.length != 2)
			{
				isException = true;
				StringBuffer strEx = new StringBuffer("\nSyntax for FieldMapping is:");
				strEx.append("\nFieldMappingN=FieldName:FieldType");
				strEx.append("\nwhere N is greater than 1");
				throw new Exception(strEx.toString());
			}
			if(fields[0] == null || fields[0].isEmpty())
			{
				isException = true;
				throw new Exception("FieldName Cannot Be Null");
			}
			if(fields[1] == null || fields[1].isEmpty())
			{
				isException = true;
				throw new Exception("FieldType Cannot Be Null");
			}
			if(fieldsMap.get(fields[0]) != null)
			{
				isException = true;
				throw new Exception("Duplicate fieldMapping for " + fields[0]);
			}
			if(isException)
				break;
			fieldsMap.put(fields[0], fields[1]);
			i = i + 1;
		}
		spConfig = new SparkConf().setMaster("local[*]");
		spSession = SparkSession.builder().config(spConfig).getOrCreate();
		spContext = spSession.sparkContext();
//		String csvFile = config.getProperty("csvFile");
//		CSVHelper.parseCSVUsingRDD(csvFile);
//		CSVHelper.parseCSVUsingDataFrame(csvFile);
		String csvFile1 = config.getProperty("csvFile1");
		String csvFile2 = config.getProperty("csvFile2");
		CSVHelper.parseMultipleCSVUsingRDD(csvFile1, csvFile2);
		spSession.stop();
	}
}