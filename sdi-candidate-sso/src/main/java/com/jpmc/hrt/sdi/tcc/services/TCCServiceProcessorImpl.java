package com.jpmc.hrt.sdi.tcc.services;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.oro.text.regex.PatternMatcher;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.PatternCompiler;
import org.apache.oro.text.regex.Substitution;
import org.apache.oro.text.regex.Util;
import org.apache.oro.text.regex.StringSubstitution;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.util.GregorianCalendar;
import java.text.DecimalFormat;
import java.io.File;

public class TCCServiceProcessorImpl implements TCCServiceProcessor {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Used to make call to executeTCC function.
	 * 
	 * @param m_JobName
	 * @param m_TCCUnixShellMaxMins
	 * @param m_FQ_TCCScriptName
	 * @param m_FQ_RequestFile
	 * @param m_FQ_ConfigFile
	 * @param m_FQ_ResultFile
	 * @param m_TCCKillProcessShellScriptFN
	 * @param m_TCCSendEmptyResult
	 * @param m_TCCEmptyResultDefinition
	 * @param m_FQ_CharSet
	 * @throws Exception
	 * 
	 * 
	 */
	@Override
	public String processTCCService(final String m_JobName, final String m_TCCUnixShellMaxMins,
			final String m_FQ_TCCScriptName, final String m_FQ_RequestFile, final String m_FQ_ConfigFile,
			final String m_FQ_ResultFile, final String m_TCCKillProcessShellScriptFN, final String m_TCCSendEmptyResult,
			final String m_TCCEmptyResultDefinition, final String m_FQ_CharSet) throws Exception {
		String m_errorMessage = "";
		String m_TCCFinalResult = "";

		logger.info("TSE_TCC_SERVICE PROCESS IS STARTING!");
		logger.info("m_TCCFinalResult - " + m_TCCFinalResult.length());

		String simpleCmd = "";
		String m_Result = "";

		// Run the TCC Job
		// Keep the Date/Time current so we can easily see how long the job
		// takes in the log.
		/*
		 * logger.info("## m_FQ_RequestFile to get m_TCCUnixIDName : " +
		 * m_FQ_RequestFile);
		 */
		/*
		 * String m_TCCUnixIDName = m_FQ_RequestFile.substring(
		 * m_FQ_RequestFile.lastIndexOf('/') + 1,
		 * m_FQ_RequestFile.lastIndexOf('.'));
		 * 
		 * logger.info("TCC Unix Shell ID Name is: " + m_TCCUnixIDName);
		 */

		// Keep track of the time it takes to run
		long m_TCCStartTime = System.currentTimeMillis();
		logger.info("calling executeTCC..");
		boolean m_TCCStatusResult = executeTCC(m_FQ_TCCScriptName, m_FQ_ConfigFile, m_FQ_RequestFile, m_FQ_ResultFile,
				m_TCCUnixShellMaxMins);
		logger.info("m_TCCStatusResult >>>> " + m_TCCStatusResult);
		long m_TCCSecondsRunning = (System.currentTimeMillis() - m_TCCStartTime) / 1000L;

		if (!m_TCCStatusResult) {
			m_errorMessage = "The TCC Shell Process Thread exceeded its allowed runtime limit, it's being interrupted!\n\nJob Name: "
					+ m_JobName
					+ " -- This is NOT fatal and the process is continuing...\n(NOTE: For your convienience the Unix TCC Process was terminated and you do not have to check with 'ps -ef')";
			logger.warn(m_errorMessage);
			m_TCCFinalResult = "TCC Unix Shell Runtime Exceeded";
			// Lets KILL the TCC Process that was interrupted.
			logger.info("Setting TCC Kill Process script to 'execute' rights.");
			simpleCmd = "chmod +x " + m_TCCKillProcessShellScriptFN;

			if ((m_Result = executeSimpleCommand(simpleCmd)) != null) {
				logger.info(m_Result);
			}
		} else {
			// Print how long it took to run the TCC Job.
			long seconds = m_TCCSecondsRunning % 60;
			m_TCCSecondsRunning = (m_TCCSecondsRunning - seconds) / 60;
			long minutes = m_TCCSecondsRunning % 60;
			m_TCCSecondsRunning = (m_TCCSecondsRunning - minutes) / 60;
			long hours = m_TCCSecondsRunning % 24;
			m_TCCSecondsRunning = (m_TCCSecondsRunning - hours) / 24;
			long days = m_TCCSecondsRunning % 7;
			long weeks = (m_TCCSecondsRunning - days) / 7;

			String m_TCCExecutionTime = (weeks != 0L ? weeks + (weeks == 1 ? " Week, " : " Weeks, ") : "")
					+ (days != 0L ? days + (days == 1 ? " Day, " : " Days, ") : "")
					+ (hours != 0L ? hours + (hours == 1 ? " Hour, " : " Hours, ") : "")
					+ (minutes != 0L ? minutes + (minutes == 1 ? " Minute" : " Minutes") : "")
					+ (seconds != 0L && (minutes > 0 || hours > 0 || days > 0 || weeks > 0) ? " and " : "")
					+ (seconds != 0L ? seconds + (seconds == 1 ? " Second" : " Seconds") : "");

			logger.info("TCC Execution time for job is " + m_TCCExecutionTime);
			logger.info("The TCC Shell finished normally.  Let's see if there is a Result file...");

			try {
				if (doesFileExist(m_FQ_ResultFile)) {
					logger.info("##### Result File Exists #####");

					String m_TCCResultFileString = "";
					m_TCCResultFileString = new String(loadFileInfo(m_FQ_ResultFile), m_FQ_CharSet); // Chars-et

					logger.info("m_TCCResultFileString :- " + m_TCCResultFileString.length());

					if (m_TCCSendEmptyResult != null && m_TCCSendEmptyResult != "") {
						logger.info("<<<< SendEmptyResult falg exists >>>>");
						if (m_TCCEmptyResultDefinition != null && m_TCCEmptyResultDefinition != "") {
							logger.info("<<<< EmptyResultDefinition exists >>>>");
							if (m_TCCResultFileString.trim().indexOf(m_TCCEmptyResultDefinition) != -1) {
								logger.info("@@@@ Result file matches with EmptyResultDefinition @@@@");
								System.out.println("@@@@ Result file matches with EmptyResultDefinition @@@@");
								m_TCCFinalResult = "Success";
							}
						} else {
							logger.info("<<<< User opted for empty result[SendEmptyResult falg has value] >>>>");
							m_TCCFinalResult = "Success";
						}
					} else {
						logger.info(
								"%% User not opted for empty result[SendEmptyResult falg empty]; Checking if file is empty below %%");
						if (!isResultFileEmpty(m_TCCResultFileString.trim())) {
							logger.info("**** ResultFile is not empty, setting success. ****");
							m_TCCFinalResult = "Success";
						}
					}
				} else {
					logger.info("!!!! Result File does not Exists !!!!");
					m_TCCFinalResult = "TCC Result file does not exist.";
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
		}
		logger.info("TSE_TCC_SERVICE PROCESS IS COMPLETE!");
		logger.info("processTCCService() sending reply back.  Reply being sent is: " + m_TCCFinalResult);

		return m_TCCFinalResult;

	}

	/**
	 * Used to execute shell script.
	 * 
	 * @param shellScript
	 * @param configFile
	 * @param requestFile
	 * @param resultFile
	 * @param minToRunShell
	 * @throws Exception
	 * 
	 */
	@Override
	public boolean executeTCC(final String shellScript, final String configFile, final String requestFile,
			final String resultFile, final String minToRunShell) throws Exception {
		logger.info("Inside executeTCC..");
		boolean processComplete = true;
		long countDownTimer = Long.parseLong(minToRunShell) * 60L * 1000L;
		long sleepTimeBeforeChecking = 15L * 1000L;
		ExecuteTCCUnixShell unixTCCShell = new ExecuteTCCUnixShell(shellScript, configFile, requestFile, resultFile);
		unixTCCShell.start();
		try {
			boolean m_PrintedOnce = false;
			while (!unixTCCShell.getIsShellFinished() && countDownTimer > 0) {
				Thread.sleep(sleepTimeBeforeChecking);
				countDownTimer -= sleepTimeBeforeChecking;
				if (!m_PrintedOnce) {
					logger.info("ExecuteTCC :: Waiting for shell process to complete. (Max wait time is "
							+ minToRunShell + " minutes.)");
					m_PrintedOnce = true;
				}
			}
			if (!unixTCCShell.getIsShellFinished()) {
				// The Unix shell process exceeded it's allowed runtime limit.
				// It's time to kill it.
				unixTCCShell.interrupt();
				logger.warn(
						"ExecuteTCC :: The Unix shell process exceeded it's allowed runtime limit. Interrupt signal was sent");
				processComplete = false;
			}
		} catch (Exception e) {
			logger.error("ExecuteTCC :: Exception=" + e.toString());
			throw new Exception(e.getCause());
		}
		return processComplete;
	}

	private class ExecuteTCCUnixShell extends Thread {

		private String m_TCCStartShellScriptFN;
		private String m_TCCConfigFile;
		private String m_TCCRequestFile;
		private String m_TCCResultFile;
		private boolean IsShellFinished;
		private Runtime m_ShellRuntime = null;

		public ExecuteTCCUnixShell(String sTCCStartShellScriptFN, String sTCCConfigFile, String sTCCRequestFile,
				String sTCCResultFile) {
			logger.info("Inside ExecuteTCCUnixShell..");
			m_TCCStartShellScriptFN = sTCCStartShellScriptFN;
			m_TCCConfigFile = sTCCConfigFile;
			m_TCCRequestFile = sTCCRequestFile;
			m_TCCResultFile = sTCCResultFile;
			IsShellFinished = false;
		}

		public boolean getIsShellFinished() {
			return IsShellFinished;
		}

		public void run() {
			// "ExecuteTCCUnixShell :: Get the Runtime object for the JVM." );
			m_ShellRuntime = java.lang.Runtime.getRuntime();

			String[] cmd = new String[4];
			String m_Result = null;
			try {
				// Now execute the TCC shell script.
				cmd[0] = m_TCCStartShellScriptFN;
				cmd[1] = m_TCCConfigFile;
				cmd[2] = m_TCCRequestFile;
				cmd[3] = m_TCCResultFile;

				logger.info("\nScript:  " + cmd[0] + "\nConfig:  " + cmd[1] + "\nRequest: " + cmd[2] + "\nResult:  "
						+ cmd[3]);
				logger.info("ExecuteTCCUnixShell :: Executing Taleo Connect Client shell script.");

				if ((m_Result = ExecuteCommand(cmd)) != null) {
					logger.info("m_Result from ExecuteCommand >>>> " + m_Result);
				}
			} catch (Exception e) {
				logger.error("ExecuteTCCUnixShell :: Run Thread Exception: " + e.toString());
			}

			IsShellFinished = true;
		}

		private String ExecuteCommand(String[] command) {
			logger.info("Inside ExecuteCommand..");
			String outputResult = null;
			String errorResult = null;
			String m_FinalResult = null;
			;
			Process m_ShellProcess = null;
			StreamProcessor errorStream = null;
			StreamProcessor outputStream = null;
			try {
				m_ShellProcess = m_ShellRuntime.exec(command);
				outputStream = new StreamProcessor("STDOUT", m_ShellProcess.getInputStream());
				errorStream = new StreamProcessor("STDERR", m_ShellProcess.getErrorStream());

				outputStream.start();
				errorStream.start();

				m_ShellProcess.waitFor();
				;
				outputStream.join();
				errorStream.join();

				outputResult = outputStream.getResult();
				errorResult = errorStream.getResult();

				m_FinalResult = "Command ==> " + command[0] + " " + command[1] + " " + command[2] + " " + command[3]
						+ "\n";
				if (outputResult.length() > 0) {
					m_FinalResult += "\n" + outputResult;
				}
				if (errorResult.length() > 0) {
					m_FinalResult += "\n" + errorResult;
				}
				m_FinalResult += "\nShell completed with exit value: " + String.valueOf(m_ShellProcess.exitValue())
						+ "\n";

			} catch (InterruptedException e) {
				logger.error("ExecuteCommand :: Interrupt Exception :: destroying the shell process.");
				try {
					outputStream.join();
					errorStream.join();
					;
					outputResult = outputStream.getResult();
					errorResult = errorStream.getResult();
				} catch (InterruptedException e1) {
				}
			} catch (Exception e) {
				logger.error("ExecuteCommand :: Run Thread Exception: " + e.toString());
				try {
					outputStream.join();
					errorStream.join();

					outputResult = outputStream.getResult();
					errorResult = errorStream.getResult();
				} catch (InterruptedException e1) {
				}
			} finally {
				if (m_ShellProcess != null) {
					m_ShellProcess.destroy();
					m_ShellProcess = null;
				}
			}
			return m_FinalResult;
		}

	}

	private class StreamProcessor extends Thread {

		java.io.InputStream is;
		String streamType;
		String logResult;

		StreamProcessor(String streamType, java.io.InputStream is) {
			this.is = is;
			this.streamType = streamType;
		}

		public String getResult() {
			return logResult;
		}

		public void run() {
			StringBuffer loggerBuffer = new StringBuffer();
			java.io.InputStreamReader isr = null;
			java.io.BufferedReader br = null;
			try {
				isr = new java.io.InputStreamReader(is);
				br = new java.io.BufferedReader(isr);
				String line = null;

				while ((line = br.readLine()) != null) {
					// Discard STDERR output (eched in the
					// TaleoConnectClient.log) -- No need to log it here
					// Log ALL other streams

					if (!streamType.equalsIgnoreCase("STDERR")) {
						loggerBuffer.append(streamType + "> " + line + "\n");
					}
				}
			} catch (Exception e) {
				logger.error("Exception occurred: " + e.toString());
				e.printStackTrace();
			} finally {
				logResult = loggerBuffer.toString();
				try {
					if (br != null) {
						br.close();
					}
					if (isr != null) {
						isr.close();
					}
				} catch (Exception e) {
				}
			}
		}

	}

	/**
	 * 
	 */
	@Override
	public String executeSimpleCommand(String command) {
		String outputResult = null;
		String errorResult = null;
		String m_FinalResult = null;

		Runtime m_ShellRuntime = java.lang.Runtime.getRuntime();
		Process m_ShellProcess = null;
		StreamProcessorAll errorStream = null;
		StreamProcessorAll outputStream = null;
		try {
			m_ShellProcess = m_ShellRuntime.exec(command);
			outputStream = new StreamProcessorAll("STDOUT", m_ShellProcess.getInputStream());
			errorStream = new StreamProcessorAll("STDERR", m_ShellProcess.getErrorStream());

			outputStream.start();
			errorStream.start();

			m_ShellProcess.waitFor();

			outputStream.join();
			errorStream.join();

			outputResult = outputStream.getResult();
			errorResult = errorStream.getResult();

			m_FinalResult = "Command ==> " + command + "\n";
			if (outputResult.length() > 0) {
				m_FinalResult += "\n" + outputResult;
			}
			if (errorResult.length() > 0) {
				m_FinalResult += "\n" + errorResult;
			}
			m_FinalResult += "\nShell completed with exit value: " + String.valueOf(m_ShellProcess.exitValue()) + "\n";

		} catch (InterruptedException e) {

			logger.error("executeSimpleCommand :: Interrupt Exception :: destroying the shell process.");
			try {
				outputStream.join();
				errorStream.join();

				outputResult = outputStream.getResult();
				errorResult = errorStream.getResult();
			} catch (InterruptedException e1) {
			}
		} catch (Exception e) {
			logger.error("executeSimpleCommand :: Run Thread Exception: " + e.toString());
			try {
				outputStream.join();
				errorStream.join();

				outputResult = outputStream.getResult();
				errorResult = errorStream.getResult();
			} catch (InterruptedException e1) {
			}
		} finally {
			if (m_ShellProcess != null) {
				m_ShellProcess.destroy();
				m_ShellProcess = null;
			}
		}
		return m_FinalResult;
	}

	private class StreamProcessorAll extends Thread {

		java.io.InputStream is;
		String streamType;
		String logResult;

		StreamProcessorAll(String streamType, java.io.InputStream is) {
			this.is = is;
			this.streamType = streamType;
		}

		public String getResult() {
			return logResult;
		}

		public void run() {
			StringBuffer loggerBuffer = new StringBuffer();
			java.io.InputStreamReader isr = null;
			java.io.BufferedReader br = null;
			try {
				isr = new java.io.InputStreamReader(is);
				br = new java.io.BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					loggerBuffer.append(streamType + "> " + line + "\n");
				}
			} catch (Exception e) {
				logger.error("Exception occurred: " + e.toString());
				e.printStackTrace();
			} finally {
				logResult = loggerBuffer.toString();
				try {
					if (br != null) {
						br.close();
					}
					if (isr != null) {
						isr.close();
					}
				} catch (Exception e) {
				}
			}
		}

	}

	/**
	 * Checking whether file is available in specified folder or not.
	 * 
	 * @param filePath
	 * 
	 * @return
	 * 
	 * @throws java.io.IOException
	 */
	private boolean doesFileExist(final String filePath) throws java.io.IOException {
		File file = new File(filePath);
		if (file.exists()) {

			logger.info("*************************************" + file);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Utility method for loading a file located at filePath.
	 * 
	 * @param filePath
	 * @return
	 * @throws java.io.IOException
	 */
	public byte[] loadFileInfo(final String filePath) throws java.io.IOException {

		logger.info("*******************FILE PATH******" + filePath);
		logger.info("Entering in to loadFileInfo");
		File file = new File(filePath);
		byte[] fileData = new byte[(int) file.length()];
		java.io.InputStream inputStream = new java.io.FileInputStream(file);
		inputStream.read(fileData);
		inputStream.close();
		logger.info("Returning in to loadFileInfo" + file.length() + fileData.toString());
		return fileData;
	}

	/**
	 * 
	 * @param resultFileAsString
	 * @return
	 * @throws IOException
	 */
	private boolean isResultFileEmpty(final String resultFileAsString) throws IOException {
		boolean isResultFileEmpty = false;

		logger.info("Checking if ResultFile is empty..");

		if ("".equals(resultFileAsString) && resultFileAsString.length() == 0) {
			logger.info("**NOTE**:  The TCC Result file is empty.");
			isResultFileEmpty = true;
		}
		logger.info("isResultFileEmpty: " + isResultFileEmpty);

		return isResultFileEmpty;

	}

	/**
	 * Used to replace some string in Template file.
	 * 
	 * 
	 * @param inputStr
	 *            *
	 * @param fromValue
	 *            *
	 * @param tovalue.
	 * 
	 */
	//
	public String searchAndReplace(String inputStr, final String fromValue, final String toValue) {
		logger.info("Entering into searchAndReplace");
		PatternMatcher patternMatcher = new org.apache.oro.text.regex.Perl5Matcher();
		Pattern pattern = null;
		PatternCompiler patternCompiler = new org.apache.oro.text.regex.Perl5Compiler();
		Substitution substitution = null;
		int searchLimit = Util.SUBSTITUTE_ALL;
		String[] searchValArr = new String[1];
		searchValArr[0] = fromValue;
		String[] replaceValArr = new String[1];
		replaceValArr[0] = toValue;
		for (int i = 0; i < searchValArr.length; i++) {
			try {
				pattern = patternCompiler.compile(searchValArr[i]);
				substitution = new StringSubstitution(replaceValArr[i]);
				inputStr = Util.substitute(patternMatcher, pattern, substitution, inputStr, searchLimit);
			} catch (Exception e) {
				logger.error("Search and Replace has failed.  Exception=" + e.toString());
			}
		}
		logger.info("Returning from searchAndReplace");
		return inputStr;
	}

	/**
	 * Calculate Standard/Daylight settings.
	 * 
	 * @param date
	 * 
	 * 
	 */
	public String calculateTimeOffset(final String date) throws Exception {
		logger.info("Entering in to calculateTimeOffset");
		String timeOffset_text = null;
		// Get DST offset
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));
		int hour = Integer.parseInt(date.substring(11, 13));
		int min = Integer.parseInt(date.substring(14, 16));
		int sec = Integer.parseInt(date.substring(17, 19));
		// Create a calendar from this date
		GregorianCalendar calDate = new GregorianCalendar(year, month, day, hour, min, sec);
		Integer timeOffset = new Integer((calDate.get(15) + calDate.get(16)) / 3600000);
		DecimalFormat two_digit_int_fmt = new DecimalFormat("00");
		timeOffset_text = two_digit_int_fmt.format(timeOffset).trim() + ":00";
		if (timeOffset.intValue() >= 0) {
			timeOffset_text = "+" + timeOffset_text;
			logger.info("Calculated time offset: " + timeOffset_text);
		}
		logger.info("Returning from calculateTimeOffset");
		return timeOffset_text;
	}

	/**
	 * Writes the specified string data in to specified file at filepath
	 * 
	 * @param filePath
	 * @param data
	 * @throws java.io.IOException
	 * 
	 */
	public void writeFileInfo(final String filePath, final String data) throws java.io.IOException {
		logger.info("Entreing in to writeFileInfo");
		FileOutputStream fos = new FileOutputStream(filePath);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		bos.write(data.getBytes());
		bos.flush();
		bos.close();
		fos.close();
		logger.info("Retruning from writeFileInfo");
	}

	/**
	 * Used to delete file from the directory.
	 * 
	 * @param m_FQ_File
	 * 
	 */
	public void deleteFile(final String m_FQ_File) {
		logger.info("Entering in to deleteFile");
		File file = new File(m_FQ_File);
		boolean m_FileDeleteStatus = file.delete();
		logger.info("Deleted file: " + m_FQ_File + ", Status: " + m_FileDeleteStatus);
		logger.info("Returning in to deleteFile");
	}

}
