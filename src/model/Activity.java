package model;

import java.io.Serializable;

import org.json.JSONObject;

/**
 * Clase Activity. Implementar en el futuro.
 * 
 * @author Claudia Martínez Herrero
 * @version 1.0
 *
 */
public class Activity implements Serializable {

	private static final long serialVersionUID = 1L;
	private String itemName;
	private String activityType;
	private float weight;
	private String minRange;
	private String maxRange;
	private float contributionCourseTotal;

	/**
	 * Constructor de Activity
	 * 
	 * @param token
	 *            token de MoodleUser
	 * @param obj
	 *            objeto JSON con la información de la actividad
	 * @throws Exception
	 */
	public Activity(String token, JSONObject obj) throws Exception {
		// TODO
	}

	/**
	 * Constructor de una actividad con todos sus parámetros
	 * 
	 * @param itemName
	 *            nombre de la actividad
	 * @param type
	 *            tipo de actividad
	 * @param weight
	 *            peso
	 * @param minRange
	 *            rango mínimo de nota
	 * @param maxRange
	 *            rango máximo de nota
	 */
	public Activity(String itemName, String type, float weight, String minRange, String maxRange) {
		this.itemName = itemName;
		this.activityType = type;
		this.weight = weight;
		this.minRange = minRange;
		this.maxRange = maxRange;
	}

	/**
	 * Devuelve el nombre de la actividad
	 * 
	 * @return nombre de la actividad
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * Modifica el nombre de la actividad
	 * 
	 * @param itemName
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * Devuelve el tipo de la actividad
	 * 
	 * @return tipo de actividad
	 */
	public String getActivityType() {
		return activityType;
	}

	/**
	 * Modifica el tipo de la actividad
	 * 
	 * @param activityType
	 */
	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	/**
	 * Devuelve el peso de la actividad
	 * 
	 * @return peso
	 */
	public float getWeight() {
		return weight;
	}

	/**
	 * Modifica el peso de la actividad
	 * 
	 * @param weight
	 */
	public void setWeight(float weight) {
		this.weight = weight;
	}

	/**
	 * Devuelve el rango mínimo
	 * 
	 * @return minRange
	 */
	public String getMinRange() {
		return minRange;
	}

	/**
	 * Modifica el rango mínimo
	 * 
	 * @param minRange
	 */
	public void setMinRange(String minRange) {
		this.minRange = minRange;
	}

	/**
	 * Devuelve el rango máximo
	 * 
	 * @return maxRange
	 */
	public String getMaxRange() {
		return maxRange;
	}

	/**
	 * Modifica el rango máximo
	 * 
	 * @param maxRange
	 */
	public void setMaxRange(String maxRange) {
		this.maxRange = maxRange;
	}

	/**
	 * Devuelve la contribución total de la actividad
	 * 
	 * @return contributionCourseTotal
	 */
	public float getContributionCourseTotal() {
		return contributionCourseTotal;
	}

	/**
	 * Modifica contrila bución total de la actividad
	 * 
	 * @param contributionCourseTotal
	 */
	public void setContributionCourseTotal(float contributionCourseTotal) {
		this.contributionCourseTotal = contributionCourseTotal;
	}

}
