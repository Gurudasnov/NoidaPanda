
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.spark.sql.Dataset;
	import org.apache.spark.sql.Row;
	import org.apache.spark.sql.SparkSession;

	public class CMParse
	{
		public static void main(String[] args)
		{
			System.setProperty("hadoop.home.dir", "C:\\spark-2.1.1-bin-hadoop2.7\\");
			
	        SparkSession sql = SparkSession.builder().appName("CM data").master("local[*]").getOrCreate();

	        Dataset<Row> df = sql.read()
	        		             .format("com.databricks.spark.xml")
	        		             .option("rowTag", "managedObject")
	        		             .load("C:\\CMdir1\\CM.xml");
	        System.out.println("Table with managed objects");
			df.show(25,false);
			
			//Shows all 'p'name and values for all managed objects
			df.select("p", "_id").write()
		                         .format("com.databricks.spark.xml")
		                         .option("rootTag", "cmData")
		                         .option("rowTag", "managedObject");
			System.out.println("Displays all p name and values for all managed objects");
		    df.show(10, false);
		
			
			Dataset<Row> df1 = sql.read().format("com.databricks.spark.xml").option("rowTag", "managedObject").load("C:\\{CMdir1\\*.xml,CMdir2\\*.xml}");
			df1.createOrReplaceTempView("test");
			
					
			//To read all 'p' for a managed object with id=1209282
			Dataset<Row> results = sql.sql("select p from test where _id=1209282");
			results.show();
			
			System.out.println("Displays all names and values for p for managed objects with id=1209282");
			
			results.selectExpr("explode(p) as e").select("e.*").show(100, false);
			results.selectExpr("explode(p) as e").select("e.*").createOrReplaceTempView("test1");
			
			
			System.out.println("Displays p with name=name"); 
			Dataset<Row> results_name = sql.sql("select _value from test1 where _name LIKE '%name%'");
			results_name.show();
			
			
			Dataset<Row> p_result = sql.sql("select _value from test1");
			p_result.show();
			p_result = p_result.filter("_value > 1");
			
			System.out.println("Displays all names and values for p for managed objects with id=1209282 with value>1");
			p_result.show();
			
			Row[] dataRows = (Row[]) p_result.collect();
			ArrayList<String> list=new ArrayList<String>();
			
			//Data row returns String by default
			for (Row row : dataRows) {
		  		//System.out.println("Row Data: " + row.get(0));
				list.add(row.getString(0));
				}
			
			//Convert to integer
			ArrayList<Integer> listInt =new ArrayList<Integer>();
			
			Iterator<String> itr=list.iterator();  
			while(itr.hasNext())
			{
				listInt.add(Integer.parseInt(itr.next()));
			}
			Iterator<Integer> itrInt = listInt.iterator(); 
			
			System.out.println("Values of p in Integer");
			while(itrInt.hasNext())
			{
			   System.out.println(itrInt.next()); 
			}
		}
	}

