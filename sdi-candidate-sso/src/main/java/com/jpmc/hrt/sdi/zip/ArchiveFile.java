package com.jpmc.hrt.sdi.zip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;
import org.slf4j.LoggerFactory;

public class ArchiveFile implements Callable {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ArchiveFile.class);

	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		Properties propJCTaleoUpdater = loadProperties();
		String m_TCCJobName = eventContext.getMessage().getInvocationProperty("job_name");
		String m_OBGAbsolutePath = propJCTaleoUpdater.getProperty("OBGAbsolutePath", "").trim();
		String m_FQ_CSVDataFile = m_OBGAbsolutePath
				+ propJCTaleoUpdater.getProperty(m_TCCJobName + "_CSVOutputDir", "").trim() + m_TCCJobName + ".csv";
		String m_ArchiveDirectory = m_OBGAbsolutePath
				+ propJCTaleoUpdater.getProperty(m_TCCJobName + "_ArchiveDirectory", "").trim();
		String m_ArchiveZipFilePrefix = propJCTaleoUpdater.getProperty(m_TCCJobName + "ArchiveZipFilePrefix", "")
				.trim();
		int m_NumberOfDaysToKeepArchives = Integer
				.parseInt(propJCTaleoUpdater.getProperty(m_TCCJobName + "NumberOfDaysToKeepArchives", "21").trim());
		SimpleDateFormat archiveDateFormatter = new SimpleDateFormat("MMddyyyy");
		String m_ArchiveFileDate = archiveDateFormatter.format(new java.util.Date(System.currentTimeMillis()));
		String m_FQ_ArchiveFile = m_ArchiveDirectory + m_ArchiveZipFilePrefix + m_TCCJobName + "_" + m_ArchiveFileDate
				+ ".zip";
		List<String> m_JobFileList = new ArrayList<String>();
		m_JobFileList.add(m_FQ_CSVDataFile);
		// m_JobFileList.add( m_FQ_TCCResultFile );
		createArchive(m_FQ_ArchiveFile, m_JobFileList);
		readZipFileToBytes(m_FQ_ArchiveFile, eventContext);
		removeOldArchives(m_NumberOfDaysToKeepArchives, m_ArchiveDirectory, m_ArchiveZipFilePrefix + m_TCCJobName);
		for (int index = 0; index < m_JobFileList.size(); index++) {
			try {
				boolean bFileDeleteStatus = (new File((String) m_JobFileList.get(index))).delete();
				logger.info("Deleted file: " + m_JobFileList.get(index) + ", Status: " + bFileDeleteStatus);
			} catch (Exception e) {
				logger.error("An exception has occurred while deleting a file (" + m_JobFileList.get(index)
						+ ")...  Ignoring...: " + e.toString());
			}
		}
		return eventContext.getMessage().getPayload();
	}

	private void createArchive(String zipFilename, List<String> jobFileList) throws Exception {
		java.util.zip.ZipOutputStream zipOutputStream = null;
		zipOutputStream = new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(zipFilename));

		byte[] m_ZipBuf = new byte[2048];
		// Lets add the CSV Files first...
		for (int i = 0; i < jobFileList.size(); i++) {
			;
			java.io.FileInputStream m_SrcIN = new java.io.FileInputStream((String) jobFileList.get(i));
			// Add ZIP entry to output stream.
			zipOutputStream.putNextEntry(new java.util.zip.ZipEntry(
					((String) jobFileList.get(i)).substring(((String) jobFileList.get(i)).lastIndexOf('/') + 1)));
			// Transfer bytes from the file to the ZIP file
			int len;
			while ((len = m_SrcIN.read(m_ZipBuf)) > 0) {
				zipOutputStream.write(m_ZipBuf, 0, len);
			}
			// Complete the entry
			zipOutputStream.closeEntry();
			m_SrcIN.close();
		}
		zipOutputStream.close();

	}

	public void removeOldArchives(int m_NumberOfDaysToKeepArchives, String m_ArchiveDirectory, String m_PrefixFilter) {
		// NowDate is the current time PLUS 5 Minutes (so we won't miss a file
		// if it's +/- a small amount when created)
		long m_NowDate = System.currentTimeMillis() + 300L * 1000L;
		long m_MillisecondsInDay = 86400L * 1000L;
		long m_RetentionDate = m_NowDate - m_MillisecondsInDay * m_NumberOfDaysToKeepArchives;
		java.io.File file;
		logger.info(
				"Scanning Archives for deletion...  Retention period is " + m_NumberOfDaysToKeepArchives + " days.");
		;
		try {
			PrefixFilter preFilter = new PrefixFilter(m_PrefixFilter);
			java.io.File dir = new java.io.File(m_ArchiveDirectory);
			String[] list = dir.list(preFilter);
			boolean m_FileWasDeleted = false;
			for (int i = 0; i < list.length; i++) {
				file = new java.io.File(m_ArchiveDirectory, list[i]);
				long m_ZipFileModifiedDate = file.lastModified();
				if (m_ZipFileModifiedDate < m_RetentionDate) {
					boolean m_FileDeleteStatus = file.delete();
					logger.info("Deleted file: " + list[i] + ", Status: " + m_FileDeleteStatus);
					m_FileWasDeleted = true;
				}
			}
			if (m_FileWasDeleted) {
				logger.info("All old Archive Zip files have been deleted.");
			} else {
				logger.info("There were no old Archive Zip files to delete.");
			}
			// *********************************************************************************
		} catch (Exception ez) {
			logger.error(
					"An Exception has occurred attempting to remove old archives...  Ignoring...: " + ez.toString());
		}
	}

	private void readZipFileToBytes(String zipFileName, MuleEventContext eventContext) {
		byte[] buffer = new byte[2048];
		try {
			FileInputStream fInputStream = new FileInputStream(zipFileName);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int iLen = 0;
			while ((iLen = fInputStream.read(buffer)) > 0) {
				baos.write(buffer, 0, iLen);
			}
			eventContext.getMessage().setProperty("zipByte", baos.toByteArray());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Properties loadProperties() throws java.io.IOException {
		Properties prop = new Properties();
		prop.load(this.getClass().getResourceAsStream("/JCTaleoUpdater.properties"));
		return prop;
	}
}
