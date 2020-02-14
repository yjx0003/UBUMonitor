package es.ubu.lsi.ubumonitor.controllers;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.controllers.configuration.Config;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.LogStats;
import es.ubu.lsi.ubumonitor.model.MoodleUser;
import es.ubu.lsi.ubumonitor.model.Stats;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.webservice.WebService;
import javafx.stage.Stage;
import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Controller {
	private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
			.ofLocalizedDateTime(FormatStyle.SHORT);

	private Languages selectedLanguage;

	private DataBase dataBase;
	private LocalDateTime loggedIn;
	private Path directoryCache;
	private URL host;
	private Stage stage;
	private String password;
	private String username;
	private String sesskey;

	private boolean offlineMode;

	private MainConfiguration mainConfiguration;

	/**
	 * Usuario actual.
	 */
	private MoodleUser user;

	private Path configuration;

	private OkHttpClient client;

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

	public void initialize() throws IOException {
		Config.initialize(AppInfo.PROPERTIES_PATH);
		setDataBase(new DataBase());
		// Si no existe el recurso de idioma especificado cargamos el Español
		Languages lang = Languages
				.getLanguageByTag(Config.getProperty("language", Locale.getDefault().toLanguageTag()));
		try {

			if (lang == null) {
				LOGGER.info("No existe fichero de idioma para :{}", Locale.getDefault());
				LOGGER.info("Cargando idioma:{} ", Languages.ENGLISH_UK);
				setSelectedLanguage(Languages.ENGLISH_UK);
			} else {
				setSelectedLanguage(lang);
			}

		} catch (NullPointerException | MissingResourceException e) {
			LOGGER.error("No se ha podido encontrar el recurso de idioma, cargando idioma " + lang + ": {}", e);
			setSelectedLanguage(Languages.ENGLISH_UK);
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
		Config.setProperty("language", a.toLanguageTag());
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
	 * @param host the host to set
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
	 * @param stage the stage to set
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
	 * @param user the user to set
	 */
	public void setUser(MoodleUser user) {
		this.user = user;
	}

	/**
	 * Intenta buscar el token de acceso a la REST API de moodle e iniciar sesion en
	 * la pagina de moodle.
	 * 
	 * @param host servidor moodle
	 * @param username nombre de usuario
	 * @param password contraseña
	 * @throws IOException si no ha podido conectarse o la contraseña es erronea
	 */
	public void tryLogin(String host, String username, String password) throws IOException {

		URL hostURL = new URL(host);

		WebService.initialize(hostURL.toString(), username, password);
		String response = loginWebScraping(hostURL.toString(), username, password);
	
		sesskey = findSesskey(response);

		setURLHost(hostURL);

		setUsername(username);
		setPassword(password);

	}

	public void reLogin() {
		String response = loginWebScraping(host.toString(), username, password);

		sesskey = findSesskey(response);

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
	 * @param selectedCourse curso seleccionado
	 */
	public void setActualCourse(Course selectedCourse) {
		dataBase.setActualCourse(selectedCourse);
	}

	/**
	 * Se loguea en el servidor de moodle mediante web scraping.
	 * 
	 * @param username nombre de usuario de la cuenta
	 * @param password password contraseña
	 * @return las cookies que se usan para navegar dentro del servidor despues de
	 * loguearse
	 * @throws IllegalStateException si no ha podido loguearse
	 */
	private String loginWebScraping(String host, String username, String password) {

		try {
			LOGGER.info("Logeandose para web scraping");

			org.jsoup.Connection.Response loginForm = Jsoup.connect(host + "/login/index.php").method(Method.GET)
					.execute();

			Document loginDoc = loginForm.parse();
			Element e = loginDoc.selectFirst("input[name=logintoken]");
			String logintoken = (e == null) ? "" : e.attr("value");

	
			RequestBody formBody = new FormBody.Builder()
					.add("username", username)
					.add("password", password)
					.add("logintoken", logintoken)
			        .build();
			CookieManager cookieManager = new CookieManager();
			cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
			
			
			client = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar(cookieManager)).build();
			try (Response response = client.newCall(new Request.Builder().url(host + "/login/index.php").post(formBody).build()).execute()) {
				return response.body().string();
			}

		} catch (Exception e) {
			LOGGER.error("Error al intentar loguearse", e);
			throw new IllegalStateException("No se ha podido loguear al servidor");
		}

	}

	public OkHttpClient getClient() {
		return client;
	}

	public void setClient(OkHttpClient client) {
		this.client = client;
	}

	public String findSesskey(String html) {
		
		Pattern pattern = Pattern.compile("sesskey=([\\w]+)");
		Matcher m = pattern.matcher(html);
		if (m.find()) {
			return m.group(1);
		}
		LOGGER.warn("Didn't found a sesskey in principal page after login.");
		return null;
	}

	public String getSesskey() {
		return sesskey;
	}

	public void setSesskey(String sesskey) {
		this.sesskey = sesskey;
	}

	public LocalDateTime getLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(LocalDateTime loggedIn) {
		this.loggedIn = loggedIn;
	}

	/**
	 * @return the directoryCache
	 */
	public Path getDirectoryCache() {
		return directoryCache;
	}

	/**
	 * @return the directoryCache
	 */
	public Path getDirectoryCache(Course course) {
		return directoryCache
				.resolve(Paths.get(UtilMethods.removeReservedChar(course.toString()) + "-" + course.getId()));
	}

	public Path getConfiguration() {
		return configuration;
	}

	public Path getConfiguration(Course course) {
		return configuration
				.resolve(Paths.get(UtilMethods.removeReservedChar(course.toString()) + "-" + course.getId() + ".json"));
	}

	public void setConfiguration(Path configuration) {
		this.configuration = configuration;
	}

	public void setDirectory() {

		String host = UtilMethods.removeReservedChar(this.getUrlHost().getHost());
		String username = UtilMethods.removeReservedChar(this.getUsername());
		this.directoryCache = Paths.get(AppInfo.CACHE_DIR, host, username);
		this.configuration = Paths.get(AppInfo.CONFIGURATION_DIR, host, username);
	}

	/**
	 * @return the offlineMode
	 */
	public boolean isOfflineMode() {
		return offlineMode;
	}

	/**
	 * @param offlineMode the offlineMode to set
	 */
	public void setOfflineMode(boolean offlineMode) {
		this.offlineMode = offlineMode;
	}

	public MainConfiguration getMainConfiguration() {
		return this.mainConfiguration;
	}

	/**
	 * @param mainConfiguration the mainConfiguration to set
	 */
	public void setMainConfiguration(MainConfiguration mainConfiguration) {
		this.mainConfiguration = mainConfiguration;
	}

}
