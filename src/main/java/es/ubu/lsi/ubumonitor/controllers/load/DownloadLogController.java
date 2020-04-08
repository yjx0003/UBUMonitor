package es.ubu.lsi.ubumonitor.controllers.load;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Response;

/**
 * Clase encargada de descargar el log de un curso mediante web scraping.
 * 
 * @author Yi Peng Ji
 * @version 2.1.0
 */
public class DownloadLogController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadLogController.class);
	/**
	 * Nombre del host del servidor moodle
	 */
	private String host;

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
	 * @param host     host moodle
	 * @param idCourse id del curso que se quiere descargar el log
	 * 
	 */
	private DownloadLogController(String host, int idCourse) {
		this.host = host;
		this.idCourse = idCourse;

	}

	/**
	 * Contructror
	 * 
	 * @param host     host moodle
	 * @param idCourse id del curso que se quiere descargar el log
	 * @param timezone zona horaria usada como referencia al descargar el log
	 * @param cookies  cookies
	 * 
	 */
	public DownloadLogController(String host, int idCourse, ZoneId timezone) {
		this(host, idCourse);
		this.userTimeZone = timezone;
	}

	/**
	 * Contructror
	 * 
	 * @param host     host moodle
	 * @param idCourse id del curso que se quiere descargar el log
	 * @param timezone zona horaria usada como referencia al descargar el log, un
	 *                 valor de 99 si es la hora del servidor o la nomenclatura IANA
	 *                 de tiempo. Por ejemplo Europe/Madrid.
	 * @param cookies  cookies
	 * @throws IOException si no ha encontrado la zona horaria del servidor de
	 *                     moodle
	 */
	public DownloadLogController(String host, int idCourse, String timezone) throws IOException {

		this(host, idCourse);
		// 99 significa que el usuario esta usando la zona horaria del servidor
		this.userTimeZone = ("99".equals(timezone)) ? findServerTimezone() : ZoneId.of(timezone);
	}

	public ZoneId getUserTimeZone() {
		return userTimeZone;
	}

	/**
	 * Busca la zona horaria del servidor a partir del perfil del usuario.
	 * 
	 * @return zona horaria del servidor.
	 * @throws IOException si no puede conectarse con el servidor
	 */
	public ZoneId findServerTimezone() throws IOException {

		LOGGER.info("Buscando el tiempo del servidor desde el perfil del usuario.");
		// vamos a la edicion del perfil de usuario para ver la zona horaria del
		// servidor
		Document d = Jsoup.parse(Connection.getResponse(host + "/user/edit.php?lang=en").body().string());

		// timezone tendra una estructuctura parecida a: Server timezone (Europe/Madrid)
		// lo unico que nos interesa es lo que hay dentro de los parentesis:
		// Europe/Madrid
		String timezone = d.selectFirst("select#id_timezone option[value=99]").text();
		LOGGER.info("Timezone del servidor: {}", timezone);
		String timezoneParsed = timezone.substring(17, timezone.length() - 1);
		serverTimeZone = ZoneId.of(timezoneParsed);
		return serverTimeZone;

	}

	public ZoneId getServerTimeZone() throws IOException {
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
	 * @param zonedDateTime zonedDateTime
	 * @param zoneId        zoneId, zona horaria
	 * @return otro zonedDateTime segun el zoneId
	 */
	public static ZonedDateTime convertTimezone(ZonedDateTime zonedDateTime, ZoneId zoneId) {
		return zonedDateTime.getZone().equals(zoneId) ? zonedDateTime : zonedDateTime.withZoneSameInstant(zoneId);
	}

	/**
	 * Descarga el log completo.
	 * 
	 * @return csv del log
	 */
	public Response downloadLog(boolean web) {
		return downloadLog("", web);
	}

	/**
	 * Descarga un dia del log a partir del zonedDateTime
	 * 
	 * @param zonedDateTime zona horaria
	 * @param web           download only web
	 * @return csv del log diario
	 */
	public Response downloadLog(ZonedDateTime zonedDateTime, boolean web) {

		return downloadLog(convertTimezone(zonedDateTime, userTimeZone).toEpochSecond(), web);
	}

	/**
	 * Descarga un dia del log a partir de los segundos transcurridos desde 1 de
	 * enero de 1970.
	 * 
	 * @param dateSeconds segundos desde 1 de enero de 1970
	 * @return csv del log diario
	 */
	public Response downloadLog(long dateSeconds, boolean web) {
		return downloadLog(Long.toString(dateSeconds), web);
	}

	/**
	 * Descarga un dia del log a partir de los segundos desde 1 de enero de 1970
	 * 
	 * @param dateSeconds los segundos
	 * @return csv del log diario
	 */
	public Response downloadLog(String dateSeconds, boolean web) {
		try {
			String url = host + "/report/log/index.php?lang=en&download=csv&id=" + idCourse
					+ (dateSeconds == null || dateSeconds.isEmpty() ? "" : "&date=" + dateSeconds)
					+ "&chooselog=1&logreader=logstore_standard" + (web ? "&origin=web" : "");
			LOGGER.info("Descargando log con la URL {} ", url);
			return Connection.getResponse(url);

		} catch (IOException e) {
			LOGGER.error("Error al descargar el log a partir de los segundos: " + dateSeconds, e);
		}
		return null;
	}

	/**
	 * Cambia el id del curso para descargar otros logs.
	 * 
	 * @param idCourse idCourse
	 */
	public void setIdCourse(int idCourse) {
		this.idCourse = idCourse;
	}

}
