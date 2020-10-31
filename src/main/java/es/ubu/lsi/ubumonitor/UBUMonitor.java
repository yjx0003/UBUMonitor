package es.ubu.lsi.ubumonitor;

import com.sun.javafx.webkit.WebConsoleListener;

import es.ubu.lsi.ubumonitor.controllers.Loader;

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
		WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> {
		    System.out.println(message + "[at " + lineNumber + "]");
		});
		// https://www.mkyong.com/logging/logback-set-log-file-name-programmatically/
		System.setProperty("logfile.name", AppInfo.LOGGER_FILE_APPENDER);
		Loader.initialize();

	}


}
