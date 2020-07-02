package es.ubu.lsi.ubumonitor.webservice.api.mod.quiz;

import es.ubu.lsi.ubumonitor.webservice.webservices.Util;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class ModQuizGetUserAttempts extends WSFunctionAbstract {

	private Integer userid;
	private Integer quizid;

	public ModQuizGetUserAttempts() {
		super(WSFunctionEnum.MOD_QUIZ_GET_USER_ATTEMPTS);
	}

	@Override
	public void addToMapParemeters() {
		Util.putIfNotNull(parameters, "userid", userid);
		Util.putIfNotNull(parameters, "quizid", quizid);

	}

	/**
	 * @return the userid
	 */
	public Integer getUserid() {
		return userid;
	}

	/**
	 * @param userid the userid to set
	 */
	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	/**
	 * @return the quizid
	 */
	public Integer getQuizid() {
		return quizid;
	}

	/**
	 * @param quizid the quizid to set
	 */
	public void setQuizid(Integer quizid) {
		this.quizid = quizid;
	}

}
