package model;

import java.util.Date;

import org.json.JSONObject;

/**
 * Clase Quiz (Cuestionario). Implementar en versiones futuras.
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class Quiz extends Activity {
	private Date timeOpen;
	private Date timeClose;
	private String password;
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor de un Quiz
	 * 
	 * @param token
	 * @param obj
	 * @throws Exception
	 */
	public Quiz(String token, JSONObject obj) throws Exception {
		super(token, obj);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Devuelve la fecha de apertura del cuestionario
	 * 
	 * @return fecha de apertura
	 */
	public Date getTimeOpen() {
		return timeOpen;
	}

	/**
	 * Modifica la fecha de apertura del cuestionario
	 * 
	 * @param timeOpen
	 *            fecha de apertura
	 */
	public void setTimeOpen(Date timeOpen) {
		this.timeOpen = timeOpen;
	}

	/**
	 * Devuelve la fecha de cierre del cuestionario
	 * 
	 * @return fecha de cierre
	 */
	public Date getTimeClose() {
		return timeClose;
	}

	/**
	 * Modifica la fecha de cierre del cuestionario
	 * 
	 * @param timeClose
	 *            fecha de cierre
	 */
	public void setTimeClose(Date timeClose) {
		this.timeClose = timeClose;
	}

	/**
	 * Devuelve la contraseña del cuestionario
	 * 
	 * @return contraseña
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Modifica la contraseña del usuario
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
