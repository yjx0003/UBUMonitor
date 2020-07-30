package es.ubu.lsi.ubumonitor.webservice.api.mod.forum;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

/**
 * Returns a list of forum discussions optionally sorted and paginated. 
 * @author Yi Peng Ji
 *
 */
public class ModForumGetForumDiscussions extends WSFunctionAbstract {

	public ModForumGetForumDiscussions(int forumid) {
		super(WSFunctionEnum.MOD_FORUM_GET_FORUM_DISCUSSIONS);
		setForumid(forumid);
	}

	public void setForumid(int forumid) {
		parameters.put("forumid", forumid);
	}

	/**
	 * Sort by this element: numreplies, , created or timemodified.
	 * 
	 * @param sortorder numreplies, , created or timemodified.
	 */
	public void setSortorder(int sortorder) {
		parameters.put("sortorder", sortorder);
	}

	/**
	 * Current page.
	 * 
	 * @param page current page.
	 */
	public void setPage(int page) {
		parameters.put("page", page);
	}

	/**
	 * Items per page.
	 * @param perpage Items per page.
	 */
	public void setPerpage(int perpage) {
		parameters.put("perpage", perpage);
	}
	
	/**
	 * Group id
	 * @param groupid group id
	 */
	public void setGroupid(int groupid) {
		parameters.put("groupid", groupid);
	}
	
}
