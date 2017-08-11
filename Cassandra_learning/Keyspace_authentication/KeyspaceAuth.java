import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.cassandra.service.StorageServiceMBean;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;


public class KeyspaceAuth {
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
       Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1")
    		                               .withCredentials("cassandra", "cassandra")
    		                               .build();
       session = cluster.connect();
       
       session.execute("CREATE USER IF NOT EXISTS 'user1' WITH PASSWORD 'user1'");
       System.out.println("User user1 created"); 	  
       
       session.execute("CREATE USER IF NOT EXISTS 'user2' WITH PASSWORD 'user2'");
       System.out.println("User user2 created"); 	
       
       session.execute("USE customer1");
       
       session.execute("GRANT ALL ON KEYSPACE customer1 TO user1");
       System.out.println("All permissions to customer1 for user1 provided"); 
       
       
       session.execute("GRANT SELECT ON KEYSPACE customer1 TO user2");
       System.out.println("Read only permission to customer1 for user2 provided");
       
       LoginAsUser1(session);
      
       LoginAsUser2(session);
     
       session.execute("REVOKE ALL ON KEYSPACE customer1 FROM user2");
       System.out.println("Revoke all permission to customer1 from user2");
    
       LoginAsUser2(session);
               
       session.execute("GRANT MODIFY ON KEYSPACE customer1 TO user2");
       System.out.println("Modify permission to customer1 for user2 provided");
       
       LoginAsUser2(session);
       
       session.execute("REVOKE ALL ON KEYSPACE customer1 FROM user2");
       System.out.println("Revoke all permission to customer1 from user2");
       
       LoginAsCassandra(session);
       
       LoginAsUser2(session);
       
       session.close();
   }
	public static void LoginAsUser1(Session session)
	{
		 		  
		   //Building a cluster
	       Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1")
	    		                               .withCredentials("user1", "user1")
	    		                               .build();
	       session = cluster.connect();
	       
	       String insertStmt = "INSERT INTO customer1.pm (Period_start_time, PLMN_name, RNC_name, WCEL_name, TT_UMTS_PS_NQI_V1) VALUES(?, ?, ?, ?, ?)";
	       PreparedStatement statement = session.prepare(insertStmt);
	       // create the bound statement and initialise it with your prepared statement
	       BoundStatement boundStatement = new BoundStatement(statement);
	       Double ft = 100.00;
	       
	       BigDecimal dec = new BigDecimal(ft);
	       session.execute(boundStatement.bind("10.08.2017 12:00:00", "plmn", "rnc", "wcel", dec));
	       
	       
	       System.out.println("Values inserted into customer1.pm by user1"); 
	}
	
	public static void LoginAsUser2(Session session)
	{
		try
		{
		   //Building a cluster
	       Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1")
	    		                               .withCredentials("user2", "user2")
	    		                               .build();
	       session = cluster.connect();
	       String insertStmt = "INSERT INTO customer1.pm (Period_start_time, PLMN_name, RNC_name, WCEL_name, TT_UMTS_PS_NQI_V1) VALUES(?, ?, ?, ?, ?)";
	       PreparedStatement statement = session.prepare(insertStmt);
	       // create the bound statement and initialise it with your prepared statement
	       BoundStatement boundStatement = new BoundStatement(statement);
	       Double ft = 200.00;
	       
	       BigDecimal dec = new BigDecimal(ft);
	       session.execute(boundStatement.bind("10.08.2017 13:00:00", "plmnNew", "rncNew", "wcelNew", dec));
	       
	       
	       System.out.println("Values inserted into customer1.pm by user2"); 
		 }
		 catch(com.datastax.driver.core.exceptions.UnauthorizedException e)
		 {
		   System.out.println("User2 has no permission to MODIFY table customer1.pm"); 	
		 }
	}
	
	public static void LoginAsCassandra(Session session)
	{
		try
		{
		   //Building a cluster
	       Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1")
	    		                               .withCredentials("cassandra", "cassandra")
	    		                               .build();
	       session = cluster.connect();
	       String insertStmt = "INSERT INTO customer1.pm (Period_start_time, PLMN_name, RNC_name, WCEL_name, TT_UMTS_PS_NQI_V1) VALUES(?, ?, ?, ?, ?)";
	       PreparedStatement statement = session.prepare(insertStmt);
	       // create the bound statement and initialise it with your prepared statement
	       BoundStatement boundStatement = new BoundStatement(statement);
	       Double ft = 200.00;
	       
	       BigDecimal dec = new BigDecimal(ft);
	       session.execute(boundStatement.bind("10.08.2017 13:00:00", "plmnCass", "rncCass", "wcelCass", dec));
	       
	       
	       System.out.println("Values inserted into customer1.pm by Cassandra"); 
		 }
		 catch(com.datastax.driver.core.exceptions.UnauthorizedException e)
		 {
		   System.out.println("User has no permission to MODIFY table customer1.pm"); 	
		 }
	}
}
