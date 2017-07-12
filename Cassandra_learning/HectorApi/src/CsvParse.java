import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.datastax.driver.core.Session;

import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.HConsistencyLevel;
import au.com.bytecode.opencsv.CSVReader;
import me.prettyprint.cassandra.model.BasicColumnDefinition;
import me.prettyprint.cassandra.model.BasicColumnFamilyDefinition;
import me.prettyprint.cassandra.model.ConfigurableConsistencyLevel;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import org.apache.commons.collections4.*;

public class CsvParse {
	private static String FILENAME = "C:/New/FL_insurance_sample.csv";

	public static void main(String[] args) throws IOException {
		 String keyspaceName = "customer2" ;
		 
		 Session session;
		  
	     
		 CSVReader reader = new CSVReader(new FileReader(FILENAME));
		 String [] nextLine;
		 
		 //Building a cluster
		 Cluster cluster = (Cluster) HFactory.getOrCreateCluster("test-cluster","localhost:9160");
	    
		// Create a customized Consistency Level
	     ConfigurableConsistencyLevel configurableConsistencyLevel = new ConfigurableConsistencyLevel();
		 configurableConsistencyLevel.setDefaultWriteConsistencyLevel(HConsistencyLevel.ANY);
	     configurableConsistencyLevel.setDefaultReadConsistencyLevel(HConsistencyLevel.ANY);
	     
		 Keyspace keySpace =  HFactory.createKeyspace(keyspaceName, cluster, configurableConsistencyLevel);
		 
		 System.out.println("Keyspace customer2 created"); 
	     
		  
	     ColumnFamilyDefinition cf = HFactory.createColumnFamilyDefinition(keyspaceName, "users", ComparatorType.UTF8TYPE);
		 cf.setKeyValidationClass(ComparatorType.UTF8TYPE.getClassName());
		 cf.setDefaultValidationClass(ComparatorType.UTF8TYPE.getClassName());
		 
		 //ColumnFamilyDefinition cf = HFactory.createColumnFamilyDefinition("keyspaceName", "DynCf");
	
	
		 KeyspaceDefinition newKeyspace = HFactory.createKeyspaceDefinition(keyspaceName,                 
                 ThriftKsDef.DEF_STRATEGY_CLASS,  
				 2, 
                 Arrays.asList(cf));

		               
		 cluster.addKeyspace(newKeyspace, true);			 
		
         
		 Mutator<String> mutator = HFactory.createMutator(keySpace, StringSerializer.get()); 
         
		 
		 //Read header
		 reader.readNext();
		 
		 try {
			while ((nextLine = reader.readNext()) != null)
			 { 
				/*
				 mutator.addInsertion(nextLine[0], cf.getName() , HFactory.createStringColumn("statecode", nextLine[1]));
				 mutator.addInsertion(nextLine[0], cf.getName() , HFactory.createColumn("policyID", nextLine[0]));
				 mutator.execute();
				 */
				 mutator.insert(nextLine[0], cf.getName(), HFactory.createStringColumn("county", nextLine[2])); 
				 mutator.insert(nextLine[0], cf.getName(), HFactory.createColumn("policyID", nextLine[0]));
				 mutator.insert(nextLine[0], cf.getName(), HFactory.createStringColumn("statecode", nextLine[1])); 
				
			 }
		 } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

        try {
			reader.close();
	  	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
