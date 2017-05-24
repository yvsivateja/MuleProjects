package com.jpmc.hrt.sdi.tcc.services;

interface TCCServiceProcessor {

	public String processTCCService(final String m_ExportJobName, final String m_TCCUnixShellMaxMins,
			final String m_FQ_TCCScriptName, final String m_FQ_RequestFile, final String m_FQ_ConfigFile,
			final String m_FQ_ResultFile, final String m_TCCKillProcessShellScriptFN, final String m_TCCSendEmptyResult,
			final String m_TCCEmptyResultDefinition, final String m_FQ_CharSet) throws Exception;

	public boolean executeTCC(final String shellScript, final String configFile, final String requestFile,
			final String resultFile, final String minToRunShell) throws Exception;

	public String executeSimpleCommand(final String command) throws Exception;

	public String searchAndReplace(String inputStr, String fromValue, String toValue) throws Exception;

}
