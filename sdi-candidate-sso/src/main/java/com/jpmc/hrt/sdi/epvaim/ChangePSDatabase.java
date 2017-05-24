package com.jpmc.hrt.sdi.epvaim;

import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.api.transport.PropertyScope;

public class ChangePSDatabase implements Callable {

	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		boolean success = false;
		MuleMessage message = eventContext.getMessage();
		String userName = message.getProperty("epvUserName", PropertyScope.SESSION);
		String password = message.getProperty("epvPassword", PropertyScope.SESSION);
		String dbCurl = message.getInvocationProperty("dbCurl", "");
		String errorMessage="";
		boolean error=false;
		if(StringUtils.isEmpty(userName)){
			errorMessage += "Username is Empty \n";
			error=true;
		}
		if(StringUtils.isEmpty(password)){
			errorMessage += "Password is Empty \n";
			error=true;
		}
		if(StringUtils.isEmpty(dbCurl)){
			errorMessage += "dbCurl is Empty \n";
			error=true;
		}
		if(error){
			throw new Exception("Error(s) in DB URL Sring \n"+errorMessage);
		}		
		org.enhydra.jdbc.standard.StandardDataSource ds = (org.enhydra.jdbc.standard.StandardDataSource) eventContext
				.getMuleContext().getRegistry().lookupObject("dataSource");
		dbCurl=dbCurl.replace("username", userName).replace("{password}", password);
		ds.setUrl(dbCurl);
		success = true;
		return success;
	}
}