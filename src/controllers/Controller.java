package controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controllers.ubugrades.CreatorUBUGradesController;
import controllers.ubulogs.logcreator.logtypes.ReferencesLog;
import javafx.stage.Stage;
import model.BBDD;
import model.Course;
import model.MoodleUser;
import model.Stats;
import webservice.WebService;

public class Controller {
	static final Logger logger = LoggerFactory.getLogger(Controller.class);

	public static final String RESOURCE_FILE_NAME = "messages/Messages";

	private Languages selectedLanguage;

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

	private Stats stats;
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
		setBBDD(new BBDD());

		// Si no existe el recurso de idioma especificado cargamos el Español
		try {
			Languages language = Languages.getLanguageByCode(System.getProperty("user.language"));
			resourceBundle = ResourceBundle.getBundle(RESOURCE_FILE_NAME,
					language.getLocale());
			setSelectedLanguage(language);
			logger.info("Cargando idoma del sistema: {}", getResourceBundle().getLocale());
		} catch (NullPointerException | MissingResourceException e) {
			logger.error("No se ha podido encontrar el recurso de idioma, cargando en_en: {}", e);
		}
	}

	public Languages getSelectedLanguage() {
		return selectedLanguage;
	}

	public void setSelectedLanguage(Languages selectedLanguage) {
		this.selectedLanguage = selectedLanguage;
	}

	public void setBBDD(BBDD BBDD) {
		CreatorUBUGradesController.setBBDD(BBDD);
		ReferencesLog.setBBDD(BBDD);
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

	public void setResourceBundle(Languages language) {
		setSelectedLanguage(language);
		this.resourceBundle = ResourceBundle.getBundle(RESOURCE_FILE_NAME, language.getLocale());
	}

	public void tryLogin(String host, String username, String password) throws JSONException, IOException {
		WebService.initialize(host, username, password);

		setHost(host);

		setUsername(username);
		setPassword(password);
	}

	public Stats getStats() {

		return stats;
	}

	public void createStats() throws Exception {
		stats = new Stats(BBDD.getActualCourse(), selectedLanguage.getLocale());

	}

	public void setActualCourse(Course selectedCourse) {
		BBDD.setActualCourse(selectedCourse);

	}

}
