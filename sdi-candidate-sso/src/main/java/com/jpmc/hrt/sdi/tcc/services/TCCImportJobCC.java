package com.jpmc.hrt.sdi.tcc.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class TCCImportJobCC {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${m_JobName_CC}")
	private String m_JobName;

	@Value("${m_TCCUnixShellMaxMins_CC}")
	private String m_TCCUnixShellMaxMins;

	@Value("${m_FQ_TCCScriptName_CC}")
	private String m_FQ_TCCScriptName;

	@Value("${m_FQ_RequestFile_CC}")
	private String m_FQ_RequestFile;

	@Value("${m_FQ_ConfigFile_CC}")
	private String m_FQ_ConfigFile;

	@Value("${m_FQ_ResultFile_CC}")
	private String m_FQ_ResultFile;

	@Value("${m_TCCKillProcessShellScriptFN_CC}")
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
			e.printStackTrace();
		}
	}

}
