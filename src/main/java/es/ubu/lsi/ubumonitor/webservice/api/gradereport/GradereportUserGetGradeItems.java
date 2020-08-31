package es.ubu.lsi.ubumonitor.webservice.api.gradereport;

import es.ubu.lsi.ubumonitor.webservice.webservices.WSFunctionEnum;

/**
 * Returns the complete list of grade items for users in a course.
 * 
 * @author Yi Peng Ji
 *
 */
public class GradereportUserGetGradeItems extends GradereportUserGetGradesTable {

	public GradereportUserGetGradeItems(int courseid) {
		super(WSFunctionEnum.GRADEREPORT_USER_GET_GRADE_ITEMS, courseid);
	}

}
