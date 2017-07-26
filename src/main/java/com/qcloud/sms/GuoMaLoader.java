package com.qcloud.sms;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GuoMaLoader {
	
	private final static Logger LOG = LoggerFactory.getLogger(GuoMaLoader.class);
	
	private final static Map<String/*country*/, GuoMa> guomaMap = new HashMap<String, GuoMa>();
	
	static {
//		String configpath = getPath("guoma.txt");
		try {
			List<String> lines = getFileLines();
			for(String line : lines){
				if(StringUtils.isBlank(line)){
					continue;
				}
				String[] parts = line.split("\\s+");
				if(parts.length == 3){
					guomaMap.put(parts[0].trim(), new GuoMa(parts[0].trim(), parts[1].trim(), parts[2].trim()));
				} else {
					LOG.warn("illegal line " + line);
				}
			}
		} catch (IOException e) {
			LOG.error("load file config file guoma.txt error", e);
		}
	}
	
	/**
	 * 根据国家精确匹配，获取对应的国码
	 * @param country
	 * @return
	 */
	public static String loadGuoma(String country){
		GuoMa guoma = guomaMap.get(country);
		
		return guoma == null ? "" : guoma.getGuoma();
	}
	
	/**
	 * 根据国家关键字，模糊匹配国家和国码的对应关系
	 * @param kword
	 * @return
	 */
	public static List<GuoMa> searchGuoma(String kword){
		List<GuoMa> guomas = new ArrayList<GuoMa>();
		for(String key : guomaMap.keySet()){
			if(key.contains(kword)){
				GuoMa guoma = guomaMap.get(key);
				if(guoma != null && StringUtils.isNotBlank(guoma.getGuoma())){
					guomas.add(guoma);
				}
			}
		}
		return guomas;
	}
	
	/**
	 * 查询所有的国家及其对应的国码列表
	 * @return
	 */
	public static List<GuoMa> loadAllGuomas(){
		List<GuoMa> guomas = new ArrayList<GuoMa>();
		for(String country : guomaMap.keySet()){
			GuoMa guoma = guomaMap.get(country);
			if(guoma != null && StringUtils.isNotBlank(guoma.getGuoma())){
				guomas.add(guoma);
			}
		}
		
		return guomas;
	}
	
	private static List<String> getFileLines() throws IOException{
		InputStream is = GuoMaLoader.class.getResourceAsStream("/guoma.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		String line = null;
		
		List<String> lines = new ArrayList<String>();
		while((line = reader.readLine()) != null ){
			lines.add(line);
		}

		return lines;

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
