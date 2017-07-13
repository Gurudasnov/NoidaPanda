package dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class SingletonDB
{
	private static Connection connection = null;
	private static String connectionURL = "jdbc:mysql://localhost:3306/test";
	private static String driverName = "com.mysql.jdbc.Driver";
	private static String userName = "root";
	private static String password = "Lucent@123?";
	public static Connection GetConnection() throws Exception
	{
		if(connection == null)
		{
			try
			{
				Class.forName(driverName).newInstance();
				connection = DriverManager.getConnection(connectionURL, userName, password);
			}
			catch (Exception e)
			{
				throw e;
			}
		}
		return connection;
	}
}