package model;

import java.io.Serializable;

/**
 * Secciones de contenido de un curso donde se guardan los modulos del curso. De
 * momento no se usa está clase.
 * 
 * @author Yi Peng Ji
 *
 */
public class Section implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * Section ID
	 */
	private int id;

	/**
	 * Section name
	 */
	private String name;

	/**
	 * Optional. is the section visible
	 */
	private boolean visible;

	/**
	 * Section description
	 */
	private String summary;

	/**
	 * summary format (1 = HTML, 0 = MOODLE, 2 = PLAIN or 4 = MARKDOWN)
	 */
	private DescriptionFormat summaryformat;

	/**
	 * Optional. Section number inside the course
	 */
	private int sectionNumber;

	/**
	 * Optional. Whether is a section hidden in the course format
	 */
	private int hiddenbynumsections;
	/**
	 * Optional. Is the section visible for the user?
	 */
	private boolean uservisible;

	/**
	 * Optional. Availability information.
	 */
	private String availabilityinfo;
	

	public Section(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public DescriptionFormat getSummaryformat() {
		return summaryformat;
	}

	public void setSummaryformat(DescriptionFormat summaryformat) {
		this.summaryformat = summaryformat;
	}

	public int getSectionNumber() {
		return sectionNumber;
	}

	public void setSectionNumber(int section) {
		this.sectionNumber = section;
	}

	public int getHiddenbynumsections() {
		return hiddenbynumsections;
	}

	public void setHiddenbynumsections(int hiddenbynumsections) {
		this.hiddenbynumsections = hiddenbynumsections;
	}

	public boolean isUservisible() {
		return uservisible;
	}

	public void setUservisible(boolean uservisible) {
		this.uservisible = uservisible;
	}

	public String getAvailabilityinfo() {
		return availabilityinfo;
	}

	public void setAvailabilityinfo(String availabilityinfo) {
		this.availabilityinfo = availabilityinfo;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Section))
			return false;
		Section other = (Section) obj;
		return id == other.id;
			
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	

}