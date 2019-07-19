package controllers;

/**
 * Clase de utilidad que devuelve la información de la aplicación.
 * 
 * @author Yi Peng Ji
 *
 */
public class AppInfo {

	public static final String APPLICATION_NAME = "UBUMonitor";

	public static final String APPLICATION_VERSION = "v2.4.1.0";

	public static final String GITHUB = "https://github.com/yjx0003/UBUMonitor";

	public static final String RESOURCE_BUNDLE_FILE_NAME = "messages/Messages";

	public static final String LOGGER_FILE_APPENDER = "./log/" + APPLICATION_NAME + "-log-" + APPLICATION_VERSION
			+ ".log";

	public static final String CACHE_DIR = "cache";

	public static final String EXPORT_DIR = "export";

	public static final String IMG_DIR = "/img/";

	public static final String IMG_FLAGS = IMG_DIR + "countries_flags/";

	private AppInfo() {
		throw new UnsupportedOperationException();
	}

}
