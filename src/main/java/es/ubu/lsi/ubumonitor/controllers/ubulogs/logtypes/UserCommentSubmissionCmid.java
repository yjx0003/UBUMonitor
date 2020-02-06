package es.ubu.lsi.ubumonitor.controllers.ubulogs.logtypes;

import java.util.List;

import es.ubu.lsi.ubumonitor.model.LogLine;
/**
 * 
 * The user with id '' added the comment with id '' to the submission with id '' for the assignment with course module id ''.
 * The user with id '' deleted the comment with id '' from the submission with id '' for the assignment with course module id ''.
 * 
 * @author Yi Peng Ji
 *
 */
public class UserCommentSubmissionCmid extends ReferencesLog{
	
	/**
	 * Instacia única de la clase UserCommentSubmissionCmid.
	 */
	private static UserCommentSubmissionCmid instance;

	/**
	 * Constructor privado de la clase singleton.
	 */
	private UserCommentSubmissionCmid() {
	}

	/**
	 * Devuelve la instancia única de UserCommentSubmissionCmid.
	 * @return instancia singleton
	 */
	public static UserCommentSubmissionCmid getInstance() {
		if (instance == null) {
			instance = new UserCommentSubmissionCmid();
		}
		return instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLogReferencesAttributes(LogLine log, List<Integer> ids) {
		setUserById(log, ids.get(0));
		//comment id core_comment_get_comments
		//submission id mod_assign_get_submissions
		setCourseModuleById(log, ids.get(3));
		
	}

}
