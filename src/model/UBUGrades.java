package model;

import java.util.ResourceBundle;

import controllers.ubugrades.GettersUBUGrades;
import controllers.ubugrades.UbuGradesFactory;
import javafx.stage.Stage;

/**
 * Clase UBUGrades.
 * 
 * @author Félix Nogal Santamaría
 * @version 1.0
 *
 */
public class UBUGrades {
	/**
	 * Instancia de UBUGrades.
	 */
	private static UBUGrades ubuGrades = null;
	/**
	 * Host sobre el que se utiliza la aplicación.
	 */
	private String host;
	/**
	 * Stage actual.
	 */
	private Stage stage;
	
	private String password;
	private String username;
	private Course actualCourse;
	/**
	 * Usuario actual.
	 */
	private MoodleUser user;
	/**
	 * ResourceBundle(idioma) actual.
	 */
	private ResourceBundle resourceBundle;
	
	/**
	 * Genera una instancia de UBUGrades en caso de no estar generada.
	 * @return
	 * 		La instancia de UBUGrades.
	 */
	public static UBUGrades getInstance() {
		if(ubuGrades == null) {
			ubuGrades = new UBUGrades();
		}
		return ubuGrades;
	}

	/**
	 * Constructor de la clase.
	 */
	protected UBUGrades() {}
	
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
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
		return actualCourse;
	}

	public void setActualCourse(Course actualCourse) {
		this.actualCourse = actualCourse;
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
	 * @return the resourceBundle
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/**
	 * @param resourceBundle the resourceBundle to set
	 */
	public void setResourceBundle(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}
}
