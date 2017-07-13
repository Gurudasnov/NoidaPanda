package webService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.google.gson.Gson;

import dao.TemplateHelper;

@Path("/WebService")
public class TemplateRESTService
{
	@GET
	@Path("/CreateTemplate")
	@Produces("application/json")
	//URL: http://localhost:8080/NPORESTfulProject/rest/WebService/CreateTemplate
	//Example Input from web UI to REST Server (Provided by user - Not hardcoded)
	//	TemplateName="AlarmTemplate"
	//	CassandraTableNames="AlarmDetails,AlarmDetails,AlarmDetails,OperatorDetails,NetworkDetails"
	//	CassandraColumnNames="AlarmID,AlarmType,AlarmTime,ATT,2GNetwork"
	public String createTemplate(
			@QueryParam("TemplateName") String templateName,
			@QueryParam("CassandraTableNames") String cassandraTableNames,
			@QueryParam("CassandraColumnNames") String cassandraColumnNames)
	{
		String resultJSON = null;
		try
		{
			TemplateHelper tempHelper = new TemplateHelper();
			String resultGSON = tempHelper.createTable(templateName, cassandraTableNames, cassandraColumnNames);
			Gson gson = new Gson();
			resultJSON = gson.toJson(resultGSON);
		}
		catch (Exception ex)
		{
			resultJSON = "createTableResult : Failure (Error in template creation)";
		}
		return resultJSON;
	}

	@GET
	@Path("/RetrieveTemplate")
	@Produces("application/json")
	//URL: http://localhost:8080/NPORESTfulProject/rest/WebService/RetrieveTemplate
	//Example Input from web UI to REST Server (Provided by user - Not hardcoded)
	//	TemplateName="AlarmTemplate"
	public String retrieveTemplate(
			@QueryParam("TemplateName") String templateName)
	{
		String resultJSON = null;
		try
		{
			TemplateHelper tempHelper = new TemplateHelper();
			String resultGSON = tempHelper.retrieveTable(templateName);
			Gson gson = new Gson();
			resultJSON = gson.toJson(resultGSON);
		}
		catch (Exception ex)
		{
			resultJSON = "retrieveTableResult : Failure (Error in template retrieval)";
		}
		return resultJSON;
	}

	@GET
	@Path("/DeleteTemplate")
	@Produces("application/json")
	//URL: http://localhost:8080/NPORESTfulProject/rest/WebService/DeleteTemplate
	//Example Input from web UI to REST Server (Provided by user - Not hardcoded)
	//	TemplateName="AlarmTemplate"
	public String deleteTemplate(
			@QueryParam("TemplateName") String templateName)
	{
		String resultJSON = null;
		try
		{
			TemplateHelper tempHelper = new TemplateHelper();
			String resultGSON = tempHelper.deleteTable(templateName);
			Gson gson = new Gson();
			resultJSON = gson.toJson(resultGSON);
		}
		catch (Exception ex)
		{
			resultJSON = "deleteTableResult : Failure (Error in template deletion)";
		}
		return resultJSON;
	}
}