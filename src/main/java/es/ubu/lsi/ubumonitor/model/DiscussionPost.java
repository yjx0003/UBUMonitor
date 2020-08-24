package es.ubu.lsi.ubumonitor.model;

import java.io.Serializable;
import java.time.Instant;

public class DiscussionPost implements Serializable{

	private static final long serialVersionUID = 1L;
	private int id;
	private ForumDiscussion discussion;
	private DiscussionPost parent;
	private EnrolledUser user;
	private Instant created;
	private Instant modified;
	private boolean mailed;
	private String subject;
	private String message;
	private DescriptionFormat messageformat;
	private boolean messagetrust;
	private String attachment;
	private int totalscore;
	private boolean mailnow;
	private boolean canreply;
	private boolean deleted;
	private boolean isprivatereply;
	
	public DiscussionPost(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ForumDiscussion getDiscussion() {
		return discussion;
	}

	public void setDiscussion(ForumDiscussion discussion) {
		this.discussion = discussion;
	}

	public DiscussionPost getParent() {
		return parent;
	}

	public void setParent(DiscussionPost parent) {
		this.parent = parent;
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

	public boolean isMailed() {
		return mailed;
	}

	public void setMailed(boolean mailed) {
		this.mailed = mailed;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public DescriptionFormat getMessageformat() {
		return messageformat;
	}

	public void setMessageformat(DescriptionFormat messageformat) {
		this.messageformat = messageformat;
	}

	public boolean isMessagetrust() {
		return messagetrust;
	}

	public void setMessagetrust(boolean messagetrust) {
		this.messagetrust = messagetrust;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public int getTotalscore() {
		return totalscore;
	}

	public void setTotalscore(int totalscore) {
		this.totalscore = totalscore;
	}

	public boolean isMailnow() {
		return mailnow;
	}

	public void setMailnow(boolean mailnow) {
		this.mailnow = mailnow;
	}

	public boolean isCanreply() {
		return canreply;
	}

	public void setCanreply(boolean canreply) {
		this.canreply = canreply;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isIsprivatereply() {
		return isprivatereply;
	}

	public void setIsprivatereply(boolean isprivatereply) {
		this.isprivatereply = isprivatereply;
	}
	
	public CourseModule getForum() {
		return getDiscussion().getForum();
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof DiscussionPost))
			return false;
		DiscussionPost other = (DiscussionPost) obj;
		return id == other.id;
	}
	
	@Override
	public String toString() {
		return subject;
	}
	
}
