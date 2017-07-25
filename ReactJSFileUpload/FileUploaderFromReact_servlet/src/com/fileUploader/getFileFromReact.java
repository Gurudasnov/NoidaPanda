package com.fileUploader;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class getFileFromReact
 */
public class getFileFromReact extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 //private static final String UPLOAD_DIRECTORY = "/usr/local/apache2/htdocs/firmware";
	  private static final String UPLOAD_DIRECTORY = "D:/temp/WEB_UPLOADS";
	  private String filePath = "";
	  private String failedResult = "";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public getFileFromReact() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		   try
		    {
		      System.out.println("In Post Method.");
		      if (!ServletFileUpload.isMultipartContent(request))
		      {
		        PrintWriter writer = response.getWriter();
		        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		        writer.println("{\"msg\": \"Request does not contain upload data\"}");
		        System.out.println("Request does not contain upload data.");
		        writer.flush();
		        return;
		      }
		      System.out.println("MultipartContent detected.");
		      DiskFileItemFactory factory = new DiskFileItemFactory();
		      factory.setSizeThreshold(3145728);
		      factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
		      ServletFileUpload upload = new ServletFileUpload(factory);
		      upload.setFileSizeMax(41943040L);
		      upload.setSizeMax(52428800L);
		      
		      File uploadDir = new File(UPLOAD_DIRECTORY);
		      if (!uploadDir.exists()) {
		        uploadDir.mkdir();
		      }
		      try
		      {
		        List formItems = upload.parseRequest(request);
		        Iterator iter = formItems.iterator();
		        while (iter.hasNext())
		        {
		          FileItem item = (FileItem)iter.next();
		          if (!item.isFormField())
		          {
		            String fileName = new File(item.getName()).getName();
		            System.out.println("File Name: " + fileName);
		            this.filePath = (UPLOAD_DIRECTORY + File.separator + fileName + new Date().getTime());
		            File storeFile = new File(this.filePath);
		           
		            item.write(storeFile);
		          }
		        }
		        if ("".equals(this.failedResult)) {
		        	response.setStatus(HttpServletResponse.SC_CREATED);
		        	PrintWriter writer = response.getWriter();
		            writer.println("{\"msg\": \"Upload has been done successfully!\"}");
		            System.out.println("Upload has been done successfully!");
		            writer.flush();
		            return;
		        } else {
		          request.setAttribute("message", "<h2>The below records failed to insert:</h2> <br/><table border=0><tr><td>  MAKE </td> <td> MODEL</td><td>   GT  </td><td>  LT</td>   <td>RESOURCE_PATH</td></tr>" + this.failedResult + "</table>");
		        }
		      }
		      catch (Exception ex)
		      {
		        request.setAttribute("message", "There was an error: " + ex.getMessage());
		      }
		    }
		    catch (Exception e)
		    {
		      System.out.println("error " + e);
		    }
		    PrintWriter writer = response.getWriter();
		    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		    
		    writer.println("{\"msg\": \"Failed to parse the file!\"}");
		    System.out.println("Failed to parse the file!");
		    writer.flush();
		    return;
	}

}
