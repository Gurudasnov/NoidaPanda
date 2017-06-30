
import java.util.ArrayList;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;


	public class CMParseSchema
	{
		public static void main(String[] args)
		{
			System.setProperty("hadoop.home.dir", "C:\\spark-2.1.1-bin-hadoop2.7\\");
			
	        SparkSession sql = SparkSession.builder().appName("CM data").master("local[*]").getOrCreate();
	        
	        /* This did not work for p. p is displayed as null with schema Map
	        StructType customSchema = new StructType(new StructField[] {
				    new StructField("_version", DataTypes.StringType, true, Metadata.empty()),
				    new StructField("_distName", DataTypes.StringType, true, Metadata.empty()),
				    new StructField("_id", DataTypes.IntegerType, true, Metadata.empty()) ,
				    //new StructField("_p", DataTypes.createMapType(DataTypes.StringType, DataTypes.StringType), true, Metadata.empty())
				    new StructField("_p", DataTypes.StringType, true, Metadata.empty())
				});
	        
	        
	        Dataset<Row> df = sql.read()
	        		             .format("com.databricks.spark.xml")
	        		             .option("rowTag", "managedObject")
	        		             .schema(customSchema)
	        		             .load("C:\\CM.xml");
	        System.out.println("Table with managed objects");
			df.show();
			*/
			
	                
			Dataset<Row> df_p = sql.read()
		             .format("com.databricks.spark.xml")
		             .option("rowTag", "managedObject")
		             .option("inferSchema", true)
		             //.schema(customSchema)
		             .load("C:\\CM.xml");
			
			//Shows all 'p'name and values for all managed objects
			 df_p.select("p", "_id").write()
            .format("com.databricks.spark.xml")
            .option("rootTag", "cmData")
            .option("rowTag", "managedObject");
			
			System.out.println("Table with p");
			df_p.show();
			
					
			
			//To read all 'p' for a managed object with id=1209282
			df_p.createOrReplaceTempView("test");
			Dataset<Row> results = sql.sql("select p from test where _id=1209282");
			results.show();
			
			System.out.println("Displays all names and values for p for managed objects with id=1209282");
			results.selectExpr("explode(p) as e").select("e.*").show(100, false);
			results.selectExpr("explode(p) as e").select("e.*").createOrReplaceTempView("test1");
			
			Dataset<Row> p_result = sql.sql("select _value from test1");
			p_result.show();
			p_result = p_result.filter("_value > 1");
			
			System.out.println("Displays all names and values for p for managed objects with id=1209282 with value>1");
			p_result.show();
			
			Row[] dataRows = (Row[]) p_result.collect();
			ArrayList<String> list=new ArrayList<String>();
						
			//Data row returns String by default
			for (Row row : dataRows) {
		  		System.out.println("Row Data: " + row.get(0));
				list.add(row.getString(0));
				}
			
		}
	}

