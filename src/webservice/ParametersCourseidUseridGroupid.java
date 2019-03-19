package webservice;

public abstract class ParametersCourseidUseridGroupid extends WebService {

	private int courseid;
	private int groupid;
	private int userid;

	public enum OptionalParameter {
		USERID, GROUPID;
	}

	public ParametersCourseidUseridGroupid(int courseid) {
		this.courseid = courseid;
	}

	public ParametersCourseidUseridGroupid(int courseid, OptionalParameter optionalParameter, int userOrGroupid) {

		this(courseid);

		switch (optionalParameter) {
		case USERID:
			this.userid = userOrGroupid;
			break;
		case GROUPID:
			this.groupid = userOrGroupid;
			break;
		}

	}

	@Override
	public void appendToUrlParameters() {

		appendToUrlCourseid(courseid);
		if (userid!=0)
		appendToUrlUserid(userid);
		if(groupid!=0)
		appendToUrlGruopid(groupid);

	}

	public int getCourseid() {
		return courseid;
	}

	public void setCourseid(int courseid) {
		this.courseid = courseid;
	}

	public int getGroupid() {
		return groupid;
	}

	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

}
