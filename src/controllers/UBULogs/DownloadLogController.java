package controllers.UBULogs;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.UBUGrades;

/**
 * 
 * @author Yi Peng Ji
 *
 */
public class DownloadLogController {

	static final Logger logger = LoggerFactory.getLogger(DownloadLogController.class);

	private String host;

	private Map<String, String> cookies;

	private String idCourse;

	private ZoneId usertimezone;

	private String idUser;

	public DownloadLogController(String host, String username, String password, int idUser, int idCourse,
			String usertimezone) {
		this.host = host;
		this.idUser = Integer.toString(idUser);
		this.idCourse = Integer.toString(idCourse);
		this.cookies = login(username, password);

		// 99 significa que el usuario esta usando la zona horaria del servidor
		this.usertimezone = (usertimezone.equals("99")) ? findServerTimezone() : ZoneId.of(usertimezone);
	}

	public DownloadLogController(UBUGrades ubugrades) {
		this(ubugrades.getHost(), ubugrades.getSession().getUserName(), ubugrades.getSession().getPassword(),
				ubugrades.getUser().getId(), ubugrades.getSession().getActualCourse().getId(),
				ubugrades.getUser().getTimezone());

	}

	private Map<String, String> login(String username, String password) {

		try {
			logger.info("Logeandose para web scraping");

			Response loginForm = Jsoup.connect(host + "/login/index.php").method(Method.GET).execute();

			Document loginDoc = loginForm.parse();
			Element e = loginDoc.selectFirst("input[name=logintoken]");
			String logintoken = (e == null) ? "" : e.attr("value");

			Response login = Jsoup.connect(host + "/login/index.php")
					.data("username", username, "password", password, "logintoken", logintoken).method(Method.POST)
					.cookies(loginForm.cookies()).execute();

			return login.cookies();
		} catch (Exception e) {
			logger.error("Error al intentar loguearse", e);

		}
		return null;

	}

	private ZoneId findServerTimezone() {
		try {

			// vamos a la edicion del perfil de usuario para ver la zona horaria del
			// servidor
			Document d = Jsoup.connect(host + "/user/edit.php?lang=en&id=" + idUser).cookies(cookies).get();

			// timezone tendra una estructuctura parecida a: Server timezone (Europe/Madrid)
			// lo unico que nos interesa es lo que hay dentro de los parentesis:
			// Europe/Madrid
			String timezone = d.selectFirst("select#id_timezone option[value=99]").text();
			logger.info("Timezone del servidor: " + timezone);
			String timezoneParsed = timezone.substring(17, timezone.length() - 1);
			return ZoneId.of(timezoneParsed);
		} catch (Exception e) {
			logger.error("Error al buscar el timezone del usuario desde el html ", e);
		}
		return null;
	}

	public String downloadLog() {
		return downloadLog("");
	}

	public String downloadLog(ZonedDateTime zonedDateTime) {

		return downloadLog(Long.toString(zonedDateTime.toEpochSecond()));
	}

	public String downloadLog(LocalDateTime localDateTime) {

		return downloadLog(localDateTime.atZone(usertimezone));
	}

	public String downloadLog(String dateSeconds) {
		try {
			Document response = Jsoup
					.connect(host + "/report/log/index.php?lang=en&download=csv&id=" + idCourse + "&date=" + dateSeconds
							+ "&modid=&chooselog=1&logreader=logstore_standard")
					.cookies(cookies).timeout(0).maxBodySize(0).get();

			return response.wholeText();

		} catch (Exception e) {
			logger.error("Error al descargar el log", e);
		}
		return null;
	}

	public List<String> downloadLog(LocalDateTime inicio, LocalDateTime fin) {
		List<String> logs = new ArrayList<String>();
		ZonedDateTime zonedInicio = inicio.atZone(usertimezone);
		ZonedDateTime zonedFin = fin.atZone(usertimezone);

		// mientras la fecha de inicio sea menor que la fecha de fin
		while (zonedInicio.compareTo(zonedFin) < 0) {
			logs.add(downloadLog(zonedInicio));
			zonedInicio = zonedInicio.plusDays(1);
		}

		return logs;

	}

	public void setIdCourse(String idCourse) {
		this.idCourse = idCourse;
	}

	public static void main(String[] args) {
		String host = "host";
		String username = "username";
		String password = "password";
		int idUser = 0;
		int idCourse = 0;
		String usertimezone = "99"; //no cambiar si tienes puesto que la zona horaria sea del servidor

		DownloadLogController log = new DownloadLogController(host, username, password, idUser, idCourse, usertimezone);

		LocalDateTime fecha = LocalDateTime.of(2019, 2, 15, 18, 40);
		
		 System.out.println(log.downloadLog(fecha));
		 
		 //Descomentar si quieres descargar todo el log
		 //System.out.println(log.downloadLog());
	}

}
