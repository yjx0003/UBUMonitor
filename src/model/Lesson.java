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
	 * Consturctor de una Lesson.
	 * 
	 */
	public Lesson(String itemName, String type, float weight, String minRange, String maxRange) {
		super(itemName, type, weight, minRange, maxRange);
	}



	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}



	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}



	/**
	 * @return the maxAnswers
	 */
	public int getMaxAnswers() {
		return maxAnswers;
	}



	/**
	 * @param maxAnswers the maxAnswers to set
	 */
	public void setMaxAnswers(int maxAnswers) {
		this.maxAnswers = maxAnswers;
	}



	/**
	 * @return the maxAttempts
	 */
	public int getMaxAttempts() {
		return maxAttempts;
	}



	/**
	 * @param maxAttempts the maxAttempts to set
	 */
	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}



	/**
	 * @return the timeLimit
	 */
	public int getTimeLimit() {
		return timeLimit;
	}



	/**
	 * @param timeLimit the timeLimit to set
	 */
	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}



	/**
	 * @return the deadLine
	 */
	public int getDeadLine() {
		return deadLine;
	}



	/**
	 * @param deadLine the deadLine to set
	 */
	public void setDeadLine(int deadLine) {
		this.deadLine = deadLine;
	}



	/**
	 * @return the retake
	 */
	public boolean isRetake() {
		return retake;
	}



	/**
	 * @param retake the retake to set
	 */
	public void setRetake(boolean retake) {
		this.retake = retake;
	}

}
