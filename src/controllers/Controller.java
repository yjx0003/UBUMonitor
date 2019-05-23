package controllers;

import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.json.JSONException;
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

	public static final String RESOURCE_FILE_NAME = "messages/Messages";


	private Languages selectedLanguage;

	private BBDD DEFAULT_BBDD;
	
	private BBDD BBDD;

	private String host;
	private Stage stage;
	private String password;
	private String username;

	/**
	 * Usuario actual.
	 */
	private MoodleUser user;
	/**
	 * ResourceBundle(idioma) actual.
	 */

	private ResourceBundle resourceBundle;

	/**
	 * static Singleton instance.
	 */
	private static Controller instance;

	/**
	 * Private constructor for singleton.
	 */
	private Controller() {
	}

	/**
	 * Return a singleton instance of Controller.
	 */
	public static Controller getInstance() {
		if (instance == null) {
			instance = new Controller();
		}
		return instance;
	}

	public void initialize() {
		BBDD BBDD=new BBDD();
		setBBDD(BBDD);
		setDefaultBBDD(BBDD);
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
		this.resourceBundle = ResourceBundle.getBundle(RESOURCE_FILE_NAME);
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
	 * @return the resourceBundle
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public void tryLogin(String host, String username, String password) throws JSONException, IOException {
		WebService.initialize(host, username, password);

		setHost(host);

		setUsername(username);
		setPassword(password);
	}

	public Stats getStats() {

		return getActualCourse().getStats();
	}

	public void createStats() throws Exception {
		Stats stats = new Stats(getActualCourse());
		getActualCourse().setStats(stats);
		
		LogStats logStats=new LogStats(getActualCourse().getLogs().getList(),getActualCourse().getEnrolledUsers());
		getActualCourse().setLogStats(logStats);
	}

	public void setActualCourse(Course selectedCourse) {
		BBDD.setActualCourse(selectedCourse);
	}

	public BBDD getDefaultBBDD() {
		return DEFAULT_BBDD;
	}

	public void setDefaultBBDD(BBDD BBDD) {
		DEFAULT_BBDD = BBDD;
	}

}
