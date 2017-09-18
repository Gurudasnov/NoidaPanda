import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URI;
import java.rmi.registry.LocateRegistry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import org.apache.cassandra.auth.PasswordAuthenticator;
import org.apache.cassandra.exceptions.InvalidRequestException;
import org.apache.cassandra.io.sstable.CQLSSTableWriter;
import org.apache.cassandra.service.StorageServiceMBean;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.api.java.UDF6;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class TransformPM {

	private static final String FILENAME1 = "C:\\PM.csv";
	//private static final String FILENAME2 = "C:\\PM1.csv";
	private static final String FILENAME2 = "/opt/PM1.csv";
	private static final String FILENAMEHDFS = "PM.csv";
	
	//private static final String CASSANDRA_PATH1 = "C:\\apache-cassandra-3.11.0\\data\\customer1\\pm	";
	private static final String CASSANDRA_PATH1 = "/opt/apache-cassandra-3.11.0/data/data/customer1/pm";
	
	//private static final String CASSANDRA_PATH2 = "C:\\apache-cassandra-3.11.0\\data\\customer1\\pm1";
	private static final String CASSANDRA_PATH2 = "/opt/apache-cassandra-3.11.0/data/data/customer1/pm1";
	
	private static JMXConnector jmxconnector;
    private static StorageServiceMBean storageBean;
    private static Session session;
    private static CassandraConnector connector;
    
    private static void connect(String host, int port) throws IOException, MalformedObjectNameException
    {
    	//System.setProperty("java.rmi.server.hostname",java.net.InetAddress.getLocalHost().getHostName());
    	
    	//System.out.println(java.net.InetAddress.getLocalHost().getHostName());
    	
        System.setProperty("java.rmi.server.hostname", host);
    	
    	System.out.println("java.rmi.server.hostname: " + System.getProperty("java.rmi.server.hostname"));  
        JMXServiceURL jmxUrl = new JMXServiceURL(String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, port));
    	Map<String, Object> env = new HashMap<String, Object>();
        env.put(JMXConnector.CREDENTIALS,new String[]{"cassandra","cassandra"});
        jmxconnector = JMXConnectorFactory.connect(jmxUrl, env);
       

        MBeanServerConnection mbeanServerConn = jmxconnector.getMBeanServerConnection();
        if(mbeanServerConn == null)
        	System.out.println("mbeanServerConn is null");
        
        ObjectName name = new ObjectName("org.apache.cassandra.db:type=StorageService");
        storageBean = JMX.newMBeanProxy(mbeanServerConn, name, StorageServiceMBean.class);
        System.out.println("JMX connection completed ");
    }

	public static void main(String[] args) throws IOException, MalformedObjectNameException, InvalidRequestException, ParseException 
	{
	   	 
	/*	
	   //Building a cluster
        Cluster cluster = Cluster.builder().addContactPoint("192.168.104.63").withCredentials("cassandra", "cassandra").build();
       session = cluster.connect();
      */
		
	 //Setup spark session
    
	 SparkSession sql = SparkSession.builder().appName("PM data")
			                                  // .config("spark.sql.warehouse.dir", "file:/C:/Users/admin/workspace/Spark_transformationsActions_PM/spark-warehouse/")
	     	                                   .config("spark.sql.warehouse.dir", "file:/opt/resources/spark-warehouse/")
			                                   .config("spark.cassandra.connection.host","192.168.104.63")
			                                   .config("spark.cassandra.connection.port", "9042")
			                                   .config("spark.cassandra.auth.username", "cassandra")
	                                           .config("spark.cassandra.auth.password", "cassandra")
	                                           .config("spark.cassandra.output.batch.grouping.key", "none")
	                                           .config("spark.cassandra.output.batch.size.bytes", "2048")
	                                           .getOrCreate();
			
	        sql.conf().set("spark.sql.crossJoin.enabled", "true");
	        sql.conf().set("spark.cassandra.output.consistency.level", "ANY");
	        	              
	       	connector = CassandraConnector.apply(sql.sparkContext().conf());
	      
			session = connector.openSession();
            Cluster cluster = session.getCluster();
            
             
       String query = "CREATE KEYSPACE IF NOT EXISTS customer1 WITH replication "
    		   + "= {'class':'NetworkTopologyStrategy', 'datacenter1':1}; ";
       
       session.execute(query);
       System.out.println("Keyspace customer1 created"); 
       
       
       session.execute("USE customer1");
       
           
       final String SCHEMA = "CREATE TABLE IF NOT EXISTS customer1.pm (" + 
            		   "Period_start_time timestamp, " + 
            		   "PLMN_name text, " + 
                       "RNC_name text, " +
                       "WCEL_name text, " +
                       "TT_UMTS_CS_NQI decimal, " +
                       "TT_UMTS_PS_NQI_V1 decimal, " +
                       "TT_UMTS_CS_ACCESSIBILITY decimal, " +
                       "TT_UMTS_CS_RETAINABILITY decimal, " +
                       "TT_UMTS_PS_ACCESSIBILITY decimal, " +
                       "TT_UMTS_PS_RETAINABILITY decimal, " +
                       "PRIMARY KEY (WCEL_name, Period_start_time))" +
                       "WITH default_time_to_live = 300 " +
                       "AND GC_GRACE_SECONDS = 60 " + 
                       "AND compaction = {'class' : 'org.apache.cassandra.db.compaction.TimeWindowCompactionStrategy', 'enabled' : 'true', 'tombstone_compaction_interval': '600', 'compaction_window_size': '2', 'compaction_window_unit': 'MINUTES', 'min_threshold': '2'}"; 
                    
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
               "PRIMARY KEY (WCEL_name, Period_start_time)), " +             
               "WITH default_time_to_live = 600 " +
               "AND GC_GRACE_SECONDS = 60 " + 
               "AND compaction = {'class' : 'org.apache.cassandra.db.compaction.TimeWindowCompactionStrategy', 'compaction_window_size': '1', 'compaction_window_unit': 'MINUTES', 'min_threshold': '2'}";  
       
       
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
      
       ReadCSVFromHDFSAndStoreInCassandra(builder, writer, FILENAMEHDFS, CASSANDRA_PATH1);
       //ReadCSVAndStoreInCassandra(builder, writer, FILENAME1, CASSANDRA_PATH1);
       
       ReadCSVAndStoreInCassandra(builder1, writer1, FILENAME2, CASSANDRA_PATH2);
      
       /*
       //Setup spark session
       SparkSession sql = SparkSession.builder().appName("PM data")
				                                  // .config("spark.sql.warehouse.dir", "file:/C:/Users/admin/workspace/Spark_transformationsActions_PM/spark-warehouse/")
				                                   .config("spark.sql.warehouse.dir", "file:/opt/resources/spark-warehouse/")
				                                   .config("spark.cassandra.connection.host","192.168.104.63")
				                                   .config("spark.cassandra.connection.port", "9042")
				                                   .config("spark.cassandra.auth.username", "cassandra")
                                                   .config("spark.cassandra.auth.password", "cassandra")
                                                   .config("spark.cassandra.output.batch.grouping.key", "none")
                                                   .config("spark.cassandra.output.batch.size.bytes", "2048")
				                                   //.master("local[*]")
				                                   .getOrCreate();
		
        sql.conf().set("spark.sql.crossJoin.enabled", "true");
        sql.conf().set("spark.cassandra.output.consistency.level", "ANY");
        sql.conf().set("spark.local.ip", "192.168.104.63");
        
       	connector = CassandraConnector.apply(sql.sparkContext().conf());
       	
		session = connector.openSession();
 		session.execute("USE customer1");
 		*/
	    //System.setProperty("hadoop.home.dir", "C:\\spark-2.1.1-bin-hadoop2.7\\");
	    System.setProperty("hadoop.home.dir", "/opt/spark-2.2.0-bin-hadoop2.7");
	    
	    Transformations1(sql);
	    Transformations2(sql);
	    
	    
	    Dataset<Row> df_mid = Transformations3(sql);
	    	    
	    
	    session.execute("ALTER TABLE customer1.pm ADD result_mid text");
	    
		df_mid.write().format("org.apache.spark.sql.cassandra").options(new HashMap<String, String>(){
         	{
        		put("keyspace", "customer1");
        		put("table", "pm");
         	}
          }).mode(SaveMode.Append).save();
		
		session.execute("ALTER TABLE customer1.pm DROP result_mid");
		
	    jmxconnector.close();
	    session.close();
	    
	    if(session.isClosed())
	    	System.out.println("Session is closed");
	    else
	    	System.out.println("Session is open");
	    //sql.close();
	    
	    //cluster.close();
	    
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
	       
	       
	       while ((line = csvReader.read()) != null) 
	       { 
	    	   
	    	          writer.addRow(formatter.parse(line.get(0)),line.get(1),line.get(2),line.get(5),
	    	                 new BigDecimal(line.get(7)).setScale(2, BigDecimal.ROUND_UP), new BigDecimal(line.get(8)).setScale(2, BigDecimal.ROUND_UP),
	        		         new BigDecimal(line.get(9)).setScale(2, BigDecimal.ROUND_UP), new BigDecimal(line.get(10)).setScale(2, BigDecimal.ROUND_UP),
	        		         new BigDecimal(line.get(11)).setScale(2, BigDecimal.ROUND_UP), new BigDecimal(line.get(12)).setScale(2, BigDecimal.ROUND_UP)); 
	    	          
	    	       	    	        
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
		     
		    	     
		     //connect("localhost", 7199);
		     connect("192.168.104.63", 7199);
		     storageBean.bulkLoad(CASSANDRA_PATH);
		    
	}
	
	public static void ReadCSVFromHDFSAndStoreInCassandra(CQLSSTableWriter.Builder builder, CQLSSTableWriter writer, final String FILENAME, final String CASSANDRA_PATH) throws IOException, MalformedObjectNameException, InvalidRequestException, ParseException
	{
		System.out.println("Reading CSV file: " + FILENAME);
	       
	       BufferedReader br = null;
	       
	       System.setProperty("HADOOP_USER_NAME", "hdfs"); 
	       System.setProperty("hadoop.home.dir", "/"); 

		   String hdfsuri = "hdfs://192.168.104.62:9000/PMFiles/";
		   
	       // Create a default hadoop configuration
	       Configuration config = new Configuration();
	       
	       config.set("fs.hdfs.impl", 
	    	        org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()
	    	    );
	       
	       config.set("fs.file.impl",
	    	        org.apache.hadoop.fs.LocalFileSystem.class.getName()
	    	    );
	    	    
	       System.out.println("fs.default.name : - " + config.get("fs.default.name"));
	       
	       //Get the filesystem - HDFS
	       FileSystem fs = FileSystem.get(URI.create(hdfsuri + FILENAME), config);
	       FSDataInputStream in = null;

	      //Open the path mentioned in HDFS
	       in = fs.open(new Path(hdfsuri + FILENAME));
      
	       br = new BufferedReader(new InputStreamReader(in));
			
		   CsvListReader csvReader = new CsvListReader(br, CsvPreference.STANDARD_PREFERENCE);
		   csvReader.getHeader(true);
		 	    
	       // Write to SSTable while reading data 
	       List<String> line; 
	       line = csvReader.read();
	       
	       SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
	       
	       
	       while ((line = csvReader.read()) != null) 
	       { 
	    	   
	    	          writer.addRow(formatter.parse(line.get(0)),line.get(1),line.get(2),line.get(5),
	    	                 new BigDecimal(line.get(7)).setScale(2, BigDecimal.ROUND_UP), new BigDecimal(line.get(8)).setScale(2, BigDecimal.ROUND_UP),
	        		         new BigDecimal(line.get(9)).setScale(2, BigDecimal.ROUND_UP), new BigDecimal(line.get(10)).setScale(2, BigDecimal.ROUND_UP),
	        		         new BigDecimal(line.get(11)).setScale(2, BigDecimal.ROUND_UP), new BigDecimal(line.get(12)).setScale(2, BigDecimal.ROUND_UP)); 
	    	          
	    	       	    	        
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
		   
		      System.out.println("Hostname: " + java.net.InetAddress.getLocalHost().getHostName());
		      if(java.net.InetAddress.getLocalHost().getHostName().equalsIgnoreCase("spark-hdfs"))
		      {
		    	 System.out.println("SCP initiated from remote node");  
		    	 
		    	 File folder = new File("/opt/apache-cassandra-3.11.0/data/data/customer1/pm");
		    	 File[] listOfFiles = folder.listFiles();
		    	 for (int i = 0; i < listOfFiles.length; i++)
		    	 {
  		    	   String command = "/usr/bin/scp /opt/apache-cassandra-3.11.0/data/data/customer1/pm/" + listOfFiles[i].getName() + " root@192.168.104.63:/opt/apache-cassandra-3.11.0/data/data/customer1/pm";
		    	   try
		    	   {
		    	     Process process = Runtime.getRuntime().exec(command);
		    	   /*
		    	     InputStream stderr = process.getErrorStream();
		             InputStreamReader isr = new InputStreamReader(stderr);
		             BufferedReader bfr = new BufferedReader(isr);
		             String lines = null;
		             System.out.println("<ERROR>");
		             while ( (lines = bfr.readLine()) != null)
		                System.out.println(lines);
		             System.out.println("</ERROR>");
		    	 */
		           
				     int exitVal = process.waitFor();
				     System.out.println("Scp exit val:" + exitVal);
		    	    
			      } catch (Throwable t){
				  // TODO Auto-generated catch block
				  t.printStackTrace();
				  }
		    	 }
			  }
		     
			     
		     //Jmxloader to load the SSTable to Cassandra
		     //connect("localhost", 7199);
		     connect("192.168.104.63", 7199);
		     storageBean.bulkLoad(CASSANDRA_PATH);
		     fs.close();
		 	     
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
		
		//df.show(200);
		df.createOrReplaceTempView("test");
		
		//Dataset<Row> dfNew = df.join(df1);
		df1.createOrReplaceTempView("test1");
		
		//Union of select queries on PM and PM1
		Dataset<Row> result_list = sql.sql("(select Period_start_time, WCEL_name, AVG(TT_UMTS_CS_NQI)TT_UMTS_CS_NQI_S, SUM(TT_UMTS_PS_NQI_V1)TT_UMTS_PS_NQI_V1_S " +
		                                   "from test group by Period_start_time, WCEL_name order by Period_start_time) UNION " +
				                           "(select Period_start_time, WCEL_name, SUM(TT_UMTS_CS_NQI)TT_UMTS_CS_NQI_S, SUM(TT_UMTS_PS_NQI_V1)TT_UMTS_PS_NQI_V1_S " +
		                                   "from test1 group by Period_start_time, WCEL_name order by Period_start_time)");
		//result_list.show(200);
		result_list.createOrReplaceTempView("testResultDate");
		
		
		//TO_DATE Works, but not exactly in the same format mentioned in the PM file sheet
		Dataset<Row> result_date_1 = sql.sql("select date_format(Period_start_time, 'yyyy-MM-dd')Period_new from testResultDate " +
		                                      "where date_format(Period_start_time, 'yyyy-MM-dd') = to_date('2017-03-23')");
		
		
		//result_date_1.show(200);
		
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
			
					
			//df.show();
			df.createOrReplaceTempView("test");
			
			
			sql.udf().register("DECODE", new UDF6<BigDecimal, BigDecimal, Integer, BigDecimal, Integer, Integer, String>()
			{
					/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

					public String call(BigDecimal arg0, BigDecimal arg1, Integer arg2, BigDecimal arg3,
							Integer arg4, Integer arg5) throws Exception {
						int i=0;
						java.util.List<Object> list = new ArrayList<Object>();
						
						list.add(arg1);
						list.add(arg2);
						list.add(arg3);
						list.add(arg4);
						list.add(arg5);
						
						String result = null;
						
						while(i<list.size())
						{
							if((i != list.size()-1) && (arg0.setScale(2, BigDecimal.ROUND_UP).compareTo((BigDecimal) list.get(i))==0))
							{
							  result = String.valueOf(list.get(i+1));
							  break;
							}
							else if(i == (list.size() -1))
							{
								result = String.valueOf(list.get(i));
							    break;
							}
							i= i+2;
						}
						return result;
					}

									
					
				}, DataTypes.StringType);
						
			
	
	       //decode not supported, Using UDF
	       Dataset<Row> result_list_decode = sql.sql("select Period_start_time, WCEL_name, TT_UMTS_CS_NQI, TT_UMTS_PS_NQI_V1, " +
	                                   "DECODE(TT_UMTS_CS_NQI, 100.00, 1, 200.00, 2, 3)result from test");
	        
	       //result_list_decode.show();
	       
	             
	       
			//CASE supported, TO_NUMBER is not supported
			Dataset<Row> result_list = sql.sql("select rnc_name, WCEL_name, Period_start_time, TT_UMTS_CS_NQI, TT_UMTS_PS_NQI_V1, " +
			                                  "CASE WHEN TT_UMTS_CS_NQI = 100 then 1 " +
					                          "WHEN TT_UMTS_CS_NQI = 200 then 2 " +
			                                  "ELSE 3 end as result from test ");
			
			//Use dataframe na().fill instead of NVL						
			String[] colNames = {"rnc_name"};
			result_list = result_list.na().fill("NDIYK06", colNames);
			//result_list.show();
			
			//Equivalent for SUMIFS functionality- SUMIFS(TT_UMTS_CS_NQI, Period_start_time = "03.23.2017 00:00:00", rnc_name = "NDIYK06" )
			result_list= result_list.groupBy(result_list.col("TT_UMTS_CS_NQI"), result_list.col("rnc_name"), result_list.col("Period_start_time")).
		                 sum("TT_UMTS_CS_NQI").
                        where(result_list.col("Period_start_time").equalTo("2017-03-23 00:00:00").
       		            and (result_list.col("rnc_name").equalTo("NDIYK06"))); 
			
			//Sort based on Column
			result_list.sort(result_list.col("Period_start_time"));
			
			//result_list.show();
			
            //Round up
			//result_list.selectExpr("round(TT_UMTS_CS_NQI)").show();
			result_list.selectExpr("round(TT_UMTS_CS_NQI)");
			
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
			
			//TO_NUMBER is not supported. Alternate solution like User Defined function to be written
			Dataset<Row> result_list_number = sql.sql("select TO_NUMBER(WCEL_name)TO_NUMBER from test");
		    //result_list_number.show();
		 				
			return;
			}
		
		public static Dataset<Row> Transformations3(SparkSession sql)
		{
			System.out.println("Transformations3");
			//Read table PM
			Dataset<Row> df = sql.read().format("org.apache.spark.sql.cassandra").options(new HashMap<String, String>(){
		     	{
		    		put("keyspace", "customer1");
		    		put("table", "pm");
		     	}
		      }).load();
			
			
			df.createOrReplaceTempView("test");
			//Not clear on usage of PERCENTAGE
			/*
			df = df.withColumn("PERCENT", df.col("TT_UMTS_CS_NQI").$percent(df.col("TT_UMTS_CS_NQI")));
			df.show();
			*/
			
			//agg, and, or , max, min, minus, plus, percent, equal, gtequal and similar operations supported on dataframe.
			Dataset<Row> df1 = df.groupBy(df.col("rnc_name"),df.col("tt_umts_ps_accessibility"), df.col("tt_umts_ps_nqi_v1")).
					           agg(df.col("tt_umts_ps_accessibility"), df.col("tt_umts_ps_nqi_v1"));
			//df1.show();
			
			//substr to retrieve part of string- Write UDF and use Left/Right/Mid operations of class StringUtils 
			if(df.col("WCEL_name").substr(1, 4).contains("WSU6") != null)
				System.out.println("Equal");
			
			//UDF for LEFT operation
			sql.udf().register("LEFT", new UDF1<String, String>() {
		
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public String call(String value)
				{
					String subStr = StringUtils.left(value, 4);
				
					return subStr;
					
				}
			}, DataTypes.StringType);
			
			//UDF for RIGHT operation
			sql.udf().register("RIGHT", new UDF1<String, String>() {
		
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public String call(String value)
				{
					String subStr = StringUtils.right(value, 4);
				
					return subStr;
					
				}
			}, DataTypes.StringType);
			
			//UDF for MID operation
			sql.udf().register("MID", new UDF1<String, String>() {
		
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public String call(String value)
				{
					String subStr = StringUtils.mid(value, 2, 3);
				
					return subStr;
					
				}
			}, DataTypes.StringType);

			Dataset<Row> result_list_left = sql.sql("select LEFT(WCEL_name) from test");
			//result_list_left.show();
			
			Dataset<Row> result_list_right= sql.sql("select RIGHT(WCEL_name) from test");
			//result_list_right.show(200);
			
			Dataset<Row> result_list_mid= sql.sql("select MID(wcel_name)result_mid from test where plmn_name='NN(100000)'");
						
			sql.conf().set("spark.sql.crossJoin.enabled", "true");
			
			result_list_mid = result_list_mid.join(df);
			//Use dataframe na().fill instead of NVL						
			String[] colNames = {"rnc_name"};
			result_list_mid = result_list_mid.na().fill("NDIYK06", colNames);
			//result_list_mid.show(200);
			
			
			return result_list_mid;
		}

}

