
import java.util.HashMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;

import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;

	public class StoreXML_Cassandra
	{
		@SuppressWarnings("serial")
		public static void main(String[] args)
		{
			Session session;
			SparkSession sql = SparkSession.builder().appName("CM data")
					                                   .config("spark.sql.warehouse.dir", "file:/C:/Users/admin/workspace/Cassandr_Spark_learning_XML/spark-warehouse/")
					                                   .config("spark.cassandra.connection.host","127.0.0.1")
					                                   .config("spark.cassandra.connection.port", "9042")
					                                   .master("local[*]")
					                                   .getOrCreate();
					
			CassandraConnector connector = CassandraConnector.apply(sql.sparkContext().conf());

			session = connector.openSession();
		  
		    String query = "CREATE KEYSPACE IF NOT EXISTS customerCMNew WITH replication "
		    		   + "= {'class':'NetworkTopologyStrategy', 'datacenter1':1}; ";
		    		   
		    
	        session.execute(query);
		    System.out.println("Keyspace customerCMNew created");
		    
			
	        session.execute("USE customerCMNew");
		     
	        
		    session.execute("CREATE TABLE IF NOT EXISTS customerCMNew.cmNew (" + 
    		        "class text, " + 
                    "id text, " + 
                    "distName text, " +
                    "version text, " +
                    "p_name text, " +
                    "list_p text, " +
                    "PRIMARY KEY (id))" ); 
		
		    session.execute("CREATE TABLE IF NOT EXISTS customerCMNew.p (" + 
    		        "class text, " + 
                    "id bigint, " + 
                    "distName text, " +
                    "version text, " +
                    "pname text, " +
                    "pvalue text, " +
                    "PRIMARY KEY (pname, pvalue))" ); 
		    
		    session.execute("CREATE TABLE IF NOT EXISTS customerCMNew.list (" + 
    		        "class text, " + 
                    "id text, " + 
                    "distName text, " +
                    //"version text, " +
                    "listname text, " +
                    "itemname text, " +
                    "itemvalue text, " +
                    "PRIMARY KEY (listname, itemname, itemvalue))" ); 
		    
		    		    
			System.setProperty("hadoop.home.dir", "C:\\spark-2.1.1-bin-hadoop2.7\\");
			
	        Dataset<Row> df = sql.read()
	        		             .format("com.databricks.spark.xml")
	        		             .option("rowTag", "managedObject")
	        		             .load("C:\\{CMdir1\\CM.xml}");
	       
	        df.select("p", "_id").write()
            .format("com.databricks.spark.xml")
            .option("rootTag", "cmData")
            .option("rowTag", "managedObject");
	        
	              	      
	        df = df.withColumnRenamed("_class", "class");
	        df = df.withColumnRenamed("_id", "id");
	        df = df.withColumnRenamed("_distName", "distname");
	        df = df.withColumnRenamed("_version", "version");
	        df = df.withColumnRenamed("p", "p_name");
	        df = df.withColumnRenamed("list", "list_p");
	        
	        df = df.drop("_corrupt_record");
	        df = df.drop("defaults");
	        df = df.drop("list");
	        df = df.filter(df.col("id").isNotNull());
	        
	        //Change column 'id' datatype to int
	        df.withColumn("id", (df.col("id").cast("int")));
	        df.createOrReplaceTempView("test");
	        	        
	        df.show(400);
	        
	        //Retrieve id for class = 'CESIF'
	        Dataset<Row> results = sql.sql("select id, distname from test where class='CESIF'");
	        results.show();
	        Long val;
	        Row[] dataRows = (Row[])results.collect();
	       	        	
	        for (Row row : dataRows) {
	        val = row.getLong(0);
	        System.out.println("Val: " + val);
	        }
	        
	        //Write to table cmnew
	        df.write().format("org.apache.spark.sql.cassandra").options(new HashMap<String, String>(){
	       	{
	        		put("keyspace", "customercmnew");
	        		put("table", "cmnew");
	       	}
	        }).mode(SaveMode.Append).save();
	          
	        
	        Dataset<Row> df1 = df.select(df.col("id"), df.col("class"), df.col("distname"), df.col("version"), 
	        		                     functions.explode(df.col("p_name")).
	        		                     as("p_name")).
	        		                     select("id", "class", "distname", "version", "p_name.*");
	        df1= df1.withColumnRenamed("_value", "pvalue");
	        df1 = df1.withColumnRenamed("_name", "pname");
	        
	        df1.show();
	        
	        //Write to table p
	        df1.write().format("org.apache.spark.sql.cassandra").options(new HashMap<String, String>(){
	       	{
	        		put("keyspace", "customercmnew");
	        		put("table", "p");
	       	}
	        }).mode(SaveMode.Append).save();
	        
	        
	        Dataset<Row> df2 = df.select(df.col("id"), df1.col("class"), df.col("distname"), df.col("version"),  
	        		         functions.explode(df.col("list_p")).as("list_p")).
	        		         select("id", "class", "distname", "version", "list_p.*");
	        
	        
	        df2= df2.withColumnRenamed("_name", "listname");
	        	               
	        df2 = df2.select(df2.col("id"), df2.col("class"), df2.col("distname"), df2.col("listname"), 
   		         functions.explode(df2.col("item")).as("item")).
   		         select("id", "class", "distname", "listname", "item.*");
	        
	        df2 = df2.select(df2.col("id"), df2.col("class"), df2.col("distname"), df2.col("listname"), 
	   		         functions.explode(df2.col("p")).as("p")).
	   		         select("id", "class", "distname", "listname",  "p.*");
	        
	        df2= df2.withColumnRenamed("_name", "itemname");
	        df2 = df2.withColumnRenamed("_value", "itemvalue");
	             
	        df2.createOrReplaceTempView("testdf2");
	        df2.show(400);
	        	        
	        //Write to table list
	        df2.write().format("org.apache.spark.sql.cassandra").options(new HashMap<String, String>(){
	         	{
	        		put("keyspace", "customercmnew");
	        		put("table", "list");
	         	}
	          }).mode(SaveMode.Append).save();
	        
	        
	        //Dynamically adding a column to cassandra table using CQL
	        Dataset<Row> df3 = sql.sql("select version from test");	 
	        df3.show();
	        Dataset<Row> df4 = sql.sql("select listname, itemname, itemvalue from testdf2");
	        df4.show();
	        sql.conf().set("spark.sql.crossJoin.enabled", "true");
	        df3 = df3.join(df4);
	        df3.show();
	        	        
	        session.execute("ALTER TABLE customerCMNew.list ADD version text");
	        
	        df3.write().format("org.apache.spark.sql.cassandra").options(new HashMap<String, String>(){
	         	{
	        		put("keyspace", "customercmnew");
	        		put("table", "list");
	         	}
	          }).mode(SaveMode.Append).save();
	       
	      }
    }
		
