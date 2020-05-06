package es.ubu.lsi.ubumonitor.webservice.api.core.completion;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreCompletionGetActivitiesCompletionStatus extends WSFunctionAbstract{

	private int courseid;
	private int userid;
	
	public CoreCompletionGetActivitiesCompletionStatus(int courseid, int userid) {
		super(WSFunctionEnum.CORE_COMPLETION_GET_ACTIVITIES_COMPLETION_STATUS);
		this.courseid = courseid;
		this.userid = userid;
	}

	@Override
	public void addToMapParemeters() {
		parameters.put("courseid", String.valueOf(courseid));
		parameters.put("userid", String.valueOf(userid));
		
	}

}
