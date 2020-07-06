package es.ubu.lsi.ubumonitor.webservice.api.core.users;

import java.util.Collection;
import java.util.Collections;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

/**
 * Retrieve users' information for a specified unique field - If you want to do
 * a user search, use core_user_get_users()
 * 
 * @author Yi Peng Ji
 *
 */
public class CoreUserGetUsersByField extends WSFunctionAbstract {

	public CoreUserGetUsersByField() {
		super(WSFunctionEnum.CORE_USER_GET_USERS_BY_FIELD);
	}

	/**
	 * User id.
	 * @param id user id
	 */
	public void setId(int id) {
		setIds(Collections.singleton(id));
	}

	/**
	 * Multiple user id's 
	 * @param ids Multiple user id's
	 */
	public void setIds(Collection<Integer> ids) {
		setField("id", ids);
	}

	/**
	 * ID number. 
	 * @param idnumber idNumber
	 */
	public void setIdnumber(int idnumber) {
		setIdnumbers(Collections.singleton(idnumber));
	}

	/**
	 * ID numbers
	 * @param idnumbers multiple ID numbers
	 */
	public void setIdnumbers(Collection<Integer> idnumbers) {
		setField("idnumber", idnumbers);
	}

	/**
	 * Username.
	 * @param username username
	 */
	public void setUsername(String username) {
		setUsernames(Collections.singleton(username));
	}
	/**
	 * Usernames.
	 * @param usernames usernames
	 */
	public void setUsernames(Collection<String> usernames) {
		setField("username", usernames);
	}

	/**
	 * Email.
	 * @param email email
	 */
	public void setEmail(String email) {
		setEmails(Collections.singleton(email));
	}
	/**
	 * Multiple emails.
	 * @param emails multiple emails
	 */
	public void setEmails(Collection<String> emails) {
		setField("email", emails);
	}

	private void setField(String field, Collection<?> values) {
		parameters.put("field", field);
		parameters.put("values", values);

	}
}
