package model;

import java.util.Date;

import org.json.JSONObject;

/**
 * Clase Assignment (Tarea). Implementar versiones futuras.
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class Assignment extends Activity {
	private Date dueDate;
	private Date allowSubmissionsFromDate;
	public Date timeModified;
	private static final long serialVersionUID = 1L;

	public Assignment(String token, JSONObject obj) throws Exception {
		super(token, obj);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Devuelve la fecha de vencimiento
	 * 
	 * @return dueDate
	 */
	public Date getDueDate() {
		return dueDate;
	}

	/**
	 * Modifica la fecha de vencimiento
	 * 
	 * @param dueDate
	 */
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * Devuelve el inicio de la fecha de entrega
	 * 
	 * @return allowSubmissionsFromDate
	 */
	public Date getAllowSubmissionsFromDate() {
		return allowSubmissionsFromDate;
	}

	/**
	 * Modifica el inicio de la fecha de entrega
	 * 
	 * @param allowSubmissionsFromDate
	 */
	public void setAllowSubmissionsFromDate(Date allowSubmissionsFromDate) {
		this.allowSubmissionsFromDate = allowSubmissionsFromDate;
	}

	/**
	 * Devuelve la fecha en que la entrega fue modificada
	 * 
	 * @return timeModified
	 */
	public Date getTimeModified() {
		return this.timeModified;
	}

	/**
	 * Modifica
	 * 
	 * @param timeModified
	 */
	public void setTimeModified(Date timeModified) {
		this.timeModified = timeModified;
	}
}
