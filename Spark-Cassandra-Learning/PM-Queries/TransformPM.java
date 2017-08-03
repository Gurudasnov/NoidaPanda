import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.cassandra.exceptions.InvalidRequestException;
import org.apache.cassandra.io.sstable.CQLSSTableWriter;
import org.apache.cassandra.service.StorageServiceMBean;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.api.java.UDF6;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Decimal;
import org.apache.spark.sql.types.DecimalType;
import org.apache.spark.sql.DataFrameNaFunctions;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;
import com.datastax.driver.core.Cluster;

import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;

import scala.Option;
import scala.collection.Seq;

import java.util.Scanner;

public class TransformPM {

	private static final String FILENAME1 = "C:\\PM.csv";
	private static final String FILENAME2 = "C:\\PM1.csv";
	private static final String CASSANDRA_PATH1 = "C:\\apache-cassandra-3.11.0\\data\\customer1\\pm";
	private static final String CASSANDRA_PATH2 = "C:\\apache-cassandra-3.11.0\\data\\customer1\\pm1";
	
	private static JMXConnector jmxconnector;
    private static StorageServiceMBean storageBean;
    private static Session session;
    private static CassandraConnector connector;
    private static void connect(String host, int port) throws IOException, MalformedObjectNameException
    {
        JMXServiceURL jmxUrl = new JMXServiceURL(String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, port));
        Map<String, Object> env = new HashMap<String, Object>();
        jmxconnector = JMXConnectorFactory.connect(jmxUrl, env);
        MBeanServerConnection mbeanServerConn = jmxconnector.getMBeanServerConnection();
        ObjectName name = new ObjectName("org.apache.cassandra.db:type=StorageService");
        storageBean = JMX.newMBeanProxy(mbeanServerConn, name, StorageServiceMBean.class);
    }

	public static void main(String[] args) throws IOException, MalformedObjectNameException, InvalidRequestException, ParseException {
	   	  
	   //Building a cluster
       Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
       session = cluster.connect();
       
       
       String query = "CREATE KEYSPACE IF NOT EXISTS customer1 WITH replication "
    		   + "= {'class':'SimpleStrategy', 'replication_factor':3}; ";
       
       session.execute(query);
       System.out.println("Keyspace customer1 created"); 
       
       
       session.execute("USE customer1");
       
           
       final String SCHEMA = "CREATE TABLE IF NOT EXISTS customer1.pm (" + 
    		   "Period_start_time timestamp, " + 
    		   //"Period_start_time text, " + 
               "PLMN_name text, " + 
               "RNC_name text, " +
               "WCEL_name text, " +
               "TT_UMTS_CS_NQI decimal, " +
               "TT_UMTS_PS_NQI_V1 decimal, " +
               "TT_UMTS_CS_ACCESSIBILITY decimal, " +
               "TT_UMTS_CS_RETAINABILITY decimal, " +
               "TT_UMTS_PS_ACCESSIBILITY decimal, " +
               "TT_UMTS_PS_RETAINABILITY decimal, " +
               "PRIMARY KEY (WCEL_name, Period_start_time))" ; 
       
       final String SCHEMA1 = "CREATE TABLE IF NOT EXISTS customer1.pm1 (" + 
    		   "Period_start_time timestamp, " + 
    		   //"Period_start_time text, " + 
               "PLMN_name text, " + 
               "RNC_name text, " +
               "WCEL_name text, " +
               "TT_UMTS_CS_NQI decimal, " +
               "TT_UMTS_PS_NQI_V1 decimal, " +
               "TT_UMTS_CS_ACCESSIBILITY decimal, " +
               "TT_UMTS_CS_RETAINABILITY decimal, " +
               "TT_UMTS_PS_ACCESSIBILITY decimal, " +
               "TT_UMTS_PS_RETAINABILITY decimal, " +
               "PRIMARY KEY (WCEL_name, Period_start_time))" ; 
       
       final String INSERT_STMT = "INSERT INTO customer1.pm (Period_start_time, PLMN_name, RNC_name, WCEL_name, TT_UMTS_CS_NQI, TT_UMTS_PS_NQI_V1, TT_UMTS_CS_ACCESSIBILITY, TT_UMTS_CS_RETAINABILITY, TT_UMTS_PS_ACCESSIBILITY, TT_UMTS_PS_RETAINABILITY ) VALUES (?,?,?,?,?,?,?,?,?,?)";	   
       final String INSERT_STMT1 = "INSERT INTO customer1.pm1 (Period_start_time, PLMN_name, RNC_name, WCEL_name, TT_UMTS_CS_NQI, TT_UMTS_PS_NQI_V1, TT_UMTS_CS_ACCESSIBILITY, TT_UMTS_CS_RETAINABILITY, TT_UMTS_PS_ACCESSIBILITY, TT_UMTS_PS_RETAINABILITY) VALUES (?,?,?,?,?,?,?,?,?,?)";
       
             
       session.execute(SCHEMA);
       session.execute(SCHEMA1);
       
            
       // Prepare SSTable writer 
       CQLSSTableWriter.Builder builder = CQLSSTableWriter.builder(); 
       
       
       // set output directory 
       builder.inDirectory(CASSANDRA_PATH1) 
             // set target schema 
             .forTable(SCHEMA) 
             // set CQL statement to put data 
             .using(INSERT_STMT) ;
            
       CQLSSTableWriter writer = builder.build(); 
       
       CQLSSTableWriter.Builder builder1 = CQLSSTableWriter.builder(); 
       
       builder1.inDirectory(CASSANDRA_PATH2) 
       // set target schema 
       .forTable(SCHEMA1) 
       // set CQL statement to put data 
       .using(INSERT_STMT1) ;
       
       CQLSSTableWriter writer1 = builder1.build(); 
      
       ReadCSVAndStoreInCassandra(builder, writer, FILENAME1, CASSANDRA_PATH1);
       ReadCSVAndStoreInCassandra(builder1, writer1, FILENAME2, CASSANDRA_PATH2);
       
       
       //Setup spark session
       SparkSession sql = SparkSession.builder().appName("PM data")
				                                   .config("spark.sql.warehouse.dir", "file:/C:/Users/admin/workspace/Spark_transformationsActions_PM/spark-warehouse/")
				                                   .config("spark.cassandra.connection.host","127.0.0.1")
				                                   .config("spark.cassandra.connection.port", "9042")
				                                   .master("local[*]")
				                                   .getOrCreate();
		
        sql.conf().set("spark.sql.crossJoin.enabled", "true");
       
		connector = CassandraConnector.apply(sql.sparkContext().conf());
		session = connector.openSession();
 		session.execute("USE customer1");
	    System.setProperty("hadoop.home.dir", "C:\\spark-2.1.1-bin-hadoop2.7\\");
		
	    //Transformations1(sql);
	    Transformations2(sql);
	     
	}
	public static void ReadCSVAndStoreInCassandra(CQLSSTableWriter.Builder builder, CQLSSTableWriter writer, final String FILENAME, final String CASSANDRA_PATH) throws IOException, MalformedObjectNameException, InvalidRequestException, ParseException
	{
		System.out.println("Reading CSV file: " + FILENAME);
	       
	       BufferedReader br = null;
		  	    
	       File file = new File(FILENAME);
		   InputStream input = new FileInputStream(file);
		   br = new BufferedReader(new InputStreamReader(input));
			
		   CsvListReader csvReader = new CsvListReader(br, CsvPreference.STANDARD_PREFERENCE);
		   csvReader.getHeader(true);
		 	    
	       // Write to SSTable while reading data 
	       List<String> line; 
	       line = csvReader.read();
	       
	       SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
	       SimpleDateFormat hourFormat = new SimpleDateFormat("hh");
	       
	       while ((line = csvReader.read()) != null) 
	       { 
	    	   
	    	    //System.out.println(hourFormat.parse(line.get(0)));
	            writer.addRow(formatter.parse(line.get(0)),line.get(1),line.get(2),line.get(5),
	    	    //writer.addRow(line.get(0),line.get(1),line.get(2),line.get(5),
	    	                 new BigDecimal(line.get(7)), new BigDecimal(line.get(8)),
	        		         new BigDecimal(line.get(9)), new BigDecimal(line.get(10)),
	        		         new BigDecimal(line.get(11)), new BigDecimal(line.get(12))); 
	           
	       } 
		   try 
	       { 
	          writer.close(); 
	       } 
	       catch (IOException ignore) {}
	       finally {

				 try {

					if (br != null)
				    	br.close();

						 
					if(csvReader !=null)
						csvReader.close();

				} catch (IOException ex) {

					ex.printStackTrace();

				}
			  }
		    //Jmxloader to load the SSTable to Cassandra
		     connect("localhost", 7199);
		     storageBean.bulkLoad(CASSANDRA_PATH);
		     jmxconnector.close();
	}
	
	//Uses select, group by, order by, union, to_date
	public static void Transformations1(SparkSession sql)
	{
		System.out.println("Transformations1");
		//Read table PM
		Dataset<Row> df = sql.read().format("org.apache.spark.sql.cassandra").options(new HashMap<String, String>(){
         	{
        		put("keyspace", "customer1");
        		put("table", "pm");
         	}
          }).load();
		
		//Read table PM1
		Dataset<Row> df1 = sql.read().format("org.apache.spark.sql.cassandra").options(new HashMap<String, String>(){
         	{
        		put("keyspace", "customer1");
        		put("table", "pm1");
         	}
          }).load();
		
		df.show(200);
		df.createOrReplaceTempView("test");
		
		//Dataset<Row> dfNew = df.join(df1);
		df1.createOrReplaceTempView("test1");
		
		//Union of select queries on PM and PM1
		Dataset<Row> result_list = sql.sql("(select Period_start_time, WCEL_name, SUM(TT_UMTS_CS_NQI)TT_UMTS_CS_NQI_S, SUM(TT_UMTS_PS_NQI_V1)TT_UMTS_PS_NQI_V1_S " +
		                                   "from test group by Period_start_time, WCEL_name order by Period_start_time) UNION " +
				                           "(select Period_start_time, WCEL_name, SUM(TT_UMTS_CS_NQI)TT_UMTS_CS_NQI_S, SUM(TT_UMTS_PS_NQI_V1)TT_UMTS_PS_NQI_V1_S " +
		                                   "from test1 group by Period_start_time, WCEL_name order by Period_start_time)");
		result_list.show(100);
		result_list.createOrReplaceTempView("testResultDate");
		
		
		//TO_DATE Works, but not exactly in the same format mentioned in the PM file sheet
		Dataset<Row> result_date_1 = sql.sql("select date_format(Period_start_time, 'yyyy-MM-dd')Period_new from testResultDate " +
		                                      "where date_format(Period_start_time, 'yyyy-MM-dd') = to_date('2017-03-23')");
		
		
		result_date_1.show(200);
		
		//Does not wokr- TRUNC for hh24 format returns NULL
		Dataset<Row> result_date = sql.sql("select TRUNC(Period_start_time, 'YY')Period_start_time from testResultDate");
		
		//result_date.show(200);
		return;
		
		}
	
	    //Uses CASE, NVL kind of operation
		public static void Transformations2(SparkSession sql)
		{
			System.out.println("Transformations2");
			//Read table PM
			Dataset<Row> df = sql.read().format("org.apache.spark.sql.cassandra").options(new HashMap<String, String>(){
	         	{
	        		put("keyspace", "customer1");
	        		put("table", "pm");
	         	}
	          }).load();
			
					
			df.show();
			df.createOrReplaceTempView("test");
			
			
			sql.udf().register("DECODE", new UDF6<Integer, Integer, Integer, Integer, Integer, Integer, String>()
			{
					/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

					@SuppressWarnings("null")
					public String call(Integer arg0, Integer arg1, Integer arg2, Integer arg3,
							Integer arg4, Integer arg5) throws Exception {
						int i=1;
						java.util.List<Integer> list = null;
						list.add(arg0);
						list.add(arg1);
						list.add(arg2);
						list.add(arg3);
						list.add(arg4);
						list.add(arg5);
						
						while(i<list.size())
						{
							if(list.get(0) == list.get(i))
							 return String.valueOf(list.get(i+1));
							
							if(i == (list.size() -1))
								return String.valueOf(list.get(i));
							
							i++;
						}
						return null;
					}

									
					
				}, DataTypes.StringType);
			//sql.udf().register("DECODE", new UDF1<List<Long>, Long>() 
		
											
				/**
				 * 
				 */
				//private static final long serialVersionUID = 1L;
/*
				public Long call(Seq<Long> seq) 
				{
					java.util.List<Long> list = scala.collection.JavaConversions.seqAsJavaList(seq);
					int i=1;
					while(i<list.size())
					{
						if(list.get(0) == list.get(i))
						 return list.get(i+1);
						
						if(i == (list.size() -1))
							return list.get(i);
						
						i++;
					}
					
					return (long) 0;
				} 

				@Override
				public Long call(List<Long> list) throws Exception {
					// TODO Auto-generated method stub
					
					int i=1;
					while(i<list.size())
					{
						if(list.get(0) == list.get(i))
						 return list.get(i+1);
						
						if(i == (list.size() -1))
							return list.get(i);
						
						i++;
					}
					
					return (long) 0;
				}
				*/

			
			
	
	       //decode not supported
	       Dataset<Row> result_list_decode = sql.sql("select Period_start_time, WCEL_name, TT_UMTS_CS_NQI, TT_UMTS_PS_NQI_V1, " +
	                                   "DECODE(TT_UMTS_CS_NQI, 100, 1, 200, 2, 3)result from test");
	        
	       result_list_decode.show();
	       
	             
	       
			//CASE supported, TO_NUMBER is not supported
			Dataset<Row> result_list = sql.sql("select rnc_name, WCEL_name, Period_start_time, TT_UMTS_CS_NQI, TT_UMTS_PS_NQI_V1, " +
			                                  "CASE WHEN TT_UMTS_CS_NQI = 100 then 1 " +
					                          "WHEN TT_UMTS_CS_NQI = 200 then 2 " +
			                                  "ELSE 3 end as result from test ");
			
			sql.udf().register("TO_NUMBER", new UDF1<String, Long>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Long call(String value)
				{
					String nums = value.replaceAll("\\D+","");
					Long a = (long) Integer.parseInt(nums);
					return a;
					
				}
			}, DataTypes.LongType);
			
			//TO_NUMBER is not supported. Alternate solution liks User Defined function to be written
			Dataset<Row> result_list_number = sql.sql("select TO_NUMBER(WCEL_name)TO_NUMBER from test");
		    result_list_number.show();
		 
			//Use dataframe na().fill instead of NVL						
			String[] colNames = {"rnc_name"};
			Dataset<Row> result_list_1 = result_list.na().fill("NDIYK06", colNames);
			//result_list_1.show(200);
			
			return;
			}
}
