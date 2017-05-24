package com.jpmc.hrt.sdi.zip;

public class PrefixFilter implements java.io.FilenameFilter {
	private String prefix;

	public PrefixFilter(String prefix) {
		this.prefix = prefix;
	}

	public boolean accept(java.io.File dir, String name) {
		return name.startsWith(prefix);
	}
}