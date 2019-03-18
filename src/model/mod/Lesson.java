package model;

/**
 * Clase Lesson (Leción). Implementar en versiones futuras.
 * 
 * @author Félix Nogal Santamaría
 * @version 1.0
 *
 */
public class Lesson extends Activity {

	private static final long serialVersionUID = 1L;
	private String password;
	private int maxAnswers;
	private int maxAttempts;
	private int timeLimit; // 0 indica que no hay tiempo limite
	private int deadLine; // 0 indica que no hay fecha limite
	private boolean retake; // indica si se puede repetir la lección
	
	/**
	 * Consturctor de una Lección(Lesson).
	 * 
	 * @param itemName
	 * 		El nombre de la lección.
	 * @param type
	 * 		El tipo.
	 * @param weight
	 * 		El peso.
	 * @param minRange
	 * 		El rango mínimo.
	 * @param maxRange
	 * 		El rango máximo.
	 */
	public Lesson(String itemName, String type, float weight, String minRange, String maxRange) {
		super(itemName, type, weight, minRange, maxRange);
	}



	/**
	 * Devuelve la contraseña de la lección.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}



	/**
	 * Modifica la contraseña de la lección.
	 * @param password
	 * 		La contraseña.
	 */
	public void setPassword(String password) {
		this.password = password;
	}



	/**
	 * Devuelve el número máximo de respuestas.
	 * 
	 * @return the maxAnswers
	 */
	public int getMaxAnswers() {
		return maxAnswers;
	}



	/**
	 * Modifica el número máximo de respuestas.
	 * @param maxAnswers
	 * 		El número máximo de respuestas.
	 */
	public void setMaxAnswers(int maxAnswers) {
		this.maxAnswers = maxAnswers;
	}



	/**
	 * Devuelve el numero maximo de intentos.
	 * 
	 * @return the maxAttempts
	 */
	public int getMaxAttempts() {
		return maxAttempts;
	}



	/**
	 * Modifica el número máximo de intentos.
	 * @param maxAttempts
	 * 		El número máximo de intentos.
	 */
	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}



	/**
	 * Devuelve 1 si la lección tiene tiempo límite y 0 si no.
	 * 
	 * @return the timeLimit
	 */
	public int getTimeLimit() {
		return timeLimit;
	}



	/**
	 * Modifica el tiempo límite de la lección.
	 * 
	 * @param timeLimit
	 * 		El tiempo límite de la lección.
	 */
	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}



	/**
	 * Devuelve 0 si la lección tiene deadline y 0 si no.
	 *
	 * @return the deadLine
	 */
	public int getDeadLine() {
		return deadLine;
	}



	/**
	 * Modifica la deadLine de la lección.
	 * 
	 * @param deadLine
	 * 		El deadLine.
	 */
	public void setDeadLine(int deadLine) {
		this.deadLine = deadLine;
	}



	/**
	 * Devuelve si la lección se puede repetir o no.
	 * 
	 * @return the retake
	 */
	public boolean isRetake() {
		return retake;
	}



	/**
	 * Modifica si la leccion se puede repetir o no.
	 * 
	 * @param retake
	 * 		True si se pude, False si no se puede.
	 * 	
	 */
	public void setRetake(boolean retake) {
		this.retake = retake;
	}

}
