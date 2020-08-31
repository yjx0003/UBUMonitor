package es.ubu.lsi.ubumonitor.model;

import java.io.Serializable;
import java.time.Instant;

public class ForumDiscussion implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private CourseModule forum;
	private String name;
	private Instant timemodified;
	private EnrolledUser usermodified;
	private Instant timestart;
	private Instant timeend;
	private EnrolledUser user;
	private Instant created;
	private Instant modified;
	private int numreplies;
	private boolean pinned;
	private boolean locked;
	private boolean starred;
	private boolean canreply;
	private boolean canlock;
	private boolean canfavourite;

	public ForumDiscussion(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Instant getTimemodified() {
		return timemodified;
	}

	public void setTimemodified(Instant timemodified) {
		this.timemodified = timemodified;
	}

	public EnrolledUser getUsermodified() {
		return usermodified;
	}

	public void setUsermodified(EnrolledUser usermodified) {
		this.usermodified = usermodified;
	}

	public Instant getTimestart() {
		return timestart;
	}

	public void setTimestart(Instant timestart) {
		this.timestart = timestart;
	}

	public Instant getTimeend() {
		return timeend;
	}

	public void setTimeend(Instant timeend) {
		this.timeend = timeend;
	}

	public EnrolledUser getUser() {
		return user;
	}

	public void setUser(EnrolledUser user) {
		this.user = user;
	}

	public Instant getCreated() {
		return created;
	}

	public void setCreated(Instant created) {
		this.created = created;
	}

	public Instant getModified() {
		return modified;
	}

	public void setModified(Instant modified) {
		this.modified = modified;
	}

	/**
	 * @return the forum
	 */
	public CourseModule getForum() {
		return forum;
	}

	/**
	 * @param forum the forum to set
	 */
	public void setForum(CourseModule forum) {
		this.forum = forum;
	}

	public int getNumreplies() {
		return numreplies;
	}

	public void setNumreplies(int numreplies) {
		this.numreplies = numreplies;
	}

	public boolean isPinned() {
		return pinned;
	}

	public void setPinned(boolean pinned) {
		this.pinned = pinned;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isStarred() {
		return starred;
	}

	public void setStarred(boolean starred) {
		this.starred = starred;
	}

	public boolean isCanreply() {
		return canreply;
	}

	public void setCanreply(boolean canreply) {
		this.canreply = canreply;
	}

	public boolean isCanlock() {
		return canlock;
	}

	public void setCanlock(boolean canlock) {
		this.canlock = canlock;
	}

	public boolean isCanfavourite() {
		return canfavourite;
	}

	public void setCanfavourite(boolean canfavourite) {
		this.canfavourite = canfavourite;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ForumDiscussion))
			return false;
		ForumDiscussion other = (ForumDiscussion) obj;
		return id == other.id;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
