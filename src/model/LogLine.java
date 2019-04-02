package model;

import java.io.Serializable;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.temporal.IsoFields;

import controllers.ubulogs.logcreator.Component;
import controllers.ubulogs.logcreator.Event;
import model.mod.Module;

public class LogLine implements Serializable {

	private static final long serialVersionUID = 1L;
	private ZonedDateTime time;
	private String eventContext;
	private Component component;
	private Event eventName;
	private String description;
	private String origin;
	private String IPAdress;

	private EnrolledUser user;
	private EnrolledUser affectedUser;
	private Module courseModule;

	public Module getCourseModule() {
		return courseModule;
	}

	public void setCourseModule(Module courseModule) {
		this.courseModule = courseModule;
		courseModule.addLog(this);
	}

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

	public Event getEventName() {
		return eventName;
	}

	public void setEventName(Event eventName) {
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

	public EnrolledUser getUser() {
		return user;
	}

	public void setUser(EnrolledUser user) {
		this.user = user;
		user.addLog(this);
	}

	public EnrolledUser getAffectedUser() {
		return affectedUser;
	}

	public void setAffectedUser(EnrolledUser affectedUser) {
		this.affectedUser = affectedUser;
		affectedUser.addLog(this);
	}

	public int getDayOfMonth() {
		return time.getDayOfMonth();
	}

	public int getDayOfYear() {
		return time.getDayOfYear();
	}

	public Month getMonth() {
		return time.getMonth();
	}

	/**
	 * Devuelve el número de semana del año
	 * 
	 * @return el número de semana del log
	 */
	public int getWeekOfYear() {
		return time.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
	}

	@Override
	public String toString() {
		return "LogLine [time=" + time + ", eventContext=" + eventContext + ", component=" + component + ", eventName="
				+ eventName + ", description=" + description + ", origin=" + origin + ", IPAdress=" + IPAdress
				+ ", user=" + user + ", affectedUser=" + affectedUser + ", courseModule=" + courseModule + "]";
	}



}
