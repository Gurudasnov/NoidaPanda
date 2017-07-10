package webService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/ldapauthenticator")
public class LDAPRESTService
{
	@POST
	@Path("/authenticate")
	@Produces("application/json")
	//URL: http://localhost:8080/LDAPWebModule/ldap/ldapauthenticator/authenticate
	//Example Input from web UI to REST Server (Provided by user - Not hardcoded)
	//	userName (Ex: "ExUser")
	//	password (Ex: "ExUser")
	public String authenticate(@FormParam("userName") String userName, @FormParam("password") String password)
	{
		String returnStr = null;
		String LDAPServerIP = null;
		String LDAPServerPort = null;
		String LDAPRoot = null;
		String LDAPServerPassword = null;
		Hashtable<String, String> env = null;
		DirContext ctx = null;
		Properties config = new Properties();
		final String LDAPContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
		final String LDAPAuthMode = "simple";
		final String[] attrsToBeFetched = {"memberOf"};
		try
		{
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("LDAP.properties");
			config.load(in);
		}
		catch(FileNotFoundException e)
		{
			return "config file cannot be found: " + e.getMessage();
		}
		catch (IOException e)
		{
			return "Error in loading the configuration: " + e.getMessage();
		}
		LDAPServerIP = config.getProperty("LDAP_Server_IP");
		LDAPServerPort = config.getProperty("LDAP_Server_Port");
		LDAPRoot = config.getProperty("LDAP_Root");
		LDAPServerPassword = config.getProperty("LDAP_Server_Password");
		env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, LDAPContextFactory);
		env.put(Context.PROVIDER_URL, "ldap://" + LDAPServerIP + ":" + LDAPServerPort);
		env.put(Context.SECURITY_AUTHENTICATION, LDAPAuthMode);
		env.put(Context.SECURITY_PRINCIPAL, LDAPRoot);
		env.put(Context.SECURITY_CREDENTIALS, LDAPServerPassword);
		try
		{
			ctx = new InitialDirContext(env);
		}
		catch (NamingException e)
		{
			return "Error in initialising the LDAP context: " + e.getMessage();
		}
		SearchControls ctrls = new SearchControls();
		ctrls.setReturningAttributes(attrsToBeFetched);
		ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String filter = "(&(uid=" + userName + ")&(userPassword=" + password + "))";
		NamingEnumeration<SearchResult> answer = null;
		try
		{
			answer = ctx.search(LDAPRoot, filter, ctrls);
			if(answer != null && answer.hasMore())
			{
				returnStr = "Authentication Success for the user '" + userName + "'";
				while(answer.hasMoreElements())
				{
					SearchResult res = (SearchResult) answer.next();
					Attributes attrFetched = res.getAttributes();
					Attribute attrMember = attrFetched.get(attrsToBeFetched[0]);
					if(attrMember != null && attrMember.size() > 0)
					{
						Object objMember = attrMember.get();
						if(objMember != null && objMember instanceof String)
						{
							//cn=ldapgroup1,ou=groups,dc=maxcrc,dc=com
							String strMember = objMember.toString();
							String[] arrTokens = strMember.split(",");
							if(arrTokens != null && arrTokens.length > 0)
							{
								//arrTokens[0]="cn=ldapgroup1"
								int index = arrTokens[0].indexOf("=");
								if(index > 0)
								{
									String strGroup = arrTokens[0].substring(index + 1);
									returnStr = returnStr + " (User '" + userName + "' is associated with the LDAP Group '" + strGroup + "')";
								}
							}
						}
					}
				}
			}
			else
				returnStr = "Authentication Failure for the user '" + userName + "'";
		}
		catch (NamingException e)
		{
			return "Error in authenticating the user " + userName + " from the LDAP directory: " + e.getMessage();
		}
		try
		{
			if(ctx != null)
				ctx.close();
		}
		catch(NamingException e)
		{
			return "Error in closing the LDAP Context: " + e.getMessage();
		}
		return returnStr;
	}
}