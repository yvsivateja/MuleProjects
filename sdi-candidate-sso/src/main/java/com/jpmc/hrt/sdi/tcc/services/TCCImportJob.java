package com.jpmc.hrt.sdi.tcc.services;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TCCImportJob")
public class TCCImportJob {
	private String ImportRequestName;
	private String ImportCSVName;
	private String CFGTemplateName;
	private String CFGName;
	private String ResultPrefix;
	private String ResultExtension;
	private String Name;
	private String Status;
	private String UnixShellMaxMins;
	private String ImportAction;

	@XmlAttribute(name = "Name")
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	@XmlAttribute(name = "Status")
	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	@XmlAttribute(name = "UnixShellMaxMins")
	public String getUnixShellMaxMins() {
		return UnixShellMaxMins;
	}

	public void setUnixShellMaxMins(String unixShellMaxMins) {
		UnixShellMaxMins = unixShellMaxMins;
	}

	@XmlAttribute(name = "ImportAction")
	public String getImportAction() {
		return ImportAction;
	}

	public void setImportAction(String importAction) {
		ImportAction = importAction;
	}

	@XmlElement(name = "ImportRequestName")
	public String getImportRequestName() {
		return ImportRequestName;
	}

	public void setImportRequestName(String importRequestName) {
		ImportRequestName = importRequestName;
	}

	@XmlElement(name = "ImportCSVName")
	public String getImportCSVName() {
		return ImportCSVName;
	}

	public void setImportCSVName(String importCSVName) {
		ImportCSVName = importCSVName;
	}

	@XmlElement(name = "CFGTemplateName")
	public String getCFGTemplateName() {
		return CFGTemplateName;
	}

	public void setCFGTemplateName(String cFGTemplateName) {
		CFGTemplateName = cFGTemplateName;
	}

	@XmlElement(name = "CFGName")
	public String getCFGName() {
		return CFGName;
	}

	public void setCFGName(String cFGName) {
		CFGName = cFGName;
	}

	@XmlElement(name = "ResultPrefix")
	public String getResultPrefix() {
		return ResultPrefix;
	}

	public void setResultPrefix(String resultPrefix) {
		ResultPrefix = resultPrefix;
	}

	@XmlElement(name = "ResultExtension")
	public String getResultExtension() {
		return ResultExtension;
	}

	public void setResultExtension(String resultExtension) {
		ResultExtension = resultExtension;
	}

}