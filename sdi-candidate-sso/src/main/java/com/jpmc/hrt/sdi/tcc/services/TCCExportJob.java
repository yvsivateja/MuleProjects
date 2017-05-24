package com.jpmc.hrt.sdi.tcc.services;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "TCCExportJob")
public class TCCExportJob {
	private String ExportSQName;
	private String CFGTemplateName;
	private String CFGName;
	private String ResultPrefix;
	private String ResultExtension;
	private String Name;
	private String Status;
	private String UnixShellMaxMins;
	private String ImportAction;

	@XmlElement(name = "ExportSQName")
	public String getExportSQName() {
		return ExportSQName;
	}

	public void setExportSQName(String ExportSQName) {
		this.ExportSQName = ExportSQName;
	}

	public String getCFGTemplateName() {
		return CFGTemplateName;
	}

	public void setCFGTemplateName(String cFGTemplateName) {
		CFGTemplateName = cFGTemplateName;
	}

	public String getCFGName() {
		return CFGName;
	}

	public void setCFGName(String cFGName) {
		CFGName = cFGName;
	}

	public String getResultPrefix() {
		return ResultPrefix;
	}

	public void setResultPrefix(String resultPrefix) {
		ResultPrefix = resultPrefix;
	}

	public String getResultExtension() {
		return ResultExtension;
	}

	public void setResultExtension(String resultExtension) {
		ResultExtension = resultExtension;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getUnixShellMaxMins() {
		return UnixShellMaxMins;
	}

	public void setUnixShellMaxMins(String unixShellMaxMins) {
		UnixShellMaxMins = unixShellMaxMins;
	}

	public String getImportAction() {
		return ImportAction;
	}

	public void setImportAction(String importAction) {
		ImportAction = importAction;
	}

}
