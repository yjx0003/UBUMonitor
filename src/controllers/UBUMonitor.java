package controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase main que se ejecuta el primero, cambia el el nombre del fichero de
 * salida para el log y ejecuta la aplicacion.
 * 
 * @author Yi Peng Ji
 *
 */
public class UBUMonitor {

	// Main comando
	public static void main(String[] args) {
		// https://www.mkyong.com/logging/logback-set-log-file-name-programmatically/
		System.setProperty("logfile.name", AppInfo.LOGGER_FILE_APPENDER);
		systemProperties();
		Loader.initialize();

	}

	/**
	 * Add to logger java, OS and user info.
	 */
	private static void systemProperties() {
		// no cambiarlo como atributo static de la clase
		final Logger logger = LoggerFactory.getLogger(MainController.class); 
		logger.info("Path used to find directories and JAR archives containing class files: {}", AppInfo.JAVA_CLASS_PATH);
		logger.info("Installation directory for Java Runtime Environment (JRE): {}", AppInfo.JAVA_HOME);
		logger.info("JRE vendor name: {}", AppInfo.JAVA_VENDOR);
		logger.info("JRE version number: {}", AppInfo.JAVA_VERSION);
		logger.info("Operating system architecture: {}", AppInfo.OS_ARCH);
		logger.info("Operating system name: {}", AppInfo.OS_NAME);
		logger.info("Operating system version: {}", AppInfo.OS_VERSION);
		logger.info("User working directory: {}", AppInfo.USER_DIR);
		logger.info("User home directory: {}", AppInfo.USER_HOME);
		logger.info("User account name: {}", AppInfo.USER_NAME);
	}
}
