import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;

public class parseXML {

	public static void main(String[] args) throws IOException {
		

		 Session session;
		  
		 //Building a cluster
	     Cluster cluster = Cluster.builder().addContactPoint("192.168.104.63").withCredentials("cassandra", "cassandra").build();
	     session = cluster.connect();
	     
	     String query = "CREATE KEYSPACE IF NOT EXISTS customer3 WITH replication "
	    		   + "= {'class':'SimpleStrategy', 'replication_factor':3}; ";
	    		 //+ "= {'class':'NetworkTopologyStrategy', 'datacenter1':1}; ";
	     //+ "= {'class':'NetworkTopologyStrategy', 'dc1':1}; ";
	       
	     session.execute(query);
	     System.out.println("Keyspace customer3 created"); 
	     
	     session.execute("USE customer3");
	 
	     session.execute("CREATE TABLE IF NOT EXISTS customer3.cm (" +
	    		         "template_name text, " +
	    		         "value text, " +
	    		         "xml_blob blob, "  + 
	                     "PRIMARY KEY (template_name, value))" ); 
	     
	     /* Limitation- cannot mix counter and non-counter data types.
	     session.execute("CREATE TABLE IF NOT EXISTS customer3.cm (" +
		         "value counter, " +
                 "template_name text, " +
		         "xml_blob blob, "  + 
                 "PRIMARY KEY (template_name))" ); 
         */
	     
	     FileInputStream fileInputStream = null; 
        
	    // File file = new File("/opt/resourcesCMParse/CM.xml");
	     File file = new File("/opt/CM.xml");
         byte[] bFile = new byte[(int) file.length()]; 
         fileInputStream = new FileInputStream(file); 
         fileInputStream.read(bFile); 
         fileInputStream.close(); 
         ByteBuffer buffer =ByteBuffer.wrap(bFile);
         
         String templateName = "template1";
         String valueName = "value";
         PreparedStatement ps = session.prepare("INSERT INTO customer3.cm (template_name, xml_blob, value) VALUES (?,?,?)");
         BoundStatement boundStatement = new BoundStatement(ps);
         session.execute(boundStatement.bind(templateName, buffer, valueName));
         System.out.println("Inserted XML as blob into cassandra"); 
         
         /*
         ps = session.prepare("UPDATE customer3.cm SET value = value + ? WHERE template_name = ?");
         boundStatement = new BoundStatement(ps);
         session.execute(boundStatement.bind(1, templateName));
         */
         //Updating the blob
         ps = session.prepare("UPDATE customer3.cm SET xml_blob = ? WHERE template_name = ? AND value = ?");
         boundStatement = new BoundStatement(ps);
         session.execute(boundStatement.bind(buffer, templateName, valueName));
         System.out.println("Updated XML as blob into cassandra"); 
         
       
         
         //Reading the blob
         ps = session.prepare("select xml_blob from customer3.cm where template_name = ?");
         ResultSet rs = null;
         boundStatement = new BoundStatement(ps);
         rs = session.execute(boundStatement.bind(templateName));
         ByteBuffer bufferFile = null;
        
         File ofile = new File("/opt/resources/CMParse/output.xml");
         
         if (rs.isExhausted()) 
         {
        	 System.out.println("No content returned");
         }
         else
         {
        	 //for (Row row : rs) 
        	 //{
        	    Row row = rs.one();
        		bufferFile = row.getBytes("xml_blob"); 
        		FileChannel wChannel = new FileOutputStream(ofile, false).getChannel();
        		wChannel.write(bufferFile);
        		wChannel.close();
                
        	 //}
         }
         
       //Deleting the blob
         ps = session.prepare("DELETE FROM customer3.cm WHERE template_name = ? AND value = ?");
         boundStatement = new BoundStatement(ps);
         session.execute(boundStatement.bind(templateName, valueName));
         System.out.println("Deleted entry from cassandra"); 
         
         session.close();
         cluster.close();
	 }
	
	
}
