package webservice.gradereport;

import java.io.IOException;

import webservice.MoodleOptions;
import webservice.WebService;


public class GradereportUserGetGradeItems extends WebService {
	
	private int courseid;
	private int groupid;
	private int userid;
	
	public enum OptionalParameter{
		USERID,
		GROUPID;
	}
	
	public GradereportUserGetGradeItems(int courseid) {
		super(MoodleOptions.OBTENER_NOTAS_ALUMNO);
		this.courseid=courseid;
	}
	

	
	public GradereportUserGetGradeItems(int courseid,OptionalParameter optionalParameter,int userOrGroupid) {
		
		this(courseid);
		
		switch(optionalParameter) {
		case USERID:
			this.userid=userOrGroupid;break;
		case GROUPID:
			this.groupid=userOrGroupid;break;
		}
		
	}
	
	@Override
	public String getResponse() throws IOException {
		
		String url=this.url;
		
		appendToUrlCourseid(courseid);
		appendToUrlUserid(userid);
		appendToUrlGruopid(groupid);
		
		return getContentWithJsoup(url);
		
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
