package es.ubu.lsi.ubumonitor.webservice.api.core.message;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreMessageGetConversationBetweenUsers extends WSFunctionAbstract {

	public CoreMessageGetConversationBetweenUsers(int userid, int otherserid, boolean includecontactrequests, boolean includeprivacyinfo) {
		super(WSFunctionEnum.CORE_MESSAGE_GET_CONVERSATION_BETWEEN_USERS);
		setUserid(userid);
		setOtheruserid(otherserid);
		setIncludecontactrequests(includecontactrequests);
		setIncludeprivacyinfo(includeprivacyinfo);
	}
	
	

	/**
	 * The id of the user who we are viewing conversations for
	 * 
	 * @param userid the id of the user who we are viewing conversations for
	 */
	public void setUserid(int userid) {
		parameters.put("userid", userid);
	}

	/**
	 * The other user id
	 * 
	 * @param userid the other user id
	 */
	public void setOtheruserid(int otheruserid) {
		parameters.put("otheruserid", otheruserid);
	}

	/**
	 * Include contact requests in the members
	 * 
	 * @param includecontactrequests
	 */
	public void setIncludecontactrequests(boolean includecontactrequests) {
		parameters.put("includecontactrequests", includecontactrequests ? 1 : 0);
	}

	/**
	 * Include privacy info in the members
	 * 
	 * @param includeprivacyinfo
	 */
	public void setIncludeprivacyinfo(boolean includeprivacyinfo) {
		parameters.put("includeprivacyinfo", includeprivacyinfo ? 1 : 0);
	}

	/**
	 * Limit for number of members
	 * 
	 * @param memberlimit
	 */
	public void memberlimit(int memberlimit) {
		parameters.put("memberlimit", memberlimit);
	}

	/**
	 * Offset for member list
	 * 
	 * @param memberoffset
	 */
	public void memberoffset(int memberoffset) {
		parameters.put("memberoffset", memberoffset);
	}

	/**
	 * Limit for number of messages
	 * 
	 * @param messagelimit
	 */
	public void messagelimit(int messagelimit) {
		parameters.put("messagelimit", messagelimit);
	}

	/**
	 * Offset for messages list
	 * 
	 * @param messageoffset
	 */
	public void messageoffset(int messageoffset) {
		parameters.put("messageoffset", messageoffset);
	}

	/**
	 * Order messages by newest first
	 * 
	 * @param newestmessagesfirst
	 */
	public void newestmessagesfirst(boolean newestmessagesfirst) {
		parameters.put("newestmessagesfirst", newestmessagesfirst ? 1 : 0);
	}

}
