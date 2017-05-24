package com.jpmc.hrt.sdi.tcc.services;

import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class TCCImportJobCP implements Callable {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${m_TCCUnixShellMaxMins}")
	private String m_TCCUnixShellMaxMins;

	@Value("${m_FQ_TCCScriptName}")
	private String m_FQ_TCCScriptName;

	@Value("${m_FQ_ConfigFile}")
	private String m_FQ_ConfigFile;

	@Value("${m_TCCKillProcessShellScriptFN}")
	private String m_TCCKillProcessShellScriptFN;

	@Value("${m_FQ_CharSet}")
	private String m_FQ_CharSet;

	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		TCCServiceProcessorImpl tccService = new TCCServiceProcessorImpl();

		String m_TCCSendEmptyResult = "";
		String m_TCCEmptyResultDefinition = "";
		String jobName = eventContext.getMessage().getInvocationProperty("job_name");
		String m_FQ_CSVDataFile = eventContext.getMessage().getInvocationProperty("csvFileOutputDirectory");
		String resultFile = m_FQ_CSVDataFile + "\\" + jobName + ".xml";
		String requestFile = m_FQ_CSVDataFile + "\\" + jobName + ".csv";
		try {
			return tccService.processTCCService(jobName, m_TCCUnixShellMaxMins, m_FQ_TCCScriptName, requestFile,
					m_FQ_ConfigFile, resultFile, m_TCCKillProcessShellScriptFN, m_TCCSendEmptyResult,
					m_TCCEmptyResultDefinition, m_FQ_CharSet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return eventContext.getMessage().getPayload();
	}

}
