package com.qcloud.sms;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GuoMaLoader {
	
	private final static Logger LOG = LoggerFactory.getLogger(GuoMaLoader.class);
	
	private final static Map<String/*country*/, GuoMa> guomaMap = new HashMap<String, GuoMa>();
	
	static {
		String configpath = getPath("guoma.txt");
		try {
			List<String> lines = FileUtils.readLines(new File(configpath), "UTF-8");
			for(String line : lines){
				if(StringUtils.isBlank(line)){
					continue;
				}
				String[] parts = line.split("\\s+");
				if(parts.length == 3){
					guomaMap.put(parts[0], new GuoMa(parts[0], parts[1], parts[2]));
				} else {
					LOG.warn("illegal line " + line);
				}
			}
		} catch (IOException e) {
			LOG.error("load file " + configpath + " error", e);
		}
	}
	
	public static String loadGuoma(String country){
		GuoMa guoma = guomaMap.get(country);
		return guoma == null ? "" : guoma.getGuoma();
	}
	
	private static String getPath(String fileName) {

		String filePath = fileName;
		if (new File(filePath).exists()) {
			return fileName;
		}

		String fileSuffix = fileName.startsWith(File.separator) || fileName.startsWith("\\") ? fileName : File.separator
				+ fileName;

		String configDir = System.getProperty("project.config.dir");
		filePath = configDir + fileSuffix;
		if (new File(filePath).exists()) {
			return filePath;
		}

		String userDir = System.getProperty("user.dir");
		filePath = userDir + fileSuffix;
		if (new File(filePath).exists()) {
			return filePath;
		}

		URL url = GuoMaLoader.class.getResource(fileSuffix);
		if (url == null) {
			url = GuoMaLoader.class.getClassLoader().getResource(fileName);

			return url == null ? null : url.toString();
		}

		return url.toString();
	}
	
	
	public static class GuoMa {
		private String country; //国家
		private String guanma; //国际冠码
		private String guoma; //国码
		
		public GuoMa(String country, String guanma, String guoma) {
			this.country = country;
			this.guanma = guanma;
			this.guoma = guoma;
		}
		
		public String getCountry() {
			return country;
		}
		public String getGuanma() {
			return guanma;
		}
		public String getGuoma() {
			return guoma;
		}
		
		@Override
		public String toString() {
			return "country:" + this.getCountry() + ",guanma:" + this.getGuanma() + ",guoma:" + this.getGuoma();
		}
	}

}
