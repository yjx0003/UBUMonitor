package webservice.gradereport;

import webservice.ParametersCourseidUseridGroupid;
import webservice.WSFunctions;

public class GradereportUserGetGradeItems extends ParametersCourseidUseridGroupid {

	public GradereportUserGetGradeItems(int courseid, OptionalParameter optionalParameter, int userOrGroupid) {
		super(courseid, optionalParameter, userOrGroupid);
	}

	public GradereportUserGetGradeItems( int courseid) {
		super(courseid);

	}

	@Override
	public WSFunctions getWSFunction() {
		return  WSFunctions.GRADEREPORT_USER_GET_GRADE_ITEMS;
	}

}