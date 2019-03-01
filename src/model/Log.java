package model;

import java.time.ZonedDateTime;

import controllers.UBULogs.logCreator.Component;
import controllers.UBULogs.logCreator.EventName;

public class Log {
	private ZonedDateTime time;
	private String eventContext;
	private Component component;
	private EventName eventName;
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

	public String getEventContext() {
		return eventContext;
	}

	public void setEventContext(String eventContext) {
		this.eventContext = eventContext;
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public EventName getEventName() {
		return eventName;
	}

	public void setEventName(EventName eventName) {
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

	public EnrolledUser getUserFullNameEnrolledUser() {
		return userFullNameEnrolledUser;
	}

	public void setUserFullNameEnrolledUser(EnrolledUser userFullNameEnrolledUser) {
		this.userFullNameEnrolledUser = userFullNameEnrolledUser;
	}

	public EnrolledUser getAffectedUserEnrolledUser() {
		return affectedUserEnrolledUser;
	}

	public void setAffectedUserEnrolledUser(EnrolledUser affectedUserEnrolledUser) {
		this.affectedUserEnrolledUser = affectedUserEnrolledUser;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	@Override
	public String toString() {
		return "Log [time=" + time + ", userFullName=" + ", affectedUser=" + ", eventContext=" + eventContext
				+ ", component=" + component + ", eventName=" + eventName + ", description=" + description + ", origin="
				+ origin + ", IPAdress=" + IPAdress + ", userFullNameEnrolledUser=" + userFullNameEnrolledUser
				+ ", affectedUserEnrolledUser=" + affectedUserEnrolledUser + ", course=" + course + "]";
	}

}
