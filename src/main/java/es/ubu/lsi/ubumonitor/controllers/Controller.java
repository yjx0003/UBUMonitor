package es.ubu.lsi.ubumonitor.controllers;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.ubu.lsi.ubumonitor.AppInfo;
import es.ubu.lsi.ubumonitor.controllers.configuration.ConfigHelper;
import es.ubu.lsi.ubumonitor.controllers.configuration.MainConfiguration;
import es.ubu.lsi.ubumonitor.controllers.load.Login;
import es.ubu.lsi.ubumonitor.model.Course;
import es.ubu.lsi.ubumonitor.model.DataBase;
import es.ubu.lsi.ubumonitor.model.MoodleUser;
import es.ubu.lsi.ubumonitor.model.Stats;
import es.ubu.lsi.ubumonitor.util.I18n;
import es.ubu.lsi.ubumonitor.util.Languages;
import es.ubu.lsi.ubumonitor.util.UtilMethods;
import es.ubu.lsi.ubumonitor.webservice.webservices.WebService;
import javafx.stage.Stage;

public class Controller {
	private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
			.ofLocalizedDateTime(FormatStyle.SHORT);

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

	private Languages selectedLanguage;

	private DataBase dataBase;
	private LocalDateTime loggedIn;
	private Path hostUserModelversionArchivedDir;
	private Path hostUserModelversionDir;
	private Path hostUserDir;
	private URL host;
	private Stage stage;
	private Login login;
	private String username;
	private String password;
	private boolean offlineMode;

	private MainConfiguration mainConfiguration;

	/**
	 * Usuario actual.
	 */
	private MoodleUser user;

	private Path configuration;

	private ZonedDateTime defaultUpdate;

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
		
		ConfigHelper.initialize(AppInfo.PROPERTIES_PATH);
		setDataBase(new DataBase());
		// Si no existe el recurso de idioma especificado cargamos el Español
		Languages lang = Languages.getLanguageByTag(ConfigHelper.getProperty("language", Locale.getDefault()
				.toLanguageTag()));
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
		ConfigHelper.setProperty("language", a.toLanguageTag());
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
	 * Devuelve las estadisticas de calificaciones del curso.
	 * 
	 * @return estadistica de calificaciones del curso
	 */
	public Stats getStats() {

		return getActualCourse().getStats();
	}

	/**
	 * Selecciona el curso en la base de datos.
	 * 
	 * @param selectedCourse curso seleccionado
	 */
	public void setActualCourse(Course selectedCourse) {
		dataBase.setActualCourse(selectedCourse);
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
	public Path getHostUserModelversionDir() {
		return hostUserModelversionDir;
	}

	public Path getCourseFile(Course course) {
		return Paths.get(getCoursePathName(course));
	}

	public String getCoursePathName(Course course) {
		return UtilMethods.removeReservedChar(course.toString()) + "-" + course.getId();
	}

	public Path getHostUserDir() {
		return hostUserDir;
	}

	public void setHostUserDir(Path hostUserDir) {
		this.hostUserDir = hostUserDir;
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

		String hostName = UtilMethods.removeReservedChar(this.getUrlHost()
				.getHost());
		String userName = UtilMethods.removeReservedChar(this.getUsername());
		this.hostUserModelversionDir = Paths.get(AppInfo.CACHE_DIR, hostName, userName, AppInfo.MODEL_VERSION);
		this.hostUserModelversionArchivedDir = Paths.get(AppInfo.CACHE_DIR, hostName, userName, AppInfo.MODEL_VERSION,
				AppInfo.ARCHIVED_DIR);
		this.hostUserDir = Paths.get(AppInfo.CACHE_DIR, hostName, userName);
		this.configuration = Paths.get(AppInfo.CONFIGURATION_DIR, hostName, userName);
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

	public ZonedDateTime getUpdatedCourseData() {
		if (dataBase.getActualCourse()
				.getUpdatedCourseData() == null) {
			return defaultUpdate;
		}
		return dataBase.getActualCourse()
				.getUpdatedCourseData();
	}

	public ZonedDateTime getUpdatedGradeItem() {
		if (dataBase.getActualCourse()
				.getUpdatedGradeItem() == null) {
			return defaultUpdate;
		}
		return dataBase.getActualCourse()
				.getUpdatedGradeItem();
	}

	public ZonedDateTime getUpdatedActivityCompletion() {
		if (dataBase.getActualCourse()
				.getUpdatedActivityCompletion() == null) {
			return defaultUpdate;
		}
		return dataBase.getActualCourse()
				.getUpdatedActivityCompletion();
	}

	public ZonedDateTime getUpdatedLog() {
		if (dataBase.getActualCourse()
				.getUpdatedLog() == null) {
			return defaultUpdate;
		}

		return dataBase.getActualCourse()
				.getUpdatedLog();
	}

	public ZonedDateTime getDefaultUpdate() {
		return defaultUpdate;
	}

	public void setDefaultUpdate(ZonedDateTime defaultUpdate) {
		this.defaultUpdate = defaultUpdate;
	}

	/**
	 * @return the webService
	 */
	public WebService getWebService() {
		return login.getWebService();
	}

	public Login getLogin() {
		return login;
	}

	public void setLogin(Login login) {
		this.login = login;
	}

	public void tryLogin(String host, String username, String password) throws IOException {

		login = new Login();
		String validHost = login.checkUrlServer(host);
		login.tryLogin(validHost, username, password);
		this.host = new URL(validHost);
	}

	public Path getHostUserModelversionArchivedDir() {
		return hostUserModelversionArchivedDir;
	}

}
