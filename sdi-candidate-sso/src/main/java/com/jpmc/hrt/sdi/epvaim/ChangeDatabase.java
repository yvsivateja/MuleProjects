package com.jpmc.hrt.sdi.epvaim;

import org.apache.commons.lang3.StringUtils;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.api.transport.PropertyScope;

public class ChangeDatabase implements Callable {

	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		boolean success = false;
		MuleMessage message = eventContext.getMessage();
		String userName = message.getProperty("epvUserName", PropertyScope.SESSION);
		String password = message.getProperty("epvPassword", PropertyScope.SESSION);
		String dbHost = message.getInvocationProperty("dbHost", "");
		String dbPort = message.getInvocationProperty("dbPort", "");
		String dbInstance = message.getInvocationProperty("dbInstance", "");
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
		if(StringUtils.isEmpty(dbHost)){
			errorMessage += "dbHost is Empty \n";
			error=true;
		}
		if(StringUtils.isEmpty(dbPort)){
			errorMessage += "dbPort is Empty \n";
			error=true;
		}
		if(StringUtils.isEmpty(dbInstance)){
			errorMessage += "dbInstance is Empty \n";
			error=true;
		}
		if(error){
			throw new Exception("Error(s) in DB URL String \n"+errorMessage);
		}		
		org.enhydra.jdbc.standard.StandardDataSource ds = (org.enhydra.jdbc.standard.StandardDataSource) eventContext
				.getMuleContext().getRegistry().lookupObject("dataSource");
		//ds.setUrl("jdbc:oracle:thin:" + userName + "/" + password + "@" + dbHost + ":" + dbPort + "/" + dbInstance);
		ds.setUrl("jdbc:oracle:thin:obglobal_admin/ora_obglobal123@psin3p970-scan.svr.us.jpmchase.net:6135/EPI_DEV.CT.whem.jpmchase.net");
		ds.setUrl("jdbc:oracle:thin:obglobal_admin/ora_abglobal123@psin3p970-scan.svr.us.jpmchase.net:6135/EPI-DEV.CT.whem.jpmchase.net");
		success = true;
		return success;
	}
}