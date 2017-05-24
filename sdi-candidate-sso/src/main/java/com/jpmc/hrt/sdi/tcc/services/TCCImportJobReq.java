package com.jpmc.hrt.sdi.tcc.services;

import org.springframework.beans.factory.annotation.Value;

public class TCCImportJobReq {
	
	@Value("${m_JobName_RC}")
	private String m_JobName;

	@Value("${m_TCCUnixShellMaxMins_RC}")
	private String m_TCCUnixShellMaxMins;

	@Value("${m_FQ_TCCScriptName_RC}")
	private String m_FQ_TCCScriptName;

	@Value("${m_FQ_RequestFile_RC}")
	private String m_FQ_RequestFile;

	@Value("${m_FQ_ConfigFile_RC}")
	private String m_FQ_ConfigFile;

	@Value("${m_FQ_ResultFile_RC}")
	private String m_FQ_ResultFile;

	@Value("${m_TCCKillProcessShellScriptFN_RC}")
	private String m_TCCKillProcessShellScriptFN;
	
	@Value("${m_FQ_CharSet}")
	private String m_FQ_CharSet;
	
	public void process() {

		TCCServiceProcessorImpl tccService = new TCCServiceProcessorImpl();
		
		String m_TCCSendEmptyResult = "";
		String m_TCCEmptyResultDefinition = "";

		try {
			tccService.processTCCService(m_JobName, m_TCCUnixShellMaxMins, m_FQ_TCCScriptName, m_FQ_RequestFile,
					m_FQ_ConfigFile, m_FQ_ResultFile, m_TCCKillProcessShellScriptFN, m_TCCSendEmptyResult,
					m_TCCEmptyResultDefinition, m_FQ_CharSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
