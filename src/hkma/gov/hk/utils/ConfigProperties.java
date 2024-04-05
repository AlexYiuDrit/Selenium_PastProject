package hkma.gov.hk.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;


public class ConfigProperties {
	
	/**
	 * Default config file: config.properties
	 * - The Program will auto search for any resource folder and look for this config file
	 * Get config file variable: ConfigProperties.getInstance().getConfig(<key>);
	 * Get other config file variable: ConfigProperties.getInstance().getProperty(<folderName(without .properties)>, <key>);
	 */
	
	public static ConfigProperties me = null;
	public static final String path = "/conf/config.properties";
	
	// Singleton design pattern
    public static ConfigProperties getInstance() {
        if (me == null){
            synchronized(ConfigProperties.class){
                if(me == null) {
                	me = new ConfigProperties();
                }
            }
        }
        return me;
    }

	private Map<String, File> fileMap = new HashMap<String, File>();
	private Map<String, Long> fileModifiedTimeMap = new HashMap<String, Long>();
	private Map<String, Properties> propertiesMap = new HashMap<String, Properties>();

	public String getConfig(String key) {
		return getPropertyFromFile(path, key);
	}

	public String getProperty(String file, String key) {
		return getPropertyFromFile("/conf/" + file + ".properties", key);
	}

	private String getPropertyFromFile(String fileName, String key) {
		try {
			if (!fileMap.containsKey(fileName)) {
				fileMap.put(fileName, new File(ConfigProperties.class.getResource(fileName).getFile()));
			}
			File f = fileMap.get(fileName);
			Long fileModifiedTime = f.lastModified();
			boolean needReload = (!fileModifiedTimeMap.containsKey(fileName) || fileModifiedTimeMap.get(fileName) + 0 != fileModifiedTime);
			if (needReload) {
				Properties prop = new Properties();
				InputStream in = null;
				try {
					in = new FileInputStream(f);
					prop.load(new InputStreamReader(in, "UTF8"));
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(in);
				}
				propertiesMap.put(fileName, prop);
				fileModifiedTimeMap.put(fileName, fileModifiedTime);
			}

			return propertiesMap.get(fileName).getProperty(key);
		} catch (Exception e) {

		}
		return null;
	}
	
	public static void main(String[] args) throws NullPointerException {
		System.out.println(me);
		System.out.println("password: " + ConfigProperties.getInstance().getConfig("account.email"));
		System.out.println("Email: " + ConfigProperties.getInstance().getProperty("test", "123.abc"));
	    System.out.println(me);
	}
}