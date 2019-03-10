package webservice.gradereport;

import webservice.CourseidUseridGroupid;
import webservice.WSFunctions;

public class GradereportUserGetGradesTable extends CourseidUseridGroupid {


	public GradereportUserGetGradesTable(int courseid, OptionalParameter optionalParameter, int userOrGroupid) {
		super( courseid, optionalParameter, userOrGroupid);
	}

	public GradereportUserGetGradesTable(WSFunctions wsFunction, int courseid) {
		super(courseid);

	}

	@Override
	public WSFunctions getWSFunction() {
		return WSFunctions.OBTENER_TABLA_NOTAS;
	}

}
