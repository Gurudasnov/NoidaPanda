
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import com.datastax.driver.core.ColumnMetadata;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import com.datastax.spark.connector.cql.CassandraConnector;


	public class ReadXML
	{
		@SuppressWarnings({ "serial", "rawtypes" })
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
		    			
	        session.execute("USE customerCM");
		   
			System.setProperty("hadoop.home.dir", "C:\\spark-2.1.1-bin-hadoop2.7\\");
			
	        Dataset<Row> df = sql.read().format("org.apache.spark.sql.cassandra").options(new HashMap<String, String>(){
	         	{
	        		put("keyspace", "customercm");
	        		put("table", "cm");
	         	}
	          }).load();
	        
	        df.show();
	       	  
	        df.select("id", "class", "distname", "version", "pname", "pvalue", "listname", "itemname", "itemvalue").write()
            .format("com.databricks.spark.xml")
            .option("rootTag", "cmData")
            .option("rowTag", "managedObject");
	        
	        System.out.println("Displaying table");
	        df.show();
	               
	        df.createOrReplaceTempView("test");
	        Dataset<Row> result_list = sql.sql("select id, pname, pvalue from test where id=1401732");
            result_list.show(35);
            
            Dataset<Row> result_list1 = sql.sql("select id, listname, itemname, itemvalue from test where id=1401732");
            result_list1.show(35);
            
            System.out.println("Schema of table customercm.cm: ");
            Map <String, DataType> schemaMap = getSchema("customercm", "cm", session);
            Iterator it = schemaMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                it.remove(); // avoids a ConcurrentModificationException
            }

	      }
		
		 public static Map<String, DataType> getSchema(String keySpace, String tableName, Session session) { 
			  Metadata m = session.getCluster().getMetadata(); 
			  KeyspaceMetadata km = m.getKeyspace(keySpace); 
			  if (km == null) 
			   return null; 
			  TableMetadata tm = km.getTable(tableName); 
			  if (tm == null) 
			   return null; 
			  // build schema 
			  Map<String, DataType> columnNames = new HashMap<String, DataType>(); 
			  for (ColumnMetadata cm : tm.getColumns()) { 
			   
			    columnNames.put(cm.getName(), cm.getType()); 
			    			    
			  } 
			 
			  	return columnNames;	 
			 } 

    }
		
