package es.ubu.lsi.ubumonitor.controllers.ubulogs;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.Controller;
import okhttp3.Request;

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
	 * @param host host moodle
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
	 * @param host host moodle
	 * @param idCourse id del curso que se quiere descargar el log
	 * @param timezone zona horaria usada como referencia al descargar el log
	 * @param cookies cookies
	 * 
	 */
	public DownloadLogController(String host, int idCourse, ZoneId timezone) {
		this(host, idCourse);
		this.userTimeZone = timezone;
	}

	/**
	 * Contructror
	 * 
	 * @param host host moodle
	 * @param idCourse id del curso que se quiere descargar el log
	 * @param timezone zona horaria usada como referencia al descargar el log, un
	 * valor de 99 si es la hora del servidor o la nomenclatura IANA de tiempo. Por
	 * ejemplo Europe/Madrid.
	 * @param cookies cookies
	 * @throws IOException si no ha encontrado la zona horaria del servidor de
	 * moodle
	 */
	public DownloadLogController(String host, int idCourse, String timezone)
			throws IOException {

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
		Document d;
		try (okhttp3.Response response = Controller.getInstance().getClient()
				.newCall(new Request.Builder().url(host + "/user/edit.php?lang=en").build()).execute()) {
			
				
			d = Jsoup.parse(response.body().string());
		}
		
	

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
	 * @param zoneId zoneId, zona horaria
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
	public String downloadLog() {
		return downloadLog("");
	}

	/**
	 * Descarga un dia del log a partir del zonedDateTime
	 * 
	 * @param zonedDateTime zona horaria
	 * @return csv del log diario
	 */
	public String downloadLog(ZonedDateTime zonedDateTime) {

		return downloadLog(convertTimezone(zonedDateTime, userTimeZone).toEpochSecond());
	}

	/**
	 * Descarga un dia del log a partir de los segundos transcurridos desde 1 de
	 * enero de 1970.
	 * 
	 * @param dateSeconds segundos desde 1 de enero de 1970
	 * @return csv del log diario
	 */
	public String downloadLog(long dateSeconds) {
		return downloadLog(Long.toString(dateSeconds));
	}

	/**
	 * Descarga un dia del log a partir del localDateTime usando la zona horaria del
	 * atributo timezone
	 * 
	 * @param localDateTime localDateTime usando el timezone
	 * @return csv del log diario
	 */
	public String downloadLog(LocalDateTime localDateTime) {

		return downloadLog(localDateTime.atZone(userTimeZone));
	}

	/**
	 * Descarga un dia del log a partir de los segundos desde 1 de enero de 1970
	 * 
	 * @param dateSeconds los segundos
	 * @return csv del log diario
	 */
	public String downloadLog(String dateSeconds) {
		try {
			String url = host + "/report/log/index.php?lang=en&download=csv&id=" + idCourse + "&date=" + dateSeconds
					+ "&modid=&chooselog=1&logreader=logstore_standard";
			LOGGER.info("Descargando log con la URL {}: ", url);
			try (okhttp3.Response response = Controller.getInstance().getClient()
					.newCall(new Request.Builder().url(url).build()).execute()) {
				return response.body().string();
			}

		} catch (IOException e) {
			LOGGER.error("Error al descargar el log a partir de los segundos: " + dateSeconds, e);
		}
		return null;
	}

	/**
	 * Descarga los logs diarios a partir de zonedInicio hasta zonedFin
	 * 
	 * @param zonedInicio fecha de inicio apartir de la descarga
	 * @param zonedFin hasta fecha fin
	 * @return lista de log diarios de fecha inicio hasta fin
	 */
	public List<String> downloadLog(ZonedDateTime zonedInicio, ZonedDateTime zonedFin) {

		ZonedDateTime newzonedInicio = convertTimezone(zonedInicio, userTimeZone);
		ZonedDateTime newzonedFin = convertTimezone(zonedFin, userTimeZone);
		LOGGER.info("Descargando los logs desde {} hasta {}", newzonedInicio, newzonedFin);
		List<String> logs = new ArrayList<>();
		// mientras la fecha de inicio sea menor que la fecha de fin
		while (newzonedInicio.isBefore(newzonedFin)) {
			logs.add(downloadLog(newzonedInicio));
			newzonedInicio = newzonedInicio.plusDays(1);
		}

		return logs;
	}

	/**
	 * Descarga los logs diarios a partir de inicio hasta fin
	 * 
	 * @param inicio fecha de inicio apartir de la descarga
	 * @param fin hasta fecha fin
	 * @return lista de log diarios de fecha inicio hasta fin
	 */
	public List<String> downloadLog(LocalDateTime inicio, LocalDateTime fin) {
		return downloadLog(inicio.atZone(userTimeZone), fin.atZone(userTimeZone));
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
