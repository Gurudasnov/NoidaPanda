import java.sql.Connection;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

public class SQLSyntaxCheck {


	public static void main(String[] args) {
		
		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);

		String sqlQuery = "insrt into user_info values(?,?)";
		
		sqlparser.sqltext = sqlQuery;
		int ret = sqlparser.parse();
		if (ret == 0) {
			System.out.println("Check syntax ok!");
		} else {
			System.out.println(sqlparser.getErrormessage());
		}

	}
}

/*
 import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

public class SQLSyntaxCheck {

	static Connection conn = null;
	static PreparedStatement pstmt = null;

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		//sqlparser.sqltext = "LOAD DATA INPATH '/user/guru99hive/data.txt' INTO TABLE guruhive_external";
		
		TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);

		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdetails", "root", "root");
		
		String sqlQuery = "insrt into user_info values(?,?)";
		
		sqlparser.sqltext = sqlQuery;
		int ret = sqlparser.parse();
		if (ret == 0) {
			System.out.println("Check syntax ok!");

			pstmt = conn.prepareStatement(sqlQuery);
			pstmt.setString(1, "gfgf");
			pstmt.setString(2, "Ratan");

			int i = pstmt.executeUpdate();
			System.out.println(i + " records inserted");
		} else {
			System.out.println(sqlparser.getErrormessage());
		}

	}
}
 
*/