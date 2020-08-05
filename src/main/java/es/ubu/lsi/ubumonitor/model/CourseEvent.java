package es.ubu.lsi.ubumonitor.model;

import java.io.Serializable;
import java.time.Instant;

public class CourseEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private String description;
	private DescriptionFormat format;
	private EnrolledUser user;
	private String eventtype;
	private Instant timestart;
	private int timeduration;
	private boolean visible;
	private Instant timemodified;

	public CourseEvent(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DescriptionFormat getFormat() {
		return format;
	}

	public void setFormat(DescriptionFormat format) {
		this.format = format;
	}

	public EnrolledUser getUser() {
		return user;
	}

	public void setUser(EnrolledUser user) {
		this.user = user;
	}

	public String getEventtype() {
		return eventtype;
	}

	public void setEventtype(String eventtype) {
		this.eventtype = eventtype;
	}

	public Instant getTimestart() {
		return timestart;
	}

	public void setTimestart(Instant timestart) {
		this.timestart = timestart;
	}

	public int getTimeduration() {
		return timeduration;
	}

	public void setTimeduration(int timeduration) {
		this.timeduration = timeduration;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Instant getTimemodified() {
		return timemodified;
	}

	public void setTimemodified(Instant timemodified) {
		this.timemodified = timemodified;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CourseEvent))
			return false;
		CourseEvent other = (CourseEvent) obj;
		return id == other.id;
	}

}
