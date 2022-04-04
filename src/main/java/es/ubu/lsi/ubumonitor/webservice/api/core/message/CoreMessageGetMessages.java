package es.ubu.lsi.ubumonitor.webservice.api.core.message;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class CoreMessageGetMessages extends WSFunctionAbstract {

	public CoreMessageGetMessages(int userdto) {
		super(WSFunctionEnum.CORE_MESSAGE_GET_MESSAGES);
		setUseridto(userdto);
	}

	/**
	 * the user id who received the message, 0 for any user
	 * 
	 * @param userdto
	 */
	public void setUseridto(int userdto) {
		parameters.put("userdto", userdto);
	}

	/**
	 * the user id who send the message, 0 for any user. -10 or -20 for no-reply or
	 * support user
	 * 
	 * @param userfrom
	 */
	public void setUserfrom(int userfrom) {
		parameters.put("userfrom", userfrom);
	}

	/**
	 * type of message to return, expected values are: notifications, conversations
	 * and both
	 * 
	 * @param type
	 */
	public void setType(String type) {
		parameters.put("type", type);
	}

	/**
	 * true for getting read messages, false for unread
	 * 
	 * @param read
	 */
	public void setRead(boolean read) {
		parameters.put("read", read ? 1 : 0);
	}
	/**
	 * true for ordering by newest first, false for oldest first
	 * @param newestfirst
	 */
	public void setNewestfirst(boolean newestfirst) {
		parameters.put("newestfirst", newestfirst ? 1 : 0);
	}
	
	/**
	 * limit from
	 * @param limitfrom
	 */
	public void setLimitfrom(int limitfrom) {
		parameters.put("limitfrom", limitfrom);
	}

	public void setLimitnum(int limitnum) {
		parameters.put("limitnum", limitnum);
	}
}
