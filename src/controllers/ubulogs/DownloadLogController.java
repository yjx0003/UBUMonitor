package controllers.ubulogs;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase encargada de descargar el log de un curso mediante web scraping.
 * 
 * @author Yi Peng Ji
 * @version 2.1.0
 */
public class DownloadLogController {

	static final Logger logger = LoggerFactory.getLogger(DownloadLogController.class);
	/**
	 * Nombre del host del servidor moodle
	 */
	private String host;

	/**
	 * Cookies que se usan para navegar despues de loguearse
	 */
	private Map<String, String> cookies;

	/**
	 * id del curso que se quiere descargar el log
	 */
	private int idCourse;

	/**
	 * Zona horaria usada como referencia en las descargas segun el tiempo. Solo se
	 * usa si es una descarga de un dia en concreto.
	 */
	private ZoneId userTimeZone;

	private ZoneId serverTimeZone;

	/**
	 * Constructor privado que solo asigna atributos basicos. Evita la repeticion de
	 * codigo en los otros constructores.
	 * 
	 * @param host
	 *            host moodle
	 * @param username
	 *            nombre de usuario cuenta
	 * @param password
	 *            contraseña de la cuenta
	 * @param idCourse
	 *            id del curso que se quiere descargar el log
	 * 
	 */
	private DownloadLogController(String host, String username, String password, int idCourse,
			Map<String, String> cookies) {
		this.host = host;
		this.idCourse = idCourse;
		this.cookies = cookies;
	}

	/**
	 * Contructror
	 * 
	 * @param host
	 *            host moodle
	 * @param username
	 *            nombre de usuario cuenta
	 * @param password
	 *            contraseña de la cuenta
	 * @param idCourse
	 *            id del curso que se quiere descargar el log
	 * @param timezone
	 *            zona horaria usada como referencia al descargar el log
	 */
	public DownloadLogController(String host, String username, String password, int idCourse, ZoneId timezone,
			Map<String, String> cookies) {
		this(host, username, password, idCourse, cookies);
		this.userTimeZone = timezone;
	}

	/**
	 * Contructror
	 * 
	 * @param host
	 *            host moodle
	 * @param username
	 *            nombre de usuario cuenta
	 * @param password
	 *            contraseña de la cuenta
	 * @param idCourse
	 *            id del curso que se quiere descargar el log
	 * @param timezone
	 *            zona horaria usada como referencia al descargar el log, un valor
	 *            de 99 si es la hora del servidor o la nomenclatura IANA de tiempo.
	 *            Por ejemplo Europe/Madrid.
	 * @throws Exception
	 *             si no ha encontrado la zona horaria del servidor de moodle
	 */
	public DownloadLogController(String host, String username, String password, int idCourse, String timezone,
			Map<String, String> cookies) throws Exception {

		this(host, username, password, idCourse, cookies);
		// 99 significa que el usuario esta usando la zona horaria del servidor
		this.userTimeZone = (timezone.equals("99")) ? findServerTimezone() : ZoneId.of(timezone);
	}

	public ZoneId getUserTimeZone() {
		return userTimeZone;
	}

	public ZoneId findServerTimezone() throws Exception {
		try {
			logger.info("Buscando el tiempo del servidor desde el perfil del usuario.");
			// vamos a la edicion del perfil de usuario para ver la zona horaria del
			// servidor
			Document d = Jsoup.connect(host + "/user/edit.php?lang=en").cookies(cookies).get();

			// timezone tendra una estructuctura parecida a: Server timezone (Europe/Madrid)
			// lo unico que nos interesa es lo que hay dentro de los parentesis:
			// Europe/Madrid
			String timezone = d.selectFirst("select#id_timezone option[value=99]").text();
			logger.info("Timezone del servidor: " + timezone);
			String timezoneParsed = timezone.substring(17, timezone.length() - 1);
			serverTimeZone = ZoneId.of(timezoneParsed);
			return serverTimeZone;
		} catch (Exception e) {
			logger.error("Error al buscar el timezone del usuario desde el html ", e);
			throw e;
		}

	}

	public ZoneId getServerTimeZone() throws Exception {
		if (serverTimeZone == null) {
			return findServerTimezone();
		}
		return serverTimeZone;
	}

	public void setServerTimeZone(ZoneId serverTimeZone) {
		this.serverTimeZone = serverTimeZone;
	}

	/**
	 * Convierte un zonedDateTime a otro zonedDateTime con diferente zona horaria
	 * 
	 * @param zonedDateTime
	 *            zonedDateTime
	 * @param zoneId
	 *            zoneId, zona horaria
	 * @return otro zonedDateTime segun el zoneId
	 */
	public static ZonedDateTime convertTimezone(ZonedDateTime zonedDateTime, ZoneId zoneId) {
		return zonedDateTime.getZone().equals(zoneId) ? zonedDateTime : zonedDateTime.withZoneSameInstant(zoneId);
	}

	/**
	 * Descarga todo el log del curso disponible
	 * 
	 * @return csv del log
	 */
	public String downloadLog() {
		return downloadLog("");
	}

	/**
	 * Descarga un dia del log a partir del zonedDateTime
	 * 
	 * @param zonedDateTime
	 *            zona horaria
	 * @return csv del log diario
	 */
	public String downloadLog(ZonedDateTime zonedDateTime) {

		return downloadLog(convertTimezone(zonedDateTime, userTimeZone).toEpochSecond());
	}

	/**
	 * Descarga un dia del log a partir de los segundos transcurridos desde 1 de
	 * enero de 1970.
	 * 
	 * @param dateSeconds
	 *            segundos desde 1 de enero de 1970
	 * @return csv del log diario
	 */
	public String downloadLog(long dateSeconds) {
		return downloadLog(Long.toString(dateSeconds));
	}

	/**
	 * Descarga un dia del log a partir del localDateTime usando la zona horaria del
	 * atributo timezone
	 * 
	 * @param localDateTime
	 *            localDateTime usando el timezone
	 * @return csv del log diario
	 */
	public String downloadLog(LocalDateTime localDateTime) {

		return downloadLog(localDateTime.atZone(userTimeZone));
	}

	/**
	 * Descarga un dia del log a partir de los segundos desde 1 de enero de 1970
	 * 
	 * @param dateSeconds
	 *            los segundos
	 * @return csv del log diario
	 */
	public String downloadLog(String dateSeconds) {
		try {
			String URL = host + "/report/log/index.php?lang=en&download=csv&id=" + idCourse + "&date=" + dateSeconds
					+ "&modid=&chooselog=1&logreader=logstore_standard";
			logger.info("Descargando log con la URL: " + URL);
			Response response = Jsoup
					.connect(URL)
					.cookies(cookies)
					.ignoreContentType(true)
					.timeout(0)
					.maxBodySize(0)
					.execute();

			return response.body();

		} catch (Exception e) {
			logger.error("Error al descargar el log a partir de los segundos: " + dateSeconds, e);
		}
		return null;
	}

	/**
	 * Descarga los logs diarios a partir de zonedInicio hasta zonedFin
	 * 
	 * @param zonedInicio
	 *            fecha de inicio apartir de la descarga
	 * @param zonedFin
	 *            hasta fecha fin
	 * @return lista de log diarios de fecha inicio hasta fin
	 */
	public List<String> downloadLog(ZonedDateTime zonedInicio, ZonedDateTime zonedFin) {

		zonedInicio = convertTimezone(zonedInicio, userTimeZone);
		zonedFin = convertTimezone(zonedFin, userTimeZone);

		List<String> logs = new ArrayList<String>();
		// mientras la fecha de inicio sea menor que la fecha de fin
		while (zonedInicio.isBefore(zonedFin)) {
			logs.add(downloadLog(zonedInicio));
			zonedInicio = zonedInicio.plusDays(1);
		}

		return logs;
	}

	/**
	 * Descarga los logs diarios a partir de inicio hasta fin
	 * 
	 * @param inicio
	 *            fecha de inicio apartir de la descarga
	 * @param fin
	 *            hasta fecha fin
	 * @return lista de log diarios de fecha inicio hasta fin
	 */
	public List<String> downloadLog(LocalDateTime inicio, LocalDateTime fin) {
		return downloadLog(inicio.atZone(userTimeZone), fin.atZone(userTimeZone));
	}

	/**
	 * Devuelve los cookies
	 * 
	 * @return cookies
	 */
	public Map<String, String> getCookies() {
		return cookies;
	}

	/**
	 * Cambia el id del curso para descargar otros logs.
	 * 
	 * @param idCourse
	 *            idCourse
	 */
	public void setIdCourse(int idCourse) {
		this.idCourse = idCourse;
	}


}
