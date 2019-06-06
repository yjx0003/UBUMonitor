package model;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Clase usuario de moodle. Los atributos optional pueden aparecer o no aparecer
 * en la respuesta de moodle.
 * 
 * @author Yi Peng Ji
 *
 */
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
	 * Optional. The first name(s) of the userjm
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
	private Set<Group> groups;

	/**
	 * Opcional. user roles
	 */
	private Set<Role> roles;

	/**
	 * Opcional. Courses where the user is enrolled - limited by which courses the
	 * user is able to see
	 */
	private Set<Course> enrolledcourses;

	private List<LogLine> logs;

	private List<LogLine> affectedLogs;

	private Map<GradeItem, Double> grades;

	private byte[] imageBytes;

	public EnrolledUser() {
		roles = new HashSet<>();
		logs = new ArrayList<>();
		affectedLogs = new ArrayList<>();
		enrolledcourses = new HashSet<>();
		grades = new HashMap<>();
		groups = new HashSet<>();

	}

	public EnrolledUser(int id) {
		this();
		this.id = id;
		lastname = "LastName_" + id;
		firstname = "FirstName_" + id;
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

	public List<LogLine> getLogs() {
		return logs;
	}

	public void setLogs(List<LogLine> logs) {
		this.logs = logs;
	}

	public List<LogLine> getAffectedLogs() {
		return affectedLogs;
	}

	public void setAffectedLogs(List<LogLine> affectedLogs) {
		this.affectedLogs = affectedLogs;
	}

	public Map<GradeItem, Double> getGrades() {
		return grades;
	}

	public void setGrades(Map<GradeItem, Double> grades) {
		this.grades = grades;
	}

	public Set<Group> getGroups() {
		return groups;
	}

	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Course> getEnrolledcourses() {
		return enrolledcourses;
	}

	public void setEnrolledcourses(Set<Course> courses) {
		this.enrolledcourses = courses;
	}

	public void addLog(LogLine log) {
		this.logs.add(log);
	}

	public void addAffectedLog(LogLine log) {
		this.affectedLogs.add(log);
	}

	public void addCourse(Course couse) {
		this.enrolledcourses.add(couse);
	}

	public void addGroup(Group group) {
		this.groups.add(group);
		group.addEnrolledUser(this);
	}

	public void addRole(Role role) {
		this.roles.add(role);
		role.addEnrolledUser(this);
	}

	public void addGrade(GradeItem gradeItem, double grade) {
		grades.put(gradeItem, grade);
		gradeItem.addUserGrade(this, grade);
	}

	public double getGrade(GradeItem gradeItem) {
		return grades.get(gradeItem);
	}

	public byte[] getImageBytes() {
		return imageBytes;
	}

	public void setImageBytes(byte[] imageBytes) {
		this.imageBytes = imageBytes;
	}

	@Override
	public int hashCode() {
		return id;
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

}
