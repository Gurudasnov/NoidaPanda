import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.sql.Timestamp;
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

import org.apache.cassandra.io.sstable.CQLSSTableWriter;
import org.apache.cassandra.service.StorageServiceMBean;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

public class App {

	private static String FILENAME = "";
	private static JMXConnector connector;
    private static StorageServiceMBean storageBean;

    private static void connect(String host, int port) throws IOException, MalformedObjectNameException
    {
        JMXServiceURL jmxUrl = new JMXServiceURL(String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, port));
        Map<String, Object> env = new HashMap<String, Object>();
        connector = JMXConnectorFactory.connect(jmxUrl, env);
        MBeanServerConnection mbeanServerConn = connector.getMBeanServerConnection();
        ObjectName name = new ObjectName("org.apache.cassandra.db:type=StorageService");
        storageBean = JMX.newMBeanProxy(mbeanServerConn, name, StorageServiceMBean.class);
    }

	public static void main(String[] args) throws IOException, MalformedObjectNameException {
		Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
		Session session;
	  
	   //Building a cluster
       Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
       session = cluster.connect();
       
       
       String query = "CREATE KEYSPACE insurance WITH replication "
    		   + "= {'class':'SimpleStrategy', 'replication_factor':3}; ";
       
       session.execute(query);
       System.out.println("Keyspace insurance created"); 
       
       
       session.execute("USE insurance");
       
             
       final String SCHEMA = "CREATE TABLE IF NOT EXISTS insurance.policy (" + 
               "policyID Varint, " + 
               "statecode text, " + 
               "county text, " +
               "PRIMARY KEY (policyID, county))" ; 
               
           
       final String INSERT_STMT = "INSERT INTO insurance.policy (policyID, statecode, county) VALUES (?,?,?)";
       
       session.execute("CREATE TABLE IF NOT EXISTS insurance.policy (" + 
               "policyID Varint, " + 
               "statecode text, " + 
               "county text, " +
               "PRIMARY KEY (policyID, county))" ); 
      
       
       // Prepare SSTable writer 
       CQLSSTableWriter.Builder builder = CQLSSTableWriter.builder(); 
       
       // set output directory 
       builder.inDirectory("C:\\apache-cassandra-3.11.0\\data\\insurance\\policy") 
             // set target schema 
             .forTable(SCHEMA) 
             // set CQL statement to put data 
             .using(INSERT_STMT) ;
             
       CQLSSTableWriter writer = builder.build(); 
       
                  
       BufferedReader br = null;
	  
       File folder = new File("C:/New/");
       File[] listOfFiles = folder.listFiles();

       for (File file1 : listOfFiles) 
       {
         if (file1.isFile()) 
         {
            FILENAME = "C:/New/" + file1.getName();
            File file = new File(FILENAME);
        	        	
        	System.out.println("Reading CSV file " + FILENAME); 	
        	
        	       	
	        InputStream input = new FileInputStream(file);
	        br = new BufferedReader(new InputStreamReader(input));
		
	        CsvListReader csvReader = new CsvListReader(br, CsvPreference.STANDARD_PREFERENCE);
	        csvReader.getHeader(true);
	        
            // Write to SSTable while reading data 
            List<String> line; 
            line = csvReader.read();
          
             while ((line = csvReader.read()) != null) 
            { 
        
               writer.addRow(new BigInteger(line.get(0)),line.get(1),line.get(2)); 
    	           
             } 
	      
			 try {

			    if (br != null)
			  	   br.close();

			    if(csvReader !=null)
			       csvReader.close();

			      } catch (IOException ex) 
			        {
			          	ex.printStackTrace();

			         }
		          }
            }
          
         try 
         { 
           writer.close(); 
          } 
          catch (IOException ignore) {}
	     //Jmxloader to load the SSTable to Cassandra
	     connect("localhost", 7199);
	     storageBean.bulkLoad("C:\\apache-cassandra-3.11.0\\data\\insurance\\policy");
	     connector.close();
	
	 	Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());
	 	System.out.println("Time elapsed ----->"+(timestamp2.getTime()-timestamp1.getTime()));
	}

}
