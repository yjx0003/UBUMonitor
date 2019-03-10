package webservice.gradereport;

import webservice.CourseidUseridGroupid;
import webservice.WSFunctions;

public class GradereportUserGetGradeItems extends CourseidUseridGroupid {

	public GradereportUserGetGradeItems(int courseid, OptionalParameter optionalParameter, int userOrGroupid) {
		super(courseid, optionalParameter, userOrGroupid);
	}

	public GradereportUserGetGradeItems( int courseid) {
		super(courseid);

	}

	@Override
	public WSFunctions getWSFunction() {
		
		return  WSFunctions.OBTENER_NOTAS_ALUMNO;
	}

}