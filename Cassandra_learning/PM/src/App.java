import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
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

	private static final String FILENAME = "C:\\PM.csv";
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
	   Session session;
	  
	   //Building a cluster
       Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
       session = cluster.connect();
       
       
       String query = "CREATE KEYSPACE customer1 WITH replication "
    		   + "= {'class':'SimpleStrategy', 'replication_factor':3}; ";
       
       session.execute(query);
       System.out.println("Keyspace customer1 created"); 
       
       
       session.execute("USE customer1");
       
           
       final String SCHEMA = "CREATE TABLE IF NOT EXISTS customer1.pm (" + 
    		   "Period_start_time text, " + 
               "PLMN_name text, " + 
               "RNC_name text, " +
               "WCEL_name text, " +
               "TT_UMTS_PS_NQI_V1 decimal, " +
               "PRIMARY KEY (RNC_name, Period_start_time))" ; 
       
       
       final String INSERT_STMT = "INSERT INTO customer1.pm (Period_start_time, PLMN_name, RNC_name, WCEL_name, TT_UMTS_PS_NQI_V1) VALUES (?,?,?,?,?)";	   
       
       session.execute("CREATE TABLE IF NOT EXISTS customer1.pm (" + 
    		   "Period_start_time text, " + 
    		   "PLMN_name text, " + 
               "RNC_name text, " +
               "WCEL_name text, " +
               "TT_UMTS_PS_NQI_V1 decimal, " +
               "PRIMARY KEY (RNC_name, Period_start_time))" ); 
       
       
       // Prepare SSTable writer 
       CQLSSTableWriter.Builder builder = CQLSSTableWriter.builder(); 
       
       // set output directory 
       builder.inDirectory("C:\\apache-cassandra-3.11.0\\data\\customer1\\pm") 
             // set target schema 
             .forTable(SCHEMA) 
             // set CQL statement to put data 
             .using(INSERT_STMT) ;
             
       CQLSSTableWriter writer = builder.build(); 
       
       System.out.println("Reading CSV file PM.csv");
       
       BufferedReader br = null;
	  	    
       File file = new File(FILENAME);
	   InputStream input = new FileInputStream(file);
	   br = new BufferedReader(new InputStreamReader(input));
		
	   CsvListReader csvReader = new CsvListReader(br, CsvPreference.STANDARD_PREFERENCE);
	   csvReader.getHeader(true);
	 	    
       // Write to SSTable while reading data 
       List<String> line; 
       line = csvReader.read();
                
       while ((line = csvReader.read()) != null) 
       { 
           writer.addRow(line.get(0),line.get(1),line.get(2),line.get(5),new BigDecimal(line.get(8))); 
           
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
	     storageBean.bulkLoad("C:\\apache-cassandra-3.11.0\\data\\customer1\\pm");
	     connector.close();
	     
	}

}
