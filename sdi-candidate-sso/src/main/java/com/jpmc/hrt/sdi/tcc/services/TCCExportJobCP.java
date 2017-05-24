package com.jpmc.hrt.sdi.tcc.services;

import org.springframework.beans.factory.annotation.Value;

public class TCCExportJobCP {
	
	@Value("${m_JobName_RE}")
	private String m_JobName;

	@Value("${m_TCCUnixShellMaxMins_RE}")
	private String m_TCCUnixShellMaxMins;

	@Value("${m_FQ_TCCScriptName_RE}")
	private String m_FQ_TCCScriptName;

	@Value("${m_FQ_RequestFile_RE}")
	private String m_FQ_RequestFile;

	@Value("${m_FQ_ConfigFile_RE}")
	private String m_FQ_ConfigFile;

	@Value("${m_FQ_ResultFile_RE}")
	private String m_FQ_ResultFile;

	@Value("${m_TCCKillProcessShellScriptFN_RE}")
	private String m_TCCKillProcessShellScriptFN;
	
	@Value("${m_FQ_CharSet}")
	private String m_FQ_CharSet;
	
	//todo: use properties
	public void process() {
		TCCServiceProcessorImpl tccService = new TCCServiceProcessorImpl();
		
		String m_TCCSendEmptyResult = "";
		String m_TCCEmptyResultDefinition = "";
		try {
			tccService.processTCCService(m_JobName, m_TCCUnixShellMaxMins, m_FQ_TCCScriptName, m_FQ_RequestFile,
					m_FQ_ConfigFile, m_FQ_ResultFile, m_TCCKillProcessShellScriptFN, m_TCCSendEmptyResult,
					m_TCCEmptyResultDefinition, m_FQ_CharSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
