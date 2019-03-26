package model;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnrolledUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * ID of the user
	 */
	private int id;
	/**
	 * Optional. Username policy is defined in Moodle security config
	 */
	private String userName;
	/**
	 * Optional. The first name(s) of the user
	 */
	private String firstname;
	/**
	 * Optional. The family name of the user
	 */
	private String lastname;

	/**
	 * . The fullname of the user
	 */
	private String fullName;
	/**
	 * Optional. An email address
	 */
	private String email;
	/**
	 * Optional. Postal address
	 */
	private String address;

	/**
	 * Optional. Phone 1
	 */
	private String phone1;
	/**
	 * Optional. Phone 2
	 */
	private String phone2;

	/**
	 * Optional. icq number
	 */
	private String icq;

	/**
	 * Optional. skype id
	 */
	private String skype;
	/**
	 * Optional. yahoo id
	 */
	private String yahoo;
	/**
	 * Optional. aim id
	 */
	private String aim;
	/**
	 * Optional. msn number
	 */
	private String msn;
	/**
	 * Optional. department
	 */
	private String department;

	/**
	 * Optional. institution
	 */
	private String institution;

	/**
	 * Optional. An arbitrary ID code number perhaps from the institution
	 */
	private String idnumber;

	/**
	 * Optional. user interests (separated by commas)
	 */
	private String interests;

	/**
	 * Optional. first access to the site (0 if never)
	 */
	private Instant firstaccess;
	/**
	 * Optional. last access to the site (0 if never)
	 */
	private Instant lastaccess;

	/**
	 * Optional. User profile description
	 */
	private String description;
	/**
	 * Optional. description format (1 = HTML, 0 = MOODLE, 2 = PLAIN or 4 =
	 * MARKDOWN)
	 */
	private DescriptionFormat descriptionformat;

	/**
	 * Optional. Home city of the user
	 */
	private String city;
	/**
	 * Optional. URL of the user
	 */
	private String url;
	/**
	 * Optional. Home country code of the user, such as AU or CZ
	 */
	private String country;
	/**
	 * Optional. User image profile URL - small version
	 */
	private String profileimageurlsmall;

	/**
	 * Optional. User image profile URL - big version
	 */
	private String profileimageurl;
	/**
	 * Optional. user groups
	 */
	private List<Group> groups;

	/**
	 * Opcional. user roles
	 */
	private List<Role> roles;

	/**
	 * Opcional. Courses where the user is enrolled - limited by which courses the
	 * user is able to see
	 */
	private List<Course> enrolledcourses;

	private List<LogLine> logs;

	private Map<GradeItem, Double> grades;

	private EnrolledUser(Builder builder) {
		id = builder.id;
		userName = builder.username;
		firstname = builder.firstname;
		lastname = builder.lastname;
		fullName = builder.fullname;
		email = builder.email;
		address = builder.address;
		phone1 = builder.phone1;
		phone2 = builder.phone2;
		icq = builder.icq;
		skype = builder.skype;
		yahoo = builder.yahoo;
		aim = builder.aim;
		msn = builder.msn;
		department = builder.department;
		institution = builder.institution;
		idnumber = builder.idnumber;
		interests = builder.interests;
		firstaccess = builder.firstaccess;
		lastaccess = builder.lastaccess;
		description = builder.description;
		descriptionformat = builder.descriptionformat;
		city = builder.city;
		url = builder.url;
		country = builder.country;
		profileimageurlsmall = builder.profileimageurlsmall;
		profileimageurl = builder.profileimageurl;

		groups = builder.groups;
		roles = builder.roles;
		enrolledcourses = builder.enrolledcourses;
		logs = builder.logs;
		grades=builder.grades;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String username) {
		this.userName = username;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullname) {
		this.fullName = fullname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone1() {
		return phone1;
	}

	public void setPhone1(String phone1) {
		this.phone1 = phone1;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getIcq() {
		return icq;
	}

	public void setIcq(String icq) {
		this.icq = icq;
	}

	public String getSkype() {
		return skype;
	}

	public void setSkype(String skype) {
		this.skype = skype;
	}

	public String getYahoo() {
		return yahoo;
	}

	public void setYahoo(String yahoo) {
		this.yahoo = yahoo;
	}

	public String getAim() {
		return aim;
	}

	public void setAim(String aim) {
		this.aim = aim;
	}

	public String getMsn() {
		return msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getIdnumber() {
		return idnumber;
	}

	public void setIdnumber(String idnumber) {
		this.idnumber = idnumber;
	}

	public String getInterests() {
		return interests;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	public Instant getFirstaccess() {
		return firstaccess;
	}

	public void setFirstaccess(Instant firstaccess) {
		this.firstaccess = firstaccess;
	}

	public Instant getLastaccess() {
		return lastaccess;
	}

	public void setLastaccess(Instant lastaccess) {
		this.lastaccess = lastaccess;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DescriptionFormat getDescriptionformat() {
		return descriptionformat;
	}

	public void setDescriptionformat(DescriptionFormat descriptionformat) {
		this.descriptionformat = descriptionformat;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProfileimageurlsmall() {
		return profileimageurlsmall;
	}

	public void setProfileimageurlsmall(String profileimageurlsmall) {
		this.profileimageurlsmall = profileimageurlsmall;
	}

	public String getProfileimageurl() {
		return profileimageurl;
	}

	public void setProfileimageurl(String profileimageurl) {
		this.profileimageurl = profileimageurl;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<Course> getEnrolledcourses() {
		return enrolledcourses;
	}

	public void setEnrolledcourses(List<Course> courses) {
		this.enrolledcourses = courses;
	}

	public void addLog(LogLine log) {
		this.logs.add(log);
	}

	@Override
	public int hashCode() {
		return id;
	}
	
	public double getGrade(GradeItem gradeItem) {
		return grades.get(gradeItem);
	}
	public void addGrade(GradeItem gradeItem,double grade) {
		grades.put(gradeItem , grade);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnrolledUser other = (EnrolledUser) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return lastname + ", " + firstname;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {

		private int id;
		private String username;
		private String firstname;
		private String lastname;
		private String fullname;
		private String email;
		private String address;
		private String phone1;
		private String phone2;
		private String icq;
		private String skype;
		private String yahoo;
		private String aim;
		private String msn;
		private String department;
		private String institution;
		private String idnumber;
		private String interests;
		private Instant firstaccess;
		private Instant lastaccess;
		private String description;
		private DescriptionFormat descriptionformat;
		private String city;
		private String url;
		private String country;
		private String profileimageurlsmall;
		private String profileimageurl;
		private List<Group> groups;
		private List<Role> roles;
		private List<Course> enrolledcourses;
		private List<LogLine> logs;
		private Map<GradeItem, Double> grades;

		public Builder() {
			groups = new ArrayList<Group>();
			roles = new ArrayList<Role>();
			enrolledcourses = new ArrayList<Course>();
			logs = new ArrayList<LogLine>();
			grades = new HashMap<>();
		}

		public Builder setId(int id) {
			this.id = id;
			return this;
		}

		public Builder setUsername(String username) {
			this.username = username;
			return this;
		}

		public Builder setFirstname(String firstname) {
			this.firstname = firstname;
			return this;
		}

		public Builder setLastname(String lastname) {
			this.lastname = lastname;
			return this;
		}

		public Builder setFullname(String fullname) {
			this.fullname = fullname;
			return this;
		}

		public Builder setEmail(String email) {
			this.email = email;
			return this;
		}

		public Builder setAddress(String address) {
			this.address = address;
			return this;
		}

		public Builder setPhone1(String phone1) {
			this.phone1 = phone1;
			return this;
		}

		public Builder setPhone2(String phone2) {
			this.phone2 = phone2;
			return this;
		}

		public Builder setIcq(String icq) {
			this.icq = icq;
			return this;
		}

		public Builder setSkype(String skype) {
			this.skype = skype;
			return this;
		}

		public Builder setYahoo(String yahoo) {
			this.yahoo = yahoo;
			return this;
		}

		public Builder setAim(String aim) {
			this.aim = aim;
			return this;
		}

		public Builder setMsn(String msn) {
			this.msn = msn;
			return this;
		}

		public Builder setDepartment(String department) {
			this.department = department;
			return this;
		}

		public Builder setInstitution(String institution) {
			this.institution = institution;
			return this;
		}

		public Builder setIdnumber(String idnumber) {
			this.idnumber = idnumber;
			return this;
		}

		public Builder setInterests(String interests) {
			this.interests = interests;
			return this;
		}

		public Builder setFirstaccess(Instant firstaccess) {
			this.firstaccess = firstaccess;
			return this;
		}

		public Builder setLastaccess(Instant lastaccess) {
			this.lastaccess = lastaccess;
			return this;
		}

		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}

		public Builder setDescriptionformat(DescriptionFormat descriptionformat) {
			this.descriptionformat = descriptionformat;
			return this;
		}

		public Builder setCity(String city) {
			this.city = city;
			return this;
		}

		public Builder setUrl(String url) {
			this.url = url;
			return this;
		}

		public Builder setCountry(String country) {
			this.country = country;
			return this;
		}

		public Builder setProfileimageurlsmall(String profileimageurlsmall) {
			this.profileimageurlsmall = profileimageurlsmall;
			return this;
		}

		public Builder setProfileimageurl(String profileimageurl) {
			this.profileimageurl = profileimageurl;
			return this;
		}

		public Builder setRoles(List<Role> roles) {
			this.roles = roles;
			return this;
		}

		public Builder setGroups(List<Group> groups) {
			this.groups = groups;
			return this;
		}

		public Builder setEnrolledcourses(List<Course> enrolledcourses) {
			this.enrolledcourses = enrolledcourses;
			return this;
		}

		public Builder setFirstaccess(int firstaccess) {
			this.firstaccess = Instant.ofEpochSecond(firstaccess);
			return this;
		}

		public Builder setLastaccess(int lastaccess) {
			this.lastaccess = Instant.ofEpochSecond(lastaccess);
			return this;
		}

		public Builder setDescriptionformat(int descriptionformat) {
			this.descriptionformat = DescriptionFormat.get(descriptionformat);
			return this;
		}

		public Builder setLogs(List<LogLine> logs) {
			this.logs = logs;
			return this;
		}

		public Builder setGrades(Map<GradeItem, Double> grades) {
			this.grades = grades;
			return this;
		}

		public EnrolledUser build() {
			return new EnrolledUser(this);
		}

	}

}
