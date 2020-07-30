package es.ubu.lsi.ubumonitor.webservice.api.mod.forum;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionAbstract;
import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

public class ModForumGetForumDiscussionPosts extends WSFunctionAbstract {

	public ModForumGetForumDiscussionPosts(int discussionid) {
		super(WSFunctionEnum.MOD_FORUM_GET_FORUM_DISCUSSION_POSTS);
		setDiscussionid(discussionid);
	}
	
	/**
	 * discussion ID
	 * @param discussionid discussion ID
	 */
	public void setDiscussionid(int discussionid) {
		parameters.put("discussionid", discussionid);
	}
	
	/**
	 * sort by this element: id, created or modified
	 * @param sortby sort by this element: id, created or modified
	 */
	public void setSortby(String sortby) {
		parameters.put("sortby", sortby);
	}
	
	/**
	 * sort direction: ASC or DESC
	 * @param sortdirection sort direction: ASC or DESC
	 */
	public void setSortdirection(String sortdirection) {
		parameters.put("sortdirection", sortdirection);
	}
}
