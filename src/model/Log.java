package model;

import java.time.ZonedDateTime;

public class Log {
	private ZonedDateTime time;
	private String userFullName;
	private String affectedUser;
	private String eventContext;
	private String component;
	private String eventName;
	private String description;
	private String origin;
	private String IPAdress;
	
	
	private EnrolledUser userFullNameEnrolledUser;
	private EnrolledUser affectedUserEnrolledUser;
	private Course course;
	
	

	public ZonedDateTime getTime() {
		return time;
	}
	public void setTime(ZonedDateTime time) {
		this.time = time;
	}
	public String getUserFullName() {
		return userFullName;
	}
	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
	}
	public String getAffectedUser() {
		return affectedUser;
	}
	public void setAffectedUser(String affectedUser) {
		this.affectedUser = affectedUser;
	}
	public String getEventContext() {
		return eventContext;
	}
	public void setEventContext(String eventContext) {
		this.eventContext = eventContext;
	}
	public String getComponent() {
		return component;
	}
	public void setComponent(String component) {
		this.component = component;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public String getIPAdress() {
		return IPAdress;
	}
	public void setIPAdress(String iPAdress) {
		IPAdress = iPAdress;
	}

	@Override
	public String toString() {
		return "Log [time=" + time + ", userFullName=" + userFullName + ", affectedUser=" + affectedUser
				+ ", eventContext=" + eventContext + ", component=" + component + ", eventName=" + eventName
				+ ", description=" + description + ", origin=" + origin + ", IPAdress=" + IPAdress
				+ ", userFullNameEnrolledUser=" + userFullNameEnrolledUser + ", affectedUserEnrolledUser="
				+ affectedUserEnrolledUser + ", course=" + course + "]";
	}
	
}
