package model;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Los clase Logs guarda cada linea de log con metodos de acceso y añadir nuevas
 * lineas de log.
 * 
 * @author Yi Peng Ji
 *
 */
public class Logs implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static final Logger logger = LoggerFactory.getLogger(Logs.class);

	/**
	 * Lineas de log, se deben añadir en orden ascendente por fecha.
	 */
	private List<LogLine> logLines;

	/**
	 * Zona horaria usado en los logline
	 */
	private ZoneId zoneId;

	/**
	 * Constructor que le pasa la zona horaria usado de los logs y las lineas de
	 * log.
	 * 
	 * @param zoneId
	 *            zona horaria
	 * @param logLines
	 *            lista de lineas de logs
	 */
	public Logs(ZoneId zoneId, List<LogLine> logLines) {
		this.zoneId = zoneId;
		this.logLines = new ArrayList<>();
		this.addAll(logLines);
	}

	/**
	 * Devuelve la zona horaria
	 * 
	 * @return zona horaria
	 */
	public ZoneId getZoneId() {
		return zoneId;
	}

	/**
	 * Devuelve la última linea del log o null si está vacio.
	 * 
	 * @return última linea del log o null si está vacio
	 */
	public ZonedDateTime getLastDatetime() {
		return isEmpty(logLines) ? null : logLines.get(logLines.size() - 1).getTime();
	}

	/**
	 * Añade los nuevas lineas de log, deben estar en orden ascendente por tiempo.
	 * 
	 * @param logLines
	 */
	public void addAll(List<LogLine> logLines) {

		if (isEmpty(logLines)) {
			return;
		}

		changeZoneId(logLines);
		this.logLines.addAll(logLines);
	}

	/**
	 * Convierte los log de una zona horaria a la zona horaria de los logs
	 * anteriores.
	 * 
	 * @param logLines
	 *            las nuevas lineas de log
	 */
	private void changeZoneId(List<LogLine> logLines) {
		if (!logLines.get(0).getTime().getZone().equals(this.zoneId)) {
			logger.info("Se ha detectado que el usuario ha cambiado de zona horaria: "
					+ logLines.get(0).getTime().getZone() + ".Inicialmente se usaba: " + zoneId);
			for (LogLine logLine : logLines) {
				ZonedDateTime changeZone = logLine.getTime().withZoneSameInstant(zoneId);
				logLine.setTime(changeZone);
			}
		}
	}

	/**
	 * Comporueba si las nuevas lineas es nulo o está vacío.
	 * 
	 * @param logLines
	 *            las nuevas lineas de log
	 * @return true si es nulo o vacío, false en caso contrario
	 */
	public boolean isEmpty(List<LogLine> logLines) {
		return logLines == null || logLines.isEmpty();
	}

	/**
	 * Devuelve la lista de las lineas de log.
	 * 
	 * @return las líneas de log
	 */
	public List<LogLine> getList() {
		return logLines;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Logs [logLines=" + logLines + "]";
	}

}
