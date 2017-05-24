package com.jpmc.hrt.sdi.tcc.services;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class TCCExport {
	public static TCCExportJob unmarshallToTCCImportJob(String xml) {
		JAXBContext jaxbContext = null;
		StringReader sr = null;
		TCCExportJob tccResult = null;
		try {
			sr = new StringReader(xml);
			jaxbContext = JAXBContext.newInstance(TCCExportJob.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			tccResult = (TCCExportJob) jaxbUnmarshaller.unmarshal(sr);

		} catch (JAXBException e) {

			e.printStackTrace();
		}
		return tccResult;
	}

}
