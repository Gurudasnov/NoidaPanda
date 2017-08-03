
import java.util.HashMap;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;

import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;

	public class StoreXML
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
		   /* String query = "CREATE KEYSPACE customerCM WITH replication "
		    		   + "= {'class':'SimpleStrategy', 'replication_factor':3}; "; 
		    
		    */	
		    String query = "CREATE KEYSPACE IF NOT EXISTS customerCM WITH replication "
		    		   + "= {'class':'NetworkTopologyStrategy', 'datacenter1':1}; ";
		    		   
		    
	        session.execute(query);
		    System.out.println("Keyspace customerCM created");
		    
			
	        session.execute("USE customerCM");
		     
	        
		    session.execute("CREATE TABLE IF NOT EXISTS customerCM.cm (" + 
    		        "class text, " + 
                    "id text, " + 
                    "distName text, " +
                    "version text, " +
                    "pvalue text, " +
                    "pname text, " +
                    "listname text, " +
                    "itemname text, " +
                    "itemvalue text, " +
                    //"PRIMARY KEY (id, pname, pvalue, listname, itemname, itemvalue))" ); 
		            "PRIMARY KEY (id, pname, listname))" ); 
		
			System.setProperty("hadoop.home.dir", "C:\\spark-2.1.1-bin-hadoop2.7\\");
			
	        Dataset<Row> df = sql.read()
	        		             .format("com.databricks.spark.xml")
	        		             .option("rowTag", "managedObject")
	        		            // .load("C:\\CMdir1\\CM.xml");
	                             .load("C:\\{CMdir1\\*.xml}");
	       
	        df.select("p", "list", "_id").write()
            .format("com.databricks.spark.xml")
            .option("rootTag", "cmData")
            .option("rowTag", "managedObject");
	        
	        /*
	        Dataset<Row> df_header = sql.read()
		             .format("com.databricks.spark.xml")
		             .option("rowTag", "header")
		             .load("C:\\CMdir1\\CM.xml");

	        df_header.select("log").write()
            .format("com.databricks.spark.xml")
            .option("rootTag", "header")
            .option("rowTag", "header");
	        df_header.show();
	        df_header.createOrReplaceTempView("test_header");
	        df_header  = sql.sql("select log from test_header");
	        df_header.show();
	        */
	       	      
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
	        df.show();
	        
	        Dataset<Row> df1 = df.select(df.col("id"), df.col("class"), df.col("distname"), df.col("version"), df.col("list_p"), 
	        		                     functions.explode(df.col("p_name")).
	        		                     as("p_name")).
	        		                     select("id", "class", "distname", "version", "list_p", "p_name.*");
	        df1= df1.withColumnRenamed("_value", "pvalue");
	        df1 = df1.withColumnRenamed("_name", "pname");
	        df1 = df1.select(df1.col("id"), df1.col("class"), df1.col("distname"), df1.col("version"), df1.col("pvalue"), df1.col("pname"), 
	        		         functions.explode(df1.col("list_p")).as("list_p")).
	        		         select("id", "class", "distname", "version", "pname", "pvalue", "list_p.*");
	        
	        
	        df1= df1.withColumnRenamed("_name", "listname");
	        
	        
	        df1= df1.withColumnRenamed("p", "plistname");
	        df1 = df1.drop("plistname");
	        
	        df1 = df1.select(df1.col("id"), df1.col("class"), df1.col("distname"), df1.col("version"), df1.col("pvalue"), df1.col("pname"), df1.col("listname"), 
   		         functions.explode(df1.col("item")).as("item")).
   		         select("id", "class", "distname", "version", "pname", "pvalue", "listname", "item.*");
	        
	        df1 = df1.select(df1.col("id"), df1.col("class"), df1.col("distname"), df1.col("version"), df1.col("pvalue"), df1.col("pname"), df1.col("listname"), 
	   		         functions.explode(df1.col("p")).as("p")).
	   		         select("id", "class", "distname", "version", "pname", "pvalue", "listname",  "p.*");
	        
	        df1= df1.withColumnRenamed("_name", "itemname");
	        df1 = df1.withColumnRenamed("_value", "itemvalue");
	       
	       
	      
	        df1.show(400);
	        /*
	        sql.conf().set("spark.sql.crossJoin.enabled", "true");
	        df1 = df1.withColumnRenamed("id", "idNew");
	        df = df.join(df1);
	        df = df.drop("idNew");
	        df = df.drop("p_name");
	        df = df.withColumnRenamed("_value", "pvalue");
	        df = df.withColumnRenamed("_name", "pname");
	        df.show(200);
	        */
	        
	        
	        df1.write().format("org.apache.spark.sql.cassandra").options(new HashMap<String, String>(){
	         	{
	        		put("keyspace", "customercm");
	        		put("table", "cm");
	         	}
	          }).mode(SaveMode.Append).save();
	        
	        	       	
	      }
    }
		
