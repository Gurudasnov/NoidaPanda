package DeleteBlob;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart;

@Path("delfile")
public class BlobDeletion {

	  @POST
	  @Path("delete")
	  @Consumes(MediaType.APPLICATION_JSON)

	public  Response JsonFileDeletion(FileDetails fd) {

		Session session;
		Response res =null;
		String filename;
		Cluster cluster = Cluster.builder().addContactPoint("192.168.104.63").withPort(9042)
				.withCredentials("cassandra", "cassandra").build();
		session = cluster.connect();
		  String activity_name = fd.getActivity_name();
	      String activity_type = fd.getActivity_type();
	      ArrayList<Files> list_file = fd.getFile();
	      for (Files temp : list_file) {
				filename= temp.getFile();
				String template_type = temp.getTemplate_type();
				}			         
				      PreparedStatement ps = session.prepare("DELETE FROM datafile.data where act_name = activity_name and act_type = activity_type and file_type = template_type");
				      System.out.println(filename +"Deleted file as blob from cassandra"); 
				   
	      return Response.ok(ae).header("Access-Control-Allow-Origin", "*").build();   
	}	 
}
