package model;

import java.util.Date;

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
	private Date timeModified;
	private int scaleId;
	
	private static final long serialVersionUID = 1L;

	public Assignment(String itemName, String type, float weight, String minRange, String maxRange) {
		super(itemName, type, weight, minRange, maxRange);
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

	/**
	 * @return the scaleId
	 */
	public int getScaleId() {
		return scaleId;
	}

	/**
	 * @param scaleId the scaleId to set
	 */
	public void setScaleId(int scaleId) {
		this.scaleId = scaleId;
	}
}
