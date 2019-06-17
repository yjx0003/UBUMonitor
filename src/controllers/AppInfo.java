package controllers;

public class AppInfo {

	public static final String APPLICATION_NAME = "UBUMonitor";

	public static final String APPLICATION_VERSION = "v2.3.5.3-alpha";

	public static final String GITHUB = "https://github.com/yjx0003/UBUMonitor";

	public static final String RESOURCE_BUNDLE_FILE_NAME = "messages/Messages";

	public static final String LOGGER_FILE_APPENDER = "./log/" + APPLICATION_NAME + "-log-" + APPLICATION_VERSION
			+ ".log";

	public static final String CACHE_DIR = "./cache/";

	public static final String IMG_DIR= "/img/";
	
	
	private AppInfo() {
		throw new UnsupportedOperationException();
	}
	
}
