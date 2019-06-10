package controllers;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.stage.Stage;
import model.BBDD;
import model.Course;
import model.LogStats;
import model.MoodleUser;
import model.Stats;
import webservice.WebService;

public class Controller {
	static final Logger logger = LoggerFactory.getLogger(Controller.class);

	
	
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);


	private Languages selectedLanguage;

	private BBDD BBDD;

	private String host;
	private Stage stage;
	private String password;
	private String username;

	private Map<String, String> cookies;

	/**
	 * Usuario actual.
	 */
	private MoodleUser user;

	/**
	 * Instacia única de la clase Controller.
	 */
	private static Controller instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private Controller() {
	}

	/**
	 * Devuelve la instancia única de Controller.
	 * 
	 * @return instancia singleton
	 */
	public static Controller getInstance() {
		if (instance == null) {
			instance = new Controller();
		}
		return instance;
	}

	public void initialize() {
		BBDD BBDD = new BBDD();
		setBBDD(BBDD);
		// Si no existe el recurso de idioma especificado cargamos el Español
		Languages lang = Languages.getLanguageByLocale(Locale.getDefault());
		try {

			if (lang == null) {
				logger.info("No existe fichero de idioma para: " + Locale.getDefault());
				logger.info("Cargando idioma: " + Languages.SPANISH);
				setSelectedLanguage(Languages.SPANISH);
			} else {
				setSelectedLanguage(lang);
			}

		} catch (NullPointerException | MissingResourceException e) {
			logger.error(
					"No se ha podido encontrar el recurso de idioma, cargando idioma " + lang + ": {}", e);
			setSelectedLanguage(Languages.SPANISH);
		}
	}

	public Languages getSelectedLanguage() {
		return selectedLanguage;

	}

	public void setSelectedLanguage(Languages selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
		Locale a = selectedLanguage.getLocale();
		Locale.setDefault(a);
		I18n.setResourceBundle(ResourceBundle.getBundle(AppInfo.RESOURCE_BUNDLE_FILE_NAME));
	}

	public void setBBDD(BBDD BBDD) {
		this.BBDD = BBDD;

	}

	public BBDD getBBDD() {
		return BBDD;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the stage
	 */
	public Stage getStage() {
		return stage;
	}

	/**
	 * @param stage
	 *            the stage to set
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Course getActualCourse() {
		return BBDD.getActualCourse();
	}

	/**
	 * @return the user
	 */
	public MoodleUser getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(MoodleUser user) {
		this.user = user;
	}

	/**
	 * Intenta buscar el token de acceso a la REST API de moodle
	 * @param host servidor moodle
	 * @param username nombre de usuario
	 * @param password contraseña
	 * @throws Exception si no ha podido conectarse o la contraseña es erronea
	 */
	public void tryLogin(String host, String username, String password) throws Exception {
		WebService.initialize(host, username, password);

		setHost(host);

		setUsername(username);
		setPassword(password);

	}

	/**
	 * Devuelve las estadisticas de calificaciones del curso.
	 * @return estadistica de calificaciones del curso
	 */
	public Stats getStats() {

		return getActualCourse().getStats();
	}

	/**
	 * Crea las estadisticas del curso tanto de calificaciones como de logs.
	 * @throws Exception si falla la creacion de las estadisticas
	 */
	public void createStats() throws Exception {
		Stats stats = new Stats(getActualCourse());
		getActualCourse().setStats(stats);

		LogStats logStats = new LogStats(getActualCourse().getLogs().getList(), getActualCourse().getEnrolledUsers());
		getActualCourse().setLogStats(logStats);
	}

	/**
	 * Selecciona el curso en la base de datos.
 	 * @param selectedCourse curso seleccionado
	 */
	public void setActualCourse(Course selectedCourse) {
		BBDD.setActualCourse(selectedCourse);
	}

	/**
	 * Se loguea en el servidor de moodle mediante web scraping.
	 * 
	 * @param username
	 *            nombre de usuario de la cuenta
	 * @param password
	 *            password contraseña
	 * @return las cookies que se usan para navegar dentro del servidor despues de
	 *         loguearse
	 * @throws IllegalStateException
	 *             si no ha podido loguearse
	 */
	private Map<String, String> login(String username, String password) {

		try {
			logger.info("Logeandose para web scraping");

			Response loginForm = Jsoup.connect(host + "/login/index.php")
					.method(Method.GET)
					.execute();

			Document loginDoc = loginForm.parse();
			Element e = loginDoc.selectFirst("input[name=logintoken]");
			String logintoken = (e == null) ? "" : e.attr("value");

			Response login = Jsoup.connect(host + "/login/index.php")
					.data("username", username, "password", password, "logintoken", logintoken)
					.method(Method.POST)
					.cookies(loginForm.cookies()).execute();

			return login.cookies();
		} catch (Exception e) {
			logger.error("Error al intentar loguearse", e);
			throw new IllegalStateException("No se ha podido loguear al servidor");
		}

	}

	/**
	 * Devuelve los cookies de sesion de moodle, si no hay cookies intenta loguearse de forma manual al moodle
	 * @return los cookies de sesion
	 */
	public Map<String, String> getCookies() {
		if (cookies == null) {
			this.cookies = login(username, password);
		}
		return cookies;
	}

	/**
	 * Modifica los cookies de sesion
	 * @param cookies cookies
	 */
	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}

}
