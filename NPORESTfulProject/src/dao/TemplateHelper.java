package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TemplateHelper
{
	private static Connection connection = null;
	private String tableCol1Name = "CASSANDRATABLENAMES";
	private String tableCol2Name = "CASSANDRACOLUMNNAMES";
	public TemplateHelper() throws Exception
	{
		connection = SingletonDB.GetConnection();
	}

	//Example Input from web UI to REST Server (Provided by user - Not hardcoded)
	//	TemplateName="AlarmTemplate"
	//	CassandraTableNames="AlarmDetails,AlarmDetails,AlarmDetails,OperatorDetails,NetworkDetails"
	//	CassandraColumnNames="AlarmID,AlarmType,AlarmTime,ATT,2GNetwork"
	public String createTable(String templateName, String cassandraTableNames, String cassandraColumnNames) throws Exception
	{
		try
		{
			if(templateName == null)
				return "createTableResult : Failure (Template Name is passed null from the web UI)";
			if(cassandraTableNames == null)
				return "createTableResult : Failure (Cassandra Tables Name is passed null from the web UI)";
			if(cassandraColumnNames == null)
				return "createTableResult : Failure (Cassandra Columns Name is passed null from the web UI)";
			String createTable = "CREATE TABLE " + templateName + " ( " + tableCol1Name + " varchar(64) NOT NULL, " + tableCol2Name + " varchar(64) NOT NULL);";
			PreparedStatement ps = connection.prepareStatement(createTable);
			int ctResult = ps.executeUpdate();
			String[] cassandraTableNamesArr = cassandraTableNames.split(",");
			String[] cassandraColumnNamesArr = cassandraColumnNames.split(",");
			if(cassandraTableNamesArr.length != cassandraColumnNamesArr.length)
				return "createTableResult : Failure (Mismatch in number of Cassandra Tables and Columns passed from the web UI)";
			int i = 0;
			int irResult = 0;
			for(; i < cassandraTableNamesArr.length; i++)
			{
				String insertRows = "INSERT INTO " + templateName + " VALUES('" + cassandraTableNamesArr[i] + "', '" + cassandraColumnNamesArr[i] + "');";
				ps = connection.prepareStatement(insertRows.toString());
				int iTemp = ps.executeUpdate();
				irResult = irResult + iTemp;
			}
			if(ctResult == 0 && irResult > 0 && irResult == i)
				return "createTableResult : Success";
			else
				return "createTableResult : Failure (Error in template creation)";
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	//Example Input from web UI to REST Server (Provided by user - Not hardcoded)
	//	TemplateName="AlarmTemplate"
	public String retrieveTable(String templateName) throws Exception
	{
		StringBuffer resultBuf = new StringBuffer(tableCol1Name + ":" + tableCol2Name + ", ");
		try
		{
			if(templateName == null)
				return "retrieveTableResult : Failure (Template Name is passed null from the web UI)";
			String retrieveTable = "select * from " + templateName + ";";
			PreparedStatement ps = connection.prepareStatement(retrieveTable);
			ResultSet rs = ps.executeQuery();
			if(rs == null)
				return "retrieveTableResult : Failure (Error in template retrieval)";
			while(rs.next())
				resultBuf.append(rs.getString(tableCol1Name) + ":" + rs.getString(tableCol2Name) + ", ");
			String strTemp = resultBuf.toString();
			return strTemp.substring(0, strTemp.lastIndexOf(","));
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	//Example Input from web UI to REST Server (Provided by user - Not hardcoded)
	//	TemplateName="AlarmTemplate"
	public String deleteTable(String templateName) throws Exception
	{
		try
		{
			if(templateName == null)
				return "deleteTableResult : Failure (Template Name is passed null from the web UI)";
			String deleteTable = "drop table " + templateName + ";";
			PreparedStatement ps = connection.prepareStatement(deleteTable);
			int result = ps.executeUpdate();
			System.out.println("result = " + result);
			if(result == 0)
				return "deleteTableResult : Success";
			else
				return "deleteTableResult : Failure (Error in template deletion)";
		}
		catch (Exception e)
		{
			throw e;
		}
	}
}