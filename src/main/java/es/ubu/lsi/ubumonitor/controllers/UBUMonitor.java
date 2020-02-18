package es.ubu.lsi.ubumonitor.controllers;

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
		Loader.initialize();

	}


}
