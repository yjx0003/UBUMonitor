package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.stage.Stage;
import model.Course;
import model.DataBase;
import model.LogStats;
import model.MoodleUser;
import model.Stats;
import webservice.WebService;

public class Controller {
	private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
			.ofLocalizedDateTime(FormatStyle.SHORT);

	private Languages selectedLanguage;
	private Timer timer;

	private DataBase dataBase;

	private URL host;
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

		setDataBase(new DataBase());
		// Si no existe el recurso de idioma especificado cargamos el Español
		Languages lang = Languages.getLanguageByLocale(Locale.getDefault());
		try {

			if (lang == null) {
				LOGGER.info("No existe fichero de idioma para :{}", Locale.getDefault());
				LOGGER.info("Cargando idioma:{} ", Languages.ENGLISH);
				setSelectedLanguage(Languages.ENGLISH);
			} else {
				setSelectedLanguage(lang);
			}

		} catch (NullPointerException | MissingResourceException e) {
			LOGGER.error(
					"No se ha podido encontrar el recurso de idioma, cargando idioma " + lang + ": {}", e);
			setSelectedLanguage(Languages.ENGLISH);
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

	public void setDataBase(DataBase dataBase) {
		this.dataBase = dataBase;

	}

	public DataBase getDataBase() {
		return dataBase;
	}

	/**
	 * @return the host
	 */
	public URL getUrlHost() {
		return host;
	}

	/**
	 * @param host
	 *            the host to set
	 */
	public void setURLHost(URL host) {
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
		return dataBase.getActualCourse();
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
	 * Intenta buscar el token de acceso a la REST API de moodle e iniciar sesion en
	 * la pagina de moodle.
	 * 
	 * @param host
	 *            servidor moodle
	 * @param username
	 *            nombre de usuario
	 * @param password
	 *            contraseña
	 * @throws IOException
	 *             si no ha podido conectarse o la contraseña es erronea
	 */
	public void tryLogin(String host, String username, String password) throws IOException {

		URL hostURL = new URL(host);

		WebService.initialize(hostURL.toString(), username, password);
		cookies = loginWebScraping(hostURL.toString(), username, password);
		setURLHost(hostURL);

		setUsername(username);
		setPassword(password);

		initTimer();

	}

	private void initTimer() {
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				try {
					Jsoup.connect(host.toString()).cookies(getCookies()).execute();
					LOGGER.info("Ejecutado el temporizador yendo a la pagina principal.");
				} catch (IOException e) {
					LOGGER.error("Fallo en la conexion del temporizador con Moodle", e);
				}
			}

		};
		timer = new Timer();
		timer.schedule(timerTask, 1800000, 1800000); // Cada 30 minutos se ejecuta
	}

	/**
	 * Devuelve las estadisticas de calificaciones del curso.
	 * 
	 * @return estadistica de calificaciones del curso
	 */
	public Stats getStats() {

		return getActualCourse().getStats();
	}

	/**
	 * Crea las estadisticas del curso tanto de calificaciones como de logs.
	 */
	public void createStats() {
		Stats stats = new Stats(getActualCourse());
		getActualCourse().setStats(stats);

		LogStats logStats = new LogStats(getActualCourse().getLogs().getList());
		getActualCourse().setLogStats(logStats);
	}

	/**
	 * Selecciona el curso en la base de datos.
	 * 
	 * @param selectedCourse
	 *            curso seleccionado
	 */
	public void setActualCourse(Course selectedCourse) {
		dataBase.setActualCourse(selectedCourse);
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
	private Map<String, String> loginWebScraping(String host, String username, String password) {

		try {
			LOGGER.info("Logeandose para web scraping");

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
			LOGGER.error("Error al intentar loguearse", e);
			throw new IllegalStateException("No se ha podido loguear al servidor");
		}

	}

	/**
	 * Devuelve los cookies de sesion de moodle, si no hay cookies intenta loguearse
	 * de forma manual al moodle
	 * 
	 * @return los cookies de sesion
	 */
	public Map<String, String> getCookies() {
		return cookies;
	}

	/**
	 * Modifica los cookies de sesion
	 * 
	 * @param cookies
	 *            cookies
	 */
	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public void cancelTimer() {
		if (timer != null) {
			timer.cancel();
			LOGGER.debug("Temporizador apagado.");
		}
	}

}
