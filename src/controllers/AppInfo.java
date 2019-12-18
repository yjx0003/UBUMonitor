package controllers;

/**
 * Clase de utilidad que devuelve la información de la aplicación.
 * 
 * @author Yi Peng Ji
 *
 */
public class AppInfo {

	public static final String APPLICATION_VERSION = "v2.5.0-stable";

	public static final String APPLICATION_NAME = "UBUMonitor " + APPLICATION_VERSION;

	public static final String GITHUB = "https://github.com/yjx0003/UBUMonitor";

	public static final String RESOURCE_BUNDLE_FILE_NAME = "messages/Messages";

	public static final String LOGGER_FILE_APPENDER = "./log/" + APPLICATION_NAME + ".log";

	public static final String CACHE_DIR = "cache";

	public static final String EXPORT_DIR = "export";

	public static final String IMG_DIR = "/img/";

	public static final String IMG_FLAGS = IMG_DIR + "countries_flags/";

	public static final String PROPERTIES_PATH = "config.properties";
	
	// Info del usuario
	
	public static final String JAVA_CLASS_PATH = System.getProperty("java.class.path");
	
	public static final String JAVA_HOME = System.getProperty("java.home");
	
	public static final String JAVA_VENDOR = System.getProperty("java.vendor");
	
	public static final String JAVA_VERSION = System.getProperty("java.version");
	
	public static final String OS_ARCH = System.getProperty("os.arch");
	
	public static final String OS_NAME = System.getProperty("os.name");
	
	public static final String OS_VERSION = System.getProperty("os.version");
	
	public static final String USER_DIR = System.getProperty("user.dir");
	
	public static final String USER_HOME = System.getProperty("user.home");
	
	public static final String USER_NAME = System.getProperty("user.name");

	private AppInfo() {
		throw new UnsupportedOperationException();
	}

}
