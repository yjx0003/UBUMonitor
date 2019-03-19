package webservice.gradereport;

import webservice.ParametersCourseidUseridGroupid;
import webservice.WSFunctions;

public class GradereportUserGetGradesTable extends ParametersCourseidUseridGroupid {


	public GradereportUserGetGradesTable(int courseid, OptionalParameter optionalParameter, int userOrGroupid) {
		super( courseid, optionalParameter, userOrGroupid);
	}

	public GradereportUserGetGradesTable(int courseid) {
		super(courseid);

	}

	@Override
	public WSFunctions getWSFunction() {
		return WSFunctions.GRADEREPORT_USER_GET_GRADES_TABLE;
	}

}
