package rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/UserService")  
public class getFileFromReact {
	private static final String UPLOAD_DIRECTORY = "D:/UploadedFiles";
	private String failedResult = "";
    @POST  
    @Path("/upload")  
    @Consumes(MediaType.MULTIPART_FORM_DATA)  
    public Response uploadFile( 
    		@FormDataParam("file") InputStream uploadedInputStream,  
    		@FormDataParam("file") FormDataContentDisposition fileDetail)
    		{  
    	System.out.println("In Post Method.");
    		String fileName = fileDetail.getFileName();
             
            	 
            	 System.out.println("MultipartContent detected.");
            	 
   		      		File uploadDir = new File(UPLOAD_DIRECTORY);
   		      		if (!uploadDir.exists()) {
   		      		uploadDir.mkdir();
   		      		}
   		      		try {       
   		      			FileOutputStream out = new FileOutputStream(new File(UPLOAD_DIRECTORY));  
   		      			int read = 0;  
   		      			byte[] bytes = new byte[1024];  
   		      			out = new FileOutputStream(new File(UPLOAD_DIRECTORY));  
   		      			while ((read = uploadedInputStream.read(bytes)) != -1) {  
   		      			out.write(bytes, 0, read);  
   		      			}  
   		      			out.flush();  
   		      			out.close();
   		      			} catch(IOException iox){
   		      				iox.printStackTrace();
   		      				System.out.println("Exception1");
   		      			}
                    System.out.println("File Name: " + fileName);
		            try{      
                    if ("".equals(this.failedResult)) {
		        	System.out.println("Upload has been done successfully!");		           	        	
		        	return Response.status(200)  
		  		          .entity("{\"msg\": \"Upload has been done successfully!\"}")  
		  		          .build();		           
                    } 
                    else {
		        	return Response.status(200)  
			  		       .entity("<h2> The records failed to upload.</h2> ")
			  		       .build();	
                    } 
		            }catch (Exception ex){
		            	ex.printStackTrace();
		            	System.out.println("Exception2");
				    }
		        String output = "File successfully uploaded to : " + UPLOAD_DIRECTORY ; 
   	    		return Response.status(200).entity(output).build();  
    		}
}
