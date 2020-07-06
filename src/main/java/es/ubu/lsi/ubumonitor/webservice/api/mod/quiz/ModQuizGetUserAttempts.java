package es.ubu.lsi.ubumonitor.webservice.api.mod.quiz;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

/**
 * 
 * @author Yi Peng Ji
 *
 */
public class ModQuizGetUserAttempts extends WSFunctionAbstract {

	public ModQuizGetUserAttempts() {
		super(WSFunctionEnum.MOD_QUIZ_GET_USER_ATTEMPTS);
	}

	public ModQuizGetUserAttempts(int quizid, int userid) {
		this(quizid);
		setUserid(userid);
	}

	public ModQuizGetUserAttempts(int quizid) {
		this();
		setQuizid(quizid);

	}

	/**
	 * Quiz instance id.
	 * 
	 * @param quizid quiz id
	 */
	public void setQuizid(int quizid) {
		parameters.put("quizid", quizid);
	}

	/**
	 * User id, empty for current user.
	 * 
	 * @param userid user id
	 */
	public void setUserid(int userid) {
		parameters.put("userid", userid);
	}

}
